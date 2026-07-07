package com.helicalinsight.adhoc.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.helicalinsight.datasource.dao.GlobalConnectionDAO;
import com.helicalinsight.datasource.service.GlobalConnectionService;
import com.helicalinsight.efw.ApplicationProperties;


import org.springframework.beans.factory.annotation.Autowired;

public class FlatFileDeleteUtils {

	private static String flatFilePath = ApplicationProperties.getInstance().getFlatFilesPath();

	public static void deleteRequestedFolders(String id) {
	
				String folderPath = String.join(File.separator, flatFilePath, id);
				File folder = new File(folderPath);

				if (folder.exists() && folder.isDirectory()) {
					deleteFolderContents(folder);
					return;
				}
			}

	private static void deleteFolderContents(File folder) {
		File[] flatfiles = folder.listFiles();

		if (flatfiles != null) {
			for (File file : flatfiles) {
				if (file.isDirectory()) {
					deleteFolderContents(file);
				}
				file.delete();
			}
		}
		
		folder.delete();
	}
}
