/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.datasource.managed.jaxb;

import com.helicalinsight.efw.utility.AdapterCDATA;
import com.helicalinsight.resourcesecurity.jaxb.Security;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author Rajesh
 *         Created by author on 4/17/2019.
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "Connection")
@XmlAccessorType(XmlAccessType.FIELD)
public class ConnectionForCe {

    @XmlAttribute
    private String id;

    @XmlAttribute
    private String type;

    @XmlElement(name = "Driver")
    private String driver;

    @XmlElement(name = "Url")
    private String url;

    @XmlElement(name = "User")
    private String user;

    @XmlElement(name = "Pass")
    private String pass;

    @XmlElement(name = "globalId")
    private String globalId;

    @XmlElement(name = "location")
    private String location;

    @XmlElement(name = "metadataFileName")
    private String metadataFileName;

    @XmlElement(name = "Condition", required = false)
    @XmlJavaTypeAdapter(AdapterCDATA.class)
    private String condition;

    @XmlElement
    private Security security;

    @XmlElement
    private Security.Share share;

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public Security.Share getShare() {
        return share;
    }

    public void setShare(Security.Share share) {
        this.share = share;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMetadataFileName() {
        return metadataFileName;
    }

    public void setMetadataFileName(String metadataFileName) {
        this.metadataFileName = metadataFileName;
    }

    public String getGlobalId() {
        return globalId;
    }

    public void setGlobalId(String globalId) {
        this.globalId = globalId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    @Override
    public String toString() {
        return "ConnectionForCe{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", driver='" + driver + '\'' +
                ", url='" + url + '\'' +
                ", user='" + user + '\'' +
                ", pass='" + pass + '\'' +
                ", globalId='" + globalId + '\'' +
                ", condition='" + condition + '\'' +
                ", location='" + location + '\'' +
                ", metadataFileName='" + metadataFileName + '\'' +
                ", security=" + security +
                ", share=" + share +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConnectionForCe that = (ConnectionForCe) o;

        if (condition != null ? !condition.equals(that.condition) : that.condition != null) return false;
        if (driver != null ? !driver.equals(that.driver) : that.driver != null) return false;
        if (globalId != null ? !globalId.equals(that.globalId) : that.globalId != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (location != null ? !location.equals(that.location) : that.location != null) return false;
        if (metadataFileName != null ? !metadataFileName.equals(that.metadataFileName) : that.metadataFileName != null)
            return false;
        if (pass != null ? !pass.equals(that.pass) : that.pass != null) return false;
        if (security != null ? !security.equals(that.security) : that.security != null) return false;
        if (share != null ? !share.equals(that.share) : that.share != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        if (user != null ? !user.equals(that.user) : that.user != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (driver != null ? driver.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (pass != null ? pass.hashCode() : 0);
        result = 31 * result + (globalId != null ? globalId.hashCode() : 0);
        result = 31 * result + (condition != null ? condition.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (metadataFileName != null ? metadataFileName.hashCode() : 0);
        result = 31 * result + (security != null ? security.hashCode() : 0);
        result = 31 * result + (share != null ? share.hashCode() : 0);
        return result;
    }

}
