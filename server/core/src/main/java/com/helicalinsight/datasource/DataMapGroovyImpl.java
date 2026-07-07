package com.helicalinsight.datasource;

import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

/** 
 * DataMapGroovyImpl
 * This class is responsible for processing the GroovyScript and
 * returns the result. This class is one of the implementation of
 * {@link IDataMap}
 * @creation Date:09-02-2018
 */

public class DataMapGroovyImpl implements IDataMap {

	private static final Logger logger = LoggerFactory.getLogger(DataMapGroovyImpl.class);

	/**
	 * getResultSet(JSONObject dataMapTagContent, JSONObject queryParameters, Connection con)
	 * @param dataMapTagContent           from the *.efwd file.
	 * @param queryParameters			  from the *.efwd file.
	 * @param Connection				   which is not required in these class.
	 * @return the Groovy Script result in form of JSONObject.
	 */
	@Override
	public JsonObject getResultSet(JsonObject dataMapTagContent, JsonObject queryParameters, Connection con) {
		EfwdQueryProcessor queryProcessor = ApplicationContextAccessor.getBean(EfwdQueryProcessor.class);
		String groovyScript = queryProcessor.getQuery(dataMapTagContent, queryParameters);

		Binding binding = new Binding();
		binding.setVariable("dataMap", dataMapTagContent);
		binding.setVariable("queryParams", queryParameters);
		GroovyShell shell = new GroovyShell(binding);
		String result = shell.evaluate(groovyScript).toString();
		logger.debug("data  :" + result);
		return new Gson().fromJson(result, JsonObject.class);
	}

}
