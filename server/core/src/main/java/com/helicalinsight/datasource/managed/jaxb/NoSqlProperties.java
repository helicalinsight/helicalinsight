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

    //Use the following sample payload to create/test a MongoDB connection. The subType is resolved from the
    //"driverName" field (see NoSqlDataSourceProperties#getSubType) and must match the Spring bean name of the
    //NoSQLLoader implementation used to handle it - see com.helicalinsight.adhoc.services.nosql.MongoNoSQLLoader,
    //registered as bean "com.helicalinsight.nosql.mongo" (matching the built-in "Mongodb" tile's driver id).
    /*
    * {"classifier":"global","name":"Test","userName":"test","password":"test",
    *  "jdbcUrl":"mongodb://localhost:27017/mongoDatabase","database":"mongoDatabase",
    *  "driverName":"com.helicalinsight.nosql.mongo","dataSourceProvider":"noSql"}
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
