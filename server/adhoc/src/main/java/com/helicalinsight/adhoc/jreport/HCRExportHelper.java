package com.helicalinsight.adhoc.jreport;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.adhoc.genericsql.AdhocUtils;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.HCRUtils;
import com.helicalinsight.datasource.MetadataUtils;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwException;
import com.helicalinsight.efw.exceptions.HCRException;
import com.helicalinsight.efw.utility.ApplicationUtilities;
import com.helicalinsight.efw.utility.FileUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.PropertiesFileReader;
import com.helicalinsight.efw.utility.ResponseMetadataEnricher;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.PrintPageFormat;
import net.sf.jasperreports.engine.export.*;
import net.sf.jasperreports.engine.export.oasis.JROdsExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRPptxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.*;
import net.sf.jasperreports.web.util.WebHtmlResourceHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * The HCRExportHelper class provides methods to export JasperPrint objects to various formats such as PDF, HTML, and Excel.
 * 
 * <p>This class includes methods to export reports to different formats using the JasperReports library.
 * The exported reports can be saved to an OutputStream or a Writer, depending on the format.</p>
 * 
 * Created by author on 11/29/2019.
 * @author Rajesh
 */
@SuppressWarnings("unused")
public class HCRExportHelper {
    public static final String DEFAULT_IMAGE_DIR = "1234567891234";
    private String tempDirPath = TempDirectoryCleaner.getTempDirectory().getAbsolutePath();
    private ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(HCRExportHelper.class);

    private JasperPrint jasperPrint;
    private String uuid;
    private String targetExportPath;
    private JsonObject newFormData;
    
    private static boolean chromePropertiesLoaded = false;
    private static boolean imagePropertiesLoaded = false;


    public HCRExportHelper(JasperPrint jasperPrint) {
        this.jasperPrint = jasperPrint;
        imagePropertiesLoaded =  imagePropertiesLoaded ||  HCRUtils.loadHcrPropertiesByPrefix("net.sf.jasperreports.image");
    }

    public HCRExportHelper(JasperPrint jasperPrint, String uuid) {
        this(jasperPrint);
        this.uuid = uuid;
        targetExportPath = tempDirPath + File.separator + uuid + JsonUtils.HTML_EXTENSION;
    }

    public HCRExportHelper(JsonObject newFormData, JasperPrint jasperPrint, String uuid) {
         this(jasperPrint, uuid);
         this.newFormData = newFormData;
    }
    private boolean validatePageNum(int page, Integer reportSize) {
        return reportSize != null && reportSize >= 0 && page >= 0 && page <= reportSize;
    }

    private Exporter prepareHTMLExporter() {
        Exporter exporter = new HtmlExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        SimpleHtmlExporterOutput htmlExporterOutput = new SimpleHtmlExporterOutput(prepareTargetExportPath(JsonUtils.HTML_EXTENSION));
        SimpleHtmlReportConfiguration reportConfig = new SimpleHtmlReportConfiguration();
        reportConfig.setEmbedImage(true);
        reportConfig.setAccessibleHtml(true);
        reportConfig.setConvertSvgToImage(true);
        exporter.setConfiguration(reportConfig);
        exporter.setExporterOutput(htmlExporterOutput);
        return exporter;
    }

