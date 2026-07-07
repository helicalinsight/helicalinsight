package com.helicalinsight.cache;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.helicalinsight.cache.dao.impl.CacheReportDaoImpl;
import com.helicalinsight.cache.model.CacheReport;

@RunWith(MockitoJUnitRunner.Silent.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CacheReportDaoImplTest {

	@Mock
	private SessionFactory sessionFactory;
	@Mock
	private Session session;
	@Mock
	private Query query;
	@Mock
	private CacheReport cacheReport;
	@InjectMocks
	private CacheReportDaoImpl cacheReportDaoImpl;
	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		// Mock the behavior of the session factory to return the session mock
		when(sessionFactory.getCurrentSession()).thenReturn(session);
	}

	@Test
	public void ut_a1_addCacheReport() {
		Long actualId = 11l;
		when(cacheReport.getCacheId()).thenReturn(actualId);
		Long expectedId = cacheReportDaoImpl.addCacheReport(cacheReport);
		verify(session, times(1)).save(cacheReport);
		assertEquals(actualId, expectedId);

	}
	@Test
	public void ut_a2_addCacheReport_exception() {
		doThrow(new RuntimeException("Mocked exception")).when(session).save(cacheReport);
    	cacheReportDaoImpl.addCacheReport(cacheReport);

	}
	
	@Test
    public void ut_a3_editCacheReport() {
		cacheReportDaoImpl.editCacheReport(cacheReport);
    	verify(session,times(1)).update(cacheReport);
    }
    @Test
    public void ut_a4_editCacheReport_exception() {
    	doThrow(new RuntimeException("Mocked exception")).when(session).update(cacheReport);
    	cacheReportDaoImpl.editCacheReport(cacheReport);
    }

    @Test
    public void ut_a5_deleteCacheReport() {
    	Long cacheReportId = 123L;
    	CacheReportDaoImpl cacheDaoSpy = spy(cacheReportDaoImpl);
        doReturn(cacheReport).when(cacheDaoSpy).getCacheReport(cacheReportId);
        cacheDaoSpy.deleteCacheReport(cacheReportId);
    	verify(session, times(1)).delete(cacheReport);

    }
    @Test
    public void ut_a6_deleteCacheReport_exception() {
    	Long cacheReportId = 123L;
    	CacheReportDaoImpl cacheDaoSpy = spy(cacheReportDaoImpl);
        doThrow(new RuntimeException("Mocked exception")).when(cacheDaoSpy).getCacheReport(cacheReportId);
        cacheDaoSpy.deleteCacheReport(cacheReportId);
    	cacheReportDaoImpl.deleteCacheReport(cacheReportId);
    } 
    @Test
    public void ut_a7_getCacheReport_exception() {
    	Long cacheReportId = 123L;
    	when(session.get(eq(CacheReport.class), eq(cacheReportId))).thenThrow(new RuntimeException("Mocked exception"));
    	cacheReportDaoImpl.getCacheReport(cacheReportId);
    }
    
    @Test
    public void ut_a8_GetCacheReportByCacheId() {
    	when(session.createQuery(anyString())).thenReturn(query);
    	when(query.setParameter(anyString(), any())).thenReturn(query);
    	Long cacheReportId = 123L;
    	cacheReportDaoImpl.getCacheReportByCacheId(cacheReportId);
    	verify(session,times(1)).createQuery("from  CacheReport where  cacheId=:cacheId");
    }
    
    @Test
    public void ut_a8_GetCacheReportByCacheId_Exception() {
    	Long cacheReportId = 123L;
    	cacheReportDaoImpl.getCacheReportByCacheId(cacheReportId);
    	verify(session,times(1)).createQuery("from  CacheReport where  cacheId=:cacheId");
    }
    
    @Test
    public void ut_a9_GetAllReports() {
    	when(session.createQuery(anyString())).thenReturn(query);
    	when(query.setParameter(anyString(), any())).thenReturn(query);
    	cacheReportDaoImpl.getAllReports();
    	verify(session,times(1)).createQuery("from  CacheReport");
    }
    @Test
    public void ut_a9_GetAllReports_Exception() {
    	cacheReportDaoImpl.getAllReports();
    	verify(session,times(1)).createQuery("from  CacheReport");
    }
    
    @Test
    public void ut_b1_GetReports_withParamter() {
    	when(session.createQuery(anyString())).thenReturn(query);
    	when(query.setParameter(anyString(), any())).thenReturn(query);
    	
    	cacheReportDaoImpl.getReports("directory");
    	verify(session,times(1)).createQuery("from  CacheReport where reportPath = :directory");
    }
    @Test
    public void ut_b1_GetReports_withParamter_Exception() {
    	cacheReportDaoImpl.getReports("directory");
    	verify(session,times(1)).createQuery("from  CacheReport where reportPath = :directory");
    }
    @Test
    public void ut_b2_getAllUniqueReports1() {
    	when(session.createQuery(anyString())).thenReturn(query);
    	cacheReportDaoImpl.getAllUniqueReports();
    	verify(session,times(1)).createQuery("select distinct  cr.reportPath from  CacheReport cr where cr.reportPath is not null");
    }
    @Test
    public void ut_b2_getAllUniqueReports2() {
    	cacheReportDaoImpl.getAllUniqueReports();
    	verify(session,times(1)).createQuery("select distinct  cr.reportPath from  CacheReport cr where cr.reportPath is not null");
    }
    @SuppressWarnings("deprecation")
	@Test
    public void ut_b3_getUniqueReports_withParameter() {
    	when(session.createQuery(anyString())).thenReturn(query);
    	when(query.setParameter(anyString(), any())).thenReturn(query);
    	cacheReportDaoImpl.getUniqueReports("AB:");
    	verify(session,times(1)).createQuery("select distinct cr.reportPath from  CacheReport  cr " +
                "where reportPath like " + ":directory and reportPath is not null");
    }
    @SuppressWarnings("deprecation")
	@Test
    public void ut_b3_getUniqueReports_withParameter_exception() {
    	cacheReportDaoImpl.getUniqueReports("AB:");
    	verify(session,times(1)).createQuery("select distinct cr.reportPath from  CacheReport  cr " +
                "where reportPath like " + ":directory and reportPath is not null");
    }
    @Test
    public void ut_b4_deleteAllCacheReport() {
    	jakarta.persistence.Query  query2 = spy(jakarta.persistence.Query.class);
    	when(session.createQuery("delete from  CacheReport")).thenReturn(query);
    	
    	when(query.unwrap(jakarta.persistence.Query.class)).thenReturn(query2);

    	cacheReportDaoImpl.deleteAllCacheReport();
    	verify(session,times(1)).createQuery("delete from  CacheReport");
    }
    
    @Test
    public void ut_b5_deleteAllCacheReport_exception() {
    	cacheReportDaoImpl.deleteAllCacheReport();
    	verify(session,times(1)).createQuery("delete from  CacheReport");
    }
}
