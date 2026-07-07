package com.helicalinsight.datasource.managed.jaxb;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by author on 09/09/2018
 *
 * @author Rajesh
 */
@Component
@Scope("prototype")

@XmlRootElement(name = "drillStorageLocation")
@XmlAccessorType(XmlAccessType.FIELD)
public class DrillStorageLocations {

    @XmlElement(name = "storage")
    List<StorageLocation> storageDetails;

    public List<StorageLocation> getStorageDetails() {
        return storageDetails;
    }

    public void setStorageDetails(List<StorageLocation> storageDetails) {
        this.storageDetails = storageDetails;
    }

    @Override
    public String toString() {
        return "DrillStorageLocations [storageDetails=" + storageDetails + "]";
    }


}
