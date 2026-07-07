package com.helicalinsight.adhoc.metadata.jaxb;

import org.jetbrains.annotations.Nullable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Created by author on 06-03-2015.
 *
 * @author Rajasekhar
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "relationship")
@XmlAccessorType(XmlAccessType.FIELD)
public class Relationship {

    @XmlAttribute
    private String table;

    @XmlAttribute
    private String referenceTable;

    @XmlElement
    private List<Join> join;

    public Relationship() {
    }

    public Relationship(String table, String referenceTable, List<Join> join) {
        this.table = table;
        this.referenceTable = referenceTable;
        this.join = join;
    }

    public boolean isIdentical(String first, String second) {
        //For equality test join can be null
        Relationship relationship = new Relationship(first, second, null);
        return (relationship.equals(this));
    }


    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        Relationship that = (Relationship) object;

        if (this.referenceTable.equals(that.referenceTable) && (this.table.equals(that.table))) {
            return true;
        }

        //noinspection RedundantIfStatement
        if (this.table.equals(that.referenceTable) && (this.referenceTable.equals(that.table))) {
            return true;
        }
        return false;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getReferenceTable() {
        return referenceTable;
    }

    public void setReferenceTable(String referenceTable) {
        this.referenceTable = referenceTable;
    }

    public List<Join> getJoin() {
        return join;
    }

    public void setJoin(List<Join> join) {
        this.join = join;
    }

    @Override
    public int hashCode() {
        int result = table != null ? table.hashCode() : 0;
        result = 31 * result + (referenceTable != null ? referenceTable.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Relationship{" +
                "table='" + table + '\'' +
                ", referenceTable='" + referenceTable + '\'' +
                ", join=" + join +
                '}';
    }
}