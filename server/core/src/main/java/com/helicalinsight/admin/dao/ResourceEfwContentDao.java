package com.helicalinsight.admin.dao;

import java.util.List;

import com.helicalinsight.admin.model.ResourceEfwContents;

public interface ResourceEfwContentDao {
	List<ResourceEfwContents> fetchResourceEfwContentByResourceId(Integer resourceId);
	void deleteResourceEwfContentByResourceId(String fileName,Integer resourceId);
	Integer addHIResourceEfwContent(ResourceEfwContents resourceEfwContents);
	ResourceEfwContents fetchEfwContentByResourceIdAndFileName(Integer resourceId, String fileName);
	List<ResourceEfwContents> findAllImageResources();
}
