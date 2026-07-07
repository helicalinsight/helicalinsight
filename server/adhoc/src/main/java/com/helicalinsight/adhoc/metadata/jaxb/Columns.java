package com.helicalinsight.adhoc.metadata.jaxb;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by author on 27-02-2015.
 *
 * @author Rajasekhar
 */
@Component
@Scope("prototype")

@XmlRootElement(name = "columns")
@XmlAccessorType(XmlAccessType.FIELD)
public class Columns {

    @XmlElement
    private List<Column> column;

    public Columns(List<Column> column) {
        this.column = column;
    }

    public Columns() {
    }

    public List<Column> getColumn() {
        return column;
    }

    public void setColumn(List<Column> column) {
        this.column = column;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        Columns columns = (Columns) object;

        if (column != null ? !column.equals(columns.column) : columns.column != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return column != null ? column.hashCode() : 0;
    }

    @NotNull
    @Override
    public String toString() {
        return "Columns{" +
                "column=" + column +
                '}';
    }
}
