package com.helicalinsight.efw.serviceframework;

import com.helicalinsight.efw.exceptions.ConfigurationException;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

/**
 * Created by author on 24-02-2015.
 *
 * @author Rajasekhar
 */
@Component
public class ServicesXmlReader {

    /**
     * Returns either null or the actual service implementation class. Picks the first matching
     * class among the configuration files.
     *
     * @param services    The json of services.xml file
     * @param type        Determines the type of feature
     * @param serviceType Actual service type
     * @param service     A specific service
     * @return The specific service implementation class
     */
    @Nullable
    public JSONObject getServiceClass(@NotNull JSONObject services, String type, String serviceType, String service) {
        JSONObject serviceClass;
        if (services.has(type)) {
            serviceClass = getServiceJson(services, type, serviceType, service);

            if (serviceClass == null) {
                serviceClass = checkInImports(services, type, serviceType, service);
            }
        } else {
            serviceClass = checkInImports(services, type, serviceType, service);
        }

        return serviceClass;
    }

    @Nullable
    private JSONObject getServiceJson(@NotNull JSONObject services, String type, String serviceType, String service) {
        try {
            JSONObject typeJson = services.getJSONObject(type);
            if (!typeJson.has(serviceType)) {
                return checkInImports(services, type, serviceType, service);
            }
            JSONObject serviceTypeJson = typeJson.getJSONObject(serviceType);
            if (!serviceTypeJson.has(service)) {
                return checkInImports(services, type, serviceType, service);
            }
            JSONObject actualService = serviceTypeJson.getJSONObject(service);
            return actualService;
        } catch (Exception ignore) {
        }
        return null;
    }

    @Nullable
    private JSONObject checkInImports(@NotNull JSONObject services, String type, String serviceType,
                                      String service) {
        try {
            JSONArray importsArray;
            try {
                importsArray = services.getJSONArray("import");
            } catch (Exception e) {
                throw new EfwServiceException("The services.xml has no imports and the " + "serviceType configuration" +
                        " is not found in the existing xml.");
            }
            for (Object importedXml : importsArray) {
                JSONObject eachXml = (JSONObject) importedXml;
                String name = eachXml.getString("@name");
                if (name == null || "".equals(name) || "".equals(name.trim())) {
                    throw new ConfigurationException(String.format("The import element of " +
                            "components configuration file %s is not configured properly. The " +
                            "name is null of empty.", name));
                }
                JSONObject importedComponents = JsonUtils.getImportedXmlJson(name);
                JSONObject serviceClass = getServiceClass(importedComponents, type, serviceType, service);
                if (serviceClass != null) {
                    return serviceClass;
                }
            }
        } catch (Exception e) {
            throw new EfwServiceException(String.format("The expected type %s is not available " + "even after " +
                    "searching in importable configuration files. Check xml " +
                    "configuration. Could not produce service class object.", type), e);
        }
        return null;
    }
}