package com.helicalinsight.export.handler.importres;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceInstantReport;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.ResourceUtils;
import com.helicalinsight.export.exception.ResourceImportException;
import com.helicalinsight.export.service.ResourceIOHandler;
import com.helicalinsight.export.utils.InstantModelUtils;
import com.helicalinsight.resourcedb.Deleted;
import com.helicalinsight.resourcesecurity.SecurityUtils;

/**
 * Handles the import of Instant BI report resources.
 * Instant reports depend on an AI Model referenced inside the state JSON at {@code subject.model}.
 */
@Component("instantImportHandler")
@Scope("prototype")
public class InstantImportHandler extends AbstractResourceImportHandler {

	private static final String AGENT_EXTENSION = "." + JsonUtils.getAiModelExtension();

	@Override
	public HIResource importResource(String resourceUrl) {

		String fileName = StringUtils.substringAfterLast(resourceUrl, "/");
		String parentUrl = StringUtils.substringBeforeLast(resourceUrl, fileName);
		parentUrl = StringUtils.chop(parentUrl);
		String onConflict = context.getRequest().getOnConflict();
		HIResourceInstantReport report = fileReader.read(context, resourceUrl, HIResourceInstantReport.class);
		HIResource resource = serviceDb.getResourceByUrl(resourceUrl, Deleted.FALSE);

		if (null != resource) {
			if (onConflict.equalsIgnoreCase("update") && context.recover(resource)) {

				HIResourceInstantReport dbReport = resource.getHiResourceInstantReport();
				dbReport.setReportName(report.getReportName());
				applyModelReference(dbReport, report.getState(), resourceUrl);

				Date date = context.getDate();
				dbReport.setLastUpdatedTime(date);
				resource.setCreatedBy(report.getCreatedBy());
				resource.setLastUpdatedTime(context.getDate());
				Integer importedBy = Boolean.TRUE.equals(context.getRequest().getOptions().getShare()) ? resource.getCreatedBy()
						: Integer.valueOf(SecurityUtils.securityObject().getCreatedBy());
				if (report.getCreatedBy() == null) {
					resource.setCreatedBy(null);
					dbReport.setCreatedBy(null);
				} else {
					resource.setCreatedBy(importedBy);
					dbReport.setCreatedBy(importedBy);
				}

				resource.setTitle(report.getReportName());
				resource.setHiResourceInstantReport(dbReport);
				serviceDb.editHIResource(resource);
				context.appendUpdate(resource.getResourceURL());

			} else {
				context.appendSkip(resource.getResourceURL());
			}
		} else {
			resource = createNewReport(report, fileName, parentUrl, resourceUrl);
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

	public HIResource createNewReport(HIResourceInstantReport report, String fileName, String parentUrl, String resourceUrl) {
		Integer ownerId = Boolean.TRUE.equals(context.getRequest().getOptions().getShare()) ? report.getCreatedBy() : null;
		if (fileName.contains(".")) {
			fileName = fileName.substring(0, fileName.lastIndexOf("."));
		}
		HIResource resource = ResourceUtils.newHIResource(JsonUtils.getInstantReportExtension(), context.getDate(), ownerId,
				resourceUrl, fileName, report.getReportName(), null, report.getCreatedBy() == null);
		HIResource parent = context.getResourceUrlMap().get(parentUrl + "." + JsonUtils.getFolderFileExtension());
		if (null != parent) {
			resource.setParentId(parent.getResourceId());
		}
		report.setCreatedBy(resource.getCreatedBy());
		report.setLastUpdatedTime(context.getDate());
		report.setCreatedDate(context.getDate());
		applyModelReference(report, report.getState(), resourceUrl);
		resource.setHiResourceInstantReport(report);
		serviceDb.addHIResource(resource);
		return resource;
	}

	/**
	 * Resolves the model from {@code subject.model} in state, links the imported model resource,
	 * and rewrites the model path in state to match the imported location.
	 */
	private void applyModelReference(HIResourceInstantReport report, String state, String resourceUrl) {
		String originalModelUrl = InstantModelUtils.getModelUrl(state);
		if (StringUtils.isBlank(originalModelUrl)) {
			throw new ResourceImportException("Model reference not found in state for Instant Report : " + resourceUrl);
		}

		HIResource modelResource = resolveImportedModel(originalModelUrl, resourceUrl);
		report.setHiResourceModel(modelResource.getResourceId());
		report.setState(InstantModelUtils.updateModelPath(state, modelResource.getResourceURL()));
	}

	private HIResource resolveImportedModel(String originalModelUrl, String resourceUrl) {
		HIResource modelResource = context.getResourceUrlMap().get(context.addDestination(originalModelUrl));
		if (modelResource != null) {
			return modelResource;
		}

		List<String> dependencies = context.getManifest().getDependencies().get(context.removeDestination(resourceUrl));
		if (dependencies != null) {
			for (String dependency : dependencies) {
				if (StringUtils.endsWithIgnoreCase(dependency, AGENT_EXTENSION)) {
					modelResource = context.getResourceUrlMap().get(context.addDestination(dependency));
					if (modelResource != null) {
						return modelResource;
					}
				}
			}
		}

		throw new ResourceImportException("Imported model not found for Instant Report : " + originalModelUrl);
	}
}
