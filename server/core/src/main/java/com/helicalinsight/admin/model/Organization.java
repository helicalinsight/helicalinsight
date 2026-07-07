package com.helicalinsight.admin.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.helicalinsight.datasource.model.GlobalConnectionSecurity;

import java.io.Serializable;
import java.util.List;

/**
 * This is persistent class for organization which maps it's properties with
 * organization table and persist them to a database, this class's object or
 * instance in stored in organization table as per rule this class should have
 * the default constructor as well as getter and setter method's for it's
 * properties Annotation Entity mark this class as Entity Bean and annotation
 * Table allows you to specify the details of the table that will be used to
 * persist the entity in the database.
 *
 * @author Muqtar Ahmed
 * @version 1.1
 * @since 1.0
 */

@Entity
@Table(name = "organization", uniqueConstraints = {@UniqueConstraint(columnNames = {"org_name"})})
@Filter(name = "isDeletedFilter", condition = "is_deleted = :isDeleted")
public class Organization implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * each entity bean have the primary key and annotate with Id generated
     * values generate the automatically determined the most appropriate primary
     * key with strategy
     */

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * details of the column organization name to which field's or properties is
     * mapped and this property should not be empty
     */
    @Column
    @NotNull
    @Size(min = 1)
    private String org_name;

    /**
     * details of the column organization description to which field's or
     * properties is mapped
     */
    @Column
    private String org_desc;
    
    @Column(name = "is_deleted")
    private Boolean deleted;
    
    @OneToMany(mappedBy = "organization", cascade = CascadeType.REMOVE)
    @LazyCollection(LazyCollectionOption.TRUE)
    private List<Role> roles;
    
    @OneToMany(mappedBy = "organization", cascade = CascadeType.REMOVE)
    @LazyCollection(LazyCollectionOption.TRUE)
    private List<User> users;
    
    @OneToMany(mappedBy = "orgId", cascade = CascadeType.REMOVE)
    @LazyCollection(LazyCollectionOption.TRUE)
    private List<HIResourceSecurityDB> hiResourceSecurityDBs;
    
    @OneToMany(mappedBy = "orgId", cascade = CascadeType.REMOVE)
    @LazyCollection(LazyCollectionOption.TRUE)
    private List<HIEfwdConnSecurity> hiEfwdConnSecurities;
    
    @OneToMany(mappedBy = "orgId", cascade = CascadeType.REMOVE)
    @LazyCollection(LazyCollectionOption.TRUE)
    private List<GlobalConnectionSecurity> globalConnectionSecurities;

    /**
     * Default Constructor
     */

    public Organization() {
    }

    /**
     * over loaded Constructor with three arguments organization id, name,
     * description
     *
     * @param ids      organization Id
     * @param org_name organization name
     * @param org_desc organization description
     */

    public Organization(int id, String org_name, String org_desc) {
        super();
        this.id = id;
        this.org_name = org_name;
        this.org_desc = org_desc;
    }

    /**
     * over loaded Constructor with two arguments organization name and
     * organization description
     *
     * @param org_name organization name
     * @param org_desc organization description
     */
    public Organization(String org_name, String org_desc) {
        super();
        this.org_name = org_name;
        this.org_desc = org_desc;
    }

    /**
     * this is getter method for id which is the primary key for the
     * organization table and return the generated primary key
     *
     * @return generated id
     */
    @JsonIgnore
    public int getId() {
        return id;
    }

    /**
     * this is setter method for the primary key for the organization table
     *
     * @param id id
     */
    @JsonProperty
    public void setId(int id) {
        this.id = id;
    }

    /**
     * this is getter method for organization name for the organization table
     * and return the organization name
     *
     * @return organization name
     */

    public String getOrg_name() {
        return org_name;
    }

    /**
     * this is setter method for organization name
     *
     * @param org_name organization name
     */

    public void setOrg_name(String org_name) {
        this.org_name = org_name;
    }

    /**
     * this is setter method for organization description and return the
     * organization description
     *
     * @return organization description
     */

    public String getOrg_desc() {
        return org_desc;
    }

    /**
     * this is setter method for organization description
     *
     * @param org_desc organization description
     */

    public void setOrg_desc(String org_desc) {
        this.org_desc = org_desc;
    }

	public Boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

    
    
    
}
