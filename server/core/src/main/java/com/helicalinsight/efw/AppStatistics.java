package com.helicalinsight.efw;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * @author Somen
 *         Created by helical021 on 12/4/2017.
 */
public class AppStatistics {
    private static boolean MASTER_STARTED = false;
    private static boolean WORKER_STARTED = false;
    private static boolean SPARK_STARTED = false;
    private static boolean LOCK_MASTER = false;
    private static boolean LOCK_WORKER = false;
    private static boolean LOCK_SPARK = false;
    private static String WEB_UI_PORT_MASTER;
    private static String WEB_UI_PORT_WORKER;
    private static String WEB_UI_PORT_SPARK;
    private static String RESOURCE_MEMORY;

    public static String getRESOURCE_MEMORY() {
        return RESOURCE_MEMORY;
    }

    public static void setRESOURCE_MEMORY(String RESOURCE_MEMORY) {
        AppStatistics.RESOURCE_MEMORY = RESOURCE_MEMORY;
    }

    private static String PORT_MASTER;

    public static String getPORT_MASTER() {
        return PORT_MASTER;
    }

    public static void setPORT_MASTER(String PORT_MASTER) {
        AppStatistics.PORT_MASTER = PORT_MASTER;
    }

    public static String getPORT_WORKER() {
        return PORT_WORKER;
    }

    public static void setPORT_WORKER(String PORT_WORKER) {
        AppStatistics.PORT_WORKER = PORT_WORKER;
    }

    public static String getPORT_SPARK() {
        return PORT_SPARK;
    }

    public static void setPORT_SPARK(String PORT_SPARK) {
        AppStatistics.PORT_SPARK = PORT_SPARK;
    }

    private static String PORT_WORKER;
    private static String PORT_SPARK;

    public static String getWEB_UI_PORT_MASTER() {
        return WEB_UI_PORT_MASTER;
    }

    public static void setWEB_UI_PORT_MASTER(String WEB_UI_PORT_MASTER) {
        AppStatistics.WEB_UI_PORT_MASTER = WEB_UI_PORT_MASTER;
    }

    public static String getWEB_UI_PORT_WORKER() {
        return WEB_UI_PORT_WORKER;
    }

    public static void setWEB_UI_PORT_WORKER(String WEB_UI_PORT_WORKER) {
        AppStatistics.WEB_UI_PORT_WORKER = WEB_UI_PORT_WORKER;
    }

    public static String getWEB_UI_PORT_SPARK() {
        return WEB_UI_PORT_SPARK;
    }

    public static void setWEB_UI_PORT_SPARK(String WEB_UI_PORT_SPARK) {
        AppStatistics.WEB_UI_PORT_SPARK = WEB_UI_PORT_SPARK;
    }

    private static String sparkSettingFile = ApplicationProperties.getInstance().getSystemDirectory() +
            File.separator + "Admin" + File.separator + "sparkStatstics.json";

    private static JsonObject contentJson = new JsonObject();

    private static void writeToFile() {
        try {

            FileUtils.write(new File(sparkSettingFile), contentJson.toString(), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String readFile() {
        try {

            return FileUtils.readFileToString(new File(sparkSettingFile), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isMASTER_STARTED() {
        return MASTER_STARTED;
    }

    public static void setMASTER_STARTED(boolean MASTER_STARTED) {
        contentJson.addProperty("masterStarted", AppStatistics.MASTER_STARTED);
        writeToFile();

        AppStatistics.MASTER_STARTED = MASTER_STARTED;
    }

    public static boolean isWORKER_STARTED() {
        return WORKER_STARTED;
    }

    public static void setWORKER_STARTED(boolean WORKER_STARTED) {
        AppStatistics.WORKER_STARTED = WORKER_STARTED;
        contentJson.addProperty("workerStarted", AppStatistics.WORKER_STARTED);
        writeToFile();
    }

    public static boolean isSPARK_STARTED() {
        return SPARK_STARTED;
    }

    public static void setSPARK_STARTED(boolean SPARK_STARTED) {
        AppStatistics.SPARK_STARTED = SPARK_STARTED;
        contentJson.addProperty("sparkStarted", AppStatistics.SPARK_STARTED);
        writeToFile();
    }


    public static void addConfiguration(String key, String value, Boolean shouldWrite) {
        Map<String, String> configMap = new HashMap<>();
        configMap.put(key, value);
        Gson gson = new Gson();
        String config = gson.toJson(configMap);
        JsonObject object = gson.fromJson(config, JsonObject.class); 
        contentJson.add("config", object);
        if (shouldWrite)
            writeToFile();
    }

    public static boolean isLOCK_MASTER() {
        return LOCK_MASTER;
    }

    public static void setLOCK_MASTER(boolean LOCK_MASTER) {
        AppStatistics.LOCK_MASTER = LOCK_MASTER;
    }

    public static boolean isLOCK_WORKER() {
        return LOCK_WORKER;
    }

    public static void setLOCK_WORKER(boolean LOCK_WORKER) {
        AppStatistics.LOCK_WORKER = LOCK_WORKER;
    }

    public static boolean isLOCK_SPARK() {
        return LOCK_SPARK;
    }

    public static void setLOCK_SPARK(boolean LOCK_SPARK) {
        AppStatistics.LOCK_SPARK = LOCK_SPARK;
    }


    public static boolean getAllStatus() {
        return WORKER_STARTED && SPARK_STARTED && MASTER_STARTED;
    }
}
