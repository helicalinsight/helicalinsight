package com.helicalinsight.externalauth.jwt;

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
import org.springframework.web.filter.OncePerRequestFilter;

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

/**
 * @author Rajesh
 * Created by author on 17/7/2019.
 */

public class JwtAuthenticationFilterSSO extends OncePerRequestFilter {

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

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String TOKEN_PREFIX = mapFromClasspathPropertiesFile.get("token_prefix");
        String HEADER_STRING = mapFromClasspathPropertiesFile.get("header_string_authToken");
        String PARAM_STRING =HEADER_STRING;
        String jwtCookie = mapFromClasspathPropertiesFile.get("jwt_cookie");
        String headerAuthorization = req.getHeader(mapFromClasspathPropertiesFile.get("header_string"));
        String header = req.getHeader(HEADER_STRING);
        String headerType = mapFromClasspathPropertiesFile.get("header_string_type");
        String expectedType = mapFromClasspathPropertiesFile.get("header_string_type_jwt");
        String headerTypeValue = req.getHeader(headerType);
        if(headerAuthorization==null && !expectedType.equals(headerTypeValue)){

            chain.doFilter(req, res);
            return ;
        }
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
        isAuthentication(req);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtTokenUtil.validateToken(authToken, userDetails)) {
                UsernamePasswordAuthenticationToken authentication = jwtTokenUtil.getAuthentication(authToken, SecurityContextHolder.getContext().getAuthentication(), userDetails);
                //UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")));
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                logger.info("authenticated user " + username + ", setting security context");
                SecurityContextHolder.getContext().setAuthentication(authentication);
                isJwt = true;
                if (cookieTrue) {
                    setCookieJwt(req, res, authToken);
                } else {
                    req.setAttribute("authToken", header);
                }
            }

        }
        req.setAttribute("jwt", isJwt);
        chain.doFilter(req, res);
        //TODO logout logic for one time session(stateless)
        if (isJwt) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                new SecurityContextLogoutHandler().logout(req, res, auth);
            }
        }
    }

    private void setCookieJwt(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String jwtToken) {
        String cookiePath = httpRequest.getContextPath();
        Cookie cookie = new Cookie("authToken", mapFromClasspathPropertiesFile.get("token_prefix") + jwtToken);
        cookie.setPath(cookiePath);
        httpResponse.addCookie(cookie);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return matchPath(request.getServletPath());
    }

    public boolean matchPath(String requestedUrl) {
        for (String pattern : ignoreUrls) {
            AntPathMatcher antPathMatcher = new AntPathMatcher();
            if (antPathMatcher.match(pattern, requestedUrl)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isAuthentication(HttpServletRequest request) {
        String token = request.getParameter("authToken");
        return false;
    }

    @Override
    public void destroy() {
        super.destroy();
    }


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