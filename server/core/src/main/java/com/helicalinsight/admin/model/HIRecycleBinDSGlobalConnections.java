package com.helicalinsight.admin.model;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.helicalinsight.datasource.model.GlobalConnections;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;

@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "hi_recycle_bin_ds_global_connections")

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HIRecycleBinDSGlobalConnections implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ds_global_connection_id")
	private GlobalConnections globalConnection;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hi_recycle_bin_id")
	@JsonBackReference
	private HIRecycleBin recycleBin;
}
