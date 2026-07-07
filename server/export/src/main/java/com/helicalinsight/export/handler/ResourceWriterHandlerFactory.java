package com.helicalinsight.export.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
/**
 * Factory class for obtaining resource writer handlers based on resource types.
 * It utilizes the Spring ApplicationContextAccessor to get the appropriate handler bean.
 * If a handler bean is not found for a given resource type, it falls back to using the NullHandler.
 */
public class ResourceWriterHandlerFactory {

	private static final Logger LOG = LoggerFactory.getLogger(ResourceWriterHandlerFactory.class);

	private ResourceWriterHandlerFactory() {

	}
	 /**
     * Gets the resource writer handler based on the provided resource type.
     *
     * @param resourceType 	 type of the resource for which the handler is needed.
     * @return The resource writer handler for the specified resource type.
     */
	public static AbstractResourceWriterHandler getHandler(String resourceType) {
		try {
			return (AbstractResourceWriterHandler) ApplicationContextAccessor.getBean(resourceType + "WriterHandler");
		} catch (NoSuchBeanDefinitionException e) {
			return ApplicationContextAccessor.getBean(NullHandler.class);
		}
	}

}
