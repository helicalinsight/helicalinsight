package com.helicalinsight.adhoc.jreport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.List;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.cache.CacheHelper;
import com.helicalinsight.cache.CacheUtils;
import com.helicalinsight.cache.manager.HCRQueryProcessCacheManagerForResultSet;
import com.helicalinsight.cache.model.Cache;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

import net.sf.jasperreports.engine.JRDataSource;

public class LazySubDatasetDataSourceFactoryTest {

	@Test
	public void resolve_withNullFactory_returnsEmptyDataSource() {
		JRDataSource dataSource = LazySubDatasetDataSourceFactory.resolve(null);
		assertNotNull(dataSource);
	}

	@Test
	public void resolve_mergesMappedParametersIntoConnectionDetails() throws Exception {
		JsonObject connectionDetails = new JsonObject();
		connectionDetails.addProperty("temp_uuid", "temp-uuid-1");
		connectionDetails.addProperty("map_id", 1);
		connectionDetails.addProperty("mode_of_payment", "Cash");

		LazySubDatasetDataSourceFactory factory = new LazySubDatasetDataSourceFactory(connectionDetails);
		HCRHelper hcrHelper = new HCRHelper();
		CacheHelper cacheHelper = mock(CacheHelper.class);
		HCRQueryProcessCacheManagerForResultSet resultSetCacheManager =
				mock(HCRQueryProcessCacheManagerForResultSet.class);
		Cache cache = mock(Cache.class);
		ResultSet resultSet = mock(ResultSet.class);
		injectCacheHelper(hcrHelper, cacheHelper);

		when(cacheHelper.prepareCacheFromRequest(resultSetCacheManager)).thenReturn(cache);
		when(cacheHelper.processCache(null, null, "report-cache", false, cache, resultSetCacheManager))
				.thenReturn(true);
		when(resultSetCacheManager.getResult()).thenReturn(resultSet);

		try (MockedStatic<ApplicationContextAccessor> applicationContext = mockStatic(ApplicationContextAccessor.class);
				MockedStatic<CacheUtils> cacheUtils = mockStatic(CacheUtils.class)) {
			applicationContext.when(() -> ApplicationContextAccessor.getBean(HCRHelper.class)).thenReturn(hcrHelper);
			cacheUtils.when(() -> CacheUtils.getCacheManager("/hcrResultSet")).thenReturn(resultSetCacheManager);
			cacheUtils.when(() -> CacheUtils.getCacheNameFromConnection(any(JsonObject.class))).thenReturn("report-cache");

			ArgumentCaptor<JsonObject> requestCaptor = ArgumentCaptor.forClass(JsonObject.class);

			JRDataSource dataSource = LazySubDatasetDataSourceFactory.resolve(factory, "mode_of_payment", "Card");

			assertNotNull(dataSource);
			cacheUtils.verify(() -> CacheUtils.getCacheNameFromConnection(requestCaptor.capture()));

			JsonObject mergedConnectionDetails = requestCaptor.getValue().getAsJsonObject("connectionDetails");
			assertEquals("temp-uuid-1", mergedConnectionDetails.get("temp_uuid").getAsString());
			assertEquals(1, mergedConnectionDetails.get("map_id").getAsInt());
			assertEquals("Card", mergedConnectionDetails.get("mode_of_payment").getAsString());
		}
	}

	@Test
	public void resolve_withoutMappedParameters_usesOriginalConnectionDetails() throws Exception {
		JsonObject connectionDetails = new JsonObject();
		connectionDetails.addProperty("temp_uuid", "temp-uuid-2");
		connectionDetails.addProperty("map_id", 2);

		LazySubDatasetDataSourceFactory factory = new LazySubDatasetDataSourceFactory(connectionDetails);
		HCRHelper hcrHelper = new HCRHelper();
		CacheHelper cacheHelper = mock(CacheHelper.class);
		HCRQueryProcessCacheManagerForResultSet resultSetCacheManager =
				mock(HCRQueryProcessCacheManagerForResultSet.class);
		Cache cache = mock(Cache.class);
		injectCacheHelper(hcrHelper, cacheHelper);

		when(cacheHelper.prepareCacheFromRequest(resultSetCacheManager)).thenReturn(cache);
		when(cacheHelper.processCache(null, null, "report-cache", false, cache, resultSetCacheManager))
				.thenReturn(false);

		try (MockedStatic<ApplicationContextAccessor> applicationContext = mockStatic(ApplicationContextAccessor.class);
				MockedStatic<CacheUtils> cacheUtils = mockStatic(CacheUtils.class)) {
			applicationContext.when(() -> ApplicationContextAccessor.getBean(HCRHelper.class)).thenReturn(hcrHelper);
			cacheUtils.when(() -> CacheUtils.getCacheManager("/hcrResultSet")).thenReturn(resultSetCacheManager);
			cacheUtils.when(() -> CacheUtils.getCacheNameFromConnection(any(JsonObject.class))).thenReturn("report-cache");

			ArgumentCaptor<JsonObject> requestCaptor = ArgumentCaptor.forClass(JsonObject.class);

			LazySubDatasetDataSourceFactory.resolve(factory);

			cacheUtils.verify(() -> CacheUtils.getCacheNameFromConnection(requestCaptor.capture()));

			JsonObject mergedConnectionDetails = requestCaptor.getValue().getAsJsonObject("connectionDetails");
			assertEquals("temp-uuid-2", mergedConnectionDetails.get("temp_uuid").getAsString());
			assertEquals(2, mergedConnectionDetails.get("map_id").getAsInt());
		}
	}

