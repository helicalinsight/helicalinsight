package com.helicalinsight.scheduling;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.serviceframework.IComponent;

/**
 * Created by author on 5/5/2020.
 * @author Rajesh
 * class CronExpressionGenerator implments {@link IComponent} interface
 * it checks formdata whether it contains schedule information or not
 */
public class CronExpressionGenerator implements IComponent {
	
	/**
	 * executeComponent(String jsonFormData)
	 * @param jsonFormData          formData in string format
	 * @return cornExpression string(jsonObject) format after generating cornExpression 
	 * @throws RequiredParameterIsNullException, if schedule is not present in formData
	 */
    @Override
    public String executeComponent(String jsonFormData) {
    	JsonObject formData = new Gson().fromJson(jsonFormData,JsonObject.class);
        JsonObject scheduleOptions = optJsonObject(formData,"ScheduleOptions");
        JsonObject response = new JsonObject();
        if (scheduleOptions == null) {
            throw new RequiredParameterIsNullException("Please provide the ScheduleOption json which is mandatory for cron generation");
        } else {
            ConvertIntoCronExpression cronExpressionGen = new ConvertIntoCronExpression();
            String generatedCronExp = cronExpressionGen.convertDateIntoCronExpression(scheduleOptions);
            response.addProperty("generatedCronExpression :", generatedCronExp);
        }
        return response.toString();
    }
    /**
     * optJsonObject(JsonObject jsonObject, String key)
     * This method checks key present in the jsonObject or not.
     * if it is present it returns JsonObject ,otherwise null.
     * @param jsonObject 		
     * @param key 				key of the JsonObject.
     * @return jsonObject		
     */
    private JsonObject optJsonObject(JsonObject jsonObject, String key) {
    	if (jsonObject != null) {
			JsonElement jsonElement = jsonObject.get(key);
			if (jsonElement != null && jsonElement.isJsonObject()) {
				return jsonElement.getAsJsonObject();
			}
		}
		return null;
	}

    /**
     * isThreadSafeToCache()
     * this method tells whether it is thread safe or not while involving that class in any multithreading task
     * @return {@code true} the implementation  class is thread-safe.
     */
	@Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
