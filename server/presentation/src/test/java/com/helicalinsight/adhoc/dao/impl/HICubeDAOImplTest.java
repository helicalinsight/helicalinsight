package com.helicalinsight.adhoc.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import org.mockito.MockitoAnnotations;

import com.helicalinsight.admin.model.HICubeDimension;
import com.helicalinsight.admin.model.HICubeHierarchyLevel;
import com.helicalinsight.admin.model.HICubeMeasure;
import com.helicalinsight.admin.model.HIDimensionHierarchy;
import com.helicalinsight.admin.model.HIMetadataCube;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HICubeDAOImplTest {

	@Mock
	private SessionFactory sessionFactory;
	@Mock
	private Session session;
	@Mock
	private Query query;
	@InjectMocks
	private HICubeDAOImpl cubeDAOImpl; 
	
	@Before
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		when(sessionFactory.getCurrentSession()).thenReturn(session);

	}
	@Test
	public void ut_a1_test_addCube() {
		HIMetadataCube cube = mock(HIMetadataCube.class);
		cubeDAOImpl.addCube(cube);
		verify(session, times(1)).save(cube);
	}
	
	@Test
	public void ut_a2_test_addCube() {
		HIMetadataCube cube = mock(HIMetadataCube.class);
		when(session.save(cube)).thenThrow(new RuntimeException("check exception"));
		cubeDAOImpl.addCube(cube);
	}
	@Test
	public void ut_a3_test_add() {
		HIMetadataCube cube = mock(HIMetadataCube.class);
		cubeDAOImpl.add(cube);
		verify(session, times(1)).save(cube);
	}
	
	@Test
	public void ut_a4_test_add() {
		HIMetadataCube cube = mock(HIMetadataCube.class);
		when(session.save(cube)).thenThrow(new RuntimeException("check exception"));
		cubeDAOImpl.add(cube);
		
	}
	@Test
	public void ut_a5_test_edit() {
		HIMetadataCube cube = mock(HIMetadataCube.class);
		cubeDAOImpl.edit(cube);
		verify(session, times(1)).update(cube);
	}
	@Test
	public void ut_a6_test_edit() {
		HIMetadataCube cube = mock(HIMetadataCube.class);  
		doThrow(new RuntimeException("check exception")).when(session).update(cube);
		cubeDAOImpl.edit(cube);
	}
	@Test
	public void ut_a7_test_findCubeMeasure() {
		HICubeMeasure cubeMeasure = mock(HICubeMeasure.class);
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.uniqueResult()).thenReturn(cubeMeasure);
		HICubeMeasure findCubeMeasure = cubeDAOImpl.findCubeMeasure(1, "123");
		assertEquals(cubeMeasure, findCubeMeasure);
	}
	@Test
	public void ut_a8_test_delete() {
		Object object = new Object();
		cubeDAOImpl.delete(object);
		verify(session, times(1)).delete(object);
	}
	@Test
	public void ut_a9_test_findCubeByResourceId() {
		HIMetadataCube cube = mock(HIMetadataCube.class);  
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.uniqueResult()).thenReturn(cube);
		HIMetadataCube findCubeByResourceId = cubeDAOImpl.findCubeByResourceId(1);
		assertEquals(cube, findCubeByResourceId);
	}
	
	@Test
	public void ut_b1_test_findCubeByResourceId() {
		when(session.createQuery(anyString())).thenThrow(new NullPointerException());
		HIMetadataCube findCubeByResourceId = cubeDAOImpl.findCubeByResourceId(1);
		assertEquals(null, findCubeByResourceId);
	}
	@Test
	public void ut_b2_test_findAllCubeDimentions() {
		List<HICubeDimension> cubeDimensions=new ArrayList<>();
		HICubeDimension cubeDimension = new HICubeDimension();
		cubeDimensions.add(cubeDimension);
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.list()).thenReturn(cubeDimensions);
		List<HICubeDimension> findAllCubeDimentions = cubeDAOImpl.findAllCubeDimentions(1);
		assertTrue(findAllCubeDimentions.contains(cubeDimension));
	}
	
	@Test
	public void ut_b3_test_findAllCubeDimentions() {
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.list()).thenThrow(new RuntimeException());
		List<HICubeDimension> findAllCubeDimentions = cubeDAOImpl.findAllCubeDimentions(1);
		assertEquals(findAllCubeDimentions,null);
	}
	
	@Test
	public void ut_b4_test_findAllDimHierarchy() {
		List<HIDimensionHierarchy> hiDimensionHierarchyList=new ArrayList<>();
		HIDimensionHierarchy hiDimensionHierarchy = new HIDimensionHierarchy();
		hiDimensionHierarchyList.add(hiDimensionHierarchy);
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.list()).thenReturn(hiDimensionHierarchyList);
		List<HIDimensionHierarchy> findAllDimHierarchy = cubeDAOImpl.findAllDimHierarchy(1);
		assertTrue(findAllDimHierarchy.contains(hiDimensionHierarchy));
	}
	
	@Test
	public void ut_b5_test_findAllDimHierarchy() {
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.list()).thenThrow(new RuntimeException());
		List<HIDimensionHierarchy> findAllDimHierarchy = cubeDAOImpl.findAllDimHierarchy(1);
		assertEquals(findAllDimHierarchy,null);
	}
	
	@Test
	public void ut_b6_test_findAllHierarchyLevels() {
		List<HICubeHierarchyLevel> hiCubeHierarchyLevelsList=new ArrayList<>();
		HICubeHierarchyLevel hiCubeHierarchyLevel = new HICubeHierarchyLevel();
		hiCubeHierarchyLevelsList.add(hiCubeHierarchyLevel);
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.list()).thenReturn(hiCubeHierarchyLevelsList);
		List<HICubeHierarchyLevel> hiCubeHierarchyLevels = cubeDAOImpl.findAllHierarchyLevels(1);
		assertTrue(hiCubeHierarchyLevels.contains(hiCubeHierarchyLevel));
	}
	
	@Test
	public void ut_b7_test_findAllHierarchyLevels() {
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.list()).thenThrow(new RuntimeException());
		List<HICubeHierarchyLevel> hiCubeHierarchyLevels = cubeDAOImpl.findAllHierarchyLevels(1);
		assertEquals(hiCubeHierarchyLevels,null);
	}
	
	@Test
	public void ut_b8_test_findAllCubeMeasuresByCubeId() {
		List<HICubeMeasure>  hiCubeMeasuresList = new ArrayList<>();
		HICubeMeasure cubeMeasure = new HICubeMeasure();
		hiCubeMeasuresList.add(cubeMeasure);
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.list()).thenReturn(hiCubeMeasuresList);
		List<HICubeMeasure> hiCubeMeasures = cubeDAOImpl.findAllCubeMeasuresByCubeId(1);
		assertTrue(hiCubeMeasures.contains(cubeMeasure));
	}
	
	@Test
	public void ut_b9_test_findAllCubeMeasuresByCubeId() {
		when(session.createQuery(anyString())).thenReturn(query);
		when(query.list()).thenThrow(new RuntimeException());
		List<HICubeMeasure> hiCubeMeasures = cubeDAOImpl.findAllCubeMeasuresByCubeId(1);
		assertEquals(hiCubeMeasures,null);
	}
}
