package com.helicalinsight.admin.service;

import java.util.List;
import java.util.Map;

import com.helicalinsight.admin.dto.RecycleBinDTO;
import com.helicalinsight.admin.model.HIRecycleBin;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.core.request.RecycleBinItem;

public interface HIRecycleBinService {
	
	boolean save(HIRecycleBin recycleBin);
	boolean delete(Long id);
	boolean delete(HIRecycleBin bin);
	List<RecycleBinItem> list();
	
	HIRecycleBin findHIRecycleBinByResourceId(Integer resourceId);
	Map<Integer, RecycleBinDTO> findHIRecycleBinsByResourceIds(List<Integer> resourceIds);
	
	HIRecycleBin findHIRecycleBinByGlobalId(Integer globalId);
	HIRecycleBin findHIRecycleBinByEFWDId(Integer efwdId);
	HIRecycleBin findHIRecycleBinById(Long id);
	RecycleBinDTO getHIRecycleBinById(Long id);
	public <T> Map<String,List<T>> prepareHIResources(HIResource resource, boolean includeParent, Integer userId) ;
	boolean isRecycleBinPresent(Long id);
	HIRecycleBin findHIRecycleBinByUserId(int userId);
	boolean deleteHIRecycleBinByResourceId(Integer resourceId);
	void deleteRecycleBinsByIds(List<RecycleBinDTO> recycleBins);
	List<HIRecycleBin> findAll();
	List<RecycleBinDTO> getAll();
	Map<String,List<Object>> findAllResourceOfRecycleBinItem(Long recycleBinId);
	void deleteHIRecycleByEfwdId(Integer connectionId);
	void deleteRecycleBinByGlobalId(Integer globalId);
	<T> Map<String, List<T>> getGlobalConnectionResources(Integer connectionId,Integer userId);
	<T> Map<String, List<T>> getEfwdConnectionResources(Integer efwdResource, Integer userId);
}
