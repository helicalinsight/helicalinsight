package com.helicalinsight.export.handler.importres;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceAIAgent;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.ResourceUtils;
import com.helicalinsight.export.service.ResourceIOHandler;
import com.helicalinsight.resourcedb.Deleted;
import com.helicalinsight.resourcesecurity.SecurityUtils;

/**
 * Handles the import of AI Agent resources.
 * This class is responsible for importing AI Agent resources from a specified file. It reads the AI Agent resource,
 * determines the import mode (update or skip), and processes the import accordingly.
 *
 * The AI Agent import handler creates or updates {@link HIResourceAIAgent} instances and handles the associated import of
 * schedules if specified in the manifest. The handler is designed to be a prototype-scoped component, allowing for
 * multiple instances to handle concurrent imports independently.
 */
@Component("agentImportHandler")
@Scope("prototype")
public class AgentImportHandler extends AbstractResourceImportHandler {

	/**
	 * Imports an AI Agent resource.
	 *
	 * @param resourceUrl URL of the AI Agent resource to import.
	 * @return The imported {@link HIResource} representing the AI Agent resource.
	 */
	@Override
	public HIResource importResource(String resourceUrl) {

		String fileName = StringUtils.substringAfterLast(resourceUrl, "/");
		String parentUrl = StringUtils.substringBeforeLast(resourceUrl, fileName);
		parentUrl = StringUtils.chop(parentUrl);
		String onConflict = context.getRequest().getOnConflict();
		HIResourceAIAgent agent = fileReader.read(context, resourceUrl, HIResourceAIAgent.class);
		HIResource resource = serviceDb.getResourceByUrl(resourceUrl, Deleted.FALSE);

		if (null != resource) {
			if (onConflict.equalsIgnoreCase("update") && context.recover(resource)) {

				HIResourceAIAgent dbAgent = resource.getAiAgent();
				dbAgent.setAgentName(agent.getAgentName());
				dbAgent.setState(agent.getState());

				Date date = context.getDate();
				dbAgent.setLastUpdatedTime(date);
				resource.setCreatedBy(agent.getCreatedBy());
				resource.setLastUpdatedTime(context.getDate());
				Integer importedBy = Boolean.TRUE.equals(context.getRequest().getOptions().getShare()) ? resource.getCreatedBy()
						: Integer.valueOf(SecurityUtils.securityObject().getCreatedBy());
				if (agent.getCreatedBy() == null) {
					resource.setCreatedBy(null);
					dbAgent.setCreatedBy(null);
				} else {
					resource.setCreatedBy(importedBy);
					dbAgent.setCreatedBy(importedBy);
				}

				String mdUrl = this.context.getManifest().getDependencies().get(context.removeDestination(resourceUrl)).get(1);
				HIResource mdResource = this.context.getResourceUrlMap().get(context.addDestination(mdUrl));
				dbAgent.setHiResourceMetadata(mdResource.getResourceId());
				resource.setTitle(agent.getAgentName());
				resource.setAiAgent(dbAgent);
				serviceDb.editHIResource(resource);
				context.appendUpdate(resource.getResourceURL());

			} else {
				context.appendSkip(resource.getResourceURL());
			}
		} else {
			resource = createNewAgent(agent, fileName, parentUrl, resourceUrl);
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
	 * Creates a new AI Agent resource.
	 *
	 * @param agent       {@link HIResourceAIAgent} instance to create from.
	 * @param fileName    Name of the file containing the AI Agent resource.
	 * @param parentUrl   URL of the parent resource.
	 * @param resourceUrl URL of the AI Agent resource.
	 * @return The created {@link HIResource} representing the AI Agent resource.
	 */
	public HIResource createNewAgent(HIResourceAIAgent agent, String fileName, String parentUrl, String resourceUrl) {
		Integer ownerId = Boolean.TRUE.equals(context.getRequest().getOptions().getShare()) ? agent.getCreatedBy() : null;
		if (fileName.contains(".")) {
			fileName = fileName.substring(0, fileName.lastIndexOf("."));
		}
		HIResource resource = ResourceUtils.newHIResource(JsonUtils.getAiAgentExtension(), context.getDate(), ownerId,
				resourceUrl, fileName, agent.getAgentName(), null, agent.getCreatedBy() == null);
		String mdUrl = this.context.getManifest().getDependencies().get(context.removeDestination(resourceUrl)).get(1);
		HIResource mdResource = this.context.getResourceUrlMap().get(context.addDestination(mdUrl));
		agent.setHiResourceMetadata(mdResource.getResourceId());
		HIResource parent = context.getResourceUrlMap().get(parentUrl + "." + JsonUtils.getFolderFileExtension());
		if (null != parent) {
			resource.setParentId(parent.getResourceId());
		}
		agent.setCreatedBy(resource.getCreatedBy());
		agent.setLastUpdatedTime(context.getDate());
		agent.setCreatedDate(context.getDate());
		resource.setAiAgent(agent);
		serviceDb.addHIResource(resource);
		return resource;
	}
}
