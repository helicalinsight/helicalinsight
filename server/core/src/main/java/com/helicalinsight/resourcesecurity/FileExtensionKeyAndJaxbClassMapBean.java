package com.helicalinsight.resourcesecurity;

import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by author on 09-07-2015.
 *
 * @author Rajasekhar
 */
@Component
@Scope("singleton")
public class FileExtensionKeyAndJaxbClassMapBean {

    private final Map<String, String> jaxbMap;

    public FileExtensionKeyAndJaxbClassMapBean() {
        Map<String, String> jaxbMap = new HashMap<>();

        JSONObject settingsJson = JsonUtils.getSettingsJson();

        JSONObject jaxbClasses = settingsJson.getJSONObject("jaxbClasses");

        Iterator<?> iterator = jaxbClasses.keys();

        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            jaxbMap.put(key, jaxbClasses.getString(key));
        }

        this.jaxbMap = jaxbMap;
    }

    public Map<String, String> resourceKeyClassMap() {
        return Collections.unmodifiableMap(this.jaxbMap);
    }
}
