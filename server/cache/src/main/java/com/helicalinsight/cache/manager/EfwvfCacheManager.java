package com.helicalinsight.cache.manager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.resourceprocessor.IProcessor;
import com.helicalinsight.efw.resourceprocessor.ResourceProcessorFactory;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.validator.ResourceValidator;
import com.helicalinsight.efw.vf.ChartResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Somen
 * responsible for managing cache for Efwvf file which extends <p>{@link EfwdCacheManager}</p>
 * The {@code @Component} annotation is used to mark a class as a Spring bean/component.
 * {@code @Scope("prototype")} new instance of the component will be created each time.
 * {@code @SuppressWarnings("unused")} used to suppress compiler warnings about unused variables, parameters, or methods. 
 * Created by Somen on 6/1/2015.
 */
@Component
@Scope("prototype")
@SuppressWarnings("unused")
public class EfwvfCacheManager extends EfwdCacheManager {
    private static final Logger logger = LoggerFactory.getLogger(EfwvfCacheManager.class);

    private String vfFile;
    private int vfId;
    private JsonObject chartData;
    private ChartResource chartResource;

    public String getVfFile() {
        return vfFile;
    }
    /**
	 * setRequestData(String requestData)
	 * this method provides efwvf file and chart file details
	 * @param requestData 		this is Efwvf file json data in String format
	 */
    @Override
    public void setRequestData(String requestData) {
        ApplicationProperties applicationProperties;
        applicationProperties = ApplicationProperties.getInstance();
        JsonObject parameterJsonObject = new Gson().fromJson(requestData,JsonObject.class);
        String rootPath = parameterJsonObject.get("dir").getAsString();
        String absolutePath = applicationProperties.getSolutionDirectory();
        IProcessor processor = ResourceProcessorFactory.getIProcessor();
        String vfFile = parameterJsonObject.get("vf_file").getAsString();

        this.vfId = Integer.parseInt(parameterJsonObject.get("vf_id").getAsString());

        JsonObject vfJsonObject;
        int dataSourceId = 0;
        this.chartData = new JsonObject();

        String completeVfFile = absolutePath + File.separator + rootPath + File.separator + vfFile;
        this.vfFile = completeVfFile;
        vfJsonObject = processor.getJsonObject(completeVfFile, true);
        ResourceValidator resourceValidator = new ResourceValidator(vfJsonObject);
        boolean exists = resourceValidator.newValidateVf();

        if (!exists) {
            logger.error("Duplicate chart id in vf file");
            return;
        } else {
        	JsonArray chartsArray = vfJsonObject.getAsJsonArray("Charts");
            for (int chartNumber = 0; chartNumber < chartsArray.size(); chartNumber++) {
                JsonObject chart = chartsArray.get(chartNumber).getAsJsonObject();
                int id = chart.get("@id").getAsInt();
                if (this.vfId == id) {
                    this.chartData = chart.getAsJsonObject("prop");
                    Object dataSource = this.chartData.get("DataSource");
                    logger.debug("The dataSource id is " + dataSource.toString());
                    if (dataSource instanceof JsonArray) {
                        break;
                    } else {
                        dataSourceId = Integer.parseInt(chartData.get("DataSource").getAsString());
                    }
                    break;
                }
            }
        }
        parameterJsonObject.addProperty("map_id", dataSourceId);//accumulate
        logger.info("Calling the efwd manager class with the map_id" + dataSourceId +
                parameterJsonObject.toString());
        super.setRequestData(parameterJsonObject.toString());

    }
    /**
	 * serveCachedContent(HttpServletRequest request, HttpServletResponse response,
                                      Object rawContent)
	 * @param request    request object provides modified cache object
	 * @param response   response sets the content type and encoding value from message.properties
	 * @param rawContent query execution result of efwvf (chart data) in object format
	 * {@return true}{@code false} if object not able to print
	 */
    @Override
    public boolean serveCachedContent(HttpServletRequest request, HttpServletResponse response,
                                      Object rawContent) {
        JsonObject fileContent = (JsonObject) rawContent;
        response.setContentType(ControllerUtils.defaultContentType());
        response.setCharacterEncoding(ApplicationUtilities.getEncoding());
        PrintWriter out = null;
        logger.info("Serving cache file for visualizeData......");
        JsonArray jsonArray = fileContent.getAsJsonArray("data");
        JsonObject resultJsonObject = new JsonObject();
        resultJsonObject.addProperty("id", "chart_" + getVfId());//accumulate
        setChartData(jsonArray);
        resultJsonObject.addProperty("script", getScript());//accumulate

        Object lastModifiedTime = request.getAttribute("lastModifiedCache");
        if (lastModifiedTime != null)
            resultJsonObject.addProperty("lastModified", (Long) lastModifiedTime);//accumulate

        try {
            out = response.getWriter();
            out.print(resultJsonObject);
        } catch (IOException ioe) {
            logger.error("Exception occurred during io", ioe);
            return false;
        } finally {
            ApplicationUtilities.closeResource(out);
        }
        return true;
    }
    /**
	 * getVfId()
	 * @return vf id from efwvf file data.
	 */
    public int getVfId() {
        return this.vfId;
    }
    /**
	 * setChartData(JsonArray data)
	 * this method instance the Constructor that sets the variables of ChartResource.
	 * @param data 			The query execution result of chart data in JsonArray format
	 */
    public void setChartData(JsonArray data) {
        String settingPath = ApplicationProperties.getInstance().getSettingPath();
        chartResource = new ChartResource(settingPath, data, this.chartData, String.valueOf(this.vfId));
    }
    /**
	 * getScript()
	 * Responsibility of this method is create instance of a class which is
     * available in setting.xml file within Charts tag.
	 * {@return data along with the chart script}{@code null} if ChartResource object is null.
	 */
    public String getScript() {
        if (chartResource != null) {
            return this.chartResource.getScript();
        } else {
            return null;
        }
    }
}
