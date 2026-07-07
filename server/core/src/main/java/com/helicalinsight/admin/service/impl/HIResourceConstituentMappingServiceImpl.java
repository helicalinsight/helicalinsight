package com.helicalinsight.admin.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.helicalinsight.admin.dao.HIResourceMappingDao;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceMapping;
import com.helicalinsight.admin.service.HIResourceConstituentMappingService;

import jakarta.transaction.Transactional;

@Service
public class HIResourceConstituentMappingServiceImpl implements HIResourceConstituentMappingService {
	
	private final HIResourceMappingDao hiResourceConstituentMappingDao;
	
	
	
	public HIResourceConstituentMappingServiceImpl(HIResourceMappingDao hiResourceConstituentMappingDao) {
		this.hiResourceConstituentMappingDao = hiResourceConstituentMappingDao;
	}
	
	
	@Transactional
	@Override
	public List<HIResourceMapping> findByParentId(Integer parentResourceId) {
		return hiResourceConstituentMappingDao.findByParentId(parentResourceId);
		
	}
	
	@Transactional
	@Override
	public void saveBatch(List<HIResourceMapping> mappingBatch) {
		 hiResourceConstituentMappingDao.saveBatch(mappingBatch);
	}
	
	
	
	@Transactional
	@Override
	public void save(HIResourceMapping mapping) {
		hiResourceConstituentMappingDao.save(mapping);
	}
	
	@Transactional
	@Override
	public void update(HIResourceMapping mapping) {
		hiResourceConstituentMappingDao.update(mapping);
	}
	
	@Transactional
	@Override
	public void deleteChildrenByParentId(Integer parentResourceId) {
		hiResourceConstituentMappingDao.deleteChildrenByParentId(parentResourceId);
	}
	
	@Transactional
	@Override
	public List<HIResourceMapping> findMappingsByParentIdAndChildType(Integer parentId, Long typeId) {
		return hiResourceConstituentMappingDao.findMappingsByParentIdAndChildType(parentId, typeId);
	}
	
	@Override
	@Transactional
	public void deleteChildrenByParentIdAndType(Integer parentResourceId, Long typeId) {
		hiResourceConstituentMappingDao.deleteChildrenByParentIdAndType(parentResourceId, typeId);
	}


	@Override
	@Transactional
	public List<HIResource> findChildMappingsByChildResourceId(Integer childResourceId) {
		return hiResourceConstituentMappingDao.findChildMappingsByChildResourceId(childResourceId);
	}
	
	@Override
	@Transactional
	public Map<Integer, List<HIResource>> findChildMappingsByChildResourceIds(List<Integer> childResourceIds) {
		return hiResourceConstituentMappingDao.findChildMappingsByChildResourceIds(childResourceIds);
	}
}
