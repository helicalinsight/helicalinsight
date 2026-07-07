package com.helicalinsight.resourcesecurity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.jetbrains.annotations.Nullable;

import com.helicalinsight.admin.dto.HIEfwdConnSecurityDTO;
import com.helicalinsight.admin.model.HIEfwdConnSecurity;
import com.helicalinsight.admin.utils.AuthenticationUtils;
import com.helicalinsight.admin.utils.ResourceDTOMapper;
import com.helicalinsight.datasource.managed.jaxb.Connections;
import com.helicalinsight.datasource.managed.jaxb.HikariProperties;
import com.helicalinsight.datasource.managed.jaxb.JdbcConnection;
import com.helicalinsight.datasource.managed.jaxb.JndiDataSource;
import com.helicalinsight.datasource.managed.jaxb.NoSqlProperties;
import com.helicalinsight.datasource.managed.jaxb.TomcatPoolProperties;
import com.helicalinsight.datasource.service.EFWDConnectionService;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.FileUtils;
import com.helicalinsight.efw.utility.JaxbContexts;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.SettingXmlUtility;
import com.helicalinsight.resourcesecurity.jaxb.Security;
import com.helicalinsight.resourcesecurity.jaxb.Security.Organization;
import com.helicalinsight.resourcesecurity.jaxb.Security.Organizations;
import com.helicalinsight.resourcesecurity.jaxb.Security.Role;
import com.helicalinsight.resourcesecurity.jaxb.Security.Roles;
import com.helicalinsight.resourcesecurity.jaxb.Security.User;
import com.helicalinsight.resourcesecurity.jaxb.Security.Users;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Created by author on 20-07-2015.
 *
 * @author Rajasekhar
 */
public class SharedInformationRetrievalComponent extends ShareRuleXmlUpdateHandler implements IComponent {

    @Nullable
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

                    }else if ("noSql".equalsIgnoreCase(dataSourceProvider)) {
                        List<NoSqlProperties> list = connections.getNoSqlProperties();
                        for (NoSqlProperties dataSource : list) {
                            if (dataSource.getId() == id) {
                                share = dataSource.getShare();
                                break;
                            }
                        }

                    }


                } else if ("efwd".equalsIgnoreCase(classifier)) {

                    EFWDConnectionService efwdService = ApplicationContextAccessor.getBean(EFWDConnectionService.class);
                    List<HIEfwdConnSecurityDTO>  securityInfo = efwdService.findEFWDConnectionSecurityByConnectionId(id);
                    ResourceDTOMapper mapper = ApplicationContextAccessor.getBean(ResourceDTOMapper.class);
                    Roles roles = new Roles();
                    Organizations orgs = new Organizations();
                    Users users = new Users();

                    List<Role> roleList = new ArrayList<>();
                    List<User> userList = new ArrayList<>();
                    List<Organization> orgList = new ArrayList<>();

                    for(HIEfwdConnSecurityDTO security : securityInfo) {
                        Role role = new Role();
                        Organization org = new Organization();
                        User user = new User();

                        com.helicalinsight.admin.model.Role resRole = mapper.map(security.getRoleId());
                        com.helicalinsight.admin.model.Organization resOrg = mapper.map(security.getOrgId());
                        com.helicalinsight.admin.model.User resUser = mapper.map(security.getUserId());

                        if (null != resRole) {
                            role.setId(String.valueOf(resRole.getId()));
                            role.setName(resRole.getRole_name());
                            role.setPermission(String.valueOf(security.getPermission()));
                            roleList.add(role);
                        }
                        if(null != resOrg) {
                            org.setId(String.valueOf(resOrg.getId()));
                            org.setPermission(String.valueOf(security.getPermission()));
                            orgList.add(org);
                        }
                        if(null != resUser) {
                            user.setId(String.valueOf(resUser.getId()));
                            user.setName(resUser.getUsername());
                            user.setPermission(String.valueOf(security.getPermission()));
                            userList.add(user);
                        }
                    }
                   if(!roleList.isEmpty() || !userList.isEmpty() || !orgList.isEmpty()) {
                	   share = new Security.Share();
                	   roles.setRoles(roleList);
                       users.setUsers(userList);
                       orgs.setOrganizations(orgList);
                       share.setRoles(roles);
                       share.setUsers(users);
                       share.setOrganizations(orgs);
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
        String typeMessage=type;
        if(typeMessage.equals("dataSource")){
            typeMessage="datasource";
        }
        if (share == null) {
             data.accumulate("message", "The selected " + typeMessage + " is not shared with other " +
                    "users/roles/organizations.");
        } else {
            boolean superOrganizationUser = AuthenticationUtils.isSuperOrganizationUser();
            if (superOrganizationUser) {
                addOrganizations(data, share);
            }
            addUsers(data, share);
            addRoles(data, share);
        }
    }

    private void addOrganizations(JSONObject data, Security.Share share) {
        Security.Organizations organizations = share.getOrganizations();
        if (organizations != null) {
            List<Security.Organization> list = organizations.getOrganizations();
            if (list != null) {
                JSONArray organizationJson = new JSONArray();
                for (Security.Organization organizationObject : list) {
                    JSONObject json = new JSONObject();
                    if (organizationObject.getId() != null) {
                        json.accumulate("id", Integer.valueOf(organizationObject.getId()));
                    } else {
                        json.accumulate("id", PROPERTIES.getNullValue());
                    }
                    json.accumulate("permission", Integer.valueOf(organizationObject.getPermission()));
                    organizationJson.add(json);
                }
                data.accumulate("organization", organizationJson);
            }
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
                        json.accumulate("id", Integer.valueOf(userObject.getId()));
                    } else {
                        String name = userObject.getName();
                        if (name != null) {
                            json.accumulate("name", name);
                        }
                    }
                    json.accumulate("permission", Integer.valueOf(userObject.getPermission()));
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
                        json.accumulate("id", Integer.valueOf(roleObject.getId()));
                    } else {
                        String name = roleObject.getName();
                        if (name != null) {
                            json.accumulate("name", name);
                        }
                    }
                    json.accumulate("permission", Integer.valueOf(roleObject.getPermission()));
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
