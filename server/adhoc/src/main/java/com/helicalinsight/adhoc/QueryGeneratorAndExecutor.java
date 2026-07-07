package com.helicalinsight.adhoc;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helicalinsight.adhoc.services.QueryGeneratorService;
import com.helicalinsight.admin.service.ComponentFactory;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.serviceframework.IComponent;
import org.jetbrains.annotations.NotNull;

import static com.helicalinsight.efw.serviceframework.ServiceUtils.componentClass;
import static com.helicalinsight.efw.serviceframework.ServiceUtils.componentJson;

/**
 * The `QueryGeneratorAndExecutor` class is responsible for generating and executing queries it implements {@link IComponent} , It provides functionality to handle query generation and execution based on the provided form data.
 * If the form data contains a specific query.
 *
 * Created by author on 09-04-2015.
 * @author Rajasekhar
 */
@SuppressWarnings("unused")
public class QueryGeneratorAndExecutor implements IComponent {
	/**
     * Executes the query generation and execution based on the provided form data. If the form data contains a
     * specific query, it is executed directly; otherwise, it dynamically generates the query using the
     * `QueryGeneratorService` and executes it using the `DatabaseQueryExecutor`.
     *
     * @param jsonFormData The JSON-formatted form data containing necessary parameters for query execution.
     * @return The result of the query execution, which includes metadata and data.
     * @throws EfwServiceException If the provided query is null or empty.
     */
    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formData = JsonParser.parseString(jsonFormData).getAsJsonObject();
        String query;
        if (formData.has("query")) {
            query = formData.get("query").getAsString();
            if (query == null || "".equals(query) || query.trim().length() == 0) {
                throw new EfwServiceException("The provided query is null or empty.");
            }
        } else {
            QueryGeneratorService queryGeneratorService = new QueryGeneratorService();
            JsonObject requestParameterJson = queryGeneratorService.newFindConfigurationSettings(jsonFormData);
            String componentClass = componentClass(componentJson("adhoc", "report", "generateQuery", requestParameterJson.get("classifier").getAsString()));
            IComponent databaseQueryGenerator = ComponentFactory.getComponentInstance(componentClass);
            query = JsonParser.parseString(databaseQueryGenerator.executeComponent(formData.toString())).getAsJsonObject().get
                    ("query").getAsString();
        }

        String emptyColumnSelection = "No Columns Selected";
        if (emptyColumnSelection.equals(query)) {
            return emptyColumnSelection;
        }

        if (!formData.has("query")) {
            GsonUtility.accumulate(formData,"query", query);
        }

        DatabaseQueryExecutor databaseQueryExecutor = new DatabaseQueryExecutor();
        String result = databaseQueryExecutor.executeComponent(formData.toString());

        boolean hasVf = formData.has("vf");
        boolean provideQuery = formData.has("provideQuery");
        if ((provideQuery && "true".equalsIgnoreCase(formData.get("provideQuery").getAsString())) || hasVf) {
            JsonObject model;
            model = new JsonObject();
            JsonObject jsonResult = JsonParser.parseString(result).getAsJsonObject();
            GsonUtility.accumulate(model,"metadata", jsonResult.getAsJsonArray("metadata"));
            GsonUtility.accumulate(model,"data", jsonResult.getAsJsonArray("data"));
            if (provideQuery) {
                GsonUtility.accumulate(model,"query", query);
            }

            if (hasVf) {
                GsonUtility.accumulate(model,"script", getScript(formData));
            }
            return model.toString();
        }

        return result;
    }

    private String getScript(@NotNull JsonObject formData) {
        VisualizationComponent component = new VisualizationComponent();
        JsonObject scriptJson = JsonParser.parseString(component.executeComponent(formData.toString())).getAsJsonObject();
        return scriptJson.get("script").getAsString();
    }
    /**
     * Checks if the class is thread-safe to be cached. Always returns true for this class.
     * @return Always returns {@code true} as this class is designed to be thread-safe for caching.
     */
    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }
}