package com.helicalinsight.resourcesecurity.jaxb;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;

/**
 * Created by author on 30-07-2015.
 *
 * @author Rajasekhar
 */
@SuppressWarnings("unused")
@Component
@Scope("prototype")
@XmlRootElement(name = "subContext")
@XmlAccessorType(XmlAccessType.FIELD)
public class SubContext {

    @XmlAttribute
    private Integer permission;

    @XmlAttribute
    private String parameters;

    @XmlAttribute
    private String isDataSource;

    public String getLookupKey() {
        return lookupKey;
    }

    public void setLookupKey(String lookupKey) {
        this.lookupKey = lookupKey;
    }

    @Override
    public String toString() {
        return "SubContext{" +
                "permission=" + permission +
                ", parameters='" + parameters + '\'' +
                ", isDataSource='" + isDataSource + '\'' +
                ", lookupKey='" + lookupKey + '\'' +
                ", type='" + type + '\'' +
                ", serviceType='" + serviceType + '\'' +
                ", service='" + service + '\'' +
                ", formData=" + formData +
                ", decisionState=" + decisionState +
                '}';
    }

    @XmlAttribute
    private String lookupKey;

    public String getIsDataSource() {
        return isDataSource;
    }

    public void setIsDataSource(String isDataSource) {
        this.isDataSource = isDataSource;
    }

    @XmlElement
    private String type;

    @XmlElement
    private String serviceType;

    @XmlElement
    private String service;

    @XmlElement
    private FormData formData;


    @XmlElement
    private DecisionState decisionState;

    public DecisionState getDecisionState() {
        return decisionState;
    }

    public void setDecisionState(DecisionState decisionState) {
        this.decisionState = decisionState;
    }

    public Integer getPermission() {
        return permission;
    }

    public void setPermission(Integer permission) {
        this.permission = permission;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public FormData getFormData() {
        return formData;
    }

    public void setFormData(FormData formData) {
        this.formData = formData;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    @Component
    @Scope("prototype")
    @XmlRootElement(name = "formData")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class FormData {

        @XmlElement
        private Directory directory;

        @XmlElement
        private File file;

        public Directory getDirectory() {
            return directory;
        }

        public void setDirectory(Directory directory) {
            this.directory = directory;
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        @Override
        public String toString() {
            return "FormData{" +
                    "directory=" + directory +
                    ", file=" + file +
                    '}';
        }
    }

    @Component
    @Scope("prototype")
    @XmlRootElement(name = "directory")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Directory {

        @XmlAttribute
        private String optional;

        @XmlValue
        private String directory;

        public String getOptional() {
            return optional;
        }

        public void setOptional(String optional) {
            this.optional = optional;
        }

        public String getDirectory() {
            return directory;
        }

        public void setDirectory(String directory) {
            this.directory = directory;
        }

        @Override
        public String toString() {
            return "Directory{" +
                    "optional='" + optional + '\'' +
                    ", directory='" + directory + '\'' +
                    '}';
        }
    }

    @Component
    @Scope("prototype")
    @XmlRootElement(name = "file")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class File {

        @XmlAttribute
        private String optional;

        @XmlValue
        private String file;


        @XmlAttribute
        private String defaultExtension;

        @Override
        public String toString() {
            return "File{" +
                    "optional='" + optional + '\'' +
                    ", file='" + file + '\'' +
                    ", defaultExtension='" + defaultExtension + '\'' +
                    '}';
        }

        public String getDefaultExtension() {
            return defaultExtension;
        }

        public void setDefaultExtension(String defaultExtension) {
            this.defaultExtension = defaultExtension;
        }

        public String getOptional() {
            return optional;
        }

        public void setOptional(String optional) {
            this.optional = optional;
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

    }
}
