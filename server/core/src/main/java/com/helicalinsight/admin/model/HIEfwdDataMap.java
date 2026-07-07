package com.helicalinsight.admin.model;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name="hi_efwd_datamap")

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HIEfwdDataMap implements Serializable {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name="datamap_id")
    private String datamapId;

    public List<HIEfwdDatamapParameters> getDataMapParams() {
		return dataMapParams;
	}

	public void setDataMapParams(List<HIEfwdDatamapParameters> dataMapParams) {
		this.dataMapParams = dataMapParams;
	}

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="efwd_connection_id",referencedColumnName = "id",nullable = false)
    private HIEfwdConnection hiEfwdConnection;
    
    @OneToMany(mappedBy = "datamap", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @BatchSize(size = 20)
    private List<HIEfwdDatamapParameters> dataMapParams = new ArrayList<>();

    @Column(name="datamap_type")
    private String datamapType;

    @Column(name="datamap_name")
    private String datamapName;

    @Column(name="datamap_query", length = Integer.MAX_VALUE)
    @Lob
    private String datamapQuery;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDatamapId() {
        return datamapId;
    }

    public void setDatamapId(String datamapId) {
        this.datamapId = datamapId;
    }

    public HIEfwdConnection getHiEfwdConnection() {
        return hiEfwdConnection;
    }

    public void setHiEfwdConnection(HIEfwdConnection hiEfwdConnection) {
        this.hiEfwdConnection = hiEfwdConnection;
    }

    public String getDatamapType() {
        return datamapType;
    }

    public void setDatamapType(String datamapType) {
        this.datamapType = datamapType;
    }

    public String getDatamapName() {
        return datamapName;
    }

    public void setDatamapName(String datamapName) {
        this.datamapName = datamapName;
    }

    public String getDatamapQuery() {
        return datamapQuery;
    }

    public void setDatamapQuery(String datamapQuery) {
        this.datamapQuery = datamapQuery;
    }
}
