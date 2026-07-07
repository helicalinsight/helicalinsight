package com.helicalinsight.adhoc.metadata.jaxb;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by author on 27-02-2015.
 *
 * @author Rajasekhar
 * @author Somen
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "column")
@XmlAccessorType(XmlAccessType.FIELD)
public class Column {

    @XmlAttribute
    private String name;

    @XmlAttribute
    private String id;
    

    @XmlAttribute
    private String aliasName;

    @XmlAttribute
    private String type;

    @XmlAttribute
    private String defaultFunction;

    @XmlAttribute
    private String originalName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Column column = (Column) o;

        if (aliasName != null ? !aliasName.equals(column.aliasName) : column.aliasName != null) return false;
        if (defaultFunction != null ? !defaultFunction.equals(column.defaultFunction) : column.defaultFunction != null)
            return false;
        if (id != null ? !id.equals(column.id) : column.id != null) return false;
        if (name != null ? !name.equals(column.name) : column.name != null) return false;
        if (originalName != null ? !originalName.equals(column.originalName) : column.originalName != null)
            return false;
        if (type != null ? !type.equals(column.type) : column.type != null) return false;

        return true;
    }

    @Override
    public String toString() {
        return "Column{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", aliasName='" + aliasName + '\'' +
                ", type='" + type + '\'' +
                ", defaultFunction='" + defaultFunction + '\'' +
                ", originalName='" + originalName + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (aliasName != null ? aliasName.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (defaultFunction != null ? defaultFunction.hashCode() : 0);
        result = 31 * result + (originalName != null ? originalName.hashCode() : 0);
        return result;
    }

    public String getOriginalName() {

        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public Column() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDefaultFunction() {
        return this.defaultFunction;
    }

    public void setDefaultFunction(String defaultFunction) {
        this.defaultFunction = defaultFunction;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
