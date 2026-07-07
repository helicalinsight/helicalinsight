package com.helicalinsight.efw.components;

import com.helicalinsight.efw.utility.ConfigurationFileReader;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExecutorUtils {
    private static Map<String, String> mapFromClasspathPropertiesFile = ConfigurationFileReader.getProjectPropertiesFile();
    private static ExecutorService executorService;
    private static Map<String, Future<?>> futureMap = new HashMap<>();

    static  {
        String threshold = mapFromClasspathPropertiesFile.get("autotrigger.threadpoolthreshold");
        executorService = Executors.newFixedThreadPool(Integer.parseInt(threshold));
    }

    public static void addTask(Runnable runnable) {
        executorService.execute(runnable);
    }

}
