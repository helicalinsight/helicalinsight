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
@XmlRootElement(name = "measure")
@XmlAccessorType(XmlAccessType.FIELD)

public class Measure {
    @XmlAttribute
    private String name;

    @XmlAttribute
    private String columnId;

    @XmlAttribute
    private String formatString;

    @XmlAttribute
    private String formatter;

    @XmlAttribute
    private String dataType;

    @XmlAttribute
    private String caption;

    @XmlAttribute
    private String description;


    @XmlAttribute
    private String measureId;

    @Override
    public String toString() {
        return "Measure{" +
                "name='" + name + '\'' +
                ", columnId='" + columnId + '\'' +
                ", formatString='" + formatString + '\'' +
                ", formatter='" + formatter + '\'' +
                ", dataType='" + dataType + '\'' +
                ", caption='" + caption + '\'' +
                ", description='" + description + '\'' +
                ", measureId='" + measureId + '\'' +
                ", aggregator='" + aggregator + '\'' +
                ", visible=" + visible +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Measure measure = (Measure) o;

        if (aggregator != null ? !aggregator.equals(measure.aggregator) : measure.aggregator != null) return false;
        if (caption != null ? !caption.equals(measure.caption) : measure.caption != null) return false;
        if (columnId != null ? !columnId.equals(measure.columnId) : measure.columnId != null) return false;
        if (dataType != null ? !dataType.equals(measure.dataType) : measure.dataType != null) return false;
        if (description != null ? !description.equals(measure.description) : measure.description != null) return false;
        if (formatString != null ? !formatString.equals(measure.formatString) : measure.formatString != null)
            return false;
        if (formatter != null ? !formatter.equals(measure.formatter) : measure.formatter != null) return false;
        if (measureId != null ? !measureId.equals(measure.measureId) : measure.measureId != null) return false;
        if (name != null ? !name.equals(measure.name) : measure.name != null) return false;
        if (visible != null ? !visible.equals(measure.visible) : measure.visible != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (columnId != null ? columnId.hashCode() : 0);
        result = 31 * result + (formatString != null ? formatString.hashCode() : 0);
        result = 31 * result + (formatter != null ? formatter.hashCode() : 0);
        result = 31 * result + (dataType != null ? dataType.hashCode() : 0);
        result = 31 * result + (caption != null ? caption.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (measureId != null ? measureId.hashCode() : 0);
        result = 31 * result + (aggregator != null ? aggregator.hashCode() : 0);
        result = 31 * result + (visible != null ? visible.hashCode() : 0);
        return result;
    }

    public String getFormatter() {

        return formatter;
    }

    public void setFormatter(String formatter) {
        this.formatter = formatter;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
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

    public String getMeasureId() {
        return measureId;
    }

    public void setMeasureId(String measureId) {
        this.measureId = measureId;
    }

    @XmlAttribute
    private String aggregator;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColumnId() {
        return columnId;
    }

    public void setColumnId(String columnId) {
        this.columnId = columnId;
    }

    public String getFormatString() {
        return formatString;
    }

    public void setFormatString(String formatString) {
        this.formatString = formatString;
    }

    public String getAggregator() {
        return aggregator;
    }

    public void setAggregator(String aggregator) {
        this.aggregator = aggregator;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    @XmlAttribute
    private Boolean visible;
}
