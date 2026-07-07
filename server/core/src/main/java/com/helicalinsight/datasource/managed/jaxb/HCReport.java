package com.helicalinsight.datasource.managed.jaxb;

import com.helicalinsight.efw.utility.AdapterCDATA;
import com.helicalinsight.resourcesecurity.IResource;
import com.helicalinsight.resourcesecurity.jaxb.Security;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Created by author on 10/17/2019.
 *
 * @author Rajesh
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "HCR")
@XmlAccessorType(XmlAccessType.FIELD)
public class HCReport implements IResource {
    @XmlElement(name = "fileName")
    private String name;

    @XmlElement(name = "directory")
    private String directory;


    @XmlElement(name = "efwdDetails")
    @XmlJavaTypeAdapter(AdapterCDATA.class)
    private String efwdDetails;

    @XmlElement(name = "state")
    @XmlJavaTypeAdapter(AdapterCDATA.class)
    private String state;

    @XmlElement(name = "previewFormData")
    @XmlJavaTypeAdapter(AdapterCDATA.class)
    private String formData;

    @XmlElement(name = "diagram")
    @XmlJavaTypeAdapter(AdapterCDATA.class)
    private String diagramData;

    @XmlElement
    private String visible;

    @XmlElement
    private Security security;

    @XmlElement
    private Security.Share share;

    public String getEfwdDetails() {
        return efwdDetails;
    }

    public void setEfwdDetails(String efwdDetails) {
        this.efwdDetails = efwdDetails;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDiagramData() {
        return diagramData;
    }

    public void setDiagramData(String diagramData) {
        this.diagramData = diagramData;
    }

    public String getFormData() {
        return formData;
    }

    public void setFormData(String formData) {
        this.formData = formData;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "HCReport{" +
                "name='" + name + '\'' +
                ", directory='" + directory + '\'' +
                ", efwdDetails='" + efwdDetails + '\'' +
                ", state='" + state + '\'' +
                ", formData='" + formData + '\'' +
                ", diagramData='" + diagramData + '\'' +
                ", visible='" + visible + '\'' +
                ", security=" + security +
                ", share=" + share +
                '}';
    }
}
