package com.helicalinsight.admin.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This is sub class of spring framework's UsernamePasswordAuthenticationFilter,
 * this class is get invoked when user submit the form using his/her
 * credentials from login page, this class is responsible for whether user login
 * along with organization name or not if organization name is in request
 * parameter along with user name and password it combine user name and
 * organization name with ':' delimiter and pass this parameters to
 * UserDetailsServiceImpl in service layer for authentication.
 *
 * @author Muqtar Ahmed
 * @version 1.1
 * @since 1.0
 */
public class PreAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(PreAuthenticationFilter.class);

    /**
     * Hardcoded with j_organization for organizational user
     * to login
     */
    private String extraParameter = "j_organization";

    /**
     * Instance variable for combining user name with organization with ':'
     * delimiter
     */
    private String delimiter = ":";

    /**
     * Kept locally so a credential-bearing resource request can persist the
     * security context and continue, instead of going through the login
     * success handler.
     */
    private SecurityContextRepository securityContextRepository;

    @Override
    public void setSecurityContextRepository(SecurityContextRepository securityContextRepository) {
        super.setSecurityContextRepository(securityContextRepository);
        this.securityContextRepository = securityContextRepository;
    }

    protected boolean requiresAuthentication(HttpServletRequest req, HttpServletResponse res) {
        if (hasCredentials(req)) {
            return true;
        }
        return super.requiresAuthentication(req, res);
    }

    /**
     * Either parameter pair is enough: {@code username}/{@code password}
     * or {@code j_username}/{@code j_password}. A mix of the two names is
     * accepted as well.
     */
    private boolean hasCredentials(HttpServletRequest req) {
        String username = firstNonBlank(req.getParameter("username"), req.getParameter("j_username"));
        String password = firstNonBlank(req.getParameter("password"), req.getParameter("j_password"));
        return username != null && password != null;
    }

    private static String firstNonBlank(String primary, String alternate) {
        if (primary != null && !primary.isEmpty()) {
            return primary;
        }
        if (alternate != null && !alternate.isEmpty()) {
            return alternate;
        }
        return null;
    }
        /**
         * override method which takes HttpServletRequest and HttpServletResponse
         * arguments and bypass it to super class
         */

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
            AuthenticationException {
    	Authentication auth= SecurityContextHolder.getContext().getAuthentication();
    	if(auth==null || auth instanceof AnonymousAuthenticationToken) {
    		auth=super.attemptAuthentication(request, response);
    		SecurityContextHolder.getContext().setAuthentication(auth);
    	}
        logger.debug("Attempting for authentication. " + "username = " + request.getParameter("username") + ", " +
                "password = " + request.getParameter("password"));
        return auth;
    }

    /**
     * Credentials on a resource URL (for example
     * {@code /workflow?...&username=...&password=...} or
     * {@code /workflow?...&j_username=...&j_password=...})
     * are not a login-form post. The success handler would otherwise send the
     * browser to {@code /welcome} ("Login Success") and drop the requested
     * URL. Persist the authentication and continue to that resource.
     * A real login post (the configured login processing URL) still uses the
     * success handler so a saved request or the default target is honored.
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        if (super.requiresAuthentication(request, response)) {
            super.successfulAuthentication(request, response, chain, authResult);
            return;
        }
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);
        if (this.securityContextRepository != null) {
            this.securityContextRepository.saveContext(context, request, response);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Authenticated from request parameters; continuing to " + request.getRequestURI());
        }
        chain.doFilter(request, response);
    }

    /**
     * override method which takes HttpServletRequest argument get the request
     * parameters from login page and check for organization request parameter
     * if present in request parameters combine it with user name and delimiter
     * ':' and return the combined user name to service layer if organization is
     * null only return the user name to service layer for authentication
     *
     * @return String combine user name
     */

    @Override
    protected String obtainUsername(HttpServletRequest request) {
        String username = request.getParameter(getUsernameParameter());
        if(username==null || username.equals("")){
            username=request.getParameter("j_username");
        }
        String parameter = getExtraParameter();
        String requestParameter = request.getParameter(parameter);
        String extraInput = requestParameter == null ? "" : requestParameter;
        String combinedUsername;
        String extParam = extraInput.trim();
        if (extParam.length() == 0) {
            combinedUsername = username;
        } else {
            combinedUsername = username + getDelimiter() + extraInput;
        }
        return combinedUsername;
    }

    public String obtainPassword(HttpServletRequest request){
        String password = request.getParameter("j_password");
        if(password!=null && !password.equals("")){ return  password;}
        return super.obtainPassword(request);
    }


    /**
     * @return The parameter name which will be used to obtain the extra input
     * from the login request
     */

    public String getExtraParameter() {
        return this.extraParameter;
    }

    /**
     * @param extraParameter The parameter name which will be used to obtain the extra
     *                       input from the login request
     */

    public void setExtraParameter(String extraParameter) {
        this.extraParameter = extraParameter;
    }

    /**
     * @return The delimiter string used to separate the user name and extra
     * input values in the string returned by
     * <code>obtainUsername()</code>
     */
    public String getDelimiter() {
        return this.delimiter;
    }

    /**
     * @param delimiter The delimiter string used to separate the user name and extra
     *                  input values in the string returned by
     *                  <code>obtainUsername()</code>
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }
}
