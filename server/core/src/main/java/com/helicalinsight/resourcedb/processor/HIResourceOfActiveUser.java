package com.helicalinsight.resourcedb.processor;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceFolder;
import com.helicalinsight.admin.model.ResourceType;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.ResourcePermissionLevelsHolder;
import com.helicalinsight.resourcedb.HIResourceDTO;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.helicalinsight.efw.utility.ResourceTypeIDMap.getResourceTypExtension;
import static com.helicalinsight.efw.utility.ResourceTypeIDMap.getResourceTypName;


/**
 * The list of resources based on the active user
 * @author karthik
 */

public class HIResourceOfActiveUser {
    ResourcePermissionLevelsHolder resourcePermissionLevelsHolder = ApplicationContextAccessor.getBean(ResourcePermissionLevelsHolder.class);

    
    private Integer defaultAccessLevel;
    
    public Map<String, Object> getResourcePermission() {
        return resourcePermission;
    }


    public List<HIResourceDTO> getResourceDTOList() {
        return resourceDTOList;
    }

    public void setResourceDTOList(List<HIResourceDTO> resourceDTOList) {
        this.resourceDTOList = resourceDTOList;
    }

    private Map<Integer, Integer> securityMap;
    private List<HIResource> hiResourceList;
    private Map<String, Object> resourcePermission;
    private List<HIResourceDTO> resourceDTOList;
    private Map<Integer,List<Integer>> parentChildIds;
    private List<Integer> childIds;
    private Integer customUserId;

    public HIResourceOfActiveUser(Map<Integer, Integer> securityMap, List<HIResource> hiResourceList) {
        this(securityMap,hiResourceList,null);
    }
    
    
    
    
    public HIResourceOfActiveUser(Map<Integer,Integer> securityMap, List<HIResource> hiResourceList, Integer customUserId) {
    	this.securityMap = securityMap;
    	this.hiResourceList = hiResourceList;
    	this.customUserId = customUserId;
    	this.resourcePermission = new HashMap<>();
    	this.parentChildIds =  new HashMap<>();
    	this.childIds = new ArrayList<>();
    	this.resourceDTOList = this.prepareData();
    }
    
    public HIResourceOfActiveUser(Map<Integer,Integer> securityMap, List<HIResource> hiResourceList, Integer customUserId, Integer accessLevel) {
    	this.securityMap = securityMap;
    	this.hiResourceList = hiResourceList;
    	this.customUserId = customUserId;
    	this.resourcePermission = new HashMap<>();
    	this.parentChildIds =  new HashMap<>();
    	this.childIds = new ArrayList<>();
    	this.defaultAccessLevel = accessLevel;
    	this.resourceDTOList = this.prepareData();
    	
    }

    private List<HIResourceDTO> prepareData() {
    	if ( defaultAccessLevel  == null ) {
    		defaultAccessLevel = resourcePermissionLevelsHolder.readAccessLevel();
    	}
        Integer userId = customUserId != null ? customUserId :Integer.valueOf(AuthenticationUtils.getUserId());
        List<HIResourceDTO> hiResourceDTOList = prepareListOfResourceDTO(userId);


        Map<Integer, List<HIResourceDTO>> resourceDTOList = hiResourceDTOList.stream().collect(Collectors.groupingBy(HIResourceDTO::getParentId));

        HIResourceDTO startDTO = new HIResourceDTO();
        startDTO.setResourceId(0);
        startDTO.setType("folder");
        startDTO.setPermissionLevel(""+resourcePermissionLevelsHolder.readAccessLevel());

        prepareJSONStructure(resourceDTOList, startDTO);
        return startDTO.getChildren();
    }

