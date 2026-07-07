package com.helicalinsight.datasource.managed.jaxb;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by author on 9/9/2019.
 *
 * @author Rajesh
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "action")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExecutionAction {

    @XmlElement
    private String executeNext;

    @XmlElement
    private String ifTrue;

    @XmlElement
    private String ifFalse;

    @XmlElement
    private String iterate;

    @XmlElement
    private String exit;

    @XmlElement
    private String onException;

    public String getIterate() {
        return iterate;
    }

    public void setIterate(String iterate) {
        this.iterate = iterate;
    }

    public String getExit() {
        return exit;
    }

    public void setExit(String exit) {
        this.exit = exit;
    }

    public String getOnException() {
        return onException;
    }

    public void setOnException(String onException) {
        this.onException = onException;
    }

    public String getExecuteNext() {
        return executeNext;
    }

    public void setExecuteNext(String executeNext) {
        this.executeNext = executeNext;
    }

    public String getIfTrue() {
        return ifTrue;
    }

    public void setIfTrue(String ifTrue) {
        this.ifTrue = ifTrue;
    }

    public String getIfFalse() {
        return ifFalse;
    }

    public void setIfFalse(String ifFalse) {
        this.ifFalse = ifFalse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExecutionAction that = (ExecutionAction) o;

        if (executeNext != null ? !executeNext.equals(that.executeNext) : that.executeNext != null) return false;
        if (exit != null ? !exit.equals(that.exit) : that.exit != null) return false;
        if (ifFalse != null ? !ifFalse.equals(that.ifFalse) : that.ifFalse != null) return false;
        if (ifTrue != null ? !ifTrue.equals(that.ifTrue) : that.ifTrue != null) return false;
        if (iterate != null ? !iterate.equals(that.iterate) : that.iterate != null) return false;
        if (onException != null ? !onException.equals(that.onException) : that.onException != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = executeNext != null ? executeNext.hashCode() : 0;
        result = 31 * result + (ifTrue != null ? ifTrue.hashCode() : 0);
        result = 31 * result + (ifFalse != null ? ifFalse.hashCode() : 0);
        result = 31 * result + (iterate != null ? iterate.hashCode() : 0);
        result = 31 * result + (exit != null ? exit.hashCode() : 0);
        result = 31 * result + (onException != null ? onException.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ExecutionAction{" +
                "executeNext='" + executeNext + '\'' +
                ", ifTrue='" + ifTrue + '\'' +
                ", ifFalse='" + ifFalse + '\'' +
                ", iterate='" + iterate + '\'' +
                ", exit='" + exit + '\'' +
                ", onException='" + onException + '\'' +
                '}';
    }
}
