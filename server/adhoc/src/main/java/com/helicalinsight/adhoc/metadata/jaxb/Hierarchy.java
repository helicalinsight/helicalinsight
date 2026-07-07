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
@XmlRootElement(name = "hierarchy")
@XmlAccessorType(XmlAccessType.FIELD)

public class Hierarchy {
    @XmlAttribute
    private  String name;

    @XmlAttribute
    private  String description;

    @XmlAttribute
    private String hierarchyId;

    @Override
    public String toString() {
        return "Hierarchy{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", hierarchyId='" + hierarchyId + '\'' +
                ", caption='" + caption + '\'' +
                ", visible=" + visible +
                ", primaryKeyColumnId='" + primaryKeyColumnId + '\'' +
                ", tableId='" + tableId + '\'' +
                ", levels=" + levels +
                ", closure=" + closure +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Hierarchy hierarchy = (Hierarchy) o;

        if (caption != null ? !caption.equals(hierarchy.caption) : hierarchy.caption != null) return false;
        if (closure != null ? !closure.equals(hierarchy.closure) : hierarchy.closure != null) return false;
        if (description != null ? !description.equals(hierarchy.description) : hierarchy.description != null)
            return false;
        if (hierarchyId != null ? !hierarchyId.equals(hierarchy.hierarchyId) : hierarchy.hierarchyId != null)
            return false;
        if (levels != null ? !levels.equals(hierarchy.levels) : hierarchy.levels != null) return false;
        if (name != null ? !name.equals(hierarchy.name) : hierarchy.name != null) return false;
        if (primaryKeyColumnId != null ? !primaryKeyColumnId.equals(hierarchy.primaryKeyColumnId) : hierarchy.primaryKeyColumnId != null)
            return false;
        if (tableId != null ? !tableId.equals(hierarchy.tableId) : hierarchy.tableId != null) return false;
        if (visible != null ? !visible.equals(hierarchy.visible) : hierarchy.visible != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (hierarchyId != null ? hierarchyId.hashCode() : 0);
        result = 31 * result + (caption != null ? caption.hashCode() : 0);
        result = 31 * result + (visible != null ? visible.hashCode() : 0);
        result = 31 * result + (primaryKeyColumnId != null ? primaryKeyColumnId.hashCode() : 0);
        result = 31 * result + (tableId != null ? tableId.hashCode() : 0);
        result = 31 * result + (levels != null ? levels.hashCode() : 0);
        result = 31 * result + (closure != null ? closure.hashCode() : 0);
        return result;
    }

    public String getHierarchyId() {

        return hierarchyId;
    }

    public void setHierarchyId(String hierarchyId) {
        this.hierarchyId = hierarchyId;
    }

    @XmlAttribute
    private  String caption;

    @XmlAttribute
    private Boolean visible;

    @XmlAttribute
    private String primaryKeyColumnId;

    @XmlAttribute
    private String tableId;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Levels getLevels() {
        return levels;
    }

    public void setLevels(Levels levels) {
        this.levels = levels;
    }

    public Closure getClosure() {
        return closure;
    }

    public void setClosure(Closure closure) {
        this.closure = closure;
    }

    @XmlElement

    private Levels levels;


    @XmlElement
    private Closure closure;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public String getPrimaryKeyColumnId() {
        return primaryKeyColumnId;
    }

    public void setPrimaryKeyColumnId(String primaryKeyColumnId) {
        this.primaryKeyColumnId = primaryKeyColumnId;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }
}
