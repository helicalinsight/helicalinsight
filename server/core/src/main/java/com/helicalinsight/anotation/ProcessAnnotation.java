package com.helicalinsight.anotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Somen
 *         Created by helical021 on 11/21/2019.
 */
public class ProcessAnnotation {
    private static final Logger logger = LoggerFactory.getLogger(ProcessAnnotation.class);
    public static void executeAMethod(Object object, String commandString) {

        Class<?> clazz = object.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(SparkCommands.class)) {
                SparkCommands command = method.getAnnotation(SparkCommands.class);
                if (command.value().equals(commandString)) {
                    try {
                        Object invoke = method.invoke(object);
                    } catch (IllegalAccessException e) {
                        logger.error("Error occurred",e);
                    } catch (InvocationTargetException e) {
                        logger.error("Error occurred",e);
                        Throwable targetException = e.getTargetException();
                        logger.error("Error occurredTarget ",targetException);
                        throw  new RuntimeException(targetException.getMessage());
                    }
                    break;
                }
            }
        }
    }
}
