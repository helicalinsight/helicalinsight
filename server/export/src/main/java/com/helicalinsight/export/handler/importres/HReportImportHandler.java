package com.helicalinsight.export.handler.importres;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceHReport;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.ResourceUtils;
import com.helicalinsight.export.service.ResourceIOHandler;
import com.helicalinsight.resourcedb.Deleted;
import com.helicalinsight.resourcesecurity.SecurityUtils;
/**
 * Handles the import of HReport resources.
 * This class is responsible for importing HReport resources from a specified file. It reads the HReport resource,
 * determines the import mode (update or skip), and processes the import accordingly.
 *
 * The HReport import handler creates or updates {@link HIResourceHReport} instances and handles the associated import of
 * schedules if specified in the manifest. The handler is designed to be a prototype-scoped component, allowing for
 * multiple instances to handle concurrent imports independently.
 */
@Component("hrImportHandler")
@Scope("prototype")
public class HReportImportHandler extends AbstractResourceImportHandler {

	/**
     * Imports an HReport resource.
     *
     * @param resourceUrl URL of the HReport resource to import.
     * @return The imported {@link HIResource} representing the HReport resource.
     */
	@Override
	public HIResource importResource(String resourceUrl) {
		
		String fileName = StringUtils.substringAfterLast(resourceUrl, "/");
		String parentUrl = StringUtils.substringBeforeLast(resourceUrl, fileName);
		parentUrl = StringUtils.chop(parentUrl);
		String onConflict = context.getRequest().getOnConflict();
		HIResourceHReport report = fileReader.read(context,resourceUrl, HIResourceHReport.class);
		HIResource resource = serviceDb.getResourceByUrl(resourceUrl,Deleted.FALSE);
		
		if (null != resource) {
			if(onConflict.equalsIgnoreCase("update") && context.recover(resource)){
				
				HIResourceHReport dbReport = resource.getHiResourceHReport();
				dbReport.setCanvasColumns(report.getCanvasColumns());
				dbReport.setReportName(report.getReportName());
				dbReport.setState(report.getState());
				
				Date date = context.getDate();
				dbReport.setLastUpdatedTime(date);
				resource.setCreatedBy(report.getCreatedBy());
				resource.setLastUpdatedTime(context.getDate());
				Integer importedBy = Boolean.TRUE.equals(context.getRequest().getOptions().getShare()) ? resource.getCreatedBy() : 
					Integer.valueOf(SecurityUtils.securityObject().getCreatedBy());
				if(report.getCreatedBy() == null ) {
					resource.setCreatedBy(null);
					dbReport.setCreatedBy(null);
				}
				else {
					resource.setCreatedBy(importedBy);
					dbReport.setCreatedBy(importedBy);
				}
				
				String mdUrl = this.context.getManifest().getDependencies().get(context.removeDestination(resourceUrl)).get(1);
				HIResource mdResource = this.context.getResourceUrlMap().get(context.addDestination(mdUrl));
				dbReport.setHiResourceMetadata(mdResource.getResourceId());
				resource.setTitle(report.getReportName());
				resource.setHiResourceHReport(dbReport);
				serviceDb.editHIResource(resource);
				context.appendUpdate(resource.getResourceURL());
				
			}
			else {
				context.appendSkip(resource.getResourceURL());
			}
		} else {
			resource = createNewReport(report, fileName, parentUrl, resourceUrl);
			context.appendInsert(resource.getResourceURL());
		}
		shareHandler.importResource(resource, context.getRequest(), context.getManifest());
		if(Boolean.TRUE.equals(context.getRequest().getOptions().getSchedules())) {
			ResourceIOHandler scheduleHandler = (ResourceIOHandler) ApplicationContextAccessor.getBean("scheduleIOHandler");
			scheduleHandler.setContext(context);
			scheduleHandler.importResource(resource, resourceUrl, onConflict);
		}
		return resource;
	}
	/**
     * Creates a new HReport resource or updates an existing one.
     *
     * @param report      {@link HIResourceHReport} instance to create or update from.
     * @param fileName    Name of the file containing the HReport resource.
     * @param parentUrl   URL of the parent resource.
     * @param resourceUrl URL of the HReport resource.
     * @return The created or updated {@link HIResource} representing the HReport resource.
     */
	public HIResource createNewReport(HIResourceHReport report, String fileName, String parentUrl, String resourceUrl) {
		Integer ownerId = Boolean.TRUE.equals(context.getRequest().getOptions().getShare()) ? report.getCreatedBy() : null;
		if(fileName.contains(".")){
			fileName=fileName.substring(0,fileName.lastIndexOf("."));
		}
		HIResource resource = ResourceUtils.newHIResource(JsonUtils.getHrReportExtension(), context.getDate(), ownerId, resourceUrl, fileName, report.getReportName(), null,report.getCreatedBy() == null);
		String mdUrl = this.context.getManifest().getDependencies().get(context.removeDestination(resourceUrl)).get(1);
		HIResource mdResource = this.context.getResourceUrlMap().get(context.addDestination(mdUrl));
		report.setHiResourceMetadata(mdResource.getResourceId());
		HIResource parent = context.getResourceUrlMap().get(parentUrl+"."+JsonUtils.getFolderFileExtension());
		if (null != parent) resource.setParentId(parent.getResourceId());
		report.setCreatedBy(resource.getCreatedBy());
		report.setLastUpdatedTime(context.getDate());
		report.setCreatedDate(context.getDate());
		resource.setHiResourceHReport(report);
		serviceDb.addHIResource(resource);
		return resource;
	}
}
