/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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

