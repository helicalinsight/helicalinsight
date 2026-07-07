package com.helicalinsight.admin.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name="hwf_execution_groovy")

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HWFExecutionGroovy implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="code", length = Integer.MAX_VALUE)
    @Lob
    private String code;

    @ManyToOne
    private HWFExecution hwfExecution;

    @Column(name="action_next")
    private String actionNext;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public HWFExecution getHwfExecution() {
        return hwfExecution;
    }

    public void setHwfExecution(HWFExecution hwfExecution) {
        this.hwfExecution = hwfExecution;
    }

    public String getActionNext() {
        return actionNext;
    }

    public void setActionNext(String actionNext) {
        this.actionNext = actionNext;
    }
}
