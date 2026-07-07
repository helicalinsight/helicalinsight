package com.helicalinsight.adhoc.jreport;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.EnhancedQueryExecutor;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.HCRUtils;
import com.helicalinsight.datasource.ResultSetDataSourceFactory;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.jasperintegration.BandFactory;
import com.helicalinsight.efw.jasperintegration.bandcontents.HCRTextField;
import com.helicalinsight.efw.utility.ExportWatermarkHelper;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.PropertiesFileReader;
import com.helicalinsight.efw.utility.ResponseMetadataEnricher;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;

import net.sf.jasperreports.engine.JRElement;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JRHyperlinkParameter;
import net.sf.jasperreports.engine.JRLineBox;
import net.sf.jasperreports.engine.JRPen;
import net.sf.jasperreports.engine.JRSection;
import net.sf.jasperreports.engine.base.JRBoxPen;
import net.sf.jasperreports.engine.design.*;
import net.sf.jasperreports.engine.type.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * JDesignHelper acts as a helper class that translates the JSON design specifications 
 * into a structured JasperDesign object, which can then be used to generate reports.
 * Created by author on 10/1/2019.
 * @author Rajesh
 */
public class JDesignHelper {
	private static final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
	private final String CONNECTION_CLASS = "__CONNECTION__";
	private final String JDBC_CONNECTION = java.sql.Connection.class.getName();
	private final String JRDATASOURCE = net.sf.jasperreports.engine.JRDataSource.class.getName();
	private final String GENERATOR_TYPE = JsonUtils.getHCRDefaultGeneratorType();
	private JRDesignField field;
	private JasperDesign jasperDesign;
	private Boolean isGenerateXml;
	private JsonObject formData;

	/**
	 * Creates a new JasperDesign object based on the designer properties provided in JSON format.
	 * This method creates a JasperDesign object based on designer properties in JSON format.
	 * It parses various sections (page, groups, title, etc.) and calls helper methods to configure the report design.
	 * @param formData 				A JsonObject containing the entire form data.
	 * @return A new JasperDesign object representing the designed report.
	 * @throws JRException If any error occurs while creating the report design.
	 */
	public JDesignHelper(JsonObject formData) {
		this.formData = formData;
		jasperDesign = new JasperDesign();
		this.isGenerateXml= GsonUtility.optBooleanValue(formData,"generateXML", false);
	}

