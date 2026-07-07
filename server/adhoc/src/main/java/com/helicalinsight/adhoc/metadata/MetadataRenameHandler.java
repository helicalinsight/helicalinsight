package com.helicalinsight.adhoc.metadata;


import com.helicalinsight.adhoc.service.HIMetadataResourceServiceDB;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceMetadata;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.resourcedb.AbstractResourceRenameAction;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


/**
 * MetadataRenameHandler
 * 
 * This class handles the renaming of metadata resources. It extends AbstractResourceRenameAction
 * and overrides the performAction method to perform the renaming operation.
 */
@Component("metadataRenameHandler")
@Scope("prototype")
public class MetadataRenameHandler extends AbstractResourceRenameAction {

    private static final Logger logger = LoggerFactory.getLogger(MetadataRenameHandler.class);

    @Autowired
    protected HIResourceServiceDB hiResourceServiceDB;

    @Autowired
    protected HIMetadataResourceServiceDB metadataResourceServiceDB;
    /**
     * Performs the renaming action on the metadata resource.
     * @return {@code true} if the rename operation is successful, otherwise {@code false}.
     */
    @Override
    public Boolean performAction() {
        if(!validateName()){
            return false;
        }
        Integer resourceId = this.getResourceId();
        String updatedName = this.getUpdatedName();
        String isVisible = this.getIsVisible();
        HIResource hiResource = hiResourceServiceDB.getHIResourceById(resourceId);
        HIResourceMetadata hiResourceMetadata = metadataResourceServiceDB.giveHIResourceMetadataByResourceId(resourceId);
        if (null != hiResourceMetadata) {
            hiResourceMetadata.setFileName(updatedName);
            hiResource.setTitle(updatedName);
            if (StringUtils.isNotEmpty(isVisible)) {
                hiResource.setVisible(Boolean.valueOf(isVisible));
            }

            if (StringUtils.isNotEmpty(getIsPublic())) {
                if (Boolean.valueOf(getIsPublic())) {
                    hiResourceMetadata.setCreatedBy(null);
                } else {
                    hiResourceMetadata.setCreatedBy(hiResource.getCreatedBy());
                }
            }

            try {
                hiResourceMetadata.setLastUpdatedTime(hiResource.getLastUpdatedTime());
                metadataResourceServiceDB.editHIResourceMetadata(hiResourceMetadata);
                hiResourceServiceDB.editHIResource(hiResource);
                setMessage("Rename is successful");
                return true;
            } catch (Exception e) {
                logger.error(e.getMessage());
                setMessage(e.getMessage());
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
}
