package com.helicalinsight.admin.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name="hi_hcr_query_parameters")
@Setter
@Getter
public class HIHcrQueryParameters implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="query_param_id")
	private Integer id;
	
	@Column(name="parameter_name")
	private String parameterName;
	
	@Column(name="parameter_type")
	private String parameterType;
	
	@Column(name="parameter_default_value")
	private String paramDefaultValue;

	@Column(name="open_quotes")
	private String openQuotes;

	@Column(name="close_quotes")
	private String closeQuotes;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="hcr_query_id")
	private HiHcrQuery hiQuery;
}