	public JasperDesign createDesigner() throws JRException {
		JsonObject designerPropertiesJson = formData.getAsJsonObject("designerProperties");

		setPage(designerPropertiesJson);
		// GROUP section
		if (designerPropertiesJson.has("groups")) {
			JsonArray groupsArray = designerPropertiesJson.getAsJsonArray("groups");
			if (!groupsArray.isEmpty())
				addGroups(groupsArray);
		}

		if (designerPropertiesJson.has("designerStyle")) {
			JsonArray styleArray = designerPropertiesJson.getAsJsonArray("designerStyle");
			addStyle(styleArray);
		}
		// TITLE section
		if (designerPropertiesJson.has("title")) {
			JsonObject titleJson = designerPropertiesJson.getAsJsonObject("title");
			if (!titleJson.entrySet().isEmpty())
				addTitle(titleJson);
		}

		// QUERY section

		addQuery(jasperDesign, designerPropertiesJson);

		if (designerPropertiesJson.has("dataSets")) {
			addDataSet(jasperDesign, designerPropertiesJson);
		}

		// PAGEHEADER section
		if (designerPropertiesJson.has("pageHeader")) {
			JsonObject pageHeader = designerPropertiesJson.getAsJsonObject("pageHeader");
			if (!pageHeader.entrySet().isEmpty()) {
				JRDesignBand band = addPageHeader(pageHeader);
				JsonObject breakObect = GsonUtility.optJsonObject(designerPropertiesJson,"pageHeaderBandBreak");
				if(breakObect==null){
					JsonArray newBreakObect=GsonUtility.optJsonArray(pageHeader,"break");
					if(newBreakObect!=null){
						for (int index = 0; index < newBreakObect.size(); index++) {
                             addBreak(newBreakObect.get(index).getAsJsonObject(),band);
						}
					}
				}
				if (breakObect != null && !breakObect.entrySet().isEmpty()) {
					addBreak(breakObect, band);
				}
			}
		}

		// COLUMNHEADER section
		if (designerPropertiesJson.has("columnHeader")) {
			JsonObject columnHeader = designerPropertiesJson.getAsJsonObject("columnHeader");
			if (!columnHeader.entrySet().isEmpty())
				addColumnHeader(columnHeader);
		}
		if (designerPropertiesJson.has("details")) {
			JsonArray details = designerPropertiesJson.getAsJsonArray("details");
			for (int index = 0; index < details.size(); index++) {
				JsonObject eachDetailJson = details.get(index).getAsJsonObject();
				JRDesignBand band = addDetail(eachDetailJson, index);
				JsonObject breakObect = GsonUtility.optJsonObject(designerPropertiesJson,"detailBandBreak");
				if(breakObect==null){
					JsonArray newBreakObect=GsonUtility.optJsonArray(eachDetailJson,"break");
					if(newBreakObect!=null){
						for (int index2 = 0; index2 < newBreakObect.size(); index2++) {
							addBreak(newBreakObect.get(index2).getAsJsonObject(),band);
						}
					}
				}
                if(breakObect!=null && !breakObect.entrySet().isEmpty()){
                   addBreak( breakObect,band);
                }

			}
			 JRSection detailSection = jasperDesign.getDetailSection();
	         detailSection.getBands();
		}

		if (designerPropertiesJson.has("variables") && !designerPropertiesJson.getAsJsonArray("variables").isEmpty()) {
			JsonArray variablesArray = designerPropertiesJson.getAsJsonArray("variables");
			addVariables(variablesArray);
		}

		if (designerPropertiesJson.has("columnFooter")) {
			JsonObject columnFooter = designerPropertiesJson.getAsJsonObject("columnFooter");
			if (!columnFooter.entrySet().isEmpty())
				addColumnFooter(columnFooter);
		}

		// PAGEFOOTER section
		JRDesignBand band = new JRDesignBand();
		if (designerPropertiesJson.has("pageFooter")) {
			JsonObject pageFooter = designerPropertiesJson.getAsJsonObject("pageFooter");
			if (!pageFooter.entrySet().isEmpty()) {
				band = addPageFooter(pageFooter);
            	JsonObject breakObect = GsonUtility.optJsonObject(designerPropertiesJson,"pageFooterBandBreak");
				if(breakObect==null){
					JsonArray newBreakObect=GsonUtility.optJsonArray(pageFooter,"break");
					if(newBreakObect!=null){
						for (int index = 0; index < newBreakObect.size(); index++) {
							addBreak(newBreakObect.get(index).getAsJsonObject(),band);
						}
					}
				}
                if (breakObect != null && !breakObect.entrySet().isEmpty()) {
                	addBreak(breakObect,band);
                }
			}
			
		}
		addWaterMark(band);
		
		// LASTPAGEFOOTER section
		if (designerPropertiesJson.has("lastPageFooter")) {
			JsonObject lastPageFooter = designerPropertiesJson.getAsJsonObject("lastPageFooter");
			if (!lastPageFooter.entrySet().isEmpty())
				addLastPageFooter(lastPageFooter);
		}

		// SUMMARY section
		if (designerPropertiesJson.has("summary")) {
			JsonObject summary = designerPropertiesJson.getAsJsonObject("summary");
			if (!summary.entrySet().isEmpty()) {
				JRDesignBand jrDesignBand = addSummary(summary);
            	JsonObject breakObect = GsonUtility.optJsonObject(designerPropertiesJson,"summaryBandBreak");
				if(breakObect==null){
					JsonArray newBreakObect=GsonUtility.optJsonArray(summary,"break");
					if(newBreakObect!=null){
						for (int index = 0; index < newBreakObect.size(); index++) {
							addBreak(newBreakObect.get(index).getAsJsonObject(),jrDesignBand);
						}
					}
				}
            	if (breakObect != null && !breakObect.entrySet().isEmpty()) {
            		addBreak(breakObect,jrDesignBand);
            	}
        	}
		}

		// NODATA section
		if (designerPropertiesJson.has("noData") && !designerPropertiesJson.getAsJsonObject("noData").entrySet().isEmpty()) {
			JsonObject noData = designerPropertiesJson.getAsJsonObject("noData");
			if (!noData.entrySet().isEmpty())
				addNoData(noData);
		}
		/*
		 * 
		 * //BACKGROUND section JSONObject background =
		 * designerPropertiesJson.getJSONObject("background");
		 * addBackground(jasperDesign, background);
		 * 
		 * jasperDesign.addStyle(retrieveNormalStyle());
		 * jasperDesign.addStyle(retrieveBoldStyle());
		 * jasperDesign.addStyle(retrieveItalicStyle());
		 */

		// Parameters
		if (designerPropertiesJson.has("parameters")) {
			JsonArray parametersJson = designerPropertiesJson.getAsJsonArray("parameters");
			addParameters(parametersJson);
		}
		
		return jasperDesign;

	}
	
	private void  addBreak(JsonObject breakObect, JRDesignBand band) {

        JRDesignBreak designBreak = new JRDesignBreak();
        designBreak.setX(GsonUtility.optIntValue(breakObect,"X", 0));
        designBreak.setY(GsonUtility.optIntValue(breakObect,"Y", 10));
        designBreak.setWidth(GsonUtility.optIntValue(breakObect,"breakWidth", 1));
        designBreak.setHeight(GsonUtility.optIntValue(breakObect,"breakHeight", 1));
		String express=GsonUtility.optString(breakObect,"printWhenExpression");
		if(express!=null && express!="") {
			JRDesignExpression designExpression = new JRDesignExpression();
			designExpression.setText(express);
			designBreak.setPrintWhenExpression(designExpression);
		}
		band.addElement(designBreak);



    }
	private void addStyle(JsonArray styleArray) throws JRException {
		for (int index = 0; index < styleArray.size(); index++) {
			JsonObject eachStyleJson = styleArray.get(index).getAsJsonObject();
			JRDesignStyle designStyle = prepareDesignStyle(eachStyleJson);
			jasperDesign.addStyle(designStyle);
		}
	}

