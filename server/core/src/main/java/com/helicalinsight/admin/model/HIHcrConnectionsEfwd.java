package com.helicalinsight.admin.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "hi_hcr_connections_efwd", indexes = {
		@Index(name = "idx_hcr_conn_efwd_conn_id", columnList = "efwd_connection_id")
})
@BatchSize(size = 20)
@DynamicUpdate
@Setter
@Getter
public class HIHcrConnectionsEfwd implements Serializable {
	
	
	private static final long serialVersionUID = 1L;

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="efwd_connection_id")
	private HIEfwdConnection hiEfwdConnection;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="hcr_connection_id")
	private HIHcrConnections hiHcrConnections;
}
