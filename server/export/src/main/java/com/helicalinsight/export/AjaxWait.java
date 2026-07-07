package com.helicalinsight.export;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.utility.JsonUtils;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Duration;

/**
 * AjaxWait a Utility class for handling AJAX wait in WebDriver.
 * AJAX (Asynchronous JavaScript and XML) is a technique used in web development to create asynchronous web applications. 
 * 
 */
public class AjaxWait {

    private static int timeOut=0;

    /**
     * Waits for AJAX calls to complete using the provided WebDriver.
     * Selenium WebDriver refers to a mechanism used to ensure that the WebDriver waits for AJAX (asynchronous) requests to complete before moving on to the next step 
     * @param driver 			WebDriver instance to pass to the expected conditions
     */
    public static void waitForAjax(WebDriver driver) {
        if(timeOut==0) {
            JsonObject export = GsonUtility.optJsonObject(JsonUtils.newGetSettingsJson(),"export");
            if (export != null && !export.entrySet().isEmpty()) {
                Integer phantomTime = GsonUtility.optInt(export,"phantomTimeout");
                timeOut = phantomTime != 0 ? phantomTime : 600000;
            }
        }

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(timeOut));
        String scriptForExecution = getFileContent("WaitingScript.js");
        // Wait for all AJAX calls to complete
        ExpectedCondition<Boolean> ajaxIsComplete = new ExpectedCondition<>() {
            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    Boolean aBoolean = (Boolean) ((JavascriptExecutor) driver).executeScript(scriptForExecution);
                    return aBoolean;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return false;
            }
        };

        wait.until(ajaxIsComplete);
    }



    /**
     * Reads the content of a file and returns it as a String.
     *
     * @param targetFile 		name of the target file.
     * @return The content of the specified file.
     * 
     */
    public  static String getFileContent(String targetFile){

        String fileLocation= ExportUtils.getReportDirectory() + File.separator + targetFile;
        File file = new File(fileLocation);
        String scriptForExecution = null;
        try {
            scriptForExecution = FileUtils.readFileToString(file, Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return scriptForExecution;

    }

}
