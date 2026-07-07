package com.helicalinsight.efw.resourceprocessor;

import com.helicalinsight.resourcedb.processor.DBProcessor;

/**
 * Created by user on 3/22/2017.
 *
 * @author Rajasekhar
 */
public class ResourceProcessorFactory {
    /**
     * For optimization purposes this field is introduced
     */
    private static final JSONProcessor PROCESSOR = new JSONProcessor();

    private static final DBProcessor dbProcessor = new DBProcessor();

    /**
     * Avoids the need to create a new object each and every time. Acts like a factory method for IProcessor
     *
     * @return The static field PROCESSOR
     */
    public static IProcessor getIProcessor() {
        return PROCESSOR;
    }

    public static DBProcessor getDbProcessor(){
        return dbProcessor;
    }
}
