package com.helicalinsight.scheduling.dao.impl;

import com.helicalinsight.scheduling.dao.JobParametersDao;
import com.helicalinsight.scheduling.model.JobParameters;
import com.helicalinsight.scheduling.model.Schedules;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * JobParametersDaoImpl
 * Implementation of the {@link JobParametersDao} interface that interacts with the database
 * to manage job parameter entities.
 * Created by author on 3/13/2020.
 * @author Rajesh
 */
@Repository
public class JobParametersDaoImpl implements JobParametersDao {
    private static final Logger logger = LoggerFactory.getLogger(JobParametersDaoImpl.class);
    @Autowired
    SessionFactory session;
    /**
     * addJobParameter(JobParameters jobParameter)
     * Methods adds {@link JobParameter} object in a database.
     * @param jobParameter    jobParameter object too add
     * @return job parameter id in long format
     */
    @Override
    public Long addJobParameter(JobParameters jobParameter) {
        try {
            session.getCurrentSession().save(jobParameter);
        } catch (Exception e) {
            logger.error("Exception", e);
        }

        return jobParameter.getParameterId();
    }
    /**
     * editJobParameter(JobParameters jobParameter)
     * This method edits already available jobParameter entity in database.
     * @param jobParameter       object for updating
     * 
     */
    @Override
    public void editJobParameter(JobParameters jobParameter) {
        try {
            session.getCurrentSession().update(jobParameter);
        } catch (Exception e) {
            logger.error("Exception", e);
        }

    }
    /**
     * deleteJobParameter(Long jobParameterId)
     * Deletes JobParameter using parameter id in a database.
     * @param jobParameterId      id 
     * 
     */
    @Override
    public void deleteJobParameter(Long jobParameterId) {
        try {
            session.getCurrentSession().delete(jobParameterId);
        } catch (Exception e) {
            logger.error("Exception", e);
        }

    }
    /**
     * getJobParameter(Long jobParameterId)
     * Returns jobParameter object from database.
     * @param jobParameterId      id 
     * @return jobParameter object
     */
    @Override
    public JobParameters getJobParameter(Long jobParameterId) {
        JobParameters jobParameters = null;
        try {
            jobParameters = (JobParameters) session.getCurrentSession().get(JobParameters.class, jobParameterId);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return jobParameters;
    }
    /**
     * findUniqueJobParameter(JobParameters sampleJobParameter)
     * Returns {@code null} as this method for finding unique job parameters is not implemented.
     *
     * @param sampleJobParameter           job parameters to search for.
     * @return {@code null} since the method is not implemented.
     */
    @Override
    public JobParameters findUniqueJobParameter(JobParameters sampleJobParameter) {
        return null;
    }
    /**
     *  deleteAllJobParameter()
     *  Deletes all JobParameters from database.
     */
    @Override
    public void deleteAllJobParameter() {
        try {
            session.getCurrentSession().createQuery("delete from JobParameters").executeUpdate();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }
    /**
     * getSchedulesById(Long jobParameterId)
     * Returns associated schedules from a job parameter.
     * @param jobParameterId      id used to get jobParameter object
     * @return {@link Schedules} object 
     */
    @Override
    public Schedules getSchedulesById(Long jobParameterId) {
        Schedules schedules = null;
        try {
            schedules = getJobParameter(jobParameterId).getScheduleIdOfJobParameter();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return schedules;
    }
    /**
     * deleteAllJobParametersRelatedToSchedule(Long scheduleId)
     * @param scheduleId                required id to delete the schedules associated with jobParamters
     * 
     */
    @Override
    public void deleteAllJobParametersRelatedToSchedule(Long scheduleId) {
        try {
            String hql = "delete from JobParameters where scheduleIdOfJobParameter= :id";
            session.getCurrentSession().createQuery(hql).setParameter("id", scheduleId).executeUpdate();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }
    /**
     * deleteAllMigratedEntries() 
     * Deletes all migrated entries from the job parameters.
     */
    @Override
    public void deleteAllMigratedEntries() {
        try {
            String hql = "delete from JobParameters where isMigrated=:id";
            session.getCurrentSession().createQuery(hql).setParameter("id", true).executeUpdate();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

}
