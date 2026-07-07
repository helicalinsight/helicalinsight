package com.helicalinsight.datasource.managed.jaxb;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Created by author on 9/6/2019.
 *
 * @author Rajesh
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "input")
@XmlAccessorType(XmlAccessType.FIELD)
public class Inputs {

    @XmlAttribute(name = "mandatory")
    private String mandatory = "true";

    @XmlAnyElement(lax = true)
    private List<Element> inputValues;

    public List<Element> getInputValues() {
        return inputValues;
    }

    public void setInputValues(List<Element> inputValues) {
        this.inputValues = inputValues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Inputs inputs = (Inputs) o;

        if (inputValues != null ? !inputValues.equals(inputs.inputValues) : inputs.inputValues != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return inputValues != null ? inputValues.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Inputs{" +
                "inputValues=" + inputValues +
                '}';
    }
}
