package com.helicalinsight.efw.utility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import com.google.gson.JsonObject;

public class ExportWatermarkHelperTest {

    @Test
    public void shouldApplyWatermark_whenAdhocAndCommunityLicense() {
        JsonObject conversionOptions = new JsonObject();
        conversionOptions.addProperty("isAdhoc", true);

        assertTrue(ExportWatermarkHelper.shouldApplyWatermark(conversionOptions, "Community"));
    }


    @Test
    public void shouldNotApplyWatermark_whenNotAdhoc() {
        JsonObject conversionOptions = new JsonObject();
        conversionOptions.addProperty("isAdhoc", false);
        assertFalse(ExportWatermarkHelper.shouldApplyWatermark(conversionOptions, "Community"));
    }

    @Test
    public void shouldNotApplyWatermark_whenCommercialLicense() {
        JsonObject conversionOptions = new JsonObject();
        conversionOptions.addProperty("isAdhoc", true);

        assertFalse(ExportWatermarkHelper.shouldApplyWatermark(conversionOptions, "COMMERCIAL"));
    }

    @Test
    public void shouldNotApplyWatermark_whenUnlimitedLicense() {
        JsonObject conversionOptions = new JsonObject();
        conversionOptions.addProperty("isAdhoc", true);

        assertFalse(ExportWatermarkHelper.shouldApplyWatermark(conversionOptions, "UNLIMITED"));
    }

    @Test
    public void isWatermarkLicense_acceptsCommunityOnly() {
        assertFalse(ExportWatermarkHelper.isWatermarkLicense("TRIAL"));
        assertFalse(ExportWatermarkHelper.isWatermarkLicense("DEVELOPER"));
        assertFalse(ExportWatermarkHelper.isWatermarkLicense("COMMERCIAL"));
        assertTrue(ExportWatermarkHelper.isWatermarkLicense(""));
        assertTrue(ExportWatermarkHelper.isWatermarkLicense("Community"));
        assertTrue(ExportWatermarkHelper.isWatermarkLicense("COMMUNITY"));
    }

    @Test
    public void getWatermarkText_containsProductNameAndVersion() {
        String watermarkText = ExportWatermarkHelper.getWatermarkText();

        assertTrue(watermarkText.startsWith("Powered By "));
        assertTrue(watermarkText.contains("\u00A9"));
        assertFalse(watermarkText.isEmpty());
    }

    @Test
    public void getWatermarkLink_isNotNull() {
        assertEquals(ResponseMetadataEnricher.getMetaObject().get("link").getAsString(),
                ExportWatermarkHelper.getWatermarkLink());
    }

    @Test
    public void shouldApplyPrintWatermark_forCommunityOnly() {
        assertFalse(ExportWatermarkHelper.shouldApplyPrintWatermark("TRIAL"));
        assertFalse(ExportWatermarkHelper.shouldApplyPrintWatermark("DEVELOPER"));
        assertFalse(ExportWatermarkHelper.shouldApplyPrintWatermark("COMMERCIAL"));
        assertTrue(ExportWatermarkHelper.shouldApplyPrintWatermark("Community"));
    }

    @Test
    public void getPrintWatermarkLabel_isEmptyForCommercialLicense() {
        assertEquals("", ExportWatermarkHelper.getPrintWatermarkLabel("COMMERCIAL"));
    }

    @Test
    public void getPrintWatermarkLabel_containsPoweredByForCommunity() {
        String label = ExportWatermarkHelper.getPrintWatermarkLabel("Community");
        assertTrue(label.startsWith("Powered By "));
        assertTrue(label.contains("\u00A9"));
    }

    @Test
    public void applyWorkbookMetadata_setsCoreProperties() throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        ExportWatermarkHelper.applyWorkbookMetadata(workbook);

        String productName = ResponseMetadataEnricher.getMetaObject().get("productName").getAsString();
        assertEquals(productName, workbook.getProperties().getCoreProperties().getTitle());
        assertEquals(productName, workbook.getProperties().getCoreProperties().getCreator());
        assertTrue(workbook.getProperties().getCoreProperties().getKeywords().contains(productName));
        workbook.close();
    }

    @Test
    public void applySlideShowMetadata_setsCoreProperties() throws Exception {
        XMLSlideShow slideShow = new XMLSlideShow();
        ExportWatermarkHelper.applySlideShowMetadata(slideShow);

        String productName = ResponseMetadataEnricher.getMetaObject().get("productName").getAsString();
        assertEquals(productName, slideShow.getProperties().getCoreProperties().getTitle());
        assertEquals(productName, slideShow.getProperties().getCoreProperties().getCreator());
        assertTrue(slideShow.getProperties().getCoreProperties().getKeywords().contains(productName));
        slideShow.close();
    }

    @Test
    public void applyImageWatermark_forCommunity_preservesImageSize() {
        BufferedImage source = new BufferedImage(400, 300, BufferedImage.TYPE_INT_RGB);
        BufferedImage watermarked = ExportWatermarkHelper.applyImageWatermark(source, "Community");

        assertEquals(400, watermarked.getWidth());
        assertEquals(300, watermarked.getHeight());
    }

    @Test
    public void applyImageWatermark_forCommercialLicense_returnsOriginalImage() {
        BufferedImage source = new BufferedImage(400, 300, BufferedImage.TYPE_INT_RGB);
        assertSame(source, ExportWatermarkHelper.applyImageWatermark(source, "COMMERCIAL"));
    }

    @Test
    public void writeWatermarkedImage_writesPngWithMetadataForCommunity() throws Exception {
        BufferedImage source = new BufferedImage(200, 150, BufferedImage.TYPE_INT_RGB);
        File outputFile = File.createTempFile("watermark-test", ".png");

        ExportWatermarkHelper.writeWatermarkedImage(source, outputFile, "png", "Community");

        assertTrue(outputFile.exists());
        assertTrue(outputFile.length() > 0);
        assertNotNull(ImageIO.read(outputFile));
        outputFile.delete();
    }
}
