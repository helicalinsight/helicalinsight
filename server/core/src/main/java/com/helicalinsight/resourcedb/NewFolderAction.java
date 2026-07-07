package com.helicalinsight.resourcedb;

import com.helicalinsight.admin.model.*;
import com.helicalinsight.admin.service.OrganizationService;
import com.helicalinsight.admin.service.ResourceTypeServiceDB;
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;
import com.helicalinsight.resourcedb.processor.DBProcessor;
import com.helicalinsight.resourcedb.processor.HIResourceOfActiveUser;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.resourcesecurity.jaxb.Security;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


import java.text.Format;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.util.concurrent.atomic.AtomicBoolean;


import static com.helicalinsight.resourcedb.ResourceConstants.*;

@Component("newFolderResourceAction")
@Scope("prototype")
public class NewFolderAction extends AbstractResourceAction {
    private static final Logger logger = LoggerFactory.getLogger(NewFolderAction.class);

    @Autowired
    OrganizationService organizationService;

    @Autowired
    ResourceTypeServiceDB resourceTypeServiceDB;

    @Autowired
    protected ResourcePermissionLevelsHolder resourcePermissionLevelsHolder;

    public Boolean performAction() {
        AtomicBoolean isSaved= new AtomicBoolean(false);
        FileOperationDTO payLoad = (FileOperationDTO) this.getPayLoad();
        List<String> sourceList = payLoad.getCreateSourceArray();
        String folderToCreate =  payLoad.getFolderName();
        String isPublic="";
        if(null!=payLoad.getIsPublic()){
            isPublic = payLoad.getIsPublic();
        }
        String isVisible="";
        if(null!=payLoad.getIsVisible()){
            isVisible= payLoad.getIsVisible();
        }

        String finalIsPublic = isPublic;
        String finalIsVisible = isVisible;

        sourceList.stream().forEach(location->{
            String folderName = folderToCreate;
            try {
                isSaved.set(createEntryInDB(location,folderName, finalIsPublic, finalIsVisible));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return isSaved.get();
    }

    private boolean createEntryInDB(String location,String folderName,String isPublic,String isVisible) throws Exception {

        if(folderName.length()<=2){
            setMessage("Please enter a valid name, minimum length 3.");
            return false;

        }



        String resourceURL="";

        Security security = SecurityUtils.securityObject();

        Integer createdBy=null;

        if(StringUtils.isNotEmpty(security.getCreatedBy())){
            createdBy=Integer.valueOf(security.getCreatedBy());
        }


        HIResourceOfActiveUser allResources = hiResourceServiceDB.getResourceOfActiveUser();
        List<HIResourceDTO> resourceDTOList = allResources.getResourceDTOList();
        DBProcessor dbProcessor = new DBProcessor();
        HIResourceDTO hiResourceDTO = new HIResourceDTO();
        HIResource hiResource = new HIResource();
        if(StringUtils.isNotEmpty(isPublic) && !isPublic.equals("true")){
            hiResource.setCreatedBy(createdBy);
        }else if(StringUtils.isEmpty(isPublic)){
            hiResource.setCreatedBy(createdBy);
        }
        hiResource.setCreated_date(new Date());
        HIUrlMapping hiUrlMapping = new HIUrlMapping();
        HIResourceSecurityDB resourceSecurity = new HIResourceSecurityDB();
        location=location.replaceAll("\\[","").replaceAll("]","").replaceAll("\"","");
        if((location.isEmpty() || location.equals("[\"\"]") || location.equals("[]"))){
            resourceURL = DBProcessor.checkAndReplaceSpecialChars(folderName).trim();
            String name = DBProcessor.checkAndReplaceSpecialChars(folderName).trim();
            hiResource.setResourceURL(resourceURL);
            hiResource.setResourcePath(name);
            hiResource.setParentId(null);
            hiResourceDTO.setPath(resourceURL);
        }else{
            Integer idFromResource = dbProcessor.findIdFromResource(resourceDTOList, location);
            if(null==idFromResource){
                throw new Exception("Invalid Path");
            }
            hiResource.setParentId(idFromResource);
            resourceURL = location+FILE_SEPERATOR+DBProcessor.checkAndReplaceSpecialChars(folderName).trim();
            hiResource.setResourceURL(resourceURL);
            hiResource.setResourcePath(DBProcessor.checkAndReplaceSpecialChars(folderName).trim());
            hiResourceDTO.setPath(resourceURL);
        }
        hiResource.setMigrated(false);
        hiResource.setCreated_date(new Date());
        hiResource.setFolder(true);

        //HIResourceFolder
        HIResourceFolder hiResourceFolder = new HIResourceFolder();
        hiResourceFolder.setTitle(folderName);
        
        hiResourceDTO.setName(folderName);
        hiResourceDTO.setChildren(new ArrayList<>());
        if(isPublic.equals("true")){
            hiResourceDTO.setPermissionLevel(""+resourcePermissionLevelsHolder.publicResourceAccessLevel());
        }else{
            hiResourceDTO.setPermissionLevel(""+resourcePermissionLevelsHolder.ownerAccessLevel());
        }


        hiResourceFolder.setCreatedDate(new Date());
        hiResource.setLastUpdatedTime(new Date());
        if(StringUtils.isNotEmpty(isPublic) && !isPublic.equals("true")){
            hiResourceFolder.setCreatedBy(createdBy);
        }else if(StringUtils.isEmpty(isPublic)){
            hiResourceFolder.setCreatedBy(createdBy);
        }

        hiResourceFolder.setLastUpdatedTime(new Date());
        hiResource.setHiResourceFolder(hiResourceFolder);
        if(StringUtils.isEmpty(isVisible)){
            hiResourceFolder.setVisible(Boolean.TRUE);
            hiResource.setVisible(Boolean.TRUE);
        }
        else if(isVisible.equals("true")){
            hiResourceFolder.setVisible(Boolean.TRUE);
            hiResource.setVisible(Boolean.TRUE);
        }else if(isVisible.equals("false")){
            hiResourceFolder.setVisible(Boolean.FALSE);
            hiResource.setVisible(Boolean.FALSE);
        }

        hiResource.setTitle(folderName);
        if(StringUtils.isNotEmpty(isPublic) && !isPublic.equals("true")){
            resourceSecurity.setCreatedBy(createdBy);
        }else if(StringUtils.isEmpty(isPublic)){
            resourceSecurity.setCreatedBy(createdBy);
        }
        String organization = security.getOrganization();
        if(StringUtils.isNotBlank(organization)){
            Organization organization1 = organizationService.getOrganization(Integer.valueOf(organization));
            resourceSecurity.setOrgId(organization1);
        }else{
            resourceSecurity.setOrgId(null);
        }




        //ResourceType
        //TODO need to check by resource name,
        //TODO need to update extension name in resource_type, should be based on tag value
        List<ResourceType> allResourceTypes = resourceTypeServiceDB.getAllResourceTypes();
        ResourceType folderType = allResourceTypes.stream().filter(rt -> rt.getName().equals(FOLDER)).findFirst().orElse(null);
        if(null!=folderType){
            hiResource.setResourceType(folderType);
        }
        hiResourceDTO.setType("folder");
        hiResourceDTO.setVisible(true);
        hiResourceDTO.setLastModified(new Date().getTime());
        try{
            logger.debug("Trying to save folder in db");
            hiUrlMapping.setResourceId(hiResource);
            resourceSecurity.setHiResource(hiResource);
           Integer add= hiResourceServiceDB.addHIResource(hiResource);
            setMessage("A new folder is created successfully");
            hiResourceDTO.setPath(hiResource.getResourceURL());
            hiResourceDTO.setResourceId(add);
            setData(hiResourceDTO);
            logger.debug("A new folder is created successfully....");
            return true;
        } catch (Exception e){
            try {
                /**
                 * Incase of same folder name creation , seconds gets appeneded with the foldername
                 */
                logger.debug("Trying to save folder in db");
                Format secondsFormat = new SimpleDateFormat("ss");
                String resourceUrl = resourceURL + "_" + secondsFormat.format(new Date()).substring(0, 2);
                String resourcePath = folderName + "_" + secondsFormat.format(new Date()).substring(0, 2);
                hiResource.setResourceURL(resourceUrl);
                hiResource.setResourcePath(resourcePath);
                hiResourceDTO.setPath(resourceUrl);
                hiResourceServiceDB.addHIResource(hiResource);
                setMessage("A new folder is created successfully");
                setData(hiResourceDTO);
                logger.debug("A new folder is created successfully....");
                return true;
            }
            catch (Exception innerException){
                String rootCauseMessage = ExceptionUtils.getRootCauseMessage(innerException);
                logger.error(rootCauseMessage);
                setMessage("There was a problem in serving the request. The cause is " + rootCauseMessage);
            }
        }
        return false;
    }

}
