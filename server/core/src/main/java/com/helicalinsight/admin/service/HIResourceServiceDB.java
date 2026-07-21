package com.helicalinsight.admin.service;


import com.helicalinsight.admin.model.*;
import com.helicalinsight.admin.dto.HIEfwdDTO;
import com.helicalinsight.resourcedb.processor.HIResourceOfActiveUser;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;

public interface HIResourceServiceDB {

    Integer addHIResource(HIResource hiResource);

    Integer editHIResource(HIResource hiResource);

    void deleteHIResource(Integer resourceId,List<Integer> resourceIds,Boolean isFolder);
    
    void deleteHIResource(HIResource hiResource);

    Integer addHIResourceSecurity(HIResourceSecurityDB hiResourceSecurityDB);

    Integer editHIResourceSecurity(HIResourceSecurityDB hiResourceSecurityDB);

    void deleteHIResourceSecurity(Integer resourceId);
    public  Map<Integer, Integer> getSecurityMap();

    HIResource getHIResourceById(Integer resourceId);

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

    Integer addHIResourceEFWContents(ResourceEfwContents resourceEfwContents);

    Boolean editHIResourceEFWContents(ResourceEfwContents hiResourceEFWSR);

    ResourceEfwContents getHIResourceEFWContents(Integer contentId);
    ResourceEfwContents getHIResourceEfwContents(String fileName);

    Boolean deleteHIResourceEFWContents(Integer contentId);

    Integer editHIResourceEFWCE(HIResourceEFWCE hiResourceEFWCE);

    void deleteHIResourceEFWCE(Integer id);

    Integer addHIResourceEFWSR(HIResourceEFWSR hiResourceEFWSR);

    Integer editHIResourceEFWSR(HIResourceEFWSR hiResourceEFWSR);

    void deleteHIResourceEFWSR(Integer id);

    Integer addHIResourceEFWD(HIEFWD hiResourceEFWD);

    Integer editHIResourceEFWD(HIEFWD hiResourceEFWD);

    void deleteHIResourceEFWD(Integer id);

    HIResourceOfActiveUser getAllResourcesOfAnyUsr(Integer userId, List<String> roleId, Integer orgId);
    
    HIEFWD getHIResourceEFWDByResourceId(Integer resourceId);

    HIResourceEFWDD getHIResourceEFWDDByResourceId(Integer resourceId);

    HIResourceEFWCE getHIResourceEFWCEByResourceId(Integer resourceId);

    HIResourceEFW getHIResourceEFWByResourceId(Integer resourceId);

    HIResourceResult getHIResourceResultByResourceId(Integer resourceId);

    HIResourceFolder getHIResourceFolderByResourceId(Integer resourceId);

    /*  HIResourceHCR getHIResourceHCRByResultId(Integer resourceId);*/

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

    HIResource getResourceByPath(String path, Integer createdBy);

    HIResource getHIResourceFolderByName(String name);

    void deleteHIResourceById(Integer id);

    void cleanUp();

    Integer addHIUrlMapping(HIUrlMapping hiUrlMapping);

    Integer editHIUrlMapping(HIUrlMapping hiUrlMapping);

    void deleteHIUrlMapping(Integer id);

    HIUrlMapping getHIUrlMapping();

    HIUrlMapping getHIUrlMappingByResourceId(Integer resourceId);

    List<HIUrlMapping> getUrlMapping(String url);

    HIUrlMapping getUrlMap(String url);

    HIResource getIsFolderByResourceId(Integer id);

    Long countResourceByUrl(String url, Integer createdBy);

    HIResource getResourceByUrl(String url);
    HIResource getResourceByUrl(String url, boolean applyFilter); 
    HIResource getResourceByIdIgnoreFilter(Integer id);
    HIResource getResourceByFolderName(String folderName);

    List<HIResource> getResourceListByUrl(String url);

    void deleteResource(Integer resourceId,List<Integer> resourceIds,Boolean isFolder);

    Boolean isTableEmpty();

    HIResource getResourceByParent(Integer parentId);

    /*Integer addHIResourceReport(HIResourceReport hiReport);*/

    List<HIResourceSecurityDB> getHIResourceSecurityByCreatedBy(Integer createdBy);

    Long countResourceByCreatedBy(Integer createdBy);

