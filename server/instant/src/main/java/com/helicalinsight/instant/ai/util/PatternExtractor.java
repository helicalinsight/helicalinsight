package com.helicalinsight.instant.ai.util;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PatternExtractor {

    private PatternExtractor() {
    }

    @NotNull
    public static String extractFromPattern(String originalString, String patternString) {
        Pattern pattern = Pattern.compile(patternString, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(originalString);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return originalString;
    }
}
