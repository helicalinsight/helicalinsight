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
 * @author Rajesh
 * Created by author on 4/9/2019.
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "efwce")
@XmlAccessorType(XmlAccessType.FIELD)
public class EFWCE implements IResource {

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

    @Override
    public Security.Share getShare() {
        return share;
    }

    @Override
    public void setShare(Security.Share share) {
        this.share = share;
    }

    @Override
    public Security getSecurity() {
        return security;
    }

    @Override
    public void setSecurity(Security security) {
        this.security = security;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public String getEfw() {
        return efw;
    }

    public void setEfw(String efw) {
        this.efw = efw;
    }

    @Override
    public String toString() {
        return "EfwDashboard{" +
                "name='" + name + '\'' +
                ", efw='" + efw + '\'' +
                ", visible='" + visible + '\'' +
                ", description='" + description + '\'' +
                ", state='" + state + '\'' +
                ", security=" + security +
                ", share=" + share +
                '}';
    }
}
