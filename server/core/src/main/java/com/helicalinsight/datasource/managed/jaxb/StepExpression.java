package com.helicalinsight.datasource.managed.jaxb;

import com.helicalinsight.efw.utility.AdapterCDATA;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Created by author on 9/9/2019.
 *
 * @author Rajesh
 */

@Component
@Scope("prototype")
@XmlRootElement(name = "expression")
@XmlAccessorType(XmlAccessType.FIELD)
public class StepExpression {

    @XmlAttribute(name = "type")
    private String expressionType;


    @XmlElement
    private String initialization;

    @XmlElement
    @XmlJavaTypeAdapter(AdapterCDATA.class)
    private String condition;

    @XmlElement
    private String iteration;


    public String getInitialization() {
        return initialization;
    }

    public void setInitialization(String initialization) {
        this.initialization = initialization;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getIteration() {
        return iteration;
    }

    public void setIteration(String iteration) {
        this.iteration = iteration;
    }

    public String getExpressionType() {
        return expressionType;
    }

    public void setExpressionType(String expressionType) {
        this.expressionType = expressionType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StepExpression that = (StepExpression) o;

        if (condition != null ? !condition.equals(that.condition) : that.condition != null) return false;
        if (expressionType != null ? !expressionType.equals(that.expressionType) : that.expressionType != null)
            return false;
        if (initialization != null ? !initialization.equals(that.initialization) : that.initialization != null)
            return false;
        if (iteration != null ? !iteration.equals(that.iteration) : that.iteration != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = expressionType != null ? expressionType.hashCode() : 0;
        result = 31 * result + (initialization != null ? initialization.hashCode() : 0);
        result = 31 * result + (condition != null ? condition.hashCode() : 0);
        result = 31 * result + (iteration != null ? iteration.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "StepExpression{" +
                "expressionType='" + expressionType + '\'' +
                ", initialization='" + initialization + '\'' +
                ", condition='" + condition + '\'' +
                ", iteration='" + iteration + '\'' +
                '}';
    }
}
