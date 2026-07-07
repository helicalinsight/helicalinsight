package com.helicalinsight.admin.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="hwf_execution_input")

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HWFExecutionInput implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="input_ref")
    private String inputRef;

    @Column(name="input_ref_for")
    private String inputRefFor;

    @Column(name="input_ref_for_value")
    private String inputRefForValue;

    @Column(name="input_type")
    private String inputType;

    @ManyToOne
    @JoinColumn(name = "id",nullable = false,insertable = false,updatable = false)
    private HWFExecution hwfExecution;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getInputRef() {
        return inputRef;
    }

    public void setInputRef(String inputRef) {
        this.inputRef = inputRef;
    }

    public String getInputRefFor() {
        return inputRefFor;
    }

    public void setInputRefFor(String inputRefFor) {
        this.inputRefFor = inputRefFor;
    }

    public String getInputRefForValue() {
        return inputRefForValue;
    }

    public void setInputRefForValue(String inputRefForValue) {
        this.inputRefForValue = inputRefForValue;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public HWFExecution getHwfExecution() {
        return hwfExecution;
    }

    public void setHwfExecution(HWFExecution hwfExecution) {
        this.hwfExecution = hwfExecution;
    }
}
