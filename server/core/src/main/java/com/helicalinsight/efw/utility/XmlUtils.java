package com.helicalinsight.efw.utility;

import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.resourcesecurity.jaxb.Security;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by author on 04-02-2015.
 *
 * @author Rajasekhar
 * @author Muqtar
 * @author Prashansa
 */
public class XmlUtils {

    public static DocumentBuilder getDocumentBuilder() {
        DocumentBuilder documentBuilder;
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException("Could not get the document builder.", ex);
        }
        return documentBuilder;
    }

    public static void transform(String filePathAsString, Document document) throws TransformerException {
        Transformer transformer = getTransformer();
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(filePathAsString);
        transformer.transform(source, result);
    }

    public static Transformer getTransformer() throws TransformerConfigurationException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, ApplicationUtilities.getEncoding());
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "5");
        return transformer;
    }

    public static Element appendSecurity(Document document) {
        Element newTag;
        Security security = SecurityUtils.securityObject();
        newTag = document.createElement("security");
        Element createdBy = document.createElement("createdBy");
        createdBy.appendChild(document.createTextNode(security.getCreatedBy()));
        newTag.appendChild(createdBy);
        Element organization = document.createElement("organization");
        organization.appendChild(document.createTextNode(security.getOrganization()));
        newTag.appendChild(organization);
        return newTag;
    }

}
