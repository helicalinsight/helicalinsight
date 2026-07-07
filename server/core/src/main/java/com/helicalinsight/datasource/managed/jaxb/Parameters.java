package com.helicalinsight.datasource.managed.jaxb;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author Rajesh
 *         Created by author on 4/22/2019.
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "Parameters")
@XmlAccessorType(XmlAccessType.FIELD)
public class Parameters {
    @XmlElement(name = "Parameter")
    private List<Parameter> parameter;

    public List<Parameter> getParameter() {
        return parameter;
    }

    public void setParameter(List<Parameter> parameter) {
        this.parameter = parameter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Parameters that = (Parameters) o;

        if (parameter != null ? !parameter.equals(that.parameter) : that.parameter != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return parameter != null ? parameter.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Parameters{" +
                "parameter=" + parameter +
                '}';
    }
}
