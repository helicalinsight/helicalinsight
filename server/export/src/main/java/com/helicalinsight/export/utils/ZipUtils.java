package com.helicalinsight.export.utils;

import java.io.File;
import java.util.List;
import java.util.function.BiConsumer;

import org.springframework.stereotype.Component;

import com.helicalinsight.export.exception.ZipResourceException;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
/**
 * Utility class for archiving and extracting files using Zip compression.
 */
@Component
public class ZipUtils {
	/**
     * Archives a specified directory into a Zip file.
     *
     * @param path   		path of the directory to be archived.
     * @param zipName 		name of the Zip file.
     * @throws ZipException If an error occurs during the archiving process.
     */
	public void archive(String path, String zipName) throws ZipException {
		File fileToZip = new File(path + File.separator + zipName);
		File zipFile = new File(path + File.separator + zipName + ".zip");
		ZipFile zip = new ZipFile(zipFile);
		ZipParameters params = new ZipParameters();
		params.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
		params.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
		zip.addFolder(fileToZip, params);
	}
	/**
     * Extracts a Zip file into the specified directory.
     *
     * @param dir      		directory where the contents of the Zip file will be extracted.
     * @param fileName 		name of the Zip file to be extracted.
     * @throws ZipResourceException If the provided Zip file is invalid or corrupted.
     */
	public void extract(String dir, String fileName) throws ZipException {
		extract(dir, fileName, null);
	}

	/**
	 * Extracts a Zip file entry-by-entry, optionally reporting {@code (done, total)} progress.
	 */
	@SuppressWarnings("unchecked")
	public void extract(String dir, String fileName, BiConsumer<Integer, Integer> progress) throws ZipException {
		ZipFile zipFile = new ZipFile(dir + File.separator + fileName);
		if (!zipFile.getFile().exists() || !zipFile.isValidZipFile()) {
			throw new ZipResourceException("Invalid Zip file provided (or) Zip File corrupted.");
		}
		List<FileHeader> headers = zipFile.getFileHeaders();
		int total = headers != null ? headers.size() : 0;
		if (total == 0) {
			zipFile.extractAll(dir);
			if (progress != null) {
				progress.accept(0, 0);
			}
			return;
		}
		int done = 0;
		for (FileHeader header : headers) {
			zipFile.extractFile(header, dir);
			done++;
			if (progress != null) {
				progress.accept(done, total);
			}
		}
	}

}
