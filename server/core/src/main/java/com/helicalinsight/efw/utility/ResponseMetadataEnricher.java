package com.helicalinsight.efw.utility;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.efw.ApplicationInformation;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.model.LicenseMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * Adds product metadata to JSON API response bodies.
 */
public final class ResponseMetadataEnricher {

    private static final Logger logger = LoggerFactory.getLogger(ResponseMetadataEnricher.class);
    private static volatile JsonObject cachedMeta;

    private ResponseMetadataEnricher() {
    }

    public static byte[] enrichJsonBytes(byte[] body) {
        if (body == null || body.length == 0) {
            return body;
        }
        try {
            String text = new String(body, StandardCharsets.UTF_8).trim();
            if (!text.startsWith("{")) {
                return body;
            }
            JsonObject json = JsonParser.parseString(text).getAsJsonObject();
            if (json.has("meta")) {
                return body;
            }
            json.add("meta", getMetaObject());
            return json.toString().getBytes(StandardCharsets.UTF_8);
        } catch (Throwable throwable) {
            logger.debug("Skipping response metadata enrichment for non-JSON body", throwable);
            return body;
        }
    }

    public static JsonObject getMetaObject() {
        if (cachedMeta == null) {
            synchronized (ResponseMetadataEnricher.class) {
                if (cachedMeta == null) {
                    ApplicationInformation appInfo = ApplicationInformation.getInstance();
                    JsonObject meta = new JsonObject();
                    meta.addProperty("productName", appInfo.getProductName());
                    meta.addProperty("version", appInfo.getVersion());
                    meta.addProperty("build", appInfo.getBuild());
                    meta.addProperty("licenseType", resolveLicenseType());
                    meta.addProperty("link", safeProductWebSite());
                    cachedMeta = meta;
                }
            }
        }
        return cachedMeta;
    }

    private static String safeProductWebSite() {
        try {
            return ApplicationProperties.getInstance().getProductWebSite();
        } catch (Throwable throwable) {
            logger.debug("Unable to resolve product website for response metadata", throwable);
            return "";
        }
    }

    private static String resolveLicenseType() {
        try {
            LicenseMetadata licenseMetadata = ApplicationProperties.getInstance().getLicenseMetadata();
            if (licenseMetadata == null || licenseMetadata.licenseKeyType() == null) {
                return "";
            }
            return licenseMetadata.licenseKeyType();
        } catch (Throwable throwable) {
            logger.debug("Unable to resolve license type for response metadata", throwable);
            return "";
        }
    }
}
