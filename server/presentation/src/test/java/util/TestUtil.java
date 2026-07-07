package util;

import com.helicalinsight.adhoc.genericsql.AdhocUtils;
import com.helicalinsight.adhoc.genericsql.EnumTypeFinder;
import com.helicalinsight.adhoc.genericsql.JavaTypes;

import java.util.List;

/**
 * Created by helical019 on 3/18/2020.
 */
public class TestUtil {


    private String getQuotedValues(Boolean isCustomValue, String dataType, List<String> values) {
        boolean quotesRequired = isCustomValue ? false : areQuotesRequired(dataType);

        return addValues(quotesRequired, values);
    }

    private String getSanitizedValue(Object object) {
        String str = String.valueOf(object);
        if (str.contains("'")) {
            return str.replaceAll("'", "\\'");
        }
        return str;
    }

    private String addValues(boolean quotesRequired, List<String> values) {
        if (values == null) {
            return "";
        }
        if (values.size() == 1) {
            if (quotesRequired) {
                return AdhocUtils.singleQuotes(getSanitizedValue(values.get(0)));
            }
            return values.get(0) + "";
        }

        String withQuotes = "";
        for (Object object : values) {
            if (quotesRequired) {
                withQuotes = withQuotes + AdhocUtils.singleQuotes(getSanitizedValue(object)) + ", ";
            } else {
                withQuotes = withQuotes + object + ", ";
            }
        }
        //Remove space and comma at the end
        if (withQuotes.length() >= 2) {
            return withQuotes.substring(0, withQuotes.length() - 2);
        } else {
            return withQuotes;
        }
    }

    private boolean areQuotesRequired(String dataType) {
        JavaTypes enumType = EnumTypeFinder.findEnumType(dataType);
        boolean isDateTime = JavaTypes.DATETIME.equals(enumType) || JavaTypes.DATE.equals(enumType) || JavaTypes.TIME
                .equals(enumType);
        boolean isString = false;
        if (!isDateTime) {
            isString = JavaTypes.STRING.equals(enumType);
        }
        return isDateTime || isString;
    }
}
