package com.helicalinsight.resourcedb.processor.iresource;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;

import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.resourcesecurity.jaxb.Context;

public interface IResourceAuthenticatorDB {
    boolean authenticate(@NotNull HttpServletRequest request, @Nullable Context urlContext);


}
