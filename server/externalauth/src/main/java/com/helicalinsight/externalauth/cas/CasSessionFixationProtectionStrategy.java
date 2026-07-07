package com.helicalinsight.externalauth.cas;

import org.apereo.cas.client.session.SessionMappingStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.util.Assert;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

/**
 * CasSessionFixationProtectionStrategy extends {@link SessionFixationProtectionStrategy} and implements {@link InitializingBean} 
 * Creates a new session for the newly authenticated user if they already have a session
 * (as a defence against session-fixation protection attacks), and copies their session
 * attributes across to the new session.
 * and to perform custom initialization,or to check that all mandatory properties have been set. 
 * Created by user on 2/10/2016.
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

    /**
     * onAuthentication(Authentication authentication, HttpServletRequest request,
                                 HttpServletResponse response)
     * Called when a user is newly authenticated.
     * If a session already exists, and matches the session Id from the client, a new
	 * session will be created.
	 * if session id is invalid nothing will done.
	 * @param authentication            auth token token for an authentication request
	 * @param  request                  provides session object and session id
	 * @param  response                 returns respective response.
     */
    public void onAuthentication(Authentication authentication, HttpServletRequest request,
                                 HttpServletResponse response) {
        super.onAuthentication(authentication, request, response);

        HttpSession newSession = request.getSession();
//        String token = CommonUtils.safeGetParameter(request, this.artifactParameterName, this.safeParameters);
//        logger.debug("Recording the new session after the previous one was destroyed to prevent session fixation " +
//                "(token " + token + ").");
//        if ((token != null) && (!token.trim().isEmpty())) {
//            this.sessionMappingStorage.addSessionById(token, newSession);
//        }
    }

    /**
     * setSessionMappingStorage(SessionMappingStorage casSessionMappingStorage)
     * Stores the mapping between sessions and keys to retrieved later
     * @param casSessionMappingStorage  contains sessions and keys
     */
    public void setSessionMappingStorage(SessionMappingStorage casSessionMappingStorage) {
        this.sessionMappingStorage = casSessionMappingStorage;
    }

    /**
     * setArtifactParameterName(String artifactParameterName)
     * @param artifactParameterName    to set name of the artifact parameter
     */
    public void setArtifactParameterName(String artifactParameterName) {
        this.artifactParameterName = artifactParameterName;
    }
    /**
     * setArtifactParameterOverPost(boolean artifactParameterOverPost)
     * @param boolean artifactParameterOverPost   {@code true} if the artifact parameter is sent over POST, {@code false} otherwise
     */
    public void setArtifactParameterOverPost(boolean artifactParameterOverPost) {
        this.artifactParameterOverPost = artifactParameterOverPost;
    }
    /**
     * setLogoutParameterName(String logoutParameterName)
     *  Sets the name of the logout parameter used in the request.
     * @param logoutParameterName     name of the logout parameter in string
     */
    public void setLogoutParameterName(String logoutParameterName) {
        this.logoutParameterName = logoutParameterName;
    }
    /**
     * afterPropertiesSet() 
     * it checks stored session properties be same that used by CAS SingleSignOutFilter
     * and initializes the properties after that have been set.
     *  @throws Exception if the sessionMappingStorage property is not specified.
     */
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
