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
 * Created by author on 06-03-2015.
 *
 * @author Rajasekhar
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "relationships")
@XmlAccessorType(XmlAccessType.FIELD)
public class Relationships {

    @XmlElement(name = "relationship")
    private List<Relationship> listOfRelations;

    public Relationships(List<Relationship> listOfRelations) {
        this.listOfRelations = listOfRelations;
    }

    public Relationships() {
    }

    public List<Relationship> getListOfRelations() {
        return listOfRelations;
    }

    public void setListOfRelations(List<Relationship> listOfRelations) {
        this.listOfRelations = listOfRelations;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        Relationships that = (Relationships) object;

        if (listOfRelations != null ? !listOfRelations.equals(that.listOfRelations) : that.listOfRelations != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return listOfRelations != null ? listOfRelations.hashCode() : 0;
    }

    @NotNull
    @Override
    public String toString() {
        return "Relationships{" +
                "listOfRelations=" + listOfRelations +
                '}';
    }
}
