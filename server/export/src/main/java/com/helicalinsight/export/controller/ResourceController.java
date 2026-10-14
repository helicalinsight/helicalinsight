package com.helicalinsight.export.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.MediaType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.admin.utils.JacksonUtility;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.export.ticket.TicketProgressEventSink;
import com.helicalinsight.export.ticket.TransferTicket;
import com.helicalinsight.export.ticket.TransferTicketService;
import com.helicalinsight.stream.ProgressEventSink;
import com.helicalinsight.export.download.ResumableDownloadService;
import com.helicalinsight.export.download.producer.DownloadArtifactProducer;
import com.helicalinsight.export.download.producer.ResourceExportDownloadProducer;
import com.helicalinsight.export.dto.ImportRequest;
import com.helicalinsight.export.dto.ImportResponse;
import com.helicalinsight.export.dto.ResourceExportRequest;
import com.helicalinsight.export.dto.validation.ExportRequestValidator;
import com.helicalinsight.export.dto.validation.ImportRequestValidator;
import com.helicalinsight.export.exception.ResourceImportException;
import com.helicalinsight.export.handler.ResourceExportHandler;
import com.helicalinsight.export.handler.importres.ImportResourceManager;
import com.helicalinsight.export.upload.ResumableUploadService;
import com.helicalinsight.export.utils.JsonMapperUtils;
import com.helicalinsight.parallelprocessor.TaskExecutorService;
/**
 * Controller class for handling resource export and import operations.
 * 
 * @Controller Indicates that this class serves as a Spring MVC controller.
 * @RequestMapping Specifies the base URI pattern for mapping requests.
 */
@Controller
public class ResourceController {

	@Autowired
	private ResourceExportHandler exportHandler;
	@Autowired
	private ImportResourceManager importManager;
	@Autowired
	private JsonMapperUtils jsonMapperUtils;
	@Autowired
	private ResumableDownloadService resumableDownloadService;
	@Autowired
	private ResumableUploadService resumableUploadService;
	@Autowired
	private TransferTicketService transferTicketService;
	@Autowired
	private TaskExecutorService taskExecutorService;

	/**
     * Handles POST requests for exporting resources.
     *
     * @param formData 			JSON-formatted data containing resource export request details.
     * @param request  			HttpServletRequest instance.
     * @param response 			HttpServletResponse instance.
     * @return Byte array representing the exported resource.
     * @throws Exception If an error occurs during the export process.
     */
	@RequestMapping(value = "/exportResource", method = RequestMethod.POST)
	@ResponseBody
	public Object exportResource(@RequestParam("formData") String formData, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ResourceExportRequest requestDTO = jsonMapperUtils.mapToDTO(formData, ResourceExportRequest.class);
		ExportRequestValidator validator = ApplicationContextAccessor.getBean(ExportRequestValidator.class);
		validator.validate(requestDTO);

		if ("true".equalsIgnoreCase(request.getParameter("resumable"))) {
			String requestId = request.getParameter("requestId");
			if (requestId == null || requestId.isEmpty()) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "requestId is required");
				return null;
			}
			String userId = AuthenticationUtils.getUserId();
			String resourcePath = requestDTO.getDir() != null ? requestDTO.getDir() : "";
			if (requestDTO.getFile() != null && !requestDTO.getFile().isEmpty()) {
				resourcePath = resourcePath + "/" + requestDTO.getFile();
			}
			String ticketId = TransferTicketService.computeTicketId(
					userId, "exportResource", resourcePath, "zip", "", "", "", "", requestId);
			DownloadArtifactProducer producer = new ResourceExportDownloadProducer(exportHandler, requestDTO, ticketId);
			resumableDownloadService.writePreparing(response, resumableDownloadService.prepare(ticketId, requestId, userId, producer));
			return null;
		}

		response.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		response.setStatus(HttpServletResponse.SC_OK);
		return exportHandler.export(requestDTO,response);

	}
	/**
     * This method handles POST requests for importing resources.
     *
     * @param multipartFile 	MultipartFile representing the uploaded file.
     * @param formData      	JSON-form data containing import request details.
     * @param response      	HttpServletResponse instance.
     * @return JSON-formatted response containing import status and details.
     * @throws Exception If an error occurs during the import process.
     */
	@RequestMapping(value = "/importResource", method = RequestMethod.POST)
	@ResponseBody
	public String importResource(@RequestParam(value = "file" , required = false) MultipartFile multipartFile, @RequestParam(value = "formData", required = false)String formData,
			HttpServletRequest httpRequest, HttpServletResponse response) throws Exception {
		if ("true".equalsIgnoreCase(httpRequest.getParameter("resumable"))) {
			return resumableUploadService.prepareFromHttpRequest(httpRequest, "/importResource");
		}
		ImportRequest request = jsonMapperUtils.mapToDTO(formData, ImportRequest.class);
		ImportRequestValidator validator = ApplicationContextAccessor.getBean(ImportRequestValidator.class);
		validator.validate(multipartFile, request);
		ImportResponse resp = new ImportResponse();
		ObjectNode responseNode = JsonNodeFactory.instance.objectNode();
		try {
			if (Boolean.TRUE.equals(request.getUpload()) 
					&& JsonUtils.isStreamResponseEnabled()
					&& "true".equalsIgnoreCase(httpRequest.getParameter("stream"))) {
				return startStreamingImport(multipartFile, request, httpRequest);
			}
			String result = importManager.importFile(multipartFile, request,resp);
			if(!request.getUpload()) {
				return result.toString();
			}
			responseNode.put("status",1);
			resp.setMessage(result);
			responseNode.set("response", JacksonUtility.mapToJson(resp));
			return responseNode.toString();
		} catch (ResourceImportException e) {
			throw new ResourceImportException(e.getLocalizedMessage());
		}
	}

	private String startStreamingImport(MultipartFile multipartFile, ImportRequest request, HttpServletRequest httpRequest)
			throws Exception {
		String userId = AuthenticationUtils.getUserId();
		// Always unique per Import click so a second run never reuses a spent ticket/latch/emitter.
		String requestId = java.util.UUID.randomUUID().toString();
		String key = StringUtils.isNotBlank(request.getKey()) ? request.getKey() : "import";
		String ticketId = TransferTicketService.computeUploadTicketId(userId, "import-stream", 0L, key, requestId);
		TransferTicket ticket = transferTicketService.beginImportStream(ticketId, requestId, userId, 60);
		final String rid = requestId;
		final MultipartFile file = multipartFile;
		taskExecutorService.submit(() -> {
			ImportResponse importResponse = new ImportResponse();
			ProgressEventSink sink = TicketProgressEventSink.batched(transferTicketService, ticketId, rid);
			try {
				if (!transferTicketService.awaitSubscriberSeconds(ticketId, 60)) {
					sink.abort("Timed out waiting for event subscriber");
					return;
				}
				String result = importManager.importFile(file, request, importResponse, sink);
				importResponse.setMessage(result);
				ObjectNode done = JsonNodeFactory.instance.objectNode();
				done.put("status", 1);
				done.set("response", JacksonUtility.mapToJson(importResponse));
				sink.complete(done.toString());
			} catch (Exception e) {
				sink.abort(e.getMessage());
			}
		}, requestId);
		ObjectNode preparing = JsonNodeFactory.instance.objectNode();
		preparing.put("status", 1);
		ObjectNode body = preparing.putObject("response");
		body.put("ticket", ticket.ticketId);
		body.put("requestId", requestId);
		body.put("status", "PREPARING");
		body.put("eventsUrl", TransferTicketService.eventsUrl(ticket.ticketId,requestId));
		return preparing.toString();
	}

}
