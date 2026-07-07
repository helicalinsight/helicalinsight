package com.helicalinsight.adhoc.genericsql;

import com.helicalinsight.efw.exceptions.MalformedJsonException;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for finding enum types based on Java class names.
 * 
 * Created by author on 07-05-2015.
 * @author Rajasekhar
 */
public final class EnumTypeFinder {
	// Mapping of full class names to JavaTypes enum values
    @SuppressWarnings("serial")
    @NotNull
    private static final Map<String, JavaTypes> types = new HashMap<String, JavaTypes>() {{
        put("java.lang.String", JavaTypes.STRING);
        put("java.math.BigDecimal", JavaTypes.NUMBER);
        put("java.lang.Boolean", JavaTypes.BOOLEAN);
        put("java.lang.Byte", JavaTypes.NUMBER);
        put("java.lang.Short", JavaTypes.NUMBER);
        put("java.lang.Integer", JavaTypes.NUMBER);
        put("java.lang.Long", JavaTypes.NUMBER);
        put("java.lang.Float", JavaTypes.NUMBER);
        put("java.lang.Double", JavaTypes.NUMBER);
        put("java.sql.Date", JavaTypes.DATE);
        put("java.sql.Time", JavaTypes.TIME);
        put("java.sql.Timestamp", JavaTypes.DATETIME);
        //Added the Other types for the Nosql data type
        put("java.sql.Struct", JavaTypes.STRING);
        put("java.sql.Array", JavaTypes.STRING);
        put("java.sql.SQLXML", JavaTypes.STRING);
        put("java.sql.RowId", JavaTypes.STRING);
        put("java.sql.Blob", JavaTypes.STRING);

        put("java.lang.Object", JavaTypes.OTHER);
    }};
    /**
     * Finds the enum type based on the provided full class name.
     * @param fullClassName 		 full class name for which the enum type is to be found.
     * @return The JavaTypes enum value corresponding to the provided full class name.
     * @throws MalformedJsonException If the provided Java data type is not valid.
     */
    public static JavaTypes findEnumType(String fullClassName) {
        if (types.containsKey(fullClassName)) {
            return types.get(fullClassName);
        } else {
            throw new MalformedJsonException("The java data type sent as part of the filters is not valid.");
        }
    }
}
