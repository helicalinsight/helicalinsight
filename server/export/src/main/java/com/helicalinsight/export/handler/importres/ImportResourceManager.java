package com.helicalinsight.export.handler.importres;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helicalinsight.admin.dto.Notification;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.JacksonUtility;
import com.helicalinsight.core.request.RequestContext;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.filters.RequestRegistryFilter;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import com.helicalinsight.export.crypto.FileCryptoHandler;
import com.helicalinsight.export.dto.ImportRequest;
import com.helicalinsight.export.dto.ImportResponse;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.exception.ResourceImportException;
import com.helicalinsight.export.exception.ZipResourceException;
import com.helicalinsight.export.handler.ImportManagerContext;
import com.helicalinsight.export.service.RecycleBinHandler;
import com.helicalinsight.export.utils.ManifestUtils;
import com.helicalinsight.export.utils.ResourceDependencySorter;
import com.helicalinsight.export.utils.ResourceFileUtils;
import com.helicalinsight.export.utils.ZipUtils;
import com.helicalinsight.resourcedb.Deleted;
import com.helicalinsight.stream.ProgressEventPublisher;
import com.helicalinsight.stream.ProgressEventSink;

import jakarta.transaction.Transactional;
/**
 * Manages the import of resources, including handling file extraction, decryption,
 * and resource import operations based on a provided manifest.
 */
@Component
@Scope("prototype")
public class ImportResourceManager {

	@Autowired
	private ZipUtils zipUtils;

	@Autowired
	private FileCryptoHandler cryptoHandler;

	@Autowired
	private ResourceFileUtils fileUtils;

	@Autowired
	private ManifestUtils manifestUtils;
	
	@Autowired
	private HIResourceServiceDB serviceDb;

	@Autowired
	private RecycleBinHandler recycleBinHandler;
	
	private static final Logger logger = LoggerFactory.getLogger(ImportResourceManager.class);
	private static final String TEMPDIR = TempDirectoryCleaner.getTempDirectory().getAbsolutePath();
	/**
     * Imports a file based on the provided MultipartFile, ImportRequest, and ImportResponse.
     * This method facilitates the process of importing a resource, typically in the form of a file,
     * into the system.
     * @param file     the MultipartFile representing the file to be imported
     * @param request  the ImportRequest containing import parameters
     * @param response the ImportResponse for reporting import status
     * @return a message indicating the result of the import operation
     * @throws Exception if an error occurs during the import process
     */
	@Transactional
	public String importFile(MultipartFile file, ImportRequest request,ImportResponse response) throws Exception {
		return importFile(file, request, response, null);
	}

