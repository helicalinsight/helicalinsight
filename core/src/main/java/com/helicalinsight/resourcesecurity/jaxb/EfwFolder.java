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
 * Created by author on 08-07-2015.
 *
 * @author Rajasekhar
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "efwfolder")
@XmlAccessorType(XmlAccessType.FIELD)
public class EfwFolder implements IResource {

    @XmlElement
    private String visible = "true";

    @XmlElement
    private String title;

    @XmlElement
    private Security security;

    @XmlElement
    private Security.Share share;

    public Security.Share getShare() {
        return share;
    }

    public void setShare(Security.Share share) {
        this.share = share;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    @Override
    public String toString() {
        return "EfwFolder{" +
                "visible='" + visible + '\'' +
                ", title='" + title + '\'' +
                ", security=" + security +
                ", share=" + share +
                '}';
    }
}
