package com.helicalinsight.efw.components;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.utility.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

/**
 * Created by Helical on 6/11/2021.
 */
@Controller
@Component
@DependsOn("applicationContextAccessor")
public class DataSourceMigrateController implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceMigrateController.class);

    @Autowired
    @Qualifier(value = "userDetailsService")
    private UserService userService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        JsonArray jsonArray = JsonUtils.newLoadOrder();
        if (jsonArray == null) {
            logger.warn("loadOrder is not configured; skipping application value initializers");
        } else {
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                String beanName = GsonUtility.optString(jsonObject, "name");
                if (!StringUtils.hasText(beanName)) {
                    logger.warn("Skipping loadOrder entry at index {} because bean name is blank", i);
                    continue;
                }
                try {
                    ApplicationValuesInitializer applicationValuesInitializer =
                            (ApplicationValuesInitializer) applicationContext.getBean(beanName);
                    applicationValuesInitializer.initializeData(applicationContext);
                } catch (Exception ex) {
                    logger.warn("Ignoring failed loadOrder bean '{}': {}", beanName, ex.getMessage());
                    logger.debug("loadOrder bean '{}' failure details", beanName, ex);
                }
            }
        }
        userService.initializeData();
    }

}
