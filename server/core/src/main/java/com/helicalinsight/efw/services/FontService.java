package com.helicalinsight.efw.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.utility.FontFileUtils;
import com.lowagie.text.FontFactory;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Font catalog for {@code GET /getFonts}: flat map keyed by display name
 * (one entry per selectable font — easy UI dictionary lookup).
 * <p>
 * Managed fonts under Admin/fonts are scanned/registered only when that directory
 * changes. OS families are enumerated live on each catalog build (not cached).
 */
public final class FontService {

    private static final Logger logger = LoggerFactory.getLogger(FontService.class);
    public static final String FONTS_RELATIVE_DIR = "System/Admin/fonts";

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    private static volatile String registeredDirectoryHash;
    private static volatile List<ManagedFace> cachedManagedFaces = List.of();
    private static volatile long cachedManagedVersion;

    private FontService() {
    }

    public static String getFontsPath() {
        return ApplicationProperties.getInstance().getFontsPath();
    }

    public static String getFontsJson() {
        ensureManagedFonts(false);
        List<ManagedFace> managed = cachedManagedFaces;
        long version = cachedManagedVersion;
        return buildFontsJson(managed, captureOsFontFamilies(), version);
    }

    public static String[] getAvailableFontFamilyNames() {
        ensureManagedFonts(false);
        List<ManagedFace> managed = cachedManagedFaces;
        Map<String, FontEntry> fonts = buildFontEntries(managed, captureOsFontFamilies());
        return fonts.keySet().toArray(new String[0]);
    }

    public static void registerFontDirectories() {
        ensureManagedFonts(false);
    }

    /**
     * O(1) FontFactory map lookup. Call {@link #registerFontDirectories()} once
     * before report build/export so managed aliases are present; do not rescan here.
     */
    public static boolean fontExists(String fontName) {
        if (fontName == null || fontName.isBlank()) {
            return false;
        }
        return FontFactory.isRegistered(fontName.toLowerCase(Locale.ROOT));
    }

    public static void refreshAfterImport() {
        refreshAll();
    }

    public static void refreshAll() {
        ensureManagedFonts(true);
    }

    /**
     * Refresh managed font scan + JVM registration only when Admin/fonts changes.
     * Does not cache OS font families.
     */
    private static synchronized void ensureManagedFonts(boolean force) {
        File fontsDirectory = ensureFontsDirectory();
        deleteObsoleteArtifacts(fontsDirectory);

        String hash = fontsDirectory.getAbsolutePath() + ":" + directoryVersion(fontsDirectory);
        if (!force && hash.equals(registeredDirectoryHash)) {
            return;
        }

        logger.info("Refreshing managed fonts from {}", fontsDirectory.getAbsolutePath());
        List<ManagedFace> managedFaces = collectManagedFaces(fontsDirectory);
        registerJvmFonts(managedFaces);

        long version = directoryVersion(fontsDirectory);
        registeredDirectoryHash = fontsDirectory.getAbsolutePath() + ":" + version;
        cachedManagedVersion = version;
        cachedManagedFaces = List.copyOf(managedFaces);
        logger.info("Managed font display names: {}",
                managedFaces.stream().map(f -> f.displayName).toArray());
    }

    private static String[] captureOsFontFamilies() {
        try {
            String[] osFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
            return osFonts != null ? osFonts : new String[0];
        } catch (Throwable ex) {
            logger.warn("Could not enumerate OS fonts", ex);
            return new String[0];
        }
    }

    /**
     * Flat dictionary keyed by display name for UI lookup.
     * OS families already covered by a managed cssFamily are omitted.
     */
    private static String buildFontsJson(List<ManagedFace> managedFaces, String[] osFonts, long version) {
        Map<String, FontEntry> fonts = buildFontEntries(managedFaces, osFonts);
        JsonObject root = new JsonObject();
        root.addProperty("version", version);
        JsonObject fontsJson = new JsonObject();
        for (Map.Entry<String, FontEntry> entry : fonts.entrySet()) {
            fontsJson.add(entry.getKey(), entry.getValue().toJson());
        }
        root.add("fonts", fontsJson);
        return GSON.toJson(root);
    }

    private static Map<String, FontEntry> buildFontEntries(List<ManagedFace> managedFaces, String[] osFonts) {
        Map<String, FontEntry> fonts = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        Set<String> managedCssFamilies = new HashSet<>();
        Set<String> managedDisplayNames = new HashSet<>();
        for (ManagedFace face : managedFaces) {
            managedCssFamilies.add(face.cssFamily.toLowerCase(Locale.ROOT));
            managedDisplayNames.add(face.displayName.toLowerCase(Locale.ROOT));
        }

        for (String family : osFonts) {
            if (family == null || family.isBlank()) {
                continue;
            }
            String lower = family.toLowerCase(Locale.ROOT);
            // Skip OS entries that match a managed upload (also covers AWT-registered managed families)
            if (managedCssFamilies.contains(lower) || managedDisplayNames.contains(lower)) {
                continue;
            }
            fonts.put(family, FontEntry.os(family));
        }

        for (ManagedFace face : managedFaces) {
            fonts.put(face.displayName, FontEntry.managed(face));
        }
        return fonts;
    }

