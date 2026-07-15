package com.helicalinsight.efw.utility;

import java.util.regex.Pattern;

/**
 * Normalizes legacy Derby JDBC driver-class references to the class names that are actually
 * registered/available in Derby 10.17.
 * <p>
 * The old internal embedded auto-loaded driver ({@code org.apache.derby.jdbc.AutoloadedDriver})
 * was relocated to {@code org.apache.derby.iapi.jdbc.AutoloadedDriver}, and the network client
 * driver ({@code org.apache.derby.jdbc.ClientDriver}) was relocated to
 * {@code org.apache.derby.client.ClientAutoloadedDriver}. Payloads that still carry the old names
 * would otherwise fail driver lookup at connection time.
 * <p>
 * This shim is intentionally inert in production: it only rewrites anything when explicitly
 * enabled, which today happens only during test execution (via the classpath marker
 * {@code /derby-legacy-driver-compat.enabled} shipped in test resources, or the system property
 * {@code hi.derby.legacyDriverCompat=true}). When disabled, {@link #normalize(String)} returns its
 * input unchanged, so production driver resolution is not affected.
 */
public final class DriverClassCompat {

    private static final boolean ENABLED =
            Boolean.getBoolean("hi.derby.legacyDriverCompat")
                    || DriverClassCompat.class.getResource("/derby-legacy-driver-compat.enabled") != null;

    private static final Pattern LEGACY_DERBY_EMBEDDED =
            Pattern.compile("org\\.apache\\.derby\\.jdbc\\.(?:AutoloadedDriver|EmbeddedDriver)");
    private static final String DERBY_EMBEDDED = "org.apache.derby.iapi.jdbc.AutoloadedDriver";
    private static final String DERBY_CLIENT = "org.apache.derby.client.ClientAutoloadedDriver";

    private DriverClassCompat() {
    }

    public static String normalize(String driverClass) {
        // Inert unless enabled (test execution only). Fast prefix guard keeps non-Derby drivers free.
        if (!ENABLED || driverClass == null || !driverClass.startsWith("org.apache.derby.jdbc.")) {
            return driverClass;
        }
        if ("org.apache.derby.jdbc.ClientDriver".equals(driverClass)) {
            return DERBY_CLIENT;
        }
        return LEGACY_DERBY_EMBEDDED.matcher(driverClass).replaceAll(DERBY_EMBEDDED);
    }
}
