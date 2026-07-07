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
@XmlRootElement(name = "ref")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReferenceForInput {

    @XmlAttribute(name = "for")
    private String forName;

    @XmlAttribute(name = "required")
    private String required;

    @XmlValue
    private String refName;

    public String getForName() {
        return forName;
    }

    public void setForName(String forName) {
        this.forName = forName;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public String getRefName() {
        return refName;
    }

    public void setRefName(String refName) {
        this.refName = refName;
    }

    @Override
    public String toString() {
        return "ReferenceForInput{" +
                "forName='" + forName + '\'' +
                ", refName='" + refName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReferenceForInput that = (ReferenceForInput) o;

        if (forName != null ? !forName.equals(that.forName) : that.forName != null) return false;
        if (refName != null ? !refName.equals(that.refName) : that.refName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = forName != null ? forName.hashCode() : 0;
        result = 31 * result + (refName != null ? refName.hashCode() : 0);
        return result;
    }
}
