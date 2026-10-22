package com.helicalinsight.export.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.export.ExportResourceManager;
import com.helicalinsight.export.download.DownloadArtifact;
import com.helicalinsight.export.dto.ResourceExportRequest;
import com.helicalinsight.export.dto.ResourceOptions;
import com.helicalinsight.export.exception.ResourceExportException;
import com.helicalinsight.resourcedb.HIResourceDTO;
import com.helicalinsight.resourcedb.processor.HIResourceOfActiveUser;
/**
 * This class handles the export of resources based on the provided export request.
 * It retrieves the necessary resources, applies security, and delegates the actual export to the ExportResourceManager.
 * The exported data is returned as a byte array.
 *
 */
@Component
public class ResourceExportHandler {

	@Autowired
	private HIResourceServiceDB serviceDb;

	@Autowired
	private ExportResourceManager manager;

	/**
     * Exports the resources based on the provided export request.
     *
     * @param request  		resource export request containing export parameters.
     * @param response 	 	HttpServletResponse object to handle response-related tasks.
     * @return The exported data as a byte array.
     * @throws Exception If an error occurs during the export process.
     */
	public byte[] export(ResourceExportRequest request,HttpServletResponse response) throws Exception {
		return manager.write(resolveResources(request), request.getDir(), request.getOptions(), response);
	}

	/**
	 * Materialize export zip on disk for resumable download (no response write, no immediate cleanup).
	 */
	public DownloadArtifact exportToArtifact(ResourceExportRequest request) throws Exception {
		return exportToArtifact(request, null);
	}

	/**
	 * Materialize export zip on disk for resumable download, cooperatively aborting when the ticket is aborted.
	 */
	public DownloadArtifact exportToArtifact(ResourceExportRequest request, String ticketId) throws Exception {
		return manager.writeToFile(resolveResources(request), request.getDir(), request.getOptions(), ticketId);
	}

	private List<HIResourceDTO> resolveResources(ResourceExportRequest request) {
		String dir = request.getDir();
		String fileName = request.getFile();
		if(!fileName.isEmpty()) {
			dir = dir + "/" + fileName;
		}
		String[] dirArr = dir.split("/");
		List<HIResourceDTO> dtoList = new ArrayList<>();
		List<HIResource> resourceList = new ArrayList<>();
		if(dirArr.length > 1) {
			StringBuilder url = new StringBuilder();
			for(int level = 0 ; level < dirArr.length; level++) {
				url.append(dirArr[level]);
				HIResource resource =  serviceDb.findResourceByUrl(url.toString());
				if(resource == null) {
					throw new ResourceExportException("Resource not found!");
				}
				resourceList.add(resource);
				if(resource.getFolder() && level == dirArr.length-1) {
					resourceList.addAll(getAllRelatedFiles(resource.getResourceId()));
				}
				url.append("/");
			}
			Map<Integer,Integer> securityMap = serviceDb.getSecurityMap();
			HIResourceOfActiveUser activeUser = new HIResourceOfActiveUser(securityMap, resourceList);
			dtoList = activeUser.getResourceDTOList();
		}
		else if(StringUtils.isNotBlank(dir)) {
			HIResource resource = serviceDb.getResourceByUrl(dir);
			if (null == resource) {
				throw new ResourceExportException("Resource not found!");
			}
			List<HIResource> children = new ArrayList<>();
			children.add(resource);
			if (Boolean.TRUE.equals(resource.getFolder())) {
				children.addAll(getAllRelatedFiles(resource.getResourceId()));
			}
			Map<Integer, Integer> securityMap = serviceDb.getSecurityMap();
			HIResourceOfActiveUser activeUser = new HIResourceOfActiveUser(securityMap, children);
			dtoList = activeUser.getResourceDTOList();
		}
		
		
		else {
			ResourceOptions options = request.getOptions();
			if (options != null && Boolean.TRUE.equals(options.getExportEverything())) {
				if (!AuthenticationUtils.isSuperAdmin(AuthenticationUtils.getLoggedInUserOrganizationId())) {
					throw new ResourceExportException(
							"Export everything is available only for super admin.");
				}
				dtoList = serviceDb.findAllResourcesUnscoped().getResourceDTOList();
			} else {
				dtoList = serviceDb.findAllResources().getResourceDTOList();
			}
		}
		
		return dtoList;
	}
	/**
	 * @param parentId  id in integer format
	 * @return list of resourceId
	 */
	private List<HIResource> getAllRelatedFiles(Integer parentId) {
		List<HIResource> children = new ArrayList<>();
		List<HIResource> resources = serviceDb.getResourceByParentId(parentId);
		for (HIResource resource : resources) {
			if (Boolean.TRUE.equals(resource.getFolder())) {
				children.addAll(getAllRelatedFiles(resource.getResourceId()));
			}
			children.add(resource);
		}
		return children;
	}
}
