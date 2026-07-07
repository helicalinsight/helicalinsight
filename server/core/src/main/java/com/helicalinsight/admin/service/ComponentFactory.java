package com.helicalinsight.admin.service;

import com.helicalinsight.efw.framework.FactoryMethodWrapper;
import com.helicalinsight.efw.serviceframework.IComponent;
import org.springframework.stereotype.Component;

@Component
public class ComponentFactory {

    public static IComponent getComponentInstance(String clazzName) {
        IComponent iComponent = FactoryMethodWrapper.getTypedInstance(clazzName, IComponent.class);
        return iComponent;
    }
}