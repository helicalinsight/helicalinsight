package com.helicalinsight.export.handler.importres;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceHReport;
import com.helicalinsight.admin.model.HIResourceImages;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.ResourceUtils;
import com.helicalinsight.export.exception.ResourceImportException;
import com.helicalinsight.export.service.ResourceIOHandler;
import com.helicalinsight.resourcedb.Deleted;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;
import java.util.Map;

/**
 * Handles the import of image resources.
 * <p>
 * Supports two package shapes:
 * <ul>
 *   <li>New: metadata in primary JSON + binary in {@code _image} sidecar listed in {@code Manifest.imageFiles}</li>
 *   <li>Legacy: full resource (including base64 content) embedded in the primary JSON</li>
 * </ul>
 * Binary payloads are stream-mapped to avoid the ObjectNode owner-rewrite path that NPEs on large images.
 * <p>
 * The handler creates or updates {@link HIResourceImages} instances and handles the associated import of
 * schedules if specified in the manifest. Prototype-scoped for concurrent imports.
 */
@Component("imageImportHandler")
@Scope("prototype")
public class HIResourceImagesImportHandler extends AbstractResourceImportHandler {

    /**
     * Imports an image resource.
     *
     * @param resourceUrl URL of the image resource to import.
     * @return The imported {@link HIResource} representing the image resource.
     */
    @Override
    public HIResource importResource(String resourceUrl) {

        String fileName = StringUtils.substringAfterLast(resourceUrl, "/");
        String parentUrl = StringUtils.substringBeforeLast(resourceUrl, fileName);
        parentUrl = StringUtils.chop(parentUrl);
        String onConflict = context.getRequest().getOnConflict();

        HIResource report = readImageResource(resourceUrl);
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

                resource.setCreatedBy(report.getCreatedBy() == null ? null : importedBy);
                resource.setHiResourceImages(dbReport);
                resource.setLastUpdatedTime(new Date());
                resource.setTitle(report.getTitle());
                resource.setResourcePath(report.getResourcePath());
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

            resource = ResourceUtils.newHIResource(JsonUtils.getImageExtension(), context.getDate(), importedBy, resourceUrl, fileName, report.getTitle(), null, report.getCreatedBy() == null);
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

    /**
     * Loads the image resource for import, preferring the {@code _image} sidecar when present
     * in the manifest (new packages), otherwise stream-reading the primary JSON (legacy packages).
     */
    private HIResource readImageResource(String resourceUrl) {
        String relativeUrl = context.removeDestination(resourceUrl);
        String sidecarPath = manifestUtils.getImageFile(relativeUrl, context.getManifest());

        if (sidecarPath != null) {
            HIResource report = fileReader.read(context, resourceUrl, HIResource.class);
            HIResourceImages sidecarImages = streamRead(resolveResourcesPath(sidecarPath), HIResourceImages.class);
            if (sidecarImages == null) {
                throw new ResourceImportException("Failed to read image sidecar: " + sidecarPath);
            }
            HIResourceImages imported = report.getHiResourceImages();
            if (imported == null) {
                imported = new HIResourceImages();
                report.setHiResourceImages(imported);
            }
            if (sidecarImages.getContentType() != null) {
                imported.setContentType(sidecarImages.getContentType());
            }
            imported.setContent(sidecarImages.getContent());
            return report;
        }

        return streamRead(resolvePrimaryResourcePath(relativeUrl), HIResource.class);
    }

    private String resolvePrimaryResourcePath(String relativeUrl) {
        String fileName = relativeUrl.contains("/") ? StringUtils.substringAfterLast(relativeUrl, "/") : relativeUrl;
        return context.getResourcesDirectory()
                + (Integer.valueOf(context.getManifest().getVersion()) > 0 ? relativeUrl : fileName);
    }

    private String resolveResourcesPath(String relativePath) {
        return context.getResourcesDirectory() + relativePath;
    }

    private <T> T streamRead(String filePath, Class<T> clazz) {
        T result = fileUtils.readFile(new File(filePath), clazz);
        if (result == null) {
            throw new ResourceImportException("Failed to read import file: " + filePath);
        }
        return result;
    }
}
