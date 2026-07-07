package com.helicalinsight.adhoc.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helicalinsight.adhoc.MetadataCacheStatus;
import com.helicalinsight.adhoc.MetadataDriverReferences;
import com.helicalinsight.adhoc.MetadataProperties;
import com.helicalinsight.adhoc.metadata.jaxb.ConnectionDetails;
import com.helicalinsight.adhoc.metadata.jaxb.Database;
import com.helicalinsight.adhoc.metadata.jaxb.DriverClass;
import com.helicalinsight.admin.model.HIEFWD;
import com.helicalinsight.admin.model.HIEfwdConnection;
import com.helicalinsight.admin.model.HIMetadataColumns;
import com.helicalinsight.admin.model.HIMetadataConnectionEFWD;
import com.helicalinsight.admin.model.HIMetadataConnectionGlobal;
import com.helicalinsight.admin.model.HIMetadataConnections;
import com.helicalinsight.admin.model.HIMetadataCube;
import com.helicalinsight.admin.model.HIMetadataRelationships;
import com.helicalinsight.admin.model.HIMetadataSecurity;
import com.helicalinsight.admin.model.HIMetadataTables;
import com.helicalinsight.admin.model.HIMetadataView;
import com.helicalinsight.admin.model.HIPhase;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceMetadata;
import com.helicalinsight.admin.model.HIResourcePhaseStatus;
import com.helicalinsight.admin.model.MetadataDatabases;
import com.helicalinsight.datasource.GlobalJdbcTypeUtils;
import com.helicalinsight.datasource.GroovyManagedJdbcHandler;
import com.helicalinsight.datasource.model.GlobalConnections;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.resourcedb.MetadataDumpDTO;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HIMetadataResourceDBDAOImplTest {

	@Mock
	private SessionFactory sessionFactory;
	@Mock
	private Session session;
	@Mock
	private Query query;

	@InjectMocks
	private HIMetadataResourceDBDAOImpl hiMetadataResourceDBDAOImpl;

	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		when(sessionFactory.getCurrentSession()).thenReturn(session);

	}

	@Test
	public void ut_a1_test_addHIResourceMetadata() {
		HIResourceMetadata hiResourceMetadata = mock(HIResourceMetadata.class);
		when(session.save(hiResourceMetadata)).thenReturn(hiResourceMetadata);
		Integer addHIResourceMetadata = hiMetadataResourceDBDAOImpl.addHIResourceMetadata(hiResourceMetadata);
		Integer id = 1;
		assertEquals(id, addHIResourceMetadata);
	}

	@Test
	public void ut_a2_test_addHIResourceMetadata() {
		HIResourceMetadata hiResourceMetadata = mock(HIResourceMetadata.class);
		when(session.save(hiResourceMetadata)).thenThrow(new RuntimeException());
		Integer addHIResourceMetadata = hiMetadataResourceDBDAOImpl.addHIResourceMetadata(hiResourceMetadata);
		Integer id = 1;
		assertEquals(id, addHIResourceMetadata);
	}

	@Test
	public void ut_a3_test_editHIResourceMetadata() {
		HIResourceMetadata hiResourceMetadata = mock(HIResourceMetadata.class);
		doNothing().when(session).update(hiResourceMetadata);
		Integer editHIResourceMetadata = hiMetadataResourceDBDAOImpl.editHIResourceMetadata(hiResourceMetadata);
		Integer id = 1;
		assertEquals(id, editHIResourceMetadata);

	}

	@Test
	public void ut_a4_test_editHIResourceMetadata() {
		HIResourceMetadata hiResourceMetadata = mock(HIResourceMetadata.class);
		doThrow(new RuntimeException()).when(session).update(hiResourceMetadata);
		Integer editHIResourceMetadata = hiMetadataResourceDBDAOImpl.editHIResourceMetadata(hiResourceMetadata);
		Integer id = 1;
		assertEquals(id, editHIResourceMetadata);

	}

	@Test
	public void ut_a5_test_deleteHIResourceMetadata() {
		doNothing().when(session).delete(anyInt());
		hiMetadataResourceDBDAOImpl.deleteHIResourceMetadata(1);
	}

	@Test
	public void ut_a6_test_deleteHIResourceMetadata() {
		doThrow(new RuntimeException()).when(session).delete(anyInt());
		hiMetadataResourceDBDAOImpl.deleteHIResourceMetadata(1);
	}

	@Test
	public void ut_a7_test_giveHIResourceMetadataByResourceId() {
		HIResourceMetadata metadataPojo = new HIResourceMetadata();
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.uniqueResult()).thenReturn(metadataPojo);
		HIResourceMetadata giveHIResourceMetadataByResourceId = hiMetadataResourceDBDAOImpl
				.giveHIResourceMetadataByResourceId(1);
		assertEquals(metadataPojo, giveHIResourceMetadataByResourceId);
	}

	@Test
	public void ut_a8_test_giveHIResourceMetadataByResourceId() {
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.uniqueResult()).thenThrow(new RuntimeException());
		HIResourceMetadata giveHIResourceMetadataByResourceId = hiMetadataResourceDBDAOImpl.giveHIResourceMetadataByResourceId(1);
		assertEquals(null, giveHIResourceMetadataByResourceId);
	}

	@Test
	public void ut_a9_test_getHIMetadataDatabases() {
		MetadataDatabases databases = new MetadataDatabases();
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.uniqueResult()).thenReturn(databases);
		MetadataDatabases hiMetadataDatabases = hiMetadataResourceDBDAOImpl.getHIMetadataDatabases(1, "12");
		assertEquals(databases, hiMetadataDatabases);
	}

	@Test
	public void ut_b1_test_getHIMetadataDatabases() {
		MetadataDatabases databases = new MetadataDatabases();
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.uniqueResult()).thenThrow(new NullPointerException());
		MetadataDatabases hiMetadataDatabases = hiMetadataResourceDBDAOImpl.getHIMetadataDatabases(1, "12");
		assertEquals(null, hiMetadataDatabases);
	}

	@Test
	public void ut_b2_test_editHIMetadataDatabases() {
		MetadataDatabases metadataDatabase = new MetadataDatabases();
		doNothing().when(session).update(metadataDatabase);
		hiMetadataResourceDBDAOImpl.editHIMetadataDatabases(metadataDatabase);
	}

	@Test
	public void ut_b3_test_editHIMetadataDatabases() {
		MetadataDatabases metadataDatabase = mock(MetadataDatabases.class);
		doThrow(new RuntimeException()).when(session).update(metadataDatabase);
		hiMetadataResourceDBDAOImpl.editHIMetadataDatabases(metadataDatabase);
	}

	@Test
	public void ut_b4_test_editHIMetadataTables() {
		HIMetadataTables hiMetadataTables = new HIMetadataTables();
		doNothing().when(session).update(hiMetadataTables);
		hiMetadataResourceDBDAOImpl.editHIMetadataTables(hiMetadataTables);
	}

	@Test
	public void ut_b5_test_editHIMetadataTables() {
		HIMetadataTables hiMetadataTables = new HIMetadataTables();
		doThrow(new RuntimeException()).when(session).update(hiMetadataTables);
		hiMetadataResourceDBDAOImpl.editHIMetadataTables(hiMetadataTables);
	}

	@Test
	public void ut_b6_test_editHIMetadataColumns() {
		HIMetadataColumns hiMetadataColumns = new HIMetadataColumns();
		doNothing().when(session).update(hiMetadataColumns);
		hiMetadataResourceDBDAOImpl.editHIMetadataColumns(hiMetadataColumns);
	}

	@Test
	public void ut_b7_test_editHIMetadataColumns() {
		HIMetadataColumns hiMetadataColumns = new HIMetadataColumns();
		doThrow(new RuntimeException()).when(session).update(hiMetadataColumns);
		hiMetadataResourceDBDAOImpl.editHIMetadataColumns(hiMetadataColumns);
	}

	@Test
	public void ut_b8_test_getHIMetadataRelationships() {
		List<HIMetadataRelationships> list = new ArrayList<>();
		HIMetadataRelationships hiMetadataRelationships = new HIMetadataRelationships();
		list.add(hiMetadataRelationships);

		when(session.createQuery(anyString())).thenReturn(query);
		when(query.list()).thenReturn(list);
		List<HIMetadataRelationships> hiMetadataRelationships2 = hiMetadataResourceDBDAOImpl
				.getHIMetadataRelationships(1, 2);
		assertTrue(hiMetadataRelationships2.contains(hiMetadataRelationships));
	}

	@Test
	public void ut_b9_test_getHIMetadataRelationships() {
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.list()).thenThrow(new NullPointerException());
		List<HIMetadataRelationships> hiMetadataRelationships2 = hiMetadataResourceDBDAOImpl.getHIMetadataRelationships(1, 2);
		assertEquals(null, hiMetadataRelationships2);
	}

	@Test
	public void ut_c1_test_getHIMetadataExternalRelationships() {
		List<HIMetadataRelationships> list = new ArrayList<>();
		HIMetadataRelationships hiMetadataRelationships = new HIMetadataRelationships();
		list.add(hiMetadataRelationships);

		when(session.createQuery(anyString())).thenReturn(query);
		when(query.list()).thenReturn(list);
		List<HIMetadataRelationships> hiMetadataRelationships2 = hiMetadataResourceDBDAOImpl
				.getHIMetadataExternalRelationships(1);
		assertTrue(hiMetadataRelationships2.contains(hiMetadataRelationships));
	}

	@Test
	public void ut_c2_test_getHIMetadataExternalRelationships() {
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.list()).thenThrow(new NullPointerException());
		List<HIMetadataRelationships> hiMetadataRelationships2 = hiMetadataResourceDBDAOImpl.getHIMetadataExternalRelationships(1);
		assertEquals(null, hiMetadataRelationships2);
	}

	@Test
	public void ut_c3_test_getMetadataTablesList() {
		List<HIMetadataTables> metadataTablesList = hiMetadataResourceDBDAOImpl.getMetadataTablesList(1, 2);
		assertEquals(null, metadataTablesList);
	}

	@Test
	public void ut_c4_test_getMetadataViewList() {
		List<HIMetadataView> metadataViewList = hiMetadataResourceDBDAOImpl.getMetadataViewList(1, 2);
		assertEquals(null, metadataViewList);
	}

	@Test
	public void ut_c5_test_addHIMetadataView() {
		HIMetadataView hiMetadataView = new HIMetadataView();
		when(session.save(hiMetadataView)).thenReturn(hiMetadataView);
		hiMetadataResourceDBDAOImpl.addHIMetadataView(hiMetadataView);
	}

	@Test
	public void ut_c6_test_addHIMetadataView() {
		HIMetadataView hiMetadataView = new HIMetadataView();
		when(session.save(hiMetadataView)).thenThrow(new NullPointerException());
		hiMetadataResourceDBDAOImpl.addHIMetadataView(hiMetadataView);
	}

	@Test
	public void ut_c7_test_editHIMetadataView() {
		HIMetadataView hiMetadataView = new HIMetadataView();
		when(session.save(hiMetadataView)).thenThrow(new NullPointerException());
		hiMetadataResourceDBDAOImpl.editHIMetadataView(hiMetadataView);
	}

	@Test
	public void ut_c8_test_getMetadataColumnsList() {
		
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.list()).thenThrow(new NullPointerException());
		List<HIMetadataColumns> metadataColumnsList = hiMetadataResourceDBDAOImpl.getMetadataColumnsList(1,2);
		assertEquals(null, metadataColumnsList);
	}

	@Test
	public void ut_c9_test_getMetaSecurity() {
		
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.list()).thenThrow(new NullPointerException());
		List<HIMetadataSecurity> metadataSecurities = hiMetadataResourceDBDAOImpl.getMetaSecurity(1);
		assertEquals(null, metadataSecurities);
	}

	@Test
	public void ut_d1_test_addHIMetadataSecurity() {
		HIMetadataSecurity metadataSecurity = new HIMetadataSecurity();
		doThrow(new NullPointerException()).when(session).save(metadataSecurity);
		hiMetadataResourceDBDAOImpl.addHIMetadataSecurity(metadataSecurity);
	}

	@Test
	public void ut_d2_test_editHIMetadataSecurity() {
		HIMetadataSecurity metadataSecurity = new HIMetadataSecurity();
		doThrow(new NullPointerException()).when(session).update(metadataSecurity);
		hiMetadataResourceDBDAOImpl.editHIMetadataSecurity(metadataSecurity);
	}

	@Test
	public void ut_d3_test_addCube() {
		HIMetadataCube cube = new HIMetadataCube();
		when(session.save(cube)).thenReturn(cube);
		hiMetadataResourceDBDAOImpl.addCube(cube);
	}

	@Test
	public void ut_d4_test_addCube() {
		HIMetadataCube cube = new HIMetadataCube();
		doThrow(new NullPointerException()).when(session).save(cube);
		hiMetadataResourceDBDAOImpl.addCube(cube);
	}

	@Test
	public void ut_d5_test_add() {
		HIMetadataCube cube = new HIMetadataCube();
		when(session.save(cube)).thenReturn(cube);
		hiMetadataResourceDBDAOImpl.add(cube);
	}

	@Test
	public void ut_d6_test_add() {
		HIMetadataCube cube = new HIMetadataCube();
		doThrow(new NullPointerException()).when(session).save(cube);
		hiMetadataResourceDBDAOImpl.add(cube);
	}

	@Test
	public void ut_d7_test_addHIMetadataTables() {
		HIMetadataTables hiMetadataTables = new HIMetadataTables();
		doThrow(new NullPointerException()).when(session).save(hiMetadataTables);
		hiMetadataResourceDBDAOImpl.addHIMetadataTables(hiMetadataTables);
	}

	@Test
	public void ut_d8_test_addHIMetadataRelationships() {
		HIMetadataRelationships hiMetadataRelationships = new HIMetadataRelationships();
		doThrow(new NullPointerException()).when(session).save(hiMetadataRelationships);
		hiMetadataResourceDBDAOImpl.addHIMetadataRelationships(hiMetadataRelationships);
	}

	@Test
	public void ut_d9_test_addHIMetadataConnections() {
		HIMetadataConnections hiMetadataConnections = new HIMetadataConnections();
		doThrow(new NullPointerException()).when(session).save(hiMetadataConnections);
		Integer addHIMetadataConnections = hiMetadataResourceDBDAOImpl.addHIMetadataConnections(hiMetadataConnections);
		assertNull(addHIMetadataConnections);
	}

	@Test
	public void ut_e1_test_editHIMetadataConnections() {
		HIMetadataConnections hiMetadataConnections = new HIMetadataConnections();
		doThrow(new NullPointerException()).when(session).save(hiMetadataConnections);
		Integer editHIMetadataConnections = hiMetadataResourceDBDAOImpl
				.editHIMetadataConnections(hiMetadataConnections);
		assertNull(editHIMetadataConnections);
	}

	@Test
	public void ut_e2_test_getHIMetadataConnections() {
		HIMetadataConnections hiMetadataConnections = new HIMetadataConnections();
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.list()).thenThrow(new NullPointerException());
		List<HIMetadataConnections> connections = hiMetadataResourceDBDAOImpl.getHIMetadataConnections(1);
		assertNull(connections);
	}

	@Test
	public void ut_e3_test_editHIMetadataConnectionGlobal() {
		HIMetadataConnectionGlobal hiMetadataConnectionGlobal = new HIMetadataConnectionGlobal();
		doThrow(new NullPointerException()).when(session).update(hiMetadataConnectionGlobal);
		Integer editHIMetadataConnectionGlobal = hiMetadataResourceDBDAOImpl
				.editHIMetadataConnectionGlobal(hiMetadataConnectionGlobal);
		assertNull(editHIMetadataConnectionGlobal);
	}

	@Test
	public void ut_e4_test_getHIMetadataConnectionGlobal() {
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.list()).thenThrow(new NullPointerException());
		HIMetadataConnectionGlobal editHIMetadataConnectionGlobal = hiMetadataResourceDBDAOImpl.getHIMetadataConnectionGlobal(1,"2");
		assertNull(editHIMetadataConnectionGlobal);
	}

	@Test
	public void ut_e5_test_addHIMetadataColumns() {
		HIMetadataColumns hiMetadataColumns = new HIMetadataColumns();
		doThrow(new NullPointerException()).when(session).save(hiMetadataColumns);
		Integer addHIMetadataColumns = hiMetadataResourceDBDAOImpl.addHIMetadataColumns(hiMetadataColumns);
		assertNull(addHIMetadataColumns);
	}

	@Test
	public void ut_e6_test_saveHIMetadataConnectionGlobal() {
		HIMetadataConnectionGlobal hiMetadataConnectionGlobal = new HIMetadataConnectionGlobal();
		doThrow(new NullPointerException()).when(session).save(hiMetadataConnectionGlobal);
		Integer saveHIMetadataConnectionGlobal = hiMetadataResourceDBDAOImpl
				.saveHIMetadataConnectionGlobal(hiMetadataConnectionGlobal);
		assertNull(saveHIMetadataConnectionGlobal);
	}

	@Test
	public void ut_e7_test_addHIMetadataDatabases() {
		MetadataDatabases hiMetadataDatabases = new MetadataDatabases();
		doThrow(new NullPointerException()).when(session).save(hiMetadataDatabases);
		Integer addHIMetadataDatabases = hiMetadataResourceDBDAOImpl.addHIMetadataDatabases(hiMetadataDatabases);
		assertNull(addHIMetadataDatabases);
	}

	@Test
	public void ut_e8_test_getHIMetadataConnectionEFWD() {
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.list()).thenThrow(new NullPointerException());
		
		HIMetadataConnectionEFWD hiMetadataConnectionEFWD = hiMetadataResourceDBDAOImpl.getHIMetadataConnectionEFWD(1);
		assertNull(hiMetadataConnectionEFWD);
	}

	@Test
	public void ut_e9_test_saveHIMetadataConnectionEfwd() {
		HIMetadataConnectionEFWD metadataConnectionEfwd = new HIMetadataConnectionEFWD();
		doThrow(new NullPointerException()).when(session).save(metadataConnectionEfwd);
		Integer saveHIMetadataConnectionEfwd = hiMetadataResourceDBDAOImpl
				.saveHIMetadataConnectionEfwd(metadataConnectionEfwd);
		assertNull(saveHIMetadataConnectionEfwd);
	}

	@Test
	public void ut_f1_test_editHIMetadataConnectionEfwd() {
		HIMetadataConnectionEFWD metadataConnectionEfwd = new HIMetadataConnectionEFWD();
		doThrow(new NullPointerException()).when(session).update(metadataConnectionEfwd);
		Integer editHIMetadataConnectionEfwd = hiMetadataResourceDBDAOImpl
				.editHIMetadataConnectionEfwd(metadataConnectionEfwd);
		assertNull(editHIMetadataConnectionEfwd);
	}

	@Test
	public void ut_f2_test_deleteMetadataGlobalConnection() {
		HIMetadataConnectionGlobal globalCon = new HIMetadataConnectionGlobal();
		doThrow(new NullPointerException()).when(session).delete(globalCon);
		hiMetadataResourceDBDAOImpl.deleteMetadataGlobalConnection(globalCon);

	}

	@Test
	public void ut_f3_test_deleteMetadataEfwdConnection() {
		HIMetadataConnectionEFWD efwdCon = new HIMetadataConnectionEFWD();
		doThrow(new NullPointerException()).when(session).delete(efwdCon);
		hiMetadataResourceDBDAOImpl.deleteMetadataEfwdConnection(efwdCon);

	}

	@Test
	public void ut_f4_test_getHIMetadataConnection() {
		HIMetadataConnectionEFWD efwdCon = new HIMetadataConnectionEFWD();
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.list()).thenThrow(new NullPointerException());
		HIMetadataConnections hiMetadataConnection = hiMetadataResourceDBDAOImpl.getHIMetadataConnection(1, 2, "3");
		assertNull(hiMetadataConnection);

	}

	@Test
	public void ut_f4_test_getDumpedMetadataList() {
		HIMetadataConnectionEFWD efwdCon = new HIMetadataConnectionEFWD();
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.list()).thenThrow(new NullPointerException());
		List<MetadataDumpDTO> dtos = hiMetadataResourceDBDAOImpl.getDumpedMetadataList();
		assertTrue(dtos.isEmpty());

	}

	@Test
	public void ut_f5_test_getDumpedMetadataList() {
		HIResourcePhaseStatus hiResourcePhaseStatus = mock(HIResourcePhaseStatus.class);
		HIPhase hiPhase = mock(HIPhase.class);
		HIResource hiResource = mock(HIResource.class);
		when(session.createQuery(anyString())).thenReturn(query);
		List<HIResourcePhaseStatus> cubePhaseDetails = new ArrayList<>();
		cubePhaseDetails.add(hiResourcePhaseStatus);
		when(hiResourcePhaseStatus.getHiPhase()).thenReturn(hiPhase);
		when(hiResourcePhaseStatus.getAction()).thenReturn("action");
		when(hiResourcePhaseStatus.getId()).thenReturn(12);

		when(hiPhase.getStatus()).thenReturn("COMPLETED");
		when(hiResourcePhaseStatus.getHiResource()).thenReturn(hiResource);
		when(hiResource.getResourcePath()).thenReturn("path");
		when(hiResource.getResourceURL()).thenReturn("url");
		when(hiResource.getTitle()).thenReturn("title");

		when(query.list()).thenReturn(cubePhaseDetails);
		List<MetadataDumpDTO> dtos = hiMetadataResourceDBDAOImpl.getDumpedMetadataList();
		assertFalse(dtos.isEmpty());

	}

	@Test
	public void ut_f6_test_getDumpedMetadataList() {
		HIResourcePhaseStatus hiResourcePhaseStatus = mock(HIResourcePhaseStatus.class);
		HIPhase hiPhase = mock(HIPhase.class);
		HIResource hiResource = mock(HIResource.class);
		when(session.createQuery(anyString())).thenReturn(query);
		List<HIResourcePhaseStatus> cubePhaseDetails = new ArrayList<>();
		cubePhaseDetails.add(hiResourcePhaseStatus);
		when(hiResourcePhaseStatus.getHiPhase()).thenReturn(hiPhase);
		when(hiResourcePhaseStatus.getAction()).thenReturn("action");
		when(hiResourcePhaseStatus.getId()).thenReturn(12);

		when(hiPhase.getStatus()).thenReturn("COMPLET");
		when(hiResourcePhaseStatus.getHiResource()).thenReturn(hiResource);
		when(hiResource.getResourcePath()).thenReturn("path");
		when(hiResource.getResourceURL()).thenReturn("url");
		when(hiResource.getTitle()).thenReturn("title");

		when(query.list()).thenReturn(cubePhaseDetails);
		List<MetadataDumpDTO> dtos = hiMetadataResourceDBDAOImpl.getDumpedMetadataList();
		assertFalse(dtos.isEmpty());

	}

	@Test
	public void ut_f7_test_setCache() {
		when(session.createQuery(anyString())).thenReturn(query);
		hiMetadataResourceDBDAOImpl.setCache(1, false);
	}

	@Test
	public void ut_f8_test_removeMetadataConnection() {
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.uniqueResult()).thenThrow(new RuntimeException());
		hiMetadataResourceDBDAOImpl.removeMetadataConnection("1", "2", "3");
	}

	@Test
	public void ut_f9_test_findJoinById() {
		when(session.get(HIMetadataRelationships.class, 1)).thenThrow(new RuntimeException());
		HIMetadataRelationships findJoinById = hiMetadataResourceDBDAOImpl.findJoinById("1");
		assertNull(findJoinById);
	}

	@Test
	public void ut_h1_test_deleteHIMetadataRelationship() {
		List<Integer> joinsToDelete = new ArrayList<>();
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.executeUpdate()).thenThrow(new RuntimeException());
		hiMetadataResourceDBDAOImpl.deleteHIMetadataRelationship(joinsToDelete);
	}

	@Test
	public void ut_h2_test_getHIMetadataDatabaseById() {
		List<Integer> joinsToDelete = new ArrayList<>();
		when(session.get(HIMetadataRelationships.class, 1)).thenThrow(new RuntimeException());
		MetadataDatabases hiMetadataDatabaseById = hiMetadataResourceDBDAOImpl.getHIMetadataDatabaseById(1);
		assertNull(hiMetadataDatabaseById);
	}

	@Test
	public void ut_h3_test_findTableById() {
		HIMetadataTables table = new HIMetadataTables();
		when(session.get(HIMetadataTables.class, 1)).thenReturn(table);
		hiMetadataResourceDBDAOImpl.findTableById(1);
	}

	@Test
	public void ut_h4_test_findColumnById() {
		when(session.get(HIMetadataColumns.class, 1)).thenThrow(new RuntimeException());
		HIMetadataColumns findColumnById = hiMetadataResourceDBDAOImpl.findColumnById(1);
		assertEquals(null, findColumnById);
	}

	@Test
	public void ut_h5_test_findColumnById() {
		HIMetadataColumns dbCol = new HIMetadataColumns();
		when(session.get(HIMetadataColumns.class, 1)).thenReturn(dbCol);
		HIMetadataColumns findColumnById = hiMetadataResourceDBDAOImpl.findColumnById(1);
		assertEquals(dbCol, findColumnById);
	}

	@Test
	public void ut_h6_test_getRelationshipListByMetadataIdAndDbId() {
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.list()).thenThrow(new RuntimeException());
		List<HIMetadataRelationships> relationshipListByMetadataIdAndDbId = hiMetadataResourceDBDAOImpl.getRelationshipListByMetadataIdAndDbId(1, 2);
		assertTrue(relationshipListByMetadataIdAndDbId.isEmpty());
	}

	@Test
	public void ut_h7_test_getMetadataCacheStatusAndUpdateTime() {
		MetadataCacheStatus cacheStatus = mock(MetadataCacheStatus.class);
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.uniqueResult()).thenReturn(cacheStatus);
		MetadataCacheStatus metadataCacheStatusAndUpdateTime = hiMetadataResourceDBDAOImpl
				.getMetadataCacheStatusAndUpdateTime(1);
		assertEquals(cacheStatus, metadataCacheStatusAndUpdateTime);
	}

	@Test
	public void ut_h8_test_loadHiResourceMetadataPropertiesById() {
		MetadataProperties metadataPojo = mock(MetadataProperties.class);
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.uniqueResult()).thenReturn(metadataPojo);
		MetadataProperties loadHiResourceMetadataPropertiesById = hiMetadataResourceDBDAOImpl
				.loadHiResourceMetadataPropertiesById(1);
		assertEquals(metadataPojo, loadHiResourceMetadataPropertiesById);
	}

	@Test
	public void ut_h9_test_loadHiResourceMetadataPropertiesById() {
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.uniqueResult()).thenThrow(new RuntimeException());
		MetadataProperties loadHiResourceMetadataPropertiesById = hiMetadataResourceDBDAOImpl.loadHiResourceMetadataPropertiesById(1);
		assertEquals(null, loadHiResourceMetadataPropertiesById);
	}

	@Test
	public void ut_i1_test_getMetadataConnection() {
		HIMetadataConnections connections = mock(HIMetadataConnections.class);
		HIMetadataConnectionEFWD connectionEFWD = mock(HIMetadataConnectionEFWD.class);
		ConnectionDetails newConn = mock(ConnectionDetails.class);
		HIEfwdConnection hiEfwdConnection = mock(HIEfwdConnection.class);
		HIEFWD hiefwd = mock(HIEFWD.class);
		HIResource hiResource = mock(HIResource.class);
		Database database = mock(Database.class);
		DriverClass driverClass = mock(DriverClass.class);
		
		when(session.createQuery(anyString())).thenReturn(query);
		List<HIMetadataConnections> list = new ArrayList<>();
		list.add(connections);
		when(query.list()).thenReturn(list);
		List<HIMetadataConnectionEFWD> connectionEFWDs = new ArrayList<>();
		connectionEFWDs.add(connectionEFWD);
		when(connections.getConnectionType()).thenReturn("type");
		when(connections.getMetadataConnectionEfwd()).thenReturn(connectionEFWDs);
		when(connectionEFWD.getHiEfwdConnection()).thenReturn(hiEfwdConnection);
		when(hiEfwdConnection.getHiResourceEFWD()).thenReturn(hiefwd);
		when(hiefwd.getParentResource()).thenReturn(hiResource);

		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode switchedConnection = objectMapper.createObjectNode();
		switchedConnection.put("driverClassName", "your_driverClassName");
		switchedConnection.put("database", "your_database_value_here");
		switchedConnection.put("catalog", "your_catalog_value_here");
		switchedConnection.put("schema", "your_schema_value_here");

		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<GlobalJdbcTypeUtils> mockedStatic2 = mockStatic(GlobalJdbcTypeUtils.class)) {
				try(MockedConstruction<GroovyManagedJdbcHandler> construction = mockConstruction(GroovyManagedJdbcHandler.class,(mock,context)->{
					when(mock.readDS(anyString(),anyString())).thenReturn(switchedConnection);
				})){
					
				
				mockedStatic2.when(() -> GlobalJdbcTypeUtils.isManagedGroovyDataSource(anyString())).thenReturn(true);

				mockedStatic.when(() -> ApplicationContextAccessor.getBean(ConnectionDetails.class))
						.thenReturn(newConn);
				mockedStatic.when(() -> ApplicationContextAccessor.getBean(Database.class)).thenReturn(database);
				mockedStatic.when(() -> ApplicationContextAccessor.getBean(DriverClass.class)).thenReturn(driverClass);
				hiMetadataResourceDBDAOImpl.getMetadataConnection(1);
				
				}
			}
		}
	}
	
	
	@Test
	public void ut_i2_test_getMetadataConnection() {
		HIMetadataConnections connections = mock(HIMetadataConnections.class);
		HIMetadataConnectionEFWD connectionEFWD = mock(HIMetadataConnectionEFWD.class);
		ConnectionDetails newConn = mock(ConnectionDetails.class);
		HIEfwdConnection hiEfwdConnection = mock(HIEfwdConnection.class);
		HIEFWD hiefwd = mock(HIEFWD.class);
		HIResource hiResource = mock(HIResource.class);
		Database database = mock(Database.class);
		DriverClass driverClass = mock(DriverClass.class);
		
		when(session.createQuery(anyString())).thenReturn(query);
		List<HIMetadataConnections> list = new ArrayList<>();
		list.add(connections);
		when(query.list()).thenReturn(list);
		List<HIMetadataConnectionEFWD> connectionEFWDs = new ArrayList<>();
		connectionEFWDs.add(connectionEFWD);
		when(connections.getConnectionType()).thenReturn("type");
		when(connections.getMetadataConnectionEfwd()).thenReturn(connectionEFWDs);
		when(connectionEFWD.getHiEfwdConnection()).thenReturn(hiEfwdConnection);
		when(hiEfwdConnection.getHiResourceEFWD()).thenReturn(hiefwd);
		when(hiefwd.getParentResource()).thenReturn(hiResource);

		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode switchedConnection = objectMapper.createObjectNode();
		switchedConnection.put("driverClassName", "your_driverClassName");
		switchedConnection.put("database", "your_database_value_here");
		switchedConnection.put("catalog", "your_catalog_value_here");
		switchedConnection.put("schema", "your_schema_value_here");

		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<GlobalJdbcTypeUtils> mockedStatic2 = mockStatic(GlobalJdbcTypeUtils.class)) {
				
					
				
				mockedStatic2.when(() -> GlobalJdbcTypeUtils.isManagedGroovyDataSource(anyString())).thenReturn(false);

				mockedStatic.when(() -> ApplicationContextAccessor.getBean(ConnectionDetails.class))
						.thenReturn(newConn);
				mockedStatic.when(() -> ApplicationContextAccessor.getBean(Database.class)).thenReturn(database);
				mockedStatic.when(() -> ApplicationContextAccessor.getBean(DriverClass.class)).thenReturn(driverClass);
				hiMetadataResourceDBDAOImpl.getMetadataConnection(1);
				
				
			}
		}
	}

	@Test
	public void ut_i3_test_getMetadataConnection() {
		HIMetadataConnections connections = mock(HIMetadataConnections.class);
		HIMetadataConnectionEFWD connectionEFWD = mock(HIMetadataConnectionEFWD.class);
		ConnectionDetails newConn = mock(ConnectionDetails.class);
		HIEfwdConnection hiEfwdConnection = mock(HIEfwdConnection.class);
		HIEFWD hiefwd = mock(HIEFWD.class);
		HIResource hiResource = mock(HIResource.class);
		Database database = mock(Database.class);
		DriverClass driverClass = mock(DriverClass.class);
		HIMetadataConnectionGlobal hiMetadataConnectionGlobal = mock(HIMetadataConnectionGlobal.class);
		
		when(session.createQuery(anyString())).thenReturn(query);
		List<HIMetadataConnections> list = new ArrayList<>();
		list.add(connections);
		when(query.list()).thenReturn(list);
		List<HIMetadataConnectionEFWD> connectionEFWDs = new ArrayList<>();
		
		when(connections.getConnectionType()).thenReturn("type");
		when(connections.getMetadataConnectionEfwd()).thenReturn(connectionEFWDs);
		when(connectionEFWD.getHiEfwdConnection()).thenReturn(hiEfwdConnection);
		when(hiEfwdConnection.getHiResourceEFWD()).thenReturn(hiefwd);
		when(hiefwd.getParentResource()).thenReturn(hiResource);

		ObjectMapper objectMapper = new ObjectMapper();
		ObjectNode switchedConnection = objectMapper.createObjectNode();
		switchedConnection.put("driverClassName", "your_driverClassName");
		switchedConnection.put("database", "your_database_value_here");
		switchedConnection.put("catalog", "your_catalog_value_here");
		switchedConnection.put("schema", "your_schema_value_here");
		
		List<HIMetadataConnectionGlobal> metadataGlobalConnList = new ArrayList<>();
		metadataGlobalConnList.add(hiMetadataConnectionGlobal);
		GlobalConnections globalConnections = mock(GlobalConnections.class);
		when(hiMetadataConnectionGlobal.getGlobalConnections()).thenReturn(globalConnections);
		when(connections.getMetadataGlobalConnList()).thenReturn(metadataGlobalConnList);
		
		try (MockedStatic<ApplicationContextAccessor> mockedStatic = mockStatic(ApplicationContextAccessor.class)) {
			try (MockedStatic<GlobalJdbcTypeUtils> mockedStatic2 = mockStatic(GlobalJdbcTypeUtils.class)) {
				
				
				mockedStatic2.when(() -> GlobalJdbcTypeUtils.isManagedGroovyDataSource(anyString())).thenReturn(false);

				mockedStatic.when(() -> ApplicationContextAccessor.getBean(ConnectionDetails.class))
						.thenReturn(newConn);
				mockedStatic.when(() -> ApplicationContextAccessor.getBean(Database.class)).thenReturn(database);
				mockedStatic.when(() -> ApplicationContextAccessor.getBean(DriverClass.class)).thenReturn(driverClass);
				hiMetadataResourceDBDAOImpl.getMetadataConnection(1);
				
				
			}
		}
	}

	@Test
	public void ut_i4_test_getConnectionRefAndDriver() {
		when(session.createSelectionQuery(anyString(), any())).thenReturn(query);
		MetadataDriverReferences connectionRefAndDriver = hiMetadataResourceDBDAOImpl.getConnectionRefAndDriver(1);
		assertNull(connectionRefAndDriver);
		
	}
	
	@Test
	public void ut_i5_test_getConnectionRefAndDriver() {
		HIMetadataConnections hiMetadataConnections = mock(HIMetadataConnections.class);
		MetadataDriverReferences driverReferences = mock(MetadataDriverReferences.class);
		List<HIMetadataConnections> connections = new ArrayList<>();
		connections.add(hiMetadataConnections);
		when(query.list()).thenReturn(connections);
		when(hiMetadataConnections.getConnectionType()).thenReturn("global.jdbc");
		when(session.createSelectionQuery(anyString(), any())).thenReturn(query);
		when(session.createQuery(anyString(),any())).thenReturn(query);
		when(query.uniqueResult()).thenReturn(driverReferences);
		MetadataDriverReferences connectionRefAndDriver = hiMetadataResourceDBDAOImpl.getConnectionRefAndDriver(1);
		assertEquals(driverReferences, connectionRefAndDriver);
		
	}
	
	@Test
	public void ut_i6_test_getConnectionRefAndDriver() {
		HIMetadataConnections hiMetadataConnections = mock(HIMetadataConnections.class);
		MetadataDriverReferences driverReferences = mock(MetadataDriverReferences.class);
		List<HIMetadataConnections> connections = new ArrayList<>();
		connections.add(hiMetadataConnections);
		when(query.list()).thenReturn(connections);
		when(hiMetadataConnections.getConnectionType()).thenReturn("jdbc");
		when(session.createSelectionQuery(anyString(), any())).thenReturn(query);
		when(session.createQuery(anyString(),any())).thenReturn(query);
		when(query.uniqueResult()).thenReturn(driverReferences);
		MetadataDriverReferences connectionRefAndDriver = hiMetadataResourceDBDAOImpl.getConnectionRefAndDriver(1);
		assertEquals(driverReferences, connectionRefAndDriver);
		
	}
	
	@Test
	public void ut_i7_test_getConnectionRefAndDriver() {
		HIMetadataConnections hiMetadataConnections = mock(HIMetadataConnections.class);
		MetadataDriverReferences driverReferences = mock(MetadataDriverReferences.class);
		List<HIMetadataConnections> connections = new ArrayList<>();
		connections.add(hiMetadataConnections);
		connections.add(hiMetadataConnections);
		when(query.list()).thenReturn(connections);
		when(hiMetadataConnections.getConnectionType()).thenReturn("jdbc");
		when(session.createSelectionQuery(anyString(), any())).thenReturn(query);
		when(session.createQuery(anyString(),any())).thenReturn(query);
		when(query.uniqueResult()).thenReturn(driverReferences);
		MetadataDriverReferences connectionRefAndDriver = hiMetadataResourceDBDAOImpl.getConnectionRefAndDriver(1);
		assertNotNull(connectionRefAndDriver);
		
	}
	
	@Test
	public void ut_i8_test_deleteHIMetadataColumnById() {
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.executeUpdate()).thenReturn(1).thenReturn(1);
		hiMetadataResourceDBDAOImpl.deleteHIMetadataColumnById(1);
	}
	
	@Test
	public void ut_i9_test_deleteHIMetadataColumnById() {
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.executeUpdate()).thenThrow(new NullPointerException());
		hiMetadataResourceDBDAOImpl.deleteHIMetadataColumnById(1);
	}
	
	@Test
	public void ut_j1_test_deleteAllSecuritiesByMetadataId() {
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.executeUpdate()).thenThrow(new NullPointerException());
		hiMetadataResourceDBDAOImpl.deleteAllSecuritiesByMetadataId(1);
	}
	
	@Test
	public void ut_j2_test_getMetadataColumns() {
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.executeUpdate()).thenThrow(new NullPointerException());
		hiMetadataResourceDBDAOImpl.getMetadataColumns(1,1);
	}
}
