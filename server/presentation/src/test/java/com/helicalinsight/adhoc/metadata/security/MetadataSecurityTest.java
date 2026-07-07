package com.helicalinsight.adhoc.metadata.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MetadataSecurityTest {

	private MetadataSecurity metadataSecurity;

    @Before
    public void setUp() {
        metadataSecurity = new MetadataSecurity();
    }

    @Test
    public void testGettersAndSetters() {
        List<SecurityExpression> expressions = new ArrayList<>();
        expressions.add(new SecurityExpression());
        expressions.add(new SecurityExpression());
        metadataSecurity.setExpressions(expressions);
        assertEquals(expressions, metadataSecurity.getExpressions());
    }

    @Test
    public void testEqualsAndHashCode() {
        List<SecurityExpression> expressions1 = new ArrayList<>();
        expressions1.add(new SecurityExpression());
        expressions1.add(new SecurityExpression());

        List<SecurityExpression> expressions2 = new ArrayList<>();
        expressions2.add(new SecurityExpression());
        expressions2.add(new SecurityExpression());

        MetadataSecurity metadataSecurity1 = new MetadataSecurity();
        metadataSecurity1.setExpressions(expressions1);

        MetadataSecurity metadataSecurity2 = new MetadataSecurity();
        metadataSecurity2.setExpressions(expressions2);

        assertTrue(metadataSecurity1.equals(metadataSecurity2));
        assertTrue(metadataSecurity2.equals(metadataSecurity1));
        assertEquals(metadataSecurity1.hashCode(), metadataSecurity2.hashCode());
    }

    @Test
    public void test2EqualsAndHashCode() {
        List<SecurityExpression> expressions1 = new ArrayList<>();
        expressions1.add(new SecurityExpression());
        expressions1.add(new SecurityExpression());

        List<SecurityExpression> expressions2 = new ArrayList<>();
        expressions2.add(new SecurityExpression());
        expressions2.add(new SecurityExpression());

        MetadataSecurity metadataSecurity1 = new MetadataSecurity();
        metadataSecurity1.setExpressions(null);

        MetadataSecurity metadataSecurity2 = new MetadataSecurity();
        metadataSecurity2.setExpressions(expressions2);

        assertTrue(metadataSecurity1.equals(metadataSecurity1));
        assertFalse(metadataSecurity2.equals(new Object()));
        assertFalse(metadataSecurity1.equals(metadataSecurity2));
    }

    @Test
    public void testToString() {
        List<SecurityExpression> expressions = new ArrayList<>();
        expressions.add(new SecurityExpression());
        expressions.add(new SecurityExpression());
        metadataSecurity.setExpressions(expressions);

        String expectedToString = "MetadataSecurity{" +
                "expressions=" + expressions +
                "}";
        assertEquals(expectedToString, metadataSecurity.toString());
    }
}
