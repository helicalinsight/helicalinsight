package com.helicalinsight.adhoc.genericsql;

import org.jetbrains.annotations.NotNull;

/**
 * A utility class representing a query generator with its implementation class and classifier.
 * This class encapsulates information about a query generator used in the ad-hoc reporting system.
 * It provides methods to retrieve the implementation class and classifier of the query generator.
 * 
 * Created by author on 02-03-2015.
 * @author Muqtar Ahmed
 * @author Rajasekhar
 */
public class QueryGeneratorUtility {

    private final String implementationClazz;

    private final String classifier;
    /**
     * Constructs a new QueryGeneratorUtility with the specified implementation class and classifier.
     *
     * @param implementationClazz 		 fully qualified name of the implementation class of the query generator.
     * @param classifier          		 classifier of the query generator.
     */
    public QueryGeneratorUtility(String implementationClazz, String classifier) {
        this.implementationClazz = implementationClazz;
        this.classifier = classifier;
    }
    /**
     * Retrieves the implementation class of the query generator.
     * @return the fully qualified name of the implementation class.
     */
    public String getImplementationClazz() {
        return implementationClazz;
    }
    /**
     * Retrieves the classifier of the query generator.
     * @return the classifier of the query generator.
     */
    public String getClassifier() {
        return classifier;
    }

    @NotNull
    @Override
    public String toString() {
        return "QueryGeneratorUtility{" +
                "implementationClazz='" + implementationClazz + '\'' +
                ", classifier='" + classifier + '\'' +
                '}';
    }
}
