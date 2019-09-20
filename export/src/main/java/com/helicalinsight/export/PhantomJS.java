/**
 *    Copyright (C) 2013-2019 Helical IT Solutions (http://www.helicalinsight.com) - All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.helicalinsight.export;

import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.JsonUtils;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The instance of this class is a separate thread which generates the pdf or
 * png or jpeg formats on the file system.
 *
 * @author Rajasekhar
 */
public class PhantomJS implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(PhantomJS.class);

    private final JSONObject printOptions;

    /**
     * The location of the phantom js binary
     */
    private String phantomLocation;

    /**
     * The file used by phantom js to create a screenshot
     */
    private String inputFileString;

    /**
     * The output of the phantom js on the file system(Temp directory)
     */
    private String destinationFileString;

    /**
     * The location of the screenshot.js file
     */
    private String scriptLocation;

    /**
     * Constructs an object of this class by setting all the required fields
     *
     * @param phantomLocation       The location of the phantom js binary
     * @param scriptLocation        The location of the screenshot.js file
     * @param inputFileString       The file used by phantom js to create a screenshot
     * @param destinationFileString The output of the phantom js on the file system(Temp
     * @param printOptions          The session id of the logged in user
     */


    public PhantomJS(String phantomLocation, String scriptLocation, String inputFileString,
                     String destinationFileString, JSONObject printOptions) {
        this.inputFileString = inputFileString;
        this.destinationFileString = destinationFileString;
        this.scriptLocation = scriptLocation;
        this.phantomLocation = phantomLocation;
        this.printOptions = printOptions;
    }


    /**
     * Starts the phantom thread
     */
    @Override
    public void run() {
        create();
    }

    /**
     * Creates an operating system process by invoking the phantom js. The
     * thread which invokes this thread will be temporarily resumed till this
     * process completes. The phantom js creates the screen shot of the html
     * from a file on the file system.
     */
    private void create() {
        /*
         * Fixed the bug 'parse error' in case the strings passed to the command
		 * line consist of url encoding.
		 */
        String encoding = this.checkEncoding();

        List<String> command = buildCommand();

        logger.info("Phantom call : " + phantomLocation + " " + scriptLocation + " " +
                inputFileString + " " + destinationFileString);
        logger.info("Phantom Command is " + command);

        this.triggerPhantomProcess(encoding, command);

        logger.info("Thread.currentThread() = " + Thread.currentThread() + " finished execution.");

    }

    private String checkEncoding() {
        String encoding = ApplicationUtilities.getEncoding();

        if (isStringURLEncoded(phantomLocation)) {
            phantomLocation = urlDecode(phantomLocation, encoding);
        }

        if (isStringURLEncoded(scriptLocation)) {
            scriptLocation = urlDecode(scriptLocation, encoding);
        }

        if (isStringURLEncoded(inputFileString)) {
            inputFileString = urlDecode(inputFileString, encoding);
        }

        if (isStringURLEncoded(destinationFileString)) {
            destinationFileString = urlDecode(destinationFileString, encoding);
        }
        return encoding;
    }

    private List<String> buildCommand() {
        List<String> command = new ArrayList<>();
        command.add(0, phantomLocation);
        command.add(1, scriptLocation);
        command.add(2, inputFileString);
        command.add(3, FilenameUtils.removeExtension(destinationFileString));
        command.add(4, printOptions.getString("domain"));
        command.add(5, printOptions.getString("username"));
        command.add(6, printOptions.getString("passCode"));
        command.add(7, printSettingFileName());
        command.add(8, ExportUtils.getReportDirectory());
        if (printOptions.has("format")) {
            String format = "";
            for (Object formatItem : printOptions.getJSONArray("format")) {
                format += "," + formatItem;
            }
            format = format.substring(1);

            command.add(9, format);
        }
        return command;
    }

    private void triggerPhantomProcess(String encoding, List<String> command) {
        Process process;
        InputStream inputStream = null;

        InputStream errorStream = null;
        try {

            ProcessBuilder processBuilder = new ProcessBuilder(command);
            process = processBuilder.start();

            // Read the output of Phantom JS and also the error stream for
            // errors
            inputStream = process.getInputStream();
            errorStream = process.getErrorStream();

            String inputMessage = IOUtils.toString(inputStream, encoding);
            String errorMessage = IOUtils.toString(errorStream, encoding);

            logger.info("Phantom message is " + inputMessage + ". Phantom error message is " +
                    errorMessage);

            //Added the timeout for phantom-js process execution default being 60000 ms
            JSONObject export = JsonUtils.getSettingsJson().optJSONObject("export");
            long timeOut = export != null && !export.isEmpty() ? export.getInt("phantomTimeout") : 600000;
            if (!waitFor(timeOut, TimeUnit.MILLISECONDS, process)) {
                process.destroy();
            }
        } catch (IOException ex) {
            logger.error("IOException during phantom call", ex);
        } catch (InterruptedException ex) {
            logger.error("InterruptedException during phantom call", ex);
        } finally {
            ApplicationUtilities.closeResource(inputStream);
            ApplicationUtilities.closeResource(errorStream);
        }
    }

    private String printSettingFileName() {
        JSONObject printSettings = this.printOptions.optJSONObject("printOptions");
        String jsonFileName = extractSettingJsonFileName(printSettings);
        if (jsonFileName == null) {
            jsonFileName = ExportUtils.getTemplatesDirectory() + File.separator + "DefaultTemplate" + ExportUtils
                    .JSON_EXTENSION;
        }
        return jsonFileName;
    }

    private String extractSettingJsonFileName(JSONObject printSettings) {
        String jsonFileName = null;
        if (printSettings != null && !printSettings.isEmpty()) {
            if (!printSettings.has("body") && printSettings.has("templateId")) {
                String templateId = printSettings.getString("templateId");
                jsonFileName = ExportUtils.getTemplatesDirectory() + File.separator + templateId + ExportUtils
                        .JSON_EXTENSION;
            } else if (printSettings.has("body")) {
                jsonFileName = ExportUtils.getTemplatesTempDirectory() + File.separator + System.currentTimeMillis()
                        + ExportUtils.JSON_EXTENSION;
                File file = new File(jsonFileName);
                try {
                    FileUtils.writeStringToFile(file, printSettings.toString(1), ApplicationUtilities.getEncoding());
                } catch (IOException ioe) {
                    logger.error("There was an error writing the file", ioe);
                }
            }
        }
        return jsonFileName;
    }

    /**
     * true if the string is url encoded. Otherwise false
     *
     * @param string The string under concern which is supposed to be url encoded
     * @return true if the string is url encoded
     */
    private boolean isStringURLEncoded(String string) {
        Pattern pattern = Pattern.compile("[%]");
        Matcher matcher = pattern.matcher(string);
        return matcher.find();
    }

    /**
     * The url decoded string
     *
     * @param urlEncodedString The string under concern which is supposed to be url encoded
     * @param encoding         The encoding; usually utf-8
     * @return the url decoded string
     */
    private String urlDecode(String urlEncodedString, String encoding) {
        try {
            return URLDecoder.decode(urlEncodedString, encoding);
        } catch (UnsupportedEncodingException e) {
            logger.error("Character encoding is not supported " + encoding, e);
            //handle error
        }
        return null;
    }

    //Adopted from jdk 8 source code as we are using only jdk 7
    public boolean waitFor(long timeout, TimeUnit unit, Process process) throws InterruptedException {
        long startTime = System.nanoTime();
        long rem = unit.toNanos(timeout);

        do {
            try {
                process.exitValue();
                return true;
            } catch (IllegalThreadStateException ex) {
                if (rem > 0) Thread.sleep(Math.min(TimeUnit.NANOSECONDS.toMillis(rem) + 1, 100));
            }
            rem = unit.toNanos(timeout) - (System.nanoTime() - startTime);
        } while (rem > 0);
        return false;
    }
}