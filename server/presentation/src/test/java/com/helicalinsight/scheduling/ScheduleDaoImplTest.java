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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.query.SelectionQuery;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.ResourceType;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.scheduling.dao.impl.ResourceTypeDaoImpl;
import com.helicalinsight.scheduling.dao.impl.ScheduleDaoImpl;
import com.helicalinsight.scheduling.model.HiResource;
import com.helicalinsight.scheduling.model.JobParameters;
import com.helicalinsight.scheduling.model.Schedules;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class ScheduleDaoImplTest {

	@Mock
	private SessionFactory sessionFactory;
	@Mock
	private Session session;
	@Mock
	private Query query;

	@Mock
	private Schedules schedules ;

	@InjectMocks
	private ScheduleDaoImpl scheduleDaoImpl ;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		when(sessionFactory.getCurrentSession()).thenReturn(session);
	}

	@Test
	public void testAddSchedule_a1() {
		scheduleDaoImpl.addSchedule(schedules);
		verify(session, times(1)).save(schedules);
	}

	@Test
	public void testAddSchedule_a2() {
		doThrow(new RuntimeException("Mocked exception")).when(session).save(schedules);
		scheduleDaoImpl.addSchedule(schedules);
	}

	@Test
	public void testEditSchedule_a1() {
		scheduleDaoImpl.editSchedule(schedules);
		verify(session, times(1)).update(schedules);
	}

	@Test
	public void testEditSchedule_a2() {
		doThrow(new RuntimeException("Mocked exception")).when(session).update(schedules);
		scheduleDaoImpl.editSchedule(schedules);
	}
	
	@Test
	public void testGetSchedule_a1() {
		when(session.get(eq(Schedules.class), anyLong())).thenReturn(schedules);
		scheduleDaoImpl.getSchedule(11l);
    	
	}
	@Test
	public void testGetSchedule_a2() {
		when(session.get(eq(Schedules.class), anyLong())).thenThrow(new RuntimeException());
		scheduleDaoImpl.getSchedule(11l);
	}
	
	@Test
	public void testDeleteSchedule_a1() {
		when(session.get(eq(Schedules.class), anyLong())).thenReturn(schedules);
		scheduleDaoImpl.deleteSchedule(11l);
    	
	}
	@Test
	public void testDeleteSchedule_a2() {
		when(session.get(eq(Schedules.class), anyLong())).thenReturn(schedules);
		doThrow(new RuntimeException("Mocked exception")).when(session).delete(schedules);
		scheduleDaoImpl.deleteSchedule(11l);
    	
	}
	
	@Test
	public void testFindUniqueSchedule() {
		Schedules findUniqueSchedule = scheduleDaoImpl.findUniqueSchedule(schedules);
    	assertNull(findUniqueSchedule);
	}
	
	@Test
	public void testDeleteAllSchedule_a1() {
		when(session.createQuery(anyString())).thenReturn(query);
		scheduleDaoImpl.deleteAllSchedule();
	}
	@Test
	public void testDeleteAllSchedule_a2() {
		scheduleDaoImpl.deleteAllSchedule();
	}
	
	@Test
	public void testGetAllJobParametersById_a1() {
		List<JobParameters> jobParametersList =  new ArrayList<>();
		SelectionQuery<JobParameters> createSelectionQuery = mock(SelectionQuery.class);
		when(session.createSelectionQuery("from JobParameters  where scheduleIdOfJobParameter.scheduleId=:id",JobParameters.class)).thenReturn(createSelectionQuery);
		when(createSelectionQuery.setParameter(anyString(), anyLong())).thenReturn(createSelectionQuery);
		when(createSelectionQuery.list()).thenReturn(jobParametersList);
		
		scheduleDaoImpl.getAllJobParametersById(11l);
	}
	@Test
	public void testGetAllJobParametersById_a2() {
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.setParameter(anyString(), anyLong())).thenThrow(new RuntimeException());
		scheduleDaoImpl.getAllJobParametersById(11l);
	}
	
	@Test
	public void testGetResourceById_a1() {
		 HIResource hiResource = mock( HIResource.class);
		when(session.get(eq(Schedules.class), anyLong())).thenReturn(schedules);
		when(schedules.getHIResource()).thenReturn(hiResource);
		scheduleDaoImpl.getResourceByScheduleId(11l);
	}
	@Test
	public void testGetResourceById_a2() {
		scheduleDaoImpl.getResourceByScheduleId(11l);
	}
	@Test
	public void testGetUserById_a1() {
		User user = mock( User.class);
		when(session.get(eq(Schedules.class), anyLong())).thenReturn(schedules);
		when(schedules.getCreatedBy()).thenReturn(user);
		scheduleDaoImpl.getUserById(11l);
	}
	@Test
	public void testGetUserById_a2() {
		scheduleDaoImpl.getUserById(11l);
	}
	@Test
	public void testDeleteAllMigratedEntries_a1() {
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.setParameter(anyString(), anyBoolean())).thenReturn(query);
		scheduleDaoImpl.deleteAllMigratedEntries();
	}
	@Test
	public void testDeleteAllMigratedEntries_a2() {
		scheduleDaoImpl.deleteAllMigratedEntries();
	}
	
	@Test
	public void testGetAllSchedules_a1() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		List<Schedules> listOfSchedules = new ArrayList<>();
		SelectionQuery<Schedules> createSelectionQuery = mock(SelectionQuery.class);
		when(sessionFactory.getCurrentSession()).thenReturn(session);
		when(session.createSelectionQuery("FROM Schedules", Schedules.class)).thenReturn(createSelectionQuery);
		when(createSelectionQuery.getResultList()).thenReturn(listOfSchedules);
		scheduleDaoImpl.getAllSchedules();
	}
	@Test
	public void testGetAllSchedules_a2() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		List<Schedules> listOfSchedules = new ArrayList<>();
		SelectionQuery<Schedules> createSelectionQuery = mock(SelectionQuery.class);
		when(sessionFactory.getCurrentSession()).thenReturn(session);
		when(session.createSelectionQuery("FROM Schedules", Schedules.class)).thenReturn(createSelectionQuery);
		when(createSelectionQuery.getResultList()).thenThrow(new RuntimeException("exception"));
		scheduleDaoImpl.getAllSchedules();
	}
	@Test
	public void testFindSchedulesByResourceId_a1() {
		List<Schedules> listOfSchedules = new ArrayList<>();
		SelectionQuery<Schedules> createSelectionQuery = mock(SelectionQuery.class);
		when(sessionFactory.getCurrentSession()).thenReturn(session);
		when(session.createSelectionQuery("FROM Schedules sch where sch.hiResource.resourceId = :resourceId", Schedules.class)).thenReturn(createSelectionQuery);
		when(createSelectionQuery.list()).thenReturn(listOfSchedules);
		scheduleDaoImpl.findSchedulesByResourceId(1);
	}
	@Test
	public void testFindSchedulesByResourceId_a2() {
		List<Schedules> listOfSchedules = new ArrayList<>();
		SelectionQuery<Schedules> createSelectionQuery = mock(SelectionQuery.class);
		when(sessionFactory.getCurrentSession()).thenReturn(session);
		when(session.createSelectionQuery("FROM Schedules sch where sch.hiResource.resourceId = :resourceId", Schedules.class)).thenReturn(createSelectionQuery);
		when(createSelectionQuery.list()).thenThrow(new RuntimeException("exception"));
		scheduleDaoImpl.findSchedulesByResourceId(1);
	}
	@Test
	public void testFindScheduleByResourceIdAndScheduleName_a1() {
		SelectionQuery<Schedules> createSelectionQuery = mock(SelectionQuery.class);
		when(sessionFactory.getCurrentSession()).thenReturn(session);
		when(session.createSelectionQuery("FROM Schedules sch WHERE sch.scheduleName=:scheduleName AND sch.hiResource.resourceId=:resourceId", Schedules.class)).thenReturn(createSelectionQuery);
		when(createSelectionQuery.uniqueResult()).thenReturn(schedules);
		scheduleDaoImpl.findScheduleByResourceIdAndScheduleName(1, "string");
	}
	@Test
	public void testFindScheduleByResourceIdAndScheduleName_a2() {
		SelectionQuery<Schedules> createSelectionQuery = mock(SelectionQuery.class);
		when(sessionFactory.getCurrentSession()).thenReturn(session);
		when(session.createSelectionQuery("FROM Schedules sch WHERE sch.scheduleName=:scheduleName AND sch.hiResource.resourceId=:resourceId", Schedules.class)).thenReturn(createSelectionQuery);
		when(createSelectionQuery.uniqueResult()).thenThrow(new RuntimeException("exception"));
		scheduleDaoImpl.findScheduleByResourceIdAndScheduleName(1, "string");
	}
}
