package com.helicalinsight.adhoc.metadata.jaxb;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Created by helical021 on 1/13/2020.
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "dimension")
@XmlAccessorType(XmlAccessType.FIELD)
public class Dimension {
    @XmlAttribute
    private String type;

    @XmlAttribute
    private Boolean visible;

    @XmlAttribute
    private String name;

    @Override
    public String toString() {
        return "Dimension{" +
                "type='" + type + '\'' +
                ", visible=" + visible +
                ", name='" + name + '\'' +
                ", dimId='" + dimId + '\'' +
                ", hierarchies=" + hierarchies +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Dimension dimension = (Dimension) o;

        if (dimId != null ? !dimId.equals(dimension.dimId) : dimension.dimId != null) return false;
        if (hierarchies != null ? !hierarchies.equals(dimension.hierarchies) : dimension.hierarchies != null)
            return false;
        if (name != null ? !name.equals(dimension.name) : dimension.name != null) return false;
        if (type != null ? !type.equals(dimension.type) : dimension.type != null) return false;
        if (visible != null ? !visible.equals(dimension.visible) : dimension.visible != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (visible != null ? visible.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (dimId != null ? dimId.hashCode() : 0);
        result = 31 * result + (hierarchies != null ? hierarchies.hashCode() : 0);
        return result;
    }

    public String getDimId() {

        return dimId;
    }

    public void setDimId(String dimId) {
        this.dimId = dimId;
    }

    @XmlAttribute
    private String dimId;

    public Hierarchies getHierarchies() {
        return hierarchies;
    }

    public void setHierarchies(Hierarchies hierarchies) {
        this.hierarchies = hierarchies;
    }

    @XmlElement
    private Hierarchies hierarchies;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
