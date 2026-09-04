package com.helicalinsight.datasource;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MongoConnectionFactoryTest {

    @Test
    void mongoDriverClassShouldBeLoadable() {
        assertDoesNotThrow(() -> {
            Class.forName("com.mongodb.jdbc.MongoDriver");
        }, "MongoDB JDBC driver class should be on the classpath");
    }

}