    private Exporter exportHtmlPageWise(ByteArrayOutputStream out, Integer page, Integer reportSize, Boolean isPreview) {
        Exporter htmlExporter = new HtmlExporter();
        htmlExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        SimpleHtmlExporterOutput htmlExporterOutput = new SimpleHtmlExporterOutput(out);
        htmlExporter.setExporterOutput(htmlExporterOutput);
        SimpleHtmlExporterConfiguration exportConfiguration = new SimpleHtmlExporterConfiguration();
        SimpleHtmlReportConfiguration reportConfiguration = new SimpleHtmlReportConfiguration();
        if (isPreview)
            reportConfiguration.setPageIndex(page);

        exportConfiguration.setHtmlHeader("");
        exportConfiguration.setHtmlFooter("");

        reportConfiguration.setWhitePageBackground(false);

        reportConfiguration.setUseBackgroundImageToAlign(false);

        reportConfiguration.setRemoveEmptySpaceBetweenRows(true);

        reportConfiguration.setWrapBreakWord(true);
        reportConfiguration.setEmbedImage(true);
        reportConfiguration.setAccessibleHtml(true);
        reportConfiguration.setConvertSvgToImage(true);

        htmlExporter.setConfiguration(reportConfiguration);
        htmlExporter.setConfiguration(exportConfiguration);
        
        return htmlExporter;
    }
    /**
     * Exports a JasperPrint object to a byte array in the specified format.
     * 
     * <p>This method supports exporting the report to PDF, HTML,CSV or Excel format, and returns the result
     * as a byte array.</p>
     * 
     * @param format 	 format to export to (e.g., "PDF", "HTML", "EXCEL")
     * @param reportSize report size
     * @param page       page 
     * @param isPreview  boolean value true or false
     * @return a byte array containing the exported report
     * @throws Exception if an error occurs during exporting
     */
    public byte[] exportInBytes(String format, int reportSize, int page, Boolean isPreview) throws JRException {
        if (!validatePageNum(page, reportSize)) {
            throw new EfwException("Invalid page number : provided page no:" + page + " is exceeding the total report size:" + reportSize + " NOTE page index starts from 0");
        }
        final Exporter exporter;
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        boolean html = false;

        switch (format) {
            case "HTML":
                exporter = exportHtmlPageWise(out, page, reportSize, isPreview);
                html = true;
                break;

            case "CSV":
                exporter = new JRCsvExporter();
                break;

            case "XML":
                exporter = new JRXmlExporter();
                break;

            case "XLSX":
                exporter = new JRXlsxExporter();
                break;

            case "PDF":
                exporter = new JRPdfExporter();
                break;

            default:
                throw new JRException("Unknown report format: " + format);
        }
        if (!html) {
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
        }
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        try {
            exporter.exportReport();
        } catch (JRRuntimeException e) {
            throw new EfwException("Unable to export the report cause: " + e.getMessage());
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return out.toByteArray();
    }
    /**
     * Exports the report specified by the jrxmlData to a file in the given format.
     *
     * @param format 		the format to export to (e.g., "PDF", "HTML", "EXCEL")
     * @param page 			the page number to export (if applicable, otherwise use -1 for all pages)
     * @param jrxmlData 	the JSON object containing the JRXML data
     * @return true if the export is successful, false otherwise
     * @throws Exception if an error occurs during exporting
     */
    public boolean exportIntoFiles(String format, int page, JsonObject jrxmlData) {
        format = format.toUpperCase();
        logger.info("Export-Format :" + format);
        
        chromePropertiesLoaded = chromePropertiesLoaded ||  HCRUtils.loadHcrPropertiesByPrefix("net.sf.jasperreports.chrome");
        
        final Exporter exporter;
        boolean isImage = false;
        switch (format) {
            case "HTML":
                exporter = prepareHTMLExporter();
                applyFileMetadata(exporter);
                break;
            case "CSV":
                exporter = prepareCSVExporter();
                applyFileMetadata(exporter);
                break;

            case "XML":
                exporter = prepareXMLExporter();
                applyFileMetadata(exporter);
                break;

            case "XLSX":
                exporter = prepareXLSXExporter();
                applyFileMetadata(exporter);
                break;

            case "PDF":
                exporter = preparePDFExporter();
                applyFileMetadata(exporter);
                break;

            case "RTF":
                exporter = prepareRTFExporter();
                applyFileMetadata(exporter);
                break;

            case "DOCX":
                exporter = prepareDocsExporter();
                applyFileMetadata(exporter);
                break;

            case "ODT":
                exporter = prepareOdtExporter();
                applyFileMetadata(exporter);
                break;

            case "ODS":
                exporter = prepareOdsExporter();
                applyFileMetadata(exporter);
                break;

            case "PPTX":
                exporter = preparePowerpointExporter();
                applyFileMetadata(exporter);
                break;

            case "TXT":
                exporter = prepareTextExporter();
                applyFileMetadata(exporter);
                break;

            case "XLS":
                exporter = prepareXLSExporter();
                applyFileMetadata(exporter);
                break;

            case "CSVMETADATA":
                exporter = prepareCSVMetadataExporter();
                applyFileMetadata(exporter);
                break;

            case "JSONMETADATA":
                exporter = prepareJSONMetadataExporter();
                applyFileMetadata(exporter);
                break;
            case "PNG":
                exporter = preparePNGAndJPEGExporter(page, JsonUtils.PNG_EXTENSION);
                applyFileMetadata(exporter);
                isImage = true;
                break;
            case "JPEG":
                exporter = preparePNGAndJPEGExporter(page, JsonUtils.JPEG_EXTENSION);
                applyFileMetadata(exporter);
                isImage = true;
                break;
            case "JPG":
                exporter = preparePNGAndJPEGExporter(page, JsonUtils.JPG_EXTENSION);
                applyFileMetadata(exporter);
                isImage = true;
                break;

            default:
                throw new HCRException("Unknown report format: " + format);
        }
        jrxmlData.addProperty("uuid", uuid);
        try {
            if (exporter != null && !isImage)
                exporter.exportReport();
        } catch (JRRuntimeException | JRException e) {
            throw new HCRException("Unable to export the report cause: " + e.getMessage());
        }
        return checkFile();
    }

    private Exporter preparePNGAndJPEGExporter(int pageIndex, String extension) {
        BufferedImage pageImage = null;
        targetExportPath = prepareTargetExportPath(extension);
        extension = extension.replace(".", "");
        try (FileOutputStream outputStream = new FileOutputStream(new File(targetExportPath))) {
            float zoom = 3f;
            PrintPageFormat pageFormat = jasperPrint.getPageFormat(pageIndex);

            pageImage = new BufferedImage(
                    (int) (pageFormat.getPageWidth() * zoom) + 1,
                    (int) (pageFormat.getPageHeight() * zoom) + 1,
                    BufferedImage.TYPE_INT_RGB
            );
            Exporter exporter = new JRGraphics2DExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            SimpleGraphics2DExporterOutput output = new SimpleGraphics2DExporterOutput();
            output.setGraphics2D((Graphics2D) pageImage.getGraphics());
            exporter.setExporterOutput(output);
            SimpleGraphics2DReportConfiguration configuration = new SimpleGraphics2DReportConfiguration();
            configuration.setPageIndex(pageIndex);
            configuration.setZoomRatio(zoom);
            exporter.setConfiguration(configuration);
            exporter.exportReport();
            ImageIO.write(pageImage, extension, outputStream);
            return exporter;

        } catch (JRException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private boolean checkFile() {
        return FileUtils.isFilePresent(new File(targetExportPath));
    }

    private Exporter prepareXMLExporter() {
        Exporter exporter = new JRXmlExporter();
        targetExportPath = prepareTargetExportPath(JsonUtils.XML_EXTENSION);
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleXmlExporterOutput(targetExportPath));
        return exporter;
    }

    private Exporter prepareRTFExporter() {
        Exporter exporter = new JRRtfExporter();
        targetExportPath = prepareTargetExportPath(JsonUtils.RTF_EXTENSION);
        prepareWriterExportIO(exporter);
        return exporter;
    }

    private Exporter prepareDocsExporter() {
        Exporter exporter = new JRDocxExporter();
        targetExportPath = prepareTargetExportPath(JsonUtils.DOCX_EXTENSION);
        prepareGenericExportIO(exporter);
        return exporter;
    }

    private Exporter prepareOdtExporter() {
        Exporter exporter = new JROdtExporter();
        targetExportPath = prepareTargetExportPath(JsonUtils.ODT_EXTENSION);
        prepareGenericExportIO(exporter);
        return exporter;
    }

    private Exporter prepareOdsExporter() {
        Exporter exporter = new JROdsExporter();
        targetExportPath = prepareTargetExportPath(JsonUtils.ODS_EXTENSION);
        prepareGenericExportIO(exporter);
        return exporter;
    }

    private Exporter preparePowerpointExporter() {
        Exporter exporter = new JRPptxExporter();
        targetExportPath = prepareTargetExportPath(JsonUtils.PPTX_EXTENSION);
        prepareGenericExportIO(exporter);
        return exporter;
    }

    private Exporter prepareTextExporter() {
        Exporter exporter = new JRTextExporter();
        targetExportPath = prepareTargetExportPath(JsonUtils.TXT_EXTENSION);
        SimpleTextReportConfiguration reportConfiguration = new SimpleTextReportConfiguration();
        JsonObject designerProperties = newFormData.getAsJsonObject("designerProperties");
        if(designerProperties!=null) {
            int pageWidth = newFormData.getAsJsonObject("designerProperties").get("pageWidth").getAsInt();
            int pageHeight = newFormData.getAsJsonObject("designerProperties").get("pageHeight").getAsInt();
        }
            reportConfiguration.setPageWidthInChars(80);
            reportConfiguration.setPageHeightInChars(40);
            reportConfiguration.setCharWidth(10f);
            reportConfiguration.setCharHeight(12f);
            exporter.setConfiguration(reportConfiguration);

        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleWriterExporterOutput(targetExportPath));
        return exporter;
    }

    private void prepareWriterExportIO(Exporter exporter) {
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleWriterExporterOutput(targetExportPath));
    }

