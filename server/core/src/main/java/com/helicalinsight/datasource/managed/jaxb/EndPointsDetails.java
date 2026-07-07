package com.helicalinsight.datasource.managed.jaxb;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;

@Component
@Scope("prototype")

@XmlRootElement(name = "endPoints")
@XmlAccessorType(XmlAccessType.FIELD)
public class EndPointsDetails {

    @XmlAttribute
    private String endPointManager;

    @XmlElement(name = "query")
    private EndPoints queryDetails;

    @XmlElement(name = "storage")
    private EndPoints storageDetails;

    @XmlElement(name = "threads")
    private EndPoints threadsDetails;

    @XmlElement(name = "options")
    private EndPoints optionsDetails;

    public String getEndPointManager() {
        return endPointManager;
    }

    public void setEndPointManager(String endPointManager) {
        this.endPointManager = endPointManager;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EndPointsDetails that = (EndPointsDetails) o;

        if (endPointManager != null ? !endPointManager.equals(that.endPointManager) : that.endPointManager != null)
            return false;
        if (optionsDetails != null ? !optionsDetails.equals(that.optionsDetails) : that.optionsDetails != null)
            return false;
        if (queryDetails != null ? !queryDetails.equals(that.queryDetails) : that.queryDetails != null) return false;
        if (storageDetails != null ? !storageDetails.equals(that.storageDetails) : that.storageDetails != null)
            return false;
        if (threadsDetails != null ? !threadsDetails.equals(that.threadsDetails) : that.threadsDetails != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = endPointManager != null ? endPointManager.hashCode() : 0;
        result = 31 * result + (queryDetails != null ? queryDetails.hashCode() : 0);
        result = 31 * result + (storageDetails != null ? storageDetails.hashCode() : 0);
        result = 31 * result + (threadsDetails != null ? threadsDetails.hashCode() : 0);
        result = 31 * result + (optionsDetails != null ? optionsDetails.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EndPointsDetails{" +
                "endPointManager='" + endPointManager + '\'' +
                ", queryDetails=" + queryDetails +
                ", storageDetails=" + storageDetails +
                ", threadsDetails=" + threadsDetails +
                ", optionsDetails=" + optionsDetails +
                '}';
    }

    public EndPoints getQueryDetails() {
        return queryDetails;
    }

    public void setQueryDetails(EndPoints queryDetails) {
        this.queryDetails = queryDetails;
    }

    public EndPoints getStorageDetails() {
        return storageDetails;
    }

    public void setStorageDetails(EndPoints storageDetails) {
        this.storageDetails = storageDetails;
    }

    public EndPoints getThreadsDetails() {
        return threadsDetails;
    }

    public void setThreadsDetails(EndPoints threadsDetails) {
        this.threadsDetails = threadsDetails;
    }

    public EndPoints getOptionsDetails() {
        return optionsDetails;
    }

    public void setOptionsDetails(EndPoints optionsDetails) {
        this.optionsDetails = optionsDetails;
    }
}
