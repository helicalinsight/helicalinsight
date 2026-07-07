package com.helicalinsight.datasource.managed.jaxb;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;

/**
 * Created by author on 9/9/2019.
 *
 * @author Rajesh
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "steps")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExecutionSteps {

    @XmlAttribute(name = "mandatory")
    private String mandatory = "true";

    @XmlElement(name = "expression")
    private StepExpression stepExpression;

    @XmlElement(name = "expression")
    private StepExpressionForGroovy stepExpressionForGroovy;

    public StepExpressionForGroovy getStepExpressionForGroovy() {
        return stepExpressionForGroovy;
    }

    public void setStepExpressionForGroovy(StepExpressionForGroovy stepExpressionForGroovy) {
        this.stepExpressionForGroovy = stepExpressionForGroovy;
    }

    public String getMandatory() {
        return mandatory;
    }

    public void setMandatory(String mandatory) {
        this.mandatory = mandatory;
    }

    public StepExpression getStepExpression() {
        return stepExpression;
    }

    public void setStepExpression(StepExpression stepExpression) {
        this.stepExpression = stepExpression;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExecutionSteps that = (ExecutionSteps) o;

        if (mandatory != null ? !mandatory.equals(that.mandatory) : that.mandatory != null) return false;
        if (stepExpression != null ? !stepExpression.equals(that.stepExpression) : that.stepExpression != null)
            return false;
        if (stepExpressionForGroovy != null ? !stepExpressionForGroovy.equals(that.stepExpressionForGroovy) : that.stepExpressionForGroovy != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mandatory != null ? mandatory.hashCode() : 0;
        result = 31 * result + (stepExpression != null ? stepExpression.hashCode() : 0);
        result = 31 * result + (stepExpressionForGroovy != null ? stepExpressionForGroovy.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ExecutionSteps{" +
                "mandatory='" + mandatory + '\'' +
                ", stepExpression=" + stepExpression +
                ", stepExpressionForGroovy=" + stepExpressionForGroovy +
                '}';
    }
}
