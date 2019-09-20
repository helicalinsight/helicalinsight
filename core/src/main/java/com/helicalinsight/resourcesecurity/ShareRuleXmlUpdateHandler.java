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

package com.helicalinsight.resourcesecurity;

import com.helicalinsight.datasource.managed.jaxb.Connection;
import com.helicalinsight.datasource.managed.jaxb.EFWD;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.components.DataSourceSecurityUtility;
import com.helicalinsight.efw.components.GlobalXmlUpdateHandler;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.FormValidationException;
import com.helicalinsight.efw.exceptions.MalformedJsonException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.*;
import com.helicalinsight.resourcesecurity.jaxb.Security;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;

/**
 * Created by author on 08-07-2015.
 *
 * @author Rajasekhar
 */
public class ShareRuleXmlUpdateHandler implements IComponent {

    static final String NAME = "index.";

    static final ApplicationProperties PROPERTIES = ApplicationProperties.getInstance();

    public static Security.Share setShareDataSource(Security.Share share, JSONObject shareJson, JSONObject revokeJson) {


        if (share == null) {
            share = ApplicationContextAccessor.getBean(Security.Share.class);
        }
        if (shareJson != null && !shareJson.isEmpty()) {
            //Add entries into share tag
            share = new ShareTagUpdateHandler().updateShareTag(share, shareJson);
        }

        if (revokeJson != null && !revokeJson.isEmpty()) {
            //Remove existing entries from share tag
            if (shareJson != null) {
                share = new SharePermissionRevokeHandler().revokePermissions(share, revokeJson);
            } else {
                share = new SharePermissionRevokeHandler().revokePermissions(share, revokeJson);
            }
        }

        return share;
    }

    @Override
    public String executeComponent(String jsonFormData) {
        JSONObject formData = JSONObject.fromObject(jsonFormData);
        String file = formData.optString("file");
        String type = formData.getString("type");
        String dir = formData.optString("dir");


        JSONObject shareJson = null;
        if (formData.has("share")) {
            shareJson = formData.getJSONObject("share");
        }


        JSONObject revokeJson = null;
        if (formData.has("revoke")) {
            revokeJson = formData.getJSONObject("revoke");
        }


        String solutionDirectory = PROPERTIES.getSolutionDirectory();
        switch (type) {
            case "folder":
                String folderFileExtension = JsonUtils.getFolderFileExtension();

                File indexFile = new File(solutionDirectory + File.separator + dir + File.separator +
                        NAME + folderFileExtension);

                String jaxBClassForKey = SecurityUtils.jaxBClassForKey(folderFileExtension);

                if (!indexFile.exists()) {
                    throw new EfwServiceException("The directory specified can't be shared as it is " + "a public " +
                            "resource.");
                }

                update(shareJson, indexFile, jaxBClassForKey, revokeJson);
                break;
            case "file":
                updateResourceXml(dir, file, shareJson, solutionDirectory, revokeJson);
                break;
            case "dataSource":
                handleDataSource(formData, dir, shareJson, revokeJson, solutionDirectory);
                break;
            default:
                throw new IllegalArgumentException("The type parameter values should be either file " + "or folder or" +
                        " dataSource.");
        }

        JSONObject model = new JSONObject();
        model.accumulate("message", "The selected " + type + " privileges are updated " +
                "successfully.");
        return model.toString();
    }

