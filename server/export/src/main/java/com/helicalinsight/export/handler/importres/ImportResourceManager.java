package com.helicalinsight.export.handler.importres;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.JacksonUtility;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import com.helicalinsight.export.crypto.FileCryptoHandler;
import com.helicalinsight.export.dto.ImportRequest;
import com.helicalinsight.export.dto.ImportResponse;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.exception.ResourceImportException;
import com.helicalinsight.export.exception.ZipResourceException;
import com.helicalinsight.export.handler.ImportManagerContext;
import com.helicalinsight.export.utils.ManifestUtils;
import com.helicalinsight.export.utils.ResourceDependencySorter;
import com.helicalinsight.export.utils.ResourceFileUtils;
import com.helicalinsight.export.utils.ZipUtils;
import com.helicalinsight.resourcedb.Deleted;
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
	public String importFile(MultipartFile file, ImportRequest request,ImportResponse response) throws Exception {
		String extractedFolder = "";
		String tempAbsolutePath = "";
		String timeStamp = "";
		boolean fileExists = StringUtils.isNotBlank(request.getKey());
		
		if(!fileExists) {
			timeStamp =  String.valueOf(System.currentTimeMillis());
			File temp = new File(String.join(File.separator, TEMPDIR, timeStamp));
			temp.mkdir();
			tempAbsolutePath = temp.getAbsolutePath().strip();
			Path path = Paths.get(String.join(File.separator,tempAbsolutePath, file.getOriginalFilename()));
			Files.copy(file.getInputStream(), path);
			zipUtils.extract(tempAbsolutePath, file.getOriginalFilename());
		}
		else {
			File temp = new File(String.join(File.separator, TEMPDIR, request.getKey()));
			tempAbsolutePath = temp.getAbsolutePath().strip();
			timeStamp = request.getKey();
		}
		
		File timeStampFolder = new File(tempAbsolutePath);
		File[] files = timeStampFolder.listFiles();
		
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
			cryptoHandler.decryptBatch(String.join(File.separator,tempAbsolutePath,extractedFolder));
		}
		
		Manifest manifest = manifestUtils.readManifest(String.join(File.separator,tempAbsolutePath,extractedFolder));
		List<String> resourcePaths = manifest.getResourcePaths();
		
		ImportManagerContext context = new ImportManagerContext();
		context.setRequest(request);
		context.setResponse(response);
		context.setDate(new Date());
		context.setManifest(manifest);
		context.setCurrentDirectory(String.join(File.separator,tempAbsolutePath,extractedFolder));
		
		if (!resourcePaths.isEmpty() && request.getUpload()) {
			ResourceDependencySorter sorter = new ResourceDependencySorter(context);
			List<String> sortedPaths = sorter.sort();
			doImport(sortedPaths, context);
			fileUtils.cleanDir(timeStamp);
			return "Imported Successfully";
		} else {
			ObjectNode data = JacksonUtility.emptyNode();
			data.putPOJO("options", manifest.getOptions());
			data.put("key", timeStamp);
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
		
		for(String path : paths) {
			String extension = FileNameUtils.getExtension(path);
			AbstractResourceImportHandler handler = ImportHandlerFactory.getHandler(extension);
			if ( handler == null ) {
				continue;
			}
			path = StringUtils.isNotBlank(destination) ? String.join("/", destination,path) : path;
			HIResource resource =  handler.setContext(context).importResource(path);
			context.getResourceUrlMap().put(path, resource);
		}
	}
}
