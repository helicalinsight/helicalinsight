package com.helicalinsight.export.handler.importres;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceHReport;
import com.helicalinsight.admin.model.HIResourceImages;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.ResourceUtils;
import com.helicalinsight.export.service.ResourceIOHandler;
import com.helicalinsight.resourcedb.Deleted;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Handles the import of HReport resources.
 * This class is responsible for importing HReport resources from a specified file. It reads the HReport resource,
 * determines the import mode (update or skip), and processes the import accordingly.
 * <p>
 * The HReport import handler creates or updates {@link HIResourceHReport} instances and handles the associated import of
 * schedules if specified in the manifest. The handler is designed to be a prototype-scoped component, allowing for
 * multiple instances to handle concurrent imports independently.
 */
@Component("imageImportHandler")
@Scope("prototype")
public class HIResourceImagesImportHandler extends AbstractResourceImportHandler {

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
        HIResource report = fileReader.read(context, resourceUrl, HIResource.class);
        HIResourceImages importedResource = report.getHiResourceImages();
        HIResource resource = serviceDb.getResourceByUrl(resourceUrl, Deleted.FALSE);
        Map<Integer, String> newOldImageIds = context.getNewOldImageIds();

        if (null != resource) {
            if (onConflict.equalsIgnoreCase("update") && context.recover(resource)) {
                Integer importedBy = Boolean.TRUE.equals(context.getRequest().getOptions().getShare()) ? resource.getCreatedBy() :
                        Integer.valueOf(SecurityUtils.securityObject().getCreatedBy());
                newOldImageIds.put(report.getResourceId(), resource.getResourceId()+":"+resource.getResourceURL());
                HIResourceImages dbReport = resource.getHiResourceImages();

                dbReport.setContentType(importedResource.getContentType());
                dbReport.setContent(importedResource.getContent());

                resource.setCreatedBy(importedBy);
                resource.setHiResourceImages(dbReport);
                serviceDb.editHIResource(resource);
                context.appendUpdate(resource.getResourceURL());

            } else {
                context.appendSkip(resource.getResourceURL());
            }
        } else {
            if (fileName.contains(".")) {
                fileName = fileName.substring(0, fileName.lastIndexOf("."));
            }
            Integer importedBy = Boolean.TRUE.equals(context.getRequest().getOptions().getShare()) ? report.getCreatedBy() :
                    Integer.valueOf(SecurityUtils.securityObject().getCreatedBy());

            resource = ResourceUtils.newHIResource(JsonUtils.getImageExtension(), context.getDate(), importedBy, resourceUrl, fileName, fileName, null, report.getCreatedBy() == null);
            HIResource parent = context.getResourceUrlMap().get(parentUrl + "." + JsonUtils.getFolderFileExtension());
            if (null != parent) resource.setParentId(parent.getResourceId());
            resource.setLastUpdatedTime(context.getDate());
            resource.setCreated_date(context.getDate());
            resource.setHiResourceImages(importedResource);
            Integer newlyAdded = serviceDb.addHIResource(resource);
            newOldImageIds.put(report.getResourceId(), newlyAdded+":"+resource.getResourceURL());
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
}
