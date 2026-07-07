package com.helicalinsight.efw.utility;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.resourcesecurity.jaxb.Efwsr;
import net.sf.json.JSONObject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Created by user on 3/14/2017.
 *
 * @author Rajasekhar
 */
public class ResponseUtils {
    public static String createJsonResponse(String message) {
        JSONObject responseMessage = new JSONObject();
        responseMessage.put("message", message);
        return handleSuccessMassage(responseMessage).toString();
    }

    /**
     * This method is responsible for handling the messages for success or failure and prepare the
     * json
     * object for message. for success message status will be 1 and for failure message status is 0
     *
     * @param message message
     * @return json as string
     */
    public static JSONObject handleSuccessMassage(Object message) {// parameter changed from JSONObject to Object
        JSONObject response = new JSONObject();
        response.accumulate("status", 1);
        response.accumulate("response",JSONObject.fromObject(message.toString()));
        return response;
    }

    /**
     * This method check the element in the list. If element present in the list it return true
     * else false
     *
     * @param element To be checked
     * @param list    List which contains elements to be check
     * @return return boolean value true/false
     */
    public static boolean contains(Integer element, ArrayList<Integer> list) {
        return list.contains(element);
    }
    /**
	 * newReportContentAsJson
	 * @deprecated
	 * This method is no longer acceptable 
	 * <p> Use {@link ResponseUtils#newReportContentAsJson( Efwsr efwsr)} instead.</p>
	 * @param Efwsr efwsr
	 * @return JsonObject 
	 */
	@Deprecated
    public static JSONObject reportContentAsJson(@NotNull Efwsr efwsr) {
        JSONObject data = new JSONObject();
        String reportParameters = efwsr.getReportParameters();
        data.accumulate("reportParameters", reportParameters);
        String reportDirectory = efwsr.getReportDirectory();
        String reportFile = efwsr.getReportFile();
        String reportName = efwsr.getReportName();
        data.accumulate("reportDirectory", reportDirectory);
        data.accumulate("reportFile", reportFile);
        data.accumulate("reportName", reportName);
        return data;
    }
	
	/**
	 * newReportContentAsJson using gson
	 * @param Efwsr efwsr
	 * @return JsonObject
	 */
	public static JsonObject newReportContentAsJson(@NotNull Efwsr efwsr) {
		JsonObject data = new JsonObject();
        String reportParameters = efwsr.getReportParameters();
        data.addProperty("reportParameters", reportParameters);
        String reportDirectory = efwsr.getReportDirectory();
        String reportFile = efwsr.getReportFile();
        String reportName = efwsr.getReportName();
        GsonUtility.accumulate(data,"reportDirectory", reportDirectory);
        GsonUtility.accumulate(data,"reportFile", reportFile);
        GsonUtility.accumulate(data,"reportName", reportName);
        return data;
    }
}