    private static void deleteObsoleteArtifacts(File fontsDirectory) {
        deleteQuietly(new File(fontsDirectory, "fonts.css"));
        deleteQuietly(new File(fontsDirectory, "fonts-registry.json"));
    }

    private static void deleteQuietly(File file) {
        if (file.exists() && !file.delete()) {
            logger.debug("Could not delete {}", file.getAbsolutePath());
        }
    }

    private static void registerJvmFonts(List<ManagedFace> managedFaces) {
        File fontsDirectory = ensureFontsDirectory();
        try {
            if (fontsDirectory.isDirectory()) {
                FontFactory.registerDirectory(fontsDirectory.getAbsolutePath(), true);
            }
        } catch (Exception ex) {
            logger.warn("FontFactory registration failed for {}", fontsDirectory.getAbsolutePath(), ex);
        }

        GraphicsEnvironment ge = null;
        try {
            ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        } catch (Throwable ex) {
            logger.warn("Skipping AWT font registration", ex);
        }

        for (ManagedFace face : managedFaces) {
            if (!FontFileUtils.isJvmFontExtension(FilenameUtils.getExtension(face.fileName))) {
                continue;
            }
            String absolutePath = face.file.getAbsolutePath();
            // Alias catalog display name (and cssFamily) so Jasper pdfFontName matches picker keys
            try {
                FontFactory.register(absolutePath, face.displayName);
                if (face.cssFamily != null
                        && !face.cssFamily.equalsIgnoreCase(face.displayName)) {
                    FontFactory.register(absolutePath, face.cssFamily);
                }
            } catch (Exception ex) {
                logger.debug("Could not register FontFactory alias for {}", absolutePath, ex);
            }
            if (ge != null) {
                try {
                    Font font = Font.createFont(Font.TRUETYPE_FONT, face.file);
                    ge.registerFont(font);
                } catch (Exception ex) {
                    logger.debug("Could not register AWT font from {}", absolutePath, ex);
                }
            }
        }
    }

    private static List<ManagedFace> collectManagedFaces(File directory) {
        List<ManagedFace> faces = new ArrayList<>();
        if (!directory.isDirectory() || Files.isSymbolicLink(directory.toPath())) {
            return faces;
        }
        File[] files = directory.listFiles();
        if (files == null) {
            return faces;
        }

        // One catalog entry per font file; uniquify display name if metadata collides
        Map<String, Integer> displayNameCounts = new LinkedHashMap<>();

        for (File file : files) {
            if (Files.isSymbolicLink(file.toPath()) || file.isDirectory()) {
                continue;
            }
            if (!FontFileUtils.isFontFile(file.getName())) {
                continue;
            }

            String displayName = resolveDisplayName(file);
            String cssFamily = resolveCssFamily(file, displayName);
            String uniqueDisplay = uniquifyDisplayName(displayName, file, displayNameCounts);

            faces.add(new ManagedFace(uniqueDisplay, cssFamily, file));
        }

        faces.sort((a, b) -> String.CASE_INSENSITIVE_ORDER.compare(a.displayName, b.displayName));
        return faces;
    }

    /**
     * Prefer full font name from the file (e.g. "Caacupe One Regular").
     * Each file still gets its own catalog entry even when names collide.
     */
    private static String resolveDisplayName(File file) {
        String extension = FilenameUtils.getExtension(file.getName()).toLowerCase(Locale.ROOT);
        if (FontFileUtils.isJvmFontExtension(extension)) {
            try {
                Font font = Font.createFont(Font.TRUETYPE_FONT, file);
                String fontName = font.getFontName();
                if (fontName != null && !fontName.isBlank()) {
                    return fontName.trim();
                }
                String family = font.getFamily();
                if (family != null && !family.isBlank()) {
                    return family.trim();
                }
            } catch (Exception ex) {
                logger.debug("Could not read display name from {}", file.getAbsolutePath(), ex);
            }
        }
        return FilenameUtils.getBaseName(file.getName());
    }

    private static String resolveCssFamily(File file, String displayNameFallback) {
        String extension = FilenameUtils.getExtension(file.getName()).toLowerCase(Locale.ROOT);
        if (FontFileUtils.isJvmFontExtension(extension)) {
            try {
                Font font = Font.createFont(Font.TRUETYPE_FONT, file);
                String family = font.getFamily();
                if (family != null && !family.isBlank()) {
                    return family.trim();
                }
            } catch (Exception ex) {
                logger.debug("Could not read CSS family from {}", file.getAbsolutePath(), ex);
            }
        }
        return displayNameFallback;
    }

