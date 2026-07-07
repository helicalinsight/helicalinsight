package com.helicalinsight.scheduling;

import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.components.ApplicationValuesInitializer;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.scheduling.model.Schedules;
import com.helicalinsight.scheduling.service.JobParametersService;
import com.helicalinsight.scheduling.service.SchedulesService;
import com.helicalinsight.scheduling.utils.ScheduleOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * InitializeScheduleData class implementing {@link ApplicationValuesInitializer}
 * this class is responsible to initialize schedules when application starts.
 * Created by Helical on 6/21/2021.
 */
@Component
public class InitializeScheduleData implements ApplicationValuesInitializer {
    private final static Logger logger = LoggerFactory.getLogger(InitializeScheduleData.class);
    @Autowired
    private SchedulesService schedulesService;
    @Autowired
    private JobParametersService jobParametersService;

    /**
     * The <code>UserService</code> object
     */
    @Autowired
    @Qualifier(value = "userDetailsService")
    private UserService userService;

    /**
     * initializeData(ApplicationContext applicationContext)
     * This method is called when the application starts to initialize scheduling data
     * based on the configured storage type (database or XML).
     *
     * @param applicationContext              provides application configuration,and provides bean component of schedule and userService.
     */
    @Override
    public void initializeData(ApplicationContext applicationContext) {
        try {
            if (JsonUtils.newIsScheduleStorageTypeIsDatabase()) {
                ScheduleOperation scheduleOperation = applicationContext.getBean(ScheduleOperation.class);
                if (scheduleOperation != null) {
                    scheduleOperation.insertDefaultDataForSchedule();

                    //Migration process for scheduling xml-Database
                    scheduleOperation.triggerMigrationProcessForScheduling(com.helicalinsight.efw.utility.JsonUtils.isScheduleMigrationIsEnabled());
                }
                EnhancedScheduleProcessCall scheduleProcessCall = new EnhancedScheduleProcessCall();
                //check whether schedules are present in database or not
                //SchedulesService schedulesService = ApplicationContextAccessor.getBean(SchedulesService.class);
                List<Schedules> allSchedules = schedulesService.getAllSchedules();
                if (!allSchedules.isEmpty()) {
                    userService = (UserService) applicationContext.getBean("userDetailsService");
                    String baseUrl = ApplicationProperties.getInstance().getDomain();
                    if (logger.isDebugEnabled()) {
                        logger.debug("The base url of the application is " + baseUrl);
                    }
                    ScheduleProcess scheduleProcess = new ScheduleProcess();
                    allSchedules.forEach(eachSchedules -> scheduleProcess.delete(String.valueOf(eachSchedules.getScheduleId())));
                    scheduleProcessCall.scheduleOperation(allSchedules, baseUrl, userService);
                }

            } else {
                ScheduleProcessCall scheduleProcessCall = new ScheduleProcessCall();
                String schedulePath = scheduleProcessCall.getSchedulePath();
                logger.debug("The schedulePath parameter is found and it is " + schedulePath);
                File file = new File(schedulePath);

                if (file.exists()) {
                    userService = (UserService) applicationContext.getBean("userDetailsService");
                    String baseUrl = ApplicationProperties.getInstance().getDomain();
                    if (logger.isDebugEnabled()) {
                        logger.debug("The base url of the application is " + baseUrl);
                    }
                    ScheduleProcess scheduleProcess = new ScheduleProcess();
                    XmlOperation xmlOperation = new XmlOperation();
                    List<String> idsList = xmlOperation.getIdFromJson(schedulePath);
                    for (String anId : idsList) {
                        //Delete from memory
                        scheduleProcess.delete(anId);
                    }
                    scheduleProcessCall.scheduleOperation(schedulePath, baseUrl, userService);
                }
            }
        } catch (Exception ex) {
            logger.error("There was an exception in handling the scheduling file processing. It " +
                    "is" + " ", ex);
        } catch (Error er) {
            logger.error("Error while application initialization process {}", er);
        }
    }
}
