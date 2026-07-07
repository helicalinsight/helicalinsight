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
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToOne
	@JoinColumn(name = "hi_resource_db_id")
	private HIResource hiResource;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hi_recycle_bin_id")
	private HIRecycleBin recycleBin;
	
}
