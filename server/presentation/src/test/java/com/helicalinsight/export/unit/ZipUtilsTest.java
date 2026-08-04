package com.helicalinsight.export.unit;


import java.io.File;
import java.io.FileWriter;

import org.junit.Assert;
import org.junit.Test;

import com.helicalinsight.export.exception.ZipResourceException;
import com.helicalinsight.export.utils.ZipUtils;

import net.lingala.zip4j.core.ZipFile;

public class ZipUtilsTest extends ExportUnitTestBase {

	@Test
	public void ut_a1_testArchiveAndExtract() throws Exception {
		ZipUtils zipUtils = new ZipUtils();
		File root = new File(System.getProperty("java.io.tmpdir"), "zip-test-" + System.nanoTime());
		root.mkdirs();
		File folder = new File(root, "archive");
		folder.mkdir();
		File content = new File(folder, "file.txt");
		try (FileWriter writer = new FileWriter(content)) {
			writer.write("zip content");
		}

		zipUtils.archive(root.getAbsolutePath(), "archive");
		File zipFile = new File(root, "archive.zip");
		Assert.assertTrue(zipFile.exists());

		File extractDir = new File(System.getProperty("java.io.tmpdir"), "zip-extract-" + System.nanoTime());
		extractDir.mkdirs();
		new ZipFile(zipFile).extractAll(extractDir.getAbsolutePath());
		Assert.assertTrue(new File(extractDir, "archive/file.txt").exists() || new File(extractDir, "file.txt").exists());

		deleteRecursively(root);
		deleteRecursively(extractDir);
	}

	@Test(expected = ZipResourceException.class)
	public void ut_a2_testExtractInvalidZip() throws Exception {
		ZipUtils zipUtils = new ZipUtils();
		File dir = new File(System.getProperty("java.io.tmpdir"), "bad-zip-" + System.nanoTime());
		dir.mkdirs();
		File badZip = new File(dir, "bad.zip");
		try (FileWriter writer = new FileWriter(badZip)) {
			writer.write("not a zip");
		}
		try {
			zipUtils.extract(dir.getAbsolutePath(), "bad.zip");
		} finally {
			deleteRecursively(dir);
		}
	}

	private void deleteRecursively(File file) {
		if (file.isDirectory()) {
			File[] children = file.listFiles();
			if (children != null) {
				for (File child : children) {
					deleteRecursively(child);
				}
			}
		}
		file.delete();
	}
}
