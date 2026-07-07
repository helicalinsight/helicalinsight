package com.helicalinsight.export.handler;

import com.helicalinsight.adhoc.service.HICubeDAOService;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceImages;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.dto.ResourceExtension;
import com.helicalinsight.export.dto.ResourceOptions;
import com.helicalinsight.resourcedb.HIResourceDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class handles the writing of Helical Insight resources of type "cube" during the export process.
 * It extends the {@link AbstractResourceWriterHandler} class and implements the necessary logic for writing cube resources.
 * The write method is responsible for writing the cube resource along with its associated metadata and dependencies.
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
        dataWriter.write(hResource, dir, report, "");
        manifestUtils.insertPath(report.getPath(), manifest);
        share(report, manifest, options, dir);
        schedule(report, manifest, options, dir);
    }

}
