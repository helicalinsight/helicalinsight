package com.helicalinsight.datasource.managed.jaxb;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author Rajesh
 *         Created by author on 4/30/2019.
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "prop")
@XmlAccessorType(XmlAccessType.FIELD)
public class GeneralProp {

    @XmlAnyElement(lax = true)
    private List<Element> otherElements;

    public List<Element> getOtherElements() {
        return otherElements;
    }

    public void setOtherElements(List<Element> otherElements) {
        this.otherElements = otherElements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeneralProp that = (GeneralProp) o;

        if (otherElements != null ? !otherElements.equals(that.otherElements) : that.otherElements != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return otherElements != null ? otherElements.hashCode() : 0;
    }
}
