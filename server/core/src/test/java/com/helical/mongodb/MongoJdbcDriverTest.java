package com.helical.mongodb;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MongoJdbcDriverTest {

    private final MongoJdbcDriver driver = new MongoJdbcDriver();

    @Test
    public void testAcceptsMongoDbUrl() throws Exception {
        assertTrue(driver.acceptsURL("mongodb://localhost:27017/testdb"));
    }

    @Test
    public void testAcceptsMongoDbSrvUrl() throws Exception {
        assertTrue(driver.acceptsURL("mongodb+srv://cluster.example.com/testdb"));
    }

    @Test
    public void testAcceptsJdbcMongoDbUrl() throws Exception {
        assertTrue(driver.acceptsURL("jdbc:mongodb://localhost:27017/testdb"));
    }

    @Test
    public void testRejectsUnsupportedUrl() throws Exception {
        assertFalse(driver.acceptsURL("jdbc:mysql://localhost:3306/testdb"));
    }

    @Test
    public void testRejectsNullUrl() throws Exception {
        assertFalse(driver.acceptsURL(null));
    }
}
