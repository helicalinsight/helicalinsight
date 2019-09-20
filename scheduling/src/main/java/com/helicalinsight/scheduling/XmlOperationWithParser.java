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

package com.helicalinsight.scheduling;

import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.XmlUtils;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * XmlOperationWithParser is use to do xml Operation using dom parser
 *
 * @author Prashansa
 */
public class XmlOperationWithParser {
    private static final Logger logger = LoggerFactory.getLogger(XmlOperationWithParser.class);

    /**
     * <p>
     * addNewJobInExistingXML() is responsible to add new schedule tag or add
     * new job in existing xml.
     * </p>
     *
     * @param newData a <code>JSONObject</code>
     * @param path    a <code>String</code> specify path of scheduling.xml
     * @param id      a <code>int</code> specify id
     */
    public String addNewJobInExistingXML(net.sf.json.JSONObject newData, String path, int id) {
        List<String> key;
        key = JsonUtils.listKeys(newData);

        String data;
        String theId = String.valueOf(id);

        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document document = docBuilder.parse(path);
            Node schedules = document.getElementsByTagName("Schedules").item(0);
            Element schedule = document.createElement("Schedule");
            Element SchedulingJob = document.createElement("SchedulingJob");
            Element Security = document.createElement("security");

            Element LastExecutedOn = document.createElement("LastExecutedOn");
            Element LastExecutionStatus = document.createElement("LastExecutionStatus");
            Element NextExecutionOn = document.createElement("NextExecutionOn");
            Element NoOfExecutions = document.createElement("NoOfExecutions");

            schedule.setAttribute("id", theId);
            SchedulingJob.setAttribute("type", newData.getString("scheduleType"));
            for (int count = 0; count < key.size(); count++) {
                Element newTag = document.createElement(key.get(count));
                if (key.get(count).equalsIgnoreCase("ScheduleOptions")) {
                    data = newData.getString(key.get(count));
                    newTag.appendChild(document.createCDATASection(data));
                    schedule.appendChild(newTag);
                } else if (key.get(count).equalsIgnoreCase("EmailSettings")) {
                    String Formats;
                    String Recipients;
                    String Subject = "";
                    String Body = "";
                    String Zip;
                    Element FormatsTag = document.createElement("Formats");
                    Formats = newData.getJSONObject("EmailSettings").getString("Formats");
                    FormatsTag.appendChild(document.createCDATASection(Formats));
                    Element RecipientsTag = document.createElement("Recipients");
                    Recipients = newData.getJSONObject("EmailSettings").getString("Recipients");
                    RecipientsTag.appendChild(document.createCDATASection(Recipients));
                    Element SubjectTag = document.createElement("Subject");
                    if (newData.getJSONObject("EmailSettings").containsKey("Subject")) {
                        Subject = newData.getJSONObject("EmailSettings").getString("Subject");
                    }
                    SubjectTag.appendChild(document.createCDATASection(Subject));
                    Element BodyTag = document.createElement("Body");
                    if (newData.getJSONObject("EmailSettings").containsKey("Body")) {
                        Body = newData.getJSONObject("EmailSettings").getString("Body");
                    }
                    BodyTag.appendChild(document.createCDATASection(Body));

                    if (newData.getJSONObject("EmailSettings").containsKey("Zip")) {
                        Element ZipTag = document.createElement("Zip");
                        Zip = newData.getJSONObject("EmailSettings").getString("Zip");
                        ZipTag.appendChild(document.createCDATASection(Zip));
                        newTag.appendChild(ZipTag);
                    }
                    newTag.appendChild(FormatsTag);
                    newTag.appendChild(RecipientsTag);
                    newTag.appendChild(SubjectTag);
                    newTag.appendChild(BodyTag);

                    SchedulingJob.appendChild(newTag);
                    schedule.appendChild(SchedulingJob);

                } else if (key.get(count).equalsIgnoreCase("ReportDirectory") || key.get(count).equalsIgnoreCase
                        ("ReportFile") || key.get(count).equalsIgnoreCase("hwfDirectory") || key.get(count)
                        .equalsIgnoreCase("hwfFile")) {
                    data = newData.getString(key.get(count));
                    newTag.appendChild(document.createTextNode(data));
                    SchedulingJob.appendChild(newTag);
                    schedule.appendChild(SchedulingJob);
                } else if (key.get(count).equalsIgnoreCase("reportParameters") || key.get(count).equalsIgnoreCase
                        ("hwfParameters")) {
                    data = newData.getString(key.get(count));
                    newTag.appendChild(document.createCDATASection(data));
                    SchedulingJob.appendChild(newTag);
                    schedule.appendChild(SchedulingJob);
                } else if (key.get(count).equalsIgnoreCase("security")) {
                    String Username;
                    Element UserTag = document.createElement("createdBy");
                    Username = AuthenticationUtils.getUserId(newData);
                    UserTag.appendChild(document.createTextNode(Username));
                    Security.appendChild(UserTag);
                    schedule.appendChild(Security);
                } else {
                    data = newData.getString(key.get(count));
                    newTag.appendChild(document.createTextNode(data));
                    schedule.appendChild(newTag);
                    LastExecutedOn.appendChild(document.createTextNode("0"));
                    LastExecutionStatus.appendChild(document.createTextNode("0"));
                    NextExecutionOn.appendChild(document.createTextNode("0"));
                    NoOfExecutions.appendChild(document.createTextNode("0"));
                    schedule.appendChild(LastExecutedOn);
                    schedule.appendChild(LastExecutionStatus);
                    schedule.appendChild(NextExecutionOn);
                    schedule.appendChild(NoOfExecutions);
                }
            }
            schedules.appendChild(schedule);
            logger.debug("Schedules: " + schedules);
            // Write the content into xml file
            XmlUtils.transform(path, document);
        } catch (ParserConfigurationException | TransformerException | SAXException | IOException pce) {
            logger.error("Exception stack trace is ", pce);
        }
        return theId;
    }

    /**
     * addNewJobInXML() is responsible to create new xml file in given path and
     * add new job.
     *
     * @param newData a <code>JSONObject</code>
     * @param path    a <code>String</code> specify path of scheduling.xml
     */
    public void addNewJobInXML(JSONObject newData, String path) {
        List<String> keysOfNewData = JsonUtils.listKeys(newData);
        String data;
        File file = null;
        try {
            file = new File(path);
            boolean status = file.createNewFile();

            if (!status) {
                throw new RuntimeException("File is not created.");
            }


            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

            Element schedules = document.createElement("Schedules");

            Element schedule = document.createElement("Schedule");
            schedule.setAttribute("id", "1");

            Element schedule1 = document.createElement("Schedule");
            schedule1.setAttribute("id", "0");

            Element SchedulingJob = document.createElement("SchedulingJob");
            SchedulingJob.setAttribute("type", newData.getString("scheduleType"));

            Element Security = document.createElement("security");

            Element LastExecutedOn = document.createElement("LastExecutedOn");
            Element LastExecutionStatus = document.createElement("LastExecutionStatus");
            Element NextExecutionOn = document.createElement("NextExecutionOn");
            Element NoOfExecutions = document.createElement("NoOfExecutions");


            for (String key : keysOfNewData) {
                Element newTag = document.createElement(key);
                if (key.equalsIgnoreCase("ScheduleOptions")) {
                    data = newData.getString(key);
                    newTag.appendChild(document.createCDATASection(data));
                    schedule.appendChild(newTag);
                } else if (key.equalsIgnoreCase("EmailSettings")) {
                    String formats;
                    String recipients;
                    String subject;
                    String body;
                    String Zip;
                    Element formatsTag = document.createElement("Formats");
                    JSONObject emailSettings = newData.getJSONObject("EmailSettings");
                    formats = emailSettings.getString("Formats");
                    formatsTag.appendChild(document.createCDATASection(formats));
                    Element RecipientsTag = document.createElement("Recipients");
                    recipients = emailSettings.getString("Recipients");
                    RecipientsTag.appendChild(document.createCDATASection(recipients));
                    Element SubjectTag = document.createElement("Subject");
                    subject = emailSettings.getString("Subject");
                    SubjectTag.appendChild(document.createCDATASection(subject));
                    Element BodyTag = document.createElement("Body");
                    body = emailSettings.optString("Body");
                    BodyTag.appendChild(document.createCDATASection(body));
                    if (emailSettings.containsKey("zip")) {
                        Element ZipTag = document.createElement("Zip");
                        Zip = emailSettings.getString("Zip");
                        ZipTag.appendChild(document.createCDATASection(Zip));
                        newTag.appendChild(ZipTag);
                    }
                    newTag.appendChild(formatsTag);
                    newTag.appendChild(RecipientsTag);
                    newTag.appendChild(SubjectTag);
                    newTag.appendChild(BodyTag);
                    SchedulingJob.appendChild(newTag);
                    schedule.appendChild(SchedulingJob);
                } else if (key.equalsIgnoreCase("ReportDirectory") || key.equalsIgnoreCase("ReportFile") || key
                        .equalsIgnoreCase("hwfDirectory") || key.equalsIgnoreCase("hwfFile")) {
                    data = newData.getString(key);
                    newTag.appendChild(document.createTextNode(data));
                    SchedulingJob.appendChild(newTag);
                    schedule.appendChild(SchedulingJob);
                } else if (key.equalsIgnoreCase("reportParameters") || key.equalsIgnoreCase("hwfParameters")) {
                    data = newData.getString(key);
                    newTag.appendChild(document.createCDATASection(data));
                    SchedulingJob.appendChild(newTag);
                    schedule.appendChild(SchedulingJob);
                } else if (key.equalsIgnoreCase("security")) {
                    String username;
                    Element userTag = document.createElement("createdBy");

                    username = AuthenticationUtils.getUserId(newData);
                    userTag.appendChild(document.createTextNode(username));
                    Security.appendChild(userTag);
                    schedule.appendChild(Security);
                } else {
                    data = newData.getString(key);
                    newTag.appendChild(document.createTextNode(data));
                    schedule.appendChild(newTag);

                    LastExecutedOn.appendChild(document.createTextNode("0"));
                    LastExecutionStatus.appendChild(document.createTextNode("0"));
                    NextExecutionOn.appendChild(document.createTextNode("0"));
                    NoOfExecutions.appendChild(document.createTextNode("0"));
                    schedule.appendChild(LastExecutedOn);
                    schedule.appendChild(LastExecutionStatus);
                    schedule.appendChild(NextExecutionOn);
                    schedule.appendChild(NoOfExecutions);
                }
            }
            schedules.appendChild(schedule);
            schedules.appendChild(schedule1);
            document.appendChild(schedules);
            // Write the content into xml file
            XmlUtils.transform(path, document);
        } catch (ParserConfigurationException pce) {
            logger.error("ParserConfigurationException occurred", pce);
            //handle error
        } catch (TransformerException | IOException tfe) {
            logger.error("Exception stack trace is ", tfe);
        } finally {
            if (file != null && file.exists() && file.length() == 0) {
                file.delete();
            }
        }
    }

    /**
     * remove <Schedule> node from XML of id given in parameter.
     *
     * @param path a <code>String</code> specify the path of xml
     * @param id   a <Code>String</code> specify schedule id for delete.
     */
    public void removeElementFromXml(String path, String id) {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        try {
            docBuilder = docFactory.newDocumentBuilder();
            Document document = docBuilder.parse(path);
            Node schedules = document.getFirstChild();
            NodeList list = schedules.getChildNodes();
            for (int count = 0; count < list.getLength(); count++) {
                Node node = list.item(count);
                if (node.getNodeName().equals("Schedule")) {
                    NamedNodeMap attr = node.getAttributes();
                    Node nodeAttr = attr.getNamedItem("id");
                    if (nodeAttr.getTextContent().equals(id)) {
                        node.getParentNode().removeChild(node);
                    }
                }
            }

            // Write the content into xml file
            document.normalize();
            XmlUtils.transform(path, document);
            logger.debug("Data deleted from schedule xml");
        } catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
            logger.error("Exception stack trace is ", e);
        }
    }

    public boolean readXml(String path) {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        boolean hasChildNode = true;
        try {
            docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(path);
            Node schedules = doc.getFirstChild();
            hasChildNode = schedules.hasChildNodes();
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            logger.error("Exception stack trace is ", ex);
        }
        return hasChildNode;
    }

    /**
     * updateJobInExistingXML() is responsible for update existing
     * scheduling.xml
     *
     * @param newData a <code>JSONObject</code>
     * @param path    a <code>String</code> specify scheduling.xml path
     * @param id      a <code>int</code> specify id which is going to modify.
     */
    public void updateJobInExistingXML(net.sf.json.JSONObject newData, String path, int id) {
        List<String> key;
        key = JsonUtils.listKeys(newData);

        String data;
        String theId = String.valueOf(id);

        try {
            logger.debug("Start creating XML");
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document document = docBuilder.parse(path);
            Node schedules = document.getElementsByTagName("Schedules").item(0);
            Element schedule = document.createElement("Schedule");
            Element SchedulingJob = document.createElement("SchedulingJob");
            Element Security = document.createElement("security");

            schedule.setAttribute("id", theId);
            SchedulingJob.setAttribute("type", newData.getString("scheduleType"));
            for (int count = 0; count < key.size(); count++) {
                Element newTag = document.createElement(key.get(count));
                logger.debug("Creating XML Node");
                if (key.get(count).equalsIgnoreCase("ScheduleOptions")) {
                    data = newData.getString(key.get(count));
                    newTag.appendChild(document.createCDATASection(data));
                    schedule.appendChild(newTag);
                } else if (key.get(count).equalsIgnoreCase("EmailSettings")) {
                    String Formats;
                    String Recipients;
                    String Subject;
                    String Body;
                    String Zip;
                    Element FormatsTag = document.createElement("Formats");
                    Formats = newData.getJSONObject("EmailSettings").getString("Formats");
                    FormatsTag.appendChild(document.createCDATASection(Formats));
                    Element RecipientsTag = document.createElement("Recipients");
                    Recipients = newData.getJSONObject("EmailSettings").getString("Recipients");
                    RecipientsTag.appendChild(document.createCDATASection(Recipients));
                    Element SubjectTag = document.createElement("Subject");
                    Subject = newData.getJSONObject("EmailSettings").getString("Subject");
                    SubjectTag.appendChild(document.createCDATASection(Subject));
                    Element BodyTag = document.createElement("Body");
                    Body = newData.getJSONObject("EmailSettings").getString("Body");
                    BodyTag.appendChild(document.createCDATASection(Body));

                    if (newData.getJSONObject("EmailSettings").containsKey("Zip")) {
                        Element ZipTag = document.createElement("Zip");
                        Zip = newData.getJSONObject("EmailSettings").getString("Zip");
                        ZipTag.appendChild(document.createCDATASection(Zip));
                        newTag.appendChild(ZipTag);
                    }
                    newTag.appendChild(FormatsTag);
                    newTag.appendChild(RecipientsTag);
                    newTag.appendChild(SubjectTag);
                    newTag.appendChild(BodyTag);

                    SchedulingJob.appendChild(newTag);
                    schedule.appendChild(SchedulingJob);

                } else if (key.get(count).equalsIgnoreCase("ReportDirectory") || key.get(count).equalsIgnoreCase
                        ("ReportFile") || key.get(count).equalsIgnoreCase("hwfDirectory") || key.get(count)
                        .equalsIgnoreCase("hwfFile")) {
                    data = newData.getString(key.get(count));
                    newTag.appendChild(document.createTextNode(data));
                    SchedulingJob.appendChild(newTag);
                    schedule.appendChild(SchedulingJob);
                } else if (key.get(count).equalsIgnoreCase("reportParameters") || key.get(count).equalsIgnoreCase
                        ("hwfParameters")) {
                    data = newData.getString(key.get(count));
                    newTag.appendChild(document.createCDATASection(data));
                    SchedulingJob.appendChild(newTag);
                    schedule.appendChild(SchedulingJob);
                } else if (key.get(count).equalsIgnoreCase("security")) {
                    String Username;
                    Element UserTag = document.createElement("createdBy");
                    Username = AuthenticationUtils.getUserId(newData);
                    UserTag.appendChild(document.createTextNode(Username));
                    Security.appendChild(UserTag);
                    schedule.appendChild(Security);
                } else {
                    data = newData.getString(key.get(count));
                    newTag.appendChild(document.createTextNode(data));
                    schedule.appendChild(newTag);
                }
            }
            schedules.appendChild(schedule);
            logger.debug("Schedules: " + schedules);
            // write the content into xml file
            XmlUtils.transform(path, document);
        } catch (ParserConfigurationException | TransformerException | SAXException | IOException pce) {
            logger.error("Exception stack trace is ", pce);
        }
    }

    /**
     * <p>
     * updateExistingXml() method is responsible for update
     * EndAfterExecutions,NoOfExecutions node which is child node of Schedule
     * node in scheduling.xml
     * <p/>
     * </p>
     *
     * @see ScheduleJob
     */
    public void updateExistingXml(JSONObject jsonObject, String path, String id) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document document = docBuilder.parse(path);

            // Get the root element
            Node Schedules = document.getFirstChild();

            // loop the Schedules child node
            NodeList list = Schedules.getChildNodes();

            for (int i = 0; i < list.getLength(); i++) {

                Node node = list.item(i);
                NodeList nodeList = node.getChildNodes();
                if (node.getNodeName().equals("Schedule")) {
                    NamedNodeMap attr1 = node.getAttributes();
                    Node nodeAttr1 = attr1.getNamedItem("id");
                    if (nodeAttr1.getTextContent().equals(id)) {
                        for (int nodeCount = 0; nodeCount < nodeList.getLength(); nodeCount++) {
                            List<String> keyList;
                            keyList = JsonUtils.listKeys(jsonObject);
                            for (int keyCount = 0; keyCount < keyList.size(); keyCount++) {
                                if (nodeList.item(nodeCount).getNodeName().equalsIgnoreCase(keyList.get(keyCount))) {
                                    nodeList.item(nodeCount).setTextContent(jsonObject.getString(keyList.get
                                            (keyCount)));
                                }
                            }
                        }
                    }
                }
            }

            // write the content into xml file
            XmlUtils.transform(path, document);

            JSONObject jsonObject1;
            XmlOperation xmlOperation = new XmlOperation();
            jsonObject1 = xmlOperation.getParticularObject(path, id);

            if (jsonObject1.getJSONObject("ScheduleOptions").containsKey("EndAfterExecutions")) {
                String EndAfterExecutions = jsonObject1.getJSONObject("ScheduleOptions").getString
                        ("EndAfterExecutions");
                String NoOfExecutionsNoOfExecutions = jsonObject1.getString("NoOfExecutions");
                if (NoOfExecutionsNoOfExecutions.equalsIgnoreCase(EndAfterExecutions) && Integer.parseInt
                        (NoOfExecutionsNoOfExecutions) >= Integer.parseInt(EndAfterExecutions)) {
                    ScheduleProcess schedulerProcess = new ScheduleProcess();
                    schedulerProcess.delete(id);
                }
            }
        } catch (ParserConfigurationException | TransformerException | IOException | SAXException pce) {
            logger.error("Exception stack trace is ", pce);
        }
    }
}