	@Transactional
	public String importFile(MultipartFile file, ImportRequest request, ImportResponse response, ProgressEventSink sink)
			throws Exception {
		String extractedFolder = "";
		String tempAbsolutePath = "";
		String timeStamp = "";
		boolean fileExists = StringUtils.isNotBlank(request.getKey());
		
		if (response.getNotification() == null) {
			response.setNotification(new Notification());
		}
		ProgressEventPublisher publisher = new ProgressEventPublisher(response.getNotification(), sink);

		if(!fileExists) {
			timeStamp =  String.valueOf(System.currentTimeMillis());
			File temp = new File(String.join(File.separator, TEMPDIR, timeStamp));
			temp.mkdir();
			tempAbsolutePath = temp.getAbsolutePath().strip();
			Path path = Paths.get(String.join(File.separator,tempAbsolutePath, file.getOriginalFilename()));
			publisher.log("package", "Saving uploaded file…");
			Files.copy(file.getInputStream(), path);
			publisher.log("package", "Extracting zip…");
			int extractBatch = Math.max(1, JsonUtils.getResponseEventBatchSize());
			AtomicInteger lastLogged = new AtomicInteger(0);
			zipUtils.extract(tempAbsolutePath, file.getOriginalFilename(), (done, total) -> {
				if (total <= 0) {
					return;
				}
				int prev = lastLogged.get();
				if (prev == 0 || done >= total || done - prev >= extractBatch) {
					lastLogged.set(done);
					publisher.log("package", "Extracting zip… (" + done + "/" + total + ")");
				}
			});
			publisher.log("package", "Zip extracted.");
		}
		else {
			publisher.log("package", "Using prepared package key=" + request.getKey());
			File temp = new File(String.join(File.separator, TEMPDIR, request.getKey()));
			tempAbsolutePath = temp.getAbsolutePath().strip();
			timeStamp = request.getKey();
		}
		
		File timeStampFolder = new File(tempAbsolutePath);
		File[] files = timeStampFolder.listFiles();
		
		if (files == null) {
		    throw new EfwServiceException("The uploaded file is no longer available. Please upload the file again.");
		}
		
		for (File extracted : files) {
			if (!extracted.getName().contains(".zip")) {
				extractedFolder = extracted.getName();
				break;
			}
		}
		
		if (StringUtils.isBlank(extractedFolder)) {
			throw new ZipResourceException("Invalid zip file provided.");
		}
		if(!fileExists) {
			publisher.log("package", "Decrypting package contents…");
			cryptoHandler.decryptBatch(String.join(File.separator,tempAbsolutePath,extractedFolder));
			publisher.log("package", "Package decrypted.");
		}
		
		publisher.log("package", "Reading manifest…");
		Manifest manifest = manifestUtils.readManifest(String.join(File.separator,tempAbsolutePath,extractedFolder));
		List<String> resourcePaths = manifest.getResourcePaths();
		publisher.log("package", "Manifest loaded with " + resourcePaths.size() + " resource(s).");
		
		ImportManagerContext context = new ImportManagerContext();
		context.setRequest(request);
		context.setResponse(response);
		context.setDate(new Date());
		context.setManifest(manifest);
		context.setCurrentDirectory(String.join(File.separator,tempAbsolutePath,extractedFolder));
		context.setProgressPublisher(publisher);
		
		if (!resourcePaths.isEmpty() && request.getUpload()) {
			if (StringUtils.isNotBlank(request.getDestination())) {
				publisher.log("package", "Destination: " + request.getDestination());
			}
			if (request.getOptions() != null) {
				publisher.log("package", "Dependencies: datasource=" + Boolean.TRUE.equals(request.getOptions().getDataSource())
						+ ", share=" + Boolean.TRUE.equals(request.getOptions().getShare())
						+ ", schedules=" + Boolean.TRUE.equals(request.getOptions().getSchedules())
						+ ", recycleBin=" + Boolean.TRUE.equals(request.getOptions().getRecycleBin()));
			}
			publisher.log("package", "Sorting resource dependencies…");
			ResourceDependencySorter sorter = new ResourceDependencySorter(context);
			List<String> sortedPaths = sorter.sort();
			publisher.log("package", "Import order ready (" + sortedPaths.size() + " item(s)). Starting import…");
			try {
				doImport(sortedPaths, context);
			}
			catch (Exception e) {
				boolean aborted = e.getMessage() != null && e.getMessage().toLowerCase().contains("aborted");
				if (aborted) {
					logger.info("Import aborted for key={}; rolling back and cleaning temp files", timeStamp);
					context.addLog("package", "Import aborted.");
					context.addLog("package", "Rolling back changes…");
				} else {
					logger.error("Error occurred ! Rolling back the changes ", e);
					context.addLog("package", "Import failed; rolling back changes.");
				}
				throw new ResourceImportException(e.getMessage(), e);
			}
			finally {
				context.addLog("package", "Cleaning temp files…");
				logger.info("Cleaning import temp files for key={}", timeStamp);
				fileUtils.cleanDir(timeStamp);
				context.addLog("package", "Temp files cleaned.");
			}
			publisher.log("package", "Import finished. Inserts=" + response.getInsertCount()
					+ ", Updates=" + response.getUpdateCount()
					+ ", Skips=" + response.getSkipCount() + ".");
			return "Imported Successfully";
		} else {
			ObjectNode data = JacksonUtility.emptyNode();
			data.putPOJO("options", manifest.getOptions());
			data.put("key", timeStamp);
			data.putPOJO("notification", response.getNotification());
			return data.toString();
		}
	}
	/**
     * Performs the actual resource import based on the sorted paths and the provided context.
     *
     * @param paths       sorted paths of resources to be imported
     * @param context     ImportManagerContext containing import context information
     */
	private void doImport(List<String> paths,ImportManagerContext context) {
		
		String destination = context.getRequest().getDestination();
		if(StringUtils.isNotBlank(destination)) {
			HIResource destinationResource =  serviceDb.getResourceByUrl(destination,Deleted.FALSE);
			if(destinationResource == null ) {
				throw new ResourceImportException("Destination folder not found.");
			}
			context.getResourceUrlMap().put(destination+".efwfolder", destinationResource);
		}
		
		int total = paths.size();
		int index = 0;
		for(String path : paths) {
			String requestId = RequestContext.get();
			if (Thread.currentThread().isInterrupted()
					|| (requestId != null && RequestRegistryFilter.cancelledRequests.contains(requestId))) {
				int next = index + 1;
				String abortMsg = "Import aborted before item " + next + "/" + total
						+ ". Stopping further imports…";
				logger.info("{} (requestId={})", abortMsg, requestId);
				context.addLog("package", abortMsg);
				throw new ResourceImportException("Import aborted");
			}
			index++;
			String extension = FileNameUtils.getExtension(path);
			AbstractResourceImportHandler handler = ImportHandlerFactory.getHandler(extension);
			if ( handler == null ) {
				String skippedPath = StringUtils.isNotBlank(destination) ? String.join("/", destination,path) : path;
				context.addInfo(skippedPath, "Unknown resource type; skipped.");
				continue;
			}
			path = StringUtils.isNotBlank(destination) ? String.join("/", destination,path) : path;
			context.setCurrentResourceUrl(path);
			int insertsBefore = context.getResponse().getInsertCount();
			int updatesBefore = context.getResponse().getUpdateCount();
			int skipsBefore = context.getResponse().getSkipCount();
			context.addLog(path, "(" + index + "/" + total + ") Importing…");
			try {
				long start = System.currentTimeMillis();
				HIResource resource =  handler.setContext(context).importResource(path);
				recycleBinHandler.importResource(resource, context);
				Double duration = (System.currentTimeMillis()-start)/1000.0;
				logger.debug("Successfully Imported {} in {} s " , path, duration);
				String outcome = "processed";
				if (context.getResponse().getInsertCount() > insertsBefore) {
					outcome = "inserted";
				} else if (context.getResponse().getUpdateCount() > updatesBefore) {
					outcome = "updated";
				} else if (context.getResponse().getSkipCount() > skipsBefore) {
					outcome = "skipped";
				}
				context.addLog(path, "(" + index + "/" + total + ") " + outcome + " in " + duration + " s");
				context.getResourceUrlMap().put(path, resource);
			}
			catch (Exception e) {
				String errorMessage = """
						ImportError: We couldn’t import the file "%s", because an issue occurred during the import process. The operation has been rolled back, and no changes were made. Please correct the issue in the file and try importing it again.
						""".formatted(path);
				logger.error(errorMessage , e);
				context.addError(path, errorMessage.trim());
				throw new ResourceImportException(errorMessage, e);
			}
		}
	}
}