    List<HIResource> getHIResourceByCreatedBy(Integer createdBy);
    
    List<HIResource> getHIResourceByCreatedBy(Integer createdBy, boolean includeFilter);
    
    List<HIResource> getHIResourceByPath(String path);

    List<HIResource> getResourceByParentId(Integer parentId);
    List<HIResource> getResourceByParentId(Integer parentId,Object searchParam);
    List<HIResource> getAllConnectedResource(Integer resourceId);

    HIResource getResourceByPath(String path);

    /**
     * Returns all resources associated with the active user.
     *
     * @deprecated Use {@link #getResourceOfActiveUser()} instead.
     *   This method will be removed in a future release.
     *
     * @return list of all resources for the active user
     */
    @Deprecated(forRemoval = true)
    HIResourceOfActiveUser getAllResources();
    HIResourceOfActiveUser getResourceOfActiveUser();
    HIResourceOfActiveUser getAllResourcesWithExtensions(List<String> extension);
    HIResourceEFWSR getHIResourceEFWSRByResourcesId(Integer resourceId);
    List<HIResourceEFWSR> getAllHIResourceEFWSRByResourcesIds(List<Integer> resourceIds);

    void deleteHIResourceSecurityByResourceId(int resourceId, Integer userId, Integer orgId, Integer roleId);

    void updateOrInsert(int resourceId, Integer userId, Integer orgId, Integer roleId, Integer permission, String createdBy);

    List<HIResourceSecurityDB> getAllResourceSecurity();

    HIResource getResourceIdFromTitleAndURL(String dir,String title);

    Integer addHIResourceEFWVF(HIResourceEFWVF hiResourceEFWVF);

    Integer editHIResourceEFWVF(HIResourceEFWVF hiResourceEFWVF);

    void deleteHIResourceEFWVF(Integer id);

    HIResourceEFWVF getHIResourceEFWVFById(Integer resourceId);
    public void editHIReport(HIResourceHReport hiResourceHReport);

    void editHIResourceHCR(HIResourceHCR hiResourceHCR);
    HIResource getDeletedResourceById(Integer id);



    HIResourceOfActiveUser findAllResources();
    HIResourceOfActiveUser findAllResources(String resourceUrl);
    HIResourceOfActiveUser findAllResourcesIncludingReadOnly();
    boolean hardDelete(HIResource hiResource);
    Integer getEfwContentIdByResourceId(Integer resourceId);
    void hardDeleteFromHiResourceEfw(Integer id);
    Long getHiRecycleBinIdOfSoftDeletedItemByHiResourceId(Integer hiResourceId);
    List<HIResource> findAllReportsByMetadataResourceId(Integer resourceId);
    List<HIResource> findAllEfwsrFilesByReportDirAndFile(String directory, String reportFile);
    void hardDeleteFromHiResourceEfwByResourceId(Integer resourceId);
    HIResourceSecurityDB fetchPermissionBySharedUserIdAndResourceId(Integer userId,Integer resourceId);

    @Transactional
    HIResourceSecurityDB fetchPermissionBySharedUserIdAndResourceIdTest(Integer userId, Integer resourceId);

    List<HIEFWD> getHIResourceEFWDByParentResourceId(Integer resourceId);
    List<HIEfwdDTO> findHIResourceEFWDByParentResourceId(Integer resourceId);
    Long getCountOfSamePrefixUrlResources(String url,Long resourceTypeId,Integer parentId,Boolean isForUrlGen);
    HIResource fetchResourceBasedOnTitleAndParentId(String title,Integer parentId);
    Map<Integer,Boolean> fetchAllParentResourceIdsForAUser(Integer userId);
    HIResource findResourceById(Integer resourceId, boolean applyFilter);
    HIResource findResourceByUrl(String url);
    List<Integer> getChildrenResourceByParentIds(List<Integer> parentIds);
    List<HIResource> getHIResourcesByIds(List<Integer> resourceIds, Boolean applyFilter);
    
    Map<Integer, List<HIResource>> findAllReportsByMetadataResourceIds(List<Integer> metadataResourceIds);
    Map<Integer, List<HIResource>> findAllInstantReportsByModelResourceIds(List<Integer> modelResourceIds);
    void restoreResourcesByIds(List<Integer> resourceIds);

}
