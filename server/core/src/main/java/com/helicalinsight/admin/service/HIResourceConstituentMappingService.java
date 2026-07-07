package com.helicalinsight.admin.service;

import java.util.List;
import java.util.Map;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceMapping;

public interface HIResourceConstituentMappingService {
	List<HIResourceMapping> findByParentId(Integer parentResourceId);
	void saveBatch(List<HIResourceMapping> mappingBatch);
	void save(HIResourceMapping mapping);
	void update(HIResourceMapping mapping);
	void deleteChildrenByParentId(Integer parentResourceId);
	List<HIResourceMapping> findMappingsByParentIdAndChildType(Integer parentId, Long typeId);
	void deleteChildrenByParentIdAndType(Integer parentResourceId, Long typeId);
	List<HIResource> findChildMappingsByChildResourceId(Integer childResourceId);
	Map<Integer, List<HIResource>> findChildMappingsByChildResourceIds(List<Integer> childResourceIds);

}
