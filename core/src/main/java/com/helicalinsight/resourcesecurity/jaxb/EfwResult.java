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
 * Created by author on 21-07-2015.
 *
 * @author Rajasekhar
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "efwresult")
@XmlAccessorType(XmlAccessType.FIELD)
public class EfwResult implements IResource {

    @XmlElement
    private String reportName;

    @XmlElement
    private String reportDir;

    @XmlElement
    private String reportFile;

    @XmlElement
    private String reportType;

    @XmlElement
    private String resultName;

    @XmlElement
    private String resultDirectory;

    @XmlElement
    private String resultFile;

    @XmlElement
    private String runDate;

    @XmlElement
    private String visible = "true";

    @XmlElement
    private Security security;

    @XmlElement
    private Security.Share share;

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getReportDir() {
        return reportDir;
    }

    public void setReportDir(String reportDir) {
        this.reportDir = reportDir;
    }

    public String getReportFile() {
        return reportFile;
    }

    public void setReportFile(String reportFile) {
        this.reportFile = reportFile;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getResultName() {
        return resultName;
    }

    public void setResultName(String resultName) {
        this.resultName = resultName;
    }

    public String getResultDirectory() {
        return resultDirectory;
    }

    public void setResultDirectory(String resultDirectory) {
        this.resultDirectory = resultDirectory;
    }

    public String getResultFile() {
        return resultFile;
    }

    public void setResultFile(String resultFile) {
        this.resultFile = resultFile;
    }

    public String getRunDate() {
        return runDate;
    }

    public void setRunDate(String runDate) {
        this.runDate = runDate;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
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
    public String toString() {
        return "EfwResult{" +
                "reportName='" + reportName + '\'' +
                ", reportDir='" + reportDir + '\'' +
                ", reportFile='" + reportFile + '\'' +
                ", reportType='" + reportType + '\'' +
                ", resultName='" + resultName + '\'' +
                ", resultDirectory='" + resultDirectory + '\'' +
                ", resultFile='" + resultFile + '\'' +
                ", runDate='" + runDate + '\'' +
                ", visible='" + visible + '\'' +
                ", security=" + security +
                ", share=" + share +
                '}';
    }
}
