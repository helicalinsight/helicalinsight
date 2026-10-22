package com.helicalinsight.export;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.helicalinsight.admin.utils.JacksonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.utility.ResponseMetadataEnricher;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import com.helicalinsight.export.crypto.FileCryptoHandler;
import com.helicalinsight.export.download.DownloadArtifact;
import com.helicalinsight.export.dto.Manifest;
import com.helicalinsight.export.dto.ResourceExtension;
import com.helicalinsight.export.dto.ResourceOptions;
import com.helicalinsight.export.exception.ResourceExportException;
import com.helicalinsight.export.handler.AbstractResourceWriterHandler;
import com.helicalinsight.export.handler.ResourceWriterHandlerFactory;
import com.helicalinsight.export.ticket.TransferTicketService;
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

	private static final Logger logger = LoggerFactory.getLogger(ExportResourceManager.class);

	@Autowired
	private ResourceFileUtils fileUtils;

	@Autowired
	private FileCryptoHandler cryptoHandler;

	@Autowired
	private ManifestUtils manifestUtils;

	@Autowired
	private TransferTicketService transferTicketService;

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
		DownloadArtifact artifact = writeToFile(resources, path, options, null);
		byte[] bytes = fileUtils.getAllBytes(artifact.getFile());
		Path stagingRoot = artifact.getTrackedPaths().stream()
				.filter(p -> p.toFile().isDirectory())
				.findFirst()
				.orElse(artifact.getFile().getParentFile().toPath());
		fileUtils.cleanDir(stagingRoot.getFileName().toString());
		response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", artifact.getFileName()));
		return bytes;
	}

	/**
	 * Stage + zip to disk and return a DownloadArtifact. Does not read all bytes into memory
	 * and does not clean temp files (caller / ticket abort owns cleanup).
	 */
	public DownloadArtifact writeToFile(List<HIResourceDTO> resources, String path, ResourceOptions options) throws Exception {
		return writeToFile(resources, path, options, null);
	}

	/**
	 * Stage + zip to disk for resumable export. When {@code ticketId} is set, staging is tracked early
	 * and abort/interrupt is checked between write / encrypt / archive steps.
	 */
	public DownloadArtifact writeToFile(List<HIResourceDTO> resources, String path, ResourceOptions options,
			String ticketId) throws Exception {
		Manifest manifest = new Manifest();
		manifest.setVersion(props.getManifestVersion());
		ObjectNode metadata =  JacksonUtility.fromObject(ResponseMetadataEnricher.getMetaObject().toString());
		metadata.put("time", Instant.now().toString());
		manifest.setMetadata(metadata);
		String timeStamp = String.valueOf(System.currentTimeMillis());
		HIResourceDTO  parent = resources.get(0);
		path = StringUtils.isBlank(path) ? "AllResources" : parent.getName().strip();
		path = path.contains("/") ? path.replace("/", "_") : path;
		String dir = String.join("/", timeStamp, path);
		if (Boolean.TRUE.equals(fileUtils.createSchema(dir))) {
			Path stagingRoot = Paths.get(TEMPDIR, timeStamp);
			if (StringUtils.isNotBlank(ticketId)) {
				transferTicketService.track(ticketId, stagingRoot);
			}
			manifest.setOptions(options);
			write(resources, dir, options, manifest, ticketId);
			throwIfAborted(ticketId);
			String dirfullPath = String.join(File.separator, TEMPDIR, dir);
			manifestUtils.writeManifest(manifest, String.join(File.separator,TEMPDIR,dir));
			throwIfAborted(ticketId);
			cryptoHandler.encryptBatch(dirfullPath);
			throwIfAborted(ticketId);
			fileUtils.archive(dir);
			throwIfAborted(ticketId);
			File zip = new File(String.join(File.separator, TEMPDIR, timeStamp, path + ResourceExtension.ZIP.getValue()));
			Set<Path> tracked = new LinkedHashSet<>();
			tracked.add(stagingRoot);
			tracked.add(zip.toPath());
			return new DownloadArtifact(zip, path + ".zip", "application/zip", tracked);
		}
		throw new ResourceExportException("Error occurred while creating Folder schema");
	}

	/**
	 * Recursively writes resources to the specified directory, applying options and updating the manifest
	 * @param resources			provides the paths, extension to export
	 * @param dir				directory for exporting resources
	 * @param options			provides share related or schedule details
	 * @param manifest			instance of manifest
	 */
	private void write(List<HIResourceDTO> resources, String dir, ResourceOptions options, Manifest manifest,
			String ticketId) {
		for (HIResourceDTO resource : resources) {
			throwIfAborted(ticketId);
			String resourceType = resource.getExtension() == null ? resource.getType() : resource.getExtension();
			AbstractResourceWriterHandler handler = ResourceWriterHandlerFactory.getHandler(resourceType);
			handler.write(resource, dir, manifest, options);
			handler.share(resource, manifest, options, dir);
			handler.schedule(resource, manifest, options, dir);
			if (resource.getChildren() != null) {
				List<HIResourceDTO> children = resource.getChildren();
				write(children, dir, options, manifest, ticketId);
			}
		}
	}

	private void throwIfAborted(String ticketId) {
		if (Thread.currentThread().isInterrupted()
				|| (StringUtils.isNotBlank(ticketId) && transferTicketService.isAborted(ticketId))) {
			logger.info("Export aborted for ticketId={}; stopping zip production (cleanup follows)", ticketId);
			throw new ResourceExportException("Export aborted");
		}
	}
}
