//PhantomThreadDaemonManager.java
package com.helicalinsight.export;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.HIManagedThread;
import com.helicalinsight.efw.IBackgroundService;
import com.helicalinsight.efw.exceptions.ExportException;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * The `PhantomThreadDemonManager` class implements the `IBackgroundService` interface
 * This class has method calls which are there to start and stop the phantom
 * daemon service thread
 * It also checks whether the daemon service is active or not.
 *
 * @author Rajasekhar M
 */
@Deprecated
public class PhantomThreadDemonManager implements IBackgroundService {
    private static Logger logger = LoggerFactory.getLogger(PhantomThreadDemonManager.class);

    private static DaemonPhantomService daemonPhantom = new DaemonPhantomService();
    private static Thread phantomThread;
    private static final String machineAddress = getMachineAddress();
    private static String message = null;
    /**
     * Starts the PhantomJS daemon service thread.
     */
    public void start() {
        hasValidConfiguration();
        Boolean isRunning = isServiceActive();
        if ((phantomThread == null || !isRunning) && daemonPhantom != null) {
            phantomThread = new HIManagedThread(daemonPhantom);
            phantomThread.setDaemon(true);
            phantomThread.setName("HI Export Service");
            phantomThread.setPriority(Thread.MAX_PRIORITY);
            if (!phantomThread.isAlive()) {
                phantomThread.start();
                logger.info("Daemon Service Thread State " + phantomThread.isAlive() + "  "
                        + phantomThread.getState().name());

				
				/*while(!isServiceActive()){
                    isServiceActive();
				}*/
                message = "Daemon Thread Started";
            }
        } else {
            logger.info("Daemon Thread is already started");
            message = "Daemon Thread already Started";
        }
    }

    /**
     * Stops the PhantomJS daemon service thread.
     */
    public void stop() {
        PhantomThreadDemonManager.hasValidConfiguration();
        if (phantomThread != null) {
            daemonPhantom.stopPhantomService();
            phantomThread = null;
            logger.info("Daemon Thread Stopped");
            message = "Daemon Thread Stopped";
        } else {
            logger.info("Daemon Thread already Stopped");
            message = "Daemon Thread already Stopped";
        }
    }
    /**
     * Checks if the PhantomJS daemon service is currently active.
     * @return {@code true} if the service is active, {@code false} otherwise.
     */
    public static boolean isServiceActive() {
        PhantomThreadDemonManager.hasValidConfiguration();
        return checkPortActive(machineAddress);
    }
    /**
     * Return the ip address of a machine .
     * @return The machine address as a String.
     */
    public static String getMachineAddress() {
        String machineAddress = null;
        try {
            machineAddress = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException unknownHostException) {
            logger.error("Failed to get IP address of server machine " + unknownHostException.getMessage());
            machineAddress = "localhost";
        }
        return machineAddress;
    }
    /**
     * Checks if the PhantomJS daemon service port is active.
     * @param machineAddress       machine address as a String.
     * @return {@code true} if the port is active, {@code false} otherwise.
     */
    public static boolean checkPortActive(String machineAddress) {

        Integer port = getPhantomPort();
        return !ApplicationUtilities.isPortAvailable(port, getMachineAddress());
    }
    /**
     * Retrieves the PhantomJS daemon service port.
     * @return the PhantomJS daemon service port as an Integer.
     */
    public static Integer getPhantomPort() {
        Integer phantomPort = null;
        JsonObject exportJSON = GsonUtility.optJsonObject(JsonUtils.newGetSettingsJson(),"export");
        if (exportJSON != null && !exportJSON.entrySet().isEmpty()) {
            phantomPort = GsonUtility.optInt(exportJSON,"phantomPort");
            phantomPort = phantomPort != 0 ? phantomPort : 3000;
        }
        return phantomPort;
    }
    /**
     * Retrieves the message associated with the PhantomJS daemon manager.
     * @return the message as a String.
     */
    public static String getMessage() {
        return message;
    }
    /**
     * @return {@code true} if the configuration is set up, {@code false} otherwise.
     */
    @Override
    public boolean setUp() {
    	JsonObject exportJSON = GsonUtility.optJsonObject(JsonUtils.newGetSettingsJson(),"export");
        if (exportJSON != null && !exportJSON.entrySet().isEmpty()) {
            return true;
        }
        return false;
    }
    /**
     * Stops the PhantomJS daemon service thread.
     * @return true if the service is still active after cleanup, false otherwise.
     */
    @Override
    public boolean cleanUp() {
        stop();
        logger.debug("cleanUpStarted");
        return isServiceActive();
    }

    private static void hasValidConfiguration() {
        if (!new PhantomThreadDemonManager().setUp()) {
            throw new ExportException("The configuration 'export' is missing in setting.xml");
        }
    }
}
