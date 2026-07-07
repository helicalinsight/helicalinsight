package com.helicalinsight.admin.model;


import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="hi_hwf_execution")

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HWFExecution implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="flow_execution_alias")
    private String flowExecutionAlias;

    @Column(name="flow_execution_type")
    private String flowExecutionType;

    @Column(name="output")
    private String output;

    @Column(name="flow_id")
    private Integer flowId;

    @OneToOne
    private HIResourceHWF hiResourceHWF;

    @OneToMany(fetch = FetchType.LAZY)
    private List<HWFExecutionInput> hwfExecutionInput;

    @OneToMany(fetch = FetchType.LAZY)
    private List<HWFExecutionOutput> hwfExecutionOutput;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFlowExecutionAlias() {
        return flowExecutionAlias;
    }

    public void setFlowExecutionAlias(String flowExecutionAlias) {
        this.flowExecutionAlias = flowExecutionAlias;
    }

    public String getFlowExecutionType() {
        return flowExecutionType;
    }

    public void setFlowExecutionType(String flowExecutionType) {
        this.flowExecutionType = flowExecutionType;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public Integer getFlowId() {
        return flowId;
    }

    public void setFlowId(Integer flowId) {
        this.flowId = flowId;
    }

    public List<HWFExecutionInput> getHwfExecutionInput() {
        return hwfExecutionInput;
    }

    public void setHwfExecutionInput(List<HWFExecutionInput> hwfExecutionInput) {
        this.hwfExecutionInput = hwfExecutionInput;
    }

    public List<HWFExecutionOutput> getHwfExecutionOutput() {
        return hwfExecutionOutput;
    }

    public void setHwfExecutionOutput(List<HWFExecutionOutput> hwfExecutionOutput) {
        this.hwfExecutionOutput = hwfExecutionOutput;
    }

    public HIResourceHWF getHiResourceHWF() {
        return hiResourceHWF;
    }

    public void setHiResourceHWF(HIResourceHWF hiResourceHWF) {
        this.hiResourceHWF = hiResourceHWF;
    }
}
