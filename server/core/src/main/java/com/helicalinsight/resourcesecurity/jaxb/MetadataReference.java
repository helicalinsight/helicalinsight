package com.helicalinsight.resourcesecurity.jaxb;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;

/**
 * Created by author on 20-03-2015.
 *
 * @author Rajasekhar
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "metadata")
@XmlAccessorType(XmlAccessType.FIELD)
public class MetadataReference {

    @XmlElement
    private String location;

    @XmlElement
    private String metadataFileName;

    @XmlAttribute
    private Boolean isCube;

    public Boolean getCube() {
        return isCube;
    }

    public void setCube(Boolean cube) {
        isCube = cube;
    }

    public MetadataReference(String location, String metadataFileName) {
        this.location = location;
        this.metadataFileName = metadataFileName;
    }

    public MetadataReference() {
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMetadataFileName() {
        return metadataFileName;
    }

    public void setMetadataFileName(String metadataFileName) {
        this.metadataFileName = metadataFileName;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        MetadataReference metadataReference = (MetadataReference) object;

        if (location != null ? !location.equals(metadataReference.location) : metadataReference.location != null)
            return false;
        if (metadataFileName != null ? !metadataFileName.equals(metadataReference.metadataFileName) :
                metadataReference.metadataFileName != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = location != null ? location.hashCode() : 0;
        result = 31 * result + (metadataFileName != null ? metadataFileName.hashCode() : 0);
        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "MetadataReference{" +
                "directory='" + location + '\'' +
                ", fileName='" + metadataFileName + '\'' +
                '}';
    }
}