	@Test(expected = EfwServiceException.class)
	public void resolve_throwsWhenNameValuePairsAreOdd() throws Exception {
		JsonObject connectionDetails = new JsonObject();
		connectionDetails.addProperty("temp_uuid", "temp-uuid-3");

		LazySubDatasetDataSourceFactory factory = new LazySubDatasetDataSourceFactory(connectionDetails);
		HCRHelper hcrHelper = new HCRHelper();

		try (MockedStatic<ApplicationContextAccessor> applicationContext = mockStatic(ApplicationContextAccessor.class)) {
			applicationContext.when(() -> ApplicationContextAccessor.getBean(HCRHelper.class)).thenReturn(hcrHelper);
			LazySubDatasetDataSourceFactory.resolve(factory, "mode_of_payment");
		}
	}

	@Test
	public void resolve_skipsNullMappedValues() throws Exception {
		JsonObject connectionDetails = new JsonObject();
		connectionDetails.addProperty("temp_uuid", "temp-uuid-4");
		connectionDetails.addProperty("mode_of_payment", "Cash");

		LazySubDatasetDataSourceFactory factory = new LazySubDatasetDataSourceFactory(connectionDetails);
		HCRHelper hcrHelper = new HCRHelper();
		CacheHelper cacheHelper = mock(CacheHelper.class);
		HCRQueryProcessCacheManagerForResultSet resultSetCacheManager =
				mock(HCRQueryProcessCacheManagerForResultSet.class);
		Cache cache = mock(Cache.class);
		injectCacheHelper(hcrHelper, cacheHelper);

		when(cacheHelper.prepareCacheFromRequest(resultSetCacheManager)).thenReturn(cache);
		when(cacheHelper.processCache(null, null, "report-cache", false, cache, resultSetCacheManager))
				.thenReturn(false);

		try (MockedStatic<ApplicationContextAccessor> applicationContext = mockStatic(ApplicationContextAccessor.class);
				MockedStatic<CacheUtils> cacheUtils = mockStatic(CacheUtils.class)) {
			applicationContext.when(() -> ApplicationContextAccessor.getBean(HCRHelper.class)).thenReturn(hcrHelper);
			cacheUtils.when(() -> CacheUtils.getCacheManager("/hcrResultSet")).thenReturn(resultSetCacheManager);
			cacheUtils.when(() -> CacheUtils.getCacheNameFromConnection(any(JsonObject.class))).thenReturn("report-cache");

			ArgumentCaptor<JsonObject> requestCaptor = ArgumentCaptor.forClass(JsonObject.class);

			LazySubDatasetDataSourceFactory.resolve(factory, "mode_of_payment", null);

			cacheUtils.verify(() -> CacheUtils.getCacheNameFromConnection(requestCaptor.capture()));

			JsonObject mergedConnectionDetails = requestCaptor.getValue().getAsJsonObject("connectionDetails");
			assertEquals("Cash", mergedConnectionDetails.get("mode_of_payment").getAsString());
		}
	}

	@Test
	public void constructor_copiesConnectionDetails() {
		JsonObject connectionDetails = new JsonObject();
		connectionDetails.addProperty("temp_uuid", "temp-uuid-5");

		LazySubDatasetDataSourceFactory factory = new LazySubDatasetDataSourceFactory(connectionDetails);
		connectionDetails.addProperty("temp_uuid", "changed");

		assertEquals("temp-uuid-5", factory.getConnectionDetails().get("temp_uuid").getAsString());
	}

