package com.helicalinsight.scheduling;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.helicalinsight.admin.model.Organization;
import com.helicalinsight.admin.model.Principal;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.utils.ApplicationDefaultUserAndRoleNamesConfigurer;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.scheduling.model.Schedules;
import com.helicalinsight.scheduling.service.SchedulesService;
import com.helicalinsight.scheduling.utils.ScheduleOperation;

import java.util.ArrayList;
import java.util.List;

/**
 * EnhancedSchedulerActionHandler is an extension of the {@link SchedulerActionHandler} class.
 * Class is responsible for scheduling action based on organization and user role.
 * Created by author on 3/30/2020.
 * @author Rajesh
 */
public class EnhancedSchedulerActionHandler extends SchedulerActionHandler {
	private ScheduleOperation scheduleOperation = ApplicationContextAccessor.getBean(ScheduleOperation.class);
	/**
	 * executeComponent(String jsonFormData)
	 * Executes scheduling-related action based on the provided JSON form data.
	 * @param jsonFormData     contains action related info like start, stop, pause to trigger job execution
	 * @return result of the executed action in string format.
	 */
	@Override
	public String executeComponent(String jsonFormData) {
		JsonObject formJson = new Gson().fromJson(jsonFormData, JsonObject.class);
		String action = GsonUtility.optString(formJson, "action");
		if (com.helicalinsight.efw.utility.JsonUtils.newIsScheduleStorageTypeIsDatabase()) {
			JsonObject responseJson;
			responseJson = new JsonObject();
			if ("list".equals(action)) {
				responseJson.add("scheduledList", filterSchedules());
				return responseJson.toString();
			} else {
				return handleScheduleAction(formJson).toString();
			}
		} else
			return super.executeComponent(jsonFormData);

	}
		/**
	 * filterSchedules()
     * Method is responsible for getting active/logged in users for schedules .
     *
     * @return A JsonArray containing jobId for schedules.
     */
	private JsonArray filterSchedules() {
		Principal activeUser = AuthenticationUtils.getUserDetails();
		User currentUser = activeUser.getLoggedInUser();
		List<JsonObject> listJobKeyForSchedules = listJobKeyForSchedules(currentUser);
		return addExtraInformation(listJobKeyForSchedules);
	}
	/**
	 * listJobKeyForSchedules(User user)
     * Generates a list of JsonObject instances representing job keys of schedules specific to a user's roles and organizations.
     *
     * @param user  			 User instance to get job keys.
     * @return A list of JsonObject instances representing job keys of schedules.
     */
	private List<JsonObject> listJobKeyForSchedules(User user) {
		ApplicationDefaultUserAndRoleNamesConfigurer namesConfigurer = ApplicationContextAccessor
				.getBean(ApplicationDefaultUserAndRoleNamesConfigurer.class);
		List<String> roleList = AuthenticationUtils.getUserRoles();
		boolean isAdmin = roleList.contains(namesConfigurer.getRoleAdmin());
		if (user.getOrganization() == null && isAdmin) {
			return findAllScheduleJobKeysForSchedules();
		} else if (isAdmin) {
			return findJobKeysSpecificToOrganizationForSchedules(user.getOrganization().getId());
		} else {
			return findJobKeysSpecificUserForSchedules(user.getId());
		}
	}
	/**
	 * findAllScheduleJobKeysForSchedules()
     * Returns a list of JsonObject instances of all schedule job keys.
     * @return A list of JsonObject instances representing job keys of all schedules.
     */
	private List<JsonObject> findAllScheduleJobKeysForSchedules() {
		List<JsonObject> jobKeyList = new ArrayList<>();
		List<Schedules> allSchedules = getAllSchedules();
		for (int count = 0; count < allSchedules.size(); count++) {
			Schedules schedules = allSchedules.get(count);
			if (schedules != null) {
				jobKeyList.add(scheduleOperation.prepareSchedulesEntityToJSON(schedules));
			}
		}
		return jobKeyList;
	}
	/**
	 * getAllSchedules()
     * Returns a list of all schedules.
     * @return A list of Schedules instances representing all schedules.
     */
	private List<Schedules> getAllSchedules() {
		SchedulesService schedulesService = ApplicationContextAccessor.getBean(SchedulesService.class);
		return schedulesService.getAllSchedules();
	}
	/**
	 * findJobKeysSpecificUserForSchedules(int userId)
     * Returns a list of JsonObject instances representing job keys of schedules specific to a user.
     *
     * @param userId 					user ID to get job keys 
     * @return A list of JsonObject instances representing job keys of schedules for the specified user.
     */
	private List<JsonObject> findJobKeysSpecificUserForSchedules(int userId) {
		List<JsonObject> jobKeyList = new ArrayList<>();
		List<Schedules> allSchedules = getAllSchedules();
		for (int count = 0; count < allSchedules.size(); count++) {
			Schedules schedules = allSchedules.get(count);
			if (schedules != null) {
				User createdBy = schedules.getCreatedBy();
				if (createdBy != null && createdBy.getId() == userId)
					jobKeyList.add(scheduleOperation.prepareSchedulesEntityToJSON(schedules));
			}

		}
		return jobKeyList;
	}
	/**
	 * findJobKeysSpecificToOrganizationForSchedules(Integer orgId)
     * Returns a list of JsonObject instances representing job keys of schedules specific to an organization.
     *
     * @param orgId 					ID of the organization for which job keys need to be get.
     * @return A list of JsonObject instances representing job keys of schedules for the specified organization.
     */
	private List<JsonObject> findJobKeysSpecificToOrganizationForSchedules(Integer orgId) {
		List<JsonObject> jobKeyList = new ArrayList<>();
		List<Schedules> allSchedules = getAllSchedules();
		for (int count = 0; count < allSchedules.size(); count++) {
			Schedules schedules = allSchedules.get(count);
			if (schedules != null) {
				User createdBy = schedules.getCreatedBy();
				if (createdBy != null) {
					Organization organizationOrg = createdBy.getOrganization();
					Integer org_id = organizationOrg != null ? organizationOrg.getId() : null;
					if (org_id != null && org_id.equals(orgId)) {
						jobKeyList.add(scheduleOperation.prepareSchedulesEntityToJSON(schedules));
					}
				}
			}

		}
		return jobKeyList;
	}
}
