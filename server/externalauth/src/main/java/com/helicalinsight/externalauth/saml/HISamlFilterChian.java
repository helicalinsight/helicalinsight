package com.helicalinsight.externalauth.saml;

import com.helicalinsight.admin.RestRedirectStrategy;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * HISamlFilterChian extends {@link FilterChainProxy}
 * This class is responsible for filtering incoming requests,response.
 * It handles SAML-related authentication and redirection logic
 * Created by helical021 on 2/9/2021.
 */
public class HISamlFilterChian extends FilterChainProxy {
    private static final Logger logger = LoggerFactory.getLogger(HISamlFilterChian.class);

    /**
     * doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
     * This method checks incoming requests,response and handles SAML-related authentication and redirection logic
     * @param request              provides session object and URI details
     * @param response             used for redirection to html page
     * @param chain                filter chain takes the request and response objects to process these objects as they flow through the chain of filters.
     * @throws IOException         If an I/O error occurs during the filter chain.
     * @throws ServletException    If a servlet exception occurs during the filter chain.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        try {


            super.doFilter(request, response, chain);
        } catch (ClassCastException cce) {
            String path="";
            if (request instanceof HttpServletRequest) {

                HttpServletRequest request1 = (HttpServletRequest) request;
                HttpSession session = request1.getSession(false);
                path=request1.getRequestURI();
                if (session != null) {
                    session.setAttribute("ssoLogin", Boolean.FALSE);
                }
            }
            HttpServletResponse res = (HttpServletResponse) response;
            if(path.contains("j_spring_")) {
                if (ControllerUtils.isAjax((HttpServletRequest) request)) {
                    RestRedirectStrategy.sendRedirect((HttpServletRequest) request, (HttpServletResponse) response);
                    return;
                }else {
                    res.sendRedirect("welcome.html");
                }
            }else {
                chain.doFilter(request, response);
            }
        }
    }
}
