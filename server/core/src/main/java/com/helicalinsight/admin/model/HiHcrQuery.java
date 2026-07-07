package com.helicalinsight.admin.model;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="hi_hcr_query")
@Setter
@Getter
public class HiHcrQuery implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="hcr_query_id")
	private Integer id;
	
	@Column(name="hcr_query_name")
	private String hcrQueryName;
	
	@Column(name="hcr_query_string", length = Integer.MAX_VALUE)
	@Lob
	private String hcrQueryString;
	
	@Column(name="hcr_query_type")
	private String hcrQueryType;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="hcr_connection_id")
	private HIHcrConnections hiHcrConnections;
	
	@OneToMany(mappedBy = "hiQuery",cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
	private List<HIHcrQueryParameters> hiHcrQueryParameters;
}
