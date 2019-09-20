/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.efw.components;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.DuplicateDatasourceConnectionException;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.SettingXmlUtility;
import com.helicalinsight.efw.utility.XmlUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
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
    private final ApplicationProperties applicationProperties;

    public EfwdWriterUtility(String name, String typeOfDataSource, String directoryName, String driver, String url,
                             String userName, String password) {
        this.name = name;
        this.typeOfDataSource = typeOfDataSource;
        this.directoryName = directoryName;
        this.driver = driver;
        this.url = url;
        this.userName = userName;
        this.password = password;
        applicationProperties = ApplicationProperties.getInstance();
    }

    public void write() {
        List<String> listOfDataSources = SettingXmlUtility.getListOfDataSources();
        if (!listOfDataSources.contains(this.typeOfDataSource)) {
            throw new IllegalArgumentException(String.format("The data source type is not valid" +
                    ". " + "Please check for typos."));
        }
        JSONObject settingsJson = JsonUtils.getSettingsJson();
        String efwdExtension = settingsJson.getJSONObject("Extentions").getString("efwd");
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
                pass.appendChild(document.createTextNode(password));
                connection.appendChild(pass);

                Element security = XmlUtils.appendSecurity(document);
                connection.appendChild(security);

                XmlUtils.transform(efwdFile.toString(), document);
            }
        } catch (TransformerException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void append(String efwdFileName, String directoryPath) {
        JSONObject fileAsJson;
        File resource = new File(directoryPath + File.separator +
                efwdFileName);
        JSONArray dataSources;
        try {
            fileAsJson = JsonUtils.getAsJson(resource);
            dataSources = fileAsJson.getJSONArray("DataSources");
        } catch (Exception ignore) {
            //Problem due to the newly created efwds with only DataSources
            fileAsJson = ResourceProcessorFactory.getIProcessor().getJSONObject(resource.toString(), true);
            dataSources = fileAsJson.getJSONArray("EFWD").getJSONArray(0);
        }

        int numberOfConnections = dataSources.size();

        List<Integer> connectionIdList = new ArrayList<>();
        int maxId;
        if (numberOfConnections > 0) {
            for (int connection = 0; connection < numberOfConnections; connection++) {
                JSONObject efwdConnection = dataSources.getJSONObject(connection);
                int id = Integer.parseInt(efwdConnection.getString("@id"));
                connectionIdList.add(id);
                if (isDuplicate(efwdConnection)) {
                    throw new DuplicateDatasourceConnectionException(String.format("Connection " +
                            "already " + "exists in %s file.", efwdFileName));
                }
            }
            maxId = Collections.max(connectionIdList) + 1;
            write(directoryPath, efwdFileName, maxId);
        }
    }

    private boolean isDuplicate(JSONObject jsonObject) {
        int size = jsonObject.size();
        for (int counter = 0; counter < size; counter++) {
            jsonObject.discard("@id");
            jsonObject.discard("@type");
            if (jsonObject.has("@name")) {
                jsonObject.discard("@name");
            }
        }

        JSONObject connection = new JSONObject();
        connection.accumulate("Driver", driver);
        connection.accumulate("Url", url);
        connection.accumulate("User", userName);
        connection.accumulate("Pass", password);
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
                pass.setTextContent(password);
                newConnection.appendChild(pass);

                Element security = XmlUtils.appendSecurity(document);
                newConnection.appendChild(security);

                Element dataSources = (Element) document.getDocumentElement().getElementsByTagName("DataSources")
                        .item(0);
                dataSources.appendChild(newConnection);
                XmlUtils.transform(efwdFile.toString(), document);
            }
        } catch (SAXException | IOException | TransformerException ex) {
            throw new RuntimeException(ex);
        }
    }
}
