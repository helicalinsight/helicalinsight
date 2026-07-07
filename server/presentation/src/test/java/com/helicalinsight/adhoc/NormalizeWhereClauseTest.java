package com.helicalinsight.adhoc;


import org.junit.Test;
import org.junit.Assert;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptableObject;

public class NormalizeWhereClauseTest {

    @Test
    public void replaceFalseOutsideQuotes() {
        assertEquals(
            "WHERE flag = 'false'",
            normalizeWhereClause("WHERE flag = false")
        );
    }

    @Test
    public void doNotReplaceFalseInsideSingleQuotes() {
        assertEquals(
            "WHERE msg = 'this is false data'",
            normalizeWhereClause("WHERE msg = 'this is false data'")
        );
    }

    @Test
    public void doNotReplaceFalseInsideDoubleQuotes() {
        assertEquals(
            "WHERE msg = \"this is false data\"",
            normalizeWhereClause("WHERE msg = \"this is false data\"")
        );
    }

    @Test
    public void replaceTrueAndFalseMixed() {
        assertEquals(
            "WHERE a = 'true' AND b = 'false'",
            normalizeWhereClause("WHERE a = true AND b = false")
        );
    }

    @Test
    public void replaceNullButSkipQuotedNull() {
        assertEquals(
            "WHERE x = null AND y = 'null' AND z = null",
            normalizeWhereClause("WHERE x = null AND y = 'null' AND z = \"null\"")
        );
    }

    @Test
    public void skipFalseInsideJsonString() {
        assertEquals(
            "WHERE meta = '{\"flag\": false}'",
            normalizeWhereClause("WHERE meta = '{\"flag\": false}'")
        );
    }

    @Test
    public void doNotReplaceSubstringFalsepositive() {
        assertEquals(
            "WHERE type = 'falsepositive'",
            normalizeWhereClause("WHERE type = 'falsepositive'")
        );
    }

    @Test
    public void complexSQL() {
        assertEquals(
            "WHERE (a = 'false' OR b = 'true') AND c IS null",
            normalizeWhereClause("WHERE (a = false OR b = true) AND c IS NULL")
        );
    }

    @Test
    public void sqlServerQueryWithSchema() {
        String input =
            "select top 10 [db].[dbo].[Users].[fathername] as [fathername], " +
            "       [db].[dbo].[Users].[firstname] as [firstname] " +
            "from   [db].[dbo].[Users] " +
            "where  ( [db].[dbo].[Users].[firstname] IN ( 'falsepositive') ) " +
            "group by [db].[dbo].[Users].[fathername], [db].[dbo].[Users].[firstname]";
        String expected = input;

        assertEquals(expected, normalizeWhereClause(input));
    }

    @Test
    public void multipleTokensBackToBack() {
        assertEquals(
            "WHERE flag='true','false','true',null",
            normalizeWhereClause("WHERE flag=true,false,true,null")
        );
    }

    @Test
    public void nestedFunctions() {
        assertEquals(
            "WHERE COALESCE(flag, 'false') = 'true'",
            normalizeWhereClause("WHERE COALESCE(flag, false) = true")
        );
    }

    @Test
    public void fixFalseDoubleQuotes() {
        assertEquals(
            "WHERE aa = 'false'",
            normalizeWhereClause("WHERE aa = ''false''")
        );
    }

    @Test
    public void fixTrueDoubleQuotes() {
        assertEquals(
            "WHERE aa = 'true'",
            normalizeWhereClause("WHERE aa = ''true''")
        );
    }

    @Test
    public void doNotFixFalseInsideString() {
        assertEquals(
            "WHERE txt = 'weird false inside string'",
            normalizeWhereClause("WHERE txt = 'weird ''false'' inside string'")
        );
    }

    @Test
    public void fixTripleQuotedFalse() {
        assertEquals(
            "WHERE val = 'false'",
            normalizeWhereClause("WHERE val = '''false'''")
        );
    }

    @Test
    public void fixBooleanNextToOperators() {
        assertEquals(
            "WHERE x='false' AND y='true'",
            normalizeWhereClause("WHERE x=false AND y=true")
        );
    }

    @Test
    public void fixBooleanInsideParentheses() {
        assertEquals(
            "WHERE ((flag= 'false')) OR((flag= 'true'))",
            normalizeWhereClause("WHERE ((flag= false)) OR((flag= true))")
        );
    }

    @Test
    public void replaceBareFalseNotQuotedFalse() {
        assertEquals(
            "WHERE a='false' AND b=\"false\"",
            normalizeWhereClause("WHERE a=false AND b=\"false\"")
        );
    }

    @Test
    public void fixDoubleQuotedTokensInINClause() {
        assertEquals(
            "WHERE status IN ('true', 'false', 'true', 'false')",
            normalizeWhereClause("WHERE status IN (true, false, ''true'', ''false'')")
        );
    }

    @Test
    public void malformedTokensNextToCommas() {
        assertEquals(
            "WHERE f ='false', t ='true', x='false'",
            normalizeWhereClause("WHERE f =false, t =true, x=''false''")
        );
    }

    @Test
    public void fixOddWrapping() {
        assertEquals(
            "WHERE x = ('false')",
            normalizeWhereClause("WHERE x = (''false'')")
        );
    }
    private static final String JS_SNIPPET = """
        function normalizeWhereClause(whereClause) {
        whereClause = whereClause.replaceAll("''false''", "false");
                whereClause = whereClause.replaceAll("''true''", "true");
                whereClause = whereClause.replaceAll('"null"', "null");

            whereClause = whereClause.replace(
                /'[^']*'|"(?:[^"]*)"|\\bfalse\\b|\\btrue\\b|\\bnull\\b/gi,
                function (match) {

                    // If it's a quoted string → return unchanged
                    if (match.charAt(0) === "'" || match.charAt(0) === '"') {
                        return match;
                    }

                    // Normalize standalone words
                    var lower = match.toLowerCase();

                    if (lower === "false") return "'false'";
                    if (lower === "true")  return "'true'";
                    if (lower === "null")  return "null";

                    return match;
                }
            );

            return whereClause;
        }
    """;



    // Your normalizeWhereClause mock (delete this in real setup)
    public static String normalizeWhereClause(String sql) {
        Context context = Context.enter();
        ScriptableObject scope = context.initStandardObjects();
        context.evaluateString(scope, JS_SNIPPET, "inline-js", 1, null);
        Function function = (Function) scope.get("normalizeWhereClause", scope);
        Object result = function.call(context, scope, scope, new Object[]{sql});
        return Context.toString(result);


    }
    private  static void assertEquals(String left, String right){
       Boolean b= left.equals(right);
        Assert.assertTrue(b);

    }
}
