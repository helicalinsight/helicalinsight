package com.helicalinsight.datasource;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.managed.jaxb.*;
import com.helicalinsight.efw.components.DataSourceSecurityUtility;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.utility.JaxbContexts;
import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.json.JSONObject;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.Iterator;

/**
 * DataSourceDeleteUtils
 * Utility class for deleting data sources from the configuration XML.
 */
public class DataSourceDeleteUtils{

	/**
	 * saveChanges(@NotNull JAXBContext jaxbContext, File xml, Connections connections)
     * Saves the changes made to the configuration XML.
     *
     * @param jaxbContext 			JAXBContext used for marshalling.
     * @param xml        			XML file to save changes to.
     * @param connections 			Connections object containing data source details.
     * @throws JAXBException If there's an issue with JAXB marshalling.
     */
    private static void saveChanges(@NotNull JAXBContext jaxbContext, File xml, Connections connections) throws
            JAXBException {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        String charsetName = ControllerUtils.defaultCharSet();
        marshaller.setProperty(Marshaller.JAXB_ENCODING, charsetName);
        marshaller.marshal(connections, xml);
    }
    /**
     * throwException(String dataSourceProvider)
     * Throws an exception indicating that the data source could not be deleted.
     * @param dataSourceProvider 		type of data source provider.
     */
    private static void throwException(String dataSourceProvider) {
        throw new EfwServiceException(String.format("Could not delete the given data " + "source details of type %s" +
                ".", dataSourceProvider));
    }
    /**
     * validateConnection(Object connection, String dataSourceProvider)
     * Validates if a connection exists for deletion or not.
     *
     * @param connection         	connection to validate.
     * @param dataSourceProvider 	type of data source provider.
     */
    private static void validateConnection(Object connection, String dataSourceProvider) {
        if (connection == null) {
            throwException(dataSourceProvider + ". The given dataSource does not exists");
        }
    }
    /**
     * deleteSuccess(Integer maxId)
     * Generates a success message after deleting a data source.
     *
     * @param maxId 		ID of the deleted data source.
     * @return A JSON string with the success message.
     */
    private static String deleteSuccess(Integer maxId) {
        JsonObject result;
        result = new JsonObject();
        result.addProperty("message", "The datasource " + maxId + " have been deleted successfully.");
        result.addProperty("dataSourceId", maxId);
        return result.toString();
    }

    /**
     * marshal(String dataSourceProvider, String id, @NotNull JSONObject formData,
                                              String mode)
     * Marshals the data source deletion operation.
     *
     * @param dataSourceProvider 	type of data source provider.
     * @param id                 	ID of the data source to delete.
     * @param formData           	fromData
     * @param mode                  deletion mode (e.g., "none", "jndi", "hikari").
     * @return A JSON string with the result of the data source deletion operation.
     */
    @NotNull
    public static synchronized String marshal(String dataSourceProvider, String id, @NotNull JsonObject formData,
                                              String mode) {


        DataSourceSecurityUtility.validateGlobalDataSourceAccessForDeleteOperation(id, mode);

        String connectionsXmlFile = JsonUtils.getGlobalConnectionsPath();
        int theId = Integer.valueOf(id);
        String edit = "";
        try {
            JaxbContexts jaxbContexts = JaxbContexts.getJaxbContexts();
            JAXBContext jaxbContext = jaxbContexts.getContextForClass(Connections.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            File xml = new File(connectionsXmlFile);
            Connections connections = (Connections) unmarshaller.unmarshal(xml);
            if ("none".equalsIgnoreCase(dataSourceProvider)) {
                //connections.getJdbcConnection().removeIf((connections ) -> removeId.equals(connections.getId()));
                Iterator<JdbcConnection> iterator = connections.getJdbcConnection().iterator();
                validateConnection(iterator, dataSourceProvider);
                while (iterator.hasNext()) {
                    if (id.equals(Integer.toString(iterator.next().getId()))) {
                        iterator.remove();
                        edit = deleteSuccess(theId);
                        break;
                    }
                }


            } else if ("jndi".equalsIgnoreCase(dataSourceProvider)) {
                Iterator<JndiDataSource> iterator = connections.getJndiDataSource().iterator();
                validateConnection(iterator, dataSourceProvider);
                while (iterator.hasNext()) {
                    if (id.equals(Integer.toString(iterator.next().getId()))) {
                        iterator.remove();
                        edit = deleteSuccess(theId);
                        break;
                    }
                }
            } else if ("hikari".equalsIgnoreCase(dataSourceProvider)) {
                Iterator<HikariProperties> iterator = connections.getHikariProperties().iterator();
                validateConnection(iterator, dataSourceProvider);
                while (iterator.hasNext()) {
                    if (id.equals(Integer.toString(iterator.next().getId()))) {
                        iterator.remove();
                        edit = deleteSuccess(theId);
                        break;
                    }
                }
            } else if ("tomcat".equalsIgnoreCase(dataSourceProvider)) {
                Iterator<TomcatPoolProperties> iterator = connections.getTomcatPoolProperties().iterator();
                validateConnection(iterator, dataSourceProvider);
                while (iterator.hasNext()) {
                    if (id.equals(Integer.toString(iterator.next().getId()))) {
                        iterator.remove();
                        edit = deleteSuccess(theId);
                        break;
                    }
                }
            } else if ("noSql".equalsIgnoreCase(dataSourceProvider)) {
                Iterator<NoSqlProperties> iterator = connections.getNoSqlProperties().iterator();
                validateConnection(iterator, dataSourceProvider);
                while (iterator.hasNext()) {
                    if (id.equals(Integer.toString(iterator.next().getId()))) {
                        iterator.remove();
                        edit = deleteSuccess(theId);
                        break;
                    }
                }
            }

            if (!edit.isEmpty()) {
                saveChanges(jaxbContext, xml, connections);
            } else {
                throwException(dataSourceProvider);
            }
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

        JSONObject requiredData = JSONObject.fromObject(edit);
        requiredData.put("data", DataSourceUtils.provideData(formData, "" + theId));

        return requiredData.toString();
    }
}
