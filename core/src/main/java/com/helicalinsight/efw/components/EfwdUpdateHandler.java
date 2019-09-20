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

import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.XmlUtils;
import net.sf.json.JSONObject;
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


    @Override
    public String executeComponent(String formData) {
        JSONObject formDataJson = JSONObject.fromObject(formData);
        ControllerUtils.validate(formDataJson);
        String id = formDataJson.getString("id");
        String type = formDataJson.getString("type");
        String directory = formDataJson.getString("directory");
        validate(id, type, directory);

        File efwdFile = ApplicationUtilities.getEfwdFile(directory);


        DataSourceSecurityUtility.checkEfwdPermission(id, efwdFile, DataSourceSecurityUtility.READ_WRITE);


        String name = formDataJson.getString("name");
        String driverName = formDataJson.getString("driverName");
        String jdbcUrl = formDataJson.getString("jdbcUrl");
        String userName = formDataJson.getString("userName");
        String password = formDataJson.getString("password");
        write(efwdFile, id, name, driverName, jdbcUrl, userName, password);

        if (logger.isDebugEnabled()) {
            logger.debug("The efwd connection is updated with the new details successfully.");
        }

        JSONObject model = new JSONObject();
        model.accumulate("message", "The efwd connection is updated with the new details " + "successfully.");
        return model.toString();
    }

    private void validate(String id, String type, String directory) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", id);
        parameters.put("type", type);
        parameters.put("dir", directory);
        ControllerUtils.checkForNullsAndEmptyParameters(parameters);
    }

    private synchronized void write(File efwdFile, String idToBeEdited, String name, String driver,
                                    String url, String userName, String password) {
        try {
            DocumentBuilder documentBuilder = XmlUtils.getDocumentBuilder();
            Document document = documentBuilder.parse(efwdFile);

            Element dataSources = (Element) document.getDocumentElement().getElementsByTagName("DataSources").item(0);

            edit(idToBeEdited, name, driver, url, userName, password, dataSources);
            XmlUtils.transform(efwdFile.toString(), document);
        } catch (SAXException | IOException | TransformerException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void edit(String idToBeEdited, String name, String driver, String url, String userName,
                      String password, Element dataSources) {
        NodeList connections = dataSources.getElementsByTagName("Connection");
        boolean foundId = false;
        int numberOfConnections = connections.getLength();
        boolean isEqual;
        for (int count = 0; count < numberOfConnections; count++) {
            Node item = connections.item(count);
            if (item.hasAttributes()) {
                NamedNodeMap attributes = item.getAttributes();
                isEqual = idToBeEdited.equals(attributes.getNamedItem("id").getTextContent());
                if (isEqual) {
                    foundId = true;
                    replace(name, driver, url, userName, password, item, attributes);
                }
            }
        }
        if (!foundId) {
            throw new IllegalArgumentException(String.format("The id %s could not be found in " +
                    "the" + " efwd file.", idToBeEdited));
        }
    }

    private void replace(String name, String driver, String url, String userName, String password,
                         Node item, NamedNodeMap attributes) {
        //Edit Name
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
                child.setTextContent(url);
            }
            if (nodeName.equals("User")) {
                child.setTextContent(userName);
            }
            if (nodeName.equals("Pass")) {
                child.setTextContent(password);
            }
        }
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
