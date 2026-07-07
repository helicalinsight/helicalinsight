package com.helicalinsight.efw.utility;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.jetbrains.annotations.NotNull;

/**
 * Converts database types to Java class types.
 *
 * @author Somen 13/04/2015
 * @author Rajasekhar
 */
public class SQLTypeMap {
    /**
     * Translates a data type from an integer (java.sql.Types value) to a string
     * that represents the corresponding class.
     *
     * @param type The java.sql.Types value to convert to its corresponding class.
     * @return The class that corresponds to the given java.sql.Types
     * value, or Object.class if the type has no known mapping.
     */
    @NotNull
    public static Class<?> toClass(int type) {
        Class<?> result = Object.class;

        switch (type) {
            case Types.CHAR://fallthrough
            case Types.VARCHAR://fallthrough
            case Types.LONGVARCHAR:
            case Types.NCHAR:
            case Types.NVARCHAR:
            case Types.LONGNVARCHAR:
                result = String.class;
                break;

            case Types.NUMERIC://fallthrough http://spark.apache.org/third-party-projects.html
            case Types.DECIMAL://fallthrough
                result = java.math.BigDecimal.class;
                break;

            case Types.BIT:
            case Types.BOOLEAN:
                result = Boolean.class;
                break;

            case Types.TINYINT:
                result = Byte.class;
                break;

            case Types.SMALLINT:
                result = Short.class;
                break;

            case Types.INTEGER:
                result = Integer.class;
                break;

            case Types.BIGINT:
                result = Long.class;
                break;

            case Types.REAL://fallthrough
            case Types.FLOAT:
                result = Float.class;
                break;

            case Types.DOUBLE:
                result = Double.class;
                break;


            case Types.DATE:
                result = java.sql.Date.class;
                break;

            case Types.TIME:
            case Types.TIME_WITH_TIMEZONE:
                result = java.sql.Time.class;
                break;
            case -101 :
            case Types.TIMESTAMP:
            case Types.TIMESTAMP_WITH_TIMEZONE:
                result = java.sql.Timestamp.class;
                break;

            case Types.STRUCT:
                result = java.sql.Struct.class;
                break;

            case Types.ARRAY:
                result = java.sql.Array.class;
                break;

            case Types.SQLXML:
                result = java.sql.SQLXML.class;
                break;

            case Types.ROWID:
                result = java.sql.RowId.class;
                break;

            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
            case Types.BLOB:
                result = java.sql.Blob.class;
           
        }
        return result;
    }
    public static String getSpecificFromResultset(String matching, ResultSet resultSet, int index) throws SQLException{
    	
    	
    	if(matching.contains("oracle.sql.TIMESTAMPTZ")) {
    		Object timestamp = resultSet.getTimestamp(index);
    		return timestamp != null ? timestamp.toString() : null;
    		
    	}
    	
    	return null;
    }
}