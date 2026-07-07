package com.helicalinsight.externalauth.saml;

import org.springframework.security.web.FilterChainProxy;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;

/**
 * HISamlProcessingFilter extends {@link FilterChainProxy}
 * This filter class is a placeholder for processing SAML-related authentication and authorization during the filter chain.
 * It allows the filter chain to continue processing without any additional logic.
 * Created by helical021 on 2/9/2021.
 */
public class HISamlProcessingFilter extends FilterChainProxy {
	/**
     * doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
     * Placeholder method for processing SAML-related authentication and authorization during the filter chain.
     * @param request              provides session object and URI details
     * @param response             used for redirection to html page
     * @param chain                filter chain takes the request and response objects to process these objects as they flow through the chain of filters.
     * @throws IOException         If an I/O error occurs during the filter chain.
     * @throws ServletException    If a servlet exception occurs during the filter chain.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        super.doFilter(request, response, chain);
    }
}
