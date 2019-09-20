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

package com.helicalinsight.resourcesecurity.jaxb;

import com.helicalinsight.resourcesecurity.IResource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by author on 29-05-2015.
 *
 * @author Rajasekhar
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "efw")
@XmlAccessorType(XmlAccessType.FIELD)
public class Efw implements IResource {

    @XmlElement
    private String title;

    @XmlElement
    private String author;

    @XmlElement
    private String description;

    @XmlElement
    private String icon;

    @XmlElement
    private String template;

    @XmlElement
    private String visible;

    @XmlElement
    private String style;

    @XmlElement
    private Security security;

    @XmlElement
    private Security.Share share;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
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

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        Efw efw = (Efw) other;

        if (title != null ? !title.equals(efw.title) : efw.title != null) return false;
        if (author != null ? !author.equals(efw.author) : efw.author != null) return false;
        if (description != null ? !description.equals(efw.description) : efw.description != null) return false;
        if (icon != null ? !icon.equals(efw.icon) : efw.icon != null) return false;
        if (template != null ? !template.equals(efw.template) : efw.template != null) return false;
        if (visible != null ? !visible.equals(efw.visible) : efw.visible != null) return false;
        if (style != null ? !style.equals(efw.style) : efw.style != null) return false;
        if (security != null ? !security.equals(efw.security) : efw.security != null) return false;
        return !(share != null ? !share.equals(efw.share) : efw.share != null);
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (icon != null ? icon.hashCode() : 0);
        result = 31 * result + (template != null ? template.hashCode() : 0);
        result = 31 * result + (visible != null ? visible.hashCode() : 0);
        result = 31 * result + (style != null ? style.hashCode() : 0);
        result = 31 * result + (security != null ? security.hashCode() : 0);
        result = 31 * result + (share != null ? share.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Efw{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", description='" + description + '\'' +
                ", icon='" + icon + '\'' +
                ", template='" + template + '\'' +
                ", visible='" + visible + '\'' +
                ", style='" + style + '\'' +
                ", security=" + security +
                ", share=" + share +
                '}';
    }
}
