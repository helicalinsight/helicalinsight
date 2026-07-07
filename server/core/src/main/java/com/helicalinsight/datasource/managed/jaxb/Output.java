package com.helicalinsight.datasource.managed.jaxb;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Created by author on 9/6/2019.
 *
 * @author Rajesh
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "output")
@XmlAccessorType(XmlAccessType.FIELD)
public class Output {

    @XmlAttribute(name = "mandatory")
    private String mandatory = "true";

    @XmlAttribute(name = "viewPage")
    private String viewPage;

    @XmlAttribute(name = "showStack")
    private String showStack;

    @XmlAttribute(name = "showFlow")
    private String showFlow;

    @XmlAnyElement(lax = true)
    private List<Element> listOfElement;

    public List<Element> getListOfElement() {
        return listOfElement;
    }

    public void setListOfElement(List<Element> listOfElement) {
        this.listOfElement = listOfElement;
    }

    public String getViewPage() {
        return viewPage;
    }

    public void setViewPage(String viewPage) {
        this.viewPage = viewPage;
    }

    public String getShowStack() {
        return showStack;
    }

    public void setShowStack(String showStack) {
        this.showStack = showStack;
    }

    public String getShowFlow() {
        return showFlow;
    }

    public void setShowFlow(String showFlow) {
        this.showFlow = showFlow;
    }


}
