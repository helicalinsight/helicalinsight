package com.helicalinsight.efw.components;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.customauth.CipherUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.DuplicateDatasourceConnectionException;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.SettingXmlUtility;
import com.helicalinsight.efw.utility.XmlUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by author on 21-01-2015
 *
 * @author Muqtar Ahmed
 * @author Rajasekhar
 */
public class EfwdWriterUtility {

    private static final Logger logger = LoggerFactory.getLogger(EfwdWriterUtility.class);

    private final String name;
    private final String typeOfDataSource;
    private final String directoryName;
    private final String driver;
    private final String url;
    private final String userName;
    private final String password;
    private final String databaseDialect;
    private final String databaseName;
    private final ApplicationProperties applicationProperties;
    private Integer maxId = 0;

    public EfwdWriterUtility(String name, String typeOfDataSource, String directoryName, String driver, String url,
                             String userName, String password, String databaseDialect, String databaseName) {
        this.name = name;
        this.typeOfDataSource = typeOfDataSource;
        this.directoryName = directoryName;
        this.driver = driver;
        this.url = url;
        this.userName = userName;
        this.password = password;
        this.databaseDialect = databaseDialect;
        this.databaseName = databaseName;
        applicationProperties = ApplicationProperties.getInstance();
    }

    public void write() {
        List<String> listOfDataSources = SettingXmlUtility.getListOfDataSources();
        if (!listOfDataSources.contains(this.typeOfDataSource)) {
            throw new IllegalArgumentException(String.format("The data source type is not valid" +
                    ". " + "Please check for typos."));
        }
        JsonObject settingsJson = JsonUtils.newGetSettingsJson();
        String efwdExtension = settingsJson.getAsJsonObject("Extentions").get("efwd").getAsString();
        String directoryPath = this.applicationProperties.getSolutionDirectory() + File.separator +
                this.directoryName;

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
                    logger.debug(String.format("Efwd file is not present in %s. Creating one " +
                            "with DataSources " +
                            "tag.", directoryName));
                }
                createEfwd(new File(directoryPath + File.separator + System.currentTimeMillis() + "." +
                        efwdExtension));
            } else {
                append(efwdFileName, directoryPath);
            }
        }
    }

    private void createEfwd(File efwdFile) {
        try {
            synchronized (EfwdWriter.class) {
                DocumentBuilder documentBuilder = XmlUtils.getDocumentBuilder();
                Document document = documentBuilder.newDocument();
                Element rootElement = document.createElement("EFWD");
                document.appendChild(rootElement);

                Element dataSources = document.createElement("DataSources");
                rootElement.appendChild(dataSources);

                Element connection = document.createElement("Connection");
                dataSources.appendChild(connection);

                Attr connectionId = document.createAttribute("id");
                connectionId.setValue("1");
                this.maxId = 1;
                connection.setAttributeNode(connectionId);

                Attr cType = document.createAttribute("type");
                cType.setValue(typeOfDataSource);
                connection.setAttributeNode(cType);

                Attr name = document.createAttribute("name");
                name.setValue(this.name);
                connection.setAttributeNode(name);

                Element visible = document.createElement("visible");
                visible.setTextContent("true");
                connection.appendChild(visible);

                Element driverType = document.createElement("Driver");
                driverType.appendChild(document.createTextNode(driver));
                connection.appendChild(driverType);

                Element urlType = document.createElement("Url");
                urlType.appendChild(document.createTextNode(url));
                connection.appendChild(urlType);

                Element userId = document.createElement("User");
                userId.appendChild(document.createTextNode(userName));
                connection.appendChild(userId);

                Element pass = document.createElement("Pass");
                pass.appendChild(document.createTextNode(CipherUtils.encrypt(password)));
                connection.appendChild(pass);

                Element security = XmlUtils.appendSecurity(document);
                connection.appendChild(security);

                Element dbDialect = document.createElement("databaseDialect");
                dbDialect.appendChild(document.createTextNode(databaseDialect));
                connection.appendChild(dbDialect);

                Element databaseNameElement = document.createElement("databaseName");
                databaseNameElement.appendChild(document.createTextNode(databaseName));
                connection.appendChild(databaseNameElement);

                XmlUtils.transform(efwdFile.toString(), document);
            }
        } catch (TransformerException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void append(String efwdFileName, String directoryPath) {
        JsonObject fileAsJson;
        File resource = new File(directoryPath + File.separator +
                efwdFileName);
        JsonArray dataSources;
        try {
            fileAsJson = JsonUtils.newGetAsJson(resource);
            dataSources = fileAsJson.getAsJsonArray("DataSources");
        } catch (Exception ignore) {
            //Problem due to the newly created efwds with only DataSources
            IProcessor processor = ResourceProcessorFactory.getIProcessor();
            fileAsJson = processor.getJsonObject(resource.toString(), true);
            dataSources = fileAsJson.getAsJsonArray("EFWD").get(0).getAsJsonArray();
            if (dataSources == null) {
                dataSources = new JsonArray();
            }
        }

        int numberOfConnections = dataSources.size();

        List<Integer> connectionIdList = new ArrayList<>();
        int maxId;
        if (numberOfConnections > 0) {
            for (int connection = 0; connection < numberOfConnections; connection++) {
                JsonObject efwdConnection = dataSources.get(connection).getAsJsonObject();
                int id = Integer.parseInt(efwdConnection.get("id").getAsString());
                connectionIdList.add(id);
                if (isDuplicate(efwdConnection)) {
                    throw new DuplicateDatasourceConnectionException(String.format("Connection " +
                            "already " + "exists in %s file.", efwdFileName));
                }
            }
            maxId = Collections.max(connectionIdList) + 1;
            this.maxId = maxId;
            write(directoryPath, efwdFileName, maxId);
        } else {
            this.maxId = 1;
            write(directoryPath, efwdFileName, this.maxId);
        }
    }

    private boolean isDuplicate(@NotNull JsonObject jsonObject) {
        int size = jsonObject.size();
        for (int counter = 0; counter < size; counter++) {
            jsonObject.remove("id");
            jsonObject.remove("type");
            if (jsonObject.has("name")) {
                jsonObject.remove("name");
            }
        }

        JsonObject connection = new JsonObject();
        GsonUtility.accumulate(connection,"Driver", driver);
        GsonUtility.accumulate(connection,"Url", url);
        GsonUtility.accumulate(connection,"User", userName);
        GsonUtility.accumulate(connection,"Pass", CipherUtils.encrypt(password));
        GsonUtility.accumulate(connection,"databaseDialect", databaseDialect);
        GsonUtility.accumulate(connection,"databaseName", databaseName);
        return jsonObject.equals(connection);
    }

    private void write(String directoryPath, String efwdFileName, int maxId) {
        File efwdFile = new File(directoryPath + File.separator + efwdFileName);
        try {
            synchronized (EfwdWriter.class) {
                DocumentBuilder documentBuilder = XmlUtils.getDocumentBuilder();
                Document document = documentBuilder.parse(efwdFile);

                Element newConnection = document.createElement("Connection");
                newConnection.setAttribute("id", String.valueOf(maxId));
                newConnection.setAttribute("type", typeOfDataSource);
                newConnection.setAttribute("name", this.name);

                Element visible = document.createElement("visible");
                visible.setTextContent("true");
                newConnection.appendChild(visible);

                Element driverName = document.createElement("Driver");
                driverName.setTextContent(driver);
                newConnection.appendChild(driverName);

                Element Url = document.createElement("Url");
                Url.setTextContent(url);
                newConnection.appendChild(Url);

                Element user = document.createElement("User");
                user.setTextContent(userName);
                newConnection.appendChild(user);

                Element pass = document.createElement("Pass");
                pass.setTextContent(CipherUtils.encrypt(password));
                newConnection.appendChild(pass);

                Element security = XmlUtils.appendSecurity(document);
                newConnection.appendChild(security);

                Element dbDialect = document.createElement("databaseDialect");
                dbDialect.appendChild(document.createTextNode(databaseDialect));
                newConnection.appendChild(dbDialect);

                Element databaseNameElement = document.createElement("databaseName");
                databaseNameElement.appendChild(document.createTextNode(databaseName));
                newConnection.appendChild(databaseNameElement);

                Element dataSources = (Element) document.getDocumentElement().getElementsByTagName("DataSources")
                        .item(0);
                dataSources.appendChild(newConnection);
                XmlUtils.transform(efwdFile.toString(), document);
            }
        } catch (SAXException | IOException | TransformerException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Integer getMaxId() {
        return maxId;
    }
}
