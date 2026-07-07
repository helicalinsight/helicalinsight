package com.helicalinsight.efw.jasperintegration;

/**
 * Created by author on 11/13/2019.
 *
 * @author Rajesh
 */
public class JasperUtils {
    public static Class<?> getEquivalentClass(String type) {
        Class<?> result = Object.class;
        switch (type) {
            case "text":
                result = String.class;
                break;
            case "numeric":
                result = Integer.class;
                break;
            case "boolean":
                result = Boolean.class;
                break;
            case "date":
                result = java.sql.Date.class;
                break;
            case "dateTime":
                result = java.sql.Timestamp.class;
                break;
            case "byte[]":
            	result = byte[].class;
            	break;
            default:
                result = String.class;
        }
        return result;
    }
}
