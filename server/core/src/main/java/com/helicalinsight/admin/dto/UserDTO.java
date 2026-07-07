package com.helicalinsight.admin.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String username;
    private String password;
    private String emailAddress;
    private Integer org_id;
    private boolean enabled;
    private Boolean deleted;
    private Boolean isExternallyAuthenticated;
    private OrganizationDTO organization;
    private List<RoleDTO> roles;
    private List<ProfileDTO> profile;
   
}