package com.helicalinsight.admin.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="hwf_execution_conditional")

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class HWFExecutionConditional implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="steps_expression", length = Integer.MAX_VALUE)
    @Lob
    private String stepsExpression;

    @Column(name="steps_expression_type")
    private String stepsExpressionType;

    @Column(name="action_if_true")
    private String actionIfTrue;

    @Column(name="action_if_false")
    private String actionIfFalse;

    @OneToOne
    private HWFExecution hwfExecution;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStepsExpression() {
        return stepsExpression;
    }

    public void setStepsExpression(String stepsExpression) {
        this.stepsExpression = stepsExpression;
    }

    public String getStepsExpressionType() {
        return stepsExpressionType;
    }

    public void setStepsExpressionType(String stepsExpressionType) {
        this.stepsExpressionType = stepsExpressionType;
    }

    public String getActionIfTrue() {
        return actionIfTrue;
    }

    public void setActionIfTrue(String actionIfTrue) {
        this.actionIfTrue = actionIfTrue;
    }

    public String getActionIfFalse() {
        return actionIfFalse;
    }

    public void setActionIfFalse(String actionIfFalse) {
        this.actionIfFalse = actionIfFalse;
    }

    @Override
    public String toString() {
        return "HWFExecutionConditional{" +
                "id=" + id +
                ", stepsExpression='" + stepsExpression + '\'' +
                ", stepsExpressionType='" + stepsExpressionType + '\'' +
                ", actionIfTrue='" + actionIfTrue + '\'' +
                ", actionIfFalse='" + actionIfFalse + '\'' +
                '}';
    }
}
