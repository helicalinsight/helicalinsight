package com.helicalinsight.admin.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name="hi_efwd_datamap_paramaters")

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HIEfwdDatamapParameters implements Serializable {
  
	
	private static final long serialVersionUID = 1L;
	

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="efwd_datamap_id",nullable = false)
    private HIEfwdDataMap datamap;

    @Column(name="parameter_name")
    private String parameterName;

    @Column(name="parameter_type")
    private String parameterType;

    @Column(name="parameter_default")
    private String paramterDefault;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public HIEfwdDataMap getDatamap() {
        return datamap;
    }

    public void setDatamap(HIEfwdDataMap datamap) {
        this.datamap = datamap;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getParameterType() {
        return parameterType;
    }

    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }

    public String getParamterDefault() {
        return paramterDefault;
    }

    public void setParamterDefault(String paramterDefault) {
        this.paramterDefault = paramterDefault;
    }
}
