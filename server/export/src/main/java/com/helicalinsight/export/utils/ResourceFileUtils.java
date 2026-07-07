package com.helicalinsight.export.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import com.helicalinsight.export.dto.ImportRequest;
import com.helicalinsight.export.exception.ResourceExportException;
import com.helicalinsight.export.exception.ResourceImportException;
import com.helicalinsight.export.exception.ZipResourceException;
import com.helicalinsight.export.service.ShareHandler;
import com.helicalinsight.resourcedb.HIResourceDTO;
import com.helicalinsight.resourcesecurity.SecurityUtils;

import net.lingala.zip4j.exception.ZipException;
/**
 * Utility class for managing resource files, including archiving, extraction, directory creation, and file reading.
 */
@Component
public class ResourceFileUtils {

	private static final String TEMPDIR = TempDirectoryCleaner.getTempDirectory().getAbsolutePath();
	private static final Logger LOG = LoggerFactory.getLogger(ResourceFileUtils.class);

	@Autowired
	private ZipUtils zipUtils;
	
	@Autowired
	private JsonMapperUtils jsonMapperUtils;
	
	@Autowired
	private ShareHandler shareHandler;
	
	private ImportRequest importRequest;
	 /**
     * Archives the specified directory into a zip file.
     * @param rootDir      Root directory to be archived.
     * @throws ZipResourceException If an error occurs during archiving.
     */
	public void archive(String rootDir) {
		try {
			zipUtils.archive(TEMPDIR, rootDir);
		} catch (ZipException e) {
			LOG.error("Error occurred :: {}", e.getMessage());
			throw new ZipResourceException("Error occurred while archiving the directory.");
		}
	}
	/**
     * Extracts a zip file into the temporary directory.
     *
     * @param zipName 		Name of the zip file to be extracted.
     * @throws ZipResourceException If an error occurs during extraction.
     */
	public void extract(String zipName) {
		try {
			zipUtils.extract(TEMPDIR, zipName);
		} catch (ZipException e) {
			LOG.error("Error occurred during extraction of zip file : {}", e.getMessage());
			throw new ZipResourceException("Error occurred while extracting the file");
		}
	}
	/**
     * Creates a directory structure for the specified root directory.
     *
     * @param rootDir 			Root directory .
     * @return {@code true} if the directory structure is successfully created, {@code false} otherwise.
     * @throws ResourceExportException If an error occurs during directory creation.
     */
	public Boolean createSchema(String rootDir) {
		rootDir = String.join(File.separator, TEMPDIR,rootDir);
		try {
			
			File root = new File(rootDir);
			root.mkdirs();


			File cacheDir = new File(String.join(File.separator, rootDir, "cache"));
			cacheDir.mkdir();

			File dwDir = new File(String.join(File.separator, rootDir, "dw"));
			dwDir.mkdir();

			File resources = new File(String.join(File.separator, rootDir, "resources"));
			resources.mkdir();

			File manifestFile = new File(String.join(File.separator, rootDir, "Manifest.json"));
			return manifestFile.createNewFile();

		} catch (Exception e) {
			LOG.error("Exception :: {}", e.getMessage());
			throw new ResourceExportException("Error occurred while creating folder schema");
		}
	}
	/**
	 * Reads the contents of a file and returns them as a byte array.
	 *
	 * @param file 		 file to read.
	 * @return A byte array containing the file content, or {@code null} if an error occurs.
	 */
	public byte[] getAllBytes(File file) {
		try (FileInputStream fis = new FileInputStream(file)) {
			byte[] bytes = new byte[(int) file.length()];
			fis.read(bytes);
			return bytes;
		} catch (IOException e) {
			LOG.error("Error occurred  while reading file data : {}",e.getLocalizedMessage());
			return null;
		}
	}
	/**
	 * Deletes the directory a directory by recursively deleting its contents.
	 *
	 * @param dirName 		name of the directory to clean.
	 * @throws ResourceExportException If an error occurs while deleting the temporary folder.
	 */
	public void cleanDir(String dirName) {
		try {
			File folder = new File(String.join(File.separator, TEMPDIR, dirName));
			org.apache.commons.io.FileUtils.deleteDirectory(folder);
		} catch (Exception e) {
			LOG.error("Error :: {}",e.getLocalizedMessage());
			throw new ResourceExportException("Error occurred while deleting the temporary folder.");
		}
	}	
	/**
     * Reads the content of a file and maps it to the specified class.
     *
     * @param filePath 		path of the file to be read.
     * @param clazz    		class for file content to mapping.
     * @param <T>      		type of the class.
     * @return An instance of the mapped class.
     * @throws ResourceImportException If an error occurs during file reading or mapping.
     */
	public <T> T  readFile(String filePath , Class<T> clazz) {
		ObjectNode node = readFile(new File(filePath), ObjectNode.class);
		
		if(importRequest != null) {
			if(Boolean.TRUE.equals(importRequest.getOptions().getShare() &&
					node.has("createdBy")) && node.get("createdBy").isObject()) {
				ObjectNode createdBy = (ObjectNode) node.get("createdBy");
				Integer newUserId = resolveOwner(createdBy);
				node.put("createdBy", newUserId);
			}
			else if (node.has("createdBy") && node.get("createdBy").isObject()) {
				node.put("createdBy", SecurityUtils.securityObject().getCreatedBy());
			}
		}
		return jsonMapperUtils.mapToDTO(node.toString(), clazz);
	}
	/**
     * Reads the content of a file and maps it to the specified class.
     *
     * @param file  		file to be read.
     * @param clazz 		class to which the file content will be mapped.
     * @param <T>  			type of the class.
     * @return An instance of the mapped class.
     * @throws ResourceImportException If an error occurs during file reading or mapping.
     */
	public <T> T readFile(File file, Class<T> clazz) {

		try (FileInputStream inputStream = new FileInputStream(file)) {
			return jsonMapperUtils.mapToDTO(inputStream, clazz);
		} catch                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  (IOException e) {
			throw new ResourceImportException("Error occurred while reading "+file.getAbsolutePath());
		}
	}
	/**
     * Resolves the owner from the provided owner information.
     *
     * @param owner 		 owner information.
     * @return The ID of the resolved owner.
     */
	private final  Integer resolveOwner(ObjectNode owner) {
		return  shareHandler.saveOwner(owner);
	}
	/**
     * Sets the import request for further processing.
     *
     * @param importRequest 	 import request to be set.
     */
	public final void setRequest(ImportRequest importRequest) {
		this.importRequest = importRequest;
	}
}
