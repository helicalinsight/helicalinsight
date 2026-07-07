package com.helicalinsight.adhoc.dao;

import com.helicalinsight.admin.model.*;

import java.util.List;
/**
 * This interface defines methods for interacting with the database to perform CRUD operations related to cubes, 
 * cube dimensions, measures, and hierarchies. Implementations of this interface are responsible for providing 
 * concrete implementations of these methods for specific database systems.
 */
public interface HICubeDAO {
	/**
     * Adds a new cube to the database.
     * @param cube The cube object to be added.
     */
    void addCube(HIMetadataCube cube);
    /**
     * Generic method to add an object to the database.
     * @param obj The object to be added.
     */
    void add(Object obj);
    /**
     * Generic method to edit/update an object in the database.
     * @param obj The object to be edited.
     */
    void edit(Object obj);
    /**
     * Finds a cube by its metadata ID, cube ID, and UUID.
     * 
     * @param metadataId 		 metadata ID.
     * @param cubeId             cube ID.
     * @param uuid 				 UUID.
     * @return The found HIMetadataCube object.
     */
    HIMetadataCube findCube(Integer metadataId, Integer cubeId, String uuid);
    /**
     * Finds a cube by using  resource ID.
     * 
     * @param resourceId          resource ID.
     * @return The found HIMetadataCube object.
     */
    HIMetadataCube findCubeByResourceId(Integer resourceId);
    /**
     * Finds a cube dimension by its cube ID and dimension ID.
     * 
     * @param cubeId         cube ID.
     * @param id             dimension ID.
     * @return The found HICubeDimension object.
     */
    HICubeDimension findHICubeDimension(String cubeId, String id);
    /**
     * Finds a dimension hierarchy by its ID and hierarchyId ID.
     * 
     * @param id              Dimension ID 
     * @param id1             hierarchyId ID 
     * @return The found HIDimensionHierarchy object.
     */
    HIDimensionHierarchy findDimensionHierarchy(Integer id, String id1);
    /**
     * Retrieves all cubes associated with a given resource ID.
     * 
     * @param resourceId 				 resource ID.
     * @return A list of all HIMetadataCube objects associated with the resource ID.
     */
    List<HIMetadataCube> findAllCubeWithResourceId(Integer resourceId);
    /**
     * Finds a cube measure by its cube ID and measure ID.
     * 
     * @param cubeId 			 cube ID.
     * @param measureId 		 measure ID.
     * @return the HICubeMeasure object.
     */
    HICubeMeasure findCubeMeasure(Integer cubeId, String measureId);
    /**
     * Finds a cube hierarchy level by its hierarchy ID and level ID.
     * 
     * @param hiecId 			 hierarchy ID.
     * @param levelId 			 level ID.
     * @return The found HICubeHierarchyLevel object.
     */
    HICubeHierarchyLevel findHICubeHierarchyLevel(Integer hiecId, String levelId);
    /**
     * Deletes an object from the database.
     * @param anyObject The object to be deleted.
     */
    void delete(Object anyObject);
    /**
     * Retrieves all cube dimensions associated with a given cube ID.
     * @param cubeId           cube ID.
     * @return A list of all HICubeDimension objects associated with the cube ID.
     */
    List<HICubeDimension> findAllCubeDimentions(Integer cubeId);
    /**
     * Retrieves all Dimension Hierarchy object .
     * @param dimId             dimension id.
     * @return A list of all HIDimensionHierarchy objects.
     */
    List<HIDimensionHierarchy> findAllDimHierarchy(Integer dimId);
    /**
     * Retrieves all hierarchy levels associated with a given hierarchy ID.
     * 
     * @param hirId          hierarchy ID.
     * @return A list of all HICubeHierarchyLevel objects associated with the hierarchy ID.
     */
    List<HICubeHierarchyLevel> findAllHierarchyLevels(Integer hirId);
    /**
     * Retrieves all cube measures associated with a given cube ID.
     * 
     * @param cubeId          cube ID.
     * @return A list of all HICubeMeasure objects associated with the cube ID.
     */
    List<HICubeMeasure> findAllCubeMeasuresByCubeId(Integer cubeId);
}
