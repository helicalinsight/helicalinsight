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

import com.helicalinsight.resourcesecurity.jaxb.Security;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.*;
import javax.xml.namespace.QName;
import java.util.List;
import java.util.Map;

@Component
@Scope("prototype")
@XmlRootElement(name = "Connection")
@XmlAccessorType(XmlAccessType.FIELD)
public class Connection {

    @XmlAttribute
    private String id;
    @XmlAttribute
    private String name;
    @XmlAttribute
    private String type;

    @XmlElement(name = "Driver")
    private String driver;

    @XmlElement(name = "Url")
    private String url;

    @XmlElement(name = "User")
    private String user;

    @XmlElement
    private String visible;

    @XmlElement(name = "Pass")
    private String pass;

    @XmlElement
    private Security security;

    @XmlElement
    private Security.Share share;


    @XmlAnyElement(lax = true)
    private List<Element> otherElements;


    @XmlAnyAttribute
    private Map<QName, Object> otherAttributes;


    public Connection() {

    }

    public List<Element> getOtherElements() {
        return otherElements;
    }

    public void setOtherElements(List<Element> otherElements) {
        this.otherElements = otherElements;
    }

    public Map<QName, Object> getOtherAttributes() {
        return otherAttributes;
    }

    public void setOtherAttributes(Map<QName, Object> otherAttributes) {
        this.otherAttributes = otherAttributes;
    }

    @Override
    public String toString() {
        return "Connection{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", driver='" + driver + '\'' +
                ", url='" + url + '\'' +
                ", user='" + user + '\'' +
                ", visible='" + visible + '\'' +
                ", pass='" + pass + '\'' +
                ", security=" + security +
                ", share=" + share +
                ", otherElements=" + otherElements +
                ", otherAttributes=" + otherAttributes +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Connection that = (Connection) o;

        if (driver != null ? !driver.equals(that.driver) : that.driver != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (otherAttributes != null ? !otherAttributes.equals(that.otherAttributes) : that.otherAttributes != null)
            return false;
        if (otherElements != null ? !otherElements.equals(that.otherElements) : that.otherElements != null)
            return false;
        if (pass != null ? !pass.equals(that.pass) : that.pass != null) return false;
        if (security != null ? !security.equals(that.security) : that.security != null) return false;
        if (share != null ? !share.equals(that.share) : that.share != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        if (user != null ? !user.equals(that.user) : that.user != null) return false;
        if (visible != null ? !visible.equals(that.visible) : that.visible != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (driver != null ? driver.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (visible != null ? visible.hashCode() : 0);
        result = 31 * result + (pass != null ? pass.hashCode() : 0);
        result = 31 * result + (security != null ? security.hashCode() : 0);
        result = 31 * result + (share != null ? share.hashCode() : 0);
        result = 31 * result + (otherElements != null ? otherElements.hashCode() : 0);
        result = 31 * result + (otherAttributes != null ? otherAttributes.hashCode() : 0);
        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

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

}
