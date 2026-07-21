package com.helicalinsight.efw.utility;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import org.apache.poi.ooxml.POIXMLProperties;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

/**
 * Shared watermark utilities for tabular and document exports.
 */
public final class ExportWatermarkHelper {

    private static final Set<String> WATERMARK_LICENSE_TYPES = Set.of("COMMUNITY","Community","");
    private static final Color IMAGE_WATERMARK_COLOR = new Color(73, 143, 222);

    private ExportWatermarkHelper() {
    }

    public static boolean shouldApplyWatermark(JsonObject conversionOptions) {
        if (!GsonUtility.optBooleanValue(conversionOptions, "isAdhoc", false)) {
            return false;
        }
        return isWatermarkLicense(resolveLicenseType());
    }

    public static boolean shouldApplyWatermark(JsonObject conversionOptions, String licenseType) {
        if (!GsonUtility.optBooleanValue(conversionOptions, "isAdhoc", false)) {
            return false;
        }
        return isWatermarkLicense(licenseType);
    }

    /**
     * Whether print exports (PDF/PNG/PPTX via Headless Chrome) should include branding.
     */
    public static boolean shouldApplyPrintWatermark() {
        return shouldApplyPrintWatermark(resolveLicenseType());
    }

    public static boolean shouldApplyPrintWatermark(String licenseType) {
        return isWatermarkLicense(licenseType);
    }

    /**
     * Watermark label for print header/footer templates. Returns empty when branding does not apply.
     */
    public static String getPrintWatermarkLabel() {
        return getPrintWatermarkLabel(resolveLicenseType());
    }

    public static String getPrintWatermarkLabel(String licenseType) {
        return isWatermarkLicense(licenseType) ? getWatermarkText() : "";
    }

    public static boolean isWatermarkLicense(String licenseType) {
        return  WATERMARK_LICENSE_TYPES.contains(licenseType);
    }

    public static String getWatermarkText() {
        JsonObject meta = ResponseMetadataEnricher.getMetaObject();
        String productName = GsonUtility.optString(meta, "productName");
        String version = GsonUtility.optString(meta, "version");
        return "Powered By %s \u00A9 %s".formatted(productName, version);
    }

    public static String getWatermarkLink() {
        JsonObject meta = ResponseMetadataEnricher.getMetaObject();
        return GsonUtility.optString(meta, "link");
    }

    public static void applyWorkbookMetadata(XSSFWorkbook workbook) {
        JsonObject meta = ResponseMetadataEnricher.getMetaObject();
        String productName = GsonUtility.optString(meta, "productName");
        String version = GsonUtility.optString(meta, "version");
        String poweredBy = getWatermarkText();

        POIXMLProperties.CoreProperties coreProperties = workbook.getProperties().getCoreProperties();
        coreProperties.setTitle(productName);
        coreProperties.setCreator(productName);
        coreProperties.setKeywords("%s,%s,%s".formatted(productName, version, poweredBy));
    }

    public static void applySlideShowMetadata(XMLSlideShow slideShow) {
        JsonObject meta = ResponseMetadataEnricher.getMetaObject();
        String productName = GsonUtility.optString(meta, "productName");
        String version = GsonUtility.optString(meta, "version");
        String poweredBy = getWatermarkText();

        POIXMLProperties.CoreProperties coreProperties = slideShow.getProperties().getCoreProperties();
        coreProperties.setTitle(productName);
        coreProperties.setCreator(productName);
        coreProperties.setKeywords("%s,%s,%s".formatted(productName, version, poweredBy));
    }

    /**
     * Draws a right-aligned watermark on the image when print branding applies.
     */
    public static BufferedImage applyImageWatermark(BufferedImage source) {
        return applyImageWatermark(source, resolveLicenseType());
    }

