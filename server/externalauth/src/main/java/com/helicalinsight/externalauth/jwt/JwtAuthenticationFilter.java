package com.helicalinsight.externalauth.jwt;
import com.helicalinsight.admin.RestRedirectStrategy;
import com.helicalinsight.admin.filter.PreAuthenticationFilter;
import com.helicalinsight.admin.filter.RequestParameterAuthenticationFilter;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;

import org.springframework.security.core.userdetails.UserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

import com.helicalinsight.efw.utility.ConfigurationFileReader;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.util.AntPathMatcher;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.core.HttpHeaders;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
/**
 * JwtAuthenticationFilter extends {@link OncePerRequestFilter}
 * This class is responsible for filtering incoming requests, processes JWT tokens, and sets authentication information.
 * It also handles logout and session management when JWT tokens are used.
 * @author Rajesh
 * Created by author on 17/7/2019.
 */

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    Map<String, String> mapFromClasspathPropertiesFile = ConfigurationFileReader.mapFromClasspathPropertiesFile
            ("project.properties");
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private TokenProvider jwtTokenUtil;


    private List<String> ignoreUrls;

    public List<String> getIgnoreUrls() {
        return ignoreUrls;
    }

    public void setIgnoreUrls(List<String> ignoreUrls) {
        this.ignoreUrls = ignoreUrls;
    }
    /**
     * doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
     * This method checks incoming requests, processes JWT tokens, and manages authentication and session.
     * @param req        		it provides credentials required to filter like cookies,and headers
     * 							and stores some jwt details
     * @param res 		 		response sets the content type
     * @param chain 	 		filter chain takes the request and response objects to process these objects as they flow through the chain of filters.
     * @throws IOException   	if an I/O exception.
     * @throws ServletException if a servlet exception occurs.
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res) throws
            AuthenticationException {
        String TOKEN_PREFIX = mapFromClasspathPropertiesFile.get("token_prefix");
        String HEADER_STRING = mapFromClasspathPropertiesFile.get("header_string_authToken");
        String PARAM_STRING =HEADER_STRING;
        String jwtCookie = mapFromClasspathPropertiesFile.get("jwt_cookie");
        String headerAuthorization = req.getHeader(mapFromClasspathPropertiesFile.get("header_string"));
        String header = req.getHeader(HEADER_STRING);
        String headerType = mapFromClasspathPropertiesFile.get("header_string_type");
        String expectedType = mapFromClasspathPropertiesFile.get("header_string_type_jwt");
        String headerTypeValue = req.getHeader(headerType);

        if(headerAuthorization!=null){
            header=headerAuthorization;
        }



        boolean cookieTrue = "true".equalsIgnoreCase(jwtCookie);
        if (cookieTrue) {
            Cookie[] cookies = req.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(HEADER_STRING)) {
                        header = cookie.getValue();
                        break;
                    }
                }
            }
        }
        boolean isJwt = false;
        if (header == null) {
            header = req.getParameter(PARAM_STRING);
        }
        String username = null;
        String authToken = null;
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            authToken = header.replace(TOKEN_PREFIX, "");
            try {
                username = jwtTokenUtil.getUsernameFromToken(authToken);
            } catch (IllegalArgumentException e) {
                logger.error("an error occurred during getting username from token", e);
            } catch (ExpiredJwtException e) {
                logger.warn("the token is expired and not valid anymore", e);
            } catch (SignatureException e) {
                logger.error("Authentication Failed. Username or Password not valid.");
            }
        } else {
            logger.warn("couldn't find bearer string, will ignore the header");
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtTokenUtil.validateToken(authToken, userDetails)) {
                UsernamePasswordAuthenticationToken authentication = jwtTokenUtil.getAuthentication(authToken, SecurityContextHolder.getContext().getAuthentication(), userDetails);
                //UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")));
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));

                if (cookieTrue) {
                    setCookieJwt(req, res, authToken);
                } else {
                    req.setAttribute("authToken", header);
                }
                return authentication;
            }

        }
return  null;
    }


    protected boolean requiresAuthentication(HttpServletRequest req, HttpServletResponse res) {
        String TOKEN_PREFIX = mapFromClasspathPropertiesFile.get("token_prefix");

        String HEADER_STRING = mapFromClasspathPropertiesFile.get("header_string_authToken");
        String PARAM_STRING =HEADER_STRING;
        String jwtCookie = mapFromClasspathPropertiesFile.get("jwt_cookie");
        String headerAuthorization = req.getHeader(mapFromClasspathPropertiesFile.get("header_string"));
        String header = req.getHeader(HEADER_STRING);
        String headerType = mapFromClasspathPropertiesFile.get("header_string_type");
        String expectedType = mapFromClasspathPropertiesFile.get("header_string_type_jwt");
        String headerTypeValue = req.getHeader(headerType);
        if(headerAuthorization!=null){
            header=headerAuthorization;
        }



        boolean cookieTrue = "true".equalsIgnoreCase(jwtCookie);
        if (cookieTrue) {
            Cookie[] cookies = req.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(HEADER_STRING)) {
                        header = cookie.getValue();
                        break;
                    }
                }
            }
        }
        boolean isJwt = false;
        if (header == null) {
            header = req.getParameter(PARAM_STRING);
        }
        String username = null;
        String authToken = null;
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            authToken = header.replace(TOKEN_PREFIX, "");
            try {
                username = jwtTokenUtil.getUsernameFromToken(authToken);
            } catch (IllegalArgumentException e) {
                logger.error("an error occurred during getting username from token", e);
            } catch (ExpiredJwtException e) {
                logger.warn("the token is expired and not valid anymore", e);
            } catch (SignatureException e) {
                logger.error("Authentication Failed. Username or Password not valid.");
            }
        } else {
            logger.warn("couldn't find bearer string, will ignore the header");
        }
        if (username != null) return  true;
        return  false;
    }


    /**
     * setCookieJwt(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String jwtToken)
     * Sets a JWT-based authentication cookie in the response.
     * @param httpRequest         provides the portion of the request URI that indicates the context of the request
     * @param httpResponse        stores the cookies
     * @param jwtToken			  to set the token
     */
    private void setCookieJwt(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String jwtToken) {
        String cookiePath = httpRequest.getContextPath();
        Cookie cookie = new Cookie("authToken", mapFromClasspathPropertiesFile.get("token_prefix") + jwtToken);
        cookie.setPath(cookiePath);
        httpResponse.addCookie(cookie);
    }
    /**
     * shouldNotFilter(HttpServletRequest request)
     * this method Determines whether the request should not be filtered based on URLs.
     * @param      provides URL
     * @return {@code true} if the request should not be filtered,{@code false} otherwise.
     */

    /**
     * matchPath(String requestedUrl)
     * it simply matches a requested URL against a list of ignored URL patterns
     * @param requestedUrl     URL to compare
     * @return {@code true} if the requested URL matches any ignore pattern, {@code false} otherwise.
     */
    public boolean matchPath(String requestedUrl) {
        for (String pattern : ignoreUrls) {
            AntPathMatcher antPathMatcher = new AntPathMatcher();
            if (antPathMatcher.match(pattern, requestedUrl)) {
                return true;
            }
        }
        return false;
    }
    private String authenticationFailureUrl;
    /**
     * isAuthentication(HttpServletRequest request)
     * @param request      it gives auth token
     * @return it always return {@code false}
     */

    protected String getFullFailureUrl(HttpServletRequest request) {
        return request.getContextPath() + authenticationFailureUrl;
    }


    public void setAuthenticationFailureUrl(String authenticationFailureUrl) {
        this.authenticationFailureUrl = authenticationFailureUrl;
    }
	/**
	 * destroy() 
	 * this method is to perform any necessary cleanup or resource release
	 * operations before the filter instance is removed from service. This method is
	 * called by the servlet container when the filter is being taken out of
	 * service, which typically happens when the application is shut down or when
	 * the filter is explicitly removed from the deployment.
	 */
    /**
     * eraseCookie(HttpServletRequest req, HttpServletResponse resp)
     * method for erasing cookies from the client's browser.
     * @param req       provides cookies
     * @param resp		after deleting cookies response will set all to empty
     */
    private void eraseCookie(HttpServletRequest req, HttpServletResponse resp) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null)
            for (Cookie cookie : cookies) {
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                resp.addCookie(cookie);
            }
    }
}