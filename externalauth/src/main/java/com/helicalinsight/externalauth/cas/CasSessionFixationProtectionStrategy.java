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

package com.helicalinsight.externalauth.cas;

import org.jasig.cas.client.session.SessionMappingStorage;
import org.jasig.cas.client.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

/**
 * Created by user on 2/10/2016.
 *
 * @author Somen
 */

public class CasSessionFixationProtectionStrategy extends SessionFixationProtectionStrategy implements
        InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(CasSessionFixationProtectionStrategy.class);
    private SessionMappingStorage sessionMappingStorage;
    private String artifactParameterName = "ticket";
    private String logoutParameterName = "logoutRequest";
    private boolean artifactParameterOverPost = false;
    private List<String> safeParameters;

    public void onAuthentication(Authentication authentication, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.onAuthentication(authentication, request, response);

        HttpSession newSession = request.getSession();
        String token = CommonUtils.safeGetParameter(request, this.artifactParameterName, this.safeParameters);
        logger.debug("Recording the new session after the previous one was destroyed to prevent session fixation " +
                "(token " + token + ").");
        if ((token != null) && (!token.trim().isEmpty())) {
            this.sessionMappingStorage.addSessionById(token, newSession);
        }
    }

    public void setSessionMappingStorage(SessionMappingStorage casSessionMappingStorage) {
        this.sessionMappingStorage = casSessionMappingStorage;
    }

    public void setArtifactParameterName(String artifactParameterName) {
        this.artifactParameterName = artifactParameterName;
    }

    public void setArtifactParameterOverPost(boolean artifactParameterOverPost) {
        this.artifactParameterOverPost = artifactParameterOverPost;
    }

    public void setLogoutParameterName(String logoutParameterName) {
        this.logoutParameterName = logoutParameterName;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.sessionMappingStorage, "sessionMappingStorage property must be specified.  It should be "
                + "the same sessionMappingStorage as that used by CAS SingleSignOutFilter");
        if (this.artifactParameterOverPost) {
            this.safeParameters = Arrays.asList(new String[]{this.logoutParameterName, this.artifactParameterName});
        } else {
            this.safeParameters = Arrays.asList(new String[]{this.logoutParameterName});
        }
    }
}
