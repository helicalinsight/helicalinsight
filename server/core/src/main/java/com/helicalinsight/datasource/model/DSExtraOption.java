package com.helicalinsight.datasource.model;

import java.util.Objects;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "ds_extra_option")
public class DSExtraOption {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne(cascade = CascadeType.REMOVE)
	@JoinColumn(name = "global_id")
	private GlobalConnections globalConnection;
	
	@Column(name = "option_key")
	private String key;
	
	@Lob
	@Column(name = "option_value",length = Integer.MAX_VALUE)
	private String value;
	
	
	public DSExtraOption() {
		// NOOP
	}
	
	public DSExtraOption(GlobalConnections globalConnections , String key, String value) {
		this.globalConnection = globalConnections;
		this.key = key;
		this.value = value;
	}
	
	@JsonIgnore
	public Integer getId() {
		return id;
	}

	@JsonProperty
	public void setId(Integer id) {
		this.id = id;
	}
	
	@JsonIgnore
	public GlobalConnections getGlobalConnection() {
		return globalConnection;
	}

	@JsonProperty
	public void setGlobalConnection(GlobalConnections globalConnection) {
		this.globalConnection = globalConnection;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "DSExtraOption [id=" + id + ", globalConnection=" + globalConnection + ", key=" + key + ", value="
				+ value + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(globalConnection, id, key, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DSExtraOption other = (DSExtraOption) obj;
		return Objects.equals(globalConnection, other.globalConnection) && Objects.equals(id, other.id)
				&& Objects.equals(key, other.key) && Objects.equals(value, other.value);
	}

	
}
