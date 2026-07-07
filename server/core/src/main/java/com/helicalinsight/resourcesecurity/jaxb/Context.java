package com.helicalinsight.resourcesecurity.jaxb;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Created by author on 30-07-2015.
 *
 * @author Rajasekhar
 */
@SuppressWarnings("unused")
@Component
@Scope("prototype")
@XmlRootElement(name = "context")
@XmlAccessorType(XmlAccessType.FIELD)
public class Context {

    @XmlAttribute
    private String name;

    @XmlAttribute
    private Integer permission;

    @XmlAttribute
    private String parameters;

    @XmlElement
    private LookupParameters lookupParameters;

    @XmlElement
    private DecisionState decisionState;

    @XmlElement(name = "subContext")
    private List<SubContext> subContexts;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPermission() {
        return permission;
    }

    public void setPermission(Integer permission) {
        this.permission = permission;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public LookupParameters getLookupParameters() {
        return lookupParameters;
    }

    public void setLookupParameters(LookupParameters lookupParameters) {
        this.lookupParameters = lookupParameters;
    }

    public DecisionState getDecisionState() {
        return decisionState;
    }

    public void setDecisionState(DecisionState decisionState) {
        this.decisionState = decisionState;
    }

    public List<SubContext> getSubContexts() {
        return subContexts;
    }

    public void setSubContexts(List<SubContext> subContexts) {
        this.subContexts = subContexts;
    }

    @Override
    public String toString() {
        return "Context{" +
                "name='" + name + '\'' +
                ", permission=" + permission +
                ", parameters='" + parameters + '\'' +
                ", lookupParameters=" + lookupParameters +
                ", decisionState=" + decisionState +
                ", subContexts=" + subContexts +
                '}';
    }
}

