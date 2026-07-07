package com.helicalinsight.datasource.managed.jaxb;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Somen
 * Created  on 11/9/2017.
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "noSqlDataSource")
@XmlAccessorType(XmlAccessType.FIELD)
public class NoSqlProperties extends  TomcatPoolProperties{


    @XmlElement
    private Integer hiveReferenceId;

    //Todo create a subType map or property file read from xml
    //Todo Create a Test connection for subtype

    //Use the following for Testing
    /*
    * {"classifier":"global","name":"Test","driverName":"org.mariadb.jdbc.Driver","userName":"test","password":"test","jdbcUrl":"jdbc:mariadb://localhost:3306/mongoDatabase?ssl=True","dataSourceProvider":"nosql",subType:"mongo"}
    *
    * */
    //Mongo Text CSV JSON etc
    @XmlElement
    private String subType;

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    @XmlElement
    private String collection;

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public Integer getHiveReferenceId() {
        return hiveReferenceId;
    }

    public void setHiveReferenceId(Integer hiveReferenceId) {
        this.hiveReferenceId = hiveReferenceId;
    }
}
