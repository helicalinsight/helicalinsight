package com.helicalinsight.admin.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name = "hi_resource_phases_status")
public class HIResourcePhaseStatus implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name = "hi_phase_id")
	private HIPhase hiPhase;
	@OneToOne(cascade = {CascadeType.PERSIST,CascadeType.REFRESH})
	@JoinColumn(name = "hi_resource_db_id")
	private HIResource hiResource;
	@Column(name = "last_updated_date")
	private Date lastUpdatedDate;
	
	@ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.REFRESH})
	@JoinColumn(name = "updated_by")
	private User user;
	private String action;

	@ElementCollection(fetch = FetchType.EAGER)
	    @CollectionTable(
	            name = "metadata_dumped_dbs",
	            joinColumns=@JoinColumn(name = "phase_details_id", referencedColumnName = "id")
	        )
	    private List<String> dbName = new ArrayList<>();

}
