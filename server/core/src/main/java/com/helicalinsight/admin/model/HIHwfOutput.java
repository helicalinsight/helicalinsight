package com.helicalinsight.admin.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "hi_hwf_output")

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HIHwfOutput implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="ouput_view_page")
    private String outputViewPage;

    @Column(name="ouput_show_stack")
    private String outputShowStack;

    @Column(name="ouput_show_flow")
    private String outputShowFlow;

    @Column(name="ouput_type")
    private String outputType;

    @Column(name="ouput_value")
    private String outputValue;

    @OneToOne
    private HIResourceHWF hwf;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOutputViewPage() {
        return outputViewPage;
    }

    public void setOutputViewPage(String outputViewPage) {
        this.outputViewPage = outputViewPage;
    }

    public String getOutputShowStack() {
        return outputShowStack;
    }

    public void setOutputShowStack(String outputShowStack) {
        this.outputShowStack = outputShowStack;
    }

    public String getOutputShowFlow() {
        return outputShowFlow;
    }

    public void setOutputShowFlow(String outputShowFlow) {
        this.outputShowFlow = outputShowFlow;
    }

    public String getOutputType() {
        return outputType;
    }

    public void setOutputType(String outputType) {
        this.outputType = outputType;
    }

    public String getOutputValue() {
        return outputValue;
    }

    public void setOutputValue(String outputValue) {
        this.outputValue = outputValue;
    }

    public HIResourceHWF getHwf() {
        return hwf;
    }

    public void setHwf(HIResourceHWF hwf) {
        this.hwf = hwf;
    }
}
