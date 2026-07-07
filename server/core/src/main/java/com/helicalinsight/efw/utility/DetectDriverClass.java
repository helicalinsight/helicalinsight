package com.helicalinsight.efw.utility;

import com.helicalinsight.efw.exceptions.OperationFailedException;
import com.helicalinsight.efw.framework.FactoryMethodWrapper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;


/**
 * @author Rajesh
 *         Created on 11/12/2018.
 */
public class DetectDriverClass {


    public static String loadGivenClass(String dbName) {

        try {
            String driverByName = findDriverByName(dbName);
            FactoryMethodWrapper.forName(driverByName);
        } catch (ClassNotFoundException e) {
            throw new OperationFailedException("The driver/plugin is not Found ");
        }
        return "The driver is found in the classpath";
    }

    public static String findDriverByName(String dbName) throws ClassNotFoundException {
        Map<String, String> propertiesMap = JdbcUrlFormatUtility.getDatabaseDriversProperty();
        Set<String> driverKeySet = propertiesMap.keySet();
        for (String eachDriverName : driverKeySet) {
            if (checkDriver(eachDriverName, dbName)) {
                return eachDriverName;
            }
        }
        throw new ClassNotFoundException("not found");
    }

    public static boolean checkDriver(String driverClassName, String dbName) {
        List<String> eachItems = Arrays.asList(dbName.split("\\s+"));
        for (String eachWord : eachItems) {
            eachWord = eachWord.trim();
            //TODO need to find out common driverClass names dynamically.
            if (!(eachWord.equals("Apache") || eachWord.equals("IBM") || eachWord.equals("Driver") || eachWord.equals("driver"))) {
                //driverClassName.contains(eachWord)
                //Pattern.compile(Pattern.quote("helicalinsight"), Pattern.CASE_INSENSITIVE).matcher(findDbName).find()
                if (Pattern.compile(Pattern.quote(eachWord), Pattern.CASE_INSENSITIVE).matcher(driverClassName).find()) {
                    return true;
                }
            }
        }
        return false;
    }
}
