package com.helicalinsight.scheduling;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.scheduling.utils.ScheduleOperation;

/** 
 * class TriggerMigrationForScheduler implements {@link IComponent} interface.
 * Responsible for migration of schedules to database.
 * This class will be no longer used. migration feature is no longer used.
 * Created by author on 4/21/2020.
 * @author Rajesh
 */
@Deprecated
public class TriggerMigrationForScheduler implements IComponent {
	
	/**
	 * executeComponent(String jsonFormData)
     * Executes the migration process for scheduling.
     *
     * @param jsonFormData                 JSON data containing the necessary information for the migration process.
     * @return A JSON string with a message indicating the success or failure of the migration process.
     */
    @Override
    public String executeComponent(String jsonFormData) {
        ScheduleOperation scheduleOperation = ApplicationContextAccessor.getBean(ScheduleOperation.class);
        Boolean aBoolean = scheduleOperation.triggerMigrationProcessForScheduling(true);
        String message = "Unable to Migrate Schedules. please ensure that Scheduling.xml is exists and contains valid schedules.";
        if (aBoolean) {
            message = "Successfully Migrated the Schedules in database. Please restart the application to update the jobs into scheduler.";
        }
        JsonObject jsonData = new JsonObject();
        jsonData.addProperty("message", message);
        return jsonData.toString();
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
