package com.helicalinsight.efw.components;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helicalinsight.admin.customauth.CipherUtils;
import com.helicalinsight.admin.dto.DsTypeDTO;
import com.helicalinsight.admin.model.EFWDConnSqlJDBC;
import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.datasource.utils.EFWDDBHandler;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.EfwdDatasourceUtils;
import com.helicalinsight.efw.utility.XmlUtils;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by author on 06-02-2015.
 *
 * @author Rajasekhar
 */
@SuppressWarnings("unused")
public class EfwdUpdateHandler implements IComponent {

    private static final Logger logger = LoggerFactory.getLogger(EfwdUpdateHandler.class);

    @NotNull
    @Override
    public String executeComponent(String formData) {
        ObjectNode formDataJson = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            formDataJson = mapper.readValue(formData, ObjectNode.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String type = "";
        String directory = "";
        String driverName = "";
        if(null != formDataJson) {
            type = formDataJson.required("type").asText();
            directory = formDataJson.required("directory").asText();
            JsonNode jsonNode = formDataJson.get("driverName");
            driverName = jsonNode!=null?jsonNode.asText():null;
        }
        EfwdDataSourceHandler handler = DsTypeHandlerFactory.handler(type);
        int id = handler.updateDS(formDataJson);

        if (logger.isDebugEnabled()) {
            logger.debug("The efwd connection is updated with the new details successfully.");
        }

        ObjectNode model = JsonNodeFactory.instance.objectNode();
        model.put("message", "The efwd connection is updated with the new details " + "successfully.");

        DataSourceUtils.addDataObj(type, directory, driverName, model, ""+id);
        formDataJson.put("prepareCache", true);
         EfwdWriter.autoTriggerCache(formDataJson, Integer.valueOf(id),true);
        return model.toString();
    }

    private void validate(String id, String type, String directory) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", id);
        parameters.put("type", type);
        parameters.put("dir", directory);
        ControllerUtils.checkForNullsAndEmptyParameters(parameters);
    }

    private String write(@NotNull File efwdFile, @NotNull String idToBeEdited, String name, String driver, String url,
                         String userName, String password, String databaseDialect, String databaseName) {
        String oldUrl = "";
        try {
            DocumentBuilder documentBuilder = XmlUtils.getDocumentBuilder();
            Document document = documentBuilder.parse(efwdFile);

            Element dataSources = (Element) document.getDocumentElement().getElementsByTagName("DataSources").item(0);

            oldUrl = edit(idToBeEdited, name, driver, url, userName, password, databaseDialect, dataSources,
                    databaseName);
            XmlUtils.transform(efwdFile.toString(), document);
        } catch (SAXException | IOException | TransformerException ex) {
            throw new RuntimeException(ex);
        }
        return oldUrl;

    }

    private String edit(@NotNull String idToBeEdited, String name, String driver, String url, String userName,
                        String password, String databaseDialect, @NotNull Element dataSources, String databaseName) {
        NodeList connections = dataSources.getElementsByTagName("Connection");
        boolean foundId = false;
        int numberOfConnections = connections.getLength();
        String oldUrl = "";
        boolean isEqual;
        for (int count = 0; count < numberOfConnections; count++) {
            Node item = connections.item(count);
            if (item.hasAttributes()) {
                NamedNodeMap attributes = item.getAttributes();
                isEqual = idToBeEdited.equals(attributes.getNamedItem("id").getTextContent());
                if (isEqual) {
                    return replace(name, driver, url, userName, password, databaseDialect, item, attributes,
                            databaseName);
                }
            }
        }
        if (!foundId) {
            throw new IllegalArgumentException(
                    String.format("The id %s could not be found in " + "the" + " efwd file.", idToBeEdited));
        }
        return "";
    }

    private String replace(String name, String driver, String url, String userName, String password,
                           String databaseDialect, @NotNull Node item, @NotNull NamedNodeMap attributes, String databaseName) {
        // Edit Name
        String oldUrl = "";
        Node node = attributes.getNamedItem("name");
        if (node == null) {
            if (name != null) {
                Element element = (Element) item;
                element.setAttribute("name", name);
                item = element;
            }
        } else {
            node.setTextContent(name);
        }
        NodeList childNodes = item.getChildNodes();
        int length = childNodes.getLength();
        for (int counter = 0; counter < length; counter++) {
            Node child = childNodes.item(counter);
            String nodeName = child.getNodeName();
            if (nodeName.equals("Driver")) {
                child.setTextContent(driver);
            }
            if (nodeName.equals("Url")) {
                oldUrl = child.getFirstChild().getNodeValue();
                child.setTextContent(url);
            }
            if (nodeName.equals("User")) {
                child.setTextContent(userName);
            }
            if (nodeName.equals("Pass")) {
                child.setTextContent(password);
            }
            if (nodeName.equals("databaseDialect")) {
                child.setTextContent(databaseDialect);
            }
            if (nodeName.equals("databaseName")) {
                child.setTextContent(databaseName);
            }
        }
        return oldUrl;
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
