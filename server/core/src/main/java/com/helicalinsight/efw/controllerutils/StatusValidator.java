package com.helicalinsight.efw.controllerutils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.model.LicenseMetadata;

import jakarta.servlet.ServletContext;

/**
 * Created by user on 3/21/2017.
 *
 * @author Rajasekhar
 */
@Component
public class StatusValidator {
    @Autowired
    private  ServletContext servletContext;
    private Logger logger = LoggerFactory.getLogger(StatusValidator.class);
    private final LicenseMetadata metadata = ApplicationProperties.getInstance().getLicenseMetadata();
    
	public boolean isStatusNotOkay() {
		synchronized (this.servletContext) {
			if (metadata != null) {
				Object status = this.servletContext.getAttribute("filterStatus");
				logger.info("HI Context is properly " + (status == null ? "not initialized" : "initialized"));
				if (status == null) {
					logger.info("Incorrect application settings. Returning null.");
					return true;
				}
			}
		}
		return false;
	}
}
