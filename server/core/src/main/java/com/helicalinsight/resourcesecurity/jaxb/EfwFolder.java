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

    @NotNull
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
