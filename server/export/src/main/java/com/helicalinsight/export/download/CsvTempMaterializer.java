package com.helicalinsight.export.download;

import java.io.File;
import java.util.Collections;

import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.ReportsUtility;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import com.helicalinsight.export.CSVUtility;

/**
 * Shared CSV → Temp file write for sync ExportController and resumable DownloadCacheProducer.
 */
public final class CsvTempMaterializer {

	public static final String CSV_EXTENSION = ".csv";
	public static final String CONTENT_TYPE = "application/octet-stream";

	private CsvTempMaterializer() {
	}

	public static DownloadArtifact writeArtifact(String dataJson, String reportNameOrNull) {
		String attachmentName = ReportsUtility.resolveAttachmentBase(reportNameOrNull);
		String tempPath = TempDirectoryCleaner.getTempDirectory() + File.separator
				+ attachmentName + CSV_EXTENSION;
		File file = new File(tempPath);
		CSVUtility csvWriter = new CSVUtility();
		ApplicationUtilities.createAFile(file, csvWriter.getCSVData(dataJson));
		return new DownloadArtifact(
				file,
				attachmentName + CSV_EXTENSION,
				CONTENT_TYPE,
				Collections.singleton(file.toPath()));
	}

	public static File write(String dataJson, String reportNameOrNull) {
		return writeArtifact(dataJson, reportNameOrNull).getFile();
	}
}
