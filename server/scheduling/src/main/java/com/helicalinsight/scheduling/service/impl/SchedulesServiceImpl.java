package com.helicalinsight.scheduling.service.impl;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.scheduling.dao.ScheduleDao;
import com.helicalinsight.scheduling.model.HiResource;
import com.helicalinsight.scheduling.model.JobParameters;
import com.helicalinsight.scheduling.model.Schedules;
import com.helicalinsight.scheduling.service.HiResourceService;
import com.helicalinsight.scheduling.service.SchedulesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SchedulesServiceImpl class performs crud operation on {@link Schedules} entity
 * which implements {@link SchedulesService} interface.
 * Created by author on 3/16/2020.
 * @author Rajesh
 */
@Service
public class SchedulesServiceImpl implements SchedulesService {

    @Autowired
    ScheduleDao scheduleDao;

    @Transactional
    @Override
    public Schedules addSchedule(Schedules schedule) {
        return scheduleDao.addSchedule(schedule);
    }

    @Transactional
    @Override
    public void editSchedule(Schedules schedule) {
        scheduleDao.editSchedule(schedule);
    }

    @Transactional
    @Override
    public void deleteSchedule(Long scheduleId) {
        scheduleDao.deleteSchedule(scheduleId);
    }

    @Transactional
    @Override
    public Schedules getSchedule(Long scheduleId) {
        return scheduleDao.getSchedule(scheduleId);
    }

    @Transactional
    @Override
    public Schedules findUniqueSchedule(Schedules sampleSchedule) {
        return scheduleDao.findUniqueSchedule(sampleSchedule);
    }

    @Transactional
    @Override
    public void deleteAllSchedule() {
        scheduleDao.deleteAllSchedule();
    }

    @Transactional
    @Override
    public List<JobParameters> getAllJobParametersById(Long scheduleId) {
        return scheduleDao.getAllJobParametersById(scheduleId);
    }



    @Transactional
    @Override
    public User getUserById(Long scheduleId) {
        return scheduleDao.getUserById(scheduleId);
    }

    @Transactional
    @Override
    public List<Schedules> getAllSchedules() {
        return scheduleDao.getAllSchedules();
    }

    @Transactional
    @Override
    public void deleteAllMigratedEntries() {
        scheduleDao.deleteAllMigratedEntries();
    }

    @Transactional
	@Override
	public List<Schedules> findSchedulesByResourceId(int resourceId) {
		return scheduleDao.findSchedulesByResourceId(resourceId);
	}
    
    @Transactional
	@Override
	public List<Long> findAllSchedulesByResourceId(int resourceId) {
		List<Schedules> scheduleList = findSchedulesByResourceId(resourceId);
		return scheduleList.stream().map(Schedules::getId).collect(Collectors.toList());
	}

    @Transactional
	@Override
	public void deleteScheduleByIds(List<Long> ids) {
    	ids.forEach(this::deleteSchedule);
	}

	@Override
	@Transactional
	public Schedules findScheduleByResourceIdAndScheduleName(Integer resourceId,String scheduleName) {
		return scheduleDao.findScheduleByResourceIdAndScheduleName(resourceId,scheduleName);
	}

	@Override
	@Transactional
	public void updateScheduleByResourceIdAndScheduleName(Integer resourceId,String oldName,String newName) {
		Schedules schedules = findScheduleByResourceIdAndScheduleName(resourceId,oldName);
		if (schedules != null) {
			schedules.setScheduleName(newName);
			scheduleDao.editSchedule(schedules);
		}
	}

	@Transactional
	@Override
	public HIResource getResourceByScheduleId(Long scheduleId) {
	        return scheduleDao.getResourceByScheduleId(scheduleId);
	}

}
