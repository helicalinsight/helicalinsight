package com.helicalinsight.admin.dto;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.helicalinsight.serializer.UserDTODeserializer;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HIEfwdConnSecurityDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    @JsonDeserialize(using = UserDTODeserializer.class)
    private UserDTO createdBy;
    private Date lastUpdatedTime;
    private Integer permission;
    private HIEfwdConnectionDTO hiEfwdConnection;
    private OrganizationDTO orgId;
    private UserDTO userId;
    private RoleDTO roleId;
    
}