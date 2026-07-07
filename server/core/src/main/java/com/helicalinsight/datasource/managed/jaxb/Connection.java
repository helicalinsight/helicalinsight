package com.helicalinsight.datasource.managed.jaxb;

import com.helicalinsight.resourcesecurity.jaxb.Security;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.*;
import javax.xml.namespace.QName;
import java.util.List;
import java.util.Map;

@Component
@Scope("prototype")
@XmlRootElement(name = "Connection")
@XmlAccessorType(XmlAccessType.FIELD)
public class Connection {

    @XmlAttribute
    private String id;
    @XmlAttribute
    private String name;
    @XmlAttribute
    private String type;

    @XmlElement(name = "Driver")
    private String driver;

    @XmlElement(name = "Url")
    private String url;

    @XmlElement(name = "User")
    private String user;

    @XmlElement
    private String visible;

    @XmlElement(name = "Pass")
    private String pass;

    @XmlElement
    private Security security;

    @XmlElement
    private Security.Share share;
    
    @XmlElement
    private String databaseDialect;

    @XmlAnyElement(lax = true)
    private List<Element> otherElements;


    @XmlAnyAttribute
    private Map<QName, Object> otherAttributes;


    public Connection() {

    }

    public List<Element> getOtherElements() {
        return otherElements;
    }

    public void setOtherElements(List<Element> otherElements) {
        this.otherElements = otherElements;
    }

    public Map<QName, Object> getOtherAttributes() {
        return otherAttributes;
    }

    public void setOtherAttributes(Map<QName, Object> otherAttributes) {
        this.otherAttributes = otherAttributes;
    }

    @Override
	public String toString() {
		return "Connection [id=" + id + ", name=" + name + ", type=" + type + ", driver=" + driver + ", url=" + url
				+ ", user=" + user + ", visible=" + visible + ", pass=" + pass + ", security=" + security + ", share="
				+ share + ", databaseDialect=" + databaseDialect + ", otherElements=" + otherElements
				+ ", otherAttributes=" + otherAttributes + "]";
	}

    @Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Connection other = (Connection) obj;
		if (databaseDialect == null) {
			if (other.databaseDialect != null)
				return false;
		} else if (!databaseDialect.equals(other.databaseDialect))
			return false;
		if (driver == null) {
			if (other.driver != null)
				return false;
		} else if (!driver.equals(other.driver))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (otherAttributes == null) {
			if (other.otherAttributes != null)
				return false;
		} else if (!otherAttributes.equals(other.otherAttributes))
			return false;
		if (otherElements == null) {
			if (other.otherElements != null)
				return false;
		} else if (!otherElements.equals(other.otherElements))
			return false;
		if (pass == null) {
			if (other.pass != null)
				return false;
		} else if (!pass.equals(other.pass))
			return false;
		if (security == null) {
			if (other.security != null)
				return false;
		} else if (!security.equals(other.security))
			return false;
		if (share == null) {
			if (other.share != null)
				return false;
		} else if (!share.equals(other.share))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		if (visible == null) {
			if (other.visible != null)
				return false;
		} else if (!visible.equals(other.visible))
			return false;
		return true;
	}

    public String getDatabaseDialect() {
		return databaseDialect;
	}

	public void setDatabaseDialect(String databaseDialect) {
		this.databaseDialect = databaseDialect;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((databaseDialect == null) ? 0 : databaseDialect.hashCode());
		result = prime * result + ((driver == null) ? 0 : driver.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((otherAttributes == null) ? 0 : otherAttributes.hashCode());
		result = prime * result + ((otherElements == null) ? 0 : otherElements.hashCode());
		result = prime * result + ((pass == null) ? 0 : pass.hashCode());
		result = prime * result + ((security == null) ? 0 : security.hashCode());
		result = prime * result + ((share == null) ? 0 : share.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		result = prime * result + ((visible == null) ? 0 : visible.hashCode());
		return result;
	}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
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

}
