package com.helicalinsight.adhoc.metadata.jaxb;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by helical021 on 1/13/2020.
 */
@Component
@Scope("prototype")
@XmlRootElement(name = "level")
@XmlAccessorType(XmlAccessType.FIELD)

public class Level {
    @XmlAttribute
    private Integer approximateRowCount;

    @XmlAttribute
    private  String name;

    @XmlAttribute
    private  String levelId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Level level = (Level) o;

        if (approximateRowCount != null ? !approximateRowCount.equals(level.approximateRowCount) : level.approximateRowCount != null)
            return false;
        if (caption != null ? !caption.equals(level.caption) : level.caption != null) return false;
        if (columnId != null ? !columnId.equals(level.columnId) : level.columnId != null) return false;
        if (dataType != null ? !dataType.equals(level.dataType) : level.dataType != null) return false;
        if (description != null ? !description.equals(level.description) : level.description != null) return false;
        if (hideMemberIfExpression != null ? !hideMemberIfExpression.equals(level.hideMemberIfExpression) : level.hideMemberIfExpression != null)
            return false;
        if (levelId != null ? !levelId.equals(level.levelId) : level.levelId != null) return false;
        if (levelType != null ? !levelType.equals(level.levelType) : level.levelType != null) return false;
        if (name != null ? !name.equals(level.name) : level.name != null) return false;
        if (nestedMemberFormatter != null ? !nestedMemberFormatter.equals(level.nestedMemberFormatter) : level.nestedMemberFormatter != null)
            return false;
        if (tableId != null ? !tableId.equals(level.tableId) : level.tableId != null) return false;
        if (visible != null ? !visible.equals(level.visible) : level.visible != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = approximateRowCount != null ? approximateRowCount.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (levelId != null ? levelId.hashCode() : 0);
        result = 31 * result + (visible != null ? visible.hashCode() : 0);
        result = 31 * result + (columnId != null ? columnId.hashCode() : 0);
        result = 31 * result + (caption != null ? caption.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (dataType != null ? dataType.hashCode() : 0);
        result = 31 * result + (nestedMemberFormatter != null ? nestedMemberFormatter.hashCode() : 0);
        result = 31 * result + (levelType != null ? levelType.hashCode() : 0);
        result = 31 * result + (hideMemberIfExpression != null ? hideMemberIfExpression.hashCode() : 0);
        result = 31 * result + (tableId != null ? tableId.hashCode() : 0);
        return result;
    }

    public String getLevelId() {
        return levelId;
    }

    public void setLevelId(String levelId) {
        this.levelId = levelId;
    }

    public Integer getApproximateRowCount() {
        return approximateRowCount;
    }

    public void setApproximateRowCount(Integer approximateRowCount) {
        this.approximateRowCount = approximateRowCount;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getNestedMemberFormatter() {
        return nestedMemberFormatter;
    }

    public void setNestedMemberFormatter(String nestedMemberFormatter) {
        this.nestedMemberFormatter = nestedMemberFormatter;
    }

    @XmlAttribute
    private Boolean visible;

    @XmlAttribute
    private String columnId;


    @XmlAttribute
    private String caption;

    @XmlAttribute
    private String description;


    @XmlAttribute
    private String dataType;


    @XmlAttribute
    private String nestedMemberFormatter;

    @XmlAttribute
    private String levelType;


    @XmlAttribute
    private String hideMemberIfExpression;

    public String getHideMemberIfExpression() {
        return hideMemberIfExpression;
    }

    public void setHideMemberIfExpression(String hideMemberIfExpression) {
        this.hideMemberIfExpression = hideMemberIfExpression;
    }

    @XmlAttribute
    private String tableId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public String getLevelType() {
        return levelType;
    }

    public void setLevelType(String levelType) {
        this.levelType = levelType;
    }
}
