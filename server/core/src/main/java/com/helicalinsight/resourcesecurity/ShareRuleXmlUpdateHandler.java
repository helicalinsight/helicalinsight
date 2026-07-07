package com.helicalinsight.resourcesecurity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
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
import org.jetbrains.annotations.NotNull;
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

    /**
     * setShareDataSource
     * @deprecated
     * This method is no longer acceptable
     * <p>use {@link #setShareDataSource(Security.Share share, JsonObject shareJson, JsonObject revokeJson)}  instead</p>
     * @param share
     * @param shareJson
     * @param revokeJson
     * @return
     */
    @Deprecated
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
    /**
     * using gson
     * setShareDataSource(Security.Share share, JsonObject shareJson, JsonObject revokeJson)
     * @param share
     * @param shareJson
     * @param revokeJson
     * @return
     */
    public static Security.Share setShareDataSource(Security.Share share, JsonObject shareJson, JsonObject revokeJson) {


        if (share == null) {
            share = ApplicationContextAccessor.getBean(Security.Share.class);
        }
        if (shareJson != null && !shareJson.entrySet().isEmpty()) {
            //Add entries into share tag
            share = new ShareTagUpdateHandler().updateShareTag(share, shareJson);
        }

        if (revokeJson != null && !revokeJson.entrySet().isEmpty()) {
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
        JsonObject formData = new Gson().fromJson(jsonFormData,JsonObject.class);
        String file = GsonUtility.optString(formData, "file");
        String type = formData.get("type").getAsString();
        String dir = GsonUtility.optString(formData, "dir");


        JsonObject shareJson = null;
        if (formData.has("share")) {
            shareJson = formData.getAsJsonObject("share");
        }


        JsonObject revokeJson = null;
        if (formData.has("revoke")) {
            revokeJson = formData.getAsJsonObject("revoke");
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
        String typeMessage=type;
        if(typeMessage.equals("dataSource")){
            typeMessage="datasource";
        }
        model.accumulate("message", "The selected " + typeMessage + " privileges are updated " +
                "successfully.");
        return model.toString();
    }

    private void handleDataSource(JsonObject formData, String dir, JsonObject shareJson, JsonObject revokeJson,
                                  String solutionDirectory) {
        String file;
        String classifier = formData.get("classifier").getAsString();
        formData.add("share", shareJson);
        formData.add("revoke", revokeJson);
        if ("global".equalsIgnoreCase(classifier)) {
            String dataSourceProvider = formData.get("dataSourceProvider").getAsString();
            String id = formData.get("id").getAsString();
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
                String id = formData.get("id").getAsString();
                File efwdFile = new File(solutionDirectory + File.separator + dir + File.separator + file);
                DataSourceSecurityUtility.checkEfwdPermission(id, efwdFile, DataSourceSecurityUtility.OWNER);
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
            ShareJsonValidator validator = new ShareJsonValidator();
            if (shareWith != null) {
                validate(shareWith, validator);
            }

            if (revokeJson != null) {
                validate(revokeJson, validator);
            }
        } catch (Exception ex) {
            throw new MalformedJsonException("The parameter share/revoke is expected to have " +
                    "user" + " or role or organization as arrays. Json is malformed.", ex);
        }
    }

    private void update(JsonObject shareJson, File resource, String jaxBClassForKey, JsonObject revokeJson) {
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

            //if (share != null) {
            iResource.setShare(share);
            synchronized (this) {
                JaxbUtils.marshal(iResource, resource);
            }
            // }
        }
    }

    private void updateResourceXml(String dir, String file, JsonObject shareJson, String solutionDirectory,
                                   JsonObject revokeJson) {
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
        if (json.has("organization")) {
            ShareTagUpdateHandler.checkWhetherAuthenticated();
            JSONArray organizationArray = json.getJSONArray("organization");
            validate(organizationArray);
        }

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

    private void validate(@NotNull JSONArray array) {
        if (array.isEmpty()) {
            throw new FormValidationException("Json has empty array. Invalid Request.");
        }
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
