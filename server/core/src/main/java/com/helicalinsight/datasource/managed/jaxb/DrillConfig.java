package com.helicalinsight.datasource.managed.jaxb;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

import java.util.List;

import javax.xml.bind.annotation.*;

/**
 * Created by author on 09/09/2018
 *
 * @author Rajesh
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "drill")
@XmlAccessorType(XmlAccessType.FIELD)
public class DrillConfig {

    @XmlAttribute(name = "managerClass")
    private String managerClass;

    @XmlElement(name = "enabled")
    private String enabled;
    @XmlElement(name = "storageImpl")
    private String storageImpl;
    @XmlAnyElement(lax = true)
    private List<Element> enabledTypes;
    @XmlElement(name = "url")
    private String url;
    @XmlElement
    private EndPointsDetails endPointsDetails;

    // @XmlElement(name = "enabledTypes")
    // private String enabledTypes;
    @XmlElement
    private UrlConfig urlConfig;
    @XmlElement(name = "drillStorageLocation")
    private DrillStorageLocations drillStorageLocations;

    public String getStorageImpl() {
        return storageImpl;
    }

    public void setStorageImpl(String storageImpl) {
        this.storageImpl = storageImpl;
    }

    public List<Element> getEnabledTypes() {
        return enabledTypes;
    }

    public void setEnabledTypes(List<Element> enabledTypes) {
        this.enabledTypes = enabledTypes;
    }

    @Override
    public String toString() {
        return "DrillConfig{" +
                "managerClass='" + managerClass + '\'' +
                ", enabled='" + enabled + '\'' +
                ", storageImpl='" + storageImpl + '\'' +
                ", enabledTypes=" + enabledTypes +
                ", url='" + url + '\'' +
                ", endPointsDetails=" + endPointsDetails +
                ", urlConfig=" + urlConfig +
                ", drillStorageLocations=" + drillStorageLocations +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DrillConfig that = (DrillConfig) o;

        if (drillStorageLocations != null ? !drillStorageLocations.equals(that.drillStorageLocations) : that.drillStorageLocations != null)
            return false;
        if (enabled != null ? !enabled.equals(that.enabled) : that.enabled != null) return false;
        if (enabledTypes != null ? !enabledTypes.equals(that.enabledTypes) : that.enabledTypes != null) return false;
        if (endPointsDetails != null ? !endPointsDetails.equals(that.endPointsDetails) : that.endPointsDetails != null)
            return false;
        if (managerClass != null ? !managerClass.equals(that.managerClass) : that.managerClass != null) return false;
        if (storageImpl != null ? !storageImpl.equals(that.storageImpl) : that.storageImpl != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        if (urlConfig != null ? !urlConfig.equals(that.urlConfig) : that.urlConfig != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = managerClass != null ? managerClass.hashCode() : 0;
        result = 31 * result + (enabled != null ? enabled.hashCode() : 0);
        result = 31 * result + (storageImpl != null ? storageImpl.hashCode() : 0);
        result = 31 * result + (enabledTypes != null ? enabledTypes.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (endPointsDetails != null ? endPointsDetails.hashCode() : 0);
        result = 31 * result + (urlConfig != null ? urlConfig.hashCode() : 0);
        result = 31 * result + (drillStorageLocations != null ? drillStorageLocations.hashCode() : 0);
        return result;
    }

    public String getUrl() {

        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getManagerClass() {
        return managerClass;
    }

    public void setManagerClass(String managerClass) {
        this.managerClass = managerClass;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public EndPointsDetails getEndPointsDetails() {
        return endPointsDetails;
    }

    public void setEndPointsDetails(EndPointsDetails endPointsDetails) {
        this.endPointsDetails = endPointsDetails;
    }

    public DrillStorageLocations getDrillStorageLocations() {
        return drillStorageLocations;
    }

    public void setDrillStorageLocations(DrillStorageLocations drillStorageLocations) {
        this.drillStorageLocations = drillStorageLocations;
    }

    public UrlConfig getUrlConfig() {

        return urlConfig;
    }

    public void setUrlConfig(UrlConfig urlConfig) {
        this.urlConfig = urlConfig;
    }
}
