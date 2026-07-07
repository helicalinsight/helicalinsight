package com.helicalinsight.efw.components;

import com.helicalinsight.datasource.model.GlobalConnections;
import com.helicalinsight.datasource.service.GlobalConnectionService;
import com.helicalinsight.efw.DatasourceOperations;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Helical on 6/21/2021.
 */
@Component
public class DataSourceMigrator implements ApplicationValuesInitializer {

    @Autowired
    private GlobalConnectionService globalConnectionService;

    @Override
    public void initializeData(ApplicationContext applicationContext) {
        Boolean dsTypeStorageDatabase = JsonUtils.isDSTypeStorageDatabase();
        Boolean isForceMigrate = JsonUtils.isForceUpdateEnabled();

        //Force Migration
        if (isForceMigrate) {
            List<GlobalConnections> migratedConnectionsList = globalConnectionService.migratedConnectionsList();
            if (null != migratedConnectionsList && migratedConnectionsList.size() > 0) {
                for (GlobalConnections con : migratedConnectionsList) {
                    globalConnectionService.deleteGlobalConnections(con.getGlobalId());
                }
            }
        }

        GlobalDSReaderUtility globalDSReaderUtility = ApplicationContextAccessor.getBean(GlobalDSReaderUtility.class);
        if (dsTypeStorageDatabase) {
            if (JsonUtils.isDSMigrationIsEnabled()) {
                triggerMigrationProcessForDataSource(true);
            }
        }
    }

    public Boolean triggerMigrationProcessForDataSource(Boolean isMigarateEnabled) {
        //todo step 1 check whether migration is enabled and scheduling-storage-type or not in setting.xml //MigrateFromXMLSchedulingToDatabase
        DatasourceOperations datasourceOperations = new DatasourceOperations();
        try {
            if (com.helicalinsight.efw.utility.JsonUtils.isDSTypeStorageDatabase() && isMigarateEnabled) {
               /* //todo step 2 delete all existing migrated schedule records from database
                jobParametersService.deleteAllMigratedEntries();
                scheduleService.deleteAllMigratedEntries();
                hiResourceService.deleteAllMigratedEntries();*/
                //todo step 3 create a utility to convert all xml (scheduling) to list of schedule entity
                //todo step 4 add all the list of entities in database.
                datasourceOperations.migrateDataSourceToDatabase();
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}

