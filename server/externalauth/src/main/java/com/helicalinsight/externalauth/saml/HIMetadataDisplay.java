//package com.helicalinsight.externalauth.saml;
//
//import com.helicalinsight.admin.model.Principal;
//import com.helicalinsight.admin.utils.ApplicationDefaultUserAndRoleNamesConfigurer;
//import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.saml.metadata.MetadataDisplayFilter;
//import org.springframework.security.web.FilterInvocation;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.ServletRequest;
//import jakarta.servlet.ServletResponse;
//import java.io.IOException;
//import java.util.Collection;
//
//public class HIMetadataDisplay extends MetadataDisplayFilter {
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//
//        FilterInvocation fi = new FilterInvocation(request, response, chain);
//
//        if (!processFilter(fi.getRequest())) {
//            chain.doFilter(request, response);
//            return;
//        }
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null) {
//            chain.doFilter(request, response);
//            return;
//        }
//        Principal activeUser = (Principal) authentication.getPrincipal();
//        Collection<GrantedAuthority> authorities = activeUser.getAuthorities();
//        ApplicationDefaultUserAndRoleNamesConfigurer namesConfigurer = ApplicationContextAccessor.getBean
//                (ApplicationDefaultUserAndRoleNamesConfigurer.class);
//        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(namesConfigurer.getRoleAdmin());
//        if (!(authorities != null && authorities.contains(simpleGrantedAuthority))) {
//            chain.doFilter(request, response);
//            return;
//
//        }
//
//        processMetadataDisplay(fi.getRequest(), fi.getResponse());
//
//    }
//}
