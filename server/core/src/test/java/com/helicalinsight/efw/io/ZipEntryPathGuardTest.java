package com.helicalinsight.efw.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class ZipEntryPathGuardTest {

	private static final String MARKER = "zip-slip-9631";

	private File sandbox;

	@Before
	public void createSandbox() throws Exception {
		sandbox = Files.createTempDirectory("zip-slip-guard-").toFile();
	}

	@After
	public void deleteSandbox() {
		deleteRecursively(sandbox);
	}

	@Test
	public void traversalEntryIsRejectedAndWritesNothing() throws Exception {
		File archive = archive("marker.crt", "../zipslip-marker.txt", "ok.txt");
		File extractDir = extractDir("marker_user");

		assertFalse(new FileArchive().unzip(archive.getAbsolutePath(), extractDir.getAbsolutePath(), null));

		assertFalse(new File(extractDir.getParentFile(), "zipslip-marker.txt").exists());
		assertFalse(new File(extractDir, "ok.txt").exists());
		assertFalse(new File(extractDir, "zipslip-marker.txt").exists());
	}

	@Test
	public void nestedTraversalEntryIsRejectedAndWritesNothing() throws Exception {
		File archive = archive("nested.crt", "foo/../../outside.txt");
		File extractDir = extractDir("nested_user");

		assertFalse(new FileArchive().unzip(archive.getAbsolutePath(), extractDir.getAbsolutePath(), null));

		assertFalse(new File(extractDir.getParentFile(), "outside.txt").exists());
		assertFalse(new File(sandbox, "outside.txt").exists());
	}

	@Test
	public void absoluteEntryNameIsRejected() throws Exception {
		File archive = archive("absolute.crt", "/zipslip-abs-marker.txt");
		File extractDir = extractDir("absolute_user");

		assertGuardRejects(archive, extractDir);
		assertFalse(new FileArchive().unzip(archive.getAbsolutePath(), extractDir.getAbsolutePath(), null));
		assertFalse(new File(extractDir, "zipslip-abs-marker.txt").exists());
	}

	@Test
	public void drivePrefixedEntryNameIsRejected() throws Exception {
		
		String entryName = File.separatorChar == '\\'
		        ? "C:/zipslip-abs-marker.txt"
		        : "C:\\zipslip-abs-marker.txt";
		File archive = archive("drive.crt", entryName);
		File extractDir = extractDir("drive_user");
		assertGuardRejects(archive, extractDir);
		assertFalse(new File(extractDir, "zipslip-abs-marker.txt").exists());
	}

	@Test
	public void legitimateNestedEntryIsExtracted() throws Exception {
		File archive = archive("inside.crt", "nested/inside.txt");
		File extractDir = extractDir("inside_user");

		assertTrue(new FileArchive().unzip(archive.getAbsolutePath(), extractDir.getAbsolutePath(), null));

		File extracted = new File(extractDir, "nested/inside.txt");
		assertTrue(extracted.isFile());
		assertEquals(MARKER, Files.readString(extracted.toPath()));
		assertFalse(new File(extractDir.getParentFile(), "inside.txt").exists());
	}

	private void assertGuardRejects(File archive, File extractDir) throws Exception {
		try {
			ZipEntryPathGuard.assertEntriesStayWithin(new ZipFile(archive), extractDir);
			fail("expected ZipException");
		} catch (ZipException expected) {
			assertTrue(expected.getMessage().contains("outside the destination"));
		}
	}

	private File extractDir(String name) {
		File dir = new File(sandbox, "System" + File.separator + "Temp" + File.separator + name);
		assertTrue(dir.mkdirs());
		return dir;
	}

	private File archive(String fileName, String... entryNames) throws Exception {
		File archive = new File(sandbox, fileName);
		try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(archive))) {
			for (String entryName : entryNames) {
				zip.putNextEntry(new ZipEntry(entryName));
				zip.write(MARKER.getBytes(StandardCharsets.UTF_8));
				zip.closeEntry();
			}
		}
		return archive;
	}

	private static void deleteRecursively(File file) {
		if (file == null || !file.exists()) {
			return;
		}
		File[] children = file.listFiles();
		if (children != null) {
			for (File child : children) {
				deleteRecursively(child);
			}
		}
		file.delete();
	}
}
