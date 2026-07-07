package com.helicalinsight.externalauth.jwt;

import java.util.Date;

/**
 * AuthToken class represents the authentication token used for user authentication and authorization.
 * @author Rajesh
 * Created by author on 17/7/2019.
 */

public class AuthToken {

    private String token;
    private Date issuedAt;
    private Date expiration;

    public AuthToken() {

    }

    public AuthToken(String token, Date issuedAt, Date expiration) {
        this.token = token;
        this.expiration = expiration;
        this.issuedAt = issuedAt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Date issuedAt) {
        this.issuedAt = issuedAt;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }
}