	private JRDesignStyle prepareDesignStyle(JsonObject eachStyleJson) {
		JRDesignStyle designStyle = new JRDesignStyle();
		if (eachStyleJson.has("lineStyle")) {
			JsonObject lineStyle = eachStyleJson.getAsJsonObject("lineStyle");
			JRPen linePen = designStyle.getLinePen();
			if (lineStyle.has("penLineWidth"))
				linePen.setLineWidth(Float.valueOf(lineStyle.get("penLineWidth").getAsInt()));
			if (lineStyle.has("lineStyle"))
				linePen.setLineStyle(LineStyleEnum.getByName(lineStyle.get("lineStyle").getAsString()));
		}
		designStyle.setItalic(GsonUtility.optBooleanValue(eachStyleJson,"isItalic", false) ? Boolean.TRUE : Boolean.FALSE);
		designStyle.setUnderline(GsonUtility.optBooleanValue(eachStyleJson,"isUnderline", false) ? Boolean.TRUE : Boolean.FALSE);
		designStyle.setStrikeThrough(GsonUtility.optBooleanValue(eachStyleJson,"strikeThrough", false) ? Boolean.TRUE : Boolean.FALSE);
		designStyle.setBold(GsonUtility.optBooleanValue(eachStyleJson,"isBold", false) ? Boolean.TRUE : Boolean.FALSE);
		if (eachStyleJson.has("foreColor"))
			designStyle.setForecolor(Color.decode(eachStyleJson.get("foreColor").getAsString()));
		if (eachStyleJson.has("fontSize"))
			designStyle.setFontSize((float) eachStyleJson.get("fontSize").getAsInt());
		if (eachStyleJson.has("fontName")) {
			String fontName = eachStyleJson.get("fontName").getAsString();
			designStyle.setFontName(fontName);
			if (HCRUtils.fontExists(fontName)) {
				designStyle.setPdfFontName(fontName);
			}
		}
			
		designStyle.setName(eachStyleJson.get("name").getAsString());
		if (eachStyleJson.has("mode"))
			designStyle.setMode(ModeEnum.getByName(eachStyleJson.get("mode").getAsString()));
		if (eachStyleJson.has("backColor"))
			designStyle.setBackcolor(Color.decode(eachStyleJson.get("backColor").getAsString()));
		if (eachStyleJson.has("rotationType"))
			designStyle.setRotation(RotationEnum.getByName(eachStyleJson.get("rotationType").getAsString()));
		if (eachStyleJson.has("horizontalTextAlign"))
			designStyle.setHorizontalTextAlign(
					HorizontalTextAlignEnum.getByName(eachStyleJson.get("horizontalTextAlign").getAsString()));
		if (eachStyleJson.has("pattern"))
			designStyle.setPattern(eachStyleJson.get("pattern").getAsString());
		if (eachStyleJson.has("parentStyleNameReference"))
			designStyle.setParentStyleNameReference(eachStyleJson.get("parentStyleNameReference").getAsString());
		designStyle.setDefault(GsonUtility.optBooleanValue(eachStyleJson,"isDefault", false));
		
		JsonObject borderJson =  GsonUtility.optJsonObject(eachStyleJson, HCRUtils.PROPERTY_BORDER);
		HCRUtils.movePaddingFromBorder(borderJson, eachStyleJson);
		HCRUtils.prepareBorder(designStyle,borderJson);
		HCRUtils.preparePadding(designStyle, GsonUtility.optJsonObject(eachStyleJson, HCRUtils.PROPERTY_PADDING));
		
		if (eachStyleJson.has("conditionalStyle")) {
			JsonObject conditionalStyleJson = eachStyleJson.getAsJsonObject("conditionalStyle");
			JRDesignConditionalStyle conditionalStyle = new JRDesignConditionalStyle();
			if (conditionalStyleJson.has("pattern"))
				conditionalStyle.setPattern(conditionalStyleJson.get("pattern").getAsString());
			if (conditionalStyleJson.has("horizontalTextAlign"))
				conditionalStyle.setHorizontalTextAlign(
						HorizontalTextAlignEnum.getByName(conditionalStyleJson.get("horizontalTextAlign").getAsString()));
			if (conditionalStyleJson.has("backColor"))
				conditionalStyle.setBackcolor(Color.decode(conditionalStyleJson.get("backColor").getAsString()));
			if (conditionalStyleJson.has("rotationType"))
				conditionalStyle.setRotation(RotationEnum.getByName(conditionalStyleJson.get("rotationType").getAsString()));
			if (conditionalStyleJson.has("expression")) {
				JRDesignExpression conditionalExpression = new JRDesignExpression();
				conditionalExpression.setText(conditionalStyleJson.get("expression").getAsString());
				conditionalStyle.setConditionExpression(conditionalExpression);
			}
			designStyle.addConditionalStyle(conditionalStyle);
		}

		return designStyle;
	}

