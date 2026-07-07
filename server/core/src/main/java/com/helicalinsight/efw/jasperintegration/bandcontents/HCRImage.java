package com.helicalinsight.efw.jasperintegration.bandcontents;

import java.awt.Color;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helicalinsight.admin.service.ComponentFactory;
import com.helicalinsight.core.ResourceLevelPermissionEvaluator;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.HCRUtils;
import com.helicalinsight.efw.exceptions.AccessDeniedException;
import com.helicalinsight.efw.exceptions.ResourceNotFoundException;
import com.helicalinsight.efw.jasperintegration.IHCRBandContents;
import com.helicalinsight.efw.serviceframework.IComponent;

import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignHyperlinkParameter;
import net.sf.jasperreports.engine.design.JRDesignImage;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;
import net.sf.jasperreports.engine.type.FillEnum;
import net.sf.jasperreports.engine.type.HorizontalImageAlignEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.OnErrorTypeEnum;
import net.sf.jasperreports.engine.type.ScaleImageEnum;
import net.sf.jasperreports.engine.type.VerticalImageAlignEnum;

/**
 * Created by author on 11/13/2019.
 *
 * @author Rajesh
 */
@Component
public class HCRImage implements IHCRBandContents {
    

	private final ResourceLevelPermissionEvaluator permissionEvaluator;
	
    public HCRImage(ResourceLevelPermissionEvaluator permissionEvaluator) {
    	this.permissionEvaluator = permissionEvaluator;
    }
    
    
    @Override
    public void processContent(JRDesignBand band, JsonObject bandJson, JasperDesign jasperDesign) {
        addImageIfAny(band, bandJson, jasperDesign);
    }

    private void addImageIfAny(JRDesignBand band, JsonObject bandJson, JasperDesign jasperDesign) {
        if (bandJson.has("image")) {
            JsonArray imageArray = bandJson.getAsJsonArray("image");
                for (int index = 0; index < imageArray.size(); index++) {
                JsonObject eachImageJson = imageArray.get(index).getAsJsonObject();
                JRDesignImage jrDesignImage = prepareImage(jasperDesign, eachImageJson);
                band.addElement(jrDesignImage);
            }
        }
    }

    public JRDesignImage prepareImage(JasperDesign jasperDesign, JsonObject imageJson) {
        
        JRDesignExpression expression = new JRDesignExpression();
        if(imageJson.has("link")&&StringUtils.isNotBlank(imageJson.get("link").getAsString())){
            String imagePathInSolutionDir=imageJson.get("link").getAsString();
            expression.setText("\""+imagePathInSolutionDir+"\"");
        }
        else{
        	String imageData =  getImage(imageJson); 
        	String[] imageDataArr = imageData.split(",",2);
            if (imageDataArr.length  > 1) {
	        	String base64 = imageDataArr[1];
	        	String base64Literal = "\"" + base64 + "\"";
	        	String exprText = 
	        	    "new java.io.ByteArrayInputStream(" +
	        	    "java.util.Base64.getDecoder().decode(" + base64Literal + "))";
	        	expression.setText(exprText);
            }
            else {
            	expression.setText(imageData);
            }
        }

        return prepareCommonImageProperties(jasperDesign, imageJson, expression);
    }
    
    
	private String getImage(JsonObject imageJson) {
	
		int imageHeight = imageJson.get("imageHeight").getAsInt();
		int imageWidth = imageJson.get("imageWidth").getAsInt();
		String onErrorType = GsonUtility.optString(imageJson,"onErrorType");
		boolean isError = "Error".equalsIgnoreCase(onErrorType);
		
		String dir = GsonUtility.optString(imageJson, "dir");
		String file = GsonUtility.optString(imageJson, "file");
		
		if ( StringUtils.isNotBlank(dir) && StringUtils.isNotBlank(file)) {
			try {
				permissionEvaluator.evaluate("/services","util", "io", "imageService", imageJson.toString());
				IComponent iComponent = ComponentFactory. getComponentInstance("com.helicalinsight.admin.management.ImageListComponentDB");
				String response = iComponent.executeComponent(imageJson.toString());
				JsonObject responseBody = GsonUtility.parseString(response, JsonObject.class);
				return GsonUtility.getByPath(responseBody, "data.imageData").getAsString();
			}
			catch (ResourceNotFoundException | AccessDeniedException ex) {
				return isError ? HCRUtils.textToImage(ex.getMessage(),imageWidth,imageHeight) : "";
			}
		}
		else {
			return GsonUtility.optStringValue(imageJson, "expression","");
		}
	}

