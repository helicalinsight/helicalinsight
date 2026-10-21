package com.helicalinsight.export.handler;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceImages;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.dto.ResourceExtension;
import com.helicalinsight.export.dto.ResourceOptions;
import com.helicalinsight.export.utils.ResourceSuffix;
import com.helicalinsight.resourcedb.HIResourceDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Writes Helical Insight image resources during export.
 * Image binary content is written to a {@code _image} sidecar file; the primary
 * resource JSON contains metadata only.
 */
@Component("imageWriterHandler")
public class ImageWriterHandler extends AbstractResourceWriterHandler {

    @Autowired
    private FolderWriterHandler folderWriterHandlerHandler;



    @Override
    public void write(HIResourceDTO report, String dir, Manifest manifest, ResourceOptions options) {
        HIResource hResource = serviceDB.getResourceByIdIgnoreFilter(report.getResourceId());

        String[] dirArr = StringUtils.split(hResource.getResourceURL(), "/");
        String depFolderUrl = StringUtils.join(dirArr, "/", 0, dirArr.length - 1);
        String depFolderUrlWithExtension = depFolderUrl + ResourceExtension.FOLDER.getValue();
        if (!manifestUtils.pathExists(depFolderUrlWithExtension, manifest)) {
            HIResource resourceDir = serviceDB.getResourceByUrl(depFolderUrl, false);
            if (resourceDir != null) {
                HIResourceDTO folderDTO = dtoMapper.map(resourceDir);
                folderWriterHandlerHandler.write(folderDTO, dir, manifest, options);
            }
        }
        manifestUtils.insertDependency(report.getPath(), depFolderUrl, manifest);

        HIResourceImages sourceImages = hResource.getHiResourceImages();
        HIResourceImages imagePayload = new HIResourceImages();
        if (sourceImages != null) {
            imagePayload.setContentType(sourceImages.getContentType());
            imagePayload.setContent(sourceImages.getContent());
            byte[] content = sourceImages.getContent();
            sourceImages.setContent(null);
            try {
                dataWriter.write(hResource, dir, report, "");
            } finally {
                sourceImages.setContent(content);
            }
        } else {
            dataWriter.write(hResource, dir, report, "");
        }

        dataWriter.write(imagePayload, dir, report, ResourceSuffix.IMAGE);
        recycleBinHandler.writeResource(hResource, report, dir, manifest);
        manifestUtils.insertImageFile(report, manifest);
        manifestUtils.insertPath(report.getPath(), manifest);
        share(report, manifest, options, dir);
        schedule(report, manifest, options, dir);
    }

}
