package com.helicalinsight.admin.model;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "hi_phases")
public class HIPhase implements Serializable {

	@Id
	private Integer id;
	private String status;
	@Column(name = "resource_type")
	private String resourceType;
	private String description;
	private String process;

}
