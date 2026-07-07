package com.helicalinsight.efw.components;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.DataSourceConnectionNotFoundException;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.XmlUtils;
import net.sf.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;

public class EfwdDeleteUtility {

    private static final Logger logger = LoggerFactory.getLogger(EfwdWriterUtility.class);

    private final String id;
    private final String directoryName;

    private final ApplicationProperties applicationProperties;

    public EfwdDeleteUtility(String id, String directoryName) {
        this.id = id;
        this.directoryName = directoryName;

        applicationProperties = ApplicationProperties.getInstance();
    }

    public static boolean extractEfwdFile(String efwdFileName, String directoryPath, String requestedId) {
        JsonObject fileAsJson;
        File resource = new File(directoryPath + File.separator + efwdFileName);
        JsonArray dataSources ;
        try {
            fileAsJson = JsonUtils.newGetAsJson(resource);
            dataSources = fileAsJson.getAsJsonArray("DataSources");
        } catch (Exception ignore) {
            // Problem due to the newly created efwds with only DataSources
            IProcessor processor = ResourceProcessorFactory.getIProcessor();
            fileAsJson = processor.getJsonObject(resource.toString(), true);
            //dataSources = fileAsJson.getJSONArray("EFWD").getJSONArray(0);
            JsonArray efwdJsonArray = fileAsJson.getAsJsonArray("EFWD");
            JsonArray jsonArray = JsonParser.parseString(efwdJsonArray.toString()).getAsJsonArray();

            JSONArray obtainedDS = DataSourceSecurityUtility.checkInstanceOfJsonArray(jsonArray);
            dataSources = JsonParser.parseString(obtainedDS.toString()).getAsJsonArray();;
        }

        int numberOfConnections = dataSources.size();

        boolean idFound = false;
        if (numberOfConnections > 0) {
            for (int connection = 0; connection < numberOfConnections; connection++) {
                JsonObject efwdConnection = dataSources.get(connection).getAsJsonObject();
                int id = Integer.parseInt(efwdConnection.get("id").getAsString());
                if (requestedId.equals(Integer.toString(id))) {
                    dataSources.remove(connection);
                    idFound = true;
                    break;
                }

            }

        }
        return idFound;
    }

    public static void deleteNode(String efwdFileName, String directoryPath, String requestId) {
        // <person>
        File efwdFile = new File(directoryPath + File.separator + efwdFileName);
        DocumentBuilder documentBuilder = XmlUtils.getDocumentBuilder();
        Document document =null;
        try {
            document = documentBuilder.parse(efwdFile);

	   /* NodeList nodes = document.getElementsByTagName("Connection");

	    for (int i = 0; i < nodes.getLength(); i++) {
	      Element node = (Element)nodes.item(i);
	      // <name>
	      //Element name = (Element)node.getElementsByTagName("Connection").item(0);
	     String id =node.getAttribute("id");
	     // String pName = name.getTextContent();
	      if (id.equals(requestId)) {
	        node.getParentNode().removeChild(node);

	      }
	    }*/
            Element efwd = document.getDocumentElement();
            Node dataSources = efwd.getElementsByTagName("DataSources").item(0);
            NodeList connections = dataSources.getChildNodes();
            for (int index = 0; index < connections.getLength(); index++) {
                Element connection = null;

                if (connections.item(index).getNodeType() == Node.ELEMENT_NODE) {
                    connection = (Element) connections.item(index);
                    if (connection.getAttribute("id").equals(requestId)) {
                        connection.getParentNode().removeChild(connection);
                    }
                }


            }

            XmlUtils.transform(efwdFile.toString(), document);
        } catch (SAXException | IOException | TransformerException ex) {
            throw new RuntimeException(ex);
        }

    }

    public void delete() {

        JsonObject settingsJson = JsonUtils.newGetSettingsJson();
        String efwdExtension = settingsJson.getAsJsonObject("Extentions").get("efwd").getAsString();
        String directoryPath = this.applicationProperties.getSolutionDirectory() + File.separator + this.directoryName;

        File solutionDirectory = new File(directoryPath);
        if (!solutionDirectory.exists()) {
            throw new IllegalArgumentException(String.format("The directory %s does not exists.", directoryPath));
        }

        File[] files = solutionDirectory.listFiles();
        String efwdFileName = null;
        if (files != null) {
            boolean isFilePresent = false;
            for (File file : files) {
                efwdFileName = file.getName();
                if (file.getName().endsWith("." + efwdExtension)) {
                    isFilePresent = true;
                    break;
                }
            }
            if (!isFilePresent) {
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Efwd file is not present in %s. " + directoryName));
                }
                throw new IllegalArgumentException(String.format("The file %s does not exists.", directoryPath));
            } else {
                delete(efwdFileName, directoryPath, this.id);
            }
        }
    }

    public void delete(String efwdFileName, String directoryPath, String requestedId) {
        boolean idFound = extractEfwdFile(efwdFileName, directoryPath, requestedId);

        if (!idFound) {
            throw new DataSourceConnectionNotFoundException(
                    String.format("The connection id :"+ requestedId+ "  is not present in dir: %s." , directoryName));
        } else {
            deleteNode(efwdFileName, directoryPath, requestedId);
        }
    }

}
