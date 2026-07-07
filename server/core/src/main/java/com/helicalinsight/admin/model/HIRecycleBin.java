package com.helicalinsight.admin.model;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.*;
import static jakarta.persistence.CascadeType.*;

import com.helicalinsight.admin.enums.RecycleBinType;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CacheConcurrencyStrategy;


@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Entity
@Table(name = "hi_recycle_bin",indexes = @Index(name="bin_idx",columnList = "created_by,org_id"))

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HIRecycleBin implements Serializable {
	
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "recycle_bin_type")
	@EqualsAndHashCode.Include
	private RecycleBinType recycleBinType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "deleted_by")
	private User deletedBy;
	
	@Column(name = "deleted_on")
	private Date deletedOn;
	
	/**
	 * Added this column to improve the performance 
	 * of Recyclebin list api service call
	 */
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "org_id")
	private Organization orgId;
	
	/**
	 * Added this column to improve the performance 
	 * of Recyclebin list api service call
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by")
	private User createdBy;
	
	@OneToOne(mappedBy = "recycleBin",cascade = { PERSIST, MERGE, REFRESH, DETACH},fetch = FetchType.LAZY)
	private HIRecycleBinHIResourceDB hiRecycleBinHIResourceDB;
	
	@OneToOne(mappedBy = "recycleBin",cascade = { PERSIST, MERGE, REFRESH, DETACH},fetch = FetchType.LAZY)
	private HIRecycleBinDSGlobalConnections hiRecycleBinDsGlobalConnections;
	
	@OneToOne(mappedBy = "recycleBin",cascade = { PERSIST, MERGE, REFRESH, DETACH},fetch = FetchType.LAZY)
	private HIRecycleBinHUsers hiRecycleBinHUsers;
	
	@OneToOne(mappedBy = "recycleBin",cascade = { PERSIST, MERGE, REFRESH, DETACH},fetch = FetchType.LAZY)
	private HIRecycleBinOrganization hiRecycleBinOrganization;
	
	@OneToOne(mappedBy = "recycleBin",cascade = { PERSIST, MERGE, REFRESH, DETACH},fetch = FetchType.LAZY)
	private HIRecycleBinHIEfwdConnection hiRecycleBinHIEfwdConnection;
	
}
