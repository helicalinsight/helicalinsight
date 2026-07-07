package com.helicalinsight.datasource.managed.jaxb;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by author on 9/9/2019.
 *
 * @author Rajesh
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "flow")
@XmlAccessorType(XmlAccessType.FIELD)
public class Flow {

    @XmlElement(name = "execution")
    private List<Execution> listOfExecution;

    public List<Execution> getListOfExecution() {
        return listOfExecution;
    }

    public void setListOfExecution(List<Execution> listOfExecution) {
        this.listOfExecution = listOfExecution;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Flow flow = (Flow) o;

        if (listOfExecution != null ? !listOfExecution.equals(flow.listOfExecution) : flow.listOfExecution != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return listOfExecution != null ? listOfExecution.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Flow{" +
                "listOfExecution=" + listOfExecution +
                '}';
    }
}
