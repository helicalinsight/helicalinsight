package com.helicalinsight.efw;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.utility.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Somen Created by helical021 on 3/5/2018.
 */
public class BackgroundService {
	private static final Logger logger = LoggerFactory.getLogger(BackgroundService.class);
	private static JsonObject settings = JsonUtils.newGetSettingsJson();
	private static final String START = "start";
	private static final String STOP = "stop";

	public static void stopAllService() {
		JsonArray backgroundServices = getBackgroundServices();
		if (backgroundServices == null)
			return;
		startStopService(backgroundServices, STOP);

	}

	private static JsonArray getBackgroundServices() {
		JsonArray backgroundServices = GsonUtility.optJsonArray(settings, "BackgroundServices");
		if (backgroundServices == null || backgroundServices.isEmpty()) {
			logger.info("No Background services found");
			return null;
		}
		return backgroundServices;
	}

	private static void startStopService(JsonArray backgroundServices, String command) {
		for (int counter = 0; counter < backgroundServices.size(); counter++) {

			JsonObject serviceClass = backgroundServices.get(counter).getAsJsonObject();
			String clazz = serviceClass.get("class").getAsString();
			if (clazz == null || clazz.isEmpty()) {
				continue;
			}
			IBackgroundService iBackgroundService = null;
			try {
				Class c = FactoryMethodWrapper.forName(clazz);
				iBackgroundService = (IBackgroundService) c.newInstance();
			} catch (ClassNotFoundException e) {
				logger.error("Class Not Found " + clazz);
			} catch (InstantiationException e) {
				logger.error("Could not instantiate " + clazz);
			} catch (IllegalAccessException e) {
				logger.error("Illegal Access " + clazz);
			}

			if (command.equalsIgnoreCase(STOP)) {
				logger.info("Stopping service for the class " + clazz);
				iBackgroundService.stop();
				iBackgroundService.cleanUp();
			} else if (command.equalsIgnoreCase(START)) {
				logger.info("Starting service for the class " + clazz);
				if (iBackgroundService.setUp()) {
					iBackgroundService.start();
				}

			}
		}
	}

	public static void startAllService() {
		JsonArray backgroundServices = getBackgroundServices();
		if (backgroundServices == null)
			return;
		startStopService(backgroundServices, START);
	}
}
