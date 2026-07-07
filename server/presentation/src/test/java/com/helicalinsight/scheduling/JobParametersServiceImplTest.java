package com.helicalinsight.scheduling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.helicalinsight.scheduling.dao.JobParametersDao;
import com.helicalinsight.scheduling.model.JobParameters;
import com.helicalinsight.scheduling.model.Schedules;
import com.helicalinsight.scheduling.service.impl.JobParametersServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class JobParametersServiceImplTest {

	@Mock
	JobParametersDao jobParametersDao;
	@Mock
	JobParameters jobParameter;

	@InjectMocks
	JobParametersServiceImpl jobParametersServiceImpl;

	@Test
	public void testAddJobParameter() {
		Long id = 11l;
		when(jobParametersDao.addJobParameter(jobParameter)).thenReturn(id);
		Long addHiResource = jobParametersServiceImpl.addJobParameter(jobParameter);
		assertEquals(addHiResource, id);
	}

	@Test
	public void testEditJobParameter() {
		jobParametersServiceImpl.editJobParameter(jobParameter);
	}
	@Test
	public void testDeleteJobParameter() {
		jobParametersServiceImpl.deleteJobParameter(11l);
	}
	@Test
	public void testGetJobParameter() {
		Long id = 11l;
		when(jobParametersDao.getJobParameter(id)).thenReturn(jobParameter);
		JobParameters paramters = jobParametersServiceImpl.getJobParameter(id);
		assertNotNull(paramters);
	}
	
	@Test
	public void testFindUniqueJobParameter() {
		when(jobParametersDao.findUniqueJobParameter(jobParameter)).thenReturn(jobParameter);
		JobParameters paramters = jobParametersServiceImpl.findUniqueJobParameter(jobParameter);
		assertNotNull(paramters);
	}
	@Test
	public void testDeleteAllJobParameter() {
		jobParametersServiceImpl.deleteAllJobParameter();
	}
	
	@Test
	public void testGetSchedulesById() {
		Long id = 11l;
		Schedules schedules = mock(Schedules.class);
		when(jobParametersDao.getSchedulesById(id)).thenReturn(schedules);
		Schedules paramters = jobParametersServiceImpl.getSchedulesById(id);
		assertNotNull(paramters);
	}
	@Test
	public void testDeleteAllJobParametersRelatedToScheduleId() {
		jobParametersServiceImpl.deleteAllJobParametersRelatedToScheduleId(11l);
	}
	@Test
	public void testDeleteAllMigratedEntries() {
		jobParametersServiceImpl.deleteAllMigratedEntries();
	}
}
