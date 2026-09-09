package com.helicalinsight.efw.utility;

import com.helicalinsight.efw.io.FileArchive;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class FontFileUtils {

    private FontFileUtils() {
    }

    public static boolean isFontFile(String fileName) {
        String extension = FilenameUtils.getExtension(fileName);
        return isFontExtension(extension);
    }

    public static boolean isFontExtension(String extension) {
        if (extension == null) {
            return false;
        }
        String normalized = extension.toLowerCase(Locale.ROOT);
        return isJvmFontExtension(normalized) || isWebFontExtension(normalized);
    }

    /** Fonts that Java AWT / Jasper can register. */
    public static boolean isJvmFontExtension(String extension) {
        if (extension == null) {
            return false;
        }
        String normalized = extension.toLowerCase(Locale.ROOT);
        return "ttf".equals(normalized) || "otf".equals(normalized) || "ttc".equals(normalized);
    }

    /** Fonts preferred for browser / Chrome via FontFace. */
    public static boolean isWebFontExtension(String extension) {
        if (extension == null) {
            return false;
        }
        String normalized = extension.toLowerCase(Locale.ROOT);
        return "woff".equals(normalized) || "woff2".equals(normalized);
    }

    public static boolean isSupportedUploadExtension(String extension) {
        if (extension == null) {
            return false;
        }
        String normalized = extension.toLowerCase(Locale.ROOT);
        return isFontExtension(normalized) || "jar".equals(normalized) || "zip".equals(normalized);
    }

    public static void copyFontFile(File source, File targetDirectory) throws IOException {
        FileUtils.forceMkdir(targetDirectory);
        File destination = new File(targetDirectory, source.getName());
        if (destination.exists()) {
            destination.delete();
        }
        FileUtils.copyFile(source, destination);
    }

    public static void extractFontEntriesFromJar(File jarFile, File targetDirectory) throws IOException {
        FileUtils.forceMkdir(targetDirectory);
        try (ZipFile zipFile = new ZipFile(jarFile)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory() || !isFontFile(entry.getName())) {
                    continue;
                }
                writeZipEntry(zipFile, entry, targetDirectory);
            }
        }
        FileUtils.copyFileToDirectory(jarFile, targetDirectory);
    }

    public static void extractFontEntriesFromZip(File zipFile, File targetDirectory, String password) throws IOException {
        FileUtils.forceMkdir(targetDirectory);
        File tempDirectory = Files.createTempDirectory("hi-font-upload-").toFile();
        try {
            FileArchive fileArchive = new FileArchive();
            if (!fileArchive.unzip(zipFile.getAbsolutePath(), tempDirectory.getAbsolutePath(), password)) {
                throw new IOException("Selected file to upload couldn't be unzipped.");
            }
            collectFontFiles(tempDirectory, targetDirectory);
        } finally {
            FileUtils.deleteQuietly(tempDirectory);
        }
    }

    private static void collectFontFiles(File sourceDirectory, File targetDirectory) throws IOException {
        File[] children = sourceDirectory.listFiles();
        if (children == null) {
            return;
        }
        for (File child : children) {
            if (child.isDirectory()) {
                collectFontFiles(child, targetDirectory);
            } else if (isFontFile(child.getName())) {
                copyFontFile(child, targetDirectory);
            } else if ("jar".equalsIgnoreCase(FilenameUtils.getExtension(child.getName()))) {
                extractFontEntriesFromJar(child, targetDirectory);
            }
        }
    }

    private static void writeZipEntry(ZipFile zipFile, ZipEntry entry, File targetDirectory) throws IOException {
        String entryName = new File(entry.getName()).getName();
        File destination = new File(targetDirectory, entryName);
        if (destination.exists()) {
            destination.delete();
        }
        try (InputStream inputStream = zipFile.getInputStream(entry);
             FileOutputStream outputStream = new FileOutputStream(destination)) {
            IOUtils.copy(inputStream, outputStream);
        }
    }
}
