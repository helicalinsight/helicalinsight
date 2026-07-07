package com.helicalinsight.adhoc.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.ConfigurationException;

public class FileDeleteUtils {
	private static String solutionDirectoryPath = ApplicationProperties.getInstance().getSolutionDirectory() ;
	
	 @NotNull
	    public static List<String> deleteRequestedMetadata(@NotNull List<String> metadataList) {
	       
	        File metadataFile;
	        List<String> deletedList = new ArrayList<>();
	        for (String file : metadataList) {
	            metadataFile = new File(solutionDirectoryPath + File.separator + file);
	            if (metadataFile.exists()) {
	                metadataFile.delete();
	                deletedList.add(file);
	            }
	        }
	        return deletedList;
	    }
	 public static int deleteReport( List<String> filteredReportList, int count) {
			for (String file : filteredReportList) {
	            try {
	                File reportFile = new File(solutionDirectoryPath + File.separator + file);
	               
	               
	                    if (reportFile.exists()) {
	                        reportFile.delete();
	                        count++;
	                    }
	            } catch (ConfigurationException ignore) {

	            }
	        }
			return count;
		}
}
