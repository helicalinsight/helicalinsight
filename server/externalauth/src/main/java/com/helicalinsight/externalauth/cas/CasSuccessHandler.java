package com.helicalinsight.externalauth.cas;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * class CasSuccessHandler extends {@link SavedRequestAwareAuthenticationSuccessHandler}
 * this Custom class represents success handler for CAS authentication.
 * This
 * class is responsible for performing the redirect to the original URL if appropriate successful authentication.
 * Created by user on 2/10/2016.
 * @author Somen
 */
public class CasSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    //private static final Logger logger = LoggerFactory.getLogger(CasSuccessHandler.class);
	
	/**
	 *  onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
     *	on Successful Authentication this method redirect to the Url of that original destination .         
	 * @param request            provides cas session object
     * @param response           returns respective response.
     * @param authentication     token for an authentication request.
     * @throws ServletException   If a servlet-specific error occurs.
     * @throws IOException        If an I/O error occurs.
     */
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
