package com.helicalinsight.cache;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.helicalinsight.cache.dao.CacheReportDao;
import com.helicalinsight.cache.model.CacheReport;
import com.helicalinsight.cache.service.impl.CacheReportServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class CacheReportServiceImplTest {
	
	@InjectMocks
	private CacheReportServiceImpl cacheReportService;
	
	@Mock
    private CacheReportDao cachereportDao;
	@Mock
	private CacheReport cacheReport;

	@Test
	public void ut_a1_getAllReports() {
		cacheReportService.getAllReports();
		verify(cachereportDao, times(1)).getAllReports();
	}
	
	@Test
	public void ut_a2_getReports() {
		cacheReportService.getReports(anyString());
		verify(cachereportDao, times(1)).getReports(anyString());
	}
	@Test
	public void ut_a3_addCacheReport() {
		cacheReportService.addCacheReport(cacheReport);
		verify(cachereportDao, times(1)).addCacheReport(cacheReport);
	}
	
	@Test
    public void ut_a4_editCacheReport() {
		cacheReportService.editCacheReport(cacheReport);
		verify(cachereportDao, times(1)).editCacheReport(cacheReport);
    }

    @Test
    public void ut_a5_deleteCacheReport() {
    	cacheReportService.deleteCacheReport(anyLong());
    	verify(cachereportDao, times(1)).deleteCacheReport(anyLong());
    	 
    }

    @Test
    public void ut_a6_getCacheReport() {
    	cacheReportService.getCacheReport(anyLong());
    	verify(cachereportDao, times(1)).getCacheReport(anyLong());
   	 
    }

    @Test
    public void ut_a7_getCacheReportByCacheId() {
    	cacheReportService.getCacheReportByCacheId(anyLong());
    	verify(cachereportDao, times(1)).getCacheReportByCacheId(anyLong());
      	 
    }

    @Test
    public void ut_a8_getAllUniqueReports() {
    	cacheReportService.getAllUniqueReports();
    	verify(cachereportDao, times(1)).getAllUniqueReports();
     	 
    }

    @Test
    public void ut_a9_getUniqueReports() {
       cacheReportService.getUniqueReports(anyString());
       verify(cachereportDao, times(1)).getUniqueReports(anyString());
    }

    @Test
    public void ut_b1_deleteAllCacheReport() {
    	cacheReportService.deleteAllCacheReport();
    	verify(cachereportDao, times(1)).deleteAllCacheReport();
    }


}
