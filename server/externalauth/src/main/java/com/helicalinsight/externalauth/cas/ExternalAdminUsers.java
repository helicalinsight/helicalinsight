package com.helicalinsight.externalauth.cas;


import java.util.List;

/**
 * ExternalAdminUsers
 * this class for storing external admin user information, including a list of admin users
 * and the external organization associated with them.
 * @author Somen
 * Created on 3/8/2016.
 */
public class ExternalAdminUsers {


    private List<String> adminUsers;
    private String externalOrganization;

    /**
     * getExternalOrganization() 
     * @return  external organization name in String format
     */
    public String getExternalOrganization() {
        return externalOrganization;
    }
    /**
     * setExternalOrganization(String externalOrganization)
     * method used to set external organization name.
     */
    public void setExternalOrganization(String externalOrganization) {
        this.externalOrganization = externalOrganization;
    }
    /**
     * getAdminUsers()
     * @return The list of admin user names.
     */
    public List<String> getAdminUsers() {
        return adminUsers;
    }
    /**
     * setAdminUsers(List<String> adminUsers)
     * Set the list of admin users.
     * @param adminUsers The list of admin user names to set.
     */
    public void setAdminUsers(List<String> adminUsers) {
        this.adminUsers = adminUsers;
    }

}
