package com.helicalinsight.export.handler.importres;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.helicalinsight.admin.model.ResourceEfwContents;
import com.helicalinsight.admin.service.ResourceEfwContentsService;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceFolder;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.ResourceUtils;
import com.helicalinsight.export.dto.ImportRequest;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.service.DatasourceFactory;
import com.helicalinsight.export.service.DatasourceHandler;
import com.helicalinsight.resourcedb.Deleted;
import com.helicalinsight.resourcedb.processor.DBProcessor;
import com.helicalinsight.resourcesecurity.SecurityUtils;

/**
 * Handles the import of EFW folder resources.
 * This class is responsible for importing EFW folder resources from a specified file. It reads the folder resource,
 * determines the import mode (update or skip), and processes the import accordingly.
 *
 * The folder import handler creates or updates HIResourceFolder instances and handles the associated import of datasource
 * resources if specified in the manifest. The handler is designed to be a prototype-scoped component, allowing for
 * multiple instances to handle concurrent imports independently.
 */
@Component("efwfolderImportHandler")
@Scope("prototype")
public class FolderImportHandler extends AbstractResourceImportHandler {
	/**
     * Imports an EFW folder resource.
     *
     * @param folderURL 		URL of the EFW folder resource to import.
     * @return The imported HIResource representing the EFW folder resource.
     */
	@Autowired
	private ResourceEfwContentsService resourceEfwContentsService;
	@Override
	public HIResource importResource(String folderURL) {
		String fileName = StringUtils.substringAfterLast(folderURL, "/");
		String parentUrl = StringUtils.substringBeforeLast(folderURL, fileName);
		String folderURLWithoutExtension = FilenameUtils.removeExtension(folderURL);
		
		ImportRequest importRequest = context.getRequest();
		Manifest manifest = context.getManifest();
		String conflictMode = importRequest.getOnConflict();
		HIResourceFolder folder = fileReader.read(context,folderURL, HIResourceFolder.class);
		HIResource resource = serviceDb.getResourceByUrl(folderURLWithoutExtension,Deleted.FALSE);
		if (null != resource) {
			if("update".equalsIgnoreCase(conflictMode) && context.recover(resource)) {
				updateFolder(folder, resource);
				context.appendUpdate(resource.getResourceURL());
			}
			else {
				context.appendSkip(resource.getResourceURL());
			}
		} else {
			resource = createNewFolder(folder, parentUrl, folderURLWithoutExtension);
			context.appendInsert(resource.getResourceURL());
			Map<String, List<String>> images = manifest.getImages();
			List<String> fileNames = images.get(resource.getResourceURL());
			if(fileNames!=null) {
				for (String it : fileNames) {
					String splitArr[] = it.split("_hiimg_");
					File file = new File(splitArr[0]); // Replace with your file path

					ResourceEfwContents rc = new ResourceEfwContents();
					try {
						byte[] fileBytes = Files.readAllBytes(file.toPath());
						rc.setContent(fileBytes);
					} catch (IOException e) {
						e.printStackTrace();
					}
					rc.setContentType(splitArr[1]);
					rc.setFileName(it);
					rc.setResourceId(resource.getResourceId());
					resourceEfwContentsService.addHIResourceEfwContent(rc);
				}
			}
		}
		shareHandler.setContext(context);
		shareHandler.importResource(resource,importRequest,manifest);
		boolean dsExists = manifestUtils.compareOptions(importRequest.getOptions(), manifest, "datasource");
		if (dsExists) {
			String dsFileName = manifestUtils.getDatasource(context.removeDestination(resource.getResourceURL()), manifest);
			if (null != dsFileName) {
				DatasourceHandler dsHandler = DatasourceFactory.getHandler("folder");
				dsHandler.setContext(context);
				dsHandler.importResource(null, dsFileName, conflictMode);
			}
		}
		if(!context.getResourceUrlMap().containsKey(folderURL)) {
			context.getResourceUrlMap().put(folderURL,resource);
		}
		return resource;
	}
	/**
     * Updates an existing EFW folder resource.
     *
     * @param folder   		 HIResourceFolder instance to update from.
     * @param resource 		 existing HIResource representing the EFW folder resource.
     */
	public void updateFolder(HIResourceFolder folder, HIResource resource) {
		Date date = context.getDate();
		folder.setLastUpdatedTime(date);
		folder.setCreatedDate(date);
		resource.setHiResourceFolder(folder);
		resource.setLastUpdatedTime(date);
		resource.setTitle(folder.getTitle());
		Integer ownerId = Boolean.TRUE.equals(context.getRequest().getOptions().getShare()) ? folder.getCreatedBy() : 
			Integer.valueOf(SecurityUtils.securityObject().getCreatedBy());
		resource.setCreatedBy(folder.getCreatedBy() == null ? null : ownerId);
		serviceDb.editHIResource(resource);

		Integer resourceId = resource.getResourceId();
		List<ResourceEfwContents> efwdFileContent=resourceEfwContentsService.fetchResourceEfwContentByResourceId(resourceId);
		Manifest manifest = context.getManifest();
		Map<String, List<String>> images = manifest.getImages();
if(images!=null) {
	List<String> fileNames = images.get(resource.getResourceURL());
	if(fileNames!=null) {
		for (ResourceEfwContents it : efwdFileContent) {
			String mixedName = it.getFileName() + "_hiimg_" + it.getContentType();
			if (fileNames.contains(mixedName)) {
				resourceEfwContentsService.deleteResourceEwfContentByResourceId(it.getFileName(), resourceId);
			}
		}

		for (String it : fileNames) {
			String[] splitArr = it.split("_hiimg_");
			File file = new File(splitArr[0]); // Replace with your file path

			ResourceEfwContents rc = new ResourceEfwContents();
			try {
				byte[] fileBytes = Files.readAllBytes(file.toPath());
				rc.setContent(fileBytes);
			} catch (IOException e) {
				e.printStackTrace();
			}
			rc.setContentType(splitArr[1]);
			rc.setFileName(it);
			rc.setResourceId(resource.getResourceId());
			resourceEfwContentsService.addHIResourceEfwContent(rc);
		}
	}
}

	}
	/**
     * Creates a new EFW folder resource.
     *
     * @param folder       	HIResourceFolder instance to create from.
     * @param parentUrl    	URL of the parent resource.
     * @param uniqueUrl    	unique URL of the new resource.
     * @return The created HIResource representing the new EFW folder resource.
     */
	public HIResource  createNewFolder(HIResourceFolder folder, String parentUrl, String uniqueUrl) {
		Integer ownerId = Boolean.TRUE.equals(context.getRequest().getOptions().getShare()) ? folder.getCreatedBy() : null;
		HIResource parent = context.getResourceUrlMap().get(StringUtils.chop(parentUrl)+"."+JsonUtils.getFolderFileExtension());
		Integer parentId = parent != null ? parent.getResourceId() : null;
		String path = DBProcessor.checkAndReplaceSpecialChars(folder.getTitle());
		HIResource resource = ResourceUtils.newHIResource(JsonUtils.getFolderFileExtension(), context.getDate(), ownerId,uniqueUrl,path,folder.getTitle(),parentId, folder.getCreatedBy() == null);
		resource.setHiResourceFolder(folder);
		folder.setLastUpdatedTime(context.getDate());
		folder.setCreatedDate(context.getDate());
		serviceDb.addHIResource(resource);
		return resource;
	}
}
