package com.helicalinsight.resourcesecurity.jaxb;

import com.helicalinsight.efw.utility.AdapterCDATA;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
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

    @XmlElement(name = "script")

    private Script requiredScript;

    @Override
    public String toString() {
        return "DecisionState{" +
                "parameter='" + parameter + '\'' +
                ", defaultValue='" + defaultValue + '\'' +
                ", defaultPermission=" + defaultPermission +
                ", logicalIfList=" + logicalIfList +
                ", requiredScript=" + requiredScript +
                '}';
    }

    public Script getRequiredScript() {
        return requiredScript;
    }

    public void setRequiredScript(Script requiredScript) {
        this.requiredScript = requiredScript;
    }

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

    @Component
    @Scope("prototype")
    @XmlRootElement(name = "if")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class LogicalIf {

        @XmlAttribute
        private String parameterValue;

        @XmlAttribute
        private String hasKey;

        @Override
        public String toString() {
            return "LogicalIf{" +
                    "parameterValue='" + parameterValue + '\'' +
                    ", hasKey='" + hasKey + '\'' +
                    ", permission=" + permission +
                    ", testOperator='" + testOperator + '\'' +
                    ", decisionState=" + decisionState +
                    '}';
        }

        public String getHasKey() {
            return hasKey;
        }

        public void setHasKey(String hasKey) {
            this.hasKey = hasKey;
        }

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

    }




    @Component
    @Scope("prototype")
    @XmlRootElement(name = "script")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Script {

        @XmlAttribute
        private String language;

        @Override
        public String toString() {
            return "Script{" +
                    "language='" + language + '\'' +
                    ", code='" + code + '\'' +
                    '}';
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        @XmlValue
        @XmlJavaTypeAdapter(AdapterCDATA.class)
        private String code;

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

    }
}