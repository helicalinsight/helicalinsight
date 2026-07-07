package com.helicalinsight.efw.resourcereader;

import com.google.gson.JsonObject;
import com.helicalinsight.efw.exceptions.ApplicationException;
import com.helicalinsight.efw.exceptions.ImproperXMLConfigurationException;
import com.helicalinsight.efw.exceptions.UnSupportedRuleImplementationException;
import net.sf.json.JSONObject;

/**
 * This interface has been changed to to be able to accept multiple extensions
 * and multiple xsd file types. Earlier only one extension and xsd were used.
 * <p>
 * Its implementations XMLResourceReader and JSONResourceReader have also been
 * changed accordingly.
 *
 * @author Muqtar Ahmed
 * @author Rajasekhar
 * @author Avi
 */

public interface IResourceReader {

    public String getResources() throws ApplicationException, UnSupportedRuleImplementationException,
            ImproperXMLConfigurationException;

    public String getPath();

    public void setPath(String path);

    public JsonObject getVisibleExtensions();

    public void setVisibleExtensions(JsonObject visibleExtensions);

    public boolean getDiscardEmptyFolders();

    public void setDiscardEmptyFolders(boolean discardEmptyFolders);
}
