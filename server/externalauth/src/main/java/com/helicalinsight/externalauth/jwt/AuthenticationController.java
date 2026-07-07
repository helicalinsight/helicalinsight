package com.helicalinsight.externalauth.jwt;


import com.google.gson.JsonObject;
import com.helicalinsight.admin.customauth.CipherUtils;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.datasource.managed.JsonUtils;
import com.helicalinsight.efw.utility.ConfigurationFileReader;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import com.helicalinsight.admin.exception.AuthenticationException;


/**
 * AuthenticationController
 * This class represents a component that handles user authentication and token management for a web application.
 * and using HTTP requests to interact with authentication and logout endpoints. 
 * {@code @CrossOrigin(origins = "*")} Indicates that the annotated controller method or class supports Cross-Origin Resource Sharing (CORS),
 * allowing web clients from different domains to access and consume resources.
 * @author Rajesh
 * Created by author on 17/7/2019.
 */

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/rest")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
    Map<String, String> mapFromClasspathPropertiesFile = ConfigurationFileReader.getProjectPropertiesFile();
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenProvider jwtTokenUtil;
    @Autowired
    private TokenProvider tokenProvider;
    
    /**
     * register(@RequestBody User loginUser, HttpServletRequest httpRequest, HttpServletResponse httpResponse)
     *
     * @param loginUser              provides user object
     * @param httpRequest			 it provides url for creting auth token
     * @param httpResponse			 response sets various response parameters,
	 *                               headers, and content.
     * @return auth token , {@code Exception} if username and password is incorrect or null or empty.
     * @throws AuthenticationException if The username and password for mocking is incorrect
     */
    @RequestMapping(value = "/authToken", method = RequestMethod.POST,produces = "application/json")
    public ResponseEntity<?> register(@RequestBody User loginUser, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws AuthenticationException {
        String TOKEN_PREFIX = mapFromClasspathPropertiesFile.get("token_prefix");
        Authentication authentication;
        try {
            if (StringUtils.isBlank(loginUser.getPassword().trim())) {
                throw new AuthenticationException("Password Cannot Be Empty");
            }
            String username = loginUser.getUsername();
            String parentPassword = CipherUtils.decrypt(loginUser.getPassword());
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            username,
                            parentPassword
                    )
            );

            String authenticateUser = loginUser.getAuthenticateUser();
            if (!StringUtils.isBlank(authenticateUser)) {
                StringBuffer requestURL = httpRequest.getRequestURL();
                String url = requestURL.substring(0, requestURL.indexOf("/rest"));
                if (isMockable(authenticateUser, username, parentPassword, url)) {
                    authentication = authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    authenticateUser, "")
                    );
                } else {
                    throw new AuthenticationException("The username and password for mocking is incorrect");
                }
            }
        } catch (Exception e) {
            return JsonUtils.get500ErrorResponse(e);
        }
        //todo need to send 500 error type if authentication fails.
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String token = jwtTokenUtil.generateToken(authentication);
        Date issueDateFromToken = tokenProvider.getIssueDateFromToken(token);
        Date expirationDateFromToken = tokenProvider.getExpirationDateFromToken(token);
        return ResponseEntity.ok()
                .header("token-issue", issueDateFromToken.toString())
                .header("token-expiration", expirationDateFromToken.toString())
                .header("token", TOKEN_PREFIX + token)
                .body(new AuthToken(TOKEN_PREFIX + token, issueDateFromToken, expirationDateFromToken));
    }

    /**
     * jwtLogin( String jwtToken, HttpServletRequest httpRequest, HttpServletResponse httpResponse)
     * @param jwtToken             jwt token in String format
     * @param httpRequest		   provides the portion of the request URI that indicates the context of the request
     * @param httpResponse		   stores cookies
     * @return ResponseEntity indicating that the JWT token was set cookie or created successfully.
     * @throws AuthenticationException If an authentication error occurs during the jwtLogin registration process.
     */
    @RequestMapping(value = "/jwtLogin", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public ResponseEntity<?> jwtLogin(@RequestParam("jwtToken") String jwtToken,
                                      HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws AuthenticationException {

        String cookiePath = httpRequest.getContextPath();
        Cookie cookie = new Cookie("authToken", jwtToken);

        cookie.setPath(cookiePath);
        httpResponse.addCookie(cookie);
        JsonObject response = new JsonObject();
        response.addProperty("jwtset", true);
        return ResponseEntity.ok().body(response.toString());
    }
    /**
     * jwtLogout(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
     * the method responsible for jwt log out.
     * @param httpRequest       request object provides context path and cookies
     * @param httpResponse      response collects the cookies
     * @return ResponseEntity indicating that JWT authentication tokens were successfully erased.
     * @throws AuthenticationException
     */
    @ResponseBody
    @RequestMapping(value = "/jwtLogout", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<?> jwtLogout(
            HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws AuthenticationException {
        String cookiePath = httpRequest.getContextPath();
        Cookie[] cookies = httpRequest.getCookies();
        if (cookies != null)
            for (Cookie cookie : cookies) {
                cookie.setValue("");

                cookie.setPath(cookiePath);
                cookie.setMaxAge(0);
                httpResponse.addCookie(cookie);
            }
        JsonObject response = new JsonObject();
        response.addProperty("jwterased", true);
        return ResponseEntity.ok().body(response.toString());
    }
    /**
     * isMockable(String authenticationUser, String parentUser, String parentPassword, String url)
     * @param authenticationUser        username for which mock impersonation is being checked.
     * @param parentUser                username used for authentication.
     * @param parentPassword			user's password used for authentication.
     * @param url                       URL to send the mock impersonation request.
     * @return {@code true} if mock impersonation is successful, @code false} otherwise.
     */
    private boolean isMockable(String authenticationUser, String parentUser, String parentPassword, String url) {
        String mockUrl = url + "/mock/impersonate?username=" + authenticationUser + "&username=" + parentUser + "+&password=" + parentPassword;
        HttpClient client = HttpClientBuilder.create().build();
        CookieStore cookieStore = new BasicCookieStore();
        HttpContext httpContext = new BasicHttpContext();
        httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);
        HttpGet httpGet = new HttpGet(mockUrl);
        try {
            HttpResponse execute = client.execute(httpGet, httpContext);
            int response = execute.getStatusLine().getStatusCode();
            if (response == 200) {
                return true;
            }
        } catch (IOException e) {
            logger.error("Error Occurred",e);
        } finally {
            doLogout(url);
        }
        return false;
    }

    /**
     * doLogout(String logoutUrl)
     * Performs a logout operation by sending a logout request to the specified logout URL.
     * @param logoutUrl    URL where the logout request should be sent
     */
    public static void doLogout(String logoutUrl) {
        try {
            logoutUrl += "/logout";
            HttpURLConnection connection = (HttpURLConnection) new URL(logoutUrl).openConnection();
        } catch (IOException e) {
            logger.error("Error while logging out ", e);
        }
    }

}
