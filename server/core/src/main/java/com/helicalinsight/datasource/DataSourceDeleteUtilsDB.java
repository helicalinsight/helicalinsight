package com.helicalinsight.datasource;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.managed.jaxb.Connections;
import com.helicalinsight.datasource.model.GlobalConnections;
import com.helicalinsight.datasource.service.GlobalConnectionService;
import com.helicalinsight.efw.components.DataSourceSecurityUtility;
import com.helicalinsight.efw.components.JndiDataSourceDB;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import org.jetbrains.annotations.NotNull;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;

/**
 * DataSourceDeleteUtilsDB Extends the {@link DataSourceDeleteUtils} class, providing additional functionality for deleting data sources
 * specifically when dealing with database-backed global connections.
 */
public class DataSourceDeleteUtilsDB extends DataSourceDeleteUtils {
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
     * marshalDelete(String dataSourceProvider, String id, @NotNull JSONObject formData,
                                String mode)
     * Performs the data source deletion operation.
     *
     * @param dataSourceProvider 	type of data source provider.
     * @param id                 	ID of the data source to delete.
     * @param formData              JSON data containing formData information.
     * @param mode               	deletion mode (e.g., "none", "jndi", "hikari").
     * @return A JSON string with the result of the data source deletion operation.
     */
    @NotNull
    public String marshalDelete(String dataSourceProvider, String id, @NotNull JsonObject formData,
                                String mode) {


        DataSourceSecurityUtility.isGlobalAccessible(id, mode);

        if(!JsonUtils.isDSTypeStorageDatabase()){
            String marshal = super.marshal(dataSourceProvider, id, formData, mode);
            return marshal;

        }
        int theId = Integer.valueOf(id);
        String edit = "";
        try {
            GlobalConnectionService globalConnectionService = ApplicationContextAccessor.getBean(GlobalConnectionService.class);
            GlobalConnections globalConnectionById = globalConnectionService.findGlobalConnectionById(theId);
            if (dataSourceProvider.equalsIgnoreCase("none")) {
                try{
                    globalConnectionService.deleteGlobalConnections(theId);
                    edit = deleteSuccess(theId);
                }catch (Exception e){
                    e.printStackTrace();
                }
            } else if (dataSourceProvider.equalsIgnoreCase("jndi")) {
                JndiDataSourceDB jndiDataSourceDB = ApplicationContextAccessor.getBean(JndiDataSourceDB.class);
                if(null!=globalConnectionById){
                    globalConnectionService.deleteGlobalConnections(theId);
                    edit = deleteSuccess(theId);
                }
            } else if (dataSourceProvider.equalsIgnoreCase("hikari")) {
                if(null!=globalConnectionById){
                    globalConnectionService.deleteGlobalConnections(theId);
                    edit = deleteSuccess(theId);
                }
            } else if (dataSourceProvider.equalsIgnoreCase("tomcat")) {
                if(null!=globalConnectionById){
                    globalConnectionService.deleteGlobalConnections(theId);
                    edit = deleteSuccess(theId);
                }
            } else if (dataSourceProvider.equalsIgnoreCase("nosql")) {
                if(null!=globalConnectionById){
                    globalConnectionService.deleteGlobalConnections(theId);
                    edit = deleteSuccess(theId);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        JsonObject requiredData = new Gson().fromJson(edit, JsonObject.class);
        requiredData.add("data", DataSourceUtils.provideData(formData, "" + theId));

        return requiredData.toString();
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
}

