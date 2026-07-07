package com.helicalinsight.adhoc.metadata.jaxb;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by author on 27-02-2015.
 *
 * @author Rajasekhar
 */
@Component
@Scope("prototype")

@XmlRootElement(name = "foreignKey")
@XmlAccessorType(XmlAccessType.FIELD)
public class ForeignKey {

    @XmlAttribute
    private String name;

    @XmlAttribute
    private String referenceTable;

    @XmlAttribute
    private String referenceColumn;

    public ForeignKey(String name, String referenceTable, String referenceColumn) {
        this.name = name;
        this.referenceTable = referenceTable;
        this.referenceColumn = referenceColumn;
    }

    public ForeignKey() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReferenceTable() {
        return referenceTable;
    }

    public void setReferenceTable(String referenceTable) {
        this.referenceTable = referenceTable;
    }

    public String getReferenceColumn() {
        return referenceColumn;
    }

    public void setReferenceColumn(String referenceColumn) {
        this.referenceColumn = referenceColumn;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        ForeignKey that = (ForeignKey) object;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (referenceColumn != null ? !referenceColumn.equals(that.referenceColumn) : that.referenceColumn != null)
            return false;
        if (referenceTable != null ? !referenceTable.equals(that.referenceTable) : that.referenceTable != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (referenceTable != null ? referenceTable.hashCode() : 0);
        result = 31 * result + (referenceColumn != null ? referenceColumn.hashCode() : 0);
        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "ForeignKey{" +
                "name='" + name + '\'' +
                ", referenceTable='" + referenceTable + '\'' +
                ", referenceColumn='" + referenceColumn + '\'' +
                '}';
    }
}
