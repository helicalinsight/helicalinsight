package com.helicalinsight.efw.components;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


/**
 * Created by Helical on 5/27/2021.
 * Author : Somen
 */
public class ModelToJsonHelper {

    public static Object toJson(Map<String, String> skeleton, Object object, List<String> excludeMethods) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objectNode1 = mapper.createObjectNode();
        if (null != object) {
            Method[] methods = object.getClass().getDeclaredMethods();


            if (excludeMethods == null) {
                excludeMethods = new ArrayList<>();
            }

            final List<String> finalExcludeMethods = excludeMethods;
            skeleton.forEach((k, v) -> {
                try {

                    Method m = object.getClass().getDeclaredMethod(v);
                    Object invokeResult = m.invoke(object);
                    objectNode1.putPOJO(k, invokeResult);
                    finalExcludeMethods.add(v);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            });

            for (Method m : methods) {
                String name = m.getName();
                if (name.startsWith("get") || name.startsWith("is") && !finalExcludeMethods.contains(name)) {
                    String propertyName = name.replaceFirst("get", "");
                    String variable = Character.toLowerCase(propertyName.charAt(0)) + propertyName.substring(1);
                    try {
                        objectNode1.putPOJO(variable, m.invoke(object));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
            return objectNode1;
        }
        return null;
    }


}
