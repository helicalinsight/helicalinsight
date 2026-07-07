package com.helicalinsight.datasource;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helicalinsight.efw.components.DsTypeHandlerFactory;
import com.helicalinsight.efw.components.EfwdDataSourceHandler;
import org.jetbrains.annotations.NotNull;

/**
 * GlobalJdbcTypeUtils
 * Utility class used to handle global JDBC data source types.
 * Created by user on 11/27/2015.
 * @author Rajasekhar
 */
public class GlobalJdbcTypeUtils {
	
	/**
	 * isTypeGlobal(String type)
	 * @param type        data Source type
	 * @return {@code true} if type equals any one of the Data source, otherwise {@code false}.
	 */
    public static boolean isTypeGlobal(String type) {
        if (type == null) {
            throw new IllegalArgumentException("Type is null");
        }

        return (type.equalsIgnoreCase(GlobalJdbcType.TYPE) || type.equalsIgnoreCase(GlobalJdbcType
                .DYNAMIC_DATASOURCE) || type.equalsIgnoreCase(GlobalJdbcType.NON_POOLED) || type.equalsIgnoreCase
                (GlobalJdbcType.STATIC_DATASOURCE) || type.equalsIgnoreCase(GlobalJdbcType.NOSQL_DATASOURCE) || isManagedGroovyDataSource(type));
    }
    /**
     * isJustGlobal(String type)
     * @param type         data source type
     * @return {@code true} if type equals any one of the Data source, otherwise {@code false}.
     */
    public static boolean isJustGlobal(String type) {
        if (type == null) {
            throw new IllegalArgumentException("Type is null");
        }

        return (type.equalsIgnoreCase(GlobalJdbcType.TYPE) || type.equalsIgnoreCase(GlobalJdbcType
                .DYNAMIC_DATASOURCE) || type.equalsIgnoreCase(GlobalJdbcType.NON_POOLED) || type.equalsIgnoreCase
                (GlobalJdbcType.STATIC_DATASOURCE) || type.equalsIgnoreCase(GlobalJdbcType.NOSQL_DATASOURCE));
    }
    /**
     * isGroovyDataSource(String connectionType)
     * @param connectionType       groovy data source
     * @return {@code true} if connectionType equals, {@code false} otherwise.
     */
    public static boolean isGroovyDataSource(String connectionType) {
        return GlobalJdbcType.GROOVY_DATASOURCE.equals(connectionType);
    }
    /**
     * isPlainDataSource(String connectionType)
     * @param connectionType          data source type
     * @return {@code true} if connectionType equals,{@code false} otherwise.
     */
    public static boolean isPlainDataSource(String connectionType) {
    	return GlobalJdbcType.PLAIN_JDBC.equals(connectionType);
    }
    /**
     * isTypeGroovy(String connectionType)
     * @param connectionType       data source type
     * @return {@code true} if connectionType equals any one condition,{@code false} otherwise.
     */
    public static boolean isTypeGroovy(String connectionType) {
        return isGroovyDataSource(connectionType) || isManagedGroovyDataSource(connectionType);
    }
    /**
     * isManagedGroovyDataSource(String connectionType)
     * @param connectionType        data source type
     * @return {@code true} if connectionType equals,{@code false} otherwise.
     */
    public static boolean isManagedGroovyDataSource(String connectionType) {
        return GlobalJdbcType.MANAGED_GROOVY_DATASOURCE.equals(connectionType);
    }
    /**
     * isGlobalJdbc(String connectionType)
     * @param connectionType           data source type
     * @return {@code true} if connectionType equals with global.jdbc ,{@code false} otherwise.
     */
    public static boolean isGlobalJdbc(String connectionType) {
        return GlobalJdbcType.TYPE.equals(connectionType);
    }
    /**
     * checkOtherConnections(String type) 
     * @param type					data source type
     * @return {@code true} if connectionType equals any one ,{@code false} otherwise.
     */
    public static boolean checkOtherConnections(String type) {
        if (type == null) {
            throw new IllegalArgumentException("Type is null");
        }
        return (isManagedGroovyDataSource(type) || isTypeGroovy(type) || isPlainDataSource(type));
    }
    /**
     * getSwitchedConnection(String connectionId,String connectionType)
     * @param connectionId			id to get connection details
     * @param connectionType        data source type
     * @return ObjectNode object.
     */
    @NotNull
    public static ObjectNode getSwitchedConnection(String connectionId,String connectionType) {

        EfwdDataSourceHandler handler = DsTypeHandlerFactory.handler(connectionType);
    	return  handler.readDS(connectionId, connectionType);
    }

}
