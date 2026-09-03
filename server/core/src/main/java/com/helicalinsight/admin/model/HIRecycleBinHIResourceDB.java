package com.helicalinsight.admin.model;

import jakarta.persistence.*;

import lombok.Data;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;


@Data
@Entity
@Table(name = "hi_recycle_bin_hi_resource_db")

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HIRecycleBinHIResourceDB implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hi_resource_db_id")
	private HIResource hiResource;
	
	/**
	 * Added this  read-only column to improve the performance.
	 */
	@Column(
		    name = "hi_resource_db_id",
		    insertable = false,
		    updatable = false
		)
	private Integer hiResourceId;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hi_recycle_bin_id")
	private HIRecycleBin recycleBin;
	
}
