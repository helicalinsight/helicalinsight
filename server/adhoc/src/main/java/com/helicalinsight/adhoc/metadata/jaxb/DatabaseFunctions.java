package com.helicalinsight.adhoc.metadata.jaxb;

import com.helicalinsight.efw.utility.AdapterCDATA;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;

/**
 * Created by author on 10/8/2015.
 *
 * @author Rajasekhar
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "functions")
@XmlAccessorType(XmlAccessType.FIELD)
@SuppressWarnings("unused")
public class DatabaseFunctions {

    @XmlAttribute
    private String dialect;
    @XmlAttribute
    private String type;
    @XmlElement(name = "function")
    private List<DbFunction> dbFunctions;

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<DbFunction> getDbFunctions() {
        return dbFunctions;
    }

    public void setDbFunctions(List<DbFunction> dbFunctions) {
        this.dbFunctions = dbFunctions;
    }

    @Override
    public String toString() {
        return "DatabaseFunctions{" +
                "dialect='" + dialect + '\'' +
                ", type='" + type + '\'' +
                ", dbFunctions=" + dbFunctions +
                '}';
    }

    @Component
    @Scope("prototype")
    @XmlRootElement(name = "function")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class DbFunction {
        @Override
        public String toString() {
            return "DbFunction{" +
                    "group='" + group + '\'' +
                    ", name='" + name + '\'' +
                    ", id='" + id + '\'' +
                    ", returns='" + returns + '\'' +
                    ", key='" + key + '\'' +
                    ", value='" + value + '\'' +
                    ", signature='" + signature + '\'' +
                    ", description='" + description + '\'' +
                    ", parameters=" + parameters +
                    '}';
        }

        @XmlAttribute
        private String group;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        @XmlAttribute
        private String name;
        @XmlAttribute
        private String id;
        @XmlAttribute
        private String returns;

        public String getReturns() {
            return returns;
        }

        public void setReturns(String returns) {
            this.returns = returns;
        }

        @XmlElement
        private String key;

        @XmlElement
        private String value;

        @XmlElement
        @XmlJavaTypeAdapter(AdapterCDATA.class)
        private String signature;

        @XmlElement
        private String description;

        @XmlElement
        private Parameters parameters;

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public Parameters getParameters() {
            return parameters;
        }

        public void setParameters(Parameters parameters) {
            this.parameters = parameters;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

    }

    @Component
    @Scope("prototype")
    @XmlRootElement(name = "parameters")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Parameters {

        @XmlElement(name = "parameter")
        private List<Parameter> parameterList;

        public List<Parameter> getParameterList() {
            return parameterList;
        }

        public void setParameterList(List<Parameter> parameterList) {
            this.parameterList = parameterList;
        }

        @Override
        public String toString() {
            return "Parameters{" +
                    "parameterList=" + parameterList +
                    '}';
        }
    }

    @Component
    @Scope("prototype")
    @XmlRootElement(name = "parameter")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Parameter {

        @XmlAttribute
        private String name;

        @XmlAttribute
        private String type;

        @XmlAttribute
        private String column;

        @XmlAttribute
        private String defaultValue;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getColumn() {
            return column;
        }

        public void setColumn(String column) {
            this.column = column;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        @Override
        public String toString() {
            return "Parameter{" +
                    "name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    ", column='" + column + '\'' +
                    ", defaultValue='" + defaultValue + '\'' +
                    '}';
        }
    }
}