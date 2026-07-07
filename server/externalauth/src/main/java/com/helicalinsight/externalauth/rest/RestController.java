package com.helicalinsight.externalauth.rest;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.dao.UserDao;
import com.helicalinsight.admin.filter.PreAuthenticationFilter;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.datasource.managed.JsonUtils;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;


/**
 * RestController
 * this controller class deal  with login and logout restApi end points
 * This class manages user authentication, session management, and logout functionality.
 */
@SuppressWarnings("unused")
@Controller
public class RestController {

    private static final Logger logger = LoggerFactory.getLogger(RestController.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * getLoginAuthentication(@RequestParam("username") String username,
                                                    @RequestParam("password") String password, ModelMap model,
                                                    HttpServletRequest request, HttpServletResponse response) 
     * This method performs user authentication, manages session, and returns relevant data in the response.                                               
     * @param username            default username
     * @param password			default password
     * @param model                 spring ModelMap
     * @param request				it provides session object, details about the request, handles authentication
     * @param response				set headers,cookies, define the content type, and provides the response data, 
     * @return A ResponseEntity containing the authentication response data
     * @throws IOException			if an I/O exception occurs.
     */
    @RequestMapping(value = "/rest/login", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public ResponseEntity<?> getLoginAuthentication(@RequestParam("username") String username,
                                                    @RequestParam("password") String password, ModelMap model,
                                                    HttpServletRequest request, HttpServletResponse response) throws IOException {
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username,
                password);
        authRequest.setDetails(new WebAuthenticationDetails(request));
        String method = request.getMethod();
        PreAuthenticationFilter preAuthenticationFilter = new PreAuthenticationFilter();
        if (method.equalsIgnoreCase("get")) {
            logger.debug("GET requested");
            preAuthenticationFilter.setPostOnly(false);
        }
        try {
            preAuthenticationFilter.setAuthenticationManager(authenticationManager);
            preAuthenticationFilter.attemptAuthentication(request, response);
        } catch (Exception e) {
            return JsonUtils.get500ErrorResponse(e);
        }
        HttpSession session = request.getSession();
        String jSessionId = session.getId();
        response.setHeader("Cookie", "JSESSIONID=" + jSessionId);
        response.setHeader("Set-Cookie", "JSESSIONID=" + jSessionId);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setContentType(ControllerUtils.defaultContentType());

        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("Cookie", "JSESSIONID=" + jSessionId);
        responseJson.addProperty("Set-Cookie", "JSESSIONID=" + jSessionId);
        responseJson.addProperty("Access-Control-Allow-Credentials", "true");
        responseJson.addProperty("Content-Type", "application/json");
        long currentTime = System.currentTimeMillis();
        responseJson.addProperty("currentTime", currentTime);
        responseJson.addProperty("serverTime", currentTime);
        int maxInactiveInterval = session.getMaxInactiveInterval();
        long expiryTime = currentTime + maxInactiveInterval * 1000;
        responseJson.addProperty("sessionExpiry", expiryTime);
        return ResponseEntity.ok().body(responseJson);
        //return responseJson.toString();
    }

    /**
     * findUserByNameAndOrgNull(String username)
     * method return user object by user name with null
     * organization from database
     * @param username    user name
     * @return user object
     */
    @Transactional
    public User findUserByNameAndOrgNull(String username) {
        return userDao.findUserByNameNorgNull(username,true);
    }
    /**
     * restLogout(HttpServletRequest request, HttpServletResponse response)
     * This method is responsible for ending the user's authenticated session and clearing session-related data.
     * @param request          Used to access session and clearing data.
     * @param response		   Used to send headers and handle the session invalidation.
     */
    @RequestMapping(value = "/rest/logout", method = {RequestMethod.POST, RequestMethod.GET})
    public void restLogout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().invalidate();
    }
}