    private Exporter prepareXLSExporter() {
        Exporter exporter = new JRXlsExporter();
        targetExportPath = prepareTargetExportPath(JsonUtils.XLS_EXTENSION);
        prepareGenericExportIO(exporter);
        return exporter;
    }

    private Exporter prepareCSVMetadataExporter() {
        Exporter exporter = new JRCsvMetadataExporter();
        targetExportPath = prepareTargetExportPath(JsonUtils.CSV_EXTENSION);
        prepareWriterExportIO(exporter);
        return exporter;
    }

    private Exporter prepareJSONMetadataExporter() {
        Exporter exporter = new JsonExporter();
        targetExportPath = prepareTargetExportPath(JsonUtils.JSON_EXTENSION);
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleJsonExporterOutput(targetExportPath));
        return exporter;
    }

    /*
    private Exporter prepareXLSXExporter() {
        Exporter exporter = new JRXlsxExporter();
        SimpleXlsxReportConfiguration config = new SimpleXlsxReportConfiguration();
        JSONObject designerProperties = formData.getJSONObject("designerProperties");

        Boolean isRemoveEmptySpaceBetweenRows = Boolean.valueOf(true);
        config.setRemoveEmptySpaceBetweenRows(isRemoveEmptySpaceBetweenRows);
        Boolean isRemoveEmptySpaceBetweenColumns = Boolean.valueOf(true);
        config.setRemoveEmptySpaceBetweenColumns(isRemoveEmptySpaceBetweenColumns);
        int printPageTopMargin = designerProperties.optInt("pageTopMargin",0);
        config.setPrintPageTopMargin(printPageTopMargin);
        int printPageRightMargin = designerProperties.optInt("rightMargin",0);;
        config.setPrintPageRightMargin(printPageRightMargin);
        int printPageLeftMargin = designerProperties.optInt("leftMargin",0);;
        config.setPrintPageLeftMargin(printPageLeftMargin);
        int printPageBottomMargin = designerProperties.optInt("bottomMargin",0);;
        config.setPrintPageBottomMargin(printPageBottomMargin);
        int printFooterMargin = designerProperties.optInt("footerMargin",0);;
        config.setPrintFooterMargin(printFooterMargin);
        int printHeaderMargin = designerProperties.optInt("headerMargin",0);;
        config.setPrintHeaderMargin(printHeaderMargin);
        Boolean onePagePerSheet = designerProperties.optBoolean("onePagePerSheet",false);;
        config.setOnePagePerSheet(onePagePerSheet);

        exporter.setConfiguration(config);

        logger.info("***************************************************");
        targetExportPath = prepareTargetExportPath(JsonUtils.XLSX_EXTENSION);
        prepareGenericExportIO(exporter);
        return exporter;
    }*/
    
    private Exporter prepareXLSXExporter() {
        Exporter exporter = new JRXlsxExporter();
        SimpleXlsxReportConfiguration config = new SimpleXlsxReportConfiguration();
        JsonObject designerProperties = newFormData.getAsJsonObject("designerProperties");
if(designerProperties!=null) {
    Boolean isRemoveEmptySpaceBetweenRows = Boolean.valueOf(true);
    config.setRemoveEmptySpaceBetweenRows(isRemoveEmptySpaceBetweenRows);
    Boolean isRemoveEmptySpaceBetweenColumns = Boolean.valueOf(true);
    config.setRemoveEmptySpaceBetweenColumns(isRemoveEmptySpaceBetweenColumns);
    int printPageTopMargin = GsonUtility.optIntValue(designerProperties, "pageTopMargin", 0);
    config.setPrintPageTopMargin(printPageTopMargin);
    int printPageRightMargin = GsonUtility.optIntValue(designerProperties, "rightMargin", 0);
    config.setPrintPageRightMargin(printPageRightMargin);
    int printPageLeftMargin = GsonUtility.optIntValue(designerProperties, "leftMargin", 0);
    config.setPrintPageLeftMargin(printPageLeftMargin);
    int printPageBottomMargin = GsonUtility.optIntValue(designerProperties, "bottomMargin", 0);
    config.setPrintPageBottomMargin(printPageBottomMargin);
    int printFooterMargin = GsonUtility.optIntValue(designerProperties, "footerMargin", 0);
    config.setPrintFooterMargin(printFooterMargin);
    int printHeaderMargin = GsonUtility.optIntValue(designerProperties, "headerMargin", 0);
    config.setPrintHeaderMargin(printHeaderMargin);
    Boolean onePagePerSheet = GsonUtility.optBooleanValue(designerProperties, "onePagePerSheet", false);
    config.setOnePagePerSheet(onePagePerSheet);

    exporter.setConfiguration(config);
}
        logger.info("***************************************************");
        targetExportPath = prepareTargetExportPath(JsonUtils.XLSX_EXTENSION);
        prepareGenericExportIO(exporter);
        return exporter;
    }

    private Exporter prepareCSVExporter() {
        Exporter exporter = new JRCsvExporter();
        Exporter exporter2 = new JRCsvMetadataExporter();

        targetExportPath = prepareTargetExportPath(JsonUtils.CSV_EXTENSION);
        prepareWriterExportIO(exporter);
        SimpleCsvExporterConfiguration configuration = new SimpleCsvExporterConfiguration();
        SimpleCsvMetadataExporterConfiguration configuration2 = new SimpleCsvMetadataExporterConfiguration();
        SimpleCsvMetadataReportConfiguration configuration3 = new SimpleCsvMetadataReportConfiguration();
        exporter.setConfiguration(configuration2);
        return exporter;
    }

    private void prepareGenericExportIO(Exporter exporter) {
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(targetExportPath));
    }

    private String prepareTargetExportPath(String extension) {
        boolean isMail = GsonUtility.optBooleanValue(newFormData,"isMail", false);
        String emailExportName = GsonUtility.optStringValue(newFormData,"emailExportName", null);
        if (isMail && emailExportName != null) {
            return targetExportPath = tempDirPath + File.separator + emailExportName + extension;
        }
        return targetExportPath = tempDirPath + File.separator + uuid + extension;
    }

    private Exporter preparePDFExporter() {
        JRPdfExporter exporter = new JRPdfExporter();
        targetExportPath = prepareTargetExportPath(JsonUtils.PDF_EXTENSION);
        //targetExportPath = tempDirPath + File.separator + uuid + JsonUtils.PDF_EXTENSION;
        prepareGenericExportIO(exporter);
        SimplePdfExporterConfiguration exportConfiguration = new SimplePdfExporterConfiguration();
        SimplePdfReportConfiguration reportConfiguration = new SimplePdfReportConfiguration();
        exporter.setConfiguration(exportConfiguration);
        exporter.setConfiguration(reportConfiguration);
        return exporter;
    }

    /**
     * Handles exporting a report into multiple formats as specified by the provided format array.
     *
     * This method takes a JSON array of formats, a page number, and a JSON object containing the JRXML data.
     * It exports the report into each specified format and updates the JSON data with the paths of the exported files.</p>
     *
     * @param format 			 JSON array of formats to export to (e.g., ["PDF", "HTML", "EXCEL"])
     * @param page 				 page number to export (if applicable, otherwise use -1 for all pages)
     * @param jrxmlDATA 		 JSON object containing the JRXML data
     * @return a message indicating the export status
     * @throws Exception if an error occurs during exporting
     */
    public String handleMultiExport(JsonArray format, int page, JsonObject jrxmlDATA) {
        String response = "Exported into multiple formats successfully..";
        JsonArray exportedFiles = new JsonArray();
        for (int index = 0; index < format.size(); index++) {
            String eachFormat = format.get(index).getAsString();
            if (exportIntoFiles(eachFormat, page, jrxmlDATA))
                exportedFiles.add(targetExportPath);
        }
        jrxmlDATA.add("exportedFiles", exportedFiles);
        return response;
    }
    
    
    private final  void applyFileMetadata(
            Exporter<?, ?, ?, ?> exporter ) {

    	JsonObject meta = ResponseMetadataEnricher.getMetaObject();
    	

		String productName = GsonUtility.optString(meta, "productName");
		String version = GsonUtility.optString(meta, "version");
		String link = GsonUtility.optString(meta, "link");
		String poweredBy = "Powered By %s \u00A9 %s".formatted(productName, version);


        if (exporter instanceof JRPdfExporter pdf) {
           
        	SimplePdfExporterConfiguration config = new SimplePdfExporterConfiguration();

            config.setMetadataTitle(productName);
            config.setMetadataAuthor(productName);
            config.setMetadataKeywords("%s,%s,%s".formatted(productName,version, poweredBy));
            config.setMetadataCreator(productName);
            config.setMetadataProducer(productName);
            pdf.setConfiguration(config);
        }

        else if (exporter instanceof JRXlsxExporter xlsx) {
            SimpleXlsxExporterConfiguration config =new SimpleXlsxExporterConfiguration();
            config.setMetadataTitle(productName);
            config.setMetadataAuthor(productName);
            config.setMetadataKeywords("%s,%s,%s".formatted(productName,version, poweredBy));
            xlsx.setConfiguration(config);
        }

        else if (exporter instanceof JRDocxExporter docx) {
            SimpleDocxExporterConfiguration config = new SimpleDocxExporterConfiguration();
            config.setMetadataTitle(productName);
            config.setMetadataAuthor(productName);
            config.setMetadataKeywords("%s,%s,%s".formatted(productName,version, poweredBy));
            docx.setConfiguration(config);
        }
    }
}

