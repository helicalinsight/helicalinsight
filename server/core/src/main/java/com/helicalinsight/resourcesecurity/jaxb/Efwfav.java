package com.helicalinsight.resourcesecurity.jaxb;

import com.helicalinsight.resourcesecurity.IResource;
import org.jetbrains.annotations.NotNull;
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

    @NotNull
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
