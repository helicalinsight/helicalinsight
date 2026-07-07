package com.helicalinsight.admin.model;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

/**
 * @author Rajesh
 *         Created by helical019 on 2/15/2019.
 */
@Entity
@Table(name = "generic_cache")

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ApplicationCache implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "cache_key")
    private String key;


    @Column(name = "cache_type")
    private String type;

    public String getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(String resultStatus) {
        if (StringUtils.isBlank(resultStatus))
            this.resultStatus = "1";
        else
            this.resultStatus = resultStatus;
    }

    @Column(name = "status")

    private String resultStatus;


    @Column
    private Integer page;

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "ApplicationCache{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", type='" + type + '\'' +
                ", resultStatus='" + resultStatus + '\'' +
                ", page=" + page +
                ", createDateTime=" + createDateTime +
                ", value=" + Arrays.toString(value) +
                '}';
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "created_time")
    private Date createDateTime;

    public Date getCreateDateTime() {
        return createDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApplicationCache that = (ApplicationCache) o;

        if (id != that.id) return false;
        if (createDateTime != null ? !createDateTime.equals(that.createDateTime) : that.createDateTime != null)
            return false;
        if (key != null ? !key.equals(that.key) : that.key != null) return false;
        if (page != null ? !page.equals(that.page) : that.page != null) return false;
        if (resultStatus != null ? !resultStatus.equals(that.resultStatus) : that.resultStatus != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (!Arrays.equals(value, that.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (key != null ? key.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (resultStatus != null ? resultStatus.hashCode() : 0);
        result = 31 * result + (page != null ? page.hashCode() : 0);
        result = 31 * result + (createDateTime != null ? createDateTime.hashCode() : 0);
        result = 31 * result + (value != null ? Arrays.hashCode(value) : 0);
        return result;
    }

    public void setCreateDateTime(Date createDateTime) {
        this.createDateTime = createDateTime;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        if (page != null) {
            this.page = page;
        } else {
            this.page = 0;
        }
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    @Lob
    @Column(name = "cache_value")
    private byte[] value;

    public String getKey() {
        return key;
    }


    public void setKey(String key) {
        this.key = key;
    }

}
