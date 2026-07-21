package com.helicalinsight.export;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.customauth.CipherUtils;
import com.helicalinsight.admin.model.User;
import com.helicalinsight.admin.service.UserService;
import com.helicalinsight.admin.utils.JacksonUtility;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.ExportWatermarkHelper;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.*;
import java.util.logging.Level;

/**
 * The instance of this class manages service calls of thread which generates
 * the pdf or png or jpeg formats on the file system.
 *
 * @author Somen
 */
public class HeadlessChromeManager extends PhantomExportService {
    private final static Logger logger = LoggerFactory.getLogger(HeadlessChromeManager.class);


    @Override
    public List<String> handlePhantomjs(JsonObject set) {
        MarginHandler marginHandler = ApplicationContextAccessor.getBean(MarginHandler.class);
        String fileName = getPrintOptionFilename(set);
        File jsonFile = new File(fileName + ExportUtils.JSON_EXTENSION);
        File jsFile = new File(fileName + ExportUtils.JS_EXTENSION);
        String jsContent = null;
        JsonObject jsonConfig = new JsonObject();
        if (jsonFile.exists()) {
            jsonConfig = ExportUtils.getFileAsJsonObject(jsonFile.getAbsolutePath());
        }
        if (jsFile.exists()) {
            jsContent = ExportUtils.getFileAsString(jsFile.getAbsolutePath());
        }
        JSONObject settings = JSONObject.fromObject(set.toString());

        ObjectNode chrome = JacksonUtility.fromObject(ReportsProcessor.getChromeDriverLocation());

        String destinationFile = settings.getString("destinationFile");
        String chromeDriverLocation = chrome.get("chromeDriverLocation").asText();
        String chromeLocation = chrome.get("chromeLocation").asText();

        String userName = settings.getString("username");
        String password = settings.optString("passCode", null);
        String orgName = settings.optString("organization", null);
        String baseUrl = settings.getString("domain");
        JSONArray formatArray = getAllFormats(settings);
        baseUrl = baseUrl.replace("hi.html", "");
        String reportUrl = sanitizeReportUri(settings);
        String format;


        String property = System.getProperty("webdriver.chrome.driver");
        if (property == null || property.length() == 0) {
            System.setProperty("webdriver.chrome.driver", chromeDriverLocation);
        }
        ChromeOptions options = new ChromeOptions();
        setDefaultOptions(options);
        JsonObject bodyElement = jsonConfig.get("body").getAsJsonObject();
        int dimensionHeight = bodyElement.has("viewPortHeight") ? bodyElement.get("viewPortHeight").getAsInt() : 1200;
        int dimensionWidth = bodyElement.has("viewPortWidth") ? bodyElement.get("viewPortWidth").getAsInt() : 1920;

        /*if (formatArray.toString().contains("pptx") || formatArray.toString().contains("png")) {
            options.addArguments("--hide-scrollbars");
            dimensionHeight = 3000;
        }
*/
        if (StringUtils.isNotBlank(chromeLocation) && new File(chromeLocation).exists()) {
            options.setBinary(chromeLocation);
        }
        ChromeDriver chromeDriver = new ChromeDriver(options);

        chromeDriver.manage().window().setSize(new Dimension(dimensionWidth, dimensionHeight));
        String errorString = null;
        List<String> stringList = new ArrayList<>();
        try {
            WebDriverWait webDriverWait = new WebDriverWait(chromeDriver, Duration.ofSeconds(20));
            String loginUrl = "";
            String loginUrl2 = "";
            if (password == null || password.isEmpty()) {
                String combinedUsername = userName + (orgName != null ? (":" + orgName) : "");
                UserService userService = (UserService) ApplicationContextAccessor.getBean("userDetailsService");
                String userName2 = "downloadManager";
                User user = userService.findUserByNameNorgNull(userName2);
                loginUrl = baseUrl + "?j_username=" + userName2 + "&j_password=" + CipherUtils.decrypt(user.getPassword());
                loginUrl2 = baseUrl + "mock/impersonate?username=" + combinedUsername;
            } else {
                loginUrl = baseUrl + "?j_username=" + userName + "&j_password=" + password + (orgName != null ? "&j_organization=" + orgName : "");
            }

            chromeDriver.get(loginUrl);
            int initialWaitMillis = 5000;
            Thread.sleep(initialWaitMillis);

            webDriverWait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
            if (!loginUrl2.isEmpty()) {
                chromeDriver.get(loginUrl2);
                Thread.sleep(initialWaitMillis);
            }
            chromeDriver.get(reportUrl);
            Object o = ((JavascriptExecutor) chromeDriver).executeScript(AjaxWait.getFileContent("PreWaitingScript.js"));
            webDriverWait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));

