package com.helicalinsight.efw.framework.utils;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;


@Component
public class ApplicationContextAccessor {

    private static ApplicationContextAccessor instance;

    @Autowired
    private ApplicationContext applicationContext;

    public static <T> T getBean(Class<T> clazz) {
        return instance.applicationContext.getBean(clazz);
    }

    public static Object getBean(String beanName) {
        return instance.applicationContext.getBean(beanName);
    }

    @PostConstruct
    private void registerInstance() {
        instance = this;
    }
}