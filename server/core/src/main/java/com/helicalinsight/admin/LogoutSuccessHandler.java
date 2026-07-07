package com.helicalinsight.admin;

import com.helicalinsight.efw.controllerutils.ControllerUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

   // Just for setting the default target URL
   public LogoutSuccessHandler(String defaultTargetURL) {
        this.setDefaultTargetUrl(defaultTargetURL);
       this.setRedirectStrategy(new RedirectStrategy() {
            @Override
            public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
                if (ControllerUtils.isAjax(request)) {
                    RestRedirectStrategy.sendRedirect(request, response);
                }else {
                    DefaultRedirectStrategy defaultRedirectStrategy = new DefaultRedirectStrategy();
                    defaultRedirectStrategy.sendRedirect(request,response,url);
                }
            }
        });
   }

   @Override
   public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // do whatever you want
        super.onLogoutSuccess(request, response, authentication);
   }
}