	@Test
	public void resolve_supportsMultipleMappedParameters() throws Exception {
		JsonObject connectionDetails = new JsonObject();
		connectionDetails.addProperty("temp_uuid", "temp-uuid-6");

		LazySubDatasetDataSourceFactory factory = new LazySubDatasetDataSourceFactory(connectionDetails);
		HCRHelper hcrHelper = new HCRHelper();
		CacheHelper cacheHelper = mock(CacheHelper.class);
		HCRQueryProcessCacheManagerForResultSet resultSetCacheManager =
				mock(HCRQueryProcessCacheManagerForResultSet.class);
		Cache cache = mock(Cache.class);
		injectCacheHelper(hcrHelper, cacheHelper);

		when(cacheHelper.prepareCacheFromRequest(resultSetCacheManager)).thenReturn(cache);
		when(cacheHelper.processCache(null, null, "report-cache", false, cache, resultSetCacheManager))
				.thenReturn(false);

		try (MockedStatic<ApplicationContextAccessor> applicationContext = mockStatic(ApplicationContextAccessor.class);
				MockedStatic<CacheUtils> cacheUtils = mockStatic(CacheUtils.class)) {
			applicationContext.when(() -> ApplicationContextAccessor.getBean(HCRHelper.class)).thenReturn(hcrHelper);
			cacheUtils.when(() -> CacheUtils.getCacheManager("/hcrResultSet")).thenReturn(resultSetCacheManager);
			cacheUtils.when(() -> CacheUtils.getCacheNameFromConnection(any(JsonObject.class))).thenReturn("report-cache");

			ArgumentCaptor<JsonObject> requestCaptor = ArgumentCaptor.forClass(JsonObject.class);

			LazySubDatasetDataSourceFactory.resolve(factory,
					"mode_of_payment", "Cash",
					"region", "APAC");

			cacheUtils.verify(() -> CacheUtils.getCacheNameFromConnection(requestCaptor.capture()));

			JsonObject mergedConnectionDetails = requestCaptor.getValue().getAsJsonObject("connectionDetails");
			assertEquals("Cash", mergedConnectionDetails.get("mode_of_payment").getAsString());
			assertEquals("APAC", mergedConnectionDetails.get("region").getAsString());
		}
	}

	@Test
	public void resolve_mergesNumericMappedParameter() throws Exception {
		JsonObject connectionDetails = new JsonObject();
		connectionDetails.addProperty("temp_uuid", "temp-uuid-numeric");

		LazySubDatasetDataSourceFactory factory = new LazySubDatasetDataSourceFactory(connectionDetails);
		HCRHelper hcrHelper = new HCRHelper();
		CacheHelper cacheHelper = mock(CacheHelper.class);
		HCRQueryProcessCacheManagerForResultSet resultSetCacheManager =
				mock(HCRQueryProcessCacheManagerForResultSet.class);
		Cache cache = mock(Cache.class);
		injectCacheHelper(hcrHelper, cacheHelper);

		when(cacheHelper.prepareCacheFromRequest(resultSetCacheManager)).thenReturn(cache);
		when(cacheHelper.processCache(null, null, "report-cache", false, cache, resultSetCacheManager))
				.thenReturn(false);

		try (MockedStatic<ApplicationContextAccessor> applicationContext = mockStatic(ApplicationContextAccessor.class);
				MockedStatic<CacheUtils> cacheUtils = mockStatic(CacheUtils.class)) {
			applicationContext.when(() -> ApplicationContextAccessor.getBean(HCRHelper.class)).thenReturn(hcrHelper);
			cacheUtils.when(() -> CacheUtils.getCacheManager("/hcrResultSet")).thenReturn(resultSetCacheManager);
			cacheUtils.when(() -> CacheUtils.getCacheNameFromConnection(any(JsonObject.class))).thenReturn("report-cache");

			ArgumentCaptor<JsonObject> requestCaptor = ArgumentCaptor.forClass(JsonObject.class);

			LazySubDatasetDataSourceFactory.resolve(factory, "TotalPrice", 1000);

			cacheUtils.verify(() -> CacheUtils.getCacheNameFromConnection(requestCaptor.capture()));

			JsonObject mergedConnectionDetails = requestCaptor.getValue().getAsJsonObject("connectionDetails");
			assertEquals(1000, mergedConnectionDetails.get("TotalPrice").getAsInt());
		}
	}