    @NotNull
    private List<HIResourceDTO> prepareListOfResourceDTO(Integer userId) {
        List<HIResourceDTO> hiResourceDTOList = new ArrayList<>();
        if(hiResourceList!=null)
        for (HIResource resource : hiResourceList) {
            Boolean folder = resource.getFolder();
            HIResourceDTO dtoObject = new HIResourceDTO();
            dtoObject.setDeleted(resource.getDeleted());
            dtoObject.setCreatedBy(resource.getCreatedBy());
            dtoObject.setTitleForRecycleBin(resource.getTitle());
            dtoObject.setResourceId(resource.getResourceId());
            if (null != resource.getParentId()) {
                dtoObject.setParentId(resource.getParentId());
            } else {
                //if parent id is null, then setting the parent_id as zero
                dtoObject.setParentId(0);
                dtoObject.setType("folder");
            }
            dtoObject.setResourceId(resource.getResourceId());
            dtoObject.setPath(resource.getResourceURL());
            dtoObject.setName(resource.getTitle());
                Integer createdBy = null;
                if(null!=resource.getCreatedBy()){
                    createdBy = resource.getCreatedBy();
                }
                if (createdBy == null) {
                    if(folder) dtoObject.setPublicFolder(true);
                    dtoObject.setPermissionLevel("" + resourcePermissionLevelsHolder.publicResourceAccessLevel());
                }else if (createdBy.equals(userId)) {
                    //Full Access Permission
                    dtoObject.setPermissionLevel("" + resourcePermissionLevelsHolder.ownerAccessLevel());
                } else{
                    Integer permission = securityMap.get(resource.getResourceId());
                    if(permission!=null) dtoObject.setPermissionLevel("" + permission);
                }

            Long resourceId = resource.getResourceTypeId();
            if (folder) {
                    if(null!= resourceId){
                        String name = getResourceTypName(resourceId);
                        dtoObject.setType(name);
                    }
                } else {
                    if(createdBy==null){
                        dtoObject.setPublicFolder(true);
                    }
                    String[] pathSplit1 = resource.getResourceURL().split(Pattern.quote("/"));
                    dtoObject.setPath(resource.getResourceURL());
                    dtoObject.setResourceId(resource.getResourceId());
                    dtoObject.setVisible(resource.getVisible());
                String extension =  getResourceTypExtension(resourceId);
                dtoObject.setDescription(resource.getTitle() + extension);
                    dtoObject.setName(pathSplit1[pathSplit1.length-1]);
                    dtoObject.setTitle(resource.getTitle());
                    dtoObject.setExtension(extension.replace(".",""));
                    dtoObject.setType("file");
                }
                if(null!=resource.getHiResourceFolder()) {
                    if (null != resource.getHiResourceFolder().getLastUpdatedTime()) {
                        dtoObject.setLastModified(resource.getHiResourceFolder().getLastUpdatedTime().getTime());
                    }
                }else{
                    dtoObject.setLastModified(resource.getLastUpdatedTime().getTime());
                }
                hiResourceDTOList.add(dtoObject);
            }
        return hiResourceDTOList;
    }

    private void prepareJSONStructure(Map<Integer, List<HIResourceDTO>> dtoMap, HIResourceDTO startDTO) {
        Integer userId = Integer.valueOf(AuthenticationUtils.getUserId());
        List<HIResourceDTO> hiResourceDTO = dtoMap.get(startDTO.getResourceId());
        if (hiResourceDTO == null) {
            startDTO.setChildren(new ArrayList<>());
            return;
        } else {
            List<HIResourceDTO> toAdd = new ArrayList<>();
            for (HIResourceDTO dtoResource : hiResourceDTO) {
                if (startDTO.getResourceId() > 0) {
                    if (dtoResource.getPermissionLevel() == null || dtoResource.getPermissionLevel().equals("null")) {
                        dtoResource.setPermissionLevel(startDTO.getPermissionLevel());
                        if(!userId.equals(dtoResource.getCreatedBy())) {
                            dtoResource.setInherit("true");
                        }
                    }
                }
                if(dtoResource.getPermissionLevel()!=null){
                    if (!dtoResource.getPermissionLevel().equals("null")) {
                        if (dtoResource.getPermissionLevel() != null && Integer.valueOf(dtoResource.getPermissionLevel()) >= defaultAccessLevel ) {
                            toAdd.add(dtoResource);
                        }
                        resourcePermission.put(dtoResource.getPath(), ""+dtoResource.getPermissionLevel());
                    }
                }
            }
            startDTO.setChildren(toAdd);
        }
        for (HIResourceDTO dto : hiResourceDTO) {
            if (dto.getType().equals("folder")) {
                prepareJSONStructure(dtoMap, dto);
            }
        }
    }

    public Map<String, Object> getResourceSecurity() {
        return resourcePermission;
    }
    public List<HIResource> getHiResourceList(){
    	return hiResourceList;
    }


	public int getDefaultAccessLevel() {
		return defaultAccessLevel;
	}


	public void setDefaultAccessLevel(int defaultAccessLevel) {
		this.defaultAccessLevel = defaultAccessLevel;
	}
}
