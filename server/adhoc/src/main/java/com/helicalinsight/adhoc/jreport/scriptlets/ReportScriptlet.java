package com.helicalinsight.adhoc.jreport.scriptlets;

import net.sf.jasperreports.engine.JRDefaultScriptlet;

/**
 * This class extends {@link JRDefaultScriptlet} and provides additional functionalities
 * specific to scriptlets used in JasperReports.
 * It includes methods to convert an integer to its English words representation and a
 * placeholder method for a function name.
 * Created by author on 12/9/2019.
 *
 * @author Rajesh
 */
public class ReportScriptlet extends JRDefaultScriptlet {
    public static String convertIntToWords(long longValue) {
        return ScriptletUtils.englishNumberToWords(longValue);
    }

    public static String functionName() {
        return null;
    }
}
