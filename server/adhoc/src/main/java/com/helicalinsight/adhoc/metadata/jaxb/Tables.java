package com.helicalinsight.adhoc.metadata.jaxb;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by author on 27-02-2015.
 *
 * @author Rajasekhar
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "tables")
@XmlAccessorType(XmlAccessType.FIELD)
public class Tables {

    @XmlElement(name = "table")
    private List<Table> tableList;

    public Tables() {
    }

    public Tables(List<Table> tableList) {
        this.tableList = tableList;
    }

    public List<Table> getTableList() {
        return tableList;
    }

    public void setTableList(List<Table> tableList) {
        this.tableList = tableList;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        Tables tables = (Tables) object;

        if (tableList != null ? !tableList.equals(tables.tableList) : tables.tableList != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return tableList != null ? tableList.hashCode() : 0;
    }

    @NotNull
    @Override
    public String toString() {
        return "Tables{" +
                "tableList=" + tableList +
                '}';
    }
}
