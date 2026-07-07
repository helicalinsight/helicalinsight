package com.helicalinsight.datasource;

/**
 * Created by user on 1/29/2016.
 *
 * @author Rajasekhar
 */
public interface DataSourceProviders {

    public static final String HIKARI = "hikari";
    public static final String TOMCAT = "tomcat";
    public static final String JNDI = "jndi";
    public static final String NONE = "none";
    public static final String CALCITE = "calcite";
    public static final String NOSQL = "noSql";
}
