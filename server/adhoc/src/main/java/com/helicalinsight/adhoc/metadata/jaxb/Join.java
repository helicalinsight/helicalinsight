package com.helicalinsight.adhoc.metadata.jaxb;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.*;

/**
 * Created by author on 06-03-2015.
 *
 * @author Rajasekhar
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "join")
@XmlAccessorType(XmlAccessType.FIELD)
public class Join {

    @XmlAttribute
    private String referenceDatabaseId;

    @XmlAttribute
    private String databaseId;

    public String getReferenceDatabaseId() {
        return referenceDatabaseId;
    }

    public void setReferenceDatabaseId(String referenceDatabaseId) {
        this.referenceDatabaseId = referenceDatabaseId;
    }

    public String getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(String databaseId) {
        this.databaseId = databaseId;
    }

    @XmlAttribute
    private String id;

    @XmlAttribute
    private String type;

    @XmlAttribute
    private String operator;

    @XmlElement(name = "left")
    private LeftTable leftTable;

    @XmlElement(name = "right")
    private RightTable rightTable;
    
    @XmlElement(name = "position")
    private Integer position;

    public Join() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public LeftTable getLeftTable() {
        return leftTable;
    }

    public void setLeftTable(LeftTable leftTable) {
        this.leftTable = leftTable;
    }

    public RightTable getRightTable() {
        return rightTable;
    }

    public void setRightTable(RightTable rightTable) {
        this.rightTable = rightTable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    

    public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	@Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        Join join = (Join) other;

        if (type != null ? !type.equals(join.type) : join.type != null) return false;
        if (operator != null ? !operator.equals(join.operator) : join.operator != null) return false;
        if (leftTable != null ? !leftTable.equals(join.leftTable) : join.leftTable != null) return false;
        return !(rightTable != null ? !rightTable.equals(join.rightTable) : join.rightTable != null);
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (operator != null ? operator.hashCode() : 0);
        result = 31 * result + (leftTable != null ? leftTable.hashCode() : 0);
        result = 31 * result + (rightTable != null ? rightTable.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Join{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", operator='" + operator + '\'' +
                ", leftTable=" + leftTable +
                ", rightTable=" + rightTable +
                '}';
    }
}
