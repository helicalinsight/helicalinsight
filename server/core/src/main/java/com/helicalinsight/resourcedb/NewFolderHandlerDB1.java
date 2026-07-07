package com.helicalinsight.resourcedb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceFolder;
import com.helicalinsight.admin.model.ResourceType;

import com.helicalinsight.admin.service.HIResourceServiceDB;

import com.helicalinsight.admin.service.ResourceTypeServiceDB;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.OperationFailedException;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.FileBrowserCacheUtils;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.resourcesecurity.jaxb.EfwFolder;
import com.helicalinsight.resourcesecurity.jaxb.Security;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;
import java.util.Iterator;

/**
 * A new directory creation service is the responsibility of this handler class.
 *
 * @author Karthik
 * @version 5.0
 */
@Component
@Scope("prototype")
public class NewFolderHandlerDB1 {

    public boolean handle(){
        return true;
    }

}
