package com.helicalinsight.resourcesecurity;

import com.helicalinsight.resourcesecurity.jaxb.Security;

/**
 * Created by author on 09-07-2015.
 *
 * @author Rajasekhar
 */
public interface IResource {

    Security getSecurity();

    void setSecurity(Security security);

    Security.Share getShare();

    void setShare(Security.Share share);
}
