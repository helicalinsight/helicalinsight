package com.helicalinsight.admin.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Helical on 5/28/2021.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GlobalConnectionDTO implements Serializable {

	private static final long serialVersionUID = 1L;


	public void setGlobalId(int globalId) {
        this.globalId = globalId;
    }

    public int getGlobalId() {
        return globalId;
    }

    private int globalId;
    private  int id;
    private String name;
    private String type;
    private String baseType;
    private String createdBy;
    private Date createdDate;
    private Date lastUpdatedTime;
    
    @JsonProperty("dsType")
    private String dsTypeClass;
    
    private Boolean isMigrated;
    private  Integer maxPermission;
    private Map<String,String> extraOptions;
    private String vendorName;


	public Boolean getMigrated() {
        return isMigrated;
    }

    public void setMigrated(Boolean migrated) {
        isMigrated = migrated;
    }

    public Integer getMaxPermission() {
        return maxPermission;
    }
    

    public void setMaxPermission(Integer maxPermission) {
        this.maxPermission = maxPermission;
    }
    
    @JsonProperty("dsTypeDTO")
    private DsTypeDTO dsType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GlobalConnectionDTO that = (GlobalConnectionDTO) o;

        if (id != that.id) return false;
        if (baseType != null ? !baseType.equals(that.baseType) : that.baseType != null) return false;
        if (createdBy != null ? !createdBy.equals(that.createdBy) : that.createdBy != null) return false;
        if (createdDate != null ? !createdDate.equals(that.createdDate) : that.createdDate != null) return false;
        if (dsType != null ? !dsType.equals(that.dsType) : that.dsType != null) return false;
        if (dsTypeClass != null ? !dsTypeClass.equals(that.dsTypeClass) : that.dsTypeClass != null) return false;
        if (isMigrated != null ? !isMigrated.equals(that.isMigrated) : that.isMigrated != null) return false;
        if (lastUpdatedTime != null ? !lastUpdatedTime.equals(that.lastUpdatedTime) : that.lastUpdatedTime != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (share != null ? !share.equals(that.share) : that.share != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (baseType != null ? baseType.hashCode() : 0);
        result = 31 * result + (createdBy != null ? createdBy.hashCode() : 0);
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        result = 31 * result + (lastUpdatedTime != null ? lastUpdatedTime.hashCode() : 0);
        result = 31 * result + (dsTypeClass != null ? dsTypeClass.hashCode() : 0);
        result = 31 * result + (isMigrated != null ? isMigrated.hashCode() : 0);
        result = 31 * result + (dsType != null ? dsType.hashCode() : 0);
        result = 31 * result + (share != null ? share.hashCode() : 0);
        return result;
    }

    public ShareDTO getShare() {
        return share;
    }

    public void setShare(ShareDTO share) {
        this.share = share;
    }

    public DsTypeDTO getDsType() {
        return dsType;
    }

    public void setDsType(DsTypeDTO dsType) {
        this.dsType = dsType;
    }

    private ShareDTO share;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBaseType() {
        return baseType;
    }

    public void setBaseType(String baseType) {
        this.baseType = baseType;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(Date lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public String getDsTypeClass() {
        return dsTypeClass;
    }

    public void setDsTypeClass(String dsTypeClass) {
        this.dsTypeClass = dsTypeClass;
    }

    public Boolean getIsMigrated() {
        return isMigrated;
    }

    public void setIsMigrated(Boolean isMigrated) {
        this.isMigrated = isMigrated;
    }

    public Map<String, String> getExtraOptions() {
		return extraOptions;
	}

	public void setExtraOptions(Map<String, String> extraOptions) {
		this.extraOptions = extraOptions;
	}
	
    public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	@Override
    public String toString() {
        return "GlobalConnectionDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", baseType='" + baseType + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdDate=" + createdDate +
                ", lastUpdatedTime=" + lastUpdatedTime +
                ", dsTypeClass='" + dsTypeClass + '\'' +
                ", isMigrated=" + isMigrated +
                '}';
    }

}
