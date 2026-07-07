package com.helicalinsight.adhoc.report;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.utils.DashboardUtils;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceMapping;
import com.helicalinsight.admin.model.ResourceType;
import com.helicalinsight.admin.service.HIResourceConstituentMappingService;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.admin.service.ResourceTypeServiceDB;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.JsonUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The HCReportReader class implements the {@link IComponent} interface and is responsible for reading
 * HCReports from the system.
 */
public class HCReportReader implements IComponent {

    private final HIResourceServiceDB serviceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);

    /**
     * The executeComponent method reads the specified HCReport and retrieves its details.
     *
     * @param jsonFormData JSON string containing form data. formData provides dir and filename.
     * @return A JSON string representing the details of the HCReport, including its UUID, name,
     * directory, state, diagram data, and preview form data.
     * @throws IllegalArgumentException If the specified file does not exist.
     */
    @Override
    public String executeComponent(String jsonFormData) {

        JsonObject formJson = JsonParser.parseString(jsonFormData).getAsJsonObject();
        String directory = formJson.get("dir").getAsString();
        String fileName = formJson.get("file").getAsString();
        JsonObject responseJson = new JsonObject();
        HIResourceConstituentMappingService mappingService = ApplicationContextAccessor.getBean(HIResourceConstituentMappingService.class);
        ResourceTypeServiceDB resourceTypeService = ApplicationContextAccessor.getBean(ResourceTypeServiceDB.class);

        HIResource hiResource = serviceDB.getResourceByUrl(directory + "/" + fileName);
        if (hiResource == null) {
            throw new IllegalArgumentException("The file " + fileName + " doesn't exists. " +
                    "Aborting operation.");
        }

        JsonObject fileDetails = new JsonObject();
        fileDetails.addProperty("uuid", fileName);
        fileDetails.addProperty("name", hiResource.getTitle());
        fileDetails.addProperty("dir", directory);
        responseJson.add("file", fileDetails);
        responseJson.add("state", JsonParser.parseString(hiResource.getHiResourceHCR().getState()));
        String diagramDataString = hiResource.getHiResourceHCR().getDiagram();
        String previewFormData = hiResource.getHiResourceHCR().getPreviewFormData();

        if (previewFormData.contains("imageResourceId")) {

            List<String> jsonStringFromLargeJson = DashboardUtils.getJsonArrayStringFromLargeJson("image", previewFormData);
            List<String> replaceImageId = new ArrayList<>();
            Integer hcrId = hiResource.getResourceId();
            
            ResourceType resouceType =  resourceTypeService.getResourceTypeByTypeAndExtension("image", "." + JsonUtils.getImageExtension());
            
            List<HIResourceMapping> existingImage = mappingService.findMappingsByParentIdAndChildType(hcrId,resouceType.getResourceTypeId());

            Map<Integer, String> imageIdPath = new HashMap<>();
            for (HIResourceMapping mp : existingImage) {
                HIResource newIds = serviceDB.getResourceByIdIgnoreFilter(mp.getChildResource().getResourceId());
                String resourceURL = newIds.getResourceURL();
                imageIdPath.put(newIds.getResourceId(), resourceURL);
            }
            Map<String,String> oldPathNewPath = new HashMap<>();

            for (String imageArray : jsonStringFromLargeJson) {
                JsonElement element = JsonParser.parseString(imageArray);
                if (element.isJsonArray()) {
                    JsonArray jsonArray = element.getAsJsonArray();
                    for (JsonElement elem : jsonArray) {
                        JsonObject elemnt = elem.getAsJsonObject();
                        if(elemnt.has("imageResourceId")){
                        Integer imageResourceId = elemnt.get("imageResourceId").getAsInt();

                        String urlOfImage = imageIdPath.get(imageResourceId);
                        if (urlOfImage != null) {
                            Path path = Paths.get(urlOfImage);
                            Path parentDir = path.getParent();
                            Path fileImage = path.getFileName();
                            String dir = elemnt.get("dir").getAsString();
                            String file = elemnt.get("file").getAsString();
                            oldPathNewPath.put(dir+"/"+file,urlOfImage);
                            elemnt.addProperty("dir", parentDir.toString());
                            elemnt.addProperty("file", fileImage.toString());
                            elemnt.addProperty("imageResourceId", imageResourceId);
                        }

                        }

                    }
                    replaceImageId.add(element.toString());
                }
            }

            int counter = 0;
            for (String imageArray : jsonStringFromLargeJson) {
                previewFormData = previewFormData.replace(imageArray, replaceImageId.get(counter));
                counter++;
            }

        for(String key:oldPathNewPath.keySet()){
            diagramDataString = diagramDataString.replace(key,oldPathNewPath.get(key));
        }
        }
        responseJson.add("diagramData", JsonParser.parseString(diagramDataString));
        responseJson.add("previewFormData", JsonParser.parseString(previewFormData));
        return responseJson.toString();
    }

    @Override
    public boolean isThreadSafeToCache() {
        return false;
    }

}
