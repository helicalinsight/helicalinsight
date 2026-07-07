package com.helicalinsight.scheduling;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.utility.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ScheduleJobFactory
 * This class provides schedule class information, creating instances of schedule classes.
 * @author Somen
 * Created  on 3/9/2018.
 */
public class ScheduleJobFactory {
    private static final Logger logger = LoggerFactory.getLogger(ScheduleJobFactory.class);
    /**
     * getScheduleClass()
     * @return jsonArray of schedule data. if array is {@code null} then it returns new JsonArray.
     */
    private static JsonArray getScheduleClass() {
        JsonObject settings = JsonUtils.newGetSettingsJson();
        JsonArray scheduleImpl = optJsonArray(settings.getAsJsonObject("schedule"),"job");
        if (scheduleImpl == null || scheduleImpl.isEmpty()) {
            return new JsonArray();
        }
        return scheduleImpl;
    }

    /**
     * optJsonArray(JsonObject jsonObject, String key)
     * This method checks key present in the jsonObject or not.
     * if it is present it returns JsonArray ,otherwise null.
     * @param jsonObject 		The JsonObject.
     * @param key 				The key of the JsonObject.
     * @return JsonArray		
     */
    private static JsonArray optJsonArray(JsonObject jsonObject, String key) {
		if (jsonObject != null) {
			JsonElement jsonElement = jsonObject.get(key);
			if (jsonElement != null && jsonElement.isJsonArray()) {
				return jsonElement.getAsJsonArray();
			}
		}
		return null;
	}

    /**
     * getScheduleClass(String type)
     * @param type				schedule type
     * @return class name in String format if type matches the condition, otherwise it returns "com.helicalinsight.scheduling.ScheduleJob"
     */
	public static String getScheduleClass(String type) {
        JsonArray scheduleArray = getScheduleClass();
        for (int counter = 0; counter < scheduleArray.size(); counter++) {
            JsonObject serviceClass = scheduleArray.get(counter).getAsJsonObject();
            if (serviceClass.has("type") && serviceClass.get("type").getAsString().equalsIgnoreCase(type)) {
                return serviceClass.get("class").getAsString();
            }
        }
        return "com.helicalinsight.scheduling.ScheduleJob";
    }
	/**
	 * getIScheduleJobInstance(String className)
	 * Provides a Wrapper class for the reflection API usage
	 * @param className					class name to be used for reflection-based usage.
	 * @return An instance of the ISchedule .
	 * @throws RuntimeException If there is an issue with class loading, instantiation, or access.
     */
    public static ISchedule getIScheduleJobInstance(String className) {
    	

        try {
            Class<?> name = FactoryMethodWrapper.forName(className);
            return (ISchedule) name.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }


}
