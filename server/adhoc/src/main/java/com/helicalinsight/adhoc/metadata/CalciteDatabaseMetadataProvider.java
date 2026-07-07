package com.helicalinsight.adhoc.metadata;

import com.helicalinsight.adhoc.metadata.genericdb.GenericDatabaseMetadataProvider;

/**
 * Created by user on 5/12/2016.
 *
 * @author Rajasekhar
 */
@SuppressWarnings("UnusedDeclaration")
public class CalciteDatabaseMetadataProvider extends GenericDatabaseMetadataProvider {

    //As of now only Metadata Producer varies. So, delegate to the parent
    @Override
    public String getMetadata(String jsonInformation) {
        return super.getMetadata(jsonInformation);
    }
}
