package com.helicalinsight.filter;

import java.io.IOException;
import java.util.Map;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.audit4j.core.AuditManager;
import org.audit4j.core.dto.EventBuilder;

/**
 * The Class Auditfilter.
 * 
 * @author <a href="mailto:janith3000@gmail.com">Janith Bandara</a>
 */
@Deprecated
//TODO: Marked for removal
public class AuditFilter implements Filter {

    /** The user session attr name. */
    private String userSessionAttrName = null;

    /*
     * (non-Javadoc)
     * 
     * @see jakarta.servlet.Filter#init(jakarta.servlet.FilterConfig)
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        userSessionAttrName = filterConfig.getServletContext().getInitParameter("userSessionAttrName");

    }

    /*
     * (non-Javadoc)
     * 
     * @see jakarta.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see jakarta.servlet.Filter#doFilter(jakarta.servlet.ServletRequest,
     * jakarta.servlet.ServletResponse, jakarta.servlet.FilterChain)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest request = (HttpServletRequest) req;

        String actor = null;

        if (userSessionAttrName != null && !userSessionAttrName.equals("")) {
            HttpSession session = request.getSession(false);
            actor = (String) session.getAttribute("userSessionAttrName");
        }
        String ipAddress = request.getRemoteAddr();
        String url = request.getRequestURL().toString();

        EventBuilder builder = new EventBuilder();
        builder.addAction(url).addOrigin(ipAddress);
        if (actor == null) {
            builder.addActor(ipAddress);
        } else {
            builder.addActor(actor + "[" + ipAddress + "]");
        }

        Map<String, String[]> params = req.getParameterMap();

        for (final Map.Entry<String, String[]> entry : params.entrySet()) {
            builder.addField(entry.getKey(), entry.getValue());
        }

        AuditManager.getInstance().audit(builder.build());
        chain.doFilter(req, res);
    }

}