    private JRDesignImage prepareCommonImageProperties(JasperDesign jasperDesign, JsonObject imageJson, JRDesignExpression expression) {
        JRDesignImage image = new JRDesignImage(jasperDesign);
        image.setX(imageJson.get("X").getAsInt());
        image.setY(imageJson.get("Y").getAsInt());
        image.setWidth(imageJson.get("imageWidth").getAsInt());
        image.setHeight(imageJson.get("imageHeight").getAsInt());
        image.setExpression(expression);
        HCRUtils.applyStyleReference(imageJson, image::setStyleNameReference);
        if (imageJson.has("mode"))
            image.setMode(ModeEnum.getByName(imageJson.get("mode").getAsString()));
        if (imageJson.has("imageBackcolor"))
            image.setBackcolor(Color.decode(imageJson.get("imageBackcolor").getAsString()));
        if (imageJson.has("imageForecolor"))
            image.setForecolor(Color.decode(imageJson.get("imageForecolor").getAsString()));
        if (imageJson.has("printRepeatedValues"))
            image.setPrintRepeatedValues(imageJson.get("printRepeatedValues").getAsBoolean());
        if (imageJson.has("removeLineWhenBlank"))
            image.setRemoveLineWhenBlank(imageJson.get("removeLineWhenBlank").getAsBoolean());
        if (imageJson.has("key"))
            image.setKey(imageJson.get("key").getAsString());
        if (imageJson.has("evaluationTime")) {
            String evaluationTime = imageJson.get("evaluationTime").getAsString();
            image.setEvaluationTime(EvaluationTimeEnum.getByName(evaluationTime));
            evaluationGroup(imageJson, jasperDesign, image, evaluationTime);
        }
        if (imageJson.has("isLazy"))
            image.setLazy(imageJson.get("isLazy").getAsBoolean());
        if (imageJson.has("isUsingCache"))
            image.setUsingCache(new Boolean(imageJson.get("isUsingCache").getAsBoolean()));
        if (imageJson.has("fill"))
            image.setFill(FillEnum.getByName(imageJson.get("fill").getAsString()));
        if (imageJson.has("scaleImage"))
            image.setScaleImage(ScaleImageEnum.getByName(imageJson.get("scaleImage").getAsString()));
        if (imageJson.has("onErrorType"))
            image.setOnErrorType(OnErrorTypeEnum.getByName(imageJson.get("onErrorType").getAsString()));
        if (imageJson.has("horizontalImageAlign"))
            image.setHorizontalImageAlign(HorizontalImageAlignEnum.getByName(imageJson.get("horizontalImageAlign").getAsString()));
        if (imageJson.has("verticalImageAlign"))
            image.setVerticalImageAlign(VerticalImageAlignEnum.getByName(imageJson.get("verticalImageAlign").getAsString()));
        
        JsonObject borderJson = GsonUtility.optJsonObject(imageJson, HCRUtils.PROPERTY_BORDER);
        HCRUtils.movePaddingFromBorder(borderJson, imageJson);
        HCRUtils.prepareBorder((JRDesignElement) image, borderJson); 
        HCRUtils.preparePadding((JRDesignElement) image, GsonUtility.optJsonObject(imageJson, HCRUtils.PROPERTY_PADDING));
       
        setElementPositionType(imageJson, image);
        prepareStretchType(imageJson, image);
        image.setPrintInFirstWholeBand(GsonUtility.optBooleanValue(imageJson,"printInFirstWholeBand", false));
        if (imageJson.has("printWhenExpression")) {
            JRDesignExpression designExpression = new JRDesignExpression();
            String printWhenExpression = GsonUtility.optString(imageJson, "printWhenExpression");
            designExpression.setText(printWhenExpression);
            if(StringUtils.isNotBlank(printWhenExpression))
            image.setPrintWhenExpression(designExpression);
        }
        if (imageJson.has("hyperlinkTooltipExpression")) {
            JRDesignExpression designExpression = new JRDesignExpression();
            String hyperlinkTooltipExpression = GsonUtility.optString(imageJson, "hyperlinkTooltipExpression");
            designExpression.setText(hyperlinkTooltipExpression);
            if(StringUtils.isNotBlank(hyperlinkTooltipExpression))
            image.setHyperlinkTooltipExpression(designExpression);
        }
        image.setPrintWhenDetailOverflows(GsonUtility.optBooleanValue(imageJson,"printWhenDetailOverflows", false));
        prepareGroupChanges(jasperDesign, imageJson, image);
        if (imageJson.has("hyperlinkParameterName") && imageJson.has("hyperlinkParameterExpression")) {
            JRDesignHyperlinkParameter hyperlinkParameters = new JRDesignHyperlinkParameter();
            String hyperlinkParameterName = imageJson.get("hyperlinkParameterName").getAsString();
            hyperlinkParameters.setName(hyperlinkParameterName);
            JRDesignExpression designExpression = new JRDesignExpression();
            String hyperlinkParameterExpression = GsonUtility.optString(imageJson, "hyperlinkParameterExpression");
            designExpression.setText(hyperlinkParameterExpression);
            hyperlinkParameters.setValueExpression(designExpression);
            if(StringUtils.isNotBlank(hyperlinkParameterExpression)&& StringUtils.isNotBlank(hyperlinkParameterName))
            image.addHyperlinkParameter(hyperlinkParameters);
        }
        
        
        JsonObject customSettings = GsonUtility.optJsonObject(imageJson, "customSettings");
        HCRUtils.applyCustomProperties(image, customSettings);
        
        return image;
    }
 
}
