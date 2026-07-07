package com.helicalinsight.adhoc.metadata.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.exceptions.ResourceNotFoundException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.model.CutCopyFileInfo;
import com.helicalinsight.efw.model.FileInfo;
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;

/**
 * The {@code ResourceInfoUtility} class is responsible for managing the
 * information of file and folder resources within the application. It provides
 * utility methods to prepare file information for various operations such as
 * displaying, copying, and pasting resources.
 */
@Component
public class ResourceInfoUtility {

	@Autowired
	HIResourceServiceDB serviceDB;

	private static final Logger logger = LoggerFactory.getLogger(ResourceInfoUtility.class);
	private static final String FOLDER = "folder";
	private static final String FILE = "file";

	/**
	 * Prepares the {@code FileInfo} object for a given resource located at the
	 * specified location with the provided file name. It sets the necessary details
	 * such as name, title, type, extension, path, permission level, and last
	 * modified time.
	 *
	 * @param location location of the resource.
	 * @param fileName name of the file.
	 * @return FileInfo prepared file information.
	 * @throws ResourceNotFoundException if the resource does not exist.
	 */
	public FileInfo prepareFileInfo(String location, String fileName) {

		HIResource resource = serviceDB.getResourceByUrl(location + "/" + fileName);
		HIResource tempResource = null;
		if (resource == null) {
			throw new ResourceNotFoundException("The Resource does not exists");
		}

		FileInfo fileInfo = new FileInfo();
		fileInfo.setResourceId(resource.getResourceId());
		String[] pathSplit = resource.getResourceURL().split(Pattern.quote("/"));
		fileInfo.setName(pathSplit[pathSplit.length - 1]);
		fileInfo.setTitle(resource.getTitle());
		if (resource.getResourceType().getName().equals(FOLDER)) {
			fileInfo.setTitle(null);
			fileInfo.setType(FOLDER);
			JsonObject json = new JsonObject();
			json.addProperty("selectable", "true");
			fileInfo.setName(resource.getTitle());
			fileInfo.setOptions(json);
		} else {
			fileInfo.setType(FILE);
			fileInfo.setExtension(resource.getResourceType().getExtension().replace(".", ""));
			fileInfo.setOptions(new JsonObject());
		}
		fileInfo.setPath(resource.getResourceURL());
		Integer createdBy = resource.getCreatedBy();
		logger.info("Created By  : {}", createdBy);
		ResourcePermissionLevelsHolder resourcePermissionLevelsHolder = ApplicationContextAccessor
				.getBean(ResourcePermissionLevelsHolder.class);
		Integer permissionLevel = null;
		if (createdBy == null) {
			permissionLevel = resourcePermissionLevelsHolder.publicResourceAccessLevel();
		} else if (createdBy.equals(Integer.valueOf(AuthenticationUtils.getUserId()))) {
			permissionLevel = resourcePermissionLevelsHolder.ownerAccessLevel();
		} else {
			tempResource = resource;
			Map<Integer, Integer> securityMap = serviceDB.getSecurityMap();
			while (tempResource != null) {
				Integer tempCreatedBy = tempResource.getCreatedBy();
				if (tempCreatedBy == null) {
					permissionLevel = resourcePermissionLevelsHolder.publicResourceAccessLevel();
				} else if (tempCreatedBy.equals(Integer.valueOf(AuthenticationUtils.getUserId()))) {
					permissionLevel = resourcePermissionLevelsHolder.ownerAccessLevel();
				} else if (securityMap.get(tempResource.getResourceId()) != null) {
					permissionLevel = securityMap.get(tempResource.getResourceId());
				}
				try {
					tempResource = serviceDB.getHIResourceById(tempResource.getParentId());
				} catch (Exception e) {
					logger.info(e.getMessage());
					tempResource = null;
				}

			}
		}

		fileInfo.setPermissionLevel(String.valueOf(permissionLevel));
		fileInfo.setLastModified(tempResource != null ? tempResource.getLastUpdatedTime().getTime()
				: resource.getLastUpdatedTime().getTime());
		logger.info("FileInfo : {} ", fileInfo);
		return fileInfo;

	}

