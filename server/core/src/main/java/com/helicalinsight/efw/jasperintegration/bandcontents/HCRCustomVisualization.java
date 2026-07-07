package com.helicalinsight.efw.jasperintegration.bandcontents;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.HCRUtils;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.jasperintegration.HCRCustomScriptCompiler;
import com.helicalinsight.efw.jasperintegration.IHCRBandContents;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;

import net.sf.jasperreports.components.items.StandardItem;
import net.sf.jasperreports.components.items.StandardItemData;
import net.sf.jasperreports.components.items.StandardItemProperty;
import net.sf.jasperreports.customvisualization.design.CVDesignComponent;
import net.sf.jasperreports.engine.component.ComponentKey;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignComponentElement;
import net.sf.jasperreports.engine.design.JRDesignDatasetRun;
import net.sf.jasperreports.engine.design.JRDesignElementDataset;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;

@Component
public class HCRCustomVisualization implements IHCRBandContents {
	
	private static final String PROPERTY_CUSTOM_VISUALIZATION = "customVisualization";
	private static final String PROPERTY_TYPE = "type";
	private static final String DEFAULT_CVC_MODULE_NAME = "CVC";
	private static final String DEFUALT_CSS_NAME = "style.css";
	
	private static final String TEMP_DIR =  TempDirectoryCleaner.getTempDirectory().getAbsolutePath();

	
	
	@Override
	public void processContent(JRDesignBand band, JsonObject bandJson, JasperDesign jasperDesign) {
			addCvcIfAny(band, bandJson);
	}

	private void addCvcIfAny(JRDesignBand band, JsonObject bandJson) {
		if (bandJson.has(PROPERTY_CUSTOM_VISUALIZATION)) {
			JsonArray cvcArray = GsonUtility.optJsonArray(bandJson, PROPERTY_CUSTOM_VISUALIZATION);
			if (cvcArray != null) {
				for (JsonElement cvcElement : cvcArray) {
					JsonObject cvc = cvcElement.getAsJsonObject();
					JRDesignComponentElement jrDesignComponentElement = prepareCvc(cvc);
					band.addElement(jrDesignComponentElement);
				}
			}
		}
	}
	
	private JRDesignComponentElement prepareCvc(JsonObject cvc) {
		
		JRDesignComponentElement element = new JRDesignComponentElement();
		
		String type = GsonUtility.optString(cvc, PROPERTY_TYPE);
		
		if ( StringUtils.isBlank(type) ) {
			throw new EfwServiceException("Required property 'type'  missing");
		}
	
		String  systemDirectory = ApplicationProperties.getInstance().getSystemDirectory().replace("\\", "/");
		
		JsonObject itemProperties = GsonUtility.optJsonObject(cvc, "itemProperties");	
		
		if ( type.equalsIgnoreCase("custom")) {
			
			JsonObject customScriptObj = GsonUtility.getJsonObjectIfNotEmpty(cvc, "customScript");
			
			if (customScriptObj == null ) {
				throw new EfwServiceException("Please provide the custom script configuration.");
			}
			
			String script = GsonUtility.optString(customScriptObj, "script");
			String css  = GsonUtility.optString(customScriptObj, "css");
			String module = GsonUtility.optString(customScriptObj, "module");
			String dependencies = GsonUtility.optString(customScriptObj, "dependencies");
			
			String dir =  TEMP_DIR + File.separator +  String.valueOf(System.currentTimeMillis());
			
			HCRCustomScriptCompiler compiler = new HCRCustomScriptCompiler();
			String compiledFilePath = compiler.compile(script, module, dependencies, dir);
			itemProperties.addProperty("script", compiledFilePath);
			itemProperties.addProperty("module", module);
			
			if(StringUtils.isNotBlank(css)) {
				
				String cssFilePath = Paths.get(dir,DEFUALT_CSS_NAME).toString();
				HCRUtils.writeToFile(css, dir, DEFUALT_CSS_NAME);
				itemProperties.addProperty("css", cssFilePath);
			}
			
		}
		else {
			String jsFilePath = Paths.get(systemDirectory , HCRUtils.HCR_SCRIPTS_PATH, "customized",  type+".js").toString();
			itemProperties.addProperty("script", jsFilePath);
			itemProperties.addProperty("module",DEFAULT_CVC_MODULE_NAME);
		}
		
		
		HCRUtils.configureComponentElementProperties(element, cvc);
		  
		CVDesignComponent component = new CVDesignComponent();
		
		JsonObject itemData = GsonUtility.optJsonObject(cvc, "itemData");		
		
		configureItemData(component, itemData);
		
		configureItemProperties(component, itemProperties);

		component.setEvaluationTime(EvaluationTimeEnum.getByName(GsonUtility.optStringValue(cvc, "evaluationTime", "Report")));
        
		element.setComponentKey(new ComponentKey("http://www.jaspersoft.com/cvcomponent", "cvc", "customvisualization"));
		
		element.setComponent(component);
		
		return element;
	}
	
	
	private void configureItemData(CVDesignComponent component, JsonObject itemData) {

		StandardItemData standardItemData = new StandardItemData();
		
		JsonObject dataSetRunJson = GsonUtility.getJsonObjectIfNotEmpty(itemData, DATASET_RUN);
		
		if (dataSetRunJson != null ) {
			JRDesignDatasetRun datasetRun = HCRUtils.configureSubDatasetRun(dataSetRunJson);
			JRDesignElementDataset dataset =  new JRDesignElementDataset();
			dataset.setDatasetRun(datasetRun);
			JsonObject itemsObject =  dataSetRunJson.getAsJsonObject("items");
			standardItemData.addItem(createItem(itemsObject));
			standardItemData.setDataset(dataset);
		}
		else {
			JsonArray dataArray = GsonUtility.optJsonArray(itemData, "data");
			if ( dataArray != null ) {
				for(JsonElement dataItemElement : dataArray ) {
					JsonObject dataObject = dataItemElement.getAsJsonObject();
					StandardItem item =  createItem(dataObject);
					standardItemData.addItem(item);
				}
			}
		}
		component.addItemData(standardItemData);
	}
	
	
	private void configureItemProperties(CVDesignComponent component,  JsonObject itemPropertyJson) {
		createItemProperties(itemPropertyJson)
		.forEach(component::addItemProperty);
	}
	
	
	private StandardItem createItem(JsonObject itemObject) {
		StandardItem item = new StandardItem();
		List<StandardItemProperty> properties =  createItemProperties(itemObject);
		properties.forEach(item::addItemProperty);	
		return item;
	}
	
	private List<StandardItemProperty> createItemProperties(JsonObject itemPropertyJson) {
		List<StandardItemProperty> properties  = new ArrayList<>();
		Set<String> keys =  itemPropertyJson.keySet();
		for(String key : keys ) {
			
			StandardItemProperty property = new StandardItemProperty();
			
			property.setName(key);
			
			JsonElement valueElement = itemPropertyJson.get(key);
			
			String value = "";
			
			if ( valueElement.isJsonPrimitive()) {
				value = valueElement.getAsString();
			}
			else {
				value = valueElement.toString();
			}
			if ( value.startsWith("$F") || value.startsWith("$V") || value.startsWith("$P") ) {
				property.setValueExpression(HCRUtils.getExpressionFromJson(itemPropertyJson, key));
			} else {
				property.setValue(value);
			}
			properties.add(property);
		}
		
		return properties;
	}
}
