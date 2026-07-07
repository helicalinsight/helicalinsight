package com.helicalinsight.adhoc.service;

import java.util.List;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.model.HICubeDimension;
import com.helicalinsight.admin.model.HICubeHierarchyLevel;
import com.helicalinsight.admin.model.HICubeMeasure;
import com.helicalinsight.admin.model.HIDimensionHierarchy;
import com.helicalinsight.admin.model.HIMetadataCube;

/**
* The {@code HICubeDAOService} interface defines the operations for managing cubes,
* dimensions, hierarchies, and measures within the application's data model.
*/
public interface HICubeDAOService {
    public void addCube(HIMetadataCube cube);
    public void add(Object obj);
    public void edit(Object obj);
    

    HIMetadataCube findCube(Integer metadataId, Integer cubeId, String  uuid);
    HIMetadataCube findCubeByResourceId(Integer resourceId);
    
    HICubeDimension findHICubeDimension(String cubeId, String id);

    HIDimensionHierarchy findDimensionHierarchy(Integer id, String id1);

    List<HIMetadataCube> findAllCubeWithResourceId(Integer resourceId);
    JsonObject getCubeAsJsonObj(Integer resourceId);

    HICubeHierarchyLevel findHICubeHierarchyLevel(Integer hiecId, String levelId);

    HICubeMeasure findCubeMeasure(Integer cubeId, String measureId);

    void delete(Object cube);
    
    List<HICubeDimension> findAllCubeDimentions(Integer cubeId);
    List<HIDimensionHierarchy> findAllDimHierarchy(Integer dimId);
    List<HICubeHierarchyLevel> findAllHierarchyLevels(Integer hirId);
    List<HICubeMeasure> findAllCubeMeasuresByCubeId(Integer cubeId);
}
