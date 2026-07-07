package com.helicalinsight.efw.components;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.utility.JsonUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

/**
 * Created by Helical on 6/11/2021.
 */
@Controller
@Component
@DependsOn("applicationContextAccessor")
public class DataSourceMigrateController implements ApplicationContextAware {

    @Autowired
    @Qualifier(value = "userDetailsService")
    private UserService userService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        JsonArray jsonArray = JsonUtils.newLoadOrder();
        for(int i=0;i<jsonArray.size();i++){
            JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
            String className = GsonUtility.optString(jsonObject,"name");
            ApplicationValuesInitializer applicationValuesInitializer=(ApplicationValuesInitializer)applicationContext.getBean(className);
            applicationValuesInitializer.initializeData(applicationContext);
        }
        userService.initializeData();



    }

}