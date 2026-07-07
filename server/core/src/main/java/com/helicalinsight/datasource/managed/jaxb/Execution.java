package com.helicalinsight.datasource.managed.jaxb;

import com.helicalinsight.efw.utility.AdapterCDATA;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;

/**
 * Created by author on 9/9/2019.
 *
 * @author Rajesh
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "execution")
@XmlAccessorType(XmlAccessType.FIELD)
public class Execution {

    @XmlAttribute
    private String alias;

    @XmlAttribute
    private String id;

    @XmlAttribute
    private String type;


    @XmlElement(name = "input")
    private ExecutionInput executionInput;

    @XmlElement(name = "code")
    @XmlJavaTypeAdapter(AdapterCDATA.class)
    private String executionCode;

    @XmlElement(name = "Query")
    @XmlJavaTypeAdapter(AdapterCDATA.class)
    private String query;

    @XmlElement(name = "steps")
    private ExecutionSteps executionSteps;

    @XmlElement(name = "output")
    private String executionOutput;

    @XmlElement(name = "action")
    private ExecutionAction executionAction;


    public ExecutionInput getExecutionInput() {
        return executionInput;
    }

    public void setExecutionInput(ExecutionInput executionInput) {
        this.executionInput = executionInput;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getExecutionCode() {
        return executionCode;
    }

    public void setExecutionCode(String executionCode) {
        this.executionCode = executionCode;
    }

    public ExecutionSteps getExecutionSteps() {
        return executionSteps;
    }

    public void setExecutionSteps(ExecutionSteps executionSteps) {
        this.executionSteps = executionSteps;
    }

    public String getExecutionOutput() {
        return executionOutput;
    }

    public void setExecutionOutput(String executionOutput) {
        this.executionOutput = executionOutput;
    }

    public ExecutionAction getExecutionAction() {
        return executionAction;
    }

    public void setExecutionAction(ExecutionAction executionAction) {
        this.executionAction = executionAction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Execution execution = (Execution) o;

        if (alias != null ? !alias.equals(execution.alias) : execution.alias != null) return false;
        if (executionAction != null ? !executionAction.equals(execution.executionAction) : execution.executionAction != null)
            return false;
        if (executionCode != null ? !executionCode.equals(execution.executionCode) : execution.executionCode != null)
            return false;
        if (executionInput != null ? !executionInput.equals(execution.executionInput) : execution.executionInput != null)
            return false;
        if (executionOutput != null ? !executionOutput.equals(execution.executionOutput) : execution.executionOutput != null)
            return false;
        if (executionSteps != null ? !executionSteps.equals(execution.executionSteps) : execution.executionSteps != null)
            return false;
        if (id != null ? !id.equals(execution.id) : execution.id != null) return false;
        if (query != null ? !query.equals(execution.query) : execution.query != null) return false;
        if (type != null ? !type.equals(execution.type) : execution.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = alias != null ? alias.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (executionInput != null ? executionInput.hashCode() : 0);
        result = 31 * result + (executionCode != null ? executionCode.hashCode() : 0);
        result = 31 * result + (query != null ? query.hashCode() : 0);
        result = 31 * result + (executionSteps != null ? executionSteps.hashCode() : 0);
        result = 31 * result + (executionOutput != null ? executionOutput.hashCode() : 0);
        result = 31 * result + (executionAction != null ? executionAction.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Execution{" +
                "alias='" + alias + '\'' +
                ", id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", executionInput=" + executionInput +
                ", executionCode='" + executionCode + '\'' +
                ", query='" + query + '\'' +
                ", executionSteps=" + executionSteps +
                ", executionOutput='" + executionOutput + '\'' +
                ", executionAction=" + executionAction +
                '}';
    }
}
