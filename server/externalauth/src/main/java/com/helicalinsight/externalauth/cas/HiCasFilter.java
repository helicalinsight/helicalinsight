package com.helicalinsight.externalauth.cas;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * HiCasFilter  extends {@link CasAuthenticationFilter}
 * this class is responsible for creating authentication object
 * and setting username and password for client.
 * Created by user on 2/10/2016.
 * @author Somen
 */
public class HiCasFilter extends CasAuthenticationFilter {

    private static final String CAS_TICKET_PARAM_NAME = "ticket";
    private String usernameParameter = "username";
    private String passwordParameter = "password";

	/**
	 * attemptAuthentication(HttpServletRequest request, HttpServletResponse
	 * response)
	 * this method creates authentication object using user credentials.
	 * @param HttpServletRequest  request provides user details
	 * @param HttpServletResponse response sets various response parameters,
	 *                            headers, and content.
	 * @return an Authentication object created using user details.
	 * @throws AuthenticationException If an authentication error occurs.
	 * @throws IOException             If an I/O error occurs.
	 */
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
            AuthenticationException, IOException {
        String password = obtainTicket(request);
        if ((password != null) && (password.trim().length() > 0)) {
            String username = "_cas_stateful_";
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken
                    ("_cas_stateful_", password);
            authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));

            return getAuthenticationManager().authenticate(authRequest);
        }
        String username = obtainUsername(request);
        password = obtainPassword(request);

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username != null ?
                username.trim() : "", password != null ? password : "");


        setDetails(request, authRequest);

        return getAuthenticationManager().authenticate(authRequest);
    }

    /**
     * obtainTicket(HttpServletRequest request)
     * @param request   provides user details
     * @return password in string format
     */
    protected String obtainTicket(HttpServletRequest request) {
        return request.getParameter("ticket");
    }
    /**
     * obtainUsername(HttpServletRequest request)
     * @param request     provides user details
     * @return username in string format
     */
    protected String obtainUsername(HttpServletRequest request) {
        return request.getParameter(this.usernameParameter);
    }
    /**
     * obtainPassword(HttpServletRequest request)
     * @param request    provides user details
     * @return password in string format
     */
    protected String obtainPassword(HttpServletRequest request) {
        return request.getParameter(this.passwordParameter);
    }
    /**
     * setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest)
     * new authentication details instance is created using request object.
     * and sets all details in UsernamePasswordAuthenticationToken.
     * @param request          provides user details
     * @param authRequest	   this is designed for simple presentation of a username and password,
     * 						   and sets all credentials with in object
     */
    protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }
    
    /**
     * getUsernameParameter()
     * @return simply returns default user name 
     */
    String getUsernameParameter() {
        return this.usernameParameter;
    }
    /**
     * setUsernameParameter(String usernameParameter)
     * it checks argument it should not be null or empty
     * @param usernameParameter  sets the user name
     */
    public void setUsernameParameter(String usernameParameter) {
        Assert.hasText(usernameParameter, "Username parameter must not be empty or null");
        this.usernameParameter = usernameParameter;
    }
    /**
     * getPasswordParameter()
     * @return simply returns default user password 
     */
    String getPasswordParameter() {
        return this.passwordParameter;
    }
    /**
     * setPasswordParameter(String passwordParameter)
     * it checks argument it should not be null or empty
     * @param passwordParameter     user password to set
     */
    public void setPasswordParameter(String passwordParameter) {
        Assert.hasText(passwordParameter, "Password parameter must not be empty or null");
        this.passwordParameter = passwordParameter;
    }
}
