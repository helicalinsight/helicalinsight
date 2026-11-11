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

    private static final String OUTSIDE_DESTINATION = "Archive entry resolves outside the destination directory";

    private ZipEntryPathGuard() {
    }

    public static void assertEntriesStayWithin(ZipFile zipFile, File destinationDir) throws ZipException {
        if (zipFile == null || destinationDir == null) {
            throw new ZipException(OUTSIDE_DESTINATION);
        }
        final Path destinationRoot;
        try {
            destinationRoot = destinationDir.getCanonicalFile().toPath();
        } catch (IOException ex) {
            throw new ZipException(OUTSIDE_DESTINATION);
        }
        List<FileHeader> headers = fileHeaders(zipFile);
        if (headers == null) {
            return;
        }
        for (FileHeader header : headers) {
            if (header == null) {
                throw new ZipException(OUTSIDE_DESTINATION);
            }
            String name = header.getFileName();
            rejectUnsafeEntryName(name);
            final Path target;
            try {
                target = new File(destinationRoot.toFile(), name).getCanonicalFile().toPath();
            } catch (IOException ex) {
                throw new ZipException(OUTSIDE_DESTINATION);
            }
            if (!target.startsWith(destinationRoot)) {
                throw new ZipException(OUTSIDE_DESTINATION);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static List<FileHeader> fileHeaders(ZipFile zipFile) throws ZipException {
        return zipFile.getFileHeaders();
    }

    private static void rejectUnsafeEntryName(String name) throws ZipException {
        if (name == null || name.isEmpty() || name.indexOf('\0') >= 0) {
            throw new ZipException(OUTSIDE_DESTINATION);
        }
        String normalized = name.replace('\\', '/');
        if (normalized.charAt(0) == '/' || hasDrivePrefix(normalized)) {
            throw new ZipException(OUTSIDE_DESTINATION);
        }
        for (String segment : normalized.split("/", -1)) {
            if ("..".equals(segment)) {
                throw new ZipException(OUTSIDE_DESTINATION);
            }
        }
    }

    private static boolean hasDrivePrefix(String normalized) {
        return normalized.length() >= 2
                && Character.isLetter(normalized.charAt(0))
                && normalized.charAt(1) == ':';
    }
}