	private void setPage(JsonObject designerPropertiesJson) {
		jasperDesign.setName(GsonUtility.optStringValue(designerPropertiesJson,"reportName", "Dynamic Designer report"));
		jasperDesign.setPageWidth(GsonUtility.optIntValue(designerPropertiesJson,"pageWidth", 842));
		jasperDesign.setPageHeight(GsonUtility.optIntValue(designerPropertiesJson,"pageHeight", 595));
		jasperDesign.setProperty("net.sf.jasperreports.awt.ignore.missing.font", "true");
		PropertiesFileReader propertiesFileReader = new PropertiesFileReader();
		Map<String, String> propertiesMap = propertiesFileReader.read("Admin", "hcrConfigProperties.properties","net.sf.jasperreports.export");
		Map<String, String> propertiesMapReplace = propertiesFileReader.read("Admin", "hcrConfigProperties.properties","replace.strategy");
		Set<String> keys=propertiesMap.keySet();
		keys.forEach(key->{
			jasperDesign.setProperty(key,propertiesMap.get(key));
		});

        if(GsonUtility.optBooleanValue(designerPropertiesJson,"applyXlsSettings",false)) {
            jasperDesign.setProperty("net.sf.jasperreports.export.xls.exclude.origin.keep.first.band.1", "pageHeader");
            jasperDesign.setProperty("net.sf.jasperreports.export.xls.exclude.origin.band.1", "title");
            jasperDesign.setProperty("net.sf.jasperreports.export.xls.exclude.origin.band.2", "pageFooter");
            jasperDesign.setProperty("net.sf.jasperreports.export.xls.exclude.origin.keep.first.band.2", "columnHeader");
            jasperDesign.setProperty("net.sf.jasperreports.export.xls.remove.empty.space.between.rows", "true");
            jasperDesign.setProperty("net.sf.jasperreports.export.xls.remove.empty.space.between.columns", "true");
        }
        if(GsonUtility.optBooleanValue(designerPropertiesJson,"applyXlsxSettings",false)) {

            jasperDesign.setProperty("net.sf.jasperreports.export.xlsx.exclude.origin.keep.first.band.1", "pageHeader");
            jasperDesign.setProperty("net.sf.jasperreports.export.xlsx.exclude.origin.band.1", "title");
            jasperDesign.setProperty("net.sf.jasperreports.export.xlsx.exclude.origin.band.2", "pageFooter");
            jasperDesign.setProperty("net.sf.jasperreports.export.xlsx.exclude.origin.keep.first.band.2", "columnHeader");
            jasperDesign.setProperty("net.sf.jasperreports.export.xlsx.remove.empty.space.between.rows", "true");
            jasperDesign.setProperty("net.sf.jasperreports.export.xlsx.remove.empty.space.between.columns", "true");

        }
        
     
        
        if(GsonUtility.optBooleanValue(designerPropertiesJson,"applyCustomSettings",false)) {
            JsonObject customSettings = designerPropertiesJson.getAsJsonObject("customSettings");
            HCRUtils.applyCustomProperties(jasperDesign, customSettings);
		}


		
		if (designerPropertiesJson.has("reportScriptletClassName"))
			jasperDesign.setScriptletClass(designerPropertiesJson.get("reportScriptletClassName").getAsString());
		jasperDesign
				.setOrientation(OrientationEnum.getByName(GsonUtility.optStringValue(designerPropertiesJson,"orientation", "Portrait")));
		if (designerPropertiesJson.has("printOrder"))
			jasperDesign.setPrintOrder(PrintOrderEnum.getByName(designerPropertiesJson.get("printOrder").getAsString()));
		jasperDesign.setColumnCount(GsonUtility.optIntValue(designerPropertiesJson,"columnCount", 1));
		jasperDesign.setSummaryWithPageHeaderAndFooter(
				GsonUtility.optBooleanValue(designerPropertiesJson,"summaryWithPageHeaderAndFooter", false));
		Integer colWidth=80;
		if(jasperDesign.getPageWidth()<=74){
			colWidth=30;
		}else	if(jasperDesign.getPageWidth()<=150){
			colWidth=60;
		} 
		jasperDesign.setColumnWidth(GsonUtility.optIntValue(designerPropertiesJson,"columnWidth", colWidth));
		jasperDesign.setColumnSpacing(GsonUtility.optIntValue(designerPropertiesJson,"columnSpacing", 2));
		jasperDesign.setLeftMargin(GsonUtility.optIntValue(designerPropertiesJson,"leftMargin", 20));
		jasperDesign.setRightMargin(GsonUtility.optIntValue(designerPropertiesJson,"rightMargin", 20));
		jasperDesign.setTopMargin(GsonUtility.optIntValue(designerPropertiesJson,"topMargin", 20));
		jasperDesign.setBottomMargin(GsonUtility.optIntValue(designerPropertiesJson,"bottomMargin", 20));
		jasperDesign.setTitleNewPage(GsonUtility.optBoolean(designerPropertiesJson,"titleNewPage"));
		jasperDesign.setFloatColumnFooter(GsonUtility.optBoolean(designerPropertiesJson,"floatColumnFooter"));
		jasperDesign.setSummaryNewPage(GsonUtility.optBoolean(designerPropertiesJson,"summaryNewPage"));
		if (designerPropertiesJson.has("language"))
			jasperDesign.setLanguage(designerPropertiesJson.get("language").getAsString());
		if (designerPropertiesJson.has("imports")) {
			JsonArray importsArray = designerPropertiesJson.getAsJsonArray("imports");
			for (int index = 0; index < importsArray.size(); index++) {
				jasperDesign.addImport(designerPropertiesJson.get(importsArray.get(index).getAsString()).getAsString());
			}
		}
		if (designerPropertiesJson.has("whenNoDataType"))
			jasperDesign.setWhenNoDataType(
					WhenNoDataTypeEnum.getByName(designerPropertiesJson.get("whenNoDataType").getAsString()));
		if (designerPropertiesJson.has("ignorePagination"))
			jasperDesign.setIgnorePagination(designerPropertiesJson.get("ignorePagination").getAsBoolean());
	}

	private void addDataSet(JasperDesign jasperDesign, JsonObject designerPropertiesJson) throws JRException {
		JsonArray dataSets = designerPropertiesJson.getAsJsonArray("dataSets");
		for (int index = 0; index < dataSets.size(); index++) {
			JsonObject eachJson = dataSets.get(index).getAsJsonObject();
			// todo prepare query from mapid
			String queryString = "";
			if (!GENERATOR_TYPE.equals("bean-datasource") && eachJson.has("connectionDetails")) {
				JsonObject efwdJson = eachJson.getAsJsonObject("connectionDetails");
				if(efwdJson != null && !efwdJson.keySet().isEmpty()) {
					queryString = prepareQueryStringForDataset(efwdJson);
				}
			}

			JRDesignDataset dataset = new JRDesignDataset(GsonUtility.optBooleanValue(eachJson,"isMainDataset", false));
			if (eachJson.has("name"))
				dataset.setName(eachJson.get("name").getAsString());
			JRDesignQuery datasetQuery = new JRDesignQuery();
			datasetQuery.setText(queryString);
			dataset.setQuery(datasetQuery);

			JsonArray fields = eachJson.getAsJsonArray("fields");
			for (int innerIndex = 0; innerIndex < fields.size(); innerIndex++) {
				JsonObject eachFieldJson = fields.get(innerIndex).getAsJsonObject();
				JRDesignField designField = new JRDesignField();
				designField.setName(eachFieldJson.get("name").getAsString());
				designField.setValueClassName(eachFieldJson.get("clazz").getAsString());
				dataset.addField(designField);
			}
			if (eachJson.has("parameters")) {
				JsonArray parametersJson = eachJson.getAsJsonArray("parameters");
				addParameters(parametersJson, dataset);
			}
			
			if (eachJson.has("groups")) {
				JsonArray groups = eachJson.getAsJsonArray("groups");
				for(JsonElement eachJsonGroupElement : groups ) {
					JsonObject eachJsonGroup  = eachJsonGroupElement.getAsJsonObject();
					JRDesignGroup group = getGroupFromJson(eachJsonGroup);
					dataset.addGroup(group);
				}
			}
			
			if (eachJson.has("variables") && !eachJson.getAsJsonArray("variables").isEmpty()) {
				JsonArray variablesArray = eachJson.getAsJsonArray("variables");
				addVariables(variablesArray, dataset);
			}
			jasperDesign.addDataset(dataset);
		}
	}