	/**
	 * Prepares the {@code CutCopyFileInfo} object for copy-paste operations. It is
	 * similar to {@code prepareFileInfo} but also includes the children of the
	 * folder resources.
	 *
	 * @param location 	 		 location of the resource.
	 * @param fileName 			 name of the file.
	 * @return CutCopyFileInfo   The prepared file information for copy-paste
	 *         operations.
	 * @throws ResourceNotFoundException if the resource does not exist.
	 */
	public CutCopyFileInfo prepareFileInfoCopyPaste(String location, String fileName) {

		HIResource resource = serviceDB.getResourceByUrl(location + "/" + fileName);
		HIResource tempResource = null;
		if (resource == null) {
			throw new ResourceNotFoundException("The Resource does not exists");
		}

		CutCopyFileInfo fileInfo = new CutCopyFileInfo();
		fileInfo.setResourceId(resource.getResourceId());
		String[] pathSplit = resource.getResourceURL().split(Pattern.quote("/"));
		fileInfo.setName(pathSplit[pathSplit.length - 1]);
		fileInfo.setTitle(resource.getTitle());
		if (resource.getResourceType().getName().equals(FOLDER)) {
			fileInfo.setTitle(null);
			fileInfo.setName(resource.getTitle());
			List<HIResource> contents = serviceDB.getResourceByParentId(resource.getResourceId());
			List<CutCopyFileInfo> children = new ArrayList<>();
			for (HIResource e : contents) {
				if (!e.getDeleted()) {
					String[] pathSplitting = e.getResourceURL().split(Pattern.quote("/"));
					String name = pathSplitting[pathSplitting.length - 1];
					children.add(prepareFileInfoCopyPaste(
							e.getResourceURL().substring(0, e.getResourceURL().length() - name.length() - 1), name));
				}
			}
			fileInfo.setChildren(children);
			fileInfo.setType(FOLDER);
			JsonObject json = new JsonObject();
			json.addProperty("selectable", "true");
			fileInfo.setOptions(json);
		} else {
			fileInfo.setType(FILE);
			fileInfo.setExtension(resource.getResourceType().getExtension().replace(".", ""));
			fileInfo.setOptions(new JsonObject());
		}
		fileInfo.setPath(resource.getResourceURL());
		Integer createdBy = resource.getCreatedBy();
		logger.info("Created By  : {}", createdBy);
		ResourcePermissionLevelsHolder resourcePermissionLevelsHolder = ApplicationContextAccessor
				.getBean(ResourcePermissionLevelsHolder.class);
		Integer permissionLevel = null;
		if(createdBy == null ) {
			JsonObject options = fileInfo.getOptions();
			options.addProperty("public",true);

			permissionLevel = resourcePermissionLevelsHolder.publicResourceAccessLevel();
		} else if (createdBy.equals(Integer.valueOf(AuthenticationUtils.getUserId()))) {
			permissionLevel = resourcePermissionLevelsHolder.ownerAccessLevel();
		} else {
			tempResource = resource;
			Map<Integer, Integer> securityMap = serviceDB.getSecurityMap();
			while (tempResource != null) {
				Integer tempCreatedBy = tempResource.getCreatedBy();
				if (tempCreatedBy == null) {
					permissionLevel = resourcePermissionLevelsHolder.publicResourceAccessLevel();
				} else if (tempCreatedBy.equals(Integer.valueOf(AuthenticationUtils.getUserId()))) {
					permissionLevel = resourcePermissionLevelsHolder.ownerAccessLevel();
				} else if (securityMap.get(tempResource.getResourceId()) != null) {
					permissionLevel = securityMap.get(tempResource.getResourceId());
				}
				try {
					tempResource = serviceDB.getHIResourceById(tempResource.getParentId());
				} catch (Exception e) {
					logger.info(e.getMessage());
					tempResource = null;
				}

			}
		}

		fileInfo.setPermissionLevel(String.valueOf(permissionLevel));
		fileInfo.setLastModified(tempResource != null ? tempResource.getLastUpdatedTime().getTime()
				: resource.getLastUpdatedTime().getTime());
		logger.info("FileInfo : {} ", fileInfo);
		return fileInfo;

	}

}
