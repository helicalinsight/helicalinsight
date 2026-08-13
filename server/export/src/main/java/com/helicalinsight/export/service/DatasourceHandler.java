package com.helicalinsight.export.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.JsonObject;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.utility.JsonUtils;

import  java.util.List;
/**
 * Abstract class for handling datasource-related operations. 
 * Extends {@link ResourceIOHandler} for common resource I/O functionality.
 */
public abstract class DatasourceHandler extends ResourceIOHandler {
	/**
     * Autowired DatasourceShareHandler for handling datasource sharing.
     */
	@Autowired
	protected DatasourceShareHandler shareHandler;
	/**
     * Constant string representing the postfix for datasource files.
     */
	protected static final String POSTFIX = "_datasource";

	public List<String> importResourceHCR( String dsFileName, String onConflict){
		return  null;
	}
}
