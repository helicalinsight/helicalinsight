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

import com.helicalinsight.datasource.managed.jaxb.*;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.*;
import com.helicalinsight.resourcesecurity.jaxb.Security;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

/**
 * Created by author on 20-07-2015.
 *
 * @author Rajasekhar
 */
public class SharedInformationRetrievalComponent extends ShareRuleXmlUpdateHandler implements IComponent {

    @Override
    public String executeComponent(String jsonFormData) {
        JSONObject formData = JSONObject.fromObject(jsonFormData);

        String file = formData.optString("file");
        String type = formData.getString("type");
        String dir = formData.optString("dir");


        String solutionDirectory = PROPERTIES.getSolutionDirectory();
        JSONObject data = new JSONObject();

        if ("folder".equals(type)) {
            String folderFileExtension = JsonUtils.getFolderFileExtension();

            File indexFile = new File(solutionDirectory + File.separator + dir + File.separator +
                    NAME + folderFileExtension);

            String jaxBClassForKey = SecurityUtils.jaxBClassForKey(folderFileExtension);

            if (!indexFile.exists()) {
                throw new EfwServiceException("The directory specified can't be shared as it is " + "a public " +
                        "resource.");
            }

            Class<IResource> iResourceClass = ApplicationUtilities.resourceJaxBType(jaxBClassForKey);

            addData(type, data, indexFile, iResourceClass);
        } else if ("file".equals(type)) {
            File resource = new File(solutionDirectory + File.separator + dir + File.separator +
                    file);

            if (!resource.exists()) {
                throw new EfwServiceException("Can't share a file that does not exist. Illegal " + "Request.");
            }

            //Actual extension
            String extensionOfFile = FileUtils.getExtensionOfFile(resource);

            //Reference key
            String extensionKey = SettingXmlUtility.fileExtensionKey(extensionOfFile);

            //Keys class
            String jaxBClassForKey = SecurityUtils.jaxBClassForKey(extensionKey);

            Class<IResource> iResourceClass = ApplicationUtilities.resourceJaxBType(jaxBClassForKey);

            addData(type, data, resource, iResourceClass);
        } else if ("dataSource".equalsIgnoreCase(type)) {
            Integer id = formData.getInt("id");
            String classifier = formData.getString("classifier");
            Security.Share share = null;
            JaxbContexts jaxbContexts = JaxbContexts.getJaxbContexts();
            JAXBContext jaxbContext;
            try {
                if ("global".equalsIgnoreCase(classifier)) {
                    String dataSourceProvider = formData.getString("dataSourceProvider");
                    String connectionsXmlFile = JsonUtils.getGlobalConnectionsPath();

                    jaxbContext = jaxbContexts.getContextForClass(Connections.class);
                    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                    File xml = new File(connectionsXmlFile);
                    Connections connections = (Connections) unmarshaller.unmarshal(xml);

                    if ("hikari".equalsIgnoreCase(dataSourceProvider)) {
                        List<HikariProperties> list = connections.getHikariProperties();
                        for (HikariProperties dataSource : list) {
                            if (dataSource.getId() == id) {
                                share = dataSource.getShare();
                                break;
                            }
                        }

                    } else if ("none".equalsIgnoreCase(dataSourceProvider)) {
                        List<JdbcConnection> list = connections.getJdbcConnection();
                        for (JdbcConnection connection : list) {
                            if (connection.getId() == id) {
                                share = connection.getShare();
                                break;
                            }
                        }

                    } else if ("tomcat".equalsIgnoreCase(dataSourceProvider)) {
                        List<TomcatPoolProperties> list = connections.getTomcatPoolProperties();
                        for (TomcatPoolProperties dataSource : list) {
                            if (dataSource.getId() == id) {
                                share = dataSource.getShare();
                                break;
                            }
                        }

                    } else if ("jndi".equalsIgnoreCase(dataSourceProvider)) {
                        List<JndiDataSource> list = connections.getJndiDataSource();
                        for (JndiDataSource dataSource : list) {
                            if (dataSource.getId() == id) {
                                share = dataSource.getShare();
                                break;
                            }
                        }

                    }


                } else if ("efwd".equalsIgnoreCase(classifier)) {

                    File efwdDir = new File(solutionDirectory + File.separator + dir);
                    final String efwdExtension = JsonUtils.getEfwdExtension();
                    File[] files = efwdDir.listFiles(new FilenameFilter() {
                        public boolean accept(File dir, String name) {
                            return name.toLowerCase().endsWith("." + efwdExtension);
                        }
                    });
                    file = files[0].getName();
                    File efwdFile = new File(solutionDirectory + File.separator + dir + File.separator + file);
                    jaxbContext = jaxbContexts.getContextForClass(EFWD.class);
                    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

                    EFWD efwd = (EFWD) unmarshaller.unmarshal(efwdFile);
                    List<Connection> dataSources = efwd.getDataSources().getConnectionList();
                    for (Connection connection : dataSources) {
                        if (connection.getId().equalsIgnoreCase(String.valueOf(id))) {
                            share = connection.getShare();
                            break;
                        }
                    }
                }
            } catch (JAXBException jax) {
                jax.printStackTrace();
            }

            makeShare(type, data, share);

        } else {
            throw new IllegalArgumentException("The type parameter values should be either file " + "or folder.");
        }

        return data.toString();
    }

    private void addData(String type, JSONObject data, File indexFile, Class<IResource> iResourceClass) {
        IResource resource = JaxbUtils.unMarshal(iResourceClass, indexFile);
        if (resource == null) {
            //We will never reach here
            throw new IllegalStateException("The selected " + type + " resource exists but the " +
                    "object is null");
        }

        Security.Share share = resource.getShare();
        makeShare(type, data, share);
    }

    private void makeShare(String type, JSONObject data, Security.Share share) {
        if (share == null) {
            data.accumulate("message", "The selected " + type + " is not shared with other " +
                    "users/roles");
        } else {
            addUsers(data, share);
            addRoles(data, share);
        }
    }


    private void addUsers(JSONObject data, Security.Share share) {
        Security.Users users = share.getUsers();
        if (users != null) {
            List<Security.User> list = users.getUsers();
            if (list != null) {
                JSONArray userJson = new JSONArray();
                for (Security.User userObject : list) {
                    JSONObject json = new JSONObject();
                    if (userObject.getId() != null) {
                        json.accumulate("id", userObject.getId());
                    } else {
                        String name = userObject.getName();
                        if (name != null) {
                            json.accumulate("name", name);
                        }
                    }
                    json.accumulate("permission", userObject.getPermission());
                    userJson.add(json);
                }
                data.accumulate("user", userJson);
            }
        }
    }

    private void addRoles(JSONObject data, Security.Share share) {
        Security.Roles roles = share.getRoles();
        if (roles != null) {
            List<Security.Role> list = roles.getRoles();
            if (list != null) {
                JSONArray roleJson = new JSONArray();
                for (Security.Role roleObject : list) {
                    JSONObject json = new JSONObject();
                    if (roleObject.getId() != null) {
                        json.accumulate("id", roleObject.getId());
                    } else {
                        String name = roleObject.getName();
                        if (name != null) {
                            json.accumulate("name", name);
                        }
                    }
                    json.accumulate("permission", roleObject.getPermission());
                    roleJson.add(json);
                }
                data.accumulate("role", roleJson);
            }
        }
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
