package com.helicalinsight.export.handler.importres;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceAIModel;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.ResourceUtils;
import com.helicalinsight.export.service.ResourceIOHandler;
import com.helicalinsight.resourcedb.Deleted;
import com.helicalinsight.resourcesecurity.SecurityUtils;

/**
 * Handles the import of AI Model resources.
 * This class is responsible for importing AI Model resources from a specified file. It reads the AI Model resource,
 * determines the import mode (update or skip), and processes the import accordingly.
 *
 * The AI Model import handler creates or updates {@link HIResourceAIModel} instances and handles the associated import of
 * schedules if specified in the manifest. The handler is designed to be a prototype-scoped component, allowing for
 * multiple instances to handle concurrent imports independently.
 */
@Component("modelImportHandler")
@Scope("prototype")
public class ModelImportHandler extends AbstractResourceImportHandler {

	/**
	 * Imports an AI Model resource.
	 *
	 * @param resourceUrl URL of the AI Model resource to import.
	 * @return The imported {@link HIResource} representing the AI Model resource.
	 */
	@Override
	public HIResource importResource(String resourceUrl) {

		String fileName = StringUtils.substringAfterLast(resourceUrl, "/");
		String parentUrl = StringUtils.substringBeforeLast(resourceUrl, fileName);
		parentUrl = StringUtils.chop(parentUrl);
		String onConflict = context.getRequest().getOnConflict();
		HIResourceAIModel model = fileReader.read(context, resourceUrl, HIResourceAIModel.class);
		HIResource resource = serviceDb.getResourceByUrl(resourceUrl, Deleted.FALSE);

		if (null != resource) {
			if (onConflict.equalsIgnoreCase("update") && context.recover(resource)) {

				HIResourceAIModel dbModel = resource.getAiModel();
				dbModel.setAiModelName(model.getAiModelName());
				dbModel.setState(model.getState());

				Date date = context.getDate();
				dbModel.setLastUpdatedTime(date);
				resource.setCreatedBy(model.getCreatedBy());
				resource.setLastUpdatedTime(context.getDate());
				Integer importedBy = Boolean.TRUE.equals(context.getRequest().getOptions().getShare()) ? resource.getCreatedBy()
						: Integer.valueOf(SecurityUtils.securityObject().getCreatedBy());
				if (model.getCreatedBy() == null) {
					resource.setCreatedBy(null);
					dbModel.setCreatedBy(null);
				} else {
					resource.setCreatedBy(importedBy);
					dbModel.setCreatedBy(importedBy);
				}

				String mdUrl = this.context.getManifest().getDependencies().get(context.removeDestination(resourceUrl)).get(1);
				HIResource mdResource = this.context.getResourceUrlMap().get(context.addDestination(mdUrl));
				dbModel.setHiResourceMetadata(mdResource.getResourceId());
				resource.setTitle(model.getAiModelName());
				resource.setAiModel(dbModel);
				serviceDb.editHIResource(resource);
				context.appendUpdate(resource.getResourceURL());

			} else {
				context.appendSkip(resource.getResourceURL());
			}
		} else {
			resource = createNewModel(model, fileName, parentUrl, resourceUrl);
			context.appendInsert(resource.getResourceURL());
		}
		shareHandler.importResource(resource, context.getRequest(), context.getManifest());
		if (Boolean.TRUE.equals(context.getRequest().getOptions().getSchedules())) {
			ResourceIOHandler scheduleHandler = (ResourceIOHandler) ApplicationContextAccessor.getBean("scheduleIOHandler");
			scheduleHandler.setContext(context);
			scheduleHandler.importResource(resource, resourceUrl, onConflict);
		}
		return resource;
	}

	/**
	 * Creates a new AI Model resource.
	 *
	 * @param model       {@link HIResourceAIModel} instance to create from.
	 * @param fileName    Name of the file containing the AI Model resource.
	 * @param parentUrl   URL of the parent resource.
	 * @param resourceUrl URL of the AI Model resource.
	 * @return The created {@link HIResource} representing the AI Model resource.
	 */
	public HIResource createNewModel(HIResourceAIModel model, String fileName, String parentUrl, String resourceUrl) {
		Integer ownerId = Boolean.TRUE.equals(context.getRequest().getOptions().getShare()) ? model.getCreatedBy() : null;
		if (fileName.contains(".")) {
			fileName = fileName.substring(0, fileName.lastIndexOf("."));
		}
		HIResource resource = ResourceUtils.newHIResource(JsonUtils.getAiModelExtension(), context.getDate(), ownerId,
				resourceUrl, fileName, model.getAiModelName(), null, model.getCreatedBy() == null);
		String mdUrl = this.context.getManifest().getDependencies().get(context.removeDestination(resourceUrl)).get(1);
		HIResource mdResource = this.context.getResourceUrlMap().get(context.addDestination(mdUrl));
		model.setHiResourceMetadata(mdResource.getResourceId());
		HIResource parent = context.getResourceUrlMap().get(parentUrl + "." + JsonUtils.getFolderFileExtension());
		if (null != parent) {
			resource.setParentId(parent.getResourceId());
		}
		model.setCreatedBy(resource.getCreatedBy());
		model.setLastUpdatedTime(context.getDate());
		model.setCreatedDate(context.getDate());
		resource.setAiModel(model);
		serviceDb.addHIResource(resource);
		return resource;
	}
}