	@Test
	public void resolve_mergesStringCollectionMappedParameter() throws Exception {
		JsonObject connectionDetails = new JsonObject();
		connectionDetails.addProperty("temp_uuid", "temp-uuid-collection");

		LazySubDatasetDataSourceFactory factory = new LazySubDatasetDataSourceFactory(connectionDetails);
		HCRHelper hcrHelper = new HCRHelper();
		CacheHelper cacheHelper = mock(CacheHelper.class);
		HCRQueryProcessCacheManagerForResultSet resultSetCacheManager =
				mock(HCRQueryProcessCacheManagerForResultSet.class);
		Cache cache = mock(Cache.class);
		injectCacheHelper(hcrHelper, cacheHelper);

		when(cacheHelper.prepareCacheFromRequest(resultSetCacheManager)).thenReturn(cache);
		when(cacheHelper.processCache(null, null, "report-cache", false, cache, resultSetCacheManager))
				.thenReturn(false);

		try (MockedStatic<ApplicationContextAccessor> applicationContext = mockStatic(ApplicationContextAccessor.class);
				MockedStatic<CacheUtils> cacheUtils = mockStatic(CacheUtils.class)) {
			applicationContext.when(() -> ApplicationContextAccessor.getBean(HCRHelper.class)).thenReturn(hcrHelper);
			cacheUtils.when(() -> CacheUtils.getCacheManager("/hcrResultSet")).thenReturn(resultSetCacheManager);
			cacheUtils.when(() -> CacheUtils.getCacheNameFromConnection(any(JsonObject.class))).thenReturn("report-cache");

			ArgumentCaptor<JsonObject> requestCaptor = ArgumentCaptor.forClass(JsonObject.class);

			LazySubDatasetDataSourceFactory.resolve(factory, "Payment", List.of("NetBanking", "Cash"));

			cacheUtils.verify(() -> CacheUtils.getCacheNameFromConnection(requestCaptor.capture()));

			JsonObject mergedConnectionDetails = requestCaptor.getValue().getAsJsonObject("connectionDetails");
			JsonArray paymentValues = mergedConnectionDetails.getAsJsonArray("Payment");
			assertEquals(2, paymentValues.size());
			assertEquals("NetBanking", paymentValues.get(0).getAsString());
			assertEquals("Cash", paymentValues.get(1).getAsString());
		}
	}

	@Test
	public void resolve_mergesNumericCollectionMappedParameter() throws Exception {
		JsonObject connectionDetails = new JsonObject();
		connectionDetails.addProperty("temp_uuid", "temp-uuid-numeric-collection");

		LazySubDatasetDataSourceFactory factory = new LazySubDatasetDataSourceFactory(connectionDetails);
		HCRHelper hcrHelper = new HCRHelper();
		CacheHelper cacheHelper = mock(CacheHelper.class);
		HCRQueryProcessCacheManagerForResultSet resultSetCacheManager =
				mock(HCRQueryProcessCacheManagerForResultSet.class);
		Cache cache = mock(Cache.class);
		injectCacheHelper(hcrHelper, cacheHelper);

		when(cacheHelper.prepareCacheFromRequest(resultSetCacheManager)).thenReturn(cache);
		when(cacheHelper.processCache(null, null, "report-cache", false, cache, resultSetCacheManager))
				.thenReturn(false);

		try (MockedStatic<ApplicationContextAccessor> applicationContext = mockStatic(ApplicationContextAccessor.class);
				MockedStatic<CacheUtils> cacheUtils = mockStatic(CacheUtils.class)) {
			applicationContext.when(() -> ApplicationContextAccessor.getBean(HCRHelper.class)).thenReturn(hcrHelper);
			cacheUtils.when(() -> CacheUtils.getCacheManager("/hcrResultSet")).thenReturn(resultSetCacheManager);
			cacheUtils.when(() -> CacheUtils.getCacheNameFromConnection(any(JsonObject.class))).thenReturn("report-cache");

			ArgumentCaptor<JsonObject> requestCaptor = ArgumentCaptor.forClass(JsonObject.class);

			LazySubDatasetDataSourceFactory.resolve(factory, "Total Price", new Integer[] { 100, 200 });

			cacheUtils.verify(() -> CacheUtils.getCacheNameFromConnection(requestCaptor.capture()));

			JsonObject mergedConnectionDetails = requestCaptor.getValue().getAsJsonObject("connectionDetails");
			JsonArray totalPriceValues = mergedConnectionDetails.getAsJsonArray("Total Price");
			assertEquals(2, totalPriceValues.size());
			assertEquals(100, totalPriceValues.get(0).getAsInt());
			assertEquals(200, totalPriceValues.get(1).getAsInt());
			assertTrue(mergedConnectionDetails.get("Total Price").isJsonArray());
		}
	}

	private void injectCacheHelper(HCRHelper hcrHelper, CacheHelper cacheHelper)
			throws NoSuchFieldException, IllegalAccessException {
		Field field = HCRHelper.class.getDeclaredField("cacheHelper");
		field.setAccessible(true);
		field.set(hcrHelper, cacheHelper);
	}
}
