package com.helicalinsight.adhoc.metadata.jaxb;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;

/**
 * Created by author on 27-02-2015.
 *
 * @author Rajasekhar
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "database")
@XmlAccessorType(XmlAccessType.FIELD)
@SuppressWarnings("unused")
public class Database {

	@XmlAttribute
	private String name;

	@XmlAttribute
	private String catalog;

	@XmlAttribute
	private String schema;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlAttribute
	private String id;

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Database database = (Database) o;

        if (catalog != null ? !catalog.equals(database.catalog) : database.catalog != null) return false;
        if (cubes != null ? !cubes.equals(database.cubes) : database.cubes != null) return false;
        if (name != null ? !name.equals(database.name) : database.name != null) return false;
        if (relationships != null ? !relationships.equals(database.relationships) : database.relationships != null)
            return false;
        if (schema != null ? !schema.equals(database.schema) : database.schema != null) return false;
        if (tables != null ? !tables.equals(database.tables) : database.tables != null) return false;
        if (views != null ? !views.equals(database.views) : database.views != null) return false;
        if (id != null ? !id.equals(database.id) : database.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (catalog != null ? catalog.hashCode() : 0);
        result = 31 * result + (schema != null ? schema.hashCode() : 0);
        result = 31 * result + (tables != null ? tables.hashCode() : 0);
        result = 31 * result + (cubes != null ? cubes.hashCode() : 0);
        result = 31 * result + (relationships != null ? relationships.hashCode() : 0);
        result = 31 * result + (views != null ? views.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Database{" +
                "name='" + name + '\'' +
                ", catalog='" + catalog + '\'' +
                ", schema='" + schema + '\'' +
                ", tables=" + tables +
                ", cubes=" + cubes +
                ", relationships=" + relationships +
                ", views=" + views +
                ", id=" + id +
                '}';
    }

    @XmlElement
	private Tables tables;

    @XmlElement
    private Cubes cubes;

    public Cubes getCubes() {

        return cubes;
    }

    public void setCubes(Cubes cubes) {
        this.cubes = cubes;
    }

    @XmlElement
	private Relationships relationships;

	@XmlElement
	private Views views;

	public Database() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public Tables getTables() {
		return tables;
	}

	public void setTables(Tables tables) {
		this.tables = tables;
	}

	public Relationships getRelationships() {
		return relationships;
	}

	public void setRelationships(Relationships relationships) {
		this.relationships = relationships;
	}

	public Views getViews() {
		return views;
	}

	public void setViews(Views views) {
		this.views = views;
	}

}
