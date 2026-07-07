package com.helicalinsight.scheduling.dao;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.ResourceType;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.scheduling.model.HiResource;
import com.helicalinsight.scheduling.model.JobParameters;
import com.helicalinsight.scheduling.model.Schedules;

import java.util.List;

/**
 * Created by author on 3/13/2020.
 *
 * @author Rajesh
 */
public interface ScheduleDao {
    Schedules addSchedule(Schedules schedule);

    void editSchedule(Schedules schedule);

    void deleteSchedule(Long scheduleId);

    Schedules getSchedule(Long scheduleId);

    Schedules findUniqueSchedule(Schedules sampleSchedule);

    void deleteAllSchedule();

    List<JobParameters> getAllJobParametersById(Long scheduleId);



    HIResource getResourceByScheduleId(Long scheduleId);

    User getUserById(Long scheduleId);


    List<Schedules> getAllSchedules();

    void deleteAllMigratedEntries();

	List<Schedules> findSchedulesByResourceId(int resourceId);
	
	Schedules findScheduleByResourceIdAndScheduleName(Integer resourceId,String scheduleName);
}
