package com.helicalinsight.datasource.managed.jaxb;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@Component
@Scope("prototype")

@XmlRootElement(name = "urlConfig")
@XmlAccessorType(XmlAccessType.FIELD)
public class UrlConfig {


    @XmlElement(name = "host")
    private String host;

    @XmlElement(name = "port")
    private String port;
    @XmlElement(name = "dbPort")
    private String dbPort;
    @XmlElement(name = "extraParam")
    private String extraParam;

    @XmlElement(name = "securityEnabled")
    private String securityEnabled;

    @XmlElement(name = "securityMode")
    private String securityMode;

    @XmlElement(name = "securityCheckType")
    private String securityCheckType;


    @XmlElement(name = "username")
    private String userName;
    @XmlElement(name = "password")
    private String password;
    @XmlElement(name = "httpsState")
    private String httpsState;

    @Override
    public String toString() {
        return "UrlConfig{" +
                "host='" + host + '\'' +
                ", port='" + port + '\'' +
                ", dbPort='" + dbPort + '\'' +
                ", extraParam='" + extraParam + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", httpsState='" + httpsState + '\'' +
                ", https='" + https + '\'' +
                ", distributedMode='" + distributedMode + '\'' +
                ", zookeeperPort='" + zookeeperPort + '\'' +
                ", securityEnabled='" + securityEnabled + '\'' +
                ", securityMode='" + securityMode + '\'' +
                ", securityCheckType='" + securityCheckType + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UrlConfig urlConfig = (UrlConfig) o;

        if (dbPort != null ? !dbPort.equals(urlConfig.dbPort) : urlConfig.dbPort != null) return false;
        if (distributedMode != null ? !distributedMode.equals(urlConfig.distributedMode) : urlConfig.distributedMode != null)
            return false;
        if (extraParam != null ? !extraParam.equals(urlConfig.extraParam) : urlConfig.extraParam != null) return false;
        if (host != null ? !host.equals(urlConfig.host) : urlConfig.host != null) return false;
        if (https != null ? !https.equals(urlConfig.https) : urlConfig.https != null) return false;
        if (httpsState != null ? !httpsState.equals(urlConfig.httpsState) : urlConfig.httpsState != null) return false;
        if (password != null ? !password.equals(urlConfig.password) : urlConfig.password != null) return false;
        if (port != null ? !port.equals(urlConfig.port) : urlConfig.port != null) return false;
        if (securityCheckType != null ? !securityCheckType.equals(urlConfig.securityCheckType) : urlConfig.securityCheckType != null)
            return false;
        if (securityEnabled != null ? !securityEnabled.equals(urlConfig.securityEnabled) : urlConfig.securityEnabled != null)
            return false;
        if (securityMode != null ? !securityMode.equals(urlConfig.securityMode) : urlConfig.securityMode != null)
            return false;
        if (userName != null ? !userName.equals(urlConfig.userName) : urlConfig.userName != null) return false;
        if (zookeeperPort != null ? !zookeeperPort.equals(urlConfig.zookeeperPort) : urlConfig.zookeeperPort != null)
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = host != null ? host.hashCode() : 0;
        result = 31 * result + (port != null ? port.hashCode() : 0);
        result = 31 * result + (dbPort != null ? dbPort.hashCode() : 0);
        result = 31 * result + (extraParam != null ? extraParam.hashCode() : 0);
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (httpsState != null ? httpsState.hashCode() : 0);
        result = 31 * result + (https != null ? https.hashCode() : 0);
        result = 31 * result + (distributedMode != null ? distributedMode.hashCode() : 0);
        result = 31 * result + (zookeeperPort != null ? zookeeperPort.hashCode() : 0);
        result = 31 * result + (securityEnabled != null ? securityEnabled.hashCode() : 0);
        result = 31 * result + (securityMode != null ? securityMode.hashCode() : 0);
        result = 31 * result + (securityCheckType != null ? securityCheckType.hashCode() : 0);
        return result;
    }

    public String getDistributedMode() {
        return distributedMode;
    }

    public void setDistributedMode(String distributedMode) {
        this.distributedMode = distributedMode;
    }

    public String getZookeeperPort() {
        return zookeeperPort;
    }

    public void setZookeeperPort(String zookeeperPort) {
        this.zookeeperPort = zookeeperPort;
    }

    @XmlElement(name = "https")

    private String https;



    @XmlElement(name = "distributedMode")
    private String distributedMode;
    @XmlElement(name = "zookeeperPort")
    private String zookeeperPort;



    public String getSecurityCheckType() {

        return securityCheckType;
    }

    public void setSecurityCheckType(String securityCheckType) {
        this.securityCheckType = securityCheckType;
    }

    public String getSecurityMode() {
        return securityMode;
    }

    public void setSecurityMode(String securityMode) {
        this.securityMode = securityMode;
    }

    public String getSecurityEnabled() {
        return securityEnabled;
    }

    public void setSecurityEnabled(String securityEnabled) {
        this.securityEnabled = securityEnabled;
    }


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getExtraParam() {
        return extraParam;
    }

    public void setExtraParam(String extraParam) {
        this.extraParam = extraParam;
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

    public String getHttpsState() {
        return httpsState;
    }

    public void setHttpsState(String httpsState) {
        this.httpsState = httpsState;
    }

    public String getHttps() {
        return https;
    }

    public void setHttps(String https) {
        this.https = https;
    }

    public String getDbPort() {
        return dbPort;
    }

    public void setDbPort(String dbPort) {
        this.dbPort = dbPort;
    }

}