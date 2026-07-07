package com.helicalinsight.datasource.managed.jaxb;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;

/**
 * Created by author on 9/6/2019.
 *
 * @author Rajesh
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "HWF")
@XmlAccessorType(XmlAccessType.FIELD)
public class HWF {


    @XmlAttribute
    private String mandatory = "true";

    @XmlAttribute
    private String type = "com.helicalinsight.hwf.core.api.HWFEngine";

    @XmlElement(name = "input")
    private Inputs inputs;

    @XmlElement(name = "flow")
    private Flow flow;

    @XmlElement(name = "output")
    private Output output;

    public Inputs getInputs() {
        return inputs;
    }

    public void setInputs(Inputs inputs) {
        this.inputs = inputs;
    }

    public Output getOutput() {
        return output;
    }

    public void setOutput(Output output) {
        this.output = output;
    }

    public Flow getFlow() {
        return flow;
    }

    public void setFlow(Flow flow) {
        this.flow = flow;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HWF hwf = (HWF) o;

        if (flow != null ? !flow.equals(hwf.flow) : hwf.flow != null) return false;
        if (inputs != null ? !inputs.equals(hwf.inputs) : hwf.inputs != null) return false;
        if (mandatory != null ? !mandatory.equals(hwf.mandatory) : hwf.mandatory != null) return false;
        if (output != null ? !output.equals(hwf.output) : hwf.output != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mandatory != null ? mandatory.hashCode() : 0;
        result = 31 * result + (inputs != null ? inputs.hashCode() : 0);
        result = 31 * result + (output != null ? output.hashCode() : 0);
        result = 31 * result + (flow != null ? flow.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "HWF{" +
                "mandatory='" + mandatory + '\'' +
                ", inputs=" + inputs +
                ", output=" + output +
                ", flow=" + flow +
                '}';
    }
}
