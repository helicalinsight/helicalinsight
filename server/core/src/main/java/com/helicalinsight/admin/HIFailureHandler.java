package com.helicalinsight.admin;

import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class HIFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    protected final Log logger = LogFactory.getLog(this.getClass());


    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        if (ControllerUtils.isAjax(request)) {
            PrintWriter out = null;
            try {
                 out = response.getWriter();
                JSONObject responseJson = new JSONObject();
                String message = exception.getMessage()==null?"":exception.getMessage();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                responseJson.put("status", "0");
                responseJson.put("message", "Login Failed." +message);
                response.setHeader("Content-Type", ControllerUtils.defaultContentType());
                out.print(responseJson.toString());
            } catch (IOException ignore) {
            } finally {
                ApplicationUtilities.closeResource(out);
            }
        }else {
            super.onAuthenticationFailure(request, response, exception);
        }
    }
}
