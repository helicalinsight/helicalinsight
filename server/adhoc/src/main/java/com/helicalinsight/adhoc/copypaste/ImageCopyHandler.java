package com.helicalinsight.adhoc.copypaste;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceImages;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.utils.UUIDGenerator;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.resourcedb.processor.DBProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class ImageCopyHandler extends HiResourceCopyHandler {
    @Autowired
    HIResourceServiceDB hiResourceServiceDB;

    @Autowired
    HiResourceCCPUtility hiResourceCCPUtility;


    @Autowired
    EFWDConnectionService efwdConnectionService;

    /**
     * Copies the resource, ensuring uniqueness and handling associated data.
     */
    @Override
    public void copyResource() {

        if (getSource().getParentId() != getDestinationResourceId().getResourceId()) {
            HIResource isSameResouceNameAlreadyExisted = hiResourceServiceDB.getResourceByUrl(getPrefix(), Boolean.FALSE);
            if (isSameResouceNameAlreadyExisted != null && !isSameResouceNameAlreadyExisted.getDeleted()) {
                if (!getOnConflictSkip()) {
                    String sourcePath = UUIDGenerator.getUuid();
                    String prefixUrl = getDestinationResourceId().getResourceURL() + "/" + sourcePath + getSource().getResourceType().getExtension();
                    HIResource hiResource = doCopy(prefixUrl, sourcePath);
                    hiResourceCCPUtility.deleteOverridenResourceAndUpdateCopiedResource(hiResource, isSameResouceNameAlreadyExisted);
                }
            } else {
                if (isSameResouceNameAlreadyExisted != null) {
                    Format secondsFormat = new SimpleDateFormat("ss");
                    String generatedSourcePath = DBProcessor.checkAndReplaceSpecialChars(getSourcePath()).trim() +
                            "_" + secondsFormat.format(new Date()).substring(0, 2);
                    String generatedUrl = getPrefix().substring(0, getPrefix().length() - getSourcePath().length()) + generatedSourcePath;
                    doCopy(generatedUrl, generatedSourcePath);
                } else
                    doCopy(getPrefix(), getSourcePath());
            }
        } else
            doCopy(getPrefix(), getSourcePath());
    }

    private HIResource doCopy(String prefix, String sourcePath) {
        HIResource sourceHiResource = getSource();
        HIResource hiResource = hiResourceCCPUtility.prepareNewReplica(
                sourceHiResource, getDestinationResourceId(), prefix, sourcePath);
        HIResourceImages images = new HIResourceImages();
        HIResourceImages hiResourceImages = sourceHiResource.getHiResourceImages();
        images.setContent(hiResourceImages.getContent());
        images.setContentType(hiResourceImages.getContentType());
        hiResource.setHiResourceImages(images);

        hiResourceServiceDB.addHIResource(hiResource);
        hiResourceCCPUtility.saveSecurityInfoReplica(sourceHiResource.getResourceId(), hiResource);
        if (sourceHiResource.getDeleted())
            hiResourceCCPUtility.doSoftDelete(hiResource.getResourceURL());
        getDestinationResourceId().setLastUpdatedTime(new Date());
        hiResourceServiceDB.editHIResource(getDestinationResourceId());


        return hiResource;
    }


}
