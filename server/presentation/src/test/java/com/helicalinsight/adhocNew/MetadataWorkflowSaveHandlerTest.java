package com.helicalinsight.adhocNew;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.MockedStatic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.AddRemoveTableColumnsHandler;
import com.helicalinsight.adhoc.MetadataWorkflowSaveHandler;
import com.helicalinsight.adhoc.metadata.genericdb.IMetadata;
import com.helicalinsight.adhoc.metadata.genericdb.MetadataServiceException;
import com.helicalinsight.adhoc.metadata.jaxb.Database;
import com.helicalinsight.adhoc.metadata.jaxb.Join;
import com.helicalinsight.adhoc.metadata.jaxb.LeftTable;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.metadata.jaxb.Relationship;
import com.helicalinsight.adhoc.metadata.jaxb.Relationships;
import com.helicalinsight.adhoc.metadata.jaxb.RightTable;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.MalformedJsonException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MetadataWorkflowSaveHandlerTest {

	@Test(expected = MalformedJsonException.class)
	public void ut_a1_test_executeComponent() {
		MetadataWorkflowSaveHandler handler = new MetadataWorkflowSaveHandler();
		JsonObject formData = new JsonObject();
		JsonObject metadata = new JsonObject();
		formData.add("metadata", metadata);

		String jsonFormData = formData.toString();
		handler.executeComponent(jsonFormData);
	}

	@Test(expected = EfwServiceException.class)
	public void ut_a2_test_executeComponent() {
		MetadataWorkflowSaveHandler handler = new MetadataWorkflowSaveHandler();
		JsonObject formData = new JsonObject();
		JsonObject metadata = new JsonObject();
		JsonArray catalogs = new JsonArray();
		metadata.add("catalogs", catalogs);
		metadata.addProperty("metadata", "value");
		formData.add("metadata", metadata);

		String jsonFormData = formData.toString();
		handler.executeComponent(jsonFormData);
	}

	@Test
	public void ut_a3_test_executeComponent() {
		MetadataWorkflowSaveHandler handler = new MetadataWorkflowSaveHandler();
		JsonObject formData = new JsonObject();
		JsonObject metadata = new JsonObject();
		JsonArray catalogs = new JsonArray();
		metadata.add("catalogs", catalogs);
		metadata.addProperty("metadata", "value");
		formData.add("metadata", metadata);
		formData.addProperty("uniqueId", "uniqueId");

		String jsonFormData = formData.toString();

		JsonObject json = new JsonObject();
		JsonObject updatedMdMd = new JsonObject();
		json.add("metadata", updatedMdMd);
		String response = json.toString();
		try (MockedStatic<AddRemoveTableColumnsHandler> mockedStatic = mockStatic(AddRemoveTableColumnsHandler.class)) {
			mockedStatic.when(() -> AddRemoveTableColumnsHandler.handleAddRemove(any(JsonObject.class)))
					.thenReturn(response);
			handler.executeComponent(jsonFormData);
		}

	}

	@Test
	public void ut_a4_test_executeComponent() {
		MetadataWorkflowSaveHandler handler = new MetadataWorkflowSaveHandler();
		JsonObject formData = new JsonObject();
		JsonObject metadata = new JsonObject();
		JsonArray catalogs = new JsonArray();
		// metadata.add("catalogs", catalogs);
		// metadata.addProperty("metadata", "value");
		formData.add("metadata", metadata);
		formData.addProperty("uniqueId", "uniqueId");

		String jsonFormData = formData.toString();

		JsonObject json = new JsonObject();
		JsonObject updatedMdMd = new JsonObject();
		json.add("metadata", updatedMdMd);
		String response = json.toString();
		try (MockedStatic<AddRemoveTableColumnsHandler> mockedStatic = mockStatic(AddRemoveTableColumnsHandler.class)) {
			mockedStatic.when(() -> AddRemoveTableColumnsHandler.handleAddRemove(any(JsonObject.class)))
					.thenReturn(response);
			handler.executeComponent(jsonFormData);
		}

	}

	@Test
	public void ut_a5_test_executeComponent() {
		MetadataWorkflowSaveHandler handler = new MetadataWorkflowSaveHandler();
		JsonObject formData = new JsonObject();
		JsonObject metadata = new JsonObject();
		JsonArray catalogs = new JsonArray();
		JsonArray schemas = new JsonArray();
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("schemas", schemas);
		catalogs.add(jsonObject);
		metadata.add("catalogs", catalogs);
		metadata.addProperty("metadata", "value");
		formData.add("metadata", metadata);
		formData.addProperty("uniqueId", "uniqueId");

		String jsonFormData = formData.toString();

		JsonObject json = new JsonObject();
		JsonObject updatedMdMd = new JsonObject();
		json.add("metadata", updatedMdMd);
		String response = json.toString();
		try (MockedStatic<AddRemoveTableColumnsHandler> mockedStatic = mockStatic(AddRemoveTableColumnsHandler.class)) {
			mockedStatic.when(() -> AddRemoveTableColumnsHandler.handleAddRemove(any(JsonObject.class)))
					.thenReturn(response);
			handler.executeComponent(jsonFormData);
		}

	}

	@Test(expected = MalformedJsonException.class)
	public void ut_a6_test_executeComponent() {
		MetadataWorkflowSaveHandler handler = new MetadataWorkflowSaveHandler();
		JsonObject formData = new JsonObject();
		JsonObject metadata = new JsonObject();
		JsonArray catalogs = new JsonArray();
		JsonArray schemas = new JsonArray();
		schemas.add("");
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("schemas", schemas);
		catalogs.add(jsonObject);
		metadata.add("catalogs", catalogs);
		metadata.addProperty("metadata", "value");
		formData.add("metadata", metadata);
		formData.addProperty("uniqueId", "uniqueId");

		String jsonFormData = formData.toString();

		JsonObject json = new JsonObject();
		JsonObject updatedMdMd = new JsonObject();
		json.add("metadata", updatedMdMd);
		String response = json.toString();
		try (MockedStatic<AddRemoveTableColumnsHandler> mockedStatic = mockStatic(AddRemoveTableColumnsHandler.class)) {
			mockedStatic.when(() -> AddRemoveTableColumnsHandler.handleAddRemove(any(JsonObject.class)))
					.thenThrow(new MetadataServiceException(""));
			handler.executeComponent(jsonFormData);
		}

	}

	@Test(expected = ConfigurationException.class)
	public void ut_a7_test_executeComponent() {
		MetadataWorkflowSaveHandler handler = new MetadataWorkflowSaveHandler();
		JsonObject formData = new JsonObject();
		JsonObject metadata = new JsonObject();
		JsonArray catalogs = new JsonArray();
		JsonArray schemas = new JsonArray();
		schemas.add("");
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("schemas", schemas);
		catalogs.add(jsonObject);
		metadata.add("catalogs", catalogs);
		metadata.addProperty("metadata", "value");
		formData.add("metadata", metadata);
		formData.addProperty("uniqueId", "uniqueId");
		formData.addProperty("metadataImplementation", "metadataImplementation");
		String jsonFormData = formData.toString();

		JsonObject json = new JsonObject();
		JsonObject updatedMdMd = new JsonObject();
		JsonArray joins = new JsonArray();
		updatedMdMd.add("joins", joins);
		json.add("metadata", updatedMdMd);
		String response = json.toString();

		try (MockedStatic<AddRemoveTableColumnsHandler> mockedStatic = mockStatic(AddRemoveTableColumnsHandler.class)) {
			try (MockedStatic<FactoryMethodWrapper> mockedStatic2 = mockStatic(FactoryMethodWrapper.class)) {
				mockedStatic2
						.when(() -> FactoryMethodWrapper.getTypedInstance("metadataImplementation", IMetadata.class))
						.thenReturn(null);
				mockedStatic.when(() -> AddRemoveTableColumnsHandler.handleAddRemove(any(JsonObject.class)))
						.thenReturn(response);
				handler.executeComponent(jsonFormData);
			}
		}
	}

	@Test
	public void ut_a8_test_executeComponent() {
		MetadataWorkflowSaveHandler handler = new MetadataWorkflowSaveHandler();
		JsonObject formData = new JsonObject();
		JsonObject metadata = new JsonObject();
		JsonArray catalogs = new JsonArray();
		JsonArray schemas = new JsonArray();
		schemas.add("");
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("schemas", schemas);
		catalogs.add(jsonObject);
		metadata.add("catalogs", catalogs);
		metadata.addProperty("metadata", "value");
		formData.add("metadata", metadata);
		formData.addProperty("uniqueId", "uniqueId");
		formData.addProperty("metadataImplementation", "metadataImplementation");
		String jsonFormData = formData.toString();

		JsonObject json = new JsonObject();
		JsonObject updatedMdMd = new JsonObject();
		JsonArray joins = new JsonArray();
		updatedMdMd.add("joins", joins);
		json.add("metadata", updatedMdMd);
		String response = json.toString();

		IMetadata iMetadata = mock(IMetadata.class);
		JsonObject meta = new JsonObject();
		JsonObject metadataJSON = new JsonObject();
		JsonArray newJoins = new JsonArray();
		metadataJSON.add("joins", newJoins);
		meta.add("metadata", metadataJSON);
		String metaString = meta.toString();
		when(iMetadata.getMetadata(anyString())).thenReturn(metaString);
		try (MockedStatic<AddRemoveTableColumnsHandler> mockedStatic = mockStatic(AddRemoveTableColumnsHandler.class)) {
			try (MockedStatic<FactoryMethodWrapper> mockedStatic2 = mockStatic(FactoryMethodWrapper.class)) {
				mockedStatic2
						.when(() -> FactoryMethodWrapper.getTypedInstance("metadataImplementation", IMetadata.class))
						.thenReturn(iMetadata);
				mockedStatic.when(() -> AddRemoveTableColumnsHandler.handleAddRemove(any(JsonObject.class)))
						.thenReturn(response);
				handler.executeComponent(jsonFormData);
			}
		}
	}

	@Test
	public void ut_a9_test_executeComponent() {
		MetadataWorkflowSaveHandler handler = new MetadataWorkflowSaveHandler();
		JsonObject formData = new JsonObject();
		JsonObject metadata = new JsonObject();
		JsonArray catalogs = new JsonArray();
		JsonArray schemas = new JsonArray();
		schemas.add("");
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("schemas", schemas);
		catalogs.add(jsonObject);
		metadata.add("catalogs", catalogs);
		metadata.addProperty("metadata", "value");
		formData.add("metadata", metadata);
		formData.addProperty("uniqueId", "uniqueId");
		formData.addProperty("metadataImplementation", "metadataImplementation");
		String jsonFormData = formData.toString();

		JsonObject json = new JsonObject();
		JsonObject updatedMdMd = new JsonObject();
		JsonArray oldJoins = new JsonArray();
		updatedMdMd.add("joins", oldJoins);
		json.add("metadata", updatedMdMd);
		String response = json.toString();

		IMetadata iMetadata = mock(IMetadata.class);
		JsonObject meta = new JsonObject();
		JsonObject metadataJSON = new JsonObject();
		JsonArray newJoins = new JsonArray();
		JsonObject itemRef = new JsonObject();
		JsonObject left = new JsonObject();
		left.addProperty("column", "lColumn");
		left.addProperty("table", "lTable");
		JsonObject right = new JsonObject();
		right.addProperty("column", "rColumn");
		right.addProperty("table", "rTable");
		itemRef.add("left", left);
		itemRef.add("right", right);
		itemRef.addProperty("type", "type");
		itemRef.addProperty("operator", "operator");
		newJoins.add(itemRef);
		metadataJSON.add("joins", newJoins);
		meta.add("metadata", metadataJSON);
		String metaString = meta.toString();
		when(iMetadata.getMetadata(anyString())).thenReturn(metaString);
		File file = new File(TempDirectoryCleaner.getTempDirectory() + File.separator+"uniqueId.metadata");
		Metadata metadataJaxb = mock(Metadata.class);
		Database database = mock(Database.class);
		Relationships relationships = mock(Relationships.class);
		when(database.getRelationships()).thenReturn(relationships);
		when(metadataJaxb.getDatabase()).thenReturn(database);
		Join newJoin = mock(Join.class);
		LeftTable leftTable = mock(LeftTable.class);
		RightTable rightTable = mock(RightTable.class);
		when(newJoin.getLeftTable()).thenReturn(leftTable);
		when(newJoin.getRightTable()).thenReturn(rightTable);
		when(newJoin.getOperator()).thenReturn("operator");
		Relationship relationship = mock(Relationship.class);
		when(relationship.getJoin()).thenReturn(null);
		try (MockedStatic<AddRemoveTableColumnsHandler> mockedStatic = mockStatic(AddRemoveTableColumnsHandler.class)) {
			try (MockedStatic<FactoryMethodWrapper> mockedStatic2 = mockStatic(FactoryMethodWrapper.class)) {
				try (MockedStatic<JaxbUtils> mockedStatic3 = mockStatic(JaxbUtils.class)) {
					try (MockedStatic<ApplicationContextAccessor> mockedStatic4 = mockStatic(
							ApplicationContextAccessor.class)) {
						mockedStatic4.when(() -> ApplicationContextAccessor.getBean(Join.class)).thenReturn(newJoin);
						mockedStatic4.when(() -> ApplicationContextAccessor.getBean(LeftTable.class))
								.thenReturn(leftTable);
						mockedStatic4.when(() -> ApplicationContextAccessor.getBean(RightTable.class))
								.thenReturn(rightTable);
						mockedStatic4.when(()-> ApplicationContextAccessor.getBean(Relationship.class))
								.thenReturn(relationship);
						mockedStatic3.when(() -> JaxbUtils.unMarshal(Metadata.class, file)).thenReturn(metadataJaxb);

						mockedStatic2.when(
								() -> FactoryMethodWrapper.getTypedInstance("metadataImplementation", IMetadata.class))
								.thenReturn(iMetadata);
						mockedStatic.when(() -> AddRemoveTableColumnsHandler.handleAddRemove(any(JsonObject.class)))
								.thenReturn(response);
						handler.executeComponent(jsonFormData);
					}
				}
			}
		}
	}

	@Test
	public void ut_b1_test_executeComponent() {
		MetadataWorkflowSaveHandler handler = new MetadataWorkflowSaveHandler();
		JsonObject formData = new JsonObject();
		JsonObject metadata = new JsonObject();
		JsonArray catalogs = new JsonArray();
		JsonArray schemas = new JsonArray();
		schemas.add("");
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("schemas", schemas);
		catalogs.add(jsonObject);
		metadata.add("catalogs", catalogs);
		metadata.addProperty("metadata", "value");
		formData.add("metadata", metadata);
		formData.addProperty("uniqueId", "uniqueId");
		formData.addProperty("metadataImplementation", "metadataImplementation");
		String jsonFormData = formData.toString();

		JsonObject json = new JsonObject();
		JsonObject updatedMdMd = new JsonObject();
		JsonArray oldJoins = new JsonArray();
		updatedMdMd.add("joins", oldJoins);
		json.add("metadata", updatedMdMd);
		String response = json.toString();

		IMetadata iMetadata = mock(IMetadata.class);
		JsonObject meta = new JsonObject();
		JsonObject metadataJSON = new JsonObject();
		JsonArray newJoins = new JsonArray();
		JsonObject itemRef = new JsonObject();
		JsonObject left = new JsonObject();
		left.addProperty("column", "lColumn");
		left.addProperty("table", "lTable");
		JsonObject right = new JsonObject();
		right.addProperty("column", "rColumn");
		right.addProperty("table", "rTable");
		itemRef.add("left", left);
		itemRef.add("right", right);
		itemRef.addProperty("type", "type");
		itemRef.addProperty("operator", "operator");
		newJoins.add(itemRef);
		metadataJSON.add("joins", newJoins);
		meta.add("metadata", metadataJSON);
		String metaString = meta.toString();
		when(iMetadata.getMetadata(anyString())).thenReturn(metaString);
		File file = new File(TempDirectoryCleaner.getTempDirectory() + File.separator+"uniqueId.metadata");
		Metadata metadataJaxb = mock(Metadata.class);
		Database database = mock(Database.class);
		Relationships relationships = mock(Relationships.class);
		when(database.getRelationships()).thenReturn(null);
		when(relationships.getListOfRelations()).thenReturn(null);
		when(metadataJaxb.getDatabase()).thenReturn(database);
		Join newJoin = mock(Join.class);
		LeftTable leftTable = mock(LeftTable.class);
		RightTable rightTable = mock(RightTable.class);
		when(newJoin.getLeftTable()).thenReturn(leftTable);
		when(newJoin.getRightTable()).thenReturn(rightTable);
		when(newJoin.getOperator()).thenReturn("operator");
		Relationship relationship = mock(Relationship.class);
		when(relationship.getJoin()).thenReturn(null);
		try (MockedStatic<AddRemoveTableColumnsHandler> mockedStatic = mockStatic(AddRemoveTableColumnsHandler.class)) {
			try (MockedStatic<FactoryMethodWrapper> mockedStatic2 = mockStatic(FactoryMethodWrapper.class)) {
				try (MockedStatic<JaxbUtils> mockedStatic3 = mockStatic(JaxbUtils.class)) {
					try (MockedStatic<ApplicationContextAccessor> mockedStatic4 = mockStatic(
							ApplicationContextAccessor.class)) {
						mockedStatic4.when(() -> ApplicationContextAccessor.getBean(Join.class)).thenReturn(newJoin);
						mockedStatic4.when(() -> ApplicationContextAccessor.getBean(LeftTable.class))
								.thenReturn(leftTable);
						mockedStatic4.when(() -> ApplicationContextAccessor.getBean(RightTable.class))
								.thenReturn(rightTable);
						mockedStatic4.when(()-> ApplicationContextAccessor.getBean(Relationship.class))
								.thenReturn(relationship);
						mockedStatic4.when(()-> ApplicationContextAccessor.getBean(Relationships.class))
						.thenReturn(relationships);
				
						mockedStatic3.when(() -> JaxbUtils.unMarshal(Metadata.class, file)).thenReturn(metadataJaxb);

						mockedStatic2.when(
								() -> FactoryMethodWrapper.getTypedInstance("metadataImplementation", IMetadata.class))
								.thenReturn(iMetadata);
						mockedStatic.when(() -> AddRemoveTableColumnsHandler.handleAddRemove(any(JsonObject.class)))
								.thenReturn(response);
						handler.executeComponent(jsonFormData);
					}
				}
			}
		}
	}

	
	@Test
	public void ut_b2_test_executeComponent() {
		MetadataWorkflowSaveHandler handler = new MetadataWorkflowSaveHandler();
		JsonObject formData = new JsonObject();
		JsonObject metadata = new JsonObject();
		JsonArray catalogs = new JsonArray();
		JsonArray schemas = new JsonArray();
		schemas.add("");
		JsonObject jsonObject = new JsonObject();
		jsonObject.add("schemas", schemas);
		catalogs.add(jsonObject);
		metadata.add("catalogs", catalogs);
		metadata.addProperty("metadata", "value");
		formData.add("metadata", metadata);
		formData.addProperty("uniqueId", "uniqueId");
		formData.addProperty("metadataImplementation", "metadataImplementation");
		String jsonFormData = formData.toString();

		JsonObject json = new JsonObject();
		JsonObject updatedMdMd = new JsonObject();
		JsonArray oldJoins = new JsonArray();
		JsonObject item = new JsonObject();
		JsonObject Oleft = new JsonObject();
		Oleft.addProperty("column", "lColumn");
		Oleft.addProperty("table", "lTable");
		JsonObject Oright = new JsonObject();
		Oright.addProperty("column", "rColumn");
		Oright.addProperty("table", "rTable");
		item.add("left", Oleft);
		item.add("right", Oright);
		item.addProperty("type", "type");
		item.addProperty("operator", "operator");
		oldJoins.add(item);
		updatedMdMd.add("joins", oldJoins);
		json.add("metadata", updatedMdMd);
		String response = json.toString();

		IMetadata iMetadata = mock(IMetadata.class);
		JsonObject meta = new JsonObject();
		JsonObject metadataJSON = new JsonObject();
		JsonArray newJoins = new JsonArray();
		JsonObject itemRef = new JsonObject();
		JsonObject left = new JsonObject();
		left.addProperty("column", "lColumn");
		left.addProperty("table", "lTable");
		JsonObject right = new JsonObject();
		right.addProperty("column", "rColumn");
		right.addProperty("table", "rTable");
		itemRef.add("left", left);
		itemRef.add("right", right);
		itemRef.addProperty("type", "type");
		itemRef.addProperty("operator", "operator");
		newJoins.add(itemRef);
		metadataJSON.add("joins", newJoins);
		meta.add("metadata", metadataJSON);
		String metaString = meta.toString();
		when(iMetadata.getMetadata(anyString())).thenReturn(metaString);
		File file = new File(TempDirectoryCleaner.getTempDirectory() + File.separator+"uniqueId.metadata");
		Metadata metadataJaxb = mock(Metadata.class);
		Database database = mock(Database.class);
		Relationships relationships = mock(Relationships.class);
		when(database.getRelationships()).thenReturn(relationships);
		when(metadataJaxb.getDatabase()).thenReturn(database);
		Join newJoin = mock(Join.class);
		LeftTable leftTable = mock(LeftTable.class);
		RightTable rightTable = mock(RightTable.class);
		when(newJoin.getLeftTable()).thenReturn(leftTable);
		when(newJoin.getRightTable()).thenReturn(rightTable);
		when(newJoin.getOperator()).thenReturn("operator");
		Relationship relationship = mock(Relationship.class);
		when(relationship.getJoin()).thenReturn(null);
		try (MockedStatic<AddRemoveTableColumnsHandler> mockedStatic = mockStatic(AddRemoveTableColumnsHandler.class)) {
			try (MockedStatic<FactoryMethodWrapper> mockedStatic2 = mockStatic(FactoryMethodWrapper.class)) {
				try (MockedStatic<JaxbUtils> mockedStatic3 = mockStatic(JaxbUtils.class)) {
					try (MockedStatic<ApplicationContextAccessor> mockedStatic4 = mockStatic(
							ApplicationContextAccessor.class)) {
						mockedStatic4.when(() -> ApplicationContextAccessor.getBean(Join.class)).thenReturn(newJoin);
						mockedStatic4.when(() -> ApplicationContextAccessor.getBean(LeftTable.class))
								.thenReturn(leftTable);
						mockedStatic4.when(() -> ApplicationContextAccessor.getBean(RightTable.class))
								.thenReturn(rightTable);
						mockedStatic4.when(()-> ApplicationContextAccessor.getBean(Relationship.class))
								.thenReturn(relationship);
						mockedStatic3.when(() -> JaxbUtils.unMarshal(Metadata.class, file)).thenReturn(metadataJaxb);

						mockedStatic2.when(
								() -> FactoryMethodWrapper.getTypedInstance("metadataImplementation", IMetadata.class))
								.thenReturn(iMetadata);
						mockedStatic.when(() -> AddRemoveTableColumnsHandler.handleAddRemove(any(JsonObject.class)))
								.thenReturn(response);
						handler.executeComponent(jsonFormData);
					}
				}
			}
		}
	}

	@Test
	public void ut_b3_test_whenNewJoinsFound() {
		MetadataWorkflowSaveHandler handler = new MetadataWorkflowSaveHandler();
		JsonObject formData = new JsonObject();
		formData.addProperty("location", "location");
		formData.addProperty("metadataFileName", "metadataFileName");
		JsonArray newJoins= new JsonArray();
		JsonObject itemRef = new JsonObject();
		JsonObject left = new JsonObject();
		left.addProperty("column", "lColumn");
		left.addProperty("table", "");
		JsonObject right = new JsonObject();
		right.addProperty("column", "rColumn");
		right.addProperty("table", "");
		itemRef.add("left", left);
		itemRef.add("right", right);
		itemRef.addProperty("type", "type");
		itemRef.addProperty("operator", "operator");
		newJoins.add(itemRef);
		JsonArray oldJoins = new JsonArray();
		Metadata metadataJaxb = mock(Metadata.class);
		File file = new File( ApplicationProperties.INSTANCE.getSolutionDirectory()+ File.separator+"location"+ File.separator+"metadataFileName.metadata");
		Database database = mock(Database.class);
		Relationships relationships = mock(Relationships.class);
		when(database.getRelationships()).thenReturn(relationships);
		when(metadataJaxb.getDatabase()).thenReturn(database);
		Join newJoin = mock(Join.class);
		LeftTable leftTable = mock(LeftTable.class);
		RightTable rightTable = mock(RightTable.class);
		when(newJoin.getLeftTable()).thenReturn(leftTable);
		when(newJoin.getRightTable()).thenReturn(rightTable);
		when(newJoin.getOperator()).thenReturn("operator");
		Relationship relationship = mock(Relationship.class);
		
	    try (MockedStatic<JaxbUtils> mockedStatic3 = mockStatic(JaxbUtils.class)) {
	    	try (MockedStatic<ApplicationContextAccessor> mockedStatic4 = mockStatic(
					ApplicationContextAccessor.class)) {
				mockedStatic4.when(() -> ApplicationContextAccessor.getBean(Join.class)).thenReturn(newJoin);
				mockedStatic4.when(() -> ApplicationContextAccessor.getBean(LeftTable.class))
						.thenReturn(leftTable);
				mockedStatic4.when(() -> ApplicationContextAccessor.getBean(RightTable.class))
						.thenReturn(rightTable);
				mockedStatic4.when(()-> ApplicationContextAccessor.getBean(Relationship.class))
						.thenReturn(relationship);
				
	    	mockedStatic3.when(() -> JaxbUtils.unMarshal(Metadata.class, file)).thenReturn(metadataJaxb);
	    	 handler.whenNewJoinsFound(formData, newJoins, oldJoins);
	    	}
	    }
	}
	
	@Test
	public void ut_b4_test_whenNewJoinsFound() {
		MetadataWorkflowSaveHandler handler = new MetadataWorkflowSaveHandler();
		JsonObject formData = new JsonObject();
		JsonObject metadata = new JsonObject();
		metadata.addProperty("uniqueId", "_temp_");
		formData.add("metadata", metadata);
		formData.addProperty("metadataFileName", "metadataFileName");
		JsonArray newJoins= new JsonArray();
		JsonObject itemRef = new JsonObject();
		JsonObject left = new JsonObject();
		left.addProperty("column", "lColumn");
		left.addProperty("table", "");
		JsonObject right = new JsonObject();
		right.addProperty("column", "rColumn");
		right.addProperty("table", "");
		itemRef.add("left", left);
		itemRef.add("right", right);
		itemRef.addProperty("type", "type");
		itemRef.addProperty("operator", "operator");
		newJoins.add(itemRef);
		JsonArray oldJoins = new JsonArray();
		Metadata metadataJaxb = mock(Metadata.class);
		File file = new File(TempDirectoryCleaner.getTempDirectory() + File.separator+"_temp_.metadata");
		Database database = mock(Database.class);
		Relationships relationships = mock(Relationships.class);
		when(database.getRelationships()).thenReturn(relationships);
		when(metadataJaxb.getDatabase()).thenReturn(database);
		Join newJoin = mock(Join.class);
		LeftTable leftTable = mock(LeftTable.class);
		RightTable rightTable = mock(RightTable.class);
		when(newJoin.getLeftTable()).thenReturn(leftTable);
		when(newJoin.getRightTable()).thenReturn(rightTable);
		when(newJoin.getOperator()).thenReturn("operator");
		Relationship relationship = mock(Relationship.class);
		
	    try (MockedStatic<JaxbUtils> mockedStatic3 = mockStatic(JaxbUtils.class)) {
	    	try (MockedStatic<ApplicationContextAccessor> mockedStatic4 = mockStatic(
					ApplicationContextAccessor.class)) {
				mockedStatic4.when(() -> ApplicationContextAccessor.getBean(Join.class)).thenReturn(newJoin);
				mockedStatic4.when(() -> ApplicationContextAccessor.getBean(LeftTable.class))
						.thenReturn(leftTable);
				mockedStatic4.when(() -> ApplicationContextAccessor.getBean(RightTable.class))
						.thenReturn(rightTable);
				mockedStatic4.when(()-> ApplicationContextAccessor.getBean(Relationship.class))
						.thenReturn(relationship);
				
	    	mockedStatic3.when(() -> JaxbUtils.unMarshal(Metadata.class, file)).thenReturn(metadataJaxb);
	    	 handler.whenNewJoinsFound(formData, newJoins, oldJoins);
	    	}
	    }
	}

	@Test
	public void ut_b5_test_isThreadSafeToCache(){
		MetadataWorkflowSaveHandler handler = new MetadataWorkflowSaveHandler();
		boolean threadSafeToCache = handler.isThreadSafeToCache();
		assertTrue(threadSafeToCache);
	}

}
