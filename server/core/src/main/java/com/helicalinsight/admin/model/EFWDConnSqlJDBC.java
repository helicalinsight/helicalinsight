package com.helicalinsight.admin.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.helicalinsight.admin.customauth.CipherUtils;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;


@Entity
@Table(name = "efwd_connections_sql_jdbc", indexes = {
        @Index(name = "idx_efwd_sql_jdbc_conn_id", columnList = "efwd_conn_id")
})
@DynamicUpdate
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class EFWDConnSqlJDBC implements Serializable {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "efwd_conn_id", referencedColumnName = "id")
	private HIEfwdConnection hiEfwdConnection;

	@Column(name = "efwd_conn_id", insertable = false, updatable = false)
	private Integer efwdConnId;

    @Column(name="driver")
    private String driver;

    @Column(name="url")
    private String url;

    @Column(name = "username")
    private String userName;

    @Column(name="pass")
    private String pass;

    @Column(name="name")
    private String name;

    @Column(name = "database_name")
    private String database;

    @JsonIgnore
    public Integer getId() {
        return id;
    }

    public HIEfwdConnection getHiEfwdConnection() {
        return hiEfwdConnection;
    }

    public String getDriver() {
        return driver;
    }

    public String getUrl() {
        return url;
    }

    public String getUserName() {
        return userName;
    }

    public String getPass() {
        return (this.pass!=null? CipherUtils.decrypt(pass):pass);
    }

    public String getName() {
        return name;
    }

    public String getDatabase() {
        return database;
    }

    @JsonProperty
    public void setId(Integer id) {
        this.id = id;
    }

    public void setHiEfwdConnection(HIEfwdConnection hiEfwdConnection) {
        this.hiEfwdConnection = hiEfwdConnection;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDatabase(String database) {
        this.database = database;
    }


}
