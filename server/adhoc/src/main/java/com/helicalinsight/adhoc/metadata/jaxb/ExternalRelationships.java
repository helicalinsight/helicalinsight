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
import java.util.Objects;

/**
 * Created by author on 25-10-2021.
 *
 * @author Somen
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "externalRelationships")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExternalRelationships {

    @XmlElement(name = "relationship")
    private List<Relationship> listOfRelations;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExternalRelationships that = (ExternalRelationships) o;
        return Objects.equals(listOfRelations, that.listOfRelations);
    }

    public ExternalRelationships(List<Relationship> listOfRelations) {
        this.listOfRelations = listOfRelations;
    }

    public ExternalRelationships() {
    }

    public List<Relationship> getListOfRelations() {
        return listOfRelations;
    }

    public void setListOfRelations(List<Relationship> listOfRelations) {
        this.listOfRelations = listOfRelations;
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
