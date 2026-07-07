package com.helicalinsight.export;

import java.util.HashMap;
import java.util.Map;

public class PaperSizes {

    // Data structure storing width & height in inches
    private static final Map<String, double[]> SIZES = new HashMap<>();

    static {
        // North-American
        SIZES.put("Letter", new double[]{8.5, 11});
        SIZES.put("Legal", new double[]{8.5, 14});
        SIZES.put("Tabloid", new double[]{11, 17});
        SIZES.put("Statement", new double[]{5.5, 8.5});
        SIZES.put("Executive", new double[]{7.25, 10.5});
        SIZES.put("C size sheet", new double[]{17, 22});
        SIZES.put("D size sheet", new double[]{22, 34});
        SIZES.put("E size sheet", new double[]{34, 44});
        SIZES.put("English 14x 17", new double[]{14, 17});

        // ISO A-series
        SIZES.put("A0", new double[]{33.11, 46.81});
        SIZES.put("A1", new double[]{23.39, 33.11});
        SIZES.put("A2", new double[]{16.54, 23.39});
        SIZES.put("A3", new double[]{11.69, 16.54});
        SIZES.put("A4", new double[]{8.27, 11.69});
        SIZES.put("A5", new double[]{5.83, 8.27});
        SIZES.put("A6", new double[]{4.13, 5.83});

        // ISO B-series / JIS
        SIZES.put("B4 (JIS)", new double[]{10.12, 14.33});
        SIZES.put("B5 (JIS)", new double[]{7.17, 10.12});
        SIZES.put("Envelope B5", new double[]{6.93, 9.84});

        // International Envelopes
        SIZES.put("Envelope #9", new double[]{3.875, 8.875});
        SIZES.put("Envelope #10", new double[]{4.125, 9.5});
        SIZES.put("Envelope DL", new double[]{4.33, 8.66});
        SIZES.put("Envelope C5", new double[]{6.38, 9.02});
        SIZES.put("Envelope C4", new double[]{9.02, 12.76});
        SIZES.put("Envelope Monarch", new double[]{3.875, 7.5});

        // Japanese formats
        SIZES.put("Japanese Postcard", new double[]{3.94, 5.83});
        SIZES.put("Japanese Envelope Kaku #2", new double[]{9.06, 12.21});
        SIZES.put("Japanese Envelope Chou #3", new double[]{4.72, 9.25});
        SIZES.put("Japanese Envelope Chou #4", new double[]{3.54, 8.07});
        SIZES.put("Japan Envelope You #4", new double[]{4.13, 5.83});
        SIZES.put("Japan Chou 40 Envelope", new double[]{3.54, 8.86});

        // Architectural / ANSI
        SIZES.put("Architecture ASheet", new double[]{9, 12});
        SIZES.put("Architecture BSheet", new double[]{12, 18});
        SIZES.put("Architecture CSheet", new double[]{18, 24});
        SIZES.put("Architecture DSheet", new double[]{24, 36});
        SIZES.put("Architecture ESheet", new double[]{36, 48});
        SIZES.put("Architecture E1Sheet", new double[]{30, 42});
        SIZES.put("ASMEFSheet", new double[]{28, 40});

        // Photo sizes
        SIZES.put("English Photo L", new double[]{3.5, 5});
        SIZES.put("Metric Photo L", new double[]{3.5, 5});
        SIZES.put("Photo 4x 4", new double[]{4, 4});
        SIZES.put("Photo 5x 5", new double[]{5, 5});
        SIZES.put("Photo 10x 12", new double[]{10, 12});
        SIZES.put("Photo 89x 89mm", new double[]{3.5, 3.5});

        // Index cards
        SIZES.put("North America 3x 5", new double[]{3, 5});
        SIZES.put("North America 4x 6", new double[]{4, 6});
        SIZES.put("North America 5x 7", new double[]{5, 7});
        SIZES.put("North America 5x 8", new double[]{5, 8});
        SIZES.put("North America 8x 10", new double[]{8, 10});

        // Super B
        SIZES.put("Super B", new double[]{13, 19});

        // Business cards
        SIZES.put("Business Card 2x 3 5", new double[]{2, 3.5});
        SIZES.put("Business Card 55x 85mm", new double[]{2.17, 3.35});
        SIZES.put("Business Card 55x 91mm", new double[]{2.17, 3.58});
        SIZES.put("Credit Card", new double[]{3.37, 2.13});
    }

    /**
     * Apply paper size to CDP params map.
     */
    public static void apply(Map<String, Object> params, String sizeName) {
        double[] wh = SIZES.get(sizeName);
        if (wh == null) {
            throw new IllegalArgumentException("Unknown paper size: " + sizeName);
        }

        params.put("paperWidth", wh[0]);
        params.put("paperHeight", wh[1]);
    }

    /**
     * List all supported sizes.
     */
    public static void listAll() {
        SIZES.keySet().forEach(System.out::println);
    }
}
