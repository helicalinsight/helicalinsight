package com.helicalinsight.efw.utility;

import com.helicalinsight.datasource.nosql.NoSQLLoader;
import com.helicalinsight.efw.exceptions.ImplementationNotFound;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;

/**
 * @author Somen
 * Created by helical021 on 11/24/2017.
 */
public class NoSqlUtils {
    public static NoSQLLoader getNoSqlImplementation(String subType) {
        if (subType != null) {
            return (NoSQLLoader) ApplicationContextAccessor.getBean(subType);
        } else {
            throw new ImplementationNotFound("The implementation of NoSql for type Null not found");
        }

    }
}
