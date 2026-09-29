package com.helicalinsight.datasource.managed;

import com.helicalinsight.datasource.managed.jaxb.TomcatPoolProperties;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TomcatConfigurerTest {

    @Test
    public void mongoDriverReceivesDatabaseNameAsConnectionProperty() {
        TomcatPoolProperties properties = new TomcatPoolProperties();
        properties.setDriverClassName("com.mongodb.jdbc.MongoDriver");
        properties.setDatabaseName("analytics");

        assertEquals("database=analytics", TomcatConfigurer.getConnectionProperties(properties));
    }

    @Test
    public void nonMongoDriverConnectionPropertiesRemainUnchanged() {
        TomcatPoolProperties properties = new TomcatPoolProperties();
        properties.setDriverClassName("org.postgresql.Driver");
        properties.setConnectionProperties("ssl=true");

        assertEquals("ssl=true", TomcatConfigurer.getConnectionProperties(properties));
    }
}