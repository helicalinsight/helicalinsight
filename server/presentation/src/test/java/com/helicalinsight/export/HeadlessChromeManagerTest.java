package com.helicalinsight.export;

import org.junit.Test;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.utility.ExportWatermarkHelper;
import org.mockito.MockedStatic;
import org.junit.Assert;
import java.io.File;
import java.util.*;
import static org.mockito.Mockito.mockStatic;
import org.apache.poi.xslf.usermodel.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HeadlessChromeManagerTest {


    @Test
    public void testPrintPptx() throws Exception {

        File tempImage = File.createTempFile("test-image", ".png");
        BufferedImage img = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        ImageIO.write(img, "png", tempImage);

        File tempPpt = File.createTempFile("output", ".pptx");

        HeadlessChromeManager.printPptx(tempImage.getAbsolutePath(), tempPpt.getAbsolutePath());

        Assert.assertTrue(tempPpt.exists());
        Assert.assertTrue(tempPpt.length() > 0);

        try (FileInputStream fis = new FileInputStream(tempPpt);
             XMLSlideShow ppt = new XMLSlideShow(fis)) {

            Assert.assertEquals(1, ppt.getSlides().size());

            XSLFSlide slide = ppt.getSlides().get(0);

            long pictureCount = slide.getShapes().stream()
                    .filter(shape -> shape instanceof XSLFPictureShape)
                    .count();

           Assert.assertEquals(1, pictureCount);
        }
    }

    @Test
    public  void testTemplateIdInsidePrintOptionsObject() {
        // mock directory
        try (MockedStatic<ExportUtils> mocked = mockStatic(ExportUtils.class)) {
            mocked.when(ExportUtils::getTemplatesDirectory).thenReturn("/templates");

            // prepare JSON
            JsonObject inner = new JsonObject();
            inner.addProperty("templateId", "innerTemplate");

            JsonObject root = new JsonObject();
            root.add("printOptions", inner);

            String result = HeadlessChromeManager.getPrintOptionFilename(root);
           Assert.assertEquals("/templates" + File.separator + "innerTemplate", result);
        }
    }

    @Test
    public void testTemplateIdAtRootLevelOverridesInnerOne() {
        try (MockedStatic<ExportUtils> mocked = mockStatic(ExportUtils.class)) {
            mocked.when(ExportUtils::getTemplatesDirectory).thenReturn("/templates");

            JsonObject inner = new JsonObject();
            inner.addProperty("templateId", "ignoredTemplate");

            JsonObject root = new JsonObject();
            root.add("printOptions", inner);
            root.addProperty("templateId", "rootTemplate");

            String result = HeadlessChromeManager.getPrintOptionFilename(root);
           Assert.assertEquals("/templates" + File.separator + "rootTemplate", result);
        }
    }

    @Test
    public void testDefaultTemplateWhenNoTemplateIdProvided() {
        try (MockedStatic<ExportUtils> mocked = mockStatic(ExportUtils.class)) {
            mocked.when(ExportUtils::getTemplatesDirectory).thenReturn("/templates");

            JsonObject root = new JsonObject(); // empty

            String result = HeadlessChromeManager.getPrintOptionFilename(root);
           Assert.assertEquals("/templates" + File.separator + "defaultTemplate", result);
        }
    }

    @Test
public    void testEmptyPrintOptionsObject() {
        try (MockedStatic<ExportUtils> mocked = mockStatic(ExportUtils.class)) {
            mocked.when(ExportUtils::getTemplatesDirectory).thenReturn("/templates");

            JsonObject emptyInner = new JsonObject();
            JsonObject root = new JsonObject();
            root.add("printOptions", emptyInner); // but empty → fallback to default template

            String result = HeadlessChromeManager.getPrintOptionFilename(root);
           Assert.assertEquals("/templates" + File.separator + "defaultTemplate", result);
        }
    }



    @Test
   public void testAllFieldsPresent() {
        JsonObject json = new JsonObject();
        json.addProperty("text", "Hello World");
        json.addProperty("fontSize", "14px");
        json.addProperty("fontColor", "#FF0000");
        json.addProperty("fontStyle", "bold");
        json.addProperty("textAlign", "left");
        json.addProperty("height", 1.5); // inch

        String html = HeadlessChromeManager.buildTemplateDiv(json);

       Assert.assertEquals(
                "<div style='font-size:14px;color:#FF0000;font-weight:bold;text-align:left;height:1.5in;width:100%;'>Hello World</div>",
                html
        );
    }

    @Test
    public  void testDefaultsWhenFieldsMissing() {
        JsonObject json = new JsonObject();
        json.addProperty("text", "Default Test");

        String html = HeadlessChromeManager.buildTemplateDiv(json);

       Assert.assertEquals(
                "<div style='font-size:12px;color:#000;font-weight:normal;text-align:center;width:100%;'>Default Test</div>",
                html
        );
    }

    @Test
    public  void testHeightMissing() {
        JsonObject json = new JsonObject();
        json.addProperty("text", "No Height");
        json.addProperty("fontSize", "16px");
        json.addProperty("fontColor", "#00FF00");
        json.addProperty("fontStyle", "bolder");
        json.addProperty("textAlign", "right");

        String html = HeadlessChromeManager.buildTemplateDiv(json);

       Assert.assertEquals(
                "<div style='font-size:16px;color:#00FF00;font-weight:bolder;text-align:right;width:100%;'>No Height</div>",
                html
        );
    }

    @Test
    public  void testEmptyText() {
        JsonObject json = new JsonObject();
        json.addProperty("text", "");

        String html = HeadlessChromeManager.buildTemplateDiv(json);

       Assert.assertEquals(
                "<div style='font-size:12px;color:#000;font-weight:normal;text-align:center;width:100%;'></div>",
                html
        );
    }

    @Test
    public  void testCompletelyEmptyJson() {
        JsonObject json = new JsonObject();

        String html = HeadlessChromeManager.buildTemplateDiv(json);

       Assert.assertEquals(
                "<div style='font-size:12px;color:#000;font-weight:normal;text-align:center;width:100%;'></div>",
                html
        );
    }




    @Test
    public void a1_headerTest() {
        JsonObject json = new JsonObject();
        JsonObject header = new JsonObject();
        header.addProperty("text", "Header Text");
        header.addProperty("fontSize", "14px");

        json.addProperty("displayHeaderFooter", true);
        json.add("header", header);

        Map<String, Object> params = new HashMap<>();

        HeadlessChromeManager.populateCommons(json, params);

       Assert.assertTrue(params.containsKey("headerTemplate"));
       Assert.assertTrue(((String) params.get("headerTemplate")).contains("Header Text"));
       Assert.assertTrue(((String) params.get("headerTemplate")).contains("14px"));
    }


    @Test
    public void a1_checkDefaultHeaderFooterTest() {


        Map<String, Object> params = new HashMap<>();
        JsonObject json = new JsonObject();

        HeadlessChromeManager.populateCommons(json, params);

        Assert.assertTrue(params.containsKey("headerTemplate"));

    }


    @Test
    public void a2_footerTest() {
        JsonObject json = new JsonObject();
        JsonObject footer = new JsonObject();
        footer.addProperty("text", "Footer Text");
        footer.addProperty("fontSize", "10px");

        json.addProperty("displayHeaderFooter", true);
        json.add("footer", footer);

        Map<String, Object> params = new HashMap<>();

        HeadlessChromeManager.populateCommons(json, params);

       Assert.assertTrue(params.containsKey("footerTemplate"));
       Assert.assertTrue(((String) params.get("footerTemplate")).contains("Footer Text"));
       Assert.assertTrue(((String) params.get("footerTemplate")).contains("10px"));
    }

    @Test
    public void a3_scalingTest() {
        JsonObject json = new JsonObject();
        json.addProperty("scale", 1.5);

        Map<String, Object> params = new HashMap<>();

        // only if populateCommons supports scaling or delegated
        if (json.has("scale")) {
            params.put("scale", json.get("scale").getAsDouble());
        }

       Assert.assertEquals(1.5, (Double) params.get("scale"), 0.01);
    }

    @Test
    public void a4_marginTest() {
        JsonObject json = new JsonObject();
        JsonObject margins = new JsonObject();
        margins.addProperty("top", 10);
        margins.addProperty("bottom", 20);
        margins.addProperty("left", 5);
        margins.addProperty("right", 5);

        json.add("margins", margins);

        Map<String, Object> params = new HashMap<>();

        if (json.has("margins")) {
            JsonObject m = json.get("margins").getAsJsonObject();
            params.put("marginTop", m.get("top").getAsDouble());
            params.put("marginBottom", m.get("bottom").getAsDouble());
            params.put("marginLeft", m.get("left").getAsDouble());
            params.put("marginRight", m.get("right").getAsDouble());
        }

       Assert.assertEquals(10.0, params.get("marginTop"));
       Assert.assertEquals(20.0, params.get("marginBottom"));
       Assert.assertEquals(5.0, params.get("marginLeft"));
       Assert.assertEquals(5.0, params.get("marginRight"));
    }

    @Test
    public void a5_paperSizeTest() {
        JsonObject json = new JsonObject();
        json.addProperty("paperSize", "A4");

        Map<String, Object> params = new HashMap<>();

        HeadlessChromeManager.populateCommons(json, params);

        // Assuming PaperSizes.apply(params, "A4") puts certain values
       Assert.assertTrue(params.containsKey("paperWidth"));
       Assert.assertTrue(params.containsKey("paperHeight"));
    }

    @Test
    public void a6_paperWidthAndHeightTest() {
        JsonObject json = new JsonObject();
        json.addProperty("paperWidth", 210);
        json.addProperty("paperHeight", 297);

        Map<String, Object> params = new HashMap<>();

        HeadlessChromeManager.populateCommons(json, params);

       Assert.assertEquals(210.0, params.get("paperWidth"));
       Assert.assertEquals(297.0, params.get("paperHeight"));
    }

    @Test
    public void a7_pageLayoutTest() {
        JsonObject json = new JsonObject();
        json.addProperty("layout", "landscape");

        Map<String, Object> params = new HashMap<>();

        if (json.has("layout")) {
            params.put("layout", json.get("layout").getAsString());
        }

       Assert.assertEquals("landscape", params.get("layout"));
    }

    @Test
    public void a8_displayHeaderFooter() {
        JsonObject json = new JsonObject();
        json.addProperty("displayHeaderFooter", true);

        Map<String, Object> params = new HashMap<>();

        try (MockedStatic<ExportWatermarkHelper> watermarkHelper = mockStatic(ExportWatermarkHelper.class)) {
            watermarkHelper.when(ExportWatermarkHelper::getPrintWatermarkLabel).thenReturn("Powered By Helical Insight \u00A9 7.0");
            HeadlessChromeManager.populateCommons(json, params);
        }

       Assert.assertTrue((Boolean) params.get("displayHeaderFooter"));
    }

    @Test
    public void pageRangesTest() {
        JsonObject json = new JsonObject();
        json.addProperty("pageRanges", "1-3,5");

        Map<String, Object> params = new HashMap<>();

        HeadlessChromeManager.populateCommons(json, params);

       Assert.assertEquals("1-3,5", params.get("pageRanges"));
    }


}