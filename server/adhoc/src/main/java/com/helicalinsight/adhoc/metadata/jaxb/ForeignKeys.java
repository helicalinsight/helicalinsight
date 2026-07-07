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

@XmlRootElement(name = "foreignKeys")
@XmlAccessorType(XmlAccessType.FIELD)
public class ForeignKeys {

    @XmlElement(name = "foreignKey")
    private List<ForeignKey> foreignKeyList;

    public ForeignKeys() {
    }

    public ForeignKeys(List<ForeignKey> foreignKeyList) {
        this.foreignKeyList = foreignKeyList;
    }

    public List<ForeignKey> getForeignKeyList() {
        return foreignKeyList;
    }

    public void setForeignKeyList(List<ForeignKey> foreignKeyList) {
        this.foreignKeyList = foreignKeyList;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        ForeignKeys that = (ForeignKeys) object;

        if (foreignKeyList != null ? !foreignKeyList.equals(that.foreignKeyList) : that.foreignKeyList != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return foreignKeyList != null ? foreignKeyList.hashCode() : 0;
    }

    @NotNull
    @Override
    public String toString() {
        return "ForeignKeys{" +
                "foreignKeyList=" + foreignKeyList +
                '}';
    }
}
