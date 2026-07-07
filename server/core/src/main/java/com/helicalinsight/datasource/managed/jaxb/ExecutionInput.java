package com.helicalinsight.datasource.managed.jaxb;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by author on 9/10/2019.
 *
 * @author Rajesh
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "input")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExecutionInput {

    @XmlElement(name = "ref")
    private List<ReferenceForInput> listOfRefs;

    public List<ReferenceForInput> getListOfRefs() {
        return listOfRefs;
    }

    public void setListOfRefs(List<ReferenceForInput> listOfRefs) {
        this.listOfRefs = listOfRefs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExecutionInput that = (ExecutionInput) o;

        if (listOfRefs != null ? !listOfRefs.equals(that.listOfRefs) : that.listOfRefs != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return listOfRefs != null ? listOfRefs.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ExecutionInput{" +
                "listOfRefs=" + listOfRefs +
                '}';
    }
}
