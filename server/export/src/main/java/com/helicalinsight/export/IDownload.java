package com.helicalinsight.export;

import com.google.gson.JsonObject;

/**
 * The `IDownload` interface is implemented by classes that provide a method to
 * download and convert data to a specified format. Classes implementing this interface
 * should return an Object representing the downloaded data based on the provided
 * `jsonData` and `conversionOptions` in the form of a JsonObject.
 *
 * @author Somen
 */
public interface IDownload {
	/**
     * Returns the downloaded data in the specified format.
     *
     * @param jsonData          	data to be downloaded.
     * @param conversionOptions 	Object containing options for the conversion process.
     * @return Object representing the downloaded data.
     */
    Object downloadFormat(Object jsonData, JsonObject conversionOptions);




}