    public static BufferedImage applyImageWatermark(BufferedImage source, String licenseType) {
        if (!shouldApplyPrintWatermark(licenseType) || source == null) {
            return source;
        }

        int imageType = source.getType() == BufferedImage.TYPE_CUSTOM ? BufferedImage.TYPE_INT_ARGB : source.getType();
        BufferedImage watermarked = new BufferedImage(source.getWidth(), source.getHeight(), imageType);
        Graphics2D graphics = watermarked.createGraphics();
        try {
            graphics.drawImage(source, 0, 0, null);
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            String watermarkText = getWatermarkText();
            int fontSize = Math.max(10, source.getWidth() / 120);
            graphics.setFont(new Font("Serif", Font.PLAIN, fontSize));
            graphics.setColor(IMAGE_WATERMARK_COLOR);

            FontMetrics fontMetrics = graphics.getFontMetrics();
            int padding = Math.max(8, source.getWidth() / 200);
            int x = source.getWidth() - fontMetrics.stringWidth(watermarkText) - padding;
            int y = source.getHeight() - padding;

            graphics.drawString(watermarkText, Math.max(padding, x), y);
        } finally {
            graphics.dispose();
        }
        return watermarked;
    }

    public static void writeWatermarkedImage(BufferedImage image, File outputFile, String format) throws IOException {
        writeWatermarkedImage(image, outputFile, format, resolveLicenseType());
    }

    public static void writeWatermarkedImage(BufferedImage image, File outputFile, String format, String licenseType)
            throws IOException {
        if (image == null) {
            throw new IOException("Image is null");
        }
        String normalizedFormat = format.equalsIgnoreCase("jpeg") ? "jpg" : format.toLowerCase();
        if (!shouldApplyPrintWatermark(licenseType)) {
            ImageIO.write(image, normalizedFormat, outputFile);
            return;
        }
        BufferedImage watermarked = applyImageWatermark(image, licenseType);
        writeImageWithMetadata(watermarked, outputFile, normalizedFormat);
    }

    static void writeImageWithMetadata(BufferedImage image, File file, String format) throws IOException {
        JsonObject meta = ResponseMetadataEnricher.getMetaObject();
        String productName = GsonUtility.optString(meta, "productName");
        String version = GsonUtility.optString(meta, "version");
        String poweredBy = getWatermarkText();
        String keywords = "%s,%s,%s".formatted(productName, version, poweredBy);

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(format);
        if (!writers.hasNext()) {
            ImageIO.write(image, format, file);
            return;
        }

        ImageWriter writer = writers.next();
        try (ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(file)) {
            writer.setOutput(imageOutputStream);
            ImageWriteParam writeParam = writer.getDefaultWriteParam();
            IIOMetadata metadata = writer.getDefaultImageMetadata(
                    ImageTypeSpecifier.createFromBufferedImageType(image.getType()), writeParam);
            if (metadata != null && metadata.isStandardMetadataFormatSupported()) {
                IIOMetadataNode root = new IIOMetadataNode("javax_imageio_1.0");
                IIOMetadataNode text = new IIOMetadataNode("Text");
                appendTextEntry(text, "Title", productName);
                appendTextEntry(text, "Author", productName);
                appendTextEntry(text, "Description", poweredBy);
                appendTextEntry(text, "Software", productName + " " + version);
                appendTextEntry(text, "Comment", keywords);
                root.appendChild(text);
                metadata.mergeTree("javax_imageio_1.0", root);
            }
            writer.write(null, new IIOImage(image, null, metadata), writeParam);
        } finally {
            writer.dispose();
        }
    }

    private static void appendTextEntry(IIOMetadataNode textNode, String key, String value) {
        IIOMetadataNode entry = new IIOMetadataNode("TextEntry");
        entry.setAttribute("keyword", key);
        entry.setAttribute("value", value);
        entry.setAttribute("encoding", "utf-8");
        entry.setAttribute("compression", "none");
        textNode.appendChild(entry);
    }

    private static String resolveLicenseType() {
        JsonObject meta = ResponseMetadataEnricher.getMetaObject();
        return GsonUtility.optString(meta, "licenseType");
    }
}
