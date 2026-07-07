package com.helicalinsight.export.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.export.dto.ImportRequest;
import com.helicalinsight.export.dto.ImportResponse;
import com.helicalinsight.export.dto.ResourceExportRequest;
import com.helicalinsight.export.dto.validation.ExportRequestValidator;
import com.helicalinsight.export.dto.validation.ImportRequestValidator;
import com.helicalinsight.export.exception.ResourceImportException;
import com.helicalinsight.export.handler.ResourceExportHandler;
import com.helicalinsight.export.handler.importres.ImportResourceManager;
import com.helicalinsight.export.utils.JsonMapperUtils;
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
	public byte[] exportResource(@RequestParam("formData") String formData, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		response.setStatus(HttpServletResponse.SC_OK);
		ResourceExportRequest requestDTO = jsonMapperUtils.mapToDTO(formData, ResourceExportRequest.class);
		ExportRequestValidator validator = ApplicationContextAccessor.getBean(ExportRequestValidator.class);
		validator.validate(requestDTO);
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
	public String importResource(@RequestParam(value = "file" , required = false) MultipartFile multipartFile, @RequestParam("formData")String formData,
			HttpServletResponse response) throws Exception {
		ImportRequest request = jsonMapperUtils.mapToDTO(formData, ImportRequest.class);
		ImportRequestValidator validator = ApplicationContextAccessor.getBean(ImportRequestValidator.class);
		validator.validate(multipartFile, request);
		ImportResponse resp = new ImportResponse();
		ObjectNode responseNode = JsonNodeFactory.instance.objectNode();
		try {
			String result = importManager.importFile(multipartFile, request,resp);
			if(!request.getUpload()) {
				return result.toString();
			}
			responseNode.put("status",1);
			resp.setMessage(result);
			responseNode.putPOJO("response", resp);
			return responseNode.toString();
		} catch (ResourceImportException e) {
			throw new ResourceImportException(e.getLocalizedMessage());
		}
	}
}
