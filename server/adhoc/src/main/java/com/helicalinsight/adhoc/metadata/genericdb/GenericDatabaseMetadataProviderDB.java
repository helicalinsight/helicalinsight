/*


package com.helicalinsight.adhoc.metadata.genericdb;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helicalinsight.adhoc.MultiConnectionMergeAdhocTable;
import com.helicalinsight.adhoc.MultiConnectionMergeAdhocTableDB;
import com.helicalinsight.adhoc.metadata.IMetadataProducer;
import com.helicalinsight.adhoc.metadata.MetadataProducerBeanFactory;
import com.helicalinsight.adhoc.metadata.jaxb.ConnectionDatabase;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.admin.model.*;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;
import com.helicalinsight.resourcedb.ResourceConstants;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;



import com.helicalinsight.adhoc.MultiConnectionMergeAdhocTable;
import com.helicalinsight.adhoc.metadata.genericdb.MetadataRetrievalException;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.model.HIResourceMetadata;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.resourcedb.ResourceConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

import java.util.Map;






@SuppressWarnings("unused")
public class GenericDatabaseMetadataProviderDB implements IMetadata {

    private static final Logger logger = LoggerFactory.getLogger(GenericDatabaseMetadataProviderDB.class);

    //As the instance of this object is obtained using reflection, Spring DI is not used.
    protected final AdhocViewJsonProvider adhocViewJsonProvider = new AdhocViewJsonProvider();

    @Override
    public String getMetadata(String jsonInformation) {
        long now = System.currentTimeMillis();
        long later;

        JSONObject formData = JSONObject.fromObject(jsonInformation);
        Metadata metadataFromFile = fromFile(formData);
        if (metadataFromFile != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("The metadata is found from an xml file. Returning the required information.");
            }
            if (formData.has("uniqueId")) {
                formData.discard("uniqueId");
            }

            formData.accumulate("uniqueId", FilenameUtils.getBaseName(formData.getString("metadataFileName")));

            filterMetadata(metadataFromFile);
            String metaDataInString = this.adhocViewJsonProvider.adhocViewJson(metadataFromFile, formData);
            JSONObject metaDataInJson = JSONObject.fromObject(metaDataInString);
            JSONArray connectionsArray = new JSONArray();
            if (metadataFromFile.getConnections() != null) {
                List<ConnectionDatabase> connectionDatabase = metadataFromFile.getConnections().getConnectionDatabase();
                for (int i = 0; i < connectionDatabase.size(); i++) {
                    ConnectionDatabase cdm = connectionDatabase.get(i);
                    Metadata metadataNew = ApplicationContextAccessor.getBean(Metadata.class);
                    metadataNew.setConnectionDetails(cdm.getConnectionDetails());
                    metadataNew.setDatabase(cdm.getDatabase());
                    filterMetadata(metadataNew);

                    String response = this.adhocViewJsonProvider.adhocViewJson(metadataNew, formData);
                    JSONObject sampleItem = JSONObject.fromObject(response);
                    sampleItem.put("connectionDatabaseId",cdm.getConnectionDatabaseID());
                    connectionsArray.add(sampleItem);
                }
                metaDataInJson.put("connections", connectionsArray);
            }
            return metaDataInJson.toString();
        }

        String classifier = formData.getString("classifier");
        IMetadataProducer metadataProducer = MetadataProducerBeanFactory.getMetadataProducer(classifier);
        Metadata metadata = metadataProducer.prepareMetadata(formData);

        if (metadata == null) {
            throw new MetadataRetrievalException("Couldn't retrieve database metadata");
        }

        String uniqueId = UUID.randomUUID().toString();
        File file = getSaveLocation(uniqueId);

        synchronized (this) {
            JaxbUtils.marshal(metadata, file);
        }

        formData.accumulate("uniqueId", uniqueId);
        filterMetadata(metadata);
        return this.adhocViewJsonProvider.adhocViewJson(metadata, formData);
    }

    @Nullable
    private Metadata fromFile(@NotNull JSONObject formDataJson) {
        if (formDataJson.has("location") && formDataJson.has("metadataFileName")) {
            String metadataFileName = formDataJson.getString("metadataFileName");
            String location = formDataJson.getString("location");

            File metadataFile = new File(ApplicationProperties.getInstance().getSolutionDirectory() + File.separator
                    + location + File.separator + metadataFileName);

            if (metadataFile.exists()) {
                Metadata metadata = JaxbUtils.unMarshal(Metadata.class, metadataFile);
                MultiConnectionMergeAdhocTable mc = new MultiConnectionMergeAdhocTable(metadata);
                mc.merge();
                return metadata;
            } else {
                throw new MetadataRetrievalException(String.format("The file %s couldn't be found in the " +
                        "specified location %s", metadataFileName, location));
            }
        }
        return null;
    }

    @Nullable
    private HIMetadataDTO fromDB(@NotNull Map<String,Object> formDataMap) {
        if (formDataMap.containsKey("location") && formDataMap.containsKey("metadataFileName")) {
            String metadataFileName = ""+formDataMap.get("metadataFileName");
            String location = ""+formDataMap.get("location");



            HIResourceServiceDB hiResourceServiceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
            String resourcePath =  location + ResourceConstants.FILE_SEPERATOR + metadataFileName;
            HIResource resourceByPath = hiResourceServiceDB.getResourceByPath(resourcePath);

            if (null!=resourceByPath) {
                //TODO need some clarification
                HIMetadataDTO resourceMetadata = hiResourceServiceDB.getHIResourceMetadataByResourceId(resourceByPath.getResourceId());
                MultiConnectionMergeAdhocTableDB mc = new MultiConnectionMergeAdhocTableDB(resourceMetadata);
                mc.merge();
               // return metadata;
                return resourceMetadata;
            } else {
                throw new MetadataRetrievalException(String.format("The file %s couldn't be found in the " +
                        "specified location %s", metadataFileName, location));
            }
        }
        return null;
    }

    //TODO Need Clarification
private void filterMetadata(Metadata metadataFromFile) {
        FilterMetadata filterMetadata = new FilterMetadata(metadataFromFile);
        filterMetadata.filter();
    }



@NotNull
    private File getSaveLocation(String uniqueId) {
        File tempDirectory = TempDirectoryCleaner.getTempDirectory();

        String metadataExtension = JsonUtils.getMetadataExtension();
        return new File(tempDirectory.getAbsolutePath() + File.separator + uniqueId + "." + metadataExtension);
    }




    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public String getMetadataDB(Map<String, Object> jsonInformation) {
        long now = System.currentTimeMillis();
        long later;

        HIMetadataDTO metadataFromDB = fromDB(jsonInformation);
        if (metadataFromDB != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("The metadata is found . Returning the required information.");
            }


jsonInformation.put("uniqueId", FilenameUtils.getBaseName(""+jsonInformation.get("metadataFileName")));


          //  filterMetadata(metadataFromFile);
          //  String metaDataInString = this.adhocViewJsonProvider.adhocViewJson(metadataFromFile, jsonInformation);
            JSONArray connectionsArray = new JSONArray();
            List<HIMetadataConnections> hiMetadataConnections = metadataFromDB.getHiMetadata().getHiMetadataConnections();
            if (hiMetadataConnections!=null && hiMetadataConnections.size()>0) {
                List<ConnectionDatabase> connectionDatabase = metadataFromDB.getHiMetadataConnections();
                for (int i = 0; i < connectionDatabase.size(); i++) {
                    ConnectionDatabase cdm = connectionDatabase.get(i);
                    Metadata metadataNew = ApplicationContextAccessor.getBean(Metadata.class);
                    metadataNew.setConnectionDetails(cdm.getConnectionDetails());
                    metadataNew.setDatabase(cdm.getDatabase());
                    filterMetadata(metadataNew);


                    String response = this.adhocViewJsonProvider.adhocViewJson(metadataNew, formData);
                    JSONObject sampleItem = JSONObject.fromObject(response);
                    sampleItem.put("connectionDatabaseId",cdm.getConnectionDatabaseID());
                    connectionsArray.add(sampleItem);
                }
                metaDataInJson.put("connections", connectionsArray);
            }
            return metaDataInJson.toString();
        }

        String classifier = formData.getString("classifier");
        IMetadataProducer metadataProducer = MetadataProducerBeanFactory.getMetadataProducer(classifier);
        Metadata metadata = metadataProducer.prepareMetadata(formData);

        if (metadata == null) {
            throw new MetadataRetrievalException("Couldn't retrieve database metadata");
        }

        String uniqueId = UUID.randomUUID().toString();
        File file = getSaveLocation(uniqueId);

        synchronized (this) {
            JaxbUtils.marshal(metadata, file);
        }

        formData.accumulate("uniqueId", uniqueId);
        filterMetadata(metadata);
        return this.adhocViewJsonProvider.adhocViewJson(metadata, formData);
    }
}
*/
