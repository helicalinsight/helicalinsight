package com.helicalinsight.admin;

import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import net.sf.json.JSONObject;
import org.springframework.security.web.RedirectStrategy;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class RestRedirectStrategy {

    public static void sendRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = null;
        try {
            String message = "Login";
            String requestURL = request.getRequestURI();
            if(requestURL.contains("exit") || requestURL.contains("logout")){
                message="Logout";

            }

            out = response.getWriter();
            JSONObject responseJson = new JSONObject();
            responseJson.put("status", "1");
            responseJson.put("message", message+" success");
            response.setHeader("Content-Type", ControllerUtils.defaultContentType());
            out.print(responseJson.toString());
        } catch (IOException ignore) {
        } finally {
            ApplicationUtilities.closeResource(out);
        }
    }
}
