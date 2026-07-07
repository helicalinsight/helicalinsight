package com.helicalinsight.admin.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.helicalinsight.admin.dao.HIRecycleBinDao;
import com.helicalinsight.admin.dao.HIResourceMappingDao;
import com.helicalinsight.admin.dto.RecycleBinDTO;
import com.helicalinsight.admin.model.*;
import com.helicalinsight.admin.service.HIRecycleBinService;
import com.helicalinsight.admin.service.ResourceTypeServiceDB;
import com.helicalinsight.admin.utils.ResourceDTOMapper;
import com.helicalinsight.core.request.RecycleBinItem;
import com.helicalinsight.efw.exceptions.EfwServiceException;

@Service
public class HIRecycleBinServiceImpl implements HIRecycleBinService {

	@Autowired
	private HIRecycleBinDao hiRecycleBinDao;

	@Autowired
	private HIResourceMappingDao resourceDao;
	
	@Autowired
	private ResourceTypeServiceDB resourceTypeService;
	
	@Autowired
	private ResourceDTOMapper mapper;
	
	@Transactional
	@Override
	public boolean save(HIRecycleBin recycleBin) {
		return hiRecycleBinDao.save(recycleBin);
	}

	@Transactional
	@Override
	public boolean delete(Long id) {
		HIRecycleBin hiRecycleBinById = hiRecycleBinDao.findHIRecycleBinByIdPlain(id);
		return delete(hiRecycleBinById);
	}
	
	@Transactional
	@Override
	public boolean delete(HIRecycleBin recyclebin) {
		deleteRelatedResources(recyclebin);
		return hiRecycleBinDao.delete(recyclebin);
	}
	
	private final void deleteRelatedResources(HIRecycleBin bin) {
		try {
			if(bin!=null){
				HIRecycleBinHIResourceDB hiRecycleBinHIResourceDB = bin.getHiRecycleBinHIResourceDB();

				if(hiRecycleBinHIResourceDB!=null) {
					HIResource hiResource = hiRecycleBinHIResourceDB.getHiResource();
					String extension = hiResource.getResourceType().getExtension().replace(".", "");
					if(extension.equals("efwdd")|| extension.equals("hcr"))
						resourceDao.deleteChildrenByParentId(Integer.valueOf("" + hiResource.getResourceId()));
				}
			}

		} catch (Exception e){
			e.printStackTrace();
		}
	}

	@Transactional
	@Override
	public List<RecycleBinItem> list() {
		return hiRecycleBinDao.list();
	}
	@Transactional
	@Override
	public HIRecycleBin findHIRecycleBinByResourceId(Integer resourceId) {
		return hiRecycleBinDao.findHIRecycleBinByResourceId(resourceId);
	}
	
	@Transactional(readOnly = true)
	@Override
	public Map<Integer, RecycleBinDTO> findHIRecycleBinsByResourceIds(List<Integer> resourceIds) {
		return hiRecycleBinDao.findHIRecycleBinsByResourceIds(resourceIds);
	}

	@Transactional
	@Override
	public boolean deleteHIRecycleBinByResourceId(Integer resourceId) {
		return hiRecycleBinDao.deleteHIRecycleBinByResourceId(resourceId);
	}

	@Transactional
	@Override
	public HIRecycleBin findHIRecycleBinById(Long id) {
		return hiRecycleBinDao.findHIRecycleBinById(id);
	}
	
	@Transactional
	@Override
	public RecycleBinDTO  getHIRecycleBinById(Long id) {
		return hiRecycleBinDao.getHIRecycleBinById(id);
	}

	@Transactional
	@Override
	public List<HIRecycleBin> findAll() {
		return hiRecycleBinDao.findAll();
	}
	
	@Transactional
	@Override
	public Map<String,List<Object>> findAllResourceOfRecycleBinItem(Long recycleBinId) {
		return hiRecycleBinDao.findAllResourceOfRecycleBinItem(recycleBinId);
	}

	@Transactional
	@Override
	public <T> Map<String,List<T>> prepareHIResources(HIResource resource,
													  boolean includeParent, Integer userId) {
		return hiRecycleBinDao.prepareHIResources(resource, includeParent,userId);
	}

	@Transactional
	@Override
	public HIRecycleBin findHIRecycleBinByGlobalId(Integer globalId) {
		return hiRecycleBinDao.findHIRecycleBinByGlobalId(globalId)
				.orElseThrow(() -> new EfwServiceException("BinItem not found for the provided Global connection."));
	}

	@Transactional
	@Override
	public HIRecycleBin findHIRecycleBinByEFWDId(Integer efwdId) {
		return hiRecycleBinDao.findHIRecycleBinByEFWDId(efwdId)
				.orElseThrow(() -> new EfwServiceException("BinItem not found for the provided EFWD connection."));
	}

	@Transactional
	@Override
	public HIRecycleBin findHIRecycleBinByUserId(int userId) {
		return hiRecycleBinDao.findHIRecycleBinBYUserId(userId).orElseThrow(() -> new EfwServiceException("BinItem not found for the provided user id"));
	}

	@Transactional
	@Override
	public void deleteHIRecycleByEfwdId(Integer connectionId) {
		hiRecycleBinDao.deleteHIRecycleByEfwdId(connectionId);
	}

	@Transactional
	@Override
	public void deleteRecycleBinByGlobalId(Integer globalId) {
		hiRecycleBinDao.deleteRecycleBinByGlobalId(globalId);
	}

	@Transactional
	@Override
	public <T> Map<String, List<T>> getGlobalConnectionResources(Integer connectionId, Integer userId) {
		return hiRecycleBinDao.getGlobalConnectionResources(connectionId,userId);
	}

	@Transactional
	@Override
	public <T> Map<String, List<T>> getEfwdConnectionResources(Integer efwdResource, Integer userId){
		return  hiRecycleBinDao.getEfwdConnectionResources(efwdResource,userId);
	}

	@Transactional
	@Override
	public boolean isRecycleBinPresent(Long id) {
		return hiRecycleBinDao.isRecycleBinPresent(id);
	}
	
	@Transactional
	@Override
	public void deleteRecycleBinsByIds(List<RecycleBinDTO> recycleBins) {
		if (recycleBins == null || recycleBins.isEmpty()) {
			return;
		}
		for (RecycleBinDTO bin : recycleBins) {
			if (bin.getResourceTypeId() == null || bin.getResourceId() == null) {
				continue;
			}
			ResourceType resourceType = resourceTypeService.getResourceType(bin.getResourceTypeId());
			if (resourceType == null) {
				continue;
			}
			String extension = resourceType.getExtension().replace(".", "");
			if ("efwdd".equals(extension) || "hcr".equals(extension)) {
				resourceDao.deleteChildrenByParentId(bin.getResourceId());
			}
		}
		List<Long> recycleBinIds = recycleBins.stream().map(RecycleBinDTO::getRecycleBinId).toList();
		hiRecycleBinDao.deleteRecycleBinsByIds(recycleBinIds);
	}

	@Transactional(readOnly = true)
	@Override
	public List<RecycleBinDTO> getAll() {
		return hiRecycleBinDao.getAllRecycleBinDTOs();
	}
}