    private void handleDataSource(JSONObject formData, String dir, JSONObject shareJson, JSONObject revokeJson,
                                  String solutionDirectory) {
        String file;
        String classifier = formData.getString("classifier");
        formData.put("share", shareJson);
        formData.put("revoke", revokeJson);
        if ("global".equalsIgnoreCase(classifier)) {
            String dataSourceProvider = formData.getString("dataSourceProvider");
            String id = formData.getString("id");
            GlobalXmlUpdateHandler.marshal(dataSourceProvider, id, formData, "share");
        } else if ("efwd".equalsIgnoreCase(classifier)) {
            try {
                JaxbContexts jaxbContexts = JaxbContexts.getJaxbContexts();
                JAXBContext jaxbContext = jaxbContexts.getContextForClass(EFWD.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                File efwdDir = new File(solutionDirectory + File.separator + dir);
                final String efwdExtension = JsonUtils.getEfwdExtension();
                File[] files = efwdDir.listFiles(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return name.toLowerCase().endsWith("." + efwdExtension);
                    }
                });
                file = files[0].getName();
                String id = formData.getString("id");
                File efwdFile = new File(solutionDirectory + File.separator + dir + File.separator + file);
                DataSourceSecurityUtility.checkEfwdPermission(id, efwdFile, DataSourceSecurityUtility.READ_WRITE);
                DocumentBuilder documentBuilder = XmlUtils.getDocumentBuilder();
                Document document = documentBuilder.newDocument();

                EFWD efwd = (EFWD) unmarshaller.unmarshal(efwdFile);
                List<Connection> dataSources = efwd.getDataSources().getConnectionList();
                for (Connection connection : dataSources) {
                    if (connection.getId().equalsIgnoreCase(id)) {
                        Security.Share share = connection.getShare();
                        connection.setShare(setShareDataSource(share, shareJson, revokeJson));
                        Marshaller marshaller = jaxbContext.createMarshaller();
                        marshaller.marshal(efwd, document);
                        Document document1 = documentBuilder.parse(efwdFile);
                        Element dataSourcesList = (Element) document.getElementsByTagName("DataSources").item(0);
                        Element toRemove = (Element) document1.getDocumentElement().getElementsByTagName
                                ("DataSources").item(0);
                        toRemove.getParentNode().removeChild(toRemove);
                        document1.getElementsByTagName("EFWD").item(0).appendChild(document1.adoptNode
                                (dataSourcesList.cloneNode(true)));
                        XmlUtils.transform(efwdFile.toString(), document1);
                        break;
                    }
                }

            } catch (JAXBException | SAXException | TransformerException | IOException exception) {
                exception.printStackTrace();

            }
        }
    }

    private void validateRequest(JSONObject shareWith, JSONObject revokeJson) {
        if (shareWith != null && shareWith.isEmpty()) {
            throw new MalformedJsonException("The parameter share json is empty");
        }

        if (revokeJson != null && revokeJson.isEmpty()) {
            throw new MalformedJsonException("The parameter revoke json is empty");
        }

        if (shareWith == null && revokeJson == null) {
            throw new MalformedJsonException("The parameters share/revoke both are empty. " +
                    "Invalid" + " request.");
        }

        try {
            ShareJsonValidator shareJsonValidator = new ShareJsonValidator();
            if (shareWith != null) {
                validate(shareWith, shareJsonValidator);
            }

            if (revokeJson != null) {
                validate(revokeJson, shareJsonValidator);
            }
        } catch (Exception ex) {
            throw new MalformedJsonException("The parameter share/revoke is expected to have " +
                    "user" + " or role. Json is malformed.", ex);
        }
    }

    private void update(JSONObject shareJson, File resource, String jaxBClassForKey, JSONObject revokeJson) {
        Class<IResource> iResourceClass = ApplicationUtilities.resourceJaxBType(jaxBClassForKey);

        synchronized (this) {
            IResource iResource = JaxbUtils.unMarshal(iResourceClass, resource);
            if (iResource == null) {
                throw new IllegalStateException("Resource exists but its object is null.");
            }

            Security.Share share = null;

            Security.Share shareObjectFromXml = iResource.getShare();
            if (shareJson != null) {
                //Add entries into share tag
                share = new ShareTagUpdateHandler().updateShareTag(shareObjectFromXml, shareJson);
            }

            if (revokeJson != null) {
                //Remove existing entries from share tag
                if (shareJson != null) {
                    share = new SharePermissionRevokeHandler().revokePermissions(share, revokeJson);
                } else {
                    share = new SharePermissionRevokeHandler().revokePermissions(shareObjectFromXml, revokeJson);
                }
            }

            if (share != null) {
                iResource.setShare(share);
                if(share.getRoles()==null && share.getUsers()==null){
                    iResource.setShare(null);
                }
                synchronized (this) {
                    JaxbUtils.marshal(iResource, resource);
                }
            }
        }
    }

    private void updateResourceXml(String dir, String file, JSONObject shareJson, String solutionDirectory,
                                   JSONObject revokeJson) {
        File resource = new File(solutionDirectory + File.separator + dir + File.separator +
                file);

        if (!resource.exists()) {
            throw new EfwServiceException("Can't share a file that does not exist. Illegal " + "Request.");
        }

        String extensionOfFile = FileUtils.getExtensionOfFile(resource);

        String extensionKey = SettingXmlUtility.fileExtensionKey(extensionOfFile);

        String jaxBClassForKey = SecurityUtils.jaxBClassForKey(extensionKey);

        update(shareJson, resource, jaxBClassForKey, revokeJson);
    }

    private void validate(JSONObject json, ShareJsonValidator validator) {


        if (json.has("user")) {
            JSONArray userArray = json.getJSONArray("user");
            validate(userArray);
        }

        if (json.has("role")) {
            JSONArray roleArray = json.getJSONArray("role");
            validate(roleArray);
        }

        //Validate whether the requested data is present in the database
        validator.validate(json);
    }

    private void validate(JSONArray array) {
        if (array.isEmpty()) {
            throw new FormValidationException("Json has empty array. Invalid Request.");
        }
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
