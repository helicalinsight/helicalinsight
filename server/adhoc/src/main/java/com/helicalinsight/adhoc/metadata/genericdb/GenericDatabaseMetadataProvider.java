package com.helicalinsight.adhoc.metadata.genericdb;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.metadata.IMetadataProducer;
import com.helicalinsight.adhoc.metadata.MetadataProducerBeanFactory;
import com.helicalinsight.adhoc.metadata.jaxb.ConnectionDatabase;
import com.helicalinsight.adhoc.metadata.jaxb.Metadata;
import com.helicalinsight.adhoc.service.HIMetadataResourceServiceDB;
import com.helicalinsight.admin.model.HIResource;
import com.helicalinsight.admin.service.HIResourceServiceDB;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.framework.utils.ApplicationContextAccessor;
import com.helicalinsight.efw.utility.JaxbUtils;
import com.helicalinsight.efw.utility.JsonUtils;
import com.helicalinsight.efw.utility.TempDirectoryCleaner;



/**
 * Created by author on 27-02-2015.
 * <p/>
 * Provides metadata from either reading a file on the file system or hitting
 * the database.
 * <p/>
 * In case if the details of the metadata file such as the the name of the file
 * 'metadataFileName' and 'location' are provided in the formData json, the file
 * will be searched for and the quick report json will be prepared from that
 * file.
 * <p/>
 * In case if the file is not found in the requested location with the file name
 * then a MetadataRetrievalException will be thrown.
 * <p/>
 * Otherwise the request will be treated as an adhoc request and hence
 * connection details are expected to the database. In case of global
 * connections only type and id are expected. In case of the jdbc connections,
 * the type 'sql.jdbc' is expected and the directory of the ewfd file and the
 * connection id are also expected.
 * <p/>
 * In the later case the connection details are utilized and quick report json
 * is prepared after saving the file with uniqueId as the name and extension
 * as metadata(configurable) in the temporary location.
 * <p/>
 * After saving the file in temporary location, the quick report json is sent as
 * the response.
 *
 * @author Rajasekhar
 * @since 1.3
 */
@SuppressWarnings("unused")
public class GenericDatabaseMetadataProvider implements IMetadata {

    private static final Logger logger = LoggerFactory.getLogger(GenericDatabaseMetadataProvider.class);
    
    

    //As the instance of this object is obtained using reflection, Spring DI is not used.
    protected final AdhocViewJsonProvider adhocViewJsonProvider = new AdhocViewJsonProvider();

    @Override
    public String getMetadata(String jsonInformation) {
        long now = System.currentTimeMillis();
        long later;

        JsonObject formData = JsonParser.parseString(jsonInformation).getAsJsonObject();
        String mode = formData.has("mode") ? formData.get("mode").getAsString() : "";
        Metadata metadataFromFile = fromFile(formData);
        if (metadataFromFile != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("The metadata is found from an xml file. Returning the required information.");
            }
            if (formData.has("uniqueId")) {
                formData.remove("uniqueId");
            }

            GsonUtility.accumulate(formData,"uniqueId", FilenameUtils.getBaseName(formData.get("metadataFileName").getAsString()));

            filterMetadata(metadataFromFile,mode);
            String metaDataInString = this.adhocViewJsonProvider.adhocViewJson(metadataFromFile, formData);
            JsonObject metaDataInJson = JsonParser.parseString(metaDataInString).getAsJsonObject();
            JsonArray connectionsArray = new JsonArray();
            if (metadataFromFile.getConnections() != null) {
                List<ConnectionDatabase> connectionDatabase = metadataFromFile.getConnections().getConnectionDatabase();
                for (int i = 0; i < connectionDatabase.size(); i++) {
                    ConnectionDatabase cdm = connectionDatabase.get(i);
                    Metadata metadataNew = ApplicationContextAccessor.getBean(Metadata.class);
                    metadataNew.setConnectionDetails(cdm.getConnectionDetails());
                    metadataNew.setDatabase(cdm.getDatabase());
                    filterMetadata(metadataNew,mode);

                    String response = this.adhocViewJsonProvider.adhocViewJson(metadataNew, formData);
                    JsonObject sampleItem = JsonParser.parseString(response).getAsJsonObject();
                    sampleItem.remove("joins");
                    connectionsArray.add(sampleItem);
                }
                metaDataInJson.add("connections", connectionsArray);
            }
            //metaDataInJson.accumulate("crossJoins",new MetadataJoinsProvider(metadataFromFile).getMetadataCrossJoins());
            return metaDataInJson.toString();
        }

        String classifier = formData.get("classifier").getAsString();
        IMetadataProducer metadataProducer = MetadataProducerBeanFactory.getMetadataProducer(classifier);
        Metadata metadata = metadataProducer.prepareMetadata(new Gson().fromJson(formData.toString(),JsonObject.class));

        if (metadata == null) {
            throw new MetadataRetrievalException("Couldn't retrieve database metadata");
        }

        String uniqueId = UUID.randomUUID().toString();
        File file = getSaveLocation(uniqueId);

        synchronized (this) {
            JaxbUtils.marshal(metadata, file);
        }

        GsonUtility.accumulate(formData,"uniqueId", uniqueId);
        filterMetadata(metadata,mode);
        return this.adhocViewJsonProvider.adhocViewJson(metadata, formData);
    }

    @Nullable
    private Metadata fromFile(@NotNull JsonObject formDataJson) {
        if (formDataJson.has("location") && formDataJson.has("metadataFileName")) {
            String metadataFileName = formDataJson.get("metadataFileName").getAsString();
            String location = formDataJson.get("location").getAsString();
            HIResourceServiceDB serviceDB = ApplicationContextAccessor.getBean(HIResourceServiceDB.class);
            HIMetadataResourceServiceDB metadataService = ApplicationContextAccessor.getBean(HIMetadataResourceServiceDB.class);
            HIResource metadataResource = serviceDB.getResourceByUrl(location + "/" + metadataFileName);
            if(metadataResource==null){
                throw new MetadataServiceException("The Resource does not exists");
            }
            Metadata databaseMetadata = metadataService.getHIResourceMetadataByResourceId(metadataResource.getResourceId());

            if (databaseMetadata != null) {
                return databaseMetadata;
            } else {
                throw new MetadataRetrievalException(String.format("The file %s couldn't be found in the " +
                        "specified location %s", metadataFileName, location));
            }
        }
        return null;
    }

    private void filterMetadata(Metadata metadataFromFile,String mode) {
    	if(!StringUtils.isBlank(mode) && "edit".equalsIgnoreCase(mode)) {
    		return ;
    	}
		FilterMetadata filterMetadata = new FilterMetadata(metadataFromFile);
		filterMetadata.filter();
    }

    @NotNull
    private File getSaveLocation(String uniqueId) {
        File tempDirectory = TempDirectoryCleaner.getTempDirectory();

        String metadataExtension = JsonUtils.getMetadataExtension();
        return new File(tempDirectory.getAbsolutePath() + File.separator + uniqueId + "." + metadataExtension);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}