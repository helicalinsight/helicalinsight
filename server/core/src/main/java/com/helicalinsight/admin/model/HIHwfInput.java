package com.helicalinsight.admin.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "hi_hwf_input")

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HIHwfInput implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="input_type")
    private String inputType;

    @Column(name="input_value")
    private String inputValue;

    @Column(name="input_default")
    private String inputDefault;

    @OneToOne
    private HIResourceHWF hiResourceHWF;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getInputValue() {
        return inputValue;
    }

    public void setInputValue(String inputValue) {
        this.inputValue = inputValue;
    }

    public String getInputDefault() {
        return inputDefault;
    }

    public void setInputDefault(String inputDefault) {
        this.inputDefault = inputDefault;
    }

    public HIResourceHWF getHiResourceHWF() {
        return hiResourceHWF;
    }

    public void setHiResourceHWF(HIResourceHWF hiResourceHWF) {
        this.hiResourceHWF = hiResourceHWF;
    }
}
