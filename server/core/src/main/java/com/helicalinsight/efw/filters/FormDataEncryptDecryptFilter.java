package com.helicalinsight.efw.filters;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.utility.JsonUtils;

@Component("formDataEncryptDecryptFilter")
public class FormDataEncryptDecryptFilter implements Filter {

	private static List<String> encryptedParametersList ;
    private static final Logger logger = LoggerFactory.getLogger(FormDataEncryptDecryptFilter.class);


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //NOP
    }
static{
	encryptedParametersList =JsonUtils.getEncryptedParametersList("encryptedParameters");
}

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {

        chain.doFilter(new FilteredRequest(request), response);
    }

    public void destroy() {
    }

    static class FilteredRequest extends HttpServletRequestWrapper {

        public FilteredRequest(ServletRequest request) {
            super((HttpServletRequest) request);
        }


        public String getParameter(String paramName) {
            String value = super.getParameter(paramName);
            boolean paramFlag=false;
            for(String stringParam :encryptedParametersList){
            		paramFlag=(paramFlag ||stringParam.equalsIgnoreCase(paramName));
            	if(paramFlag){
            		
                        value= sanitize(value);
                  
            		break;
            	}
            }
            return value;
        }

        public String sanitize(String input) {
            String result = null;
            if (input != null && Base64.isBase64(input)) {
                Base64 base64 = new Base64();
                try {
                    result = new String(base64.decode(input), ControllerUtils.defaultCharSet());
                }catch(Exception ignore){
                   logger.error("Encoding exception occurred "+ignore);
                }
                return result;
            }else {
                return input;
            }
        }

        public String[] getParameterValues(String paramName) {
            String values[] = super.getParameterValues(paramName);
            if(values==null){
                return null;
            }
            boolean paramFlag=false;
            for(String stringParam :encryptedParametersList){
            		paramFlag=(paramFlag ||stringParam.equalsIgnoreCase(paramName));
            	if(paramFlag){
            		for (int index = 0; index < values.length; index++) {
                        values[index] = sanitize(values[index]);
                    }
            		break;
            	}
            }
          
            return values;
        }
    }

}