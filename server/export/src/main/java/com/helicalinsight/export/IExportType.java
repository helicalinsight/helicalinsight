//IExportType.java
package com.helicalinsight.export;

import java.io.File;

import com.google.gson.JsonObject;

/**
 * @author Rajasekhar M
 *
 */
public interface IExportType {
	public File export(JsonObject options, Object object);
}
