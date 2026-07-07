package com.helicalinsight.datasource.managed.jaxb;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Rajesh
 *         Created by author on 4/9/2019.
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "Parameter")
@XmlAccessorType(XmlAccessType.FIELD)
public class Parameter {

    @XmlAttribute
    private String name;
    @XmlAttribute
    private String type;
    @XmlAttribute(name = "default")
    private String defaultValue;
    @XmlAttribute(name = "openQuote", required = false)
    private String openQuote;
    @XmlAttribute(name = "closeQuote", required = false)
    private String closeQuote;

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

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getOpenQuote() {
        return openQuote;
    }

    public void setOpenQuote(String openQuote) {
        this.openQuote = openQuote;
    }

    public String getCloseQuote() {
        return closeQuote;
    }

    public void setCloseQuote(String closeQuote) {
        this.closeQuote = closeQuote;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Parameter parameter = (Parameter) o;

        if (closeQuote != null ? !closeQuote.equals(parameter.closeQuote) : parameter.closeQuote != null) return false;
        if (defaultValue != null ? !defaultValue.equals(parameter.defaultValue) : parameter.defaultValue != null)
            return false;
        if (name != null ? !name.equals(parameter.name) : parameter.name != null) return false;
        if (openQuote != null ? !openQuote.equals(parameter.openQuote) : parameter.openQuote != null) return false;
        if (type != null ? !type.equals(parameter.type) : parameter.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (defaultValue != null ? defaultValue.hashCode() : 0);
        result = 31 * result + (openQuote != null ? openQuote.hashCode() : 0);
        result = 31 * result + (closeQuote != null ? closeQuote.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Parameter{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                ", openQuote='" + openQuote + '\'' +
                ", closeQuote='" + closeQuote + '\'' +
                '}';
    }
}
