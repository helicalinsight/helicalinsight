package com.helicalinsight.admin.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Table(name = "hi_resource_mapping")
@Entity
public class HIResourceMapping implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HIResourceMapping() {
		// NOOP 
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_resource_id", nullable = false)
	private HIResource parentResource;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "child_resource_id", nullable = false)
	private HIResource childResource;
	

	public Long getId() {
		return id;
	}
 
	public void setId(Long id) {
		this.id = id;
	}

	public HIResource getParentResource() {
		return parentResource;
	}

	public void setParentResource(HIResource parentResource) {
		this.parentResource = parentResource;
	}

	public HIResource getChildResource() {
		return childResource;
	}

	public void setChildResource(HIResource childResource) {
		this.childResource = childResource;
	}
	
}
