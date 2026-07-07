package com.helicalinsight.admin.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EFWDConnGroovyDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private HIEfwdConnectionDTO hiEfwdConnection;
    private String driver;
    private String url;
    private String userName;
    private String pass;
    private String condition;
    private String database;
    private String name;
}