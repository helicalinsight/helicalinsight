package com.helicalinsight.admin.model;



import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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
@Table(name="hi_hcr_connections")
@Setter
@Getter
public class HIHcrConnections implements Serializable {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hcr_connection_id")
    private Integer id;
    
    @Column(name="connection_type")
    private String connectionType;
    
    @ManyToOne
    @JoinColumn(name="hcr_id")
    private HIResource hiResourceHcr;
    
	@OneToOne(mappedBy = "hiHcrConnections",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private HIHcrConnectionsEfwd hiHcrConnectionsEfwd;
	
	@OneToOne(mappedBy = "hiHcrConnections",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private HIHcrConnectionsGlobal hiHcrConnectionsGlobal;
	
	@OneToOne(mappedBy = "hiHcrConnections",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private HiHcrQuery hiHcrQuery;
}
