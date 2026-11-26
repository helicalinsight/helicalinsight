package com.helicalinsight.efw.io;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Rejects archive entries whose names would resolve outside the destination directory.
 * Validation finishes before any entry is written.
 */
public final class ZipEntryPathGuard {

    private static final String REJECTION_PREFIX =
            "The archive was rejected because an entry resolves outside the destination directory: ";

    private ZipEntryPathGuard() {
    }

    public static void assertEntriesStayWithin(ZipFile zipFile, File destinationDir) throws ZipException {
        if (zipFile == null || destinationDir == null) {
            reject("the archive or destination is missing");
            return;
        }
        final Path destinationRoot;
        try {
            destinationRoot = destinationDir.getCanonicalFile().toPath();
        } catch (IOException ex) {
            reject("the destination directory could not be resolved");
            return;
        }
        List<FileHeader> headers = fileHeaders(zipFile);
        if (headers == null) {
            return;
        }
        for (FileHeader header : headers) {
            if (header == null) {
                reject("an archive entry is missing");
                return;
            }
            String name = header.getFileName();
            rejectUnsafeEntryName(name);
            final Path target;
            try {
                target = new File(destinationRoot.toFile(), name).getCanonicalFile().toPath();
            } catch (IOException ex) {
                reject(entry(name, "could not be resolved inside the destination directory"));
                return;
            }
            if (!target.startsWith(destinationRoot)) {
                reject(entry(name, "resolves outside the destination directory"));
            }
        }
    }

    private static void reject(String reason) throws ZipException {
        throw new UnsafeArchiveEntryException(REJECTION_PREFIX + reason);
    }

    private static String entry(String name, String detail) {
        return "entry '" + name + "' " + detail;
    }

    @SuppressWarnings("unchecked")
    private static List<FileHeader> fileHeaders(ZipFile zipFile) throws ZipException {
        return zipFile.getFileHeaders();
    }

    private static void rejectUnsafeEntryName(String name) throws ZipException {
        if (name == null || name.isEmpty() || name.indexOf('\0') >= 0) {
            reject("an entry name is empty or contains a null character");
            return;
        }
        String normalized = name.replace('\\', '/');
        if (normalized.charAt(0) == '/') {
            reject(entry(name, "is an absolute path"));
        }
        if (hasDrivePrefix(normalized)) {
            reject(entry(name, "uses a drive prefix"));
        }
        for (String segment : normalized.split("/", -1)) {
            if ("..".equals(segment)) {
                reject(entry(name, "contains a parent path segment"));
            }
        }
    }

    private static boolean hasDrivePrefix(String normalized) {
        return normalized.length() >= 2
                && Character.isLetter(normalized.charAt(0))
                && normalized.charAt(1) == ':';
    }
}
