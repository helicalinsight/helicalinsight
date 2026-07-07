package com.helicalinsight.export.handler.importres;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
/**
 * Factory class for creating instances of AbstractResourceImportHandler based on file extension.
 */
public class ImportHandlerFactory {

	private ImportHandlerFactory() {

	}
	/**
     * Retrieves an AbstractResourceImportHandler based on the provided file extension.
     * @param extension 			file extension
     * @return an instance of AbstractResourceImportHandler, or null if not found
     */
	public static AbstractResourceImportHandler getHandler(String extension) {
		try {
			return (AbstractResourceImportHandler) ApplicationContextAccessor.getBean(extension + "ImportHandler");
		} catch (NoSuchBeanDefinitionException e) {
			return null;
		}
	}
}
