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

import com.helicalinsight.efw.utility.AdapterCDATA;
import com.helicalinsight.resourcesecurity.IResource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Created by author on 08-07-2015.
 *
 * @author Rajasekhar
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "efwsr")
@XmlAccessorType(XmlAccessType.FIELD)
public class Efwsr implements IResource {

    @XmlElement
    private String reportName;

    @XmlElement
    private String reportFile;

    @XmlElement
    private String reportDirectory;

    @XmlJavaTypeAdapter(AdapterCDATA.class)
    private String reportParameters;

    @XmlElement
    private String visible = "true";

    @XmlElement
    private String schedulingReference;

    @XmlElement
    private Security security;

    @XmlElement
    private Security.Share share;

    @XmlElement
    private String favourite = "false";

    @Override
    public String toString() {
        return "Efwsr{" +
                "reportName='" + reportName + '\'' +
                ", reportFile='" + reportFile + '\'' +
                ", reportDirectory='" + reportDirectory + '\'' +
                ", reportParameters='" + reportParameters + '\'' +
                ", visible='" + visible + '\'' +
                ", schedulingReference=" + schedulingReference +
                ", security=" + security +
                ", share=" + share +
                ", favourite='" + favourite + '\'' +
                '}';
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getReportFile() {
        return reportFile;
    }

    public void setReportFile(String reportFile) {
        this.reportFile = reportFile;
    }

    public String getReportDirectory() {
        return reportDirectory;
    }

    public void setReportDirectory(String reportDirectory) {
        this.reportDirectory = reportDirectory;
    }

    public String getReportParameters() {
        return reportParameters;
    }

    public void setReportParameters(String reportParameters) {
        this.reportParameters = reportParameters;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public String getSchedulingReference() {
        return schedulingReference;
    }

    public void setSchedulingReference(String schedulingReference) {
        this.schedulingReference = schedulingReference;
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

    public String getFavourite() {
        return favourite;
    }

    public void setFavourite(String favourite) {
        this.favourite = favourite;
    }
}
