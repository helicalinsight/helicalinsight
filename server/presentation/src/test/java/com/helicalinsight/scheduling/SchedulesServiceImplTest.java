package com.helicalinsight.scheduling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.scheduling.dao.ScheduleDao;
import com.helicalinsight.scheduling.model.HiResource;
import com.helicalinsight.scheduling.model.JobParameters;
import com.helicalinsight.scheduling.model.Schedules;
import com.helicalinsight.scheduling.service.impl.SchedulesServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class SchedulesServiceImplTest {

	@Mock
	ScheduleDao scheduleDao;
	@Mock
	Schedules schedule;
	@InjectMocks
	SchedulesServiceImpl schedulesServiceImpl;

	@Test
	public void testAddSchedule() {
		when(scheduleDao.addSchedule(schedule)).thenReturn(schedule);
		Schedules addSchedule = schedulesServiceImpl.addSchedule(schedule);
		assertNotNull(addSchedule);
	}

	@Test
	public void testEditSchedule() {
		schedulesServiceImpl.editSchedule(schedule);
	}

	@Test
	public void testDeleteSchedule() {
		schedulesServiceImpl.deleteSchedule(11l);
	}

	@Test
	public void testGetSchedule() {
		when(scheduleDao.getSchedule(anyLong())).thenReturn(schedule);
		Schedules addSchedule = schedulesServiceImpl.getSchedule(11l);
		assertNotNull(addSchedule);
	}

	@Test
	public void testFindUniqueSchedule() {
		when(scheduleDao.findUniqueSchedule(schedule)).thenReturn(schedule);
		Schedules addSchedule = schedulesServiceImpl.findUniqueSchedule(schedule);
		assertNotNull(addSchedule);
	}

	@Test
	public void testDeleteAllSchedule() {
		schedulesServiceImpl.deleteAllSchedule();
	}

	@Test
	public void testGetAllJobParametersById() {
		List<JobParameters> list = new ArrayList<>();
		when(scheduleDao.getAllJobParametersById(anyLong())).thenReturn(list);
		List<JobParameters> allJobParametersById = schedulesServiceImpl.getAllJobParametersById(11l);
		assertEquals(allJobParametersById, list);
	}

	@Test
	public void testGetResourceById() {
		HIResource hiResource = mock(HIResource.class);
		when(scheduleDao.getResourceByScheduleId(anyLong())).thenReturn(hiResource);
		HIResource resourceById = schedulesServiceImpl.getResourceByScheduleId(11l);
		assertNotNull(resourceById);
	}

	@Test
	public void testGetUserById() {
		User user = mock(User.class);
		when(scheduleDao.getUserById(anyLong())).thenReturn(user);
		User userById = schedulesServiceImpl.getUserById(11l);
		assertNotNull(userById);
	}
	@Test
	public void testGetAllSchedules() {
		List<Schedules> list = new ArrayList<>();
		when(scheduleDao.getAllSchedules()).thenReturn(list);
		List<Schedules> allSchedules = schedulesServiceImpl.getAllSchedules();
		assertEquals(allSchedules,list);
	}
	@Test
	public void testDeleteAllMigratedEntries() {
		schedulesServiceImpl.deleteAllMigratedEntries();
	}
	
}
