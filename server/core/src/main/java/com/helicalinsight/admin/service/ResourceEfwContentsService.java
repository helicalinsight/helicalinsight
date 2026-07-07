package com.helicalinsight.admin.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.helicalinsight.admin.model.ResourceEfwContents;
import jakarta.transaction.Transactional;
import org.apache.commons.fileupload.disk.DiskFileItem;
public interface ResourceEfwContentsService {

	List<ResourceEfwContents> fetchResourceEfwContentByResourceId(Integer resourceId);
	void deleteResourceEwfContentByResourceId(String fileName,Integer resourceId);
    Integer addHIResourceEfwContent(String destination,Integer resourceId,DiskFileItem fileObject,
    		String type);

    @Transactional
    Integer addHIResourceEfwContent(ResourceEfwContents entity);

    ResourceEfwContents fetchEfwContentByResourceIdAndFileName(Integer resourceId, String fileName);
    File loadFile(Integer resourceId, String fileName);
    List<ResourceEfwContents> findAllImageResources();
}
