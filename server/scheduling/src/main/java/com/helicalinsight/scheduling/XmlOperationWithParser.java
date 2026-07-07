package com.helicalinsight.scheduling;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.XmlUtils;
import org.apache.commons.lang.StringUtils;
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
 * 
 * XmlOperationWithParser
 * XmlOperationWithParser is use to do xml Operation using dom parser
 * This class will be not used in future.
 * xml logic will shifted to annotation
 * @author Prashansa
 * @version 1.1
 */
@Deprecated
public class XmlOperationWithParser {
    private static final Logger logger = LoggerFactory.getLogger(XmlOperationWithParser.class);

    /**
     * addNewJobInExistingXML(com.google.gson.JsonObject newData, String path, int id)
     * <p>
     * addNewJobInExistingXML() is responsible to add new schedule tag or add
     * new job in existing xml.
     * </p>
     *
     * @param newData a <code>JSONObject</code>
     * @param path    a <code>String</code> specify path of scheduling.xml
     * @param id      a <code>int</code> specify id
     */
    public String addNewJobInExistingXML(com.google.gson.JsonObject newData, String path, int id) {
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
            SchedulingJob.setAttribute("type", newData.get("scheduleType").getAsString());
            for (int count = 0; count < key.size(); count++) {
                Element newTag = document.createElement(key.get(count));
                if (key.get(count).equalsIgnoreCase("ScheduleOptions")) {
                    data = newData.get(key.get(count)).getAsString();
                    newTag.appendChild(document.createCDATASection(data));
                    schedule.appendChild(newTag);
                } else if (key.get(count).equalsIgnoreCase("EmailSettings")) {
                    String emailString = optString(newData,"EmailSettings");
                    if(StringUtils.isEmpty(emailString)){
                        continue;
                    }                                                       

                    JsonObject emailSettings = newData.getAsJsonObject("EmailSettings");
                    String Formats;
                    String Recipients;
                    String Subject = "";
                    String Body = "";
                    String Zip;
                    Element FormatsTag = document.createElement("Formats");
                    Formats = emailSettings.get("Formats").getAsString();
                    FormatsTag.appendChild(document.createCDATASection(Formats));
                    Element RecipientsTag = document.createElement("Recipients");
                    Recipients = emailSettings.get("Recipients").getAsString();
                    RecipientsTag.appendChild(document.createCDATASection(Recipients));
                    Element SubjectTag = document.createElement("Subject");
                    if (emailSettings.has("Subject")) {
                        Subject = emailSettings.get("Subject").getAsString();
                    }
                    SubjectTag.appendChild(document.createCDATASection(Subject));
                    Element BodyTag = document.createElement("Body");
                    if (emailSettings.has("Body")) {
                        Body = emailSettings.get("Body").getAsString();
                    }
                    BodyTag.appendChild(document.createCDATASection(Body));

                    if (emailSettings.has("Zip")) {
                        Element ZipTag = document.createElement("Zip");
                        Zip = emailSettings.get("Zip").getAsString();
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
                        ("ReportFile") || key.get(count).equalsIgnoreCase("hcrFile") || key.get(count).equalsIgnoreCase("hcrDirectory")) {
                    data = newData.get(key.get(count)).getAsString();
                    newTag.appendChild(document.createTextNode(data));
                    SchedulingJob.appendChild(newTag);
                    schedule.appendChild(SchedulingJob);
                } else if (key.get(count).equalsIgnoreCase("reportParameters")) {
                    data = newData.get(key.get(count)).getAsString();
                    newTag.appendChild(document.createCDATASection(data));
                    SchedulingJob.appendChild(newTag);
                    schedule.appendChild(SchedulingJob);
                } else if (key.get(count).equalsIgnoreCase("security")) {
                    String Username;
                    String Organization;
                    Element UserTag = document.createElement("createdBy");
                    Element organizationTag = document.createElement("organization");
                    final JsonObject security = newData.getAsJsonObject("security");
                    Username = security.get("createdBy").getAsString();
                    Organization = security.get("organization").getAsString();
                    organizationTag.appendChild(document.createTextNode(Organization));
                    UserTag.appendChild(document.createTextNode(Username));
                    Security.appendChild(UserTag);
                    Security.appendChild(organizationTag);
                    schedule.appendChild(Security);
                } else {
                    data = newData.get(key.get(count)).getAsString();
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

    private static String optString(JsonObject jsonObject, String key) {
		if (jsonObject != null) {
			JsonElement jsonElement = jsonObject.get(key);
			if (jsonElement != null && jsonElement.isJsonPrimitive()) {
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if (jsonPrimitive.isString()) {
					return jsonPrimitive.getAsString();
				}
			}
		}
		return null;
	}


	/**
     * addNewJobInXML(JsonObject newData, String path) 
     * It is responsible to create new xml file in given path and
     * add new job.
     *
     * @param newData a <code>JsonObject</code>
     * @param path    a <code>String</code> specify path of scheduling.xml
     */
    public void addNewJobInXML(JsonObject newData, String path) {
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
            SchedulingJob.setAttribute("type", newData.get("scheduleType").getAsString());

            Element Security = document.createElement("security");

            Element LastExecutedOn = document.createElement("LastExecutedOn");
            Element LastExecutionStatus = document.createElement("LastExecutionStatus");
            Element NextExecutionOn = document.createElement("NextExecutionOn");
            Element NoOfExecutions = document.createElement("NoOfExecutions");


            for (String key : keysOfNewData) {
                Element newTag = document.createElement(key);
                if (key.equalsIgnoreCase("ScheduleOptions")) {
                    data = newData.get(key).getAsString();
                    newTag.appendChild(document.createCDATASection(data));
                    schedule.appendChild(newTag);
                } else if (key.equalsIgnoreCase("EmailSettings")) {
                    String formats;
                    String recipients;
                    String subject;
                    String body;
                    String Zip;
                    Element formatsTag = document.createElement("Formats");
                    JsonObject emailSettings = newData.getAsJsonObject("EmailSettings");
                    formats = emailSettings.get("Formats").getAsString();
                    formatsTag.appendChild(document.createCDATASection(formats));
                    Element RecipientsTag = document.createElement("Recipients");
                    recipients = emailSettings.get("Recipients").getAsString();
                    RecipientsTag.appendChild(document.createCDATASection(recipients));
                    Element SubjectTag = document.createElement("Subject");
                    subject = emailSettings.get("Subject").getAsString();
                    SubjectTag.appendChild(document.createCDATASection(subject));
                    Element BodyTag = document.createElement("Body");
                    body = optString(emailSettings,"Body");
                    BodyTag.appendChild(document.createCDATASection(body));
                    if (emailSettings.has("zip")) {
                        Element ZipTag = document.createElement("Zip");
                        Zip = emailSettings.get("Zip").getAsString();
                        ZipTag.appendChild(document.createCDATASection(Zip));
                        newTag.appendChild(ZipTag);
                    }
                    newTag.appendChild(formatsTag);
                    newTag.appendChild(RecipientsTag);
                    newTag.appendChild(SubjectTag);
                    newTag.appendChild(BodyTag);
                    SchedulingJob.appendChild(newTag);
                    schedule.appendChild(SchedulingJob);
                } else if (key.equalsIgnoreCase("ReportDirectory") || key.equalsIgnoreCase("ReportFile")) {
                    data = newData.get(key).getAsString();
                    newTag.appendChild(document.createTextNode(data));
                    SchedulingJob.appendChild(newTag);
                    schedule.appendChild(SchedulingJob);
                } else if (key.equalsIgnoreCase("reportParameters")) {
                    data = newData.get(key).getAsString();
                    newTag.appendChild(document.createCDATASection(data));
                    SchedulingJob.appendChild(newTag);
                    schedule.appendChild(SchedulingJob);
                } else if (key.equalsIgnoreCase("security")) {
                    String username;
                    String organization;
                    Element userTag = document.createElement("createdBy");
                    Element organizationTag = document.createElement("organization");
                    final JsonObject security = newData.getAsJsonObject("security");
                    username = security.get("createdBy").getAsString();
                    organization = security.get("organization").getAsString();
                    organizationTag.appendChild(document.createTextNode(organization));
                    userTag.appendChild(document.createTextNode(username));
                    Security.appendChild(userTag);
                    Security.appendChild(organizationTag);
                    schedule.appendChild(Security);
                } else {
                    data = newData.get(key).getAsString();
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
     * removeElementFromXml(String path, String id)
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
    /**
     * readXml(String path)
     * Reads an XML file from the specified path
     * @param path			path to the XML file.
     * @return {@code true} if the root element of the XML file has child nodes, {@code false} otherwise.
     */
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
     * updateJobInExistingXML(com.google.gson.JsonObject newData, String path, int id)
     * method is responsible for update existing
     * scheduling.xml
     *
     * @param newData a <code>JSONObject</code>
     * @param path    a <code>String</code> specify scheduling.xml path
     * @param id      a <code>int</code> specify id which is going to modify.
     */
    public void updateJobInExistingXML(com.google.gson.JsonObject newData, String path, int id) {
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
            SchedulingJob.setAttribute("type", newData.get("scheduleType").getAsString());
            for (int count = 0; count < key.size(); count++) {
                Element newTag = document.createElement(key.get(count));
                logger.debug("Creating XML Node");
                if (key.get(count).equalsIgnoreCase("ScheduleOptions")) {
                    data = newData.get(key.get(count)).getAsString();
                    newTag.appendChild(document.createCDATASection(data));
                    schedule.appendChild(newTag);
                } else if (key.get(count).equalsIgnoreCase("EmailSettings")) {
                    String Formats;
                    String Recipients;
                    String Subject;
                    String Body;
                    String Zip;
                    Element FormatsTag = document.createElement("Formats");
                    Formats = newData.getAsJsonObject("EmailSettings").get("Formats").getAsString();
                    FormatsTag.appendChild(document.createCDATASection(Formats));
                    Element RecipientsTag = document.createElement("Recipients");
                    Recipients = newData.getAsJsonObject("EmailSettings").get("Recipients").getAsString();
                    RecipientsTag.appendChild(document.createCDATASection(Recipients));
                    Element SubjectTag = document.createElement("Subject");
                    Subject = newData.getAsJsonObject("EmailSettings").get("Subject").getAsString();
                    SubjectTag.appendChild(document.createCDATASection(Subject));
                    Element BodyTag = document.createElement("Body");
                    Body = newData.getAsJsonObject("EmailSettings").get("Body").getAsString();
                    BodyTag.appendChild(document.createCDATASection(Body));

                    if (newData.getAsJsonObject("EmailSettings").has("Zip")) {
                        Element ZipTag = document.createElement("Zip");
                        Zip = newData.getAsJsonObject("EmailSettings").get("Zip").getAsString();
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
                        ("ReportFile")) {
                    data = newData.get(key.get(count)).getAsString();
                    newTag.appendChild(document.createTextNode(data));
                    SchedulingJob.appendChild(newTag);
                    schedule.appendChild(SchedulingJob);
                } else if (key.get(count).equalsIgnoreCase("reportParameters")) {
                    data = newData.get(key.get(count)).getAsString();
                    newTag.appendChild(document.createCDATASection(data));
                    SchedulingJob.appendChild(newTag);
                    schedule.appendChild(SchedulingJob);
                } else if (key.get(count).equalsIgnoreCase("security")) {
                    String Username;
                    String Organization;
                    Element UserTag = document.createElement("createdBy");
                    Element organizationTag = document.createElement("organization");
                    Username = newData.getAsJsonObject("security").get("createdBy").getAsString();
                    Organization = newData.getAsJsonObject("security").get("organization").getAsString();
                    organizationTag.appendChild(document.createTextNode(Organization));
                    UserTag.appendChild(document.createTextNode(Username));
                    Security.appendChild(UserTag);
                    Security.appendChild(organizationTag);
                    schedule.appendChild(Security);
                } else {
                    data = newData.get(key.get(count)).getAsString();
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
     * updateExistingXml(JsonObject jsonObject, String path, String id)
     * <p>
     * method is responsible for update
     * EndAfterExecutions,NoOfExecutions node which is child node of Schedule
     * node in scheduling.xml
     * <p/>
     * @param jsonObject    provides list of keys 
     * @param path          path of file
     * @see ScheduleJob
     */
    public void updateExistingXml(JsonObject jsonObject, String path, String id) {
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
                                    nodeList.item(nodeCount).setTextContent(jsonObject.get(keyList.get
                                            (keyCount)).getAsString());
                                }
                            }
                        }
                    }
                }
            }

            // write the content into xml file
            XmlUtils.transform(path, document);

            JsonObject jsonObject1;
            XmlOperation xmlOperation = new XmlOperation();
            jsonObject1 = xmlOperation.getParticularObject(path, id);

            if (jsonObject1.getAsJsonObject("ScheduleOptions").has("EndAfterExecutions")) {
                String EndAfterExecutions = jsonObject1.getAsJsonObject("ScheduleOptions").get
                        ("EndAfterExecutions").getAsString();
                String NoOfExecutionsNoOfExecutions = jsonObject1.get("NoOfExecutions").getAsString();
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