	private String prepareQueryStringForDataset(JsonObject connectionFormData) {
//		String queryString;
				
		if (connectionFormData.has("temp_uuid") && !connectionFormData.has("dir")) {
			connectionFormData.addProperty("dir", TempDirectoryCleaner.getTempDirectory().getAbsolutePath());
			JsonObject efwd = new JsonObject();
			efwd.addProperty("file", connectionFormData.get("temp_uuid").getAsString() + "." + JsonUtils.getEfwdExtension());
			connectionFormData.add("efwd", efwd);
			EnhancedQueryExecutor queryExecutor = new EnhancedQueryExecutor(connectionFormData.toString(),
					applicationProperties);
			return  queryExecutor.getUnProcessedQueryFromTemp();
		}
		else {
			EnhancedQueryExecutor queryExecutor = new EnhancedQueryExecutor(connectionFormData.toString(),
					applicationProperties);
			return  queryExecutor.getUnProcessedQuery();
		}
		
		
		
//		EnhancedQueryExecutor queryExecutor = new EnhancedQueryExecutor(efwdJson.toString(), applicationProperties);
//		queryString = queryExecutor.getUnProcessedQuery();
//		JsonObject dataMapTagContent = queryExecutor.getDataMapTagContent();
//		if (dataMapTagContent != null && dataMapTagContent.has("Parameters")) {
//			queryString = queryString.contains("${") ? queryString.replace("${", "$P{") : queryString;
//		}

//		return queryString;
	}

	private void addNoData(JsonObject noData) {
		JRDesignBand band = prepareCommonBands(noData);
		jasperDesign.setNoData(band);
	}

	private void addLastPageFooter(JsonObject lastPageFooter) {
		JRDesignBand band = prepareCommonBands(lastPageFooter);
		JsonArray newBreakObect=GsonUtility.optJsonArray(lastPageFooter,"break");
		if(newBreakObect!=null){
			for (int index = 0; index < newBreakObect.size(); index++) {
				addBreak(newBreakObect.get(index).getAsJsonObject(),band);
			}
		}

		jasperDesign.setLastPageFooter(band);
	}

	private void addColumnFooter(JsonObject columnFooter) {
		JRDesignBand band = prepareCommonBands(columnFooter);
		jasperDesign.setColumnFooter(band);
	}

	private void addGroups(JsonArray groupsArray) {
		
		for(JsonElement groupElement : groupsArray ) {
			JsonObject eachGroupJson = groupElement.getAsJsonObject();
			JRDesignGroup group =  getGroupFromJson(eachGroupJson);
			try {
				jasperDesign.addGroup(group);
				processGroupSection(eachGroupJson, group, "groupHeader", true);
				processGroupSection(eachGroupJson, group, "groupFooter", false);
			}
			catch (Exception e) {
				throw new EfwServiceException(e.getMessage());
			}
		}
	}
	
	
	
	private void processGroupSection(JsonObject groupJson, JRDesignGroup jrDesignGroup, String sectionKey,
			boolean isHeader) throws JRException {

			JsonObject section = GsonUtility.optJsonObject(groupJson, sectionKey);

			if (section == null || section.entrySet().isEmpty()) return ;

			JRDesignBand jrDesignBand = addGroupHeaderOrFooterSection(jrDesignGroup, section, isHeader);
		    JsonObject singleBreak = GsonUtility.optJsonObject(section, isHeader ? "break" : "groupFooterBandBreak");
		    if (singleBreak != null && !singleBreak.entrySet().isEmpty()) {
		        addBreak(singleBreak, jrDesignBand);
		    } else {
		        JsonArray breakArray = GsonUtility.optJsonArray(section, "break");
		        if (breakArray != null) {
		            for (int i = 0; i < breakArray.size(); i++) {
		                addBreak(breakArray.get(i).getAsJsonObject(), jrDesignBand);
		            }
		        }
		    }
	}

	private JRDesignBand addGroupHeaderOrFooterSection(JRDesignGroup designerGroup, JsonObject groupHeader, Boolean isHeader){
		JRDesignBand jrDesignBand = prepareCommonBands(groupHeader);
		JRSection groupSection = isHeader ? designerGroup.getGroupHeaderSection() : designerGroup.getGroupFooterSection();
		((JRDesignSection) groupSection).addBand(jrDesignBand);
		return jrDesignBand;
	}

