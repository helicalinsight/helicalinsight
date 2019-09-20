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

package com.helicalinsight.admin.management;

import com.helicalinsight.admin.model.User;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.springframework.security.core.session.SessionRegistry;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * @author Somen
 *         Created by Somen on 10/6/2015.
 */
@SuppressWarnings("unused")
public class SystemInformationProvider implements IComponent {

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public String executeComponent(String jsonFormData) {
        JSONObject formData = JSONObject.fromObject(jsonFormData);
        String action = formData.optString("action");
        JSONObject sysInfo = new JSONObject();
        if ("system".equalsIgnoreCase(action)) {
            getSystemInformation(sysInfo);
        } else if ("memory".equalsIgnoreCase(action)) {
            getMemoryDetails(sysInfo);
        } else if ("threads".equalsIgnoreCase(action)) {
            getThreadDetails(sysInfo);
        } else if ("driver".equalsIgnoreCase(action)) {
            getDriverDetails(sysInfo);
        } else if ("users".equalsIgnoreCase(action)) {
            getAllUsers(sysInfo);
        } else {
            throw new EfwServiceException("The requested action is not valid");
        }
        return sysInfo.toString();
    }

    public void getSystemInformation(JSONObject sysInfo) {
        Properties props = System.getProperties();
        JSONObject propJson = JSONObject.fromObject(props);
        sysInfo.put("sysInfo", propJson);
    }

    private void getMemoryDetails(JSONObject sysInfo) {
        long mb = FileUtils.ONE_MB;

        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        sysInfo.put("totalMemory", (totalMemory / mb));
        long freeMemory = runtime.freeMemory();
        sysInfo.put("freeMemory", (freeMemory / mb));
        sysInfo.put("usedMemory", ((totalMemory - freeMemory) / mb));
        sysInfo.put("maxMemory", (runtime.maxMemory() / mb));
        sysInfo.put("unit", "MB");
    }

    private void getThreadDetails(JSONObject sysInfo) {
        int slNo = 1;
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            JSONObject threadJson = new JSONObject();
            threadJson.accumulate("alive", thread.isAlive());
            threadJson.accumulate("daemon", thread.isDaemon());
            threadJson.accumulate("interrupted", thread.isInterrupted());
            threadJson.accumulate("name", thread.getName());
            threadJson.accumulate("priority", thread.getPriority());
            threadJson.accumulate("state", thread.getState());
            threadJson.accumulate("id", thread.getId());
            threadJson.accumulate("threadGroupName", thread.getThreadGroup().getName());
            threadJson.accumulate("toString", thread.toString());

            threadJson.put("slNo", slNo++);
            sysInfo.accumulate("threadArray", threadJson);
        }
    }

    private void getDriverDetails(JSONObject sysInfo) {
        Enumeration<Driver> allDrivers = DriverManager.getDrivers();
        int count = 0;
        while (allDrivers.hasMoreElements()) {
            Driver driver = allDrivers.nextElement();
            JSONObject driverJson = new JSONObject();
            driverJson.accumulate("driverClass", driver.getClass());
            driverJson.accumulate("driverName", driver.getClass().getName());
            driverJson.accumulate("driverMajorVersion", driver.getMajorVersion());
            driverJson.accumulate("driverMinorVersion", driver.getMinorVersion());
            sysInfo.accumulate("driver", driverJson);
            count++;
        }
        sysInfo.accumulate("totalDrivers", count);

    }

    private void getAllUsers(JSONObject sysInfo) {
        SessionRegistry sessionRegistry = (SessionRegistry) ApplicationContextAccessor.getBean("sessionRegistry");
        List<Object> principals = sessionRegistry.getAllPrincipals();
        for (Object principal : principals) {
            if (principal instanceof User) {
                User user = ((User) principal);
                sysInfo.accumulate("users", JSONObject.fromObject(user));
            }
        }
    }
}