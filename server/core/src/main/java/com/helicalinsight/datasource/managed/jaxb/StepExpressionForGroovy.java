package com.helicalinsight.datasource.managed.jaxb;

import com.helicalinsight.efw.utility.AdapterCDATA;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Created by author on 9/11/2019.
 *
 * @author Rajesh
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "expression")
@XmlAccessorType(XmlAccessType.FIELD)

public class StepExpressionForGroovy {

    @XmlAttribute(name = "type")
    private String expressionType;

    @XmlValue
    @XmlJavaTypeAdapter(AdapterCDATA.class)
    private String expressionValue;

    public String getExpressionType() {
        return expressionType;
    }

    public void setExpressionType(String expressionType) {
        this.expressionType = expressionType;
    }

    public String getExpressionValue() {
        return expressionValue;
    }

    public void setExpressionValue(String expressionValue) {
        this.expressionValue = expressionValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StepExpressionForGroovy that = (StepExpressionForGroovy) o;

        if (expressionType != null ? !expressionType.equals(that.expressionType) : that.expressionType != null)
            return false;
        if (expressionValue != null ? !expressionValue.equals(that.expressionValue) : that.expressionValue != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = expressionType != null ? expressionType.hashCode() : 0;
        result = 31 * result + (expressionValue != null ? expressionValue.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "StepExpressionForGroovy{" +
                "expressionType='" + expressionType + '\'' +
                ", expressionValue='" + expressionValue + '\'' +
                '}';
    }
}
