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

@SuppressWarnings("unused")
@Component
@Scope("prototype")
@XmlRootElement(name = "decisionState")
@XmlAccessorType(XmlAccessType.FIELD)
public class DecisionState {

    @XmlAttribute
    private String parameter;

    @XmlAttribute
    private String defaultValue;

    @XmlAttribute
    private Integer defaultPermission;

    @XmlElement(name = "if")
    private List<LogicalIf> logicalIfList;

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public List<LogicalIf> getLogicalIfList() {
        return logicalIfList;
    }

    public void setLogicalIfList(List<LogicalIf> logicalIfList) {
        this.logicalIfList = logicalIfList;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Integer getDefaultPermission() {
        return defaultPermission;
    }

    public void setDefaultPermission(Integer defaultPermission) {
        this.defaultPermission = defaultPermission;
    }

    @Override
    public String toString() {
        return "DecisionState{" +
                "parameter='" + parameter + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                ", defaultPermission='" + defaultPermission + '\'' +
                ", logicalIfList=" + logicalIfList +
                '}';
    }

    @Component
    @Scope("prototype")
    @XmlRootElement(name = "if")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class LogicalIf {

        @XmlAttribute
        private String parameterValue;

        @XmlAttribute
        private Integer permission;

        @XmlAttribute
        private String testOperator;

        @XmlElement
        private DecisionState decisionState;

        public String getParameterValue() {
            return parameterValue;
        }

        public void setParameterValue(String parameterValue) {
            this.parameterValue = parameterValue;
        }

        public Integer getPermission() {
            return permission;
        }

        public void setPermission(Integer permission) {
            this.permission = permission;
        }

        public String getTestOperator() {
            return testOperator;
        }

        public void setTestOperator(String testOperator) {
            this.testOperator = testOperator;
        }

        public DecisionState getDecisionState() {
            return decisionState;
        }

        public void setDecisionState(DecisionState decisionState) {
            this.decisionState = decisionState;
        }

        @Override
        public String toString() {
            return "LogicalIf{" +
                    "parameterValue='" + parameterValue + '\'' +
                    ", permission=" + permission +
                    ", testOperator='" + testOperator + '\'' +
                    ", decisionState=" + decisionState +
                    '}';
        }
    }
}