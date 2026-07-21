package com.helicalinsight.admin.dao;

import com.helicalinsight.admin.model.*;

import java.util.List;
import java.util.Map;

public interface HIResourceDBDAO {

    Integer addHIResource(HIResource hiResource);

    Integer editHIResource(HIResource hiResource);

    void deleteHIResource(Integer resourceId,List<Integer> resourceIds,Boolean isFolder);
    
    void deleteHIResource(HIResource hiResource);

    Integer addHIResourceSecurity(HIResourceSecurityDB hiResourceSecurityDB);

    Integer editHIResourceSecurity(HIResourceSecurityDB hiResourceSecurityDB);

    void deleteHIResourceSecurity(Integer resourceId);

    HIResource getHIResourceById(Integer resourceId,boolean applyFilter);
    HIResource findResourceById(Integer resourceId,boolean applyFilter);

    List<HIResource> getAllHIResources();

    List<HIResourceSecurityDB> getHIResourceSecurityByResourceId(Integer resourceId);

    Integer addHIResourceFolder(HIResourceFolder hiResourceFolder);

    Integer editHIResourceFolder(HIResourceFolder hiResourceFolder);

    void deleteHIResourceFolder(Integer id);

    Integer addHIResourceEFW(HIResourceEFW hiResourceEFW);

    Integer editHIResourceEFW(HIResourceEFW hiResourceEFW);

    void deleteHIResourceEFW(Integer id);

    Integer addHIResourceEFWDD(HIResourceEFWDD hiResourceEFWDD);

    Integer editHIResourceEFWDD(HIResourceEFWDD hiResourceEFWDD);

    void deleteHIResourceEFWDD(Integer id);

    Integer addHIResourceEFWCE(HIResourceEFWCE hiResourceEFWCE);

    Integer editHIResourceEFWCE(HIResourceEFWCE hiResourceEFWCE);

    void deleteHIResourceEFWCE(Integer id);

    Integer addHIResourceEFWSR(HIResourceEFWSR hiResourceEFWSR);

    Integer editHIResourceEFWSR(HIResourceEFWSR hiResourceEFWSR);

    void deleteHIResourceEFWSR(Integer id);

    Integer addHIResourceEFWD(HIEFWD hiResourceEFWD);

    Integer editHIResourceEFWD(HIEFWD hiResourceEFWD);

    void deleteHIResourceEFWD(Integer id);

    HIEFWD getHIResourceEFWDByResourceId(Integer resourceId);

    HIResourceEFWDD getHIResourceEFWDDByResourceId(Integer resourceId);

    HIResourceEFWCE getHIResourceEFWCEByResourceId(Integer resourceId);

    HIResourceEFW getHIResourceEFWByResourceId(Integer resourceId);

    HIResourceResult getHIResourceResultByResourceId(Integer resourceId);

    HIResourceFolder getHIResourceFolderByResourceId(Integer resourceId);

    /*HIResourceHCR getHIResourceHCRByResultId(Integer resourceId);*/

    HIResourceHReport getHIResourceHReportByResultId(Integer resourceId);


    Integer addHIResourceHwf(HIResourceHWF hiResourceHWF);

    Integer editHIResourceHwf(HIResourceHWF hiResourceHWF);

    void deleteHIResourceHwfById(Integer id);

    HIResourceHWF getHIResourceByResourceId(Integer resourceId);

    Integer addHIHwfExecution(HWFExecution hiHwfExecution);

    Integer editHIHwfExecution(HWFExecution hiHwfExecution);

    void deleteHIHwfExecution(Integer id);

    HWFExecution getHIHwfExecutionByResourceId(Integer id);

    Integer addHIHwfInput(HIHwfInput hiHwfInput);
    
    Map<Integer, Integer> getSecurityMapOfAnyUser(Integer userId, List<String> roleId, Integer orgId);
    
    Integer editHIHwfInput(HIHwfInput hiHwfInput);

    void deleteHIHwfInput(Integer id);

    HIHwfInput getHIHwfInput(Integer id);

    Integer addHIHwfOutput(HIHwfOutput hiHwfOutput);

    Integer editHIHwfOutput(HIHwfOutput hiHwfOutput);

    void deleteHIHwfOutput(Integer id);

    HIHwfOutput getHIHwfOutput(Integer id);

    Integer addHwfExecutionInput(HWFExecutionInput hwfExecutionInput);

    Integer addHwfExecutionOutput(HWFExecutionOutput hwfExecutionOutput);

    Integer editHwfExecutionInput(HWFExecutionInput hwfExecutionInput);

    Integer editHwfExecutionOutput(HWFExecutionOutput hwfExecutionOutput);

    void deleteHwfExecutionInput(Integer id);

    HWFExecutionInput getHwfExecutionInput(Integer id);

    void deleteHwfExecutionOutput(Integer id);

    HWFExecutionOutput getHwfExecutionOutput(Integer id);

    HIResource getResourceByPath(String path,Integer createdBy);

    HIResource getHIResourceFolderByName(String name);

    void deleteHIResourceById(Integer id);

    void cleanUp();

    Integer addHIUrlMapping(HIUrlMapping hiUrlMapping);

    Integer editHIUrlMapping(HIUrlMapping hiUrlMapping);

    void deleteHIUrlMapping(Integer id);

    HIUrlMapping getHIUrlMapping();

    List<HIUrlMapping> getUrlMapping(String url);

    HIUrlMapping getHIUrlMappingByResourceId(Integer resourceId);

