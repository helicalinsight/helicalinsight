package com.helicalinsight.efw.framework;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

@Component
final class RegistryBeanFactory implements FactoryBean<FrameworkObjectsRegistry> {

    @NotNull
    public FrameworkObjectsRegistry getObject() throws Exception {
        return FrameworkObjectsRegistry.FRAMEWORK_OBJECTS_REGISTRY;
    }

    @NotNull
    public Class<?> getObjectType() {
        return FrameworkObjectsRegistry.class;
    }

    public boolean isSingleton() {
        return true;
    }
}