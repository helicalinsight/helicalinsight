package com.helicalinsight.adhoc.metadata.jaxb;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;

/**
 * Created by author on 10/9/2015.
 *
 * @author Rajasekhar
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "driverClass")
@XmlAccessorType(XmlAccessType.FIELD)
public class DriverClass {

    @XmlAttribute
    private String reference;

    @XmlValue
    private String driverClass;

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    @Override
    public String toString() {
        return "DriverClass{" +
                "reference='" + reference + '\'' +
                ", driverClass='" + driverClass + '\'' +
                '}';
    }
}
