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

/**
 * this class is no longer used
 * <p> Use {@link HeadlessChromeManager}</p> 
 * @author HDEV016
 *
 */
@Deprecated
public class PhantomServiceManager extends PhantomExportService {
    private final static Logger logger = LoggerFactory.getLogger(PhantomServiceManager.class);

    
    public static Integer getPoolSize() {
        Integer poolSize = null;
        JsonObject exportJSON = GsonUtility.optJsonObject(JsonUtils.newGetSettingsJson(),"export");
        logger.debug("Export JSON " + exportJSON);
        if (exportJSON != null && !exportJSON.entrySet().isEmpty()) {
            poolSize = GsonUtility.optInt(exportJSON,"phanmtomPoolSize");
            poolSize = poolSize != 0 ? poolSize : 3;
        }
        return poolSize;
    }
//created new method for gson
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
            phantomThread.start();
            phantomThread.join();

        } catch (InterruptedException interruptedException) {
            logger.info("Phantom Thread got interrupted " + interruptedException.getMessage());
        }
        String inputString = phantomJS.getInputString();
        stringList.add(inputString);
        String errorString = phantomJS.getErrorString();
        stringList.add(errorString);
        return stringList;
	}

}
