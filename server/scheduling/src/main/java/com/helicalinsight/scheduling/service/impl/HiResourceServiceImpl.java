package com.helicalinsight.scheduling.service.impl;

import com.helicalinsight.admin.model.ResourceType;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.scheduling.dao.HiResourceDao;
import com.helicalinsight.scheduling.model.HiResource;
import com.helicalinsight.scheduling.model.Schedules;
import com.helicalinsight.scheduling.service.HiResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;

/**
 * HiResourceServiceImpl class performs crud operation on {@link HiResource} entity
 * which implements {@link HiResourceService} interface.
 * Created by author on 3/16/2020.
 * @author Rajesh
 */
@Service
public class HiResourceServiceImpl implements HiResourceService {
    @Autowired
    private HiResourceDao hiResourceDao;

    @Transactional
    @Override
    public Long addHiResource(HiResource hiResource) {
        return hiResourceDao.addHiResource(hiResource);
    }

    @Transactional
    @Override
    public void editHiResource(HiResource hiResource) {
        hiResourceDao.editHiResource(hiResource);
    }

    @Transactional
    @Override
    public void deleteHiResource(Long hiResourceId) {
        hiResourceDao.deleteHiResource(hiResourceId);
    }

    @Transactional
    @Override
    public HiResource getHiResource(Long hiResourceId) {
        return hiResourceDao.getHiResource(hiResourceId);
    }

    @Transactional
    @Override
    public HiResource getHiResourceByPath(String path, Long parentId) {
        return hiResourceDao.getHiResourceByPath(path, parentId);
    }

    @Transactional
    @Override
    public HiResource findUniqueHiResource(HiResource sampleHiResource) {
        return hiResourceDao.findUniqueHiResource(sampleHiResource);
    }

    @Transactional
    @Override
    public void deleteAllHiResource() {
        hiResourceDao.deleteAllHiResource();
    }

    @Transactional
    @Override
    public ResourceType getResourceTypeById(Long hiResourceId) {
        return hiResourceDao.getResourceTypeById(hiResourceId);
    }

    @Transactional
    @Override
    public User getUserById(Long hiResourceId) {
        return hiResourceDao.getUserById(hiResourceId);
    }

    @Transactional
    @Override
    public List<Schedules> getAllSchedulesById(Long hiResourceId) {
        return hiResourceDao.getAllSchedulesById(hiResourceId);
    }

    @Transactional
    @Override
    public void deleteAllMigratedEntries() {
        hiResourceDao.deleteAllMigratedEntries();
    }
    @Transactional
	@Override
	public HiResource getHiResourceByPath(String path) {
		return hiResourceDao.getHiResourceByPath(path);
	}
}
