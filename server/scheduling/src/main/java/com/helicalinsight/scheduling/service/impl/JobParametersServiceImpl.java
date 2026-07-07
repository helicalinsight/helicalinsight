package com.helicalinsight.scheduling.service.impl;

import com.helicalinsight.scheduling.dao.JobParametersDao;
import com.helicalinsight.scheduling.model.HiResource;
import com.helicalinsight.scheduling.model.JobParameters;
import com.helicalinsight.scheduling.model.Schedules;
import com.helicalinsight.scheduling.service.HiResourceService;
import com.helicalinsight.scheduling.service.JobParametersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

/**
 * JobParametersServiceImpl class performs crud operation on {@link JobParameters} entity
 * which implements {@link JobParametersService} interface.
 * Created by author on 3/16/2020.
 * @author Rajesh
 */
@Service
public class JobParametersServiceImpl implements JobParametersService {
    @Autowired
    JobParametersDao jobParametersDao;

    @Transactional
    @Override
    public Long addJobParameter(JobParameters jobParameter) {
        return jobParametersDao.addJobParameter(jobParameter);
    }

    @Transactional
    @Override
    public void editJobParameter(JobParameters jobParameter) {
        jobParametersDao.editJobParameter(jobParameter);
    }

    @Transactional
    @Override
    public void deleteJobParameter(Long jobParameterId) {
        jobParametersDao.deleteJobParameter(jobParameterId);
    }

    @Transactional
    @Override
    public JobParameters getJobParameter(Long jobParameterId) {
        return jobParametersDao.getJobParameter(jobParameterId);
    }

    @Transactional
    @Override
    public JobParameters findUniqueJobParameter(JobParameters sampleJobParameter) {
        return jobParametersDao.findUniqueJobParameter(sampleJobParameter);
    }

    @Transactional
    @Override
    public void deleteAllJobParameter() {
        jobParametersDao.deleteAllJobParameter();
    }

    @Transactional
    @Override
    public Schedules getSchedulesById(Long jobParameterId) {
        return jobParametersDao.getSchedulesById(jobParameterId);
    }

    @Transactional
    @Override
    public void deleteAllJobParametersRelatedToScheduleId(Long scheduleId) {
        jobParametersDao.deleteAllJobParametersRelatedToSchedule(scheduleId);
    }

    @Transactional
    @Override
    public void deleteAllMigratedEntries() {
        jobParametersDao.deleteAllMigratedEntries();
    }
}
