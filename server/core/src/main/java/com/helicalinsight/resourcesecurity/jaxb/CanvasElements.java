package com.helicalinsight.resourcesecurity.jaxb;

import com.google.gson.JsonArray;
import com.helicalinsight.efw.utility.AdapterCDATA;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Created by author on 20-03-2015.
 *
 * @author Rajasekhar
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "canvas")
@XmlAccessorType(XmlAccessType.FIELD)
public class CanvasElements {

    @XmlJavaTypeAdapter(AdapterCDATA.class)
    private JsonArray columns;

    @XmlJavaTypeAdapter(AdapterCDATA.class)
    private String groups;

    public CanvasElements() {
    }

    public CanvasElements(JsonArray columns, String groups) {
        this.columns = columns;
        this.groups = groups;
    }

    public JsonArray getColumns() {
        return columns;
    }

    public void setColumns(JsonArray columns) {
        this.columns = columns;
    }

    public String getGroups() {
        return groups;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        CanvasElements that = (CanvasElements) object;

        if (columns != null ? !columns.equals(that.columns) : that.columns != null) return false;
        if (groups != null ? !groups.equals(that.groups) : that.groups != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = columns != null ? columns.hashCode() : 0;
        result = 31 * result + (groups != null ? groups.hashCode() : 0);
        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "CanvasElements{" +
                "columns='" + columns + '\'' +
                ", groups='" + groups + '\'' +
                '}';
    }
}
