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
@XmlRootElement(name = "cube")
@XmlAccessorType(XmlAccessType.FIELD)
public class Cube {

    @XmlAttribute
    private String name;

    @XmlAttribute
    private String visible;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cube cube = (Cube) o;

        if (annotation != null ? !annotation.equals(cube.annotation) : cube.annotation != null) return false;
        if (cache != null ? !cache.equals(cube.cache) : cube.cache != null) return false;
        if (caption != null ? !caption.equals(cube.caption) : cube.caption != null) return false;
        if (cubeId != null ? !cubeId.equals(cube.cubeId) : cube.cubeId != null) return false;
        if (defaultMeasure != null ? !defaultMeasure.equals(cube.defaultMeasure) : cube.defaultMeasure != null)
            return false;
        if (description != null ? !description.equals(cube.description) : cube.description != null) return false;
        if (dimensions != null ? !dimensions.equals(cube.dimensions) : cube.dimensions != null) return false;
        if (enabled != null ? !enabled.equals(cube.enabled) : cube.enabled != null) return false;
        if (measures != null ? !measures.equals(cube.measures) : cube.measures != null) return false;
        if (name != null ? !name.equals(cube.name) : cube.name != null) return false;
        if (visible != null ? !visible.equals(cube.visible) : cube.visible != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (visible != null ? visible.hashCode() : 0);
        result = 31 * result + (cubeId != null ? cubeId.hashCode() : 0);
        result = 31 * result + (caption != null ? caption.hashCode() : 0);
        result = 31 * result + (defaultMeasure != null ? defaultMeasure.hashCode() : 0);
        result = 31 * result + (annotation != null ? annotation.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (cache != null ? cache.hashCode() : 0);
        result = 31 * result + (enabled != null ? enabled.hashCode() : 0);
        result = 31 * result + (dimensions != null ? dimensions.hashCode() : 0);
        result = 31 * result + (measures != null ? measures.hashCode() : 0);
        return result;
    }

    public String getCubeId() {

        return cubeId;
    }

    public void setCubeId(String cubeId) {
        this.cubeId = cubeId;
    }

    @XmlAttribute
    private String cubeId;

    //A string being displayed instead of the Dimension's name.
    @XmlAttribute
    private String caption;

    @XmlAttribute
    private String defaultMeasure;

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getDefaultMeasure() {
        return defaultMeasure;
    }

    public void setDefaultMeasure(String defaultMeasure) {
        this.defaultMeasure = defaultMeasure;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Measures getMeasures() {
        return measures;
    }

    private Annotation annotation;

    @XmlAttribute
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVisible() {
        return visible;
    }

    public Dimensions getDimensions() {
        return dimensions;
    }

    public void setDimensions(Dimensions dimensions) {
        this.dimensions = dimensions;
    }

    public void setMeasures(Measures measures) {
        this.measures = measures;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public Boolean getCache() {
        return cache;
    }

    public void setCache(Boolean cache) {
        this.cache = cache;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @XmlAttribute
    private Boolean cache;

    @XmlAttribute
    private Boolean enabled;


    @XmlElement
    private Dimensions dimensions;

    @XmlElement
    private Measures measures;


    @Override
    public String toString() {
        return "Cube{" +
                "name='" + name + '\'' +
                ", visible='" + visible + '\'' +
                ", cubeId='" + cubeId + '\'' +
                ", caption='" + caption + '\'' +
                ", defaultMeasure='" + defaultMeasure + '\'' +
                ", annotation=" + annotation +
                ", description='" + description + '\'' +
                ", cache=" + cache +
                ", enabled=" + enabled +
                ", dimensions=" + dimensions +
                ", measures=" + measures +
                '}';
    }
}
