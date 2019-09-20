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
 * Created by author on 09-07-2015.
 *
 * @author Rajasekhar
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "efwfav")
@XmlAccessorType(XmlAccessType.FIELD)
public class Efwfav implements IResource {

    @XmlElement
    private String savedReportFileName;

    @XmlElement
    private String visible = "true";

    @XmlElement
    private String reportName;

    @XmlElement
    private Security security;

    @XmlElement
    private Security.Share share;

    public String getSavedReportFileName() {
        return savedReportFileName;
    }

    public void setSavedReportFileName(String savedReportFileName) {
        this.savedReportFileName = savedReportFileName;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    @Override
    public Security.Share getShare() {
        return share;
    }

    public void setShare(Security.Share share) {
        this.share = share;
    }

    @Override
    public String toString() {
        return "Efwfav{" +
                "savedReportFileName='" + savedReportFileName + '\'' +
                ", visible='" + visible + '\'' +
                ", reportName='" + reportName + '\'' +
                ", security=" + security +
                ", share=" + share +
                '}';
    }
}
