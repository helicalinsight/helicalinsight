/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.externalauth.rest;

import com.helicalinsight.admin.dao.UserDao;
import com.helicalinsight.admin.filter.PreAuthenticationFilter;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.datasource.managed.JsonUtils;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import net.sf.json.JSONObject;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@SuppressWarnings("unused")
@Controller
public class RestController {

    private static final Logger logger = LoggerFactory.getLogger(RestController.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private AuthenticationManager authenticationManager;

    @RequestMapping(value = "/rest/login", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public ResponseEntity<?> getLoginAuthentication(@RequestParam("j_username") String j_username,
                                                    @RequestParam("j_password") String j_password, ModelMap model,
                                                    HttpServletRequest request, HttpServletResponse response) throws IOException {
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(j_username,
                j_password);
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

        JSONObject responseJson = new JSONObject();
        responseJson.put("Cookie", "JSESSIONID=" + jSessionId);
        responseJson.put("Set-Cookie", "JSESSIONID=" + jSessionId);
        responseJson.put("Access-Control-Allow-Credentials", "true");
        responseJson.put("Content-Type", "application/json");
        long currentTime = System.currentTimeMillis();
        responseJson.put("currentTime", currentTime);
        responseJson.put("serverTime", currentTime);
        int maxInactiveInterval = session.getMaxInactiveInterval();
        long expiryTime = currentTime + maxInactiveInterval * 1000;
        responseJson.put("sessionExpiry", expiryTime);
        return ResponseEntity.ok().body(responseJson);
        //return responseJson.toString();
    }

    @Transactional
    public User findUserByNameAndOrgNull(String username) {
        return userDao.findUserByNameNorgNull(username);
    }

    @RequestMapping(value = "/rest/logout", method = {RequestMethod.POST, RequestMethod.GET})
    public void restLogout(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().invalidate();
    }
}
