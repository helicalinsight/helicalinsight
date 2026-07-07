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

@XmlRootElement(name = "primaryKeys")
@XmlAccessorType(XmlAccessType.FIELD)
public class PrimaryKeys {

    @XmlElement
    private List<PrimaryKey> primaryKey;

    public PrimaryKeys(List<PrimaryKey> primaryKey) {
        this.primaryKey = primaryKey;
    }

    public PrimaryKeys() {
    }

    public List<PrimaryKey> getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(List<PrimaryKey> primaryKey) {
        this.primaryKey = primaryKey;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        PrimaryKeys that = (PrimaryKeys) object;

        if (primaryKey != null ? !primaryKey.equals(that.primaryKey) : that.primaryKey != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return primaryKey != null ? primaryKey.hashCode() : 0;
    }

    @NotNull
    @Override
    public String toString() {
        return "PrimaryKeys{" +
                "primaryKey=" + primaryKey +
                '}';
    }
}
