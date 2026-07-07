package com.helicalinsight.efw.components;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.utility.AutoTriggerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author Somen
 * Created by helical021 on 2/18/2019.
 */

@Component
@Scope("prototype")
public class AutoTriggerCatalogSchema implements Runnable {

    @Autowired
    private AutoTriggerUtils autoTriggerUtils;
    
    public void setData(JsonObject data) {
        autoTriggerUtils.setData(data);
    }
    
    @Override
    public void run() {
        JsonObject result = autoTriggerUtils.cacheCatalogAndSchema();
        autoTriggerUtils.cacheTablesAndColumns(result);
    }


}
