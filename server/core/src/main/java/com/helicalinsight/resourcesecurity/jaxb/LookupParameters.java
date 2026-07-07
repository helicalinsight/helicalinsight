package com.helicalinsight.resourcesecurity.jaxb;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;

@SuppressWarnings("unused")
@Component
@Scope("prototype")
@XmlRootElement(name = "lookupParameters")
@XmlAccessorType(XmlAccessType.FIELD)
public class LookupParameters {

    @XmlAttribute
    private String type;

    @XmlElement
    private String directory;

    @XmlElement
    private String file;

    @XmlElement
    private Parameter parameter;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Parameter getParameter() {
        return parameter;
    }

    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }

    @Override
    public String toString() {
        return "LookupParameters{" +
                "type='" + type + '\'' +
                ", directory='" + directory + '\'' +
                ", file='" + file + '\'' +
                ", parameter=" + parameter +
                '}';
    }

    @Component
    @Scope("prototype")
    @XmlRootElement(name = "parameter")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Parameter {

        @XmlAttribute
        private String dataType;

        @XmlValue
        private String parameter;

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public String getParameter() {
            return parameter;
        }

        public void setParameter(String parameter) {
            this.parameter = parameter;
        }

        @Override
        public String toString() {
            return "Parameter{" +
                    "dataType='" + dataType + '\'' +
                    ", parameter='" + parameter + '\'' +
                    '}';
        }
    }
}