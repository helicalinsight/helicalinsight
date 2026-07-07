package com.helicalinsight.resourcesecurity;

import com.google.gson.JsonObject;
//import com.helicalinsight.resourcedb.processor.FileResourcePermissionDB;
import com.helicalinsight.resourcedb.processor.FileResourcePermissionDB;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by author on 31-07-2015.
 * <p/>
 * Produces the FileResourcePermission objects that is used to calculate the maximum
 * permission level on resources.
 *
 * @author Rajasekhar
 */
@Component
public class ResourcePermissionFactory {

    //As of now only one implementation. Pass the resource as Json
    public IResourcePermission resourcePermission(JsonObject resourceAsJson) {
        return new FileResourcePermission(resourceAsJson);
    }

    public IResourcePermission resourcePermissionDB(Map<String,Object> resourceMap){
        return new FileResourcePermissionDB(resourceMap);
    }

   /* public IResourcePermission resourcePermissionDB(Map<String,Object> resourceMap){
        return new FileResourcePermissionDB(resourceMap);
    }*/
}
