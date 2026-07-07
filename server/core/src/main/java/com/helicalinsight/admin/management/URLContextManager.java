package com.helicalinsight.admin.management;

import java.io.File;
import java.util.List;

import org.springframework.stereotype.Component;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.resourcesecurity.jaxb.Context;
import com.helicalinsight.resourcesecurity.jaxb.SubContext;
import com.helicalinsight.resourcesecurity.jaxb.UrlContexts;


@Component
public class URLContextManager {
	
	private  List<Context> configuredContexts;
	
	public List<Context> refreshUrlContext() {
		String systemDirectory = ApplicationProperties.getInstance().getSystemDirectory();
		String pathname = systemDirectory + File.separator + "Admin" + File.separator + "urlContexts.xml";
		File contextsXml = new File(pathname);

		if (contextsXml.exists()) {
			UrlContexts urlContexts = JaxbUtils.unMarshal(UrlContexts.class, contextsXml);
			if (urlContexts != null) {
				this.configuredContexts = urlContexts.getContexts();
			}
		}
		
		return configuredContexts;
	}

	public Context findUrlContext(String requestedUrl) {
		Context presentContext = null;
		String withoutHtml = requestedUrl.toLowerCase().replaceAll(".html", "");
		
		if ( configuredContexts == null ) {
			this.configuredContexts = refreshUrlContext();
		}
		
		for (Context context : configuredContexts) {
			String contextEndPoint = context.getName().toLowerCase();

			if (withoutHtml.equals(contextEndPoint)) {
				presentContext = context;
				break;
			}
		}
		return presentContext;
	}
	
	public SubContext findSubContext(List<SubContext> subContexts, String type, String serviceType, String service ) {
        if (type == null || serviceType == null || service == null) {
            throw new RequiredParameterIsNullException("The request is missing either or more" + " of type, " +
                    "serviceType, service parameters.");
        }

        for (SubContext subContext : subContexts) {
            if ((type.equalsIgnoreCase(subContext.getType())) && (serviceType.equalsIgnoreCase(subContext
                    .getServiceType())) && (service.equalsIgnoreCase(subContext.getService()))) {
                return subContext;
            }
        }
        return null;
   }

}
