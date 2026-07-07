package com.helicalinsight.export.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import com.helicalinsight.datasource.GlobalJdbcType;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

/**
 * Factory class for obtaining instances of DatasourceHandler. 
 * Used to get handlers for exporting and importing datasources.
 */
public class DatasourceFactory {

	private static final Logger LOG = LoggerFactory.getLogger(DatasourceFactory.class);

	private DatasourceFactory() {

	}
	/**
	 * Method returns the instance of class.
	 * @param type     to check the content holding(Ex: folder, global.jdbc ).
	 * @return  instance or bean of of `efwdDSHandler` or `globalDSHandler` with respect to type of paramter.
	 */
	public static DatasourceHandler getHandler(String type) {
		DatasourceHandler handler = null;
		try {
			if ("folder".equalsIgnoreCase(type) || isEfwdConnection(type)) {
				handler = (DatasourceHandler) ApplicationContextAccessor.getBean("efwdDSHandler");
			} else if (type.equalsIgnoreCase("global.jdbc")) {
				handler = (DatasourceHandler) ApplicationContextAccessor.getBean("globalDSHandler");
			}
		} catch (NoSuchBeanDefinitionException e) {
			LOG.error("Bean not found: {}", e.getMessage());
			return null;
		}
		return handler;
	}
	/**
	 * Checks the JDBC type if it is present or not.
	 * @param connectionType       for checking JDBC type
	 * @return {@code true} if it is present , otherwise {@code false}.
	 */
	private static Boolean isEfwdConnection(String connectionType) {
		if ( connectionType.equalsIgnoreCase(GlobalJdbcType.PLAIN_JDBC)) return true;
		else if  ( connectionType.equalsIgnoreCase(GlobalJdbcType.GROOVY_DATASOURCE)) return true;
		else if ( connectionType.equalsIgnoreCase(GlobalJdbcType.MANAGED_GROOVY_DATASOURCE)) return true;
		else return false;
	}

}
