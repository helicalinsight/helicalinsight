package com.helicalinsight.admin.dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.helicalinsight.admin.dto.RecycleBinDTO;
import com.helicalinsight.admin.model.HIRecycleBin;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.core.request.RecycleBinItem;

public interface HIRecycleBinDao {

	boolean save(HIRecycleBin recycleBin);

	boolean delete(Long id);
	boolean delete(HIRecycleBin recyclebin);

	List<RecycleBinItem> list();

	HIRecycleBin findHIRecycleBinByResourceId(Integer resourceId);
	Map<Integer, RecycleBinDTO> findHIRecycleBinsByResourceIds(List<Integer> resourceIds);

	boolean deleteHIRecycleBinByResourceId(Integer resourceId);
	void deleteRecycleBinsByIds(List<Long> recycleBinIds);

	HIRecycleBin findHIRecycleBinById(Long id);
	RecycleBinDTO getHIRecycleBinById(Long id);

	HIRecycleBin findHIRecycleBinByIdPlain(Long id);

	List<HIRecycleBin> findAll();
	List<RecycleBinDTO> getAllRecycleBinDTOs();

	Map<String, List<Object>> findAllResourceOfRecycleBinItem(Long recycleBinId);


	<T> Map<String, List<T>> prepareHIResources(HIResource resource,
												boolean includeParent, Integer userId);
	<T> Map<String, List<T>> getEfwdConnectionResources(Integer efwdResource, Integer userId);
	<T> Map<String, List<T>> getGlobalConnectionResources(Integer connectionId,Integer userId);

	Optional<HIRecycleBin> findHIRecycleBinByGlobalId(Integer globalId);

	Optional<HIRecycleBin> findHIRecycleBinByEFWDId(Integer efwdId);

	Optional<HIRecycleBin> findHIRecycleBinBYUserId(int userId);

	void deleteHIRecycleByEfwdId(Integer connectionId);

	void deleteRecycleBinByGlobalId(Integer globalId);

	boolean isRecycleBinPresent(Long id);


}
