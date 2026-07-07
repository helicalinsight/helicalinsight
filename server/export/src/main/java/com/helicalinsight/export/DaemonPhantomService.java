//DaemonPhantomService.java
package com.helicalinsight.export;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.exceptions.ExportException;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The single instance of this runnable class object responsible for starting
 * daemon service thread
 *
 * @author Rajasekhar M
 */
@Deprecated
public class DaemonPhantomService implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(DaemonPhantomService.class);
    private static boolean isInstantiated = false;
    private static boolean isRunnable = false;
    private static Process process;
    private boolean threadState;
    /**
     * Constructor to prevent multiple instantiations of the DaemonPhantomService class.
     * Throws IllegalThreadStateException if an attempt is made to instantiate it again.
     */
    public DaemonPhantomService() {
        if (!isInstantiated) {
            isInstantiated = true;
        } else {
            throw new IllegalThreadStateException();
        }
    }


    /**
     * Creates an operating system process by invoking the phantom js.The
     * phantom js creates the screen shot of the html from a file on the file
     * system.
     */
    @Override
    public void run() {
        List<String> commands = buildCommand();
        isRunnable = true;
        threadState = PhantomThreadDemonManager.isServiceActive();
       while (!this.threadState) {
        try {
            logger.info("Sleeping the thread for an interval");
            Thread.sleep(getDaemonTime());
        } catch (InterruptedException ie) {
            logger.error("Interrupted ", ie);
        }
        logger.info("Triggering phantom process and commands is " + commands);
        if(!threadState){
        triggerPhantomProcess(commands);
        }
       }
    }
    /**
     * returns the current PhantomJS process.
     *
     * @return The PhantomJS process.
     */
    public static Process getProcess() {
		return process;
	}
    /**
     * Builds the command list for starting the PhantomJS process.
     * @return The list of commands.
     */
	private List<String> buildCommand() {
        String phantomLocation = ReportsProcessor.getPhantomLocation();
        String scriptLocation = getScriptLocation("server.js");
        String exportJsLocation = getScriptLocation("export.js");
        Integer port = PhantomThreadDemonManager.getPhantomPort();

        List<String> command = new ArrayList<>();
        /*command.add( "cmd.exe");
        command.add( " /c");*/
        command.add( phantomLocation);

        /*command.add(1, "--remote-debugger-port=9000");*/
        command.add( scriptLocation);
        command.add( "" + port);
        command.add(exportJsLocation);
        command.add( ExportUtils.getReportDirectory());

        return command;
    }
	/**
     * Gets the location of the script file.
     *
     * @param fileName 		name of the script file.
     * @return The absolute path to the script file.
     */
    public String getScriptLocation(final String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        URL serverJsFile = classLoader.getResource("/HDIPhantomjs/" + fileName);

        if (serverJsFile == null) {
            throw new ExportException("The file " + fileName + " location is not found. ");
        }

        File requiredFile = new File(serverJsFile.getFile());
        String absolutePath = requiredFile.getAbsolutePath();
        if (isStringURLEncoded(absolutePath)) {
            return urlDecode(absolutePath);
        }
        return absolutePath;
    }
    /**
     * Checks the given string is URL encoded or not.
     *
     * @param string The string to check.
     * @return True if the string is URL encoded, false otherwise.
     */
    private boolean isStringURLEncoded(String string) {
        Pattern pattern = Pattern.compile("[%]");
        Matcher matcher = pattern.matcher(string);
        return matcher.find();
    }
    /**
     * Decodes a encoded string.
     *
     * @param urlEncodedString 	encoded URL in string format.
     * @return The decoded string.
     */
    private String urlDecode(String urlEncodedString) {
        String encoding = ApplicationUtilities.getEncoding();
        try {
            return URLDecoder.decode(urlEncodedString, encoding);
        } catch (UnsupportedEncodingException e) {
            logger.error("Character encoding is not supported " + encoding, e);
            // handle error
        }
        return null;
    }
    /**
     * Triggers the PhantomJS process.
     * @param command 			list of commands to execute.
     */
    private void triggerPhantomProcess(List<String> command) {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
      //  processBuilder= processBuilder.redirectError(new File("E:\\PhantomProcess.err")).redirectOutput(new File("E:\\PhantomProcess.out"));
        try {
            if (process == null) {
                process = processBuilder.start();
                logger.info("Phantom process started");

            } else {
                if (!process.isAlive() && !threadState) {
                    process = processBuilder.start();
                    logger.info("Phantom process restarted");
                }
            }
        } catch (IOException e) {
            logger.error("Problem during starting phantom service");
            e.printStackTrace();

        }
    }
    /**
     * Stops the PhantomJS service.
     */
    public void stopPhantomService() {
        if (isRunnable && process != null) {
            process.destroy();
            process.destroyForcibly();
            //process.waitFor();
            process=null;

            threadState = true;
        } else {
            logger.info("Daemon Service Thread already stopped");
        }
    }
    /**
     * Gets the daemon time from the setting.xml configuration.
     *
     * @return The daemon time in milliseconds.
     */
    public static Integer getDaemonTime() {
        Integer daemonTime = null;
        JsonObject exportJSON = GsonUtility.optJsonObject(JsonUtils.newGetSettingsJson(),"export");
        if (exportJSON != null && !exportJSON.entrySet().isEmpty()) {
            daemonTime = GsonUtility.optInt(exportJSON,"daemonTimeCheck");
            daemonTime = daemonTime != 0 ? daemonTime : 8000;
        }
        return daemonTime;
    }


}
