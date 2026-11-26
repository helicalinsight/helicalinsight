package com.helicalinsight.datasource.managed;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;

import org.junit.Test;
import org.mockito.MockedStatic;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.utility.JsonUtils;

public class JdbcServiceImplLimitTest {

	@Test
	public void previewLimitIsPassedOnlyForTheCallThatSetsIt() throws Exception {
		JdbcServiceImpl service = new JdbcServiceImpl();
		JdbcDaoImpl jdbcDao = mock(JdbcDaoImpl.class);
		IGlobalXmlReader globalXmlReader = mock(IGlobalXmlReader.class);
		IJdbcConnectionService connectionService = mock(IJdbcConnectionService.class);
		set(service, "jdbcDao", jdbcDao);
		set(service, "globalXmlReader", globalXmlReader);
		set(service, "jdbcConnectionService", connectionService);

		when(globalXmlReader.getDataSourceJson(1)).thenReturn("{}");
		when(connectionService.getDatabaseConnection(any())).thenReturn(mock(Connection.class));
		when(jdbcDao.query(any(), any())).thenReturn(new JsonObject());

		try (MockedStatic<JsonUtils> jsonUtils = mockStatic(JsonUtils.class)) {
			jsonUtils.when(JsonUtils::isDSTypeStorageDatabase).thenReturn(false);

			service.setLimit(10);
			service.execute("connections.xml", 1, "select * from travel_details");
			service.execute("connections.xml", 1, "select * from products");
		}

		verify(jdbcDao, times(1)).setLimit(10);
		verify(jdbcDao, never()).setLimit(null);
		verify(jdbcDao).query(any(), eq("select * from travel_details"));
		verify(jdbcDao).query(any(), eq("select * from products"));
	}

	private static void set(Object target, String name, Object value) throws Exception {
		java.lang.reflect.Field field = JdbcServiceImpl.class.getDeclaredField(name);
		field.setAccessible(true);
		field.set(target, value);
	}
}
