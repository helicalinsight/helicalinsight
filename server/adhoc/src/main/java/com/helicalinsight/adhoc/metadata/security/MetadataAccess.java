package com.helicalinsight.adhoc.metadata.security;


import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Somen
 *         Created on 9/9/2015.
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "access")
@XmlAccessorType(XmlAccessType.FIELD)
@SuppressWarnings("unused")
public class MetadataAccess {

    @XmlElement(name = "location")
    private String location;

    @XmlElement(name = "uuid")
    private String uuid;
    @XmlElement(name = "viewId")
    private String viewId;
    @XmlElement(name = "expression")
    private SecurityExpression expression;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MetadataAccess that = (MetadataAccess) o;

        if (!expression.equals(that.expression)) return false;
        if (!location.equals(that.location)) return false;
        if (!uuid.equals(that.uuid)) return false;
        if (!viewId.equals(that.viewId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = location.hashCode();
        result = 31 * result + uuid.hashCode();
        result = 31 * result + viewId.hashCode();
        result = 31 * result + expression.hashCode();
        return result;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getViewId() {
        return viewId;
    }

    public void setViewId(String viewId) {
        this.viewId = viewId;
    }

    public SecurityExpression getExpression() {
        return expression;
    }

    public void setExpression(SecurityExpression expression) {
        this.expression = expression;
    }

}
