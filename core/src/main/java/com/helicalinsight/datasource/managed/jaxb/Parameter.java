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

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Rajesh
 *         Created by author on 4/9/2019.
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "Parameter")
@XmlAccessorType(XmlAccessType.FIELD)
public class Parameter {

    @XmlAttribute
    private String name;
    @XmlAttribute
    private String type;
    @XmlAttribute(name = "default")
    private String defaultValue;
    @XmlAttribute(name = "openQuote", required = false)
    private String openQuote;
    @XmlAttribute(name = "closeQuote", required = false)
    private String closeQuote;

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

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getOpenQuote() {
        return openQuote;
    }

    public void setOpenQuote(String openQuote) {
        this.openQuote = openQuote;
    }

    public String getCloseQuote() {
        return closeQuote;
    }

    public void setCloseQuote(String closeQuote) {
        this.closeQuote = closeQuote;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Parameter parameter = (Parameter) o;

        if (closeQuote != null ? !closeQuote.equals(parameter.closeQuote) : parameter.closeQuote != null) return false;
        if (defaultValue != null ? !defaultValue.equals(parameter.defaultValue) : parameter.defaultValue != null)
            return false;
        if (name != null ? !name.equals(parameter.name) : parameter.name != null) return false;
        if (openQuote != null ? !openQuote.equals(parameter.openQuote) : parameter.openQuote != null) return false;
        if (type != null ? !type.equals(parameter.type) : parameter.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (defaultValue != null ? defaultValue.hashCode() : 0);
        result = 31 * result + (openQuote != null ? openQuote.hashCode() : 0);
        result = 31 * result + (closeQuote != null ? closeQuote.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Parameter{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                ", openQuote='" + openQuote + '\'' +
                ", closeQuote='" + closeQuote + '\'' +
                '}';
    }
}
