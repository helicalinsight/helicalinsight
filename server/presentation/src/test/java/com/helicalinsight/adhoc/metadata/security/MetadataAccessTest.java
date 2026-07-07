package com.helicalinsight.adhoc.metadata.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MetadataAccessTest {
	private MetadataAccess metadataAccess;

    @Before
    public void setUp() {
        metadataAccess = new MetadataAccess();
    }

    @Test
    public void ut_a1_test_GettersAndSetters() {
        metadataAccess.setLocation("Location");
        assertEquals("Location", metadataAccess.getLocation());

        metadataAccess.setUuid("UUID");
        assertEquals("UUID", metadataAccess.getUuid());

        metadataAccess.setViewId("ViewId");
        assertEquals("ViewId", metadataAccess.getViewId());

        SecurityExpression expression = new SecurityExpression();
        metadataAccess.setExpression(expression);
        assertEquals(expression, metadataAccess.getExpression());
    }

    @Test
    public void ut_a2_test_EqualsAndHashCode() {
        MetadataAccess metadataAccess1 = new MetadataAccess();
        metadataAccess1.setLocation("Location");
        metadataAccess1.setUuid("UUID");
        metadataAccess1.setViewId("ViewId");
        SecurityExpression expression1 = new SecurityExpression();
        metadataAccess1.setExpression(expression1);

        MetadataAccess metadataAccess2 = new MetadataAccess();
        metadataAccess2.setLocation("Location");
        metadataAccess2.setUuid("UUID");
        metadataAccess2.setViewId("ViewId");
        SecurityExpression expression2 = new SecurityExpression();
        metadataAccess2.setExpression(expression2);

        assertTrue(metadataAccess1.equals(metadataAccess2));
        assertTrue(metadataAccess2.equals(metadataAccess1));
        assertEquals(metadataAccess1.hashCode(), metadataAccess2.hashCode());
    }
    
    @Test
    public void ut_a3_test_Equals() {
        MetadataAccess metadataAccess1 = new MetadataAccess();
        metadataAccess1.setLocation("Location");
        metadataAccess1.setUuid("UUID");
        metadataAccess1.setViewId("ViewId");
        SecurityExpression expression1 = new SecurityExpression();
        metadataAccess1.setExpression(expression1);

        MetadataAccess metadataAccess2 = new MetadataAccess();
        metadataAccess2.setLocation("Location2");
        metadataAccess2.setUuid("UUID2");
        metadataAccess2.setViewId("ViewId2");
        SecurityExpression expression2 = new SecurityExpression();
        metadataAccess2.setExpression(expression2);

        assertTrue(metadataAccess1.equals(metadataAccess1));
        assertFalse(metadataAccess1.equals(null));
        assertFalse(metadataAccess1.equals(metadataAccess2));
    }

    @Test
    public void ut_a4_test_Equals() {
        MetadataAccess metadataAccess1 = new MetadataAccess();
        metadataAccess1.setLocation("Location");
        metadataAccess1.setUuid("UUID");
        metadataAccess1.setViewId("ViewId");
        SecurityExpression expression1 = new SecurityExpression();
        metadataAccess1.setExpression(expression1);

        MetadataAccess metadataAccess2 = new MetadataAccess();
        metadataAccess2.setLocation("Location");
        metadataAccess2.setUuid("UUID2");
        metadataAccess2.setViewId("ViewId2");
        SecurityExpression expression2 = new SecurityExpression();
        metadataAccess2.setExpression(expression2);

        assertFalse(metadataAccess1.equals(metadataAccess2));
       
    }

    @Test
    public void ut_a5_test_Equals() {
        MetadataAccess metadataAccess1 = new MetadataAccess();
        metadataAccess1.setLocation("Location");
        metadataAccess1.setUuid("UUID");
        metadataAccess1.setViewId("ViewId");
        SecurityExpression expression1 = new SecurityExpression();
        metadataAccess1.setExpression(expression1);

        MetadataAccess metadataAccess2 = new MetadataAccess();
        metadataAccess2.setLocation("Location");
        metadataAccess2.setUuid("UUID");
        metadataAccess2.setViewId("ViewId2");
        SecurityExpression expression2 = new SecurityExpression();
        metadataAccess2.setExpression(expression2);

        assertFalse(metadataAccess1.equals(metadataAccess2));
       
    }
}
