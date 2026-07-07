package com.helicalinsight.datasource.managed.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")

@XmlRootElement(name = "storage")
@XmlAccessorType(XmlAccessType.FIELD)
public class StorageLocation {

	@XmlAttribute
    String name;
	
	@XmlAttribute
    String path;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String toString() {
		return "StorageLocation [name=" + name + ", path=" + path + "]";
	}
	
}
