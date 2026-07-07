package com.helicalinsight.admin.model;

import com.helicalinsight.datasource.model.GlobalConnections;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "hi_hcr_connections_global")
@Setter
@Getter
public class HIHcrConnectionsGlobal implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "global_connection_id")
	private GlobalConnections globalConnections;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hcr_connection_id")
	private HIHcrConnections hiHcrConnections;
}
