//PhantomExportService.java
package com.helicalinsight.export;

import java.util.List;

import com.google.gson.JsonObject;

/**
 * Implementations of this abstract class provides different ways of exporting
 * reports
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
public abstract class PhantomExportService {
	//creted new method for gson
	public abstract  List<String> handlePhantomjs(JsonObject settings);
}
