package com.helicalinsight.adhoc.metadata.security;

import com.helicalinsight.efw.utility.AdapterCDATA;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author Somen by helicalinsight on 9/9/2015.
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "expression")
@XmlAccessorType(XmlAccessType.FIELD)
@SuppressWarnings("unused")
public class SecurityExpression {
    @XmlAttribute(name = "expressionId")
    private String id;

    @XmlAttribute
    private String type;

    @XmlAttribute
    private String expressionName;
    @XmlAttribute
    private String expressionType;
    @XmlAttribute
    private String on;
    @XmlAttribute
    private String accessType;
    @XmlElement
    @XmlJavaTypeAdapter(AdapterCDATA.class)
    private String condition;
    @XmlElement
    @XmlJavaTypeAdapter(AdapterCDATA.class)
    private String filter;

    public String getExpressionName() {
        return expressionName;
    }

    public void setExpressionName(String expressionName) {
        this.expressionName = expressionName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExpressionType() {
        return expressionType;
    }

    public void setExpressionType(String expressionType) {
        this.expressionType = expressionType;
    }

    public String getOn() {
        return on;
    }

    public void setOn(String on) {
        this.on = on;
    }

    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    @Override
    public String toString() {
        return "SecurityExpression{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", expressionName='" + expressionName + '\'' +
                ", expressionType='" + expressionType + '\'' +
                ", on='" + on + '\'' +
                ", accessType='" + accessType + '\'' +
                ", condition='" + condition + '\'' +
                ", filter='" + filter + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SecurityExpression that = (SecurityExpression) o;

        if (accessType != null ? !accessType.equals(that.accessType) : that.accessType != null) return false;
        if (condition != null ? !condition.equals(that.condition) : that.condition != null) return false;
        if (expressionName != null ? !expressionName.equals(that.expressionName) : that.expressionName != null)
            return false;
        if (expressionType != null ? !expressionType.equals(that.expressionType) : that.expressionType != null)
            return false;
        if (filter != null ? !filter.equals(that.filter) : that.filter != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (on != null ? !on.equals(that.on) : that.on != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (expressionName != null ? expressionName.hashCode() : 0);
        result = 31 * result + (expressionType != null ? expressionType.hashCode() : 0);
        result = 31 * result + (on != null ? on.hashCode() : 0);
        result = 31 * result + (accessType != null ? accessType.hashCode() : 0);
        result = 31 * result + (condition != null ? condition.hashCode() : 0);
        result = 31 * result + (filter != null ? filter.hashCode() : 0);
        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

}