package com.helicalinsight.export;

import java.io.File;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import com.helicalinsight.export.crypto.FileCryptoHandler;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.dto.ResourceExtension;
import com.helicalinsight.export.dto.ResourceOptions;
import com.helicalinsight.export.exception.ResourceExportException;
import com.helicalinsight.export.handler.AbstractResourceWriterHandler;
import com.helicalinsight.export.handler.ResourceWriterHandlerFactory;
import com.helicalinsight.export.utils.ManifestUtils;
import com.helicalinsight.export.utils.ResourceFileUtils;
import com.helicalinsight.resourcedb.HIResourceDTO;
/**
 * The ExportResourceManager class provides functionality for exporting resources, such as reports ,
 * as a batch operation. It handles the writing, archiving, encryption, and manifest creation for the exported resources.
 *
 * The exported resources are organized into a directory structure based on a timestamp and the hierarchy of the
 * original resources. The exported data is encrypted for security and stored in a zip file for easy distribution.
 */
@Component
public class ExportResourceManager {

	@Autowired
	private ResourceFileUtils fileUtils;

	@Autowired
	private FileCryptoHandler cryptoHandler;

	@Autowired
	private ManifestUtils manifestUtils;
	//path of the temporary directory used for exporting resources
	private static final String TEMPDIR = TempDirectoryCleaner.getTempDirectory().getAbsolutePath();
	
	private static final ApplicationProperties props = ApplicationProperties.getInstance();

	/**
	 * Writes in byte format to the provided list of resources to the specified directory, applies resource options,
     * creates a manifest, encrypts the exported data, archives it, and returns the byte array of the archived file.
	 * @param resources			provides the paths, extension to export
	 * @param dir				directory for exporting resources
	 * @param options			provides share related or schedule details
	 * @param response			to set header file
	 * @return byte array of export file.
	 * @throws Exception If an error occurs during the export process.
	 */
	public byte[] write(List<HIResourceDTO> resources, String path, ResourceOptions options , HttpServletResponse response) throws Exception {
		Manifest manifest = new Manifest();
		manifest.setVersion(props.getManifestVersion());
		String timeStamp = String.valueOf(System.currentTimeMillis());
		HIResourceDTO  parent = resources.get(0);
		path = StringUtils.isBlank(path) ? "AllResources" : parent.getName().strip();
		path = path.contains("/") ? path.replace("/", "_") : path;
		String dir = String.join("/", timeStamp, path);
		if (Boolean.TRUE.equals(fileUtils.createSchema(dir))) {
			write(resources, dir, options, manifest);
			String dirfullPath = String.join(File.separator, TEMPDIR, dir);
			manifest.setOptions(options);
			manifestUtils.writeManifest(manifest, String.join(File.separator,TEMPDIR,dir));
			cryptoHandler.encryptBatch(dirfullPath);
			fileUtils.archive(dir);
			File file = new File(String.join(File.separator, TEMPDIR, timeStamp, path + ResourceExtension.ZIP.getValue()));
			byte[] bytes = fileUtils.getAllBytes(file);
			fileUtils.cleanDir(timeStamp);
			response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", path + ".zip"));
			return bytes;

		} else {
			throw new ResourceExportException("Error occurred while creating Folder schema");
		}

	}
	/**
	 * Recursively writes resources to the specified directory, applying options and updating the manifest
	 * @param resources			provides the paths, extension to export
	 * @param dir				directory for exporting resources
	 * @param options			provides share related or schedule details
	 * @param manifest			instance of manifest
	 */
	private void write(List<HIResourceDTO> resources, String dir, ResourceOptions options, Manifest manifest) {

		for (HIResourceDTO resource : resources) {
			String resourceType = resource.getExtension() == null ? resource.getType() : resource.getExtension();
			AbstractResourceWriterHandler handler = ResourceWriterHandlerFactory.getHandler(resourceType);
			handler.write(resource, dir, manifest, options);
			handler.share(resource, manifest, options, dir);
			handler.schedule(resource, manifest, options, dir);
			if (resource.getChildren() != null) {
				List<HIResourceDTO> children = resource.getChildren();
				write(children, dir, options, manifest);
			}

		}
	}

	
}
