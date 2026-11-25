package com.helicalinsight.efw.io;

import net.lingala.zip4j.exception.ZipException;

/**
 * An archive entry would resolve outside the destination directory.
 * The message includes the specific reason.
 */
public class UnsafeArchiveEntryException extends ZipException {

    public UnsafeArchiveEntryException(String message) {
        super(message);
    }
}
