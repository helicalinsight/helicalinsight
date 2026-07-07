package com.helicalinsight.admin.model;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;

@Data
@Entity
@Table(name = "hi_recycle_bin_hi_efwd_connection")

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HIRecycleBinHIEfwdConnection implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToOne
	@JoinColumn(name = "hi_efwd_connection_id")
	@JsonBackReference("efwdConnection")
	private HIEfwdConnection efwdConnection;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hi_recycle_bin_id")
	private HIRecycleBin recycleBin;

}
