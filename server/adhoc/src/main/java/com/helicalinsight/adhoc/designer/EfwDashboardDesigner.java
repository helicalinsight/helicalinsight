package com.helicalinsight.adhoc.designer;

import com.helicalinsight.efw.utility.AdapterCDATA;
import com.helicalinsight.resourcesecurity.IResource;
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
 * Created by author on 29-05-2015.
 *
 * @author Rajasekhar
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "efwdd")
@XmlAccessorType(XmlAccessType.FIELD)
public class EfwDashboardDesigner implements IResource {

    @XmlElement(name = "fileName")
    private String name;

    @XmlElement
    private String efw;

    @XmlElement
    private String visible;

    @XmlElement
    private String description;

    @XmlJavaTypeAdapter(AdapterCDATA.class)
    private String state;

    @XmlElement
    private Security security;

    @XmlElement
    private Security.Share share;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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

    public String getEfw() {
        return efw;
    }

    public void setEfw(String efw) {
        this.efw = efw;
    }

    @NotNull
    @Override
    public String toString() {
        return "EfwDashboardDesigner [name=" + name + ", efw=" + efw + ", " +
                "visible=" + visible + ", description=" + description + ", state=" + state + ", " +
                "security=" + security + ", share=" + share + "]";
    }
}