	private void addQuery(JasperDesign jasperDesign, JsonObject designerPropertiesJson) {
		// if (!designerPropertiesJson.has("query")/* &&
		// "bean-datasource".equals(GENERATOR_TYPE)*/) {
		prepareQueryForDifferentGenerator(jasperDesign, designerPropertiesJson);
		/*
		 * }else if (designerPropertiesJson.has("query") &&
		 * "regular".equals(GENERATOR_TYPE)) {
		 * prepareQueryForDifferentGenerator(jasperDesign, designerPropertiesJson); }
		 */
	}

	private void prepareQueryForDifferentGenerator(JasperDesign jasperDesign, JsonObject designerPropertiesJson) {
		String query = GsonUtility.optStringValue(designerPropertiesJson,"query", " ");
		JRDesignQuery newQuery = new JRDesignQuery();
		newQuery.setText(query);
		jasperDesign.setQuery(newQuery);
		addFieldsForQuery(jasperDesign, designerPropertiesJson);
	}

	private void addFieldsForQuery(JasperDesign jasperDesign, JsonObject designerPropertiesJson) {
		try {
			addFields(jasperDesign, designerPropertiesJson.getAsJsonArray("fields"));
		} catch (JRException e) {
			e.printStackTrace();
		}
	}

	private void addTitle(JsonObject titleJson) {
		JRDesignBand band = prepareCommonBands(titleJson);
		JsonArray newBreakObect=GsonUtility.optJsonArray(titleJson,"break");
		if(newBreakObect!=null){
			for (int index = 0; index < newBreakObect.size(); index++) {
				addBreak(newBreakObect.get(index).getAsJsonObject(),band);
			}
		}
		jasperDesign.setTitle(band);
	}

	private JRDesignBand prepareCommonBands(JsonObject bandJson) {
		bandJson.addProperty("generateXML",isGenerateXml);
		BandFactory factory = new BandFactory(bandJson, jasperDesign);
		return factory.getBand();
	}

	private JRDesignBand addSummary(JsonObject summaryJson) {
		JRDesignBand band = prepareCommonBands(summaryJson);
		jasperDesign.setSummary(band);
		return band;
	}

	private JRDesignBand addPageFooter(JsonObject pageFooterJson) {
		JRDesignBand band = prepareCommonBands(pageFooterJson);
		jasperDesign.setPageFooter(band);
		return band;
	}

	private JRDesignBand addPageHeader(JsonObject pageHeaderJson) {
		JRDesignBand band = prepareCommonBands(pageHeaderJson);
		jasperDesign.setPageHeader(band);
        return band;
	}

	// add Parameters
	private void addParameters(JsonArray parameters, JRDesignDataset dataSet) throws JRException {
		prepareCommonParameters(parameters, dataSet);
	}

	private void prepareCommonParameters(JsonArray parameters, Object object) throws JRException {
		if (parameters.isEmpty()) {
			return;
		}
		for (int index = 0; index < parameters.size(); index++) {
			JsonObject eachParameterJson = parameters.get(index).getAsJsonObject();
			JRDesignParameter parameter = new JRDesignParameter();
			parameter.setName(eachParameterJson.get("name").getAsString());
			String valueClassName = eachParameterJson.get("className").getAsString();
			
			// TODO : This is unnecessary check, fix the testcases and remove this
			if (java.sql.Connection.class.getName().equalsIgnoreCase(valueClassName)) {
				valueClassName = net.sf.jasperreports.engine.JRDataSource.class.getName();
			}
			
			if (net.sf.jasperreports.engine.JRDataSource.class.getName().equalsIgnoreCase(valueClassName)) {
				valueClassName = com.helicalinsight.adhoc.jreport.LazySubDatasetDataSourceFactory.class.getName();
			}
			parameter.setValueClassName(valueClassName);
			parameter.setForPrompting(GsonUtility.optBooleanValue(eachParameterJson,"isPrompt", false));
			if (eachParameterJson.has("evaluationTime"))
				parameter.setEvaluationTime(
						ParameterEvaluationTimeEnum.byName(eachParameterJson.get("evaluationTime").getAsString()));
			if (eachParameterJson.has("defaultExpression")) {
				String defaultExpression = eachParameterJson.get("defaultExpression").isJsonArray()?
						eachParameterJson.get("defaultExpression").toString():
						eachParameterJson.get("defaultExpression").getAsString();

				if (!defaultExpression.isEmpty()) {
                    JRDesignExpression expression = new JRDesignExpression();
                    String name = GsonUtility.optStringValue(eachParameterJson,"expressionType", "default");
                    if(isGenerateXml && "simpleText".equals(name)) {
                        expression.setText("\""+defaultExpression.replaceAll("\\\"","\\\\\"")+"\"");
                    }else{
                        expression.setText(defaultExpression);
                    }

                    expression.setType(ExpressionTypeEnum.getByName(name));
                    parameter.setDefaultValueExpression(expression);
                }
			}
			if (object instanceof JRDesignDataset)
				((JRDesignDataset) object).addParameter(parameter);
			else
				((JasperDesign) object).addParameter(parameter);
		}
	}

	private void addParameters(JsonArray parameters) throws JRException {
		prepareCommonParameters(parameters, jasperDesign);
	}

	// Adding fields that are variable types and maintaining and declaring of
	// Add Fields
	private void addFields(JasperDesign jasperDesign, JsonArray fieldsArray) throws JRException {
		if (!fieldsArray.isEmpty()) {
			for (int index = 0; index < fieldsArray.size(); index++) {
				field = new JRDesignField();
				JsonObject eachJson = fieldsArray.get(index).getAsJsonObject();
				field.setName(eachJson.get("name").getAsString());
				field.setValueClassName(eachJson.get("clazz").getAsString());
				if (eachJson.has("description"))
					field.setDescription(eachJson.get("description").getAsString());
				jasperDesign.addField(field);
			}

		}
	}

