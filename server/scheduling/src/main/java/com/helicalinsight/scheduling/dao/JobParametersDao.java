package com.helicalinsight.scheduling.dao;

import com.helicalinsight.scheduling.model.JobParameters;
import com.helicalinsight.scheduling.model.Schedules;

/**
 * Created by author on 3/13/2020.
 *
 * @author Rajesh
 */
public interface JobParametersDao {

    Long addJobParameter(JobParameters jobParameter);

    void editJobParameter(JobParameters jobParameter);

    void deleteJobParameter(Long jobParameterId);

    JobParameters getJobParameter(Long jobParameterId);

    JobParameters findUniqueJobParameter(JobParameters sampleJobParameter);

    void deleteAllJobParameter();


    Schedules getSchedulesById(Long jobParameterId);


    void deleteAllJobParametersRelatedToSchedule(Long scheduleId);

    void deleteAllMigratedEntries();
}
