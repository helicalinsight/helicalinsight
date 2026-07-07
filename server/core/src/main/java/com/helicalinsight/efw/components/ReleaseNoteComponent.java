package com.helicalinsight.efw.components;

import java.nio.file.Path;
import java.util.Properties;

import org.springframework.core.io.FileSystemResource;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helicalinsight.admin.utils.JacksonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.serviceframework.IComponent;

public class ReleaseNoteComponent implements IComponent {

	private static final ApplicationProperties properties = ApplicationProperties.INSTANCE;

	@Override
	public String executeComponent(String jsonFormData) {

		Properties props = new Properties();
		Path filePath = Path.of(properties.getSystemDirectory(), "Admin", "releaseNote.properties");
		ObjectNode response = JacksonUtility.emptyNode();
		try {
			FileSystemResource propertyFile = new FileSystemResource(filePath.toString());
			props.load(propertyFile.getInputStream());
			for (String key : props.stringPropertyNames()) {
				response.put(key, props.getProperty(key));
			}
		} catch (Exception e) {
			throw new EfwServiceException(e.getMessage());
		}

		return response.toString();

	}

	@Override
	public boolean isThreadSafeToCache() {
		return true;
	}

}
