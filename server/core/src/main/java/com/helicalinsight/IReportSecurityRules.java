package com.helicalinsight;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.resourceloader.rules.IRule;

/**
 * Created by Helical on 3/9/2021.
 */
@Deprecated
public interface IReportSecurityRules extends IRule {
	
	/**
	 * checkSqlInjection using gson
	 *  @param JsonObject formData
	 * @param String key
	 * @param Object obj
	 * @return JsonObject 
	 * @throws Exception
	 */
	public JsonObject checkSqlInjection(JsonObject formData, String key, Object obj) throws Exception;
}
