package com.helicalinsight.resourcedb.processor;

import com.helicalinsight.resourcesecurity.IResourcePermission;
import org.springframework.stereotype.Component;

import java.util.Map;

/**

 * @author Karthik
 */
@Component
public class ResourcePermissionFactoryDB {


    public IResourcePermission resourcePermissionDB(Map<String, Object> resourceMap) {
        return new FileResourcePermissionDB(resourceMap);
    }


}
