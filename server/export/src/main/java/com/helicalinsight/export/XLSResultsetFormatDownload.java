package com.helicalinsight.export;

import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.managed.ResultSetHelper;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.utility.ExportWatermarkHelper;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The `XLSResultsetFormatDownload` class implements the `IDownload` interface to handle the download of ResultSet data in XLSX format.
 * It utilizes the Apache POI library to create an XSSFWorkbook and populate it with data from the provided ResultSet.
 */
@Component
@Scope("prototype")
@SuppressWarnings("unused")
public class XLSResultsetFormatDownload implements IDownload {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(XLSResultsetFormatDownload.class);

    private List<String> hiddenList;
    private List<Integer> hiddenIndex = new ArrayList<>();
    private int lastVisibleColumnIndex = -1;
    /**
     * Generates an XSSFWorkbook with ResultSet data in XLSX format based on the provided ResultSet and conversion options.
     *
     * @param obj               ResultSet containing the data to be exported.
     * @param conversionOptions Additional options for the XLSX conversion (e.g., sheetName).
     * @return XSSFWorkbook containing the formatted data.
     */
    public XSSFWorkbook downloadFormat(Object obj, JsonObject conversionOptions) {
        hiddenList = ResultSetHelper.getHiddenList(conversionOptions);
        hiddenIndex = new ArrayList<>();
        lastVisibleColumnIndex = -1;
        ResultSet rs = (ResultSet) obj;
        String sheetName = GsonUtility.optString(conversionOptions, "sheetName");
        sheetName = (sheetName.length() == 0) ? "sheet 1" : sheetName;

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFSheet sheet = workbook.createSheet(sheetName);
        try {
            createHeaderRow(rs, sheet, cellStyle, workbook, conversionOptions);
            if (ExportWatermarkHelper.shouldApplyWatermark(conversionOptions)) {
                ExportWatermarkHelper.applyWorkbookMetadata(workbook);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return workbook;
    }
    /**
     * Creates the header row in the Excel sheet based on the ResultSet metadata.
     *
     * @param resultSet  ResultSet containing the data.
     * @param sheet      XSSFSheet(SpreadsheetML worksheet) to which the header row is added.
     * @param cellStyle  XSSFCellStyle to be applied to the header cells,the possible formatting information for the contents of the cells on a sheet in a
     * 					 SpreadsheetML document.
     * @throws SQLException If a database access error occurs.
     */
    public void createHeaderRow(ResultSet resultSet, XSSFSheet sheet, XSSFCellStyle cellStyle, XSSFWorkbook workbook,
                                JsonObject conversionOptions) throws SQLException {
        int i = 0;
        XSSFRow rowHead = sheet.createRow(i);
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
            String columnName = metaData.getColumnLabel(columnIndex + 1);
            if (hiddenList.contains(columnName)) {
                hiddenIndex.add(columnIndex + 1);
                continue;
            }
            lastVisibleColumnIndex = columnIndex;
            rowHead.createCell(columnIndex).setCellValue(columnName);
        }
        createDataRows(resultSet, metaData, sheet, cellStyle, workbook, conversionOptions);
    }
    /**
     * Creates data rows in the Excel sheet based on the ResultSet data.
     *
     * @param resultSet 	ResultSet containing the data.
     * @param metaData 		object that can be used to get information about the types
     * 						and properties of the columns 
     * @param sheet         XSSFSheet to which data rows are added(SpreadsheetML worksheet).
     * @throws SQLException If a database access error occurs.
     */
    private void createDataRows(ResultSet resultSet, ResultSetMetaData metaData, XSSFSheet sheet, XSSFCellStyle cellStyle,
                                XSSFWorkbook workbook, JsonObject conversionOptions) throws SQLException {

        int rowNumber = 1;
        long size = metaData.getColumnCount();
        XSSFCreationHelper creationHelper = workbook.getCreationHelper();
        while (resultSet.next()) {
            XSSFRow columnRow = sheet.createRow(rowNumber++);
            for (int i = 0; i < size; i++) {
                if (hiddenIndex.contains(i + 1)) {
                    continue;
                }
                Object object = resultSet.getObject(i + 1);
                if (null==object) columnRow.createCell(i).setCellValue(ApplicationProperties.getInstance().getNullValue());
                else if (object instanceof Date) {
                	XSSFCell createCell = columnRow.createCell(i);
                	createCell.setCellValue((Date) object);
                	short format = creationHelper.createDataFormat().getFormat("yyyy-MM-dd");
                	cellStyle.setDataFormat(format);
                	createCell.setCellStyle(cellStyle);
                }
                else if (object instanceof Boolean) columnRow.createCell(i).setCellValue((Boolean) object);
                else if (object instanceof String) columnRow.createCell(i).setCellValue((String) object);
                else if (object instanceof Double) columnRow.createCell(i).setCellValue((Double) object);
                else if (object instanceof Integer) columnRow.createCell(i).setCellValue((Integer) object);
                else columnRow.createCell(i).setCellValue(object.toString());
            }
        }
        appendWatermarkFooter(sheet, workbook, rowNumber, conversionOptions);
    }

    private void appendWatermarkFooter(XSSFSheet sheet, XSSFWorkbook workbook, int rowNumber, JsonObject conversionOptions) {
        if (!ExportWatermarkHelper.shouldApplyWatermark(conversionOptions) || lastVisibleColumnIndex < 0) {
            return;
        }

        sheet.createRow(rowNumber++);
        XSSFRow footerRow = sheet.createRow(rowNumber);
        XSSFCell footerCell = footerRow.createCell(0);
        footerCell.setCellValue(ExportWatermarkHelper.getWatermarkText());

        XSSFCellStyle footerStyle = workbook.createCellStyle();
        footerStyle.setAlignment(HorizontalAlignment.RIGHT);
        XSSFFont footerFont = workbook.createFont();
        footerFont.setFontName("Serif");
        footerFont.setFontHeightInPoints((short) 8);
        footerStyle.setFont(footerFont);
        footerCell.setCellStyle(footerStyle);

        if (lastVisibleColumnIndex > 0) {
            sheet.addMergedRegion(new CellRangeAddress(rowNumber, rowNumber, 0, lastVisibleColumnIndex));
        }

        String watermarkLink = ExportWatermarkHelper.getWatermarkLink();
        if (watermarkLink != null && !watermarkLink.isEmpty()) {
            XSSFHyperlink hyperlink = workbook.getCreationHelper().createHyperlink(HyperlinkType.URL);
            hyperlink.setAddress(watermarkLink);
            footerCell.setHyperlink(hyperlink);
        }
    }
}
