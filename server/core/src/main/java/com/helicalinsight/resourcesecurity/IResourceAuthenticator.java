package com.helicalinsight.resourcesecurity;

import com.google.gson.JsonObject;
import com.helicalinsight.resourcesecurity.jaxb.Context;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Created by user on 3/8/2017.
 *
 * @author Rajasekhar
 */
public interface IResourceAuthenticator {
    boolean authenticate(@NotNull HttpServletRequest request, @Nullable Context urlContext);

    int maxPermissionOnResource(JsonObject fileAsJson);
}