	// Add Variables JRDesignDataset dataset
	private void addVariables(JsonArray variablesArray) throws JRException {
		for (int index = 0; index < variablesArray.size(); index++) {
			JsonObject eachVariableJson = variablesArray.get(index).getAsJsonObject();
			prepareVariable(eachVariableJson);
		}
	}

	private void addVariables(JsonArray variablesArray, JRDesignDataset dataset) throws JRException {
		for (int index = 0; index < variablesArray.size(); index++) {
			JsonObject eachVariableJson = variablesArray.get(index).getAsJsonObject();
			prepareVariable(eachVariableJson, dataset);
		}
	}

	private void prepareVariable(JsonObject eachVariableJson) throws JRException {
		JRDesignVariable variable = prepareCommonVariables(eachVariableJson, null);
		jasperDesign.addVariable(variable);
	}

	private void prepareVariable(JsonObject eachVariableJson, JRDesignDataset dataSet) throws JRException {
		JRDesignVariable variable = prepareCommonVariables(eachVariableJson, dataSet);
		dataSet.addVariable(variable);
	}

	private JRDesignVariable prepareCommonVariables(JsonObject eachVariableJson, JRDesignDataset dataset) {
		Map<String, JRGroup> groupsMap = dataset != null ? dataset.getGroupsMap() : jasperDesign.getGroupsMap();
		JRDesignVariable variable = new JRDesignVariable();
		variable.setName(eachVariableJson.get("name").getAsString());
		if (eachVariableJson.has("incrementType")) {
			String incrementType = eachVariableJson.get("incrementType").getAsString();
			variable.setIncrementType(IncrementTypeEnum.getByName(incrementType));
			if (incrementType.equals("Group")) {
				variable.setIncrementGroup(groupsMap.get(eachVariableJson.get("incrementGroup").getAsString()));
			}
		}
		if (eachVariableJson.has("incrementFactoryClass"))
			variable.setIncrementerFactoryClassName(eachVariableJson.get("incrementFactoryClass").getAsString());
		// todo need to change the dynamic retrieving of class name to static from front
		// end.
		variable.setValueClassName(eachVariableJson.get("className").getAsString());
		variable.setCalculation(CalculationEnum.getByName(eachVariableJson.get("calculation").getAsString()));
		String resetType = eachVariableJson.get("resetType").getAsString();
		variable.setResetType(ResetTypeEnum.getByName(resetType));
		if (resetType.equals("Group")) {
			variable.setResetGroup(groupsMap.get(eachVariableJson.get("resetGroup").getAsString()));// resetGroup
		}
		JRDesignExpression expression = new JRDesignExpression();
		expression.setText(eachVariableJson.get("expression").getAsString());
		if (eachVariableJson.has("expressionType"))
			expression.setType(ExpressionTypeEnum.getByName(eachVariableJson.get("expressionType").getAsString()));
		variable.setExpression(expression);
		if (eachVariableJson.has("isSystemDefined"))
			variable.setSystemDefined(eachVariableJson.get("isSystemDefined").getAsBoolean());
		if (eachVariableJson.has("initialValueExpression")) {
			JRDesignExpression initialValueExpression = new JRDesignExpression();
			initialValueExpression.setText(eachVariableJson.get("initialValueExpression").getAsString());
			variable.setInitialValueExpression(initialValueExpression);
		}
		return variable;
	}

	// Add ColumnHeader
	private void addColumnHeader(JsonObject columnHeaderJson) throws JRException {
		JRDesignBand band = prepareCommonBands(columnHeaderJson);
		jasperDesign.setColumnHeader(band);
	}

	// Add Detail
	private JRDesignBand addDetail(JsonObject detailJson, int index) throws JRException {
		JRDesignBand band = prepareCommonBands(detailJson);
		((JRDesignSection) jasperDesign.getDetailSection()).addBand(index, band);
		return band;
	}

