package com.helicalinsight.export.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import com.helicalinsight.export.exception.ResourceExportException;
import com.helicalinsight.export.utils.ResourceSuffix;
import com.helicalinsight.resourcedb.HIResourceDTO;
/**
 * This class is responsible for writing resource data to files during the export process.
 * 
 */
@Component
public class ResourceDataWriter {

	
	private static final String TEMPDIR = TempDirectoryCleaner.getTempDirectory().getAbsolutePath();
	private static final ApplicationProperties properties = ApplicationProperties.getInstance();
	public void write(Object object, String dir, HIResourceDTO resource, String suffix) {
		
		String fileName = "";
		String version = properties.getManifestVersion();
		if(StringUtils.isNotBlank(version) && Integer.valueOf(version) >  0) {
			fileName = resource.getPath();
			createHierarchy(dir,FilenameUtils.getFullPath(fileName));
		}
		else {
			String path = resource.getPath();
			fileName = path.contains("/") ? StringUtils.substringAfterLast(path, "/"):path;
		}
		
		if(ResourceSuffix.SCHEDULE_RESULT.equalsIgnoreCase(suffix)) {
			fileName = FilenameUtils.removeExtension(fileName);
		}
		
		String filePath = String.join(File.separator, TEMPDIR, dir, "resources", fileName+suffix);
		try {
			write(object, filePath);
		} catch (Exception e) {
			throw new ResourceExportException(e.getMessage());
		}
	}
	
	private void createHierarchy(String dir , String parentDirPath) {
		try {
			Files.createDirectories(Path.of(TEMPDIR, dir, "resources", parentDirPath));
		}
		catch (Exception e) {
			throw new ResourceExportException(e.getMessage());
		}
	}
	
	private void write(Object object, String filePath) {
		try (FileOutputStream fos = new FileOutputStream(filePath)) {
			ObjectMapper mapper = new ObjectMapper();
			ObjectWriter writer = mapper.writer();
			writer.writeValue(fos, object);
		} catch (Exception e) {
			throw new ResourceExportException(e.getMessage());
		}
	}
}