            Thread.sleep(10000);
            try {
                WebDriverWait wait = new WebDriverWait(chromeDriver, Duration.ofMillis(60000));
                wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
                AjaxWait.waitForAjax(chromeDriver);
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int renderWaitMillis = 10000;
            Thread.sleep(renderWaitMillis);
            chromeDriver.executeScript("return document.readyState");

            AjaxWait.waitForAjax(chromeDriver);
            //Additional 3 second wait
            int postAjaxMillis = 3000;
            Thread.sleep(postAjaxMillis);

            LoggingPreferences logs = new LoggingPreferences();
            logs.enable(LogType.BROWSER, Level.ALL);
            options.setCapability("goog:loggingPrefs", logs);
            if (bodyElement.has("hasScript") && bodyElement.get("hasScript").getAsBoolean() && jsContent != null) {
                chromeDriver.executeScript(jsContent);
            }
            Thread.sleep(postAjaxMillis);

            for (Object s : formatArray) {
                format = s.toString();
                String outputLocation = destinationFile + "." + (format.equalsIgnoreCase("pdf") ? "pdf" : "png");
                Map<String, Object> params = new HashMap<>();

                String command = format.equals("pdf") ? "Page.printToPDF" : "Page.captureScreenshot";
                params.put("displayHeaderFooter", Boolean.FALSE);
                params.put("printBackground", Boolean.TRUE);
                params.put("scale", Double.valueOf(bodyElement.has("scaling") ? bodyElement.get("scaling").getAsString() : "0.5"));
                params.put("landscape", bodyElement.has("layout") ? bodyElement.get("layout").getAsString().equals("landscape") ? Boolean.TRUE : Boolean.FALSE : Boolean.FALSE);
                marginHandler.applyMargins(bodyElement, params);
                populateCommons(bodyElement, params);


                if (format.equalsIgnoreCase("pptx")) {
                    params.put("format", "png"); // Set the format to PNG
                    params.remove("scale");
                } else if (!format.equalsIgnoreCase("pdf")) {
                    params.put("format", format);
                }
                if (format.equalsIgnoreCase("jpg") || format.equalsIgnoreCase("jpeg")) {
                    TakesScreenshot screenshot = (TakesScreenshot) chromeDriver;
                    String base64Screenshot = screenshot.getScreenshotAs(OutputType.BASE64);
                    byte[] imageBytes = java.util.Base64.getDecoder().decode(base64Screenshot);
                    BufferedImage read = ImageIO.read(new ByteArrayInputStream(imageBytes));
                    File outputfile = new File(destinationFile + ".jpg");
                    ExportWatermarkHelper.writeWatermarkedImage(read, outputfile, "jpg");

                } else {
                    if(format.equals("png")) {
                        Map<String, Object> metrics = new HashMap<>();
                        metrics.put("width", 1920);
                        metrics.put("height", 3000);   // big enough to cover full page
                        metrics.put("deviceScaleFactor", 1);
                        metrics.put("mobile", false);

                        Map<String, Object> output2 = chromeDriver.executeCdpCommand("Emulation.setDeviceMetricsOverride", metrics);
                        Thread.sleep(3000);
                    }

                    Map<String, Object> output = chromeDriver.executeCdpCommand(command, params);
                    byte[] byteArray = java.util.Base64.getDecoder().decode((String) output.get("data"));
                    if (format.equalsIgnoreCase("png")) {
                        BufferedImage image = ImageIO.read(new ByteArrayInputStream(byteArray));
                        ExportWatermarkHelper.writeWatermarkedImage(image, new File(outputLocation), "png");
                    } else {
                        try (FileOutputStream fileOutputStream = new FileOutputStream(outputLocation)) {
                            fileOutputStream.write(byteArray);
                        }
                    }
                }
                if (format.equalsIgnoreCase("pptx")) {
                    printPptx(outputLocation, destinationFile + ".pptx");
                }

                stringList.add(destinationFile);

                stringList.add(errorString);
            }
            LogEntries browserLogs = chromeDriver.manage().logs().get(LogType.BROWSER);
            for (LogEntry entry : browserLogs) {
                logger.info(entry.getLevel() + " : " + entry.getMessage());
            }

            String url = baseUrl + "logout";
            chromeDriver.get(url);
            Thread.sleep(5000);
        } catch (Exception e) {
            errorString = e.getMessage();
            stringList.add(errorString);
            logger.error("Exception occurred during screen capture ", e);
        }
        chromeDriver.close();
        chromeDriver.quit();


