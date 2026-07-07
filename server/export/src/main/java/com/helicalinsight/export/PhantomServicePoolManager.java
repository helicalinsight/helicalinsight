package com.helicalinsight.export;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.HIManagedThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helicalinsight.efw.utility.JsonUtils;

/**
 * The instance of this class manages service calls of thread which generates
 * the pdf or png or jpeg formats on the file system.
 *
 * @author Rajasekhar M
 * 
 */
@Deprecated
public class PhantomServicePoolManager extends PhantomServiceManager {
	private final static Logger logger = LoggerFactory.getLogger(PhantomServicePoolManager.class);

	/**
     * Handles PhantomJS calls based on the provided settings.
     * @param settings 		JsonObject containing necessary settings for export such as
     *                      phantomLocation, scriptLocation, reportSourceUri, destinationFile, etc.
     * @return List of strings containing input and error information from the PhantomJS process.
     */
	@Override
	public List<String> handlePhantomjs(JsonObject settings) {
		PhantomJS phantomJS = new PhantomJS(settings.get("phantomLocation").getAsString(), settings.get("scriptLocation").getAsString(),
				settings.get("reportSourceUri").getAsString(), settings.get("destinationFile").getAsString(), settings);
		List<String> stringList = new ArrayList<>();
        HIManagedThread phantomThread = new HIManagedThread(phantomJS);
		phantomThread.setName("phantom-thread " + System.currentTimeMillis());
		logger.info("CurrentThread = " + Thread.currentThread() + ". Starting phantom-thread "
				+ "to generate the report format.");
		try {
			Integer poolSize = getPoolSize();
			while (!(PhantomJS.getCounter() < poolSize)) {
				Thread.sleep(5000);
			}
			PhantomJS.incrementCounter();
			phantomThread.start();
			phantomThread.join();
			PhantomJS.decrementCounter();

		} catch (InterruptedException interruptedException) {
			logger.info("Phantom Thread got interrupted " + interruptedException.getMessage());
		}
		String inputString = phantomJS.getInputString();
		stringList.add(inputString);
		String errorString = phantomJS.getErrorString();
		stringList.add(errorString);
		return stringList;
	}
	/**
     * Retrieves the pool size for managing PhantomJS threads.
     * @return The pool size as an Integer.
     */
	public static Integer getPoolSize() {
		Integer poolSize = null;
		JsonObject exportJSON = GsonUtility.optJsonObject(JsonUtils.newGetSettingsJson(),"export");
		if (exportJSON != null && !exportJSON.entrySet().isEmpty()) {
			poolSize = GsonUtility.optInt(exportJSON,"phanmtomPoolSize");
			poolSize = poolSize != 0 ? poolSize : 3;
		}
		return poolSize;
	}

}
