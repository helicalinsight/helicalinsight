package com.helicalinsight.efw.jasperintegration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceHCR;
import com.helicalinsight.admin.service.ComponentFactory;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.serviceframework.IComponent;


@Component
public class HCRCompiler {
	
	
	private final HIResourceServiceDB hiResourceServiceDB;
	
	private static final Logger logger = LoggerFactory.getLogger(HCRCompiler.class);
	
	
	public HCRCompiler(HIResourceServiceDB hiResourceServiceDB) {
		this.hiResourceServiceDB = hiResourceServiceDB;
	}
	
	public String compile(CompileRequest request) {
		
		Integer resourceId = request.resourceId();
		Boolean generateXML = request.generateXML();
		
		HIResource hiResource = null;

		try {
			hiResource =  hiResourceServiceDB.getHIResourceById(resourceId);
		}
		catch (EfwServiceException  ex) {
			logger.warn("Resource with id {}  not found.", resourceId);
			return "";
		}

		HIResourceHCR hcrResource =  hiResource.getHiResourceHCR();
		String previewFormData = addBooleanPropertyInRoot(hcrResource.getPreviewFormData(), "compile");
		IComponent component = ComponentFactory.getComponentInstance("com.helicalinsight.adhoc.services.GenerateHCReport");
		
		
		if( generateXML ) {
			Pattern pattern = Pattern.compile("\"generateXML\"\\s*:\\s*(true|false)", Pattern.MULTILINE);
			Matcher matcher = pattern.matcher(previewFormData);
			if (matcher.find()) {
				previewFormData = matcher.replaceFirst("\"generateXML\": true");
			} else {
				previewFormData = previewFormData.replaceAll("(?m)^\\s*\\}", ",\n  \"generateXML\": true\n}");
			}
		}
		
		String response = component.executeComponent(previewFormData);
		
		Pattern pattern = Pattern.compile("\"jrxmlData\"\\s*:\\s*\\{[^}]*?\"compiledFilePath\"\\s*:\\s*\"([^\"]+)\"");
	    Matcher matcher = pattern.matcher(response);
	    return   matcher.find() ? matcher.group(1).replace('\\', '/') : "";
	}
	
	private String addBooleanPropertyInRoot(String formData, String property) {
		String json = formData.trim();
		if( json.startsWith("{")  && json.endsWith("}")) {
			String newProperty = ",\""+property+"\" : true" ;
			return json.substring(0,json.length()-1) + newProperty + "}" ;
		}
		return formData;
	}

}