        return stringList;
    }

    @NotNull
    private static String sanitizeReportUri(JSONObject settings) {
        String inputPath = settings.getString("reportSourceUri");
        try {
            inputPath = new java.net.URI(inputPath).getPath();
            inputPath = inputPath.replace("//hi.html#", "/#/report-viewer");
            inputPath = inputPath.replace("hi.html#", "#/report-viewer");
            inputPath = inputPath.replace("hi.html", "#/report-viewer");
        } catch (URISyntaxException e) {
            logger.error("There is a problem in the URI");
        }
        return inputPath;
    }

    private static JSONArray getAllFormats(JSONObject settings) {
        JSONArray formatArray = settings.optJSONArray("format");
        if (formatArray == null) {
            String format = settings.optString("format");
            String formatArr[] = format.replace("]", "").replace("[", "").split(",");
            for (int i = 0; i < formatArr.length; i++) {
                formatArr[i] = formatArr[i].trim(); // Trim spaces from each element
            }
            formatArray = JSONArray.fromObject(Arrays.asList(formatArr));

        }
        return formatArray;
    }

    public static void populateCommons(JsonObject bodyElement, Map<String, Object> params) {
        if (bodyElement.has("paperSize")) {
            PaperSizes.apply(params, bodyElement.get("paperSize").getAsString());
        }
        if (bodyElement.has("paperHeight")) {
            params.put("paperHeight", bodyElement.get("paperHeight").getAsDouble());
        }

        if (bodyElement.has("paperWidth")) {
            params.put("paperWidth", bodyElement.get("paperWidth").getAsDouble());
        }
        if (!bodyElement.has("displayHeaderFooter")) {
            bodyElement.addProperty("displayHeaderFooter", Boolean.TRUE);
        }

        if (bodyElement.has("displayHeaderFooter")) {
            boolean displayHeaderFooter = bodyElement.get("displayHeaderFooter").getAsBoolean();
            params.put("displayHeaderFooter", displayHeaderFooter);
            if (displayHeaderFooter) {
                String defaultFileName = ExportUtils.getTemplatesDirectory() + File.separator + "defaultTemplate.json";
                JsonObject defaultJsonFile = ExportUtils.getFileAsJsonObject(defaultFileName);
                JsonObject bodyElementDefault = defaultJsonFile.get("body").getAsJsonObject();

                String poweredBy = ExportWatermarkHelper.getPrintWatermarkLabel();
                String brandLink = ExportWatermarkHelper.getWatermarkLink();
                
                String headerTemplate = bodyElement.has("headerTemplate") ? bodyElement.get("headerTemplate").getAsString() : bodyElementDefault.get("headerTemplate").getAsString();
                headerTemplate=headerTemplate.formatted(brandLink, poweredBy);
                params.put("headerTemplate", headerTemplate);
                String footerTemplate = bodyElement.has("footerTemplate") ?bodyElement.get("footerTemplate").getAsString() : bodyElementDefault.get("footerTemplate").getAsString();
                footerTemplate = footerTemplate.formatted(brandLink, poweredBy);
                params.put("footerTemplate", footerTemplate);
                if (bodyElement.has("footer")) {
                    params.put("footerTemplate", buildTemplateDiv(bodyElement.get("footer").getAsJsonObject()));
                }
                if (bodyElement.has("header")) {
                    params.put("headerTemplate", buildTemplateDiv(bodyElement.get("header").getAsJsonObject()));
                }
            }


        }
        if (bodyElement.has("pageRanges")) {
            params.put("pageRanges", bodyElement.get("pageRanges").getAsString());
        }
    }

    private static void setDefaultOptions(ChromeOptions options) {
        options.addArguments("--headless", "--run-all-compositor-stages-before-draw");
        //options.setCapability("chrome.switches", Arrays.asList("--ignore-certificate-errors,--web-security=false,--ssl-protocol=any,--ignore-ssl-errors=true"));

        options.addArguments("start-maximized"); // open Browser in maximized mode
        options.addArguments("disable-infobars"); // disabling infobars
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-extensions"); // disabling extensions
        options.addArguments("--disable-gpu"); // applicable to windows os only
        options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
        options.addArguments("--no-sandbox"); // Bypass OS security model
        options.addArguments("--enable-print-browser");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--ignore-certificate-errors");
    }


    public static void printPptx(String imagePath, String outputPath) {

        try (FileInputStream imageInputStream = new FileInputStream(imagePath);
             XMLSlideShow ppt = new XMLSlideShow()) {

            XSLFSlide slide = ppt.createSlide();

            if (ExportWatermarkHelper.shouldApplyPrintWatermark()) {
                ExportWatermarkHelper.applySlideShowMetadata(ppt);
            }


            // Add PNG image to the slide
            XSLFPictureData pictureData = ppt.addPicture(imageInputStream, XSLFPictureData.PictureType.PNG);
            XSLFPictureShape pictureShape = slide.createPicture(pictureData);


            // Save the presentation
            try (FileOutputStream out = new FileOutputStream(outputPath)) {
                ppt.write(out);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String getPrintOptionFilename(JsonObject printOptions) {
        JsonObject printSettings = GsonUtility.optJsonObject(printOptions, "printOptions");
        String templateId = "defaultTemplate";
        if (printSettings != null && !printSettings.entrySet().isEmpty()) {
            if (printSettings.has("templateId")) {
                templateId = printSettings.get("templateId").getAsString();
            }
        }
        if (printOptions.has("templateId")) {
            templateId = printOptions.get("templateId").getAsString();
        }
        return ExportUtils.getTemplatesDirectory() + File.separator + templateId;
    }


    public static String buildTemplateDiv(JsonObject json) {

        StringBuilder sb = new StringBuilder();

        // Read values safely
        String text = json.has("text") ? json.get("text").getAsString() : "";

        String fontSize = json.has("fontSize") ? json.get("fontSize").getAsString() : "12px";
        String fontColor = json.has("fontColor") ? json.get("fontColor").getAsString() : "#000";

        String fontStyle = json.has("fontStyle")
                ? json.get("fontStyle").getAsString()
                : "normal";

        String textAlign = json.has("textAlign")
                ? json.get("textAlign").getAsString()
                : "center";

        // height (in inches)
        String height = "";
        if (json.has("height")) {
            height = "height:" + json.get("height").getAsDouble() + "in;";
        }

        // Build the <div>
        sb.append("<div style='");

        sb.append("font-size:").append(fontSize).append(";");
        sb.append("color:").append(fontColor).append(";");
        sb.append("font-weight:").append(fontStyle).append(";");
        sb.append("text-align:").append(textAlign).append(";");
        sb.append(height);
        sb.append("width:100%;");

        sb.append("'>");

        sb.append(text);

        sb.append("</div>");

        return sb.toString();
    }


}

