package com.helicalinsight.scheduling.dao.impl;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.scheduling.dao.ScheduleDao;
import com.helicalinsight.scheduling.model.JobParameters;
import com.helicalinsight.scheduling.model.Schedules;

import org.hibernate.SessionFactory;
import org.hibernate.query.SelectionQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


/**
 * ScheduleDaoImpl
 * Implementation of the {@link ScheduleDao} interface that interacts with the database
 * to manage scheduling-related entities
 * Created by author on 3/13/2020.
 * @author Rajesh
 */
@Repository
public class ScheduleDaoImpl implements ScheduleDao {
    private static final Logger logger = LoggerFactory.getLogger(ScheduleDaoImpl.class);

    @Autowired
    SessionFactory session;
    @Override
    public Schedules addSchedule(Schedules schedule) {
        try {
            session.getCurrentSession().save(schedule);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return schedule;
    }
    /**
     * editSchedule(Schedules schedule)
     * Updates an existing {@link Schedules} entity in the database.
     * @param schedule 				schedule object for edit.
     */
    @Override
    public void editSchedule(Schedules schedule) {
        try {
            session.getCurrentSession().update(schedule);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }
    /**
     * deleteSchedule(Long scheduleId)
     * Deletes a schedule using its ID.
     *
     * @param scheduleId 	    ID of the schedule to delete.
     */
    @Override
    public void deleteSchedule(Long scheduleId) {
        try {
            session.getCurrentSession().delete(getSchedule(scheduleId));
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }
    /**
     * getSchedule(Long scheduleId)
     * Returns a {@link Schedules} entity using its ID.
     *
     * @param scheduleId 		 id of the schedule 
     * @return {@link Schedules} object .
     */
    @Override
    public Schedules getSchedule(Long scheduleId) {
        Schedules schedules = null;
        try {
            schedules =  session.getCurrentSession().get(Schedules.class, scheduleId);
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return schedules;
    }

    //Not required this method as of now
    @Override
    public Schedules findUniqueSchedule(Schedules sampleSchedule) {
        return null;
    }
    /**
     * deleteAllSchedule()
     * Deletes all schedules form database.
     */
    @Override
    public void deleteAllSchedule() {
        try {
            session.getCurrentSession().createQuery("delete from Schedules").executeUpdate();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }
    /**
     * getAllJobParametersById(Long scheduleId)
     * method provides a list of all {@link JobParameters} using scheduleId.
     * @param scheduleId 			ID of the schedule 
     * @return A list containing {@link JobParameters} objects .
     *
     */
    @Override
    public List<JobParameters> getAllJobParametersById(Long scheduleId) {

        List<JobParameters> jobParametersList = null;
        try {
        	List<JobParameters> list = session.getCurrentSession().
            createSelectionQuery("from JobParameters  where scheduleIdOfJobParameter.scheduleId=:id",JobParameters.class)
            .setParameter("id", scheduleId).list();
            jobParametersList = ApplicationUtilities.castList(JobParameters.class, list);

        } catch (Exception e) {
            logger.error("Exception", e);
        }

        return jobParametersList;
    }
    /**
     * getResourceById(Long scheduleId) 
     * Returns the associated {@link HiResource} entity from a schedule.
     *
     * @param scheduleId 			ID to get entity
     * @return HiResource object.
     */
    @Override
    public HIResource getResourceByScheduleId(Long scheduleId) {
        HIResource hiResource = null;
        try {
            hiResource = getSchedule(scheduleId).getHIResource();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return hiResource;
    }
    /**
     * getUserById(Long scheduleId)
     * this method returns user who created the schedule
     * @param scheduleId 	        id to get user.
     * @return user object.
     * 
     */
    @Override
    public User getUserById(Long scheduleId) {
        User user = null;
        try {
            user = getSchedule(scheduleId).getCreatedBy();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
        return user;
    }
    /**
     * getAllSchedules()
     * Returns a list of all schedules.
     * @return A list containing all available {@link Schedules} objects , otherwise {@code null} if not found.
     */
    @Override
    public List<Schedules> getAllSchedules() {
        List<Schedules> listOfSchedules = new ArrayList<>();
        try {
        	session.getCurrentSession().createSelectionQuery("FROM Schedules",Schedules.class);
            listOfSchedules = session.getCurrentSession().createSelectionQuery("FROM Schedules",Schedules.class).getResultList();
        } catch (Exception e) {
            logger.error("Exception", e);
        }

        return listOfSchedules;
    }
    /**
     * deleteAllMigratedEntries()
     * Deletes all migration entries for schedules related.
     */
    @Override
    public void deleteAllMigratedEntries() {
        try {
            String hql = "delete from Schedules where isMigrated=:id";
            session.getCurrentSession().createQuery(hql)
            .setParameter("id", true)
            .executeUpdate();
        } catch (Exception e) {
            logger.error("Exception", e);
        }
    }

	@SuppressWarnings("unchecked")
	@Override
	public List<Schedules> findSchedulesByResourceId(int resourceId) {
		List<Schedules> scheduleList  = new ArrayList<>();
		try {
			String hql = "FROM Schedules sch where sch.hiResource.resourceId = :resourceId";
			SelectionQuery<Schedules> query = session.getCurrentSession().createSelectionQuery(hql,Schedules.class);
			query.setParameter("resourceId", resourceId);
			scheduleList =   query.list();
		}
		catch (Exception e) {
			logger.error("Error occurred while fetching Schedules by ResourceId , Root Cause :{} ",e.getMessage());
			if(logger.isDebugEnabled()) {
				e.printStackTrace();
			} 
		}
		return scheduleList;
	}

	@Override
	public Schedules findScheduleByResourceIdAndScheduleName(Integer resourceId,String scheduleName) {
		Schedules schedule =null;
        try {
        	SelectionQuery<Schedules> query=session.getCurrentSession().createSelectionQuery("FROM Schedules sch WHERE sch.scheduleName=:scheduleName "
        			+ "AND sch.hiResource.resourceId=:resourceId",Schedules.class);
        	query.setParameter("resourceId", resourceId);
        	query.setParameter("scheduleName", scheduleName);
        	schedule= query.uniqueResult();
        } catch (Exception e) {
            logger.error("Exception", e);
        }

        return schedule;

	}
}
