package com.helicalinsight.adhoc.report;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.utility.AdapterCDATA;
import com.helicalinsight.resourcesecurity.IResource;
import com.helicalinsight.resourcesecurity.jaxb.CanvasElements;
import com.helicalinsight.resourcesecurity.jaxb.MetadataReference;
import com.helicalinsight.resourcesecurity.jaxb.Security;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Created by author on 20-03-2015.
 *
 * @author Rajasekhar
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "report")
@XmlAccessorType(XmlAccessType.FIELD)
public class AdhocReport implements IResource {

    @XmlElement
    private String reportName;

    @XmlElement
    private String visible;

    @XmlElement
    private String description;

    @XmlElement(name = "metadata")
    private MetadataReference metadataReference;

    @XmlElement(name = "canvas")
    private CanvasElements canvasElements;

    @XmlJavaTypeAdapter(AdapterCDATA.class)
    private JsonObject state;

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

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MetadataReference getMetadataReference() {
        return metadataReference;
    }

    public void setMetadataReference(MetadataReference metadataReference) {
        this.metadataReference = metadataReference;
    }

    public CanvasElements getCanvasElements() {
        return canvasElements;
    }

    public void setCanvasElements(CanvasElements canvasElements) {
        this.canvasElements = canvasElements;
    }

    public JsonObject getState() {
        return state;
    }

    public void setState(JsonObject asJsonObject) {
        this.state = asJsonObject;
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

    @NotNull
    @Override
    public String toString() {
        return "AdhocReport{" +
                "reportName='" + reportName + '\'' +
                ", visible='" + visible + '\'' +
                ", description='" + description + '\'' +
                ", metadataReference=" + metadataReference +
                ", canvasElements=" + canvasElements +
                ", state='" + state + '\'' +
                ", security=" + security +
                ", share=" + share +
                '}';
    }
}
