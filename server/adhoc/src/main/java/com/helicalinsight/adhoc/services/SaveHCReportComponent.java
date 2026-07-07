package com.helicalinsight.adhoc.services;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.datasource.managed.jaxb.DataMap;
import com.helicalinsight.datasource.managed.jaxb.EfwdForCe;
import com.helicalinsight.datasource.managed.jaxb.HCReport;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.FileUtils;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import com.helicalinsight.efwce.EFWCEUtils;
import com.helicalinsight.resourcesecurity.SecurityUtils;
import com.helicalinsight.resourcesecurity.jaxb.Security;

/**
 * Created by author on 10/17/2019.
 *
 * @author Rajesh
 */
public class SaveHCReportComponent implements IComponent {
    private static final ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
    private boolean isUpdate = false;

    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formJson = JsonParser.parseString(jsonFormData).getAsJsonObject();
        String uuid;
        JsonObject responseJson;
        responseJson = new JsonObject();
        Boolean isTempSave;
        // String temp_uuid = null;
        String dir = GsonUtility.optStringValue(formJson,"dir", null);
        Boolean isHCRPresent = formJson.has("state") || formJson.has("diagram");
        String hcrExtension = JsonUtils.getHCRExtension();
        String efwdExtension = JsonUtils.getEfwdExtension();
      /*  if (formJson.has("temp_uuid")) {
            temp_uuid = formJson.getString("temp_uuid");
        }*/
        if (formJson.has("uuid")) {
            uuid = FilenameUtils.removeExtension(formJson.get("uuid").getAsString());
            isUpdate = true;
        } else if (formJson.has("saveUUID") && !formJson.get("saveUUID").getAsString().isEmpty()) {
            uuid = formJson.get("saveUUID").getAsString();
            isUpdate = false;
        } else {
            uuid = UUID.randomUUID().toString();
            isUpdate = false;
        }

        if (dir == null) {
            dir = TempDirectoryCleaner.getTempDirectory().getAbsolutePath();
            isTempSave = true;
            if (!isUpdate)
                uuid = "_temp_" + uuid;
        } else {
            isTempSave = false;
        }
        EfwdForCe efwdForCe = null;
        JsonObject efwdJsonObject = GsonUtility.optJsonObject(formJson,"efwd");
        //save efwd file
        boolean saveStatus;
        if (efwdJsonObject != null && isTempSave) {
            saveStatus = EFWCEUtils.saveTempEfwd(efwdJsonObject, dir, uuid, efwdExtension);
            if (saveStatus) {
            	GsonUtility.accumulate(responseJson,"temp_uuid", uuid);
            } else {
            	GsonUtility.accumulate(responseJson,"message", "Could not able to save the file in temp.");
            }
            return responseJson.toString();

        } else if (efwdJsonObject != null && isHCRPresent) {
            efwdForCe = EFWCEUtils.saveEfwd(efwdJsonObject, dir, uuid, efwdExtension);
        }
        /*if (temp_uuid != null && !temp_uuid.isEmpty() && isHCRPresent) {
            efwdForCe = EFWCEUtils.moveEfwdFromTempToReal(temp_uuid, dir, uuid, JsonUtils.getEfwdExtension());
        }*/

