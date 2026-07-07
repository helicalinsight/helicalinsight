package com.helicalinsight.resourcedb;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceFolder;


import com.helicalinsight.admin.service.HIResourceServiceDB;

import com.helicalinsight.admin.service.ResourceTypeServiceDB;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.resourcesecurity.jaxb.Security;
import com.helicalinsight.resourcesecurity.jaxb.SubContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.helicalinsight.resourcedb.ResourceConstants.*;

/**
 * A new directory creation service is the responsibility of this handler class.
 *
 * @author Karthik
 * @version 5.0
 */
//TODO Delete this class
@Component
public class NewFolderHandlerDB {
    private static final Logger logger = LoggerFactory.getLogger(NewFolderHandlerDB.class);




    public boolean handle(List<String> sourceList, String folderToCreate) {


        return false;
    }

    //saving hi resource folder in database



}
