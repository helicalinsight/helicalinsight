package com.helicalinsight.scheduling.dao;

import com.helicalinsight.admin.model.ResourceType;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.scheduling.model.HiResource;
import com.helicalinsight.scheduling.model.Schedules;

import java.util.List;

/**
 * Created by author on 3/13/2020.
 *
 * @author Rajesh
 */
public interface HiResourceDao {

    Long addHiResource(HiResource hiResource);

    void editHiResource(HiResource hiResource);

    void deleteHiResource(Long hiResourceId);

    HiResource getHiResource(Long hiResourceId);

    HiResource findUniqueHiResource(HiResource sampleHiResource);

    void deleteAllHiResource();

    ResourceType getResourceTypeById(Long hiResourceId);

    User getUserById(Long hiResourceId);

    List<Schedules> getAllSchedulesById(Long hiResourceId);

    HiResource getHiResourceByPath(String path,Long parentId);

    void deleteAllMigratedEntries();

	HiResource getHiResourceByPath(String path);
}