	/**
	 * prepareParameterForReportExecution using gson
	 * 
	 * @param JsonObject jsonFormData
	 * @return JsonObject
	 */
	public JsonObject prepareParameterForReportExecution(JsonObject jsonFormData) {
		JsonObject designerPropertiesObject = jsonFormData.getAsJsonObject("designerProperties");
		JsonArray parametersJsonArray = GsonUtility.optJsonArray(designerPropertiesObject, "parameters");
		JsonObject parameterJson = new JsonObject();
		if (parametersJsonArray != null) {
			for (int index = 0; index < parametersJsonArray.size(); index++) {
				JsonObject eachJson = parametersJsonArray.get(index).getAsJsonObject();
				if (eachJson.has("value"))
					parameterJson.add(eachJson.get("name").getAsString(), eachJson.get("value"));
				else if ( JDBC_CONNECTION.equals(GsonUtility.optString(eachJson, "className")) && eachJson.has("connectionDetails")) {
					parameterJson.add(eachJson.get("name").getAsString(),eachJson.getAsJsonObject("connectionDetails"));
				}
				else if ( JRDATASOURCE.equals(GsonUtility.optString(eachJson, "className")) && eachJson.has("connectionDetails")) {
							parameterJson.add(eachJson.get("name").getAsString(),eachJson.getAsJsonObject("connectionDetails"));
				}
			}
		}
		JsonObject preparedFormData = new Gson().fromJson(jsonFormData, JsonObject.class);
		preparedFormData.remove("designerProperties");
		preparedFormData.add("parameters", parameterJson);
		return preparedFormData;
	}

	
	/**
	 * method not used that's why commented it
	private String executeQuery(JsonObject connectionDetails) {
		IComponent viewLabelsRetrievalComponent = FactoryMethodWrapper
				.getTypedInstance("com.helicalinsight.adhoc.ViewLabelsRetrievalComponent", IComponent.class);

		return viewLabelsRetrievalComponent.executeComponent(connectionDetails.toString());
	}
	 **/
	public Class<?> getEquivalentClass(String type) {
		Class<?> result = Object.class;
		switch (type) {
		case "text":
			result = String.class;
			break;
		case "numeric":
			result = Integer.class;
			break;
		case "boolean":
			result = Boolean.class;
			break;
		case "date":
			result = java.sql.Date.class;
			break;
		case "dateTime":
			result = java.sql.Timestamp.class;
			break;
		case CONNECTION_CLASS:
			if ("bean-datasource".equals(GENERATOR_TYPE))
				result = net.sf.jasperreports.engine.JRDataSource.class;
			else
				result = java.sql.Connection.class;
			break;
		default:
			result = String.class;
		}
		return result;
	}
	
	
	private JRDesignGroup getGroupFromJson(JsonObject eachGroupJson) {
		JRDesignGroup jrDesignGroup = new JRDesignGroup();
		if (eachGroupJson.has("countVariable")) {
			JRDesignVariable countVariable = prepareCommonVariables(eachGroupJson.getAsJsonObject("countVariable"),null);
			jrDesignGroup.setCountVariable(countVariable);
		}
		if (eachGroupJson.has("expression")) {
			String defaultExpression = eachGroupJson.get("expression").getAsString();
			if (!defaultExpression.isEmpty()) {
				JRDesignExpression expression = new JRDesignExpression();
				expression.setText(defaultExpression);
				expression.setType(ExpressionTypeEnum
						.getByName(GsonUtility.optStringValue(eachGroupJson, "expressionType", "default")));
				jrDesignGroup.setExpression(expression);
			}
		}
		if (eachGroupJson.has("name"))
			jrDesignGroup.setName(eachGroupJson.get("name").getAsString());
		if (eachGroupJson.has("footerPosition"))
			jrDesignGroup
					.setFooterPosition(FooterPositionEnum.getByName(eachGroupJson.get("footerPosition").getAsString()));
		if (eachGroupJson.has("keepTogether"))
			jrDesignGroup.setKeepTogether(eachGroupJson.get("keepTogether").getAsBoolean());
		if (eachGroupJson.has("minDetailsToStartFromTop"))
			jrDesignGroup.setMinDetailsToStartFromTop(eachGroupJson.get("minDetailsToStartFromTop").getAsInt());
		if (eachGroupJson.has("preventOrphanFooter"))
			jrDesignGroup.setPreventOrphanFooter(eachGroupJson.get("preventOrphanFooter").getAsBoolean());
		if (eachGroupJson.has("minHeightToStartNewPage"))
			jrDesignGroup.setMinHeightToStartNewPage(eachGroupJson.get("minHeightToStartNewPage").getAsInt());
		if (eachGroupJson.has("reprintHeaderOnEachColumn"))
			jrDesignGroup.setReprintHeaderOnEachColumn(eachGroupJson.get("reprintHeaderOnEachColumn").getAsBoolean());
		if (eachGroupJson.has("reprintHeaderOnEachPage"))
			jrDesignGroup.setReprintHeaderOnEachPage(eachGroupJson.get("reprintHeaderOnEachPage").getAsBoolean());
		if (eachGroupJson.has("resetPageNumber"))
			jrDesignGroup.setResetPageNumber(eachGroupJson.get("resetPageNumber").getAsBoolean());
		if (eachGroupJson.has("startNewColumn"))
			jrDesignGroup.setStartNewColumn(eachGroupJson.get("startNewColumn").getAsBoolean());
		if (eachGroupJson.has("startNewPage"))
			jrDesignGroup.setStartNewPage(eachGroupJson.get("startNewPage").getAsBoolean());

		return jrDesignGroup;
	}
    
	
	private void addWaterMark(JRDesignBand band) {
		
		Boolean isExport = GsonUtility.optBooleanValue(formData, "isExport", false);
		
		if ( !isExport) return ;
		
		JsonObject meta = ResponseMetadataEnricher.getMetaObject();
		String licenseType = GsonUtility.optString(meta, "licenseType");
		if (ExportWatermarkHelper.isWatermarkLicense(licenseType)) {
			HCRTextField hcrTextField = ApplicationContextAccessor.getBean(HCRTextField.class);

			String link = ExportWatermarkHelper.getWatermarkLink();
			String poweredBy = ExportWatermarkHelper.getWatermarkText();

			int y = 0;

			for (JRElement element : band.getElements()) {
				y = Math.max(y, element.getY() + element.getHeight());
			}

			String textField = """
							{
							"textFieldExpression": "\\"%s\\"",
							"X": 0,
							"Y": %d,
							"textHeight": 30,
							"textWidth": 555,
							"fontName": "Serif",
							"textFontSize": 8,
							"printRepeatedValues": true
						}
					""".formatted(poweredBy, y);

			JRDesignTextField field = hcrTextField.prepareTextField(GsonUtility.parseString(textField, JsonObject.class), jasperDesign);
			field.setVerticalTextAlign(VerticalTextAlignEnum.BOTTOM);
			field.setHorizontalTextAlign(HorizontalTextAlignEnum.RIGHT);
			field.setHyperlinkType(HyperlinkTypeEnum.REFERENCE);
			
			JRDesignExpression expression = new JRDesignExpression();
			expression.setText("\"" + link + "\"");
			field.setHyperlinkReferenceExpression(expression);
			band.addElement(field);
			band.setHeight(y + field.getHeight());
			jasperDesign.setPageFooter(band);
		}
	}

}