    /**
     * Catalog keys must be unique. First file keeps metadata name; later collisions use file base name.
     */
    private static String uniquifyDisplayName(String displayName, File file, Map<String, Integer> counts) {
        String key = displayName.toLowerCase(Locale.ROOT);
        int count = counts.getOrDefault(key, 0) + 1;
        counts.put(key, count);
        if (count == 1) {
            return displayName;
        }
        String baseName = FilenameUtils.getBaseName(file.getName());
        String baseKey = baseName.toLowerCase(Locale.ROOT);
        int baseCount = counts.getOrDefault(baseKey, 0) + 1;
        counts.put(baseKey, baseCount);
        if (baseCount == 1) {
            return baseName;
        }
        return baseName + " (" + baseCount + ")";
    }

    private static File ensureFontsDirectory() {
        String path = getFontsPath();
        if (path == null || path.isBlank()) {
            return new File("fonts");
        }
        File fontsDirectory = new File(path);
        if (!fontsDirectory.exists() && !fontsDirectory.mkdirs()) {
            logger.warn("Could not create fonts directory {}", fontsDirectory.getAbsolutePath());
        }
        return fontsDirectory;
    }

    private static long directoryVersion(File directory) {
        if (Files.isSymbolicLink(directory.toPath())) {
            return 0L;
        }
        long version = directory.lastModified();
        File[] files = directory.listFiles();
        if (files == null) {
            return version;
        }
        for (File file : files) {
            if (Files.isSymbolicLink(file.toPath()) || file.isDirectory()) {
                continue;
            }
            version += file.lastModified();
        }
        return version;
    }

    public static boolean isManagedFontFile(File file) {
        if (file == null) {
            return false;
        }
        String fontsPath = getFontsPath();
        if (fontsPath == null || fontsPath.isBlank()) {
            return false;
        }
        try {
            File fontsDirectory = new File(fontsPath).getCanonicalFile();
            File canonical = file.getCanonicalFile();
            if (!canonical.getPath().startsWith(fontsDirectory.getPath() + File.separator)
                    && !canonical.equals(fontsDirectory)) {
                return false;
            }
            String name = canonical.getName();
            if ("fonts.css".equalsIgnoreCase(name) || "fonts-registry.json".equalsIgnoreCase(name)) {
                return false;
            }
            return FontFileUtils.isFontFile(name);
        } catch (IOException ex) {
            return false;
        }
    }

    private static String cssFormat(String extension) {
        if (extension == null) {
            return "truetype";
        }
        return switch (extension.toLowerCase(Locale.ROOT)) {
            case "ttf", "ttc" -> "truetype";
            case "otf" -> "opentype";
            case "woff" -> "woff";
            case "woff2" -> "woff2";
            default -> "truetype";
        };
    }

    private static final class ManagedFace {
        private final String displayName;
        private final String cssFamily;
        private final File file;
        private final String fileName;

        private ManagedFace(String displayName, String cssFamily, File file) {
            this.displayName = displayName;
            this.cssFamily = cssFamily;
            this.file = file;
            this.fileName = file.getName();
        }
    }

    private static final class FontEntry {
        private final String displayName;
        private final boolean managed;
        private final String fileName;
        private final String path;
        private final String format;
        private final String cssFamily;

        private FontEntry(String displayName, boolean managed, String fileName, String path, String format, String cssFamily) {
            this.displayName = displayName;
            this.managed = managed;
            this.fileName = fileName;
            this.path = path;
            this.format = format;
            this.cssFamily = cssFamily;
        }

        static FontEntry managed(ManagedFace face) {
            return new FontEntry(
                    face.displayName,
                    true,
                    face.fileName,
                    FONTS_RELATIVE_DIR + "/" + face.fileName,
                    cssFormat(FilenameUtils.getExtension(face.fileName)),
                    face.cssFamily
            );
        }

        static FontEntry os(String family) {
            return new FontEntry(family, false, null, null, null, family);
        }

        JsonObject toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("family", displayName);
            json.addProperty("isManaged", managed);
            if (managed) {
                json.addProperty("fileName", fileName);
                json.addProperty("path", path);
                json.addProperty("format", format);
                json.addProperty("cssFamily", cssFamily);
            } else {
                json.add("fileName", JsonNull.INSTANCE);
                json.add("path", JsonNull.INSTANCE);
                json.add("format", JsonNull.INSTANCE);
                json.addProperty("cssFamily", cssFamily);
            }
            return json;
        }
    }
}
