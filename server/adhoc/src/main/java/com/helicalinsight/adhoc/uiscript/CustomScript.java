package com.helicalinsight.adhoc.uiscript;

import com.helicalinsight.efw.utility.AdapterCDATA;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;

/**
 * Created  on 14-05-2015.
 *
 * @author Somen
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "customScript")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomScript {

    @XmlElement
    private String visible;

    @XmlElement
    private String scriptId;

    @XmlElement
    private String scriptType;

    @XmlElement
    private String name;

    @XmlElement
    private String group;

    @XmlElement
    private String subGroup;

    @XmlElement
    private String description;

    @XmlElement
    private String renderOn;


    @XmlElement
    private Parameters parameters;


    @XmlElement
    private String icon;
    @XmlJavaTypeAdapter(AdapterCDATA.class)
    private String snippet;
    @XmlJavaTypeAdapter(AdapterCDATA.class)
    private String uiSnippet;


    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    public String getScriptId() {
        return scriptId;
    }

    public void setScriptId(String scriptId) {
        this.scriptId = scriptId;
    }

    public String getScriptType() {
        return scriptType;
    }

    public void setScriptType(String scriptType) {
        this.scriptType = scriptType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getSubGroup() {
        return subGroup;
    }

    public void setSubGroup(String subGroup) {
        this.subGroup = subGroup;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRenderOn() {
        return renderOn;
    }

    public void setRenderOn(String renderOn) {
        this.renderOn = renderOn;
    }

    public Parameters getParameters() {
        return parameters;
    }

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getUiSnippet() {
        return uiSnippet;
    }

    public void setUiSnippet(String uiSnippet) {
        this.uiSnippet = uiSnippet;
    }


    @Override
    public String toString() {
        return "CustomScript{" +
                "visible='" + visible + '\'' +
                ", scriptId='" + scriptId + '\'' +
                ", scriptType='" + scriptType + '\'' +
                ", name='" + name + '\'' +
                ", group='" + group + '\'' +
                ", subGroup='" + subGroup + '\'' +
                ", description='" + description + '\'' +
                ", renderOn='" + renderOn + '\'' +
                ", parameters=" + parameters +
                ", icon='" + icon + '\'' +
                ", snippet='" + snippet + '\'' +
                ", uiSnippet='" + uiSnippet + '\'' +
                '}';
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
        private String value;

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

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Parameter parameter = (Parameter) o;

            if (column != null ? !column.equals(parameter.column) : parameter.column != null) return false;
            if (value != null ? !value.equals(parameter.value) : parameter.value != null) return false;
            if (name != null ? !name.equals(parameter.name) : parameter.name != null) return false;
            if (type != null ? !type.equals(parameter.type) : parameter.type != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (type != null ? type.hashCode() : 0);
            result = 31 * result + (column != null ? column.hashCode() : 0);
            result = 31 * result + (value != null ? value.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Parameter{" +
                    "name='" + name + '\'' +
                    ", type='" + type + '\'' +
                    ", column='" + column + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }
}