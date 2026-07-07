package com.helicalinsight.efw.controller;

import java.io.File;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.DataSourceUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.managed.jaxb.HCReport;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.HCRException;
import com.helicalinsight.efw.exceptions.RequiredParameterIsNullException;
import com.helicalinsight.efw.utility.FileUtils;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.resourcesecurity.IResource;
import com.helicalinsight.resourcesecurity.jaxb.Efwsr;

/**
 * Created by Rajasekhar on 27-04-2015.
 *
 * @author Rajasekhar
 */
public class ReportOpenHelper {

    @Nullable
    public static IResource getHCRReport(String dir, String fileName) {
        String solutionDirectory = ApplicationProperties.getInstance().getSolutionDirectory();
        File reportFile;
        String efwsrFilePath = solutionDirectory + File.separator + dir + File.separator + fileName;
        File efwsrFile = new File(efwsrFilePath);
        Efwsr efwsr = JaxbUtils.unMarshal(Efwsr.class, efwsrFile);
        String hcrFileName = efwsr.getReportFile();
        String hcrReportDirectory = efwsr.getReportDirectory();
        try {
            DataSourceUtils.validate(dir);
            DataSourceUtils.validate(fileName);
            reportFile = new File(solutionDirectory + File.separator + hcrReportDirectory + File.separator + hcrFileName);
        } catch (RequiredParameterIsNullException e) {
            throw new EfwServiceException("One of the required parameters dir or file name is " + "null/empty");
        }

        if (!reportFile.exists()) {
            throw new HCRException("The report file does not exists.");
        }

        String hcrExtension = JsonUtils.getHCRExtension();
        String extensionOfFile = FileUtils.getExtensionOfFile(reportFile);

        if (extensionOfFile == null || !hcrExtension.equals(extensionOfFile)) {
            throw new EfwServiceException(String.format("Only %s files are supported to be " +
                    "opened" + "." + "," + hcrExtension));
        }

        return JaxbUtils.unMarshal(HCReport.class, reportFile);
    }


    @NotNull
    public static JsonObject reportContentAsJson(@NotNull HCReport hcrReport) {
        String diagramData = hcrReport.getDiagramData();
        String directory = hcrReport.getDirectory();
        String formData = hcrReport.getFormData();
        String name = hcrReport.getName();
        String state = hcrReport.getState();
        JsonObject data = new JsonObject();
        GsonUtility.accumulate(data,"diagramData", diagramData);
        GsonUtility.accumulate(data,"directory", directory);
        GsonUtility.accumulate(data,"previewFormData", formData);
        GsonUtility.accumulate(data,"state", state);
        GsonUtility.accumulate(data,"reportName", name);
        return data;
    }
}
