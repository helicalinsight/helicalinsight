package com.helicalinsight.scheduling;

import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.helicalinsight.scheduling.dao.impl.JobParametersDaoImpl;
import com.helicalinsight.scheduling.model.JobParameters;
import com.helicalinsight.scheduling.model.Schedules;

public class JobParametersDaoImplTest {

	@Mock
	private SessionFactory sessionFactory;
	@Mock
	private Session session;
	@Mock
	private Query query;
	@Mock
	private JobParameters jobParameter;
	@InjectMocks
	private JobParametersDaoImpl jobParametersDaoImpl;
	
	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		when(sessionFactory.getCurrentSession()).thenReturn(session);
	}
	@Test
	public void testAddJobParameter_a1() {
		jobParametersDaoImpl.addJobParameter(jobParameter);
		verify(session, times(1)).save(jobParameter);
	}
	@Test
	public void testAddJobParameter_a2() {
		doThrow(new RuntimeException("Mocked exception")).when(session).save(jobParameter);
		jobParametersDaoImpl.addJobParameter(jobParameter);
	}
	
	@Test
	public void testEditJobParameter_a1() {
		jobParametersDaoImpl.editJobParameter(jobParameter);
		verify(session, times(1)).update(jobParameter);
	}
	@Test
	public void testEditJobParameter_a2() {
		doThrow(new RuntimeException("Mocked exception")).when(session).update(jobParameter);
		jobParametersDaoImpl.editJobParameter(jobParameter);
		verify(session, times(1)).update(jobParameter);
	}
	@Test
	public void testDeleteJobParameter_a1() {
		jobParametersDaoImpl.deleteJobParameter(11l);
		verify(session, times(1)).delete(11l);
	}
	
	@Test
	public void testDeleteJobParameter_a2() {
		doThrow(new RuntimeException("Mocked exception")).when(session).delete(anyLong());
		jobParametersDaoImpl.deleteJobParameter(11l);
		verify(session, times(1)).delete(11l);
	}
	@Test
	public void testGetJobParameter_a1() {
		jobParametersDaoImpl.getJobParameter(11l);
	}
	
	@Test
	public void testGetJobParameter_a2() {
		when(session.get(eq(JobParameters.class), anyLong())).thenThrow(new RuntimeException("Mocked exception"));
		jobParametersDaoImpl.getJobParameter(11l);
	}
	
	@Test
	public void testFindUniqueJobParameter() {
		JobParameters findUniqueJobParameter = jobParametersDaoImpl.findUniqueJobParameter(jobParameter);
		assertNull(findUniqueJobParameter);
	}
	@Test
	public void testDeleteAllJobParameter_a1() {
		when(session.createQuery(anyString())).thenReturn(query);
		jobParametersDaoImpl.deleteAllJobParameter();
		verify(session,times(1)).createQuery(anyString());
	}
	@Test
	public void testDeleteAllJobParameter_a2() {
		jobParametersDaoImpl.deleteAllJobParameter();
		verify(session,times(1)).createQuery(anyString());
	}
	
	@Test
	public void testGetSchedulesById_a1() {
		 Schedules schedules =mock( Schedules.class);
		when(session.get(eq(JobParameters.class), anyLong())).thenReturn(jobParameter);
		when(jobParameter.getScheduleIdOfJobParameter()).thenReturn(schedules);
		jobParametersDaoImpl.getSchedulesById(11l);
	}
	@Test
	public void testGetSchedulesById_a2() {
		jobParametersDaoImpl.getSchedulesById(11l);
	}
	
	@Test
	public void testDeleteAllJobParametersRelatedToSchedule_a1() {
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.setParameter(anyString(), anyLong())).thenReturn(query);
		jobParametersDaoImpl.deleteAllJobParametersRelatedToSchedule(11l);
	}
	@Test
	public void testDeleteAllJobParametersRelatedToSchedule_a2() {
		jobParametersDaoImpl.deleteAllJobParametersRelatedToSchedule(11l);
	}
	
	@Test
	public void testDeleteAllMigratedEntries_a1() {
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.setParameter(anyString(), anyBoolean())).thenReturn(query);
		jobParametersDaoImpl.deleteAllMigratedEntries();
	}
	@Test
	public void testDeleteAllMigratedEntries_a2() {
		jobParametersDaoImpl.deleteAllMigratedEntries();
	}
}
