package com.helicalinsight.admin.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String role_name;
    private Integer org_id;
    private OrganizationDTO organization;
}