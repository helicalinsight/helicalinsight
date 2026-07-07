package com.helicalinsight.adhoc.metadata.genericdb;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.AddRemoveTableColumnsHandler;
import com.helicalinsight.adhoc.genericsql.AdhocUtils;
import com.helicalinsight.adhoc.metadata.DbMetadataUtils;
import com.helicalinsight.adhoc.metadata.jaxb.Column;
import com.helicalinsight.adhoc.metadata.jaxb.Columns;
import com.helicalinsight.adhoc.metadata.jaxb.ConnectionDetails;
import com.helicalinsight.adhoc.metadata.jaxb.Database;
import com.helicalinsight.adhoc.metadata.jaxb.DriverClass;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.metadata.jaxb.Table;
import com.helicalinsight.adhoc.metadata.jaxb.Tables;
import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.datasource.GlobalJdbcTypeUtils;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.EfwdDatasourceUtils;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DatabaseMetadataUpdateHandlerTest {

	@Test
	public void ut_a1_test_isThreadSafeToCache() {
		DatabaseMetadataUpdateHandler databaseMetadataUpdateHandler = new DatabaseMetadataUpdateHandler();
		boolean threadSafeToCache = databaseMetadataUpdateHandler.isThreadSafeToCache();
		assertTrue(threadSafeToCache);
	}

	@Test(expected = RequiredParameterIsNullException.class)
	public void ut_a2_test_executeComponent() {
		DatabaseMetadataUpdateHandler databaseMetadataUpdateHandler = new DatabaseMetadataUpdateHandler();
		JsonObject formJson = new JsonObject();
		JsonObject dataSource = new JsonObject();
		dataSource.addProperty("id", "id");
		dataSource.addProperty("type", "type");
		dataSource.addProperty("dir", "dir");
		formJson.add("dataSource", dataSource);
		formJson.addProperty("fileName", "fileName");
		databaseMetadataUpdateHandler.executeComponent(formJson.toString());

	}

	@Test(expected = MetadataServiceException.class)
	public void ut_a3_test_executeComponent() {
		DatabaseMetadataUpdateHandler databaseMetadataUpdateHandler = new DatabaseMetadataUpdateHandler();
		JsonObject formJson = new JsonObject();
		JsonObject dataSource = new JsonObject();
		dataSource.addProperty("id", "id");
		dataSource.addProperty("type", "type");
		dataSource.addProperty("dir", "dir");
		formJson.add("dataSource", dataSource);
		formJson.addProperty("fileName", "fileName");
		formJson.addProperty("location", "location");
		formJson.addProperty("uniqueId", "uniqueId");
		formJson.addProperty("newLocation", "newLocation");
		formJson.addProperty("databaseDialect", "databaseDialect");

		File efwdFile = mock(File.class);

		try (MockedStatic<GlobalJdbcTypeUtils> mockedStatic = mockStatic(GlobalJdbcTypeUtils.class)) {
			try (MockedStatic<ApplicationUtilities> mockedStatic1 = mockStatic(ApplicationUtilities.class)) {
				try (MockedStatic<EfwdDatasourceUtils> mockedStatic2 = mockStatic(EfwdDatasourceUtils.class)) {

					mockedStatic.when(() -> GlobalJdbcTypeUtils.isTypeGlobal(anyString())).thenReturn(false);
					mockedStatic1.when(() -> ApplicationUtilities.getEfwdFile(anyString())).thenReturn(efwdFile);
					mockedStatic2.when(() -> EfwdDatasourceUtils.newGetJsonOfEfwd(efwdFile))
							.thenReturn(new JsonObject());
					mockedStatic2.when(() -> EfwdDatasourceUtils.getEfwdConnection(any(), anyString(), anyString()))
							.thenReturn(new JsonObject());

					databaseMetadataUpdateHandler.executeComponent(formJson.toString());

				}
			}
		}

	}

	@Test(expected = MetadataServiceException.class)
	public void ut_a4_test_executeComponent() {
		DatabaseMetadataUpdateHandler databaseMetadataUpdateHandler = new DatabaseMetadataUpdateHandler();
		JsonObject formJson = new JsonObject();
		JsonObject dataSource = new JsonObject();
		dataSource.addProperty("id", "id");
		dataSource.addProperty("type", "type");
		dataSource.addProperty("dir", "dir");
		dataSource.addProperty("changed", "true");

		formJson.add("dataSource", dataSource);
		formJson.addProperty("fileName", "fileName");
		formJson.addProperty("location", "location");
		formJson.addProperty("uniqueId", "uniqueId");
		formJson.addProperty("newLocation", "newLocation");
		formJson.addProperty("databaseDialect", "databaseDialect");
		JsonObject tables = new JsonObject();
		formJson.add("tables", tables);
		JsonObject columns = new JsonObject();
		formJson.add("columns", columns);
		formJson.addProperty("uuid", "uuid");
		JsonArray views = new JsonArray();
		formJson.add("views", views);
		JsonArray joins = new JsonArray();
		formJson.add("joins", joins);

		formJson.addProperty("database", "database");
		formJson.addProperty("catalog", "catalog");
		formJson.addProperty("schema", "schema");

		File efwdFile = mock(File.class);

		try (MockedStatic<GlobalJdbcTypeUtils> mockedStatic = mockStatic(GlobalJdbcTypeUtils.class)) {
			try (MockedStatic<ApplicationUtilities> mockedStatic1 = mockStatic(ApplicationUtilities.class)) {
				try (MockedStatic<EfwdDatasourceUtils> mockedStatic2 = mockStatic(EfwdDatasourceUtils.class)) {

					mockedStatic.when(() -> GlobalJdbcTypeUtils.isTypeGlobal(anyString())).thenReturn(false);
					mockedStatic1.when(() -> ApplicationUtilities.getEfwdFile(anyString())).thenReturn(efwdFile);
					mockedStatic2.when(() -> EfwdDatasourceUtils.newGetJsonOfEfwd(efwdFile))
							.thenReturn(new JsonObject());
					mockedStatic2.when(() -> EfwdDatasourceUtils.getEfwdConnection(any(), anyString(), anyString()))
							.thenReturn(new JsonObject());

					databaseMetadataUpdateHandler.executeComponent(formJson.toString());

				}
			}
		}

	}

	@Test(expected = RequiredParameterIsNullException.class)
	public void ut_a5_test_executeComponent() {
		DatabaseMetadataUpdateHandler databaseMetadataUpdateHandler = new DatabaseMetadataUpdateHandler();
		JsonObject formJson = new JsonObject();
		JsonObject dataSource = new JsonObject();
		dataSource.addProperty("id", "id");
		dataSource.addProperty("type", "type");
		dataSource.addProperty("dir", "dir");
		dataSource.addProperty("changed", "true");

		formJson.add("dataSource", dataSource);
		formJson.addProperty("location", "location");
		formJson.addProperty("fileName", "fileName");
		formJson.addProperty("newLocation", "newLocation");
		formJson.addProperty("databaseDialect", "databaseDialect");
		JsonObject tables = new JsonObject();
		formJson.add("tables", tables);
		JsonObject columns = new JsonObject();
		formJson.add("columns", columns);
		JsonArray views = new JsonArray();
		formJson.add("views", views);
		JsonArray joins = new JsonArray();
		formJson.add("joins", joins);

		formJson.addProperty("database", "database");
		formJson.addProperty("catalog", "catalog");
		formJson.addProperty("schema", "schema");

		File efwdFile = mock(File.class);

		try (MockedStatic<GlobalJdbcTypeUtils> mockedStatic = mockStatic(GlobalJdbcTypeUtils.class)) {
			try (MockedStatic<ApplicationUtilities> mockedStatic1 = mockStatic(ApplicationUtilities.class)) {
				try (MockedStatic<EfwdDatasourceUtils> mockedStatic2 = mockStatic(EfwdDatasourceUtils.class)) {

					mockedStatic.when(() -> GlobalJdbcTypeUtils.isTypeGlobal(anyString())).thenReturn(false);
					mockedStatic1.when(() -> ApplicationUtilities.getEfwdFile(anyString())).thenReturn(efwdFile);
					mockedStatic2.when(() -> EfwdDatasourceUtils.newGetJsonOfEfwd(efwdFile))
							.thenReturn(new JsonObject());
					mockedStatic2.when(() -> EfwdDatasourceUtils.getEfwdConnection(any(), anyString(), anyString()))
							.thenReturn(new JsonObject());

					databaseMetadataUpdateHandler.executeComponent(formJson.toString());

				}
			}
		}

	}

	@Test(expected = MetadataServiceException.class)
	public void ut_a6_test_executeComponent() {
		DatabaseMetadataUpdateHandler databaseMetadataUpdateHandler = new DatabaseMetadataUpdateHandler();
		JsonObject formJson = new JsonObject();
		JsonObject dataSource = new JsonObject();
		dataSource.addProperty("id", "id");
		dataSource.addProperty("type", "type");
		dataSource.addProperty("dir", "dir");
		dataSource.addProperty("changed", "true");

		formJson.add("dataSource", dataSource);
		formJson.addProperty("location", "location");
		formJson.addProperty("fileName", "fileName");
		formJson.addProperty("uniqueId", "uniqueId");

		formJson.addProperty("newLocation", "newLocation");
		formJson.addProperty("databaseDialect", "databaseDialect");
		JsonObject tables = new JsonObject();
		formJson.add("tables", tables);
		JsonObject columns = new JsonObject();
		formJson.add("columns", columns);
		JsonArray views = new JsonArray();
		formJson.add("views", views);
		JsonArray joins = new JsonArray();
		formJson.add("joins", joins);

		formJson.addProperty("database", "database");
		formJson.addProperty("catalog", "catalog");
		formJson.addProperty("schema", "schema");

		File efwdFile = mock(File.class);

		try (MockedStatic<GlobalJdbcTypeUtils> mockedStatic = mockStatic(GlobalJdbcTypeUtils.class)) {
			try (MockedStatic<ApplicationUtilities> mockedStatic1 = mockStatic(ApplicationUtilities.class)) {
				try (MockedStatic<EfwdDatasourceUtils> mockedStatic2 = mockStatic(EfwdDatasourceUtils.class)) {

					mockedStatic.when(() -> GlobalJdbcTypeUtils.isTypeGlobal(anyString())).thenReturn(false);
					mockedStatic1.when(() -> ApplicationUtilities.getEfwdFile(anyString())).thenReturn(efwdFile);
					mockedStatic2.when(() -> EfwdDatasourceUtils.newGetJsonOfEfwd(efwdFile))
							.thenReturn(new JsonObject());
					mockedStatic2.when(() -> EfwdDatasourceUtils.getEfwdConnection(any(), anyString(), anyString()))
							.thenReturn(new JsonObject());

					databaseMetadataUpdateHandler.executeComponent(formJson.toString());

				}
			}
		}

	}

	@Test(expected = IllegalArgumentException.class)
	public void ut_a7_test_executeComponent() {
		DatabaseMetadataUpdateHandler databaseMetadataUpdateHandler = new DatabaseMetadataUpdateHandler();
		JsonObject formJson = new JsonObject();
		JsonObject dataSource = new JsonObject();
		dataSource.addProperty("id", "id");
		dataSource.addProperty("type", "type");
		dataSource.addProperty("dir", "dir");
		dataSource.addProperty("changed", "true");

		formJson.add("dataSource", dataSource);
		formJson.addProperty("location", "location");
		formJson.addProperty("uniqueId", "uniqueId");
		formJson.addProperty("databaseDialect", "databaseDialect");
		formJson.addProperty("newLocation", "newLocation");
		JsonObject tables = new JsonObject();
		formJson.add("tables", tables);
		JsonObject columns = new JsonObject();
		formJson.add("columns", columns);
		JsonArray views = new JsonArray();
		formJson.add("views", views);
		JsonArray joins = new JsonArray();
		formJson.add("joins", joins);

		formJson.addProperty("database", "database");
		formJson.addProperty("catalog", "catalog");
		formJson.addProperty("schema", "schema");

		File efwdFile = mock(File.class);

		try (MockedStatic<GlobalJdbcTypeUtils> mockedStatic = mockStatic(GlobalJdbcTypeUtils.class)) {
			try (MockedStatic<ApplicationUtilities> mockedStatic1 = mockStatic(ApplicationUtilities.class)) {
				try (MockedStatic<EfwdDatasourceUtils> mockedStatic2 = mockStatic(EfwdDatasourceUtils.class)) {

					mockedStatic.when(() -> GlobalJdbcTypeUtils.isTypeGlobal(anyString())).thenReturn(false);
					mockedStatic1.when(() -> ApplicationUtilities.getEfwdFile(anyString())).thenReturn(efwdFile);
					mockedStatic2.when(() -> EfwdDatasourceUtils.newGetJsonOfEfwd(efwdFile))
							.thenReturn(new JsonObject());
					mockedStatic2.when(() -> EfwdDatasourceUtils.getEfwdConnection(any(), anyString(), anyString()))
							.thenReturn(new JsonObject());

					databaseMetadataUpdateHandler.executeComponent(formJson.toString());

				}
			}
		}

	}

	@Test(expected = IllegalStateException.class)
	public void ut_a8_test_executeComponent() {
		DatabaseMetadataUpdateHandler databaseMetadataUpdateHandler = new DatabaseMetadataUpdateHandler();
		JsonObject formJson = new JsonObject();
		JsonObject dataSource = new JsonObject();
		dataSource.addProperty("id", "id");
		dataSource.addProperty("type", "type");
		dataSource.addProperty("dir", "dir");
		dataSource.addProperty("changed", "true");

		formJson.add("dataSource", dataSource);
		formJson.addProperty("fileName", "fileName");

		formJson.addProperty("location", "location");
		formJson.addProperty("uniqueId", "uniqueId");

		formJson.addProperty("newLocation", "newLocation");
		JsonObject tables = new JsonObject();
		formJson.add("tables", tables);
		JsonObject columns = new JsonObject();
		formJson.add("columns", columns);
		JsonArray views = new JsonArray();
		formJson.add("views", views);
		JsonArray joins = new JsonArray();
		formJson.add("joins", joins);
		formJson.addProperty("uuid", "uuid");
		formJson.addProperty("database", "database");
		formJson.addProperty("catalog", "catalog");
		formJson.addProperty("schema", "schema");

		File efwdFile = mock(File.class);
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("databaseDialect", "databaseDialect");
		try (MockedStatic<GlobalJdbcTypeUtils> mockedStatic = mockStatic(GlobalJdbcTypeUtils.class)) {
			try (MockedStatic<ApplicationUtilities> mockedStatic1 = mockStatic(ApplicationUtilities.class)) {
				try (MockedStatic<EfwdDatasourceUtils> mockedStatic2 = mockStatic(EfwdDatasourceUtils.class)) {
					try (MockedStatic<JaxbUtils> mockedStatic3 = mockStatic(JaxbUtils.class)) {

						mockedStatic.when(() -> GlobalJdbcTypeUtils.isTypeGlobal(anyString())).thenReturn(false);
						mockedStatic1.when(() -> ApplicationUtilities.getEfwdFile(anyString())).thenReturn(efwdFile);
						mockedStatic2.when(() -> EfwdDatasourceUtils.newGetJsonOfEfwd(efwdFile))
								.thenReturn(new JsonObject());
						mockedStatic2.when(() -> EfwdDatasourceUtils.getEfwdConnection(any(), anyString(), anyString()))
								.thenReturn(jsonObject);
						mockedStatic3.when(() -> JaxbUtils.unMarshal(any(), any())).thenReturn(null);
						databaseMetadataUpdateHandler.executeComponent(formJson.toString());

					}
				}
			}
		}

	}

	@Test(expected = IllegalStateException.class)
	public void ut_a9_test_executeComponent() {
		DatabaseMetadataUpdateHandler databaseMetadataUpdateHandler = new DatabaseMetadataUpdateHandler();
		JsonObject formJson = new JsonObject();
		JsonObject dataSource = new JsonObject();
		dataSource.addProperty("id", "id");
		dataSource.addProperty("type", "type");
		dataSource.addProperty("dir", "dir");
		dataSource.addProperty("changed", "true");

		formJson.add("dataSource", dataSource);
		formJson.addProperty("fileName", "fileName");

		formJson.addProperty("location", "location");
		formJson.addProperty("uniqueId", "uniqueId");

		formJson.addProperty("newLocation", "newLocation");
		JsonObject tables = new JsonObject();
		formJson.add("tables", tables);
		JsonObject columns = new JsonObject();
		formJson.add("columns", columns);
		JsonArray views = new JsonArray();
		formJson.add("views", views);
		JsonArray joins = new JsonArray();
		formJson.add("joins", joins);
		formJson.addProperty("uuid", "uuid");
		formJson.addProperty("database", "database");
		formJson.addProperty("catalog", "catalog");
		formJson.addProperty("schema", "schema");

		File efwdFile = mock(File.class);
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("driverName", "databaseDialect");
		try (MockedStatic<GlobalJdbcTypeUtils> mockedStatic = mockStatic(GlobalJdbcTypeUtils.class)) {
			try (MockedStatic<ApplicationUtilities> mockedStatic1 = mockStatic(ApplicationUtilities.class)) {
				try (MockedStatic<EfwdDatasourceUtils> mockedStatic2 = mockStatic(EfwdDatasourceUtils.class)) {
					try (MockedStatic<JaxbUtils> mockedStatic3 = mockStatic(JaxbUtils.class)) {
						try (MockedStatic<JsonUtils> mockedStatic4 = mockStatic(JsonUtils.class)) {

							mockedStatic.when(() -> GlobalJdbcTypeUtils.isTypeGlobal(anyString())).thenReturn(false);
							mockedStatic1.when(() -> ApplicationUtilities.getEfwdFile(anyString()))
									.thenReturn(efwdFile);
							mockedStatic2.when(() -> EfwdDatasourceUtils.newGetJsonOfEfwd(efwdFile))
									.thenReturn(new JsonObject());
							mockedStatic2
									.when(() -> EfwdDatasourceUtils.getEfwdConnection(any(), anyString(), anyString()))
									.thenReturn(jsonObject);
							mockedStatic3.when(() -> JaxbUtils.unMarshal(any(), any())).thenReturn(null);
							mockedStatic4.when(() -> JsonUtils.functionsReference(anyString())).thenReturn("reference");
							databaseMetadataUpdateHandler.executeComponent(formJson.toString());

						}
					}
				}
			}
		}

	}

	@Test(expected = MetadataServiceException.class)
	public void ut_b1_test_executeComponent() {
		DatabaseMetadataUpdateHandler databaseMetadataUpdateHandler = new DatabaseMetadataUpdateHandler();
		JsonObject formJson = new JsonObject();
		JsonObject dataSource = new JsonObject();
		dataSource.addProperty("id", "id");
		dataSource.addProperty("type", "type");
		dataSource.addProperty("dir", "dir");
		dataSource.addProperty("changed", "true");

		formJson.add("dataSource", dataSource);
		formJson.addProperty("fileName", "fileName");

		formJson.addProperty("location", "location");
		formJson.addProperty("uniqueId", "uniqueId");

		formJson.addProperty("newLocation", "newLocation");
		JsonObject tables = new JsonObject();
		formJson.add("tables", tables);
		JsonObject columns = new JsonObject();
		formJson.add("columns", columns);
		JsonArray views = new JsonArray();
		views.add("");
		formJson.add("views", views);
		JsonArray joins = new JsonArray();
		formJson.add("joins", joins);
		formJson.addProperty("uuid", "uuid");
		formJson.addProperty("database", "database");
		formJson.addProperty("catalog", "catalog");
		formJson.addProperty("schema", "schema");

		File efwdFile = mock(File.class);
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("databaseDialect", "");
		jsonObject.addProperty("Driver", "databaseDialect");
		Metadata metadata = mock(Metadata.class);
		Database database = mock(Database.class);
		ConnectionDetails connectionDetails = mock(ConnectionDetails.class);
		DriverClass driverClass = mock(DriverClass.class);
		MetadataDataSourceChangeHandler changeHandler = mock(MetadataDataSourceChangeHandler.class);
		ViewsUpdateHandler handler = mock(ViewsUpdateHandler.class);

		when(metadata.getConnectionDetails()).thenReturn(connectionDetails);
		when(connectionDetails.getDriverClass()).thenReturn(driverClass);

		when(metadata.getDatabase()).thenReturn(database);
		when(database.getId()).thenReturn(null);
		try (MockedStatic<GlobalJdbcTypeUtils> mockedStatic = mockStatic(GlobalJdbcTypeUtils.class)) {
			try (MockedStatic<ApplicationUtilities> mockedStatic1 = mockStatic(ApplicationUtilities.class)) {
				try (MockedStatic<EfwdDatasourceUtils> mockedStatic2 = mockStatic(EfwdDatasourceUtils.class)) {
					try (MockedStatic<JaxbUtils> mockedStatic3 = mockStatic(JaxbUtils.class)) {
						try (MockedStatic<JsonUtils> mockedStatic4 = mockStatic(JsonUtils.class)) {
							try (MockedStatic<AdhocUtils> mockedStatic5 = mockStatic(AdhocUtils.class)) {
								try (MockedStatic<ApplicationContextAccessor> mockedStatic6 = mockStatic(
										ApplicationContextAccessor.class)) {
									try (MockedStatic<AddRemoveTableColumnsHandler> mockedStatic7 = mockStatic(
											AddRemoveTableColumnsHandler.class)) {
										try (MockedStatic<DuplicateItemHandler> mockedStatic8 = mockStatic(
												DuplicateItemHandler.class)) {

											mockedStatic.when(() -> GlobalJdbcTypeUtils.isTypeGlobal(anyString()))
													.thenReturn(false);
											mockedStatic1.when(() -> ApplicationUtilities.getEfwdFile(anyString()))
													.thenReturn(efwdFile);
											mockedStatic2.when(() -> EfwdDatasourceUtils.newGetJsonOfEfwd(efwdFile))
													.thenReturn(new JsonObject());
											mockedStatic2.when(() -> EfwdDatasourceUtils.getEfwdConnection(any(),
													anyString(), anyString())).thenReturn(jsonObject);
											mockedStatic3.when(() -> JaxbUtils.unMarshal(any(), any()))
													.thenReturn(metadata);
											mockedStatic4.when(() -> JsonUtils.functionsReference(anyString()))
													.thenReturn("reference");
											mockedStatic5.when(() -> AdhocUtils.getUuid()).thenReturn("dbId");
											mockedStatic6
													.when(() -> ApplicationContextAccessor
															.getBean(MetadataDataSourceChangeHandler.class))
													.thenReturn(changeHandler);
											mockedStatic6.when(
													() -> ApplicationContextAccessor.getBean(ViewsUpdateHandler.class))
													.thenReturn(handler);

											databaseMetadataUpdateHandler.executeComponent(formJson.toString());
										}
									}
								}
							}
						}
					}
				}
			}
		}

	}

	@Test
	public void ut_b2_test_executeComponent() {
		DatabaseMetadataUpdateHandler databaseMetadataUpdateHandler = new DatabaseMetadataUpdateHandler();
		JsonObject formJson = new JsonObject();
		JsonObject dataSource = new JsonObject();
		dataSource.addProperty("id", "id");
		dataSource.addProperty("type", "type");
		dataSource.addProperty("dir", "dir");
		dataSource.addProperty("changed", "true");

		formJson.add("dataSource", dataSource);
		formJson.addProperty("fileName", "fileName");

		formJson.addProperty("location", "location");
		formJson.addProperty("uniqueId", "uniqueId");

		formJson.addProperty("newLocation", "newLocation");
		JsonObject tables = new JsonObject();
		formJson.add("tables", tables);
		JsonObject columns = new JsonObject();
		formJson.add("columns", columns);
		JsonArray views = new JsonArray();
		views.add("");
		formJson.add("views", views);
		JsonArray joins = new JsonArray();
		formJson.add("joins", joins);
		formJson.addProperty("uuid", "uuid");
		formJson.addProperty("database", "database");
		formJson.addProperty("catalog", "catalog");
		formJson.addProperty("schema", "schema");
		JsonObject access = new JsonObject();
		access.addProperty("action", "deleteAll");
		formJson.add("access", access);
		File efwdFile = mock(File.class);
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("databaseDialect", "[]");
		jsonObject.addProperty("Driver", "databaseDialect");
		Metadata metadata = mock(Metadata.class);
		Database database = mock(Database.class);
		ConnectionDetails connectionDetails = mock(ConnectionDetails.class);
		DriverClass driverClass = mock(DriverClass.class);
		MetadataDataSourceChangeHandler changeHandler = mock(MetadataDataSourceChangeHandler.class);
		ViewsUpdateHandler handler = mock(ViewsUpdateHandler.class);

		when(metadata.getConnectionDetails()).thenReturn(connectionDetails);
		when(connectionDetails.getDriverClass()).thenReturn(driverClass);

		when(metadata.getDatabase()).thenReturn(database);
		when(database.getId()).thenReturn(null);
		try (MockedStatic<GlobalJdbcTypeUtils> mockedStatic = mockStatic(GlobalJdbcTypeUtils.class)) {
			try (MockedStatic<ApplicationUtilities> mockedStatic1 = mockStatic(ApplicationUtilities.class)) {
				try (MockedStatic<EfwdDatasourceUtils> mockedStatic2 = mockStatic(EfwdDatasourceUtils.class)) {
					try (MockedStatic<JaxbUtils> mockedStatic3 = mockStatic(JaxbUtils.class)) {
						try (MockedStatic<JsonUtils> mockedStatic4 = mockStatic(JsonUtils.class)) {
							try (MockedStatic<AdhocUtils> mockedStatic5 = mockStatic(AdhocUtils.class)) {
								try (MockedStatic<ApplicationContextAccessor> mockedStatic6 = mockStatic(
										ApplicationContextAccessor.class)) {
									try (MockedStatic<AddRemoveTableColumnsHandler> mockedStatic7 = mockStatic(
											AddRemoveTableColumnsHandler.class)) {
										try (MockedStatic<DuplicateItemHandler> mockedStatic8 = mockStatic(
												DuplicateItemHandler.class)) {
											try (MockedStatic<DbMetadataUtils> mockedStatic9 = mockStatic(
													DbMetadataUtils.class)) {

												mockedStatic.when(() -> GlobalJdbcTypeUtils.isTypeGlobal(anyString()))
														.thenReturn(false);
												mockedStatic1.when(() -> ApplicationUtilities.getEfwdFile(anyString()))
														.thenReturn(efwdFile);
												mockedStatic2.when(() -> EfwdDatasourceUtils.newGetJsonOfEfwd(efwdFile))
														.thenReturn(new JsonObject());
												mockedStatic2.when(() -> EfwdDatasourceUtils.getEfwdConnection(any(),
														anyString(), anyString())).thenReturn(jsonObject);
												mockedStatic3.when(() -> JaxbUtils.unMarshal(any(), any()))
														.thenReturn(metadata);
												mockedStatic4.when(() -> JsonUtils.functionsReference(anyString()))
														.thenReturn("reference");
												mockedStatic5.when(() -> AdhocUtils.getUuid()).thenReturn("dbId");
												mockedStatic6
														.when(() -> ApplicationContextAccessor
																.getBean(MetadataDataSourceChangeHandler.class))
														.thenReturn(changeHandler);
												mockedStatic6.when(() -> ApplicationContextAccessor
														.getBean(ViewsUpdateHandler.class)).thenReturn(handler);
												mockedStatic9
														.when(() -> DbMetadataUtils.checkMetadataBeforeSave(metadata))
														.thenReturn(false);

												databaseMetadataUpdateHandler.executeComponent(formJson.toString());

											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

	}

	@Test
	public void ut_b3_test_executeComponent() {
		DatabaseMetadataUpdateHandler databaseMetadataUpdateHandler = new DatabaseMetadataUpdateHandler();
		JsonObject formJson = new JsonObject();
		JsonObject dataSource = new JsonObject();
		dataSource.addProperty("id", "id");
		dataSource.addProperty("type", "type");
		dataSource.addProperty("dir", "dir");
		dataSource.addProperty("changed", "true");

		formJson.add("dataSource", dataSource);
		formJson.addProperty("fileName", "fileName");

		formJson.addProperty("location", "location");
		formJson.addProperty("uniqueId", "uniqueId");
		JsonObject tables = new JsonObject();
		formJson.add("tables", tables);
		JsonObject columns = new JsonObject();
		formJson.add("columns", columns);
		JsonArray views = new JsonArray();
		views.add("");
		formJson.add("views", views);
		JsonArray joins = new JsonArray();
		formJson.add("joins", joins);

		formJson.addProperty("database", "database");
		formJson.addProperty("catalog", "catalog");
		formJson.addProperty("schema", "schema");
		JsonObject access = new JsonObject();
		JsonArray expression = new JsonArray();
		access.add("expression", expression);
		formJson.add("access", access);
		File efwdFile = mock(File.class);
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("databaseDialect", "[]");
		jsonObject.addProperty("Driver", "databaseDialect");
		Metadata metadata = mock(Metadata.class);
		Database database = mock(Database.class);
		ConnectionDetails connectionDetails = mock(ConnectionDetails.class);
		DriverClass driverClass = mock(DriverClass.class);
		MetadataDataSourceChangeHandler changeHandler = mock(MetadataDataSourceChangeHandler.class);
		ViewsUpdateHandler handler = mock(ViewsUpdateHandler.class);

		when(metadata.getConnectionDetails()).thenReturn(connectionDetails);
		when(connectionDetails.getDriverClass()).thenReturn(driverClass);

		when(metadata.getDatabase()).thenReturn(database);
		when(database.getId()).thenReturn(null);
		try (MockedStatic<GlobalJdbcTypeUtils> mockedStatic = mockStatic(GlobalJdbcTypeUtils.class)) {
			try (MockedStatic<ApplicationUtilities> mockedStatic1 = mockStatic(ApplicationUtilities.class)) {
				try (MockedStatic<EfwdDatasourceUtils> mockedStatic2 = mockStatic(EfwdDatasourceUtils.class)) {
					try (MockedStatic<JaxbUtils> mockedStatic3 = mockStatic(JaxbUtils.class)) {
						try (MockedStatic<JsonUtils> mockedStatic4 = mockStatic(JsonUtils.class)) {
							try (MockedStatic<AdhocUtils> mockedStatic5 = mockStatic(AdhocUtils.class)) {
								try (MockedStatic<ApplicationContextAccessor> mockedStatic6 = mockStatic(
										ApplicationContextAccessor.class)) {
									try (MockedStatic<AddRemoveTableColumnsHandler> mockedStatic7 = mockStatic(
											AddRemoveTableColumnsHandler.class)) {
										try (MockedStatic<DuplicateItemHandler> mockedStatic8 = mockStatic(
												DuplicateItemHandler.class)) {
											try (MockedStatic<DbMetadataUtils> mockedStatic9 = mockStatic(
													DbMetadataUtils.class)) {

												mockedStatic.when(() -> GlobalJdbcTypeUtils.isTypeGlobal(anyString()))
														.thenReturn(false);
												mockedStatic1.when(() -> ApplicationUtilities.getEfwdFile(anyString()))
														.thenReturn(efwdFile);
												mockedStatic2.when(() -> EfwdDatasourceUtils.newGetJsonOfEfwd(efwdFile))
														.thenReturn(new JsonObject());
												mockedStatic2.when(() -> EfwdDatasourceUtils.getEfwdConnection(any(),
														anyString(), anyString())).thenReturn(jsonObject);
												mockedStatic3.when(() -> JaxbUtils.unMarshal(any(), any()))
														.thenReturn(metadata);
												mockedStatic4.when(() -> JsonUtils.functionsReference(anyString()))
														.thenReturn("reference");
												mockedStatic5.when(() -> AdhocUtils.getUuid()).thenReturn("dbId");
												mockedStatic6
														.when(() -> ApplicationContextAccessor
																.getBean(MetadataDataSourceChangeHandler.class))
														.thenReturn(changeHandler);
												mockedStatic6.when(() -> ApplicationContextAccessor
														.getBean(ViewsUpdateHandler.class)).thenReturn(handler);
												mockedStatic9
														.when(() -> DbMetadataUtils.checkMetadataBeforeSave(metadata))
														.thenReturn(false);

												databaseMetadataUpdateHandler.executeComponent(formJson.toString());

											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

	}

	@Test
	public void ut_b4_test_executeComponent() {
		DatabaseMetadataUpdateHandler databaseMetadataUpdateHandler = new DatabaseMetadataUpdateHandler();
		JsonObject formJson = new JsonObject();
		JsonObject dataSource = new JsonObject();
		dataSource.addProperty("id", "id");
		dataSource.addProperty("type", "type");
		dataSource.addProperty("dir", "dir");
		dataSource.addProperty("changed", "true");

		formJson.add("dataSource", dataSource);
		formJson.addProperty("fileName", "fileName");

		formJson.addProperty("location", "location");
		formJson.addProperty("uniqueId", "uniqueId");
		JsonObject tables = new JsonObject();
		formJson.add("tables", tables);
		JsonObject columns = new JsonObject();
		formJson.add("columns", columns);
		JsonArray views = new JsonArray();
		views.add("");
		formJson.add("views", views);
		JsonArray joins = new JsonArray();
		formJson.add("joins", joins);

		formJson.addProperty("database", "database");
		formJson.addProperty("catalog", "catalog");
		formJson.addProperty("schema", "schema");
		JsonObject access = new JsonObject();
		JsonArray expression = new JsonArray();
		expression.add("");
		access.add("expression", expression);
		formJson.add("access", access);
		File efwdFile = mock(File.class);
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("databaseDialect", "[]");
		jsonObject.addProperty("Driver", "databaseDialect");
		Metadata metadata = mock(Metadata.class);
		Database database = mock(Database.class);
		ConnectionDetails connectionDetails = mock(ConnectionDetails.class);
		DriverClass driverClass = mock(DriverClass.class);
		MetadataDataSourceChangeHandler changeHandler = mock(MetadataDataSourceChangeHandler.class);
		ViewsUpdateHandler handler = mock(ViewsUpdateHandler.class);

		when(metadata.getConnectionDetails()).thenReturn(connectionDetails);
		when(connectionDetails.getDriverClass()).thenReturn(driverClass);

		when(metadata.getDatabase()).thenReturn(database);
		when(database.getId()).thenReturn(null);
		try (MockedStatic<GlobalJdbcTypeUtils> mockedStatic = mockStatic(GlobalJdbcTypeUtils.class)) {
			try (MockedStatic<ApplicationUtilities> mockedStatic1 = mockStatic(ApplicationUtilities.class)) {
				try (MockedStatic<EfwdDatasourceUtils> mockedStatic2 = mockStatic(EfwdDatasourceUtils.class)) {
					try (MockedStatic<JaxbUtils> mockedStatic3 = mockStatic(JaxbUtils.class)) {
						try (MockedStatic<JsonUtils> mockedStatic4 = mockStatic(JsonUtils.class)) {
							try (MockedStatic<AdhocUtils> mockedStatic5 = mockStatic(AdhocUtils.class)) {
								try (MockedStatic<ApplicationContextAccessor> mockedStatic6 = mockStatic(
										ApplicationContextAccessor.class)) {
									try (MockedStatic<AddRemoveTableColumnsHandler> mockedStatic7 = mockStatic(
											AddRemoveTableColumnsHandler.class)) {
										try (MockedStatic<DuplicateItemHandler> mockedStatic8 = mockStatic(
												DuplicateItemHandler.class)) {
											try (MockedStatic<DbMetadataUtils> mockedStatic9 = mockStatic(
													DbMetadataUtils.class)) {
												try (MockedStatic<MetadataSecurityUpdateHandler> mockedStatic10 = mockStatic(
														MetadataSecurityUpdateHandler.class)) {

													mockedStatic
															.when(() -> GlobalJdbcTypeUtils.isTypeGlobal(anyString()))
															.thenReturn(false);
													mockedStatic1
															.when(() -> ApplicationUtilities.getEfwdFile(anyString()))
															.thenReturn(efwdFile);
													mockedStatic2
															.when(() -> EfwdDatasourceUtils.newGetJsonOfEfwd(efwdFile))
															.thenReturn(new JsonObject());
													mockedStatic2.when(() -> EfwdDatasourceUtils
															.getEfwdConnection(any(), anyString(), anyString()))
															.thenReturn(jsonObject);
													mockedStatic3.when(() -> JaxbUtils.unMarshal(any(), any()))
															.thenReturn(metadata);
													mockedStatic4.when(() -> JsonUtils.functionsReference(anyString()))
															.thenReturn("reference");
													mockedStatic5.when(() -> AdhocUtils.getUuid()).thenReturn("dbId");
													mockedStatic6
															.when(() -> ApplicationContextAccessor
																	.getBean(MetadataDataSourceChangeHandler.class))
															.thenReturn(changeHandler);
													mockedStatic6
															.when(() -> ApplicationContextAccessor
																	.getBean(ViewsUpdateHandler.class))
															.thenReturn(handler);
													mockedStatic9.when(
															() -> DbMetadataUtils.checkMetadataBeforeSave(metadata))
															.thenReturn(false);

													databaseMetadataUpdateHandler.executeComponent(formJson.toString());

												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

	}

	@Test
	public void ut_b5_test_executeComponent() {
		DatabaseMetadataUpdateHandler databaseMetadataUpdateHandler = new DatabaseMetadataUpdateHandler();
		JsonObject formJson = new JsonObject();
		JsonObject dataSource = new JsonObject();
		dataSource.addProperty("id", "12");
		dataSource.addProperty("type", "type");
		dataSource.addProperty("dir", "dir");
		dataSource.addProperty("changed", "true");

		formJson.add("dataSource", dataSource);
		formJson.addProperty("fileName", "fileName");

		formJson.addProperty("location", "location");
		formJson.addProperty("uniqueId", "uniqueId");
		JsonObject tables = new JsonObject();
		formJson.add("tables", tables);
		JsonObject columns = new JsonObject();
		formJson.add("columns", columns);
		JsonArray views = new JsonArray();
		views.add("");
		formJson.add("views", views);
		JsonArray joins = new JsonArray();
		formJson.add("joins", joins);

		formJson.addProperty("database", "database");
		formJson.addProperty("catalog", "catalog");
		formJson.addProperty("schema", "schema");
		JsonObject access = new JsonObject();
		JsonArray expression = new JsonArray();
		expression.add("");
		access.add("expression", expression);
		formJson.add("access", access);
		JsonObject duplicate = new JsonObject();
		JsonArray table = new JsonArray();
		JsonArray column = new JsonArray();
		duplicate.add("table", table);
		duplicate.add("column", column);
		formJson.add("duplicate", duplicate);

		File efwdFile = mock(File.class);
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("databaseDialect", "[]");
		jsonObject.addProperty("Driver", "databaseDialect");
		Metadata metadata = mock(Metadata.class);
		Database database = mock(Database.class);
		ConnectionDetails connectionDetails = mock(ConnectionDetails.class);
		DriverClass driverClass = mock(DriverClass.class);
		MetadataDataSourceChangeHandler changeHandler = mock(MetadataDataSourceChangeHandler.class);
		ViewsUpdateHandler handler = mock(ViewsUpdateHandler.class);

		when(metadata.getConnectionDetails()).thenReturn(connectionDetails);
		when(connectionDetails.getDriverClass()).thenReturn(driverClass);

		when(metadata.getDatabase()).thenReturn(database);
		when(database.getId()).thenReturn("12");
		JsonObject globalConnectionJSONObject = new JsonObject();
		globalConnectionJSONObject.addProperty("driverClassName", "driverClassName");

		try (MockedStatic<GlobalJdbcTypeUtils> mockedStatic = mockStatic(GlobalJdbcTypeUtils.class)) {
			try (MockedStatic<ApplicationUtilities> mockedStatic1 = mockStatic(ApplicationUtilities.class)) {
				try (MockedStatic<EfwdDatasourceUtils> mockedStatic2 = mockStatic(EfwdDatasourceUtils.class)) {
					try (MockedStatic<JaxbUtils> mockedStatic3 = mockStatic(JaxbUtils.class)) {
						try (MockedStatic<JsonUtils> mockedStatic4 = mockStatic(JsonUtils.class)) {
							try (MockedStatic<AdhocUtils> mockedStatic5 = mockStatic(AdhocUtils.class)) {
								try (MockedStatic<ApplicationContextAccessor> mockedStatic6 = mockStatic(
										ApplicationContextAccessor.class)) {
									try (MockedStatic<AddRemoveTableColumnsHandler> mockedStatic7 = mockStatic(
											AddRemoveTableColumnsHandler.class)) {
										try (MockedStatic<DuplicateItemHandler> mockedStatic8 = mockStatic(
												DuplicateItemHandler.class)) {
											try (MockedStatic<DbMetadataUtils> mockedStatic9 = mockStatic(
													DbMetadataUtils.class)) {
												try (MockedStatic<MetadataSecurityUpdateHandler> mockedStatic10 = mockStatic(
														MetadataSecurityUpdateHandler.class)) {
													try (MockedStatic<DataSourceUtils> mockedStatic11 = mockStatic(
															DataSourceUtils.class)) {

														mockedStatic.when(
																() -> GlobalJdbcTypeUtils.isTypeGlobal(anyString()))
																.thenReturn(true);
														mockedStatic1.when(
																() -> ApplicationUtilities.getEfwdFile(anyString()))
																.thenReturn(efwdFile);
														mockedStatic2.when(
																() -> EfwdDatasourceUtils.newGetJsonOfEfwd(efwdFile))
																.thenReturn(new JsonObject());
														mockedStatic2
																.when(() -> EfwdDatasourceUtils.getEfwdConnection(any(),
																		anyString(), anyString()))
																.thenReturn(jsonObject);
														mockedStatic3.when(() -> JaxbUtils.unMarshal(any(), any()))
																.thenReturn(metadata);
														mockedStatic4
																.when(() -> JsonUtils.functionsReference(anyString()))
																.thenReturn("reference");
														mockedStatic5.when(() -> AdhocUtils.getUuid())
																.thenReturn("dbId");
														mockedStatic6
																.when(() -> ApplicationContextAccessor
																		.getBean(MetadataDataSourceChangeHandler.class))
																.thenReturn(changeHandler);
														mockedStatic6
																.when(() -> ApplicationContextAccessor
																		.getBean(ViewsUpdateHandler.class))
																.thenReturn(handler);
														mockedStatic9.when(
																() -> DbMetadataUtils.checkMetadataBeforeSave(metadata))
																.thenReturn(false);
														mockedStatic11
																.when(() -> DataSourceUtils.globalIdJson(anyInt()))
																.thenReturn(globalConnectionJSONObject.toString());
														databaseMetadataUpdateHandler
																.executeComponent(formJson.toString());
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

	}

	@Test
	public void ut_b6_test_prepareReferenceForGlobal() throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		DatabaseMetadataUpdateHandler databaseMetadataUpdateHandler = new DatabaseMetadataUpdateHandler();
		Method method = DatabaseMetadataUpdateHandler.class.getDeclaredMethod("prepareReferenceForGlobal",
				JsonObject.class, String.class);
		method.setAccessible(true);
		JsonObject formJson = new JsonObject();
		formJson.addProperty("databaseDialect", "databaseDialect");
		JsonObject globalConnectionJSONObject = new JsonObject();
		globalConnectionJSONObject.addProperty("driverName", "driverName");
		try (MockedStatic<DataSourceUtils> mockedStatic = mockStatic(DataSourceUtils.class)) {
			mockedStatic.when(() -> DataSourceUtils.globalIdJson(anyInt()))
					.thenReturn(globalConnectionJSONObject.toString());
			method.invoke(databaseMetadataUpdateHandler, formJson, "12");

		}
	}

	@Test
	public void ut_b7_test_prepareReferenceForGlobal() throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		DatabaseMetadataUpdateHandler databaseMetadataUpdateHandler = new DatabaseMetadataUpdateHandler();
		Method method = DatabaseMetadataUpdateHandler.class.getDeclaredMethod("prepareReferenceForGlobal",
				JsonObject.class, String.class);
		method.setAccessible(true);
		JsonObject formJson = new JsonObject();
		JsonObject globalConnectionJSONObject = new JsonObject();
		globalConnectionJSONObject.addProperty("driverName", "driverName");
		globalConnectionJSONObject.addProperty("databaseDialect", "databaseDialect");

		try (MockedStatic<DataSourceUtils> mockedStatic = mockStatic(DataSourceUtils.class)) {
			mockedStatic.when(() -> DataSourceUtils.globalIdJson(anyInt()))
					.thenReturn(globalConnectionJSONObject.toString());
			method.invoke(databaseMetadataUpdateHandler, formJson, "12");

		}
	}

	@Test
	public void ut_b8_test_saveXml() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		DatabaseMetadataUpdateHandler databaseMetadataUpdateHandler = new DatabaseMetadataUpdateHandler();
		Method method = DatabaseMetadataUpdateHandler.class.getDeclaredMethod("saveXml", String.class, String.class,
				String.class, String.class, String.class, Metadata.class, String.class);
		method.setAccessible(true);
		Metadata metadata = mock(Metadata.class);
		try (MockedStatic<JaxbUtils> mockedStatic = mockStatic(JaxbUtils.class)) {
			method.invoke(databaseMetadataUpdateHandler, "fileName","location","_temp_", "extension", "solutionDirectory", metadata, null);
		}
		
	}
	
	@Test
	public void ut_b9_test_updateMetadata() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		DatabaseMetadataUpdateHandler databaseMetadataUpdateHandler = new DatabaseMetadataUpdateHandler();
		Method method = DatabaseMetadataUpdateHandler.class.getDeclaredMethod("updateMetadata", JsonArray.class, Map.class,
				Map.class,  Metadata.class);
		method.setAccessible(true);
		Metadata metadata = mock(Metadata.class);
		JsonArray json = new JsonArray();
		Map<String,String> requestTableMap = new  HashMap<>();
		Map<String,String> requestColumnMap = new  HashMap<>();
		Database database = mock(Database.class);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		when(metadata.getDatabase()).thenReturn(database);
		when(database.getName()).thenReturn("naninnamariyalare");
		when(database.getTables()).thenReturn(tables);
		List<Table> tableList = new ArrayList<>();
		tableList.add(table);
		when(tables.getTableList()).thenReturn(tableList);
		method.invoke(databaseMetadataUpdateHandler, json, requestTableMap,requestColumnMap, metadata);
		
		
	}
	
	@Test
	public void ut_c1_test_updateMetadata() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		DatabaseMetadataUpdateHandler databaseMetadataUpdateHandler = new DatabaseMetadataUpdateHandler();
		Method method = DatabaseMetadataUpdateHandler.class.getDeclaredMethod("updateMetadata", JsonArray.class, Map.class,
				Map.class,  Metadata.class);
		method.setAccessible(true);
		Metadata metadata = mock(Metadata.class);
		JsonArray json = new JsonArray();
		Map<String,String> requestTableMap = new  HashMap<>();
		requestTableMap.put("db.Table", "tableName");
		Map<String,String> requestColumnMap = new  HashMap<>();
		requestColumnMap.put("Table.Column", "columnName");
		
		Database database = mock(Database.class);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		Columns columns = mock(Columns.class);
		Column column = mock(Column.class);
		
		when(metadata.getDatabase()).thenReturn(database);
		when(database.getName()).thenReturn("db");
		when(database.getTables()).thenReturn(tables);
		List<Table> tableList = new ArrayList<>();
		tableList.add(table);
		when(table.getName()).thenReturn("Table");
		when(table.getColumns()).thenReturn(columns);
		List<Column> columnList = new ArrayList<>();
		columnList.add(column);
		columnList.add(column);
		when(column.getName()).thenReturn("cc").thenReturn("column");
		when(columns.getColumn()).thenReturn(columnList);
		when(tables.getTableList()).thenReturn(tableList);
		method.invoke(databaseMetadataUpdateHandler, json, requestTableMap,requestColumnMap, metadata);
		
		
	}
	
	@Test
	public void ut_c2_test_updateMetadata() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		DatabaseMetadataUpdateHandler databaseMetadataUpdateHandler = new DatabaseMetadataUpdateHandler();
		Method method = DatabaseMetadataUpdateHandler.class.getDeclaredMethod("updateMetadata", JsonArray.class, Map.class,
				Map.class,  Metadata.class);
		method.setAccessible(true);
		Metadata metadata = mock(Metadata.class);
		JsonArray json = new JsonArray();
		Map<String,String> requestTableMap = new  HashMap<>();
		requestTableMap.put("db.Table", "tableName");
		Map<String,String> requestColumnMap = new  HashMap<>();
		requestColumnMap.put("TTravelDetails.Column", "columnName");
		
		Database database = mock(Database.class);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		Columns columns = mock(Columns.class);
		Column column = mock(Column.class);
		
		when(metadata.getDatabase()).thenReturn(database);
		when(database.getName()).thenReturn("");
		when(database.getTables()).thenReturn(tables);
		List<Table> tableList = new ArrayList<>();
		tableList.add(table);
		when(table.getName()).thenReturn("TravelDetails");
		when(table.getColumns()).thenReturn(columns);
		List<Column> columnList = new ArrayList<>();
		columnList.add(column);
		columnList.add(column);
		when(column.getName()).thenReturn("cc").thenReturn("column");
		when(columns.getColumn()).thenReturn(columnList);
		when(tables.getTableList()).thenReturn(tableList);
		method.invoke(databaseMetadataUpdateHandler, json, requestTableMap,requestColumnMap, metadata);
		
		
	}

	@Test
	public void ut_c3_test_updateMetadata() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		DatabaseMetadataUpdateHandler databaseMetadataUpdateHandler = new DatabaseMetadataUpdateHandler();
		Method method = DatabaseMetadataUpdateHandler.class.getDeclaredMethod("updateMetadata", JsonArray.class, Map.class,
				Map.class,  Metadata.class);
		method.setAccessible(true);
		Metadata metadata = mock(Metadata.class);
		JsonArray json = new JsonArray();
		Map<String,String> requestTableMap = new  HashMap<>();
		requestTableMap.put("TravelDetails", "tableName");
		Map<String,String> requestColumnMap = new  HashMap<>();
		requestColumnMap.put("TTravelDetails.Column", "columnName");
		
		Database database = mock(Database.class);
		Tables tables = mock(Tables.class);
		Table table = mock(Table.class);
		Columns columns = mock(Columns.class);
		Column column = mock(Column.class);
		
		when(metadata.getDatabase()).thenReturn(database);
		when(database.getName()).thenReturn("");
		when(database.getTables()).thenReturn(tables);
		List<Table> tableList = new ArrayList<>();
		tableList.add(table);
		when(table.getName()).thenReturn("TravelDetails");
		when(table.getColumns()).thenReturn(columns);
		List<Column> columnList = new ArrayList<>();
		columnList.add(column);
		columnList.add(column);
		when(column.getName()).thenReturn("cc").thenReturn("column");
		when(columns.getColumn()).thenReturn(columnList);
		when(tables.getTableList()).thenReturn(tableList);
		method.invoke(databaseMetadataUpdateHandler, json, requestTableMap,requestColumnMap, metadata);
		
		
	}

	@Test(expected = InvocationTargetException.class)
	public void ut_c4_test_validate() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		DatabaseMetadataUpdateHandler databaseMetadataUpdateHandler = new DatabaseMetadataUpdateHandler();
		Method method = DatabaseMetadataUpdateHandler.class.getDeclaredMethod("validate",  Map.class,
				Map.class,  Metadata.class);
		method.setAccessible(true);
		Metadata metadata = mock(Metadata.class);
		Map<String,String> requestTableMap = new  HashMap<>();
		requestTableMap.put("TravelDetails", "tableName");
		Map<String,String> requestColumnMap = new  HashMap<>();
		requestColumnMap.put("TTravelDetails.Column", "columnName");
		
		try(MockedConstruction<ValidationUtility> construction = mockConstruction(ValidationUtility.class,(mock,context)->{
			when(mock.getTablesMap()).thenReturn(requestColumnMap);
		})){
			method.invoke(databaseMetadataUpdateHandler, requestTableMap,requestColumnMap, metadata);
			
		}
	}
	
	
	@Test(expected = InvocationTargetException.class)
	public void ut_c5_test_validate() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		DatabaseMetadataUpdateHandler databaseMetadataUpdateHandler = new DatabaseMetadataUpdateHandler();
		Method method = DatabaseMetadataUpdateHandler.class.getDeclaredMethod("validate",  Map.class,
				Map.class,  Metadata.class);
		method.setAccessible(true);
		Metadata metadata = mock(Metadata.class);
		Map<String,String> requestTableMap = new  HashMap<>();
		requestTableMap.put("TravelDetails", "tableName");
		Map<String,String> requestColumnMap = new  HashMap<>();
		requestColumnMap.put("TTravelDetails.Column", "columnName");
		
		try(MockedConstruction<ValidationUtility> construction = mockConstruction(ValidationUtility.class,(mock,context)->{
			when(mock.getTablesMap()).thenReturn(requestTableMap);
		})){
			method.invoke(databaseMetadataUpdateHandler, requestTableMap,requestColumnMap, metadata);
			
		}
	}
	
	@Test(expected = InvocationTargetException.class)
	public void ut_c6_test_validate() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		DatabaseMetadataUpdateHandler databaseMetadataUpdateHandler = new DatabaseMetadataUpdateHandler();
		Method method = DatabaseMetadataUpdateHandler.class.getDeclaredMethod("validate",  Map.class,
				Map.class,  Metadata.class);
		method.setAccessible(true);
		Metadata metadata = mock(Metadata.class);
		Map<String,String> requestTableMap = new  HashMap<>();
		requestTableMap.put("TravelDetails", "tableName");
		Map<String,String> requestColumnMap = new  HashMap<>();
		requestColumnMap.put("TTravelDetails.Column", "columnName");
		
		try(MockedConstruction<ValidationUtility> construction = mockConstruction(ValidationUtility.class,(mock,context)->{
			when(mock.getTablesMap()).thenReturn(requestTableMap);
			when(mock.getColumnsMap()).thenReturn(requestTableMap);
		})){
			method.invoke(databaseMetadataUpdateHandler, requestTableMap,requestTableMap, metadata);
			
		}
	}
	
	@Test(expected = InvocationTargetException.class)
	public void ut_c7_test_validate() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		DatabaseMetadataUpdateHandler databaseMetadataUpdateHandler = new DatabaseMetadataUpdateHandler();
		Method method = DatabaseMetadataUpdateHandler.class.getDeclaredMethod("validate",  Map.class,
				Map.class,  Metadata.class);
		method.setAccessible(true);
		Metadata metadata = mock(Metadata.class);
		Map<String,String> requestTableMap = new  HashMap<>();
		requestTableMap.put("TravelDetails", "tableName");
		Map<String,String> requestColumnMap = new  HashMap<>();
		requestColumnMap.put("TravelDetails", "columnName");
		
		try(MockedConstruction<ValidationUtility> construction = mockConstruction(ValidationUtility.class,(mock,context)->{
			when(mock.getTablesMap()).thenReturn(requestColumnMap);
			when(mock.getColumnsMap()).thenReturn(requestColumnMap);
		})){
			method.invoke(databaseMetadataUpdateHandler, requestTableMap,requestColumnMap, metadata);
			
		}
	}
	
	@Test
	public void ut_c8_test_prepareMapFromJsonObject() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		DatabaseMetadataUpdateHandler databaseMetadataUpdateHandler = new DatabaseMetadataUpdateHandler();
		Method method = DatabaseMetadataUpdateHandler.class.getDeclaredMethod("prepareMapFromJsonObject",  JsonObject.class);
		method.setAccessible(true);
		JsonObject json = null;
		method.invoke(databaseMetadataUpdateHandler, json);
		
	}
	
	@Test
	public void ut_c9_test_prepareMapFromJsonObject() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		DatabaseMetadataUpdateHandler databaseMetadataUpdateHandler = new DatabaseMetadataUpdateHandler();
		Method method = DatabaseMetadataUpdateHandler.class.getDeclaredMethod("prepareMapFromJsonObject",  JsonObject.class);
		method.setAccessible(true);
		JsonObject json = new JsonObject();
		json.addProperty("key", "value");
		json.addProperty("key1", "value1");
		
		method.invoke(databaseMetadataUpdateHandler, json);
		
	}
}
