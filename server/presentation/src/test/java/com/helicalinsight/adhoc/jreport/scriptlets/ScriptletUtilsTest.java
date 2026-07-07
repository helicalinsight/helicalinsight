package com.helicalinsight.adhoc.jreport.scriptlets;

import org.junit.Test;
import static org.junit.Assert.*;

public class ScriptletUtilsTest {

    @Test
    public void testEnglishNumberToWords() {
        assertEquals("zero", ScriptletUtils.englishNumberToWords(0));
        assertEquals("one", ScriptletUtils.englishNumberToWords(1));
        assertEquals("sixteen", ScriptletUtils.englishNumberToWords(16));
        assertEquals("one hundred", ScriptletUtils.englishNumberToWords(100));
        assertEquals("one hundred eighteen", ScriptletUtils.englishNumberToWords(118));
        assertEquals("two hundred", ScriptletUtils.englishNumberToWords(200));
        assertEquals("two hundred nineteen", ScriptletUtils.englishNumberToWords(219));
        assertEquals("eight hundred", ScriptletUtils.englishNumberToWords(800));
        assertEquals("eight hundred one", ScriptletUtils.englishNumberToWords(801));
        assertEquals("one thousand three hundred sixteen", ScriptletUtils.englishNumberToWords(1316));
        assertEquals("one million ", ScriptletUtils.englishNumberToWords(1000000));
        assertEquals("two million ", ScriptletUtils.englishNumberToWords(2000000));
        assertEquals("three million two hundred", ScriptletUtils.englishNumberToWords(3000200));
        assertEquals("seven hundred thousand ", ScriptletUtils.englishNumberToWords(700000));
        assertEquals("nine million ", ScriptletUtils.englishNumberToWords(9000000));
        assertEquals("nine million one thousand ", ScriptletUtils.englishNumberToWords(9001000));
        assertEquals("one hundred twenty three million four hundred fifty six thousand seven hundred eighty nine",
                ScriptletUtils.englishNumberToWords(123456789));
        assertEquals("two billion one hundred forty seven million four hundred eighty three thousand six hundred forty seven",
                ScriptletUtils.englishNumberToWords(2147483647));
        assertEquals("one billion ten", ScriptletUtils.englishNumberToWords(1000000010L));
        assertEquals("seven billion one hundred twenty three million four hundred fifty six thousand seven hundred eighty nine",
                ScriptletUtils.englishNumberToWords(7123456789L));
    }
}
