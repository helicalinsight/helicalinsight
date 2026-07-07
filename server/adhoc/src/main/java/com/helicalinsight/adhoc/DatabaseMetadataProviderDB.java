/*

package com.helicalinsight.adhoc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helicalinsight.adhoc.metadata.genericdb.IMetadata;
import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;



public class DatabaseMetadataProviderDB implements IComponent {

    private final static Logger logger = LoggerFactory.getLogger(DatabaseMetadataProvider.class);

    @Override
    public String executeComponent(String jsonFormData) {

        Map<String, Object> responseJSONMap = responseJSONMap(jsonFormData);


        String metadataImplementation;

        if (logger.isDebugEnabled()) {
            logger.debug("The metadata implementation is being invoked.");
        }

        metadataImplementation = ""+responseJSONMap.get("metadataImplementation");
        IMetadata iMetadata = FactoryMethodWrapper.getTypedInstance(metadataImplementation, IMetadata.class);
        if (iMetadata == null) {
            throw new ConfigurationException("The application configuration(setting.xml) is incorrect.");
        }

        String metadata = iMetadata.getMetadataDB(responseJSONMap);
        if (logger.isDebugEnabled()) {
            logger.debug("The metadata implementation has completed processing.");
        }
        return metadata;
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public Map<String, Object> responseJSONMap(String jsonFormData) {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<HashMap<String, Object>> typeRef
                = new TypeReference<HashMap<String, Object>>() {
        };
        HashMap<String, Object> jsonFormDataMap = new HashMap<>();
        try {
            jsonFormDataMap = mapper.readValue(jsonFormData, typeRef);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return jsonFormDataMap;
    }
}

*/
