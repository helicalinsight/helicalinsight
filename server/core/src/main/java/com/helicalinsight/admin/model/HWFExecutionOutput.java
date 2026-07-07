package com.helicalinsight.admin.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="hwf_execution_output")

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HWFExecutionOutput implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="output_ref")
    private String outputRef;

    @Column(name="output_ref_for")
    private String outputRefFor;

    @Column(name="output_ref_for_value")
    private String outputRefForValue;

    @ManyToOne
    @JoinColumn(name = "id",nullable = false,insertable = false,updatable = false)
    private HWFExecution hwfExecution;

    public String getOutputRef() {
        return outputRef;
    }

    public void setOutputRef(String outputRef) {
        this.outputRef = outputRef;
    }

    public String getOutputRefFor() {
        return outputRefFor;
    }

    public void setOutputRefFor(String outputRefFor) {
        this.outputRefFor = outputRefFor;
    }

    public String getOutputRefForValue() {
        return outputRefForValue;
    }

    public void setOutputRefForValue(String outputRefForValue) {
        this.outputRefForValue = outputRefForValue;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public HWFExecution getHwfExecution() {
        return hwfExecution;
    }

    public void setHwfExecution(HWFExecution hwfExecution) {
        this.hwfExecution = hwfExecution;
    }
}