    HIResource getIsFolderByResourceId(Integer id);

    HIUrlMapping getUrlMap(String url);

    Long countResourceByUrl(String url,Integer createdBy);

    HIResource getResourceByUrl(String url,boolean applyFilter);

    HIResource getResourceByFolderName(String folderName);

    List<HIResource> getResourceListByUrl(String url);

    void deleteResource(Integer resourceId,List<Integer> resourceIds,Boolean isFolder);

    Boolean isTableEmpty();

    HIResource getResourceByParent(Integer parentId);


    List<HIResourceSecurityDB> getHIResourceSecurityByCreatedBy(Integer createdBy);

    Long countResourceByCreatedBy(Integer createdBy);

    List<HIResource> getHIResourceByCreatedBy(Integer createdBy, boolean includeFilter);

    List<HIResource> getHIResourceByPath(String path);

    List<HIResource> getResourceByParentId(Integer parentId,Object searchParam);

    HIResource getResourceByPath(String path);

    /**
     * This method has been deprecated. Use {@link #getHIResourceIdsOfActiveUser()} and {@link #getResourceByResourceIds()}  together.
     * 
     * @param includeDeletedFilter
     * @return resourceList
     */
    @Deprecated (forRemoval =  true)
    List<HIResource> getAllResourceList(boolean includeDeletedFilter);

    Map<Integer,Integer> getSecurityMap();

    void deleteHIResourceSecurityByResourceId(int resourceId, Integer userId, Integer orgId, Integer roleId);

    void updateOrInsert(int resourceId, Integer userId, Integer orgId, Integer roleId, Integer permission, String createdBy);

    Long countResourceByFolderName(String folderName,Integer userId);

    List<HIResourceSecurityDB> getAllResourceSecurity();


    List<HIResource> getAllResourceListWithExtension(List<Long> resourceIds);

    Integer addHIResourceEFWContents(ResourceEfwContents resourceEfwContents);

    Boolean editHIResourceEFWContents(ResourceEfwContents resourceEfwContents);

    Boolean deleteHIResourceEFWContents(Integer contentId);

    ResourceEfwContents getHIResourceEFWContents(Integer contentId);

    HIResourceMetadata getMetadataByResourceId(Integer resourceId);

    HIResource getResourceIdFromTitleAndURL(String dir, String title);

    Integer addHIResourceEFWVF(HIResourceEFWVF hiResourceEFWVF);

    Integer editHIResourceEFWVF(HIResourceEFWVF hiResourceEFWVF);

    void deleteHIResourceEFWVF(Integer id);

    HIResourceEFWVF getHIResourceEFWVFById(Integer resourceId);
    void editHIReport(HIResourceHReport hiResourceHReport);

    void editHIResourceHCR(HIResourceHCR hiResourceHCR);
    
    Boolean deleteDatasoureRelatedResources(Integer globalId);

	ResourceEfwContents getHIResourceEFWContents(String fileName);

	HIResource getDeletedResourceById(Integer id);

	boolean hardDelete(HIResource hiResource);
	List<Integer> findMetadataResourcesByGlobalConnectionId(int globalId);


    HIResourceEFWSR getHIResourceEFWSRByResourcesId(Integer resourceId);

    List<HIResourceEFWSR> getAllHIResourceEFWSRByResourcesIds(List<Integer> resourceIds);

    List<HIResource> getAllConnectedResource(Integer resourceId);

	Integer getEfwContentIdByResourceId(Integer resourceId);

	void hardDeleteFromHiResourceEfw(Integer id);
	
	Long getHiRecycleBinIdOfSoftDeletedItemByHiResourceId(Integer hiResourceId);

	List<HIResource> findAllReportsByMetadataResourceId(Integer resourceId);
	List<HIResource> findAllEfwsrFilesByReportDirAndFile(String directory, String reportFile);
	void hardDeleteFromHiResourceEfwByResourceId(Integer resourceId);

    HIResourceSecurityDB  fetchPermissionBySharedUserIdAndResourceIdTest(Integer userId, Integer resourceId);

    HIResourceSecurityDB fetchPermissionBySharedUserIdAndResourceId(Integer userId, Integer resourceId);
	List<HIEFWD> getHIResourceEFWDByParentResourceId(Integer resourceId);
	List<String> fetchGeneratedUrlsFromSpecifiedPath(String url,Long resourceTypeId,Integer parentId,Boolean isForUrlGen);
	HIResource fetchResourceBasedOnTitleAndParentId(String title, Integer parentId);
	Map<Integer,Boolean> fetchAllParentResourceIdsForAUser(Integer userId);
	String getUniqueUrl(String resourceUrl, ResourceType resourceType);


    List<HIResource> fetchResourceIdsBasedOnConTypeAndConId(String conType,Integer conId);

	List<HIResource> findAllResources(String url);
	
    List<Integer> getHIResourceIdsOfActiveUser();
    HIResource findResourceByUrl(String url);
    List<Integer> getChildrenResourceByParentIds(List<Integer> parentIds);
	Map<Integer, List<HIResource>> findAllReportsByMetadataResourceIds(List<Integer> metadataResourceIds);
	Map<Integer, List<HIResource>> findAllInstantReportsByModelResourceIds(List<Integer> modelResourceIds);
	List<HIResource> getHIResourcesByIds(List<Integer> resourceIds, Boolean applyFilter);
	
	void restoreResourcesByIds(List<Integer> resourceIds);
    
}
