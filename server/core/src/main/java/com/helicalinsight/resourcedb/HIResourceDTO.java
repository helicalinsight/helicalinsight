package com.helicalinsight.resourcedb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.helicalinsight.resourcedb.processor.model.ResourceSecurity;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HIResourceDTO {
    private Integer resourceId;
//    @JsonInclude(value = JsonInclude.Include.CUSTOM,valueFilter = DTOFilter.class)
    private Integer parentId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String path;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String resourceURL;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String extension;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String permissionLevel;
//    @JsonInclude(value = JsonInclude.Include.CUSTOM,valueFilter = DTOFilter.class)
    private Boolean isVisible;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String name;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String resourcePath;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private long lastModified;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String type;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String title;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String,Object> options=null;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String inherit;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<HIResourceDTO> children;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ResourceSecurity resourceSecurity;
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY, value = "isDeleted")
    private Boolean deleted;
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String titleForRecycleBin;

    private List<Integer> globalIds;
    private List<Integer> efwdIds;

    public List<Integer> getEfwdIds() {
        return efwdIds;
    }

    public void setEfwdIds(List<Integer> efwdIds) {
        this.efwdIds = efwdIds;
    }

    @JsonIgnore
    private Integer createdBy;

    public Boolean getPublicFolder() {
        return publicFolder;
    }

    public void setPublicFolder(Boolean publicFolder) {
        this.publicFolder = publicFolder;
    }

    @JsonProperty("public")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean publicFolder;

    public HIResourceDTO(){
            this.options=new HashMap<>();
    }

    public HIResourceDTO(Integer resourceId, Integer parentId, String path, String extension, String permissionLevel, Boolean isVisible, String name, String description, long lastModified, String type, String title, String inherit, List<HIResourceDTO> children) {
        this.resourceId = resourceId;
        this.parentId = parentId;
        this.path = path;
        this.extension = extension;
        this.permissionLevel = permissionLevel;
        this.isVisible = isVisible;
        this.name = name;
        this.description = description;
        this.lastModified = lastModified;
        this.type = type;
        this.title = title;
        this.inherit = inherit;
        this.children = children;
    }
    

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(String permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    @JsonInclude(value = JsonInclude.Include.CUSTOM,valueFilter = DTOFilter.class)
    public Boolean isVisible() {
        return isVisible;
    }

    public void setVisible(Boolean visible) {
        isVisible = visible;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public List<Integer> getGlobalIds() {
        return globalIds;
    }

    public void setGlobalIds(List<Integer> globalIds) {
        this.globalIds = globalIds;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, Object> getOptions() {
        if(type.equals("folder")){
            this.options.put("selectable","true");
        }
        return options;
    }

    public String getInherit() {
        return inherit;
    }

    public void setInherit(String inherit) {
        this.inherit = inherit;
    }

    public List<HIResourceDTO> getChildren() {
        return children;
    }

    public void setChildren(List<HIResourceDTO> children) {
        this.children = children;
    }

    public ResourceSecurity getResourceSecurity() {
        return resourceSecurity;
    }

    public void setResourceSecurity(ResourceSecurity resourceSecurity) {
        this.resourceSecurity = resourceSecurity;
    }

    @Override
    public String toString() {
        return "HIResourceDTO{" +
                "resourceId=" + resourceId +
                ", parentId=" + parentId +
                ", path='" + path + '\'' +
                ", extension='" + extension + '\'' +
                ", permissionLevel=" + permissionLevel +
                ", isVisible=" + isVisible +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", lastModified=" + lastModified +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", options=" + options +
                ", inherit=" + inherit +
                ", children=" + children +
                '}';
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy=createdBy;

    }

    public Integer getCreatedBy() {
        return createdBy;
    }

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public String getTitleForRecycleBin() {
		return titleForRecycleBin;
	}

	public void setTitleForRecycleBin(String titleForRecycleBin) {
		this.titleForRecycleBin = titleForRecycleBin;
	}

	public String getResourceURL() {
		return resourceURL;
	}

	public void setResourceURL(String resourceURL) {
		this.resourceURL = resourceURL;
	}

	public String getResourcePath() {
		return resourcePath;
	}

	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}
}
