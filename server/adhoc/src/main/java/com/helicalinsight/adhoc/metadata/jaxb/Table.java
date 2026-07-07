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
@XmlRootElement(name = "table")
@XmlAccessorType(XmlAccessType.FIELD)
public class Table {

    @XmlAttribute
    private String id;
    
    
    @XmlAttribute
    private String dbId;

    @XmlAttribute
    private String name;

    @XmlAttribute
    private String originalName;

    @XmlAttribute
    private String type;

    @XmlAttribute
    private String aliasName;

    @XmlElement
    private Columns columns;

    @XmlElement
    private PrimaryKeys primaryKeys;

    @XmlElement
    private ForeignKeys foreignKeys;

    public Table() {
    }

    @Override
    public String toString() {
        return "Table{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", originalName='" + originalName + '\'' +
                ", type='" + type + '\'' +
                ", aliasName='" + aliasName + '\'' +
                ", columns=" + columns +
                ", primaryKeys=" + primaryKeys +
                ", foreignKeys=" + foreignKeys +
                  ", dbId=" + dbId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Table table = (Table) o;

        if (aliasName != null ? !aliasName.equals(table.aliasName) : table.aliasName != null) return false;
        if (columns != null ? !columns.equals(table.columns) : table.columns != null) return false;
        if (originalName != null ? !originalName.equals(table.originalName) : table.originalName != null)
            return false;
        if (foreignKeys != null ? !foreignKeys.equals(table.foreignKeys) : table.foreignKeys != null) return false;
        if (id != null ? !id.equals(table.id) : table.id != null) return false;
        if (name != null ? !name.equals(table.name) : table.name != null) return false;
        if (primaryKeys != null ? !primaryKeys.equals(table.primaryKeys) : table.primaryKeys != null) return false;
        if (type != null ? !type.equals(table.type) : table.type != null) return false;
        if (dbId != null ? !dbId.equals(table.dbId) : table.dbId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (originalName != null ? originalName.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (aliasName != null ? aliasName.hashCode() : 0);
        result = 31 * result + (columns != null ? columns.hashCode() : 0);
        result = 31 * result + (primaryKeys != null ? primaryKeys.hashCode() : 0);
        result = 31 * result + (foreignKeys != null ? foreignKeys.hashCode() : 0);
        result = 31 * result + (dbId != null ? dbId.hashCode() : 0);
        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public Columns getColumns() {
        return columns;
    }

    public void setColumns(Columns columns) {
        this.columns = columns;
    }

    public PrimaryKeys getPrimaryKeys() {
        return primaryKeys;
    }

    public void setPrimaryKeys(PrimaryKeys primaryKeys) {
        this.primaryKeys = primaryKeys;
    }

    public ForeignKeys getForeignKeys() {
        return foreignKeys;
    }

    public void setForeignKeys(ForeignKeys foreignKeys) {
        this.foreignKeys = foreignKeys;
    }

	public String getDbId() {
		return dbId;
	}

	public void setDbId(String dbId) {
		this.dbId = dbId;
	}
    
}