        if (formJson.has("state") || formJson.has("diagram")) {
            HCReport hcr = prepareHCRObject(formJson, uuid, dir, getHCRFile(dir, uuid, hcrExtension));
            addEfwdDetailsToHcr(hcr, uuid, efwdForCe);
            saveHCRFile(hcrExtension, hcr, uuid, dir);
        }
        GsonUtility.accumulate(responseJson,"uuid", uuid);
        if (!isUpdate)
        	GsonUtility.accumulate(responseJson,"message", "Report is saved successfully");
        else if (isUpdate)
        	GsonUtility.accumulate(responseJson,"message", "Report is updated successfully");
        return responseJson.toString();
    }

    private void addEfwdDetailsToHcr(HCReport hcr, String uuid, EfwdForCe efwdForCe) {
        if (efwdForCe != null) {
            String efwdFileName = uuid + "." + JsonUtils.getEfwdExtension();
            List<DataMap> dataMapList = efwdForCe.getDataMaps() != null ? efwdForCe.getDataMaps().getDataMapList() : null;
            if (dataMapList != null) {
                List<String> listOfMapId = new ArrayList<>();
                dataMapList.forEach(eachDataMap -> listOfMapId.add(eachDataMap.getId()));
                JsonObject efwdDetails = new JsonObject();
                efwdDetails.addProperty("efwdFileName", efwdFileName);
                JsonArray listOfMapIdArr = new Gson().toJsonTree(listOfMapId).getAsJsonArray();
                efwdDetails.add("mapIds", listOfMapIdArr);
                hcr.setEfwdDetails(efwdDetails.toString());
            }
        }
    }

    private HCReport prepareHCRObject(JsonObject formJson, String uuid, String dir, HCReport hcr) {
        if (formJson.has("state") || formJson.has("diagram")) {
            String state = formJson.get("state").getAsString();
            String diagram = formJson.get("diagram").getAsString();

            String name = formJson.get("name").getAsString();
            String existingName = hcr.getName();
            hcr.setName(StringUtils.isEmpty(existingName) ? name : existingName);
            hcr.setDiagramData(diagram);
            hcr.setState(state);
            //JSONObject formData = null;
            if (formJson.has("previewFormData")) {//previewFormData
                //formData = prepareHCRPreviewFormData(formJson, uuid, dir, hcr);
                //call hcr preview with some flag to identify that it is for preview and designer.
                //need to add hcrdesigner_xml to the hcr file.
                //need to add hcrprint to the hcr file.
                /*JSONObject formData =*/
                prepareHCRPreviewFormData(formJson, uuid, dir, hcr);
               /* IHCRGenerator generator = (IHCRGenerator) ApplicationContextAccessor.getBean(JsonUtils.getHCRGeneratorType());
                new HIManagedThread(() -> generator.generateHCReport(formData)).start();*/
            }

        }
        String visible = hcr.getVisible();
        Security security = hcr.getSecurity();
        if (security == null)
            hcr.setSecurity(SecurityUtils.securityObject());
        if (visible == null)
            hcr.setVisible("true");
        return hcr;
    }

    private JsonObject prepareHCRPreviewFormData(JsonObject formJson, String uuid, String dir, HCReport hcr) {
        JsonObject formData;
        formData = JsonParser.parseString(formJson.get("previewFormData").getAsString()).getAsJsonObject();
        JsonObject saveDetails = new JsonObject();
        saveDetails.addProperty("dir", dir);
        saveDetails.addProperty("uuid", uuid);
        formData.add("saveDetails", saveDetails);
        hcr.setFormData(formData.toString());
        formData.addProperty("isFromSaveService", true);
        return formData;
    }

    private HCReport getHCRFile(String dir, String uuid, String hcrExtension) {
        HCReport hcr = ApplicationContextAccessor.getBean(HCReport.class);
        File hcrFile = new File(applicationProperties.getSolutionDirectory() + File.separator + dir + File
                .separator + uuid + "." + hcrExtension);
        if (isUpdate && FileUtils.isFilePresent(hcrFile)) {
            try {
                synchronized (this) {
                    hcr = JaxbUtils.unMarshal(HCReport.class, hcrFile);
                }
            } catch (Exception e) {
                throw new EfwServiceException("Error in saving file. The HCR couldn't be saved. " +
                        "" + "" + "Reason is ", e);
            }
        }
        return hcr;
    }

    private String saveHCRFile(String hcrExtension, HCReport hcr, String uuid, String dir) {

        File hcrFile = new File(applicationProperties.getSolutionDirectory() + File.separator + dir + File
                .separator + uuid + "." + hcrExtension);

        try {
            synchronized (this) {
                JaxbUtils.marshal(hcr, hcrFile);
            }
        } catch (Exception e) {
            throw new EfwServiceException("Error in saving file. The HCR couldn't be saved. " +
                    "" + "" + "Reason is ", e);
        }
        return uuid + "." + hcrExtension;
    }

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}
