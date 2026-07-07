package com.helicalinsight.datasource.managed.jaxb;

import com.helicalinsight.datasource.GlobalJdbcType;
import com.helicalinsight.resourcesecurity.IResource;
import com.helicalinsight.resourcesecurity.jaxb.Security;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;

/**
 * Created by author on 26-Dec-14.
 *
 * @author Rajasekhar
 */
@Component
@Scope("prototype")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@SuppressWarnings("unused")
public class JdbcConnection implements IResource {

    @XmlAttribute
    private int id;

    @XmlAttribute
    private String type = GlobalJdbcType.NON_POOLED;

    @XmlAttribute
    private String name;
    @XmlAttribute
    private String baseType = GlobalJdbcType.TYPE;

    @XmlElement
    private String visible;

    @XmlElement
    private String userName;

    @XmlElement
    private String password;
    @XmlElement
    private String jdbcUrl;
    @XmlElement
    private String driverName;
    @XmlElement
    private String dataSourceProvider;

    @XmlElement
    private Security security;

    @XmlElement
    private Security.Share share;

    @XmlElement
    private String databaseDialect;

    @XmlElement
    private String databaseName;

    public JdbcConnection() {
    }


    public JdbcConnection(int id, String type, String name, String baseType, String userName, String password,
                          String jdbcUrl, String driverName, String dataSourceProvider, Security security,
                          Security.Share share, String databaseDialect, String databaseName) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.baseType = baseType;
        this.userName = userName;
        this.password = password;
        this.jdbcUrl = jdbcUrl;

        this.driverName = driverName;
        this.dataSourceProvider = dataSourceProvider;
        this.security = security;
        this.share = share;
        this.databaseDialect = databaseDialect;
        this.databaseName = databaseName;
    }

    @Override
    public String toString() {
        return "JdbcConnection{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", baseType='" + baseType + '\'' +
                ", visible='" + visible + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", jdbcUrl='" + jdbcUrl + '\'' +
                ", driverName='" + driverName + '\'' +
                ", dataSourceProvider='" + dataSourceProvider + '\'' +
                ", security=" + security +
                ", share=" + share +
                ", databaseDialect='" + databaseDialect + '\'' +
                ", databaseName='" + databaseName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JdbcConnection that = (JdbcConnection) o;

        if (id != that.id) return false;
        if (!baseType.equals(that.baseType)) return false;
        if (!dataSourceProvider.equals(that.dataSourceProvider)) return false;
        if (!databaseDialect.equals(that.databaseDialect)) return false;
        if (!databaseName.equals(that.databaseName)) return false;
        if (!driverName.equals(that.driverName)) return false;
        if (!jdbcUrl.equals(that.jdbcUrl)) return false;
        if (!name.equals(that.name)) return false;
        if (!password.equals(that.password)) return false;
        if (!security.equals(that.security)) return false;
        if (!share.equals(that.share)) return false;
        if (!type.equals(that.type)) return false;
        if (!userName.equals(that.userName)) return false;
        if (!visible.equals(that.visible)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + type.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + baseType.hashCode();
       // result = 31 * result + visible.hashCode();
        result = 31 * result + userName.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + jdbcUrl.hashCode();
        result = 31 * result + driverName.hashCode();
        result = 31 * result + dataSourceProvider.hashCode();
        result = 31 * result + security.hashCode();
        result = 31 * result + share.hashCode();
        result = 31 * result + databaseDialect.hashCode();
        result = 31 * result + databaseName.hashCode();
        return result;
    }

    public void setDatabaseName(String databaseName) {

        this.databaseName = databaseName;
    }

    public String getDatabaseName() {

        return databaseName;
    }

    public String getDatabaseDialect() {
        return databaseDialect;
    }

    public void setDatabaseDialect(String databaseVersion) {
        this.databaseDialect = databaseVersion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBaseType() {
        return baseType;
    }

    public void setBaseType(String baseType) {
        this.baseType = baseType;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDataSourceProvider() {
        return dataSourceProvider;
    }

    public void setDataSourceProvider(String dataSourceProvider) {
        this.dataSourceProvider = dataSourceProvider;
    }

    @Override
    public Security getSecurity() {
        return security;
    }

    @Override
    public void setSecurity(Security security) {
        this.security = security;
    }

    @Override
    public Security.Share getShare() {
        return share;
    }

    @Override
    public void setShare(Security.Share share) {
        this.share = share;
    }


}