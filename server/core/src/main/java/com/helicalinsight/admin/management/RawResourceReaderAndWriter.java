package com.helicalinsight.admin.management;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.helicalinsight.datasource.GsonUtility;
import com.helicalinsight.efw.ApplicationProperties;
import com.helicalinsight.efw.controllerutils.ControllerUtils;
import com.helicalinsight.efw.exceptions.EfwServiceException;
import com.helicalinsight.efw.exceptions.OperationFailedException;
import com.helicalinsight.efw.exceptions.ResourceNotFoundException;
import com.helicalinsight.efw.serviceframework.IComponent;
import com.helicalinsight.efw.utility.FileUtils;
import com.helicalinsight.efw.utility.XmlUtils;
import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Lists, reads and writes configuration files under hi-repository/System
 * (Admin by default, plus Mail and other System subfolders via {@code path}).
 * JSON is handled with Google Gson; XML with DOM DocumentBuilder.
 *
 * @author somen
 */
@SuppressWarnings("unused")
public class RawResourceReaderAndWriter implements IComponent {

    private static final String TYPE_PROPERTIES = "properties";
    private static final String TYPE_XML = "xml";
    private static final String TYPE_JSON = "json";
    private static final String TYPE_OTHER = "other";

    private static final String DEFAULT_PATH = "Admin";
    /** System-relative folders included in the configuration file list. */
    private static final String[] LIST_PATHS = {"Admin", "Mail"};

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
    private static final Gson PRETTY_GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

    @Override
    public boolean isThreadSafeToCache() {
        return true;
    }

    @Override
    public String executeComponent(String jsonFormData) {
        JsonObject formJson = GSON.fromJson(jsonFormData, JsonObject.class);
        String action = formJson.get("action").getAsString();

        switch (action) {
            case "list":
                return listConfigurationFiles();
            case "read":
                return readResource(resolveFile(formJson), formJson);
            case "write":
                return writeFile(formJson.get("content"), resolveFile(formJson), formJson);
            default:
                throw new EfwServiceException("This action is not found");
        }
    }

    private String listConfigurationFiles() {
        JsonArray fileList = new JsonArray();
        for (String path : LIST_PATHS) {
            appendFilesFromPath(fileList, path);
        }
        fileList = sortFileList(fileList);

        JsonObject response = new JsonObject();
        response.add("files", fileList);
        return response.toString();
    }

    private void appendFilesFromPath(JsonArray fileList, String path) {
        File directory = getSystemSubDirectory(path);
        if (!directory.exists() || !directory.isDirectory()) {
            return;
        }
        File[] files = directory.listFiles(File::isFile);
        if (files == null) {
            return;
        }
        for (File file : files) {
            JsonObject item = new JsonObject();
            item.addProperty("name", file.getName());
            item.addProperty("type", resolveFileType(file.getName()));
            item.addProperty("path", path);
            fileList.add(item);
        }
    }

    private JsonArray sortFileList(JsonArray fileList) {
        List<JsonObject> items = new ArrayList<>();
        fileList.forEach(element -> items.add(element.getAsJsonObject()));
        items.sort(Comparator
                .comparing((JsonObject o) -> GsonUtility.optStringValue(o, "path", DEFAULT_PATH),
                        String.CASE_INSENSITIVE_ORDER)
                .thenComparing(o -> o.get("name").getAsString(), String.CASE_INSENSITIVE_ORDER));
        JsonArray sorted = new JsonArray();
        items.forEach(sorted::add);
        return sorted;
    }

    public String readResource(File file) {
        return readResource(file, null);
    }

    public String readResource(File file, JsonObject formJson) {
        if (!file.exists() || !file.isFile()) {
            throw new ResourceNotFoundException("The given file does not exists");
        }

        String type = resolveFileType(file.getName());
        JsonObject response = new JsonObject();
        response.addProperty("file", file.getName());
        response.addProperty("type", type);
        response.addProperty("path", resolveRequestPath(formJson));

        try {
            switch (type) {
                case TYPE_PROPERTIES:
                    response.add("content", readProperties(file));
                    break;
                case TYPE_JSON:
                    response.add("content", readJson(file));
                    break;
                case TYPE_XML:
                    response.addProperty("content", readXml(file));
                    break;
                case TYPE_OTHER:
                default:
                    response.addProperty("content",
                            org.apache.commons.io.FileUtils.readFileToString(file, Charset.defaultCharset()));
                    break;
            }
            return response.toString();
        } catch (IOException | SAXException | TransformerException ex) {
            throw new OperationFailedException("There was some problem " + ex.getMessage());
        }
    }

    public String writeFile(JsonElement content, File file) {
        return writeFile(content, file, null);
    }

    public String writeFile(JsonElement content, File file, JsonObject formJson) {
        String type = resolveFileType(file.getName());
        JsonObject response = new JsonObject();
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                throw new OperationFailedException("Unable to create parent directory for file");
            }

            switch (type) {
                case TYPE_PROPERTIES:
                    writeProperties(file, toPropertiesMap(content));
                    break;
                case TYPE_JSON:
                    writeJson(file, content);
                    break;
                case TYPE_XML:
                    writeXml(file, asString(content));
                    break;
                case TYPE_OTHER:
                default:
                    org.apache.commons.io.FileUtils.write(file, asString(content), ControllerUtils.defaultCharSet());
                    break;
            }
            response.addProperty("message", "File Saved Successfully");
            response.addProperty("file", file.getName());
            response.addProperty("type", type);
            response.addProperty("path", resolveRequestPath(formJson));
            return response.toString();
        } catch (IOException | SAXException | TransformerException ex) {
            throw new OperationFailedException("File could not be saved. " + ex.getMessage());
        }
    }

    /**
     * Resolves a file under System/{path} by default (Admin).
     * Prefer {@code path} relative to the System directory (e.g. Admin, Mail).
     * Optional {@code dir} (relative to solution directory) is retained for backward compatibility.
     */
    private File resolveFile(JsonObject formJson) {
        String fileName = GsonUtility.optString(formJson, "file");
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new EfwServiceException("file is required");
        }
        if (!fileName.equals(FilenameUtils.getName(fileName))
                || fileName.contains("..")
                || fileName.contains("/")
                || fileName.contains("\\")) {
            throw new EfwServiceException("Invalid file name");
        }

        String path = optRequestPath(formJson);
        String dir = GsonUtility.optString(formJson, "dir");
        File baseDir;
        File systemRoot = getSystemDirectory();
        String resolvedPath = path;
        if (path != null && !path.isEmpty()) {
            baseDir = getSystemSubDirectory(path);
        } else if (dir != null && !dir.isEmpty()) {
            if (dir.contains("..")) {
                throw new EfwServiceException("Invalid directory");
            }
            baseDir = new File(ApplicationProperties.getInstance().getSolutionDirectory() + File.separator + dir);
            resolvedPath = null;
        } else {
            resolvedPath = DEFAULT_PATH;
            baseDir = getSystemSubDirectory(DEFAULT_PATH);
        }

        File target = new File(baseDir, fileName);
        try {
            String systemRootPath = systemRoot.getCanonicalPath();
            String basePath = baseDir.getCanonicalPath();
            String targetPath = target.getCanonicalPath();
            // When using System-relative path, keep writes inside System/
            if (resolvedPath != null && !basePath.startsWith(systemRootPath + File.separator)
                    && !basePath.equals(systemRootPath)) {
                throw new EfwServiceException("Invalid path");
            }
            if (!targetPath.startsWith(basePath + File.separator)) {
                throw new EfwServiceException("Invalid file name");
            }
        } catch (IOException ex) {
            throw new OperationFailedException("Unable to resolve file path. " + ex.getMessage());
        }
        return target;
    }

    private String resolveRequestPath(JsonObject formJson) {
        String path = optRequestPath(formJson);
        return path == null || path.isEmpty() ? DEFAULT_PATH : path;
    }

    private String optRequestPath(JsonObject formJson) {
        if (formJson == null) {
            return null;
        }
        String path = GsonUtility.optString(formJson, "path");
        if (path == null || path.trim().isEmpty()) {
            return null;
        }
        path = path.trim().replace('\\', '/');
        if (path.contains("..") || path.startsWith("/") || path.contains(":")) {
            throw new EfwServiceException("Invalid path");
        }
        return path;
    }

    private File getSystemDirectory() {
        return new File(ApplicationProperties.getInstance().getSystemDirectory());
    }

    private File getSystemSubDirectory(String path) {
        String normalized = path == null ? DEFAULT_PATH : path.trim().replace('\\', '/');
        if (normalized.isEmpty()) {
            normalized = DEFAULT_PATH;
        }
        if (normalized.contains("..") || normalized.startsWith("/") || normalized.contains(":")) {
            throw new EfwServiceException("Invalid path");
        }
        File systemRoot = getSystemDirectory();
        File target = new File(systemRoot, normalized.replace('/', File.separatorChar));
        try {
            String rootPath = systemRoot.getCanonicalPath();
            String targetPath = target.getCanonicalPath();
            if (!targetPath.equals(rootPath) && !targetPath.startsWith(rootPath + File.separator)) {
                throw new EfwServiceException("Invalid path");
            }
        } catch (IOException ex) {
            throw new OperationFailedException("Unable to resolve path. " + ex.getMessage());
        }
        return target;
    }

    private File getAdminDirectory() {
        return getSystemSubDirectory(DEFAULT_PATH);
    }

    private String resolveFileType(String fileName) {
        String extension = FileUtils.getExtension(fileName);
        if (extension == null) {
            return TYPE_OTHER;
        }
        switch (extension.toLowerCase()) {
            case "properties":
                return TYPE_PROPERTIES;
            case "xml":
                return TYPE_XML;
            case "json":
                return TYPE_JSON;
            default:
                return TYPE_OTHER;
        }
    }

    private JsonObject readProperties(File file) throws IOException {
        Properties properties = new Properties();
        try (FileInputStream inputStream = new FileInputStream(file)) {
            properties.load(inputStream);
        }
        JsonObject content = new JsonObject();
        Map<String, String> ordered = new LinkedHashMap<>();
        for (String name : properties.stringPropertyNames()) {
            ordered.put(name, properties.getProperty(name));
        }
        ordered.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(String.CASE_INSENSITIVE_ORDER))
                .forEach(entry -> content.addProperty(entry.getKey(), entry.getValue()));
        return content;
    }

    private void writeProperties(File file, Map<String, String> values) throws IOException {
        Properties properties = new Properties();
        for (Map.Entry<String, String> entry : values.entrySet()) {
            if (entry.getKey() != null) {
                properties.setProperty(entry.getKey(), entry.getValue() == null ? "" : entry.getValue());
            }
        }
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), ControllerUtils.defaultCharSet())) {
            properties.store(writer, "Updated via RawResourceReaderAndWriter");
        }
    }

    private JsonElement readJson(File file) throws IOException {
        String jsonText = org.apache.commons.io.FileUtils.readFileToString(file, Charset.defaultCharset());
        if (jsonText == null || jsonText.trim().isEmpty()) {
            return new JsonObject();
        }
        try {
            return JsonParser.parseString(jsonText);
        } catch (JsonSyntaxException ex) {
            throw new OperationFailedException("Invalid JSON content in file. " + ex.getMessage());
        }
    }

    private void writeJson(File file, JsonElement content) throws IOException {
        JsonElement json = content;
        if (content == null || content.isJsonNull()) {
            json = new JsonObject();
        } else if (content.isJsonPrimitive()) {
            String text = content.getAsString().trim();
            try {
                json = JsonParser.parseString(text.isEmpty() ? "{}" : text);
            } catch (JsonSyntaxException ex) {
                throw new OperationFailedException("Invalid JSON content. " + ex.getMessage());
            }
        }
        org.apache.commons.io.FileUtils.write(file, PRETTY_GSON.toJson(json), ControllerUtils.defaultCharSet());
    }

    private String readXml(File file) throws IOException, SAXException, TransformerException {
        DocumentBuilder documentBuilder = XmlUtils.getDocumentBuilder();
        Document document = documentBuilder.parse(file);
        document.getDocumentElement().normalize();
        return documentToString(document);
    }

    private void writeXml(File file, String xmlContent) throws IOException, SAXException, TransformerException {
        if (xmlContent == null || xmlContent.trim().isEmpty()) {
            throw new EfwServiceException("XML content is required");
        }
        DocumentBuilder documentBuilder = XmlUtils.getDocumentBuilder();
        Document document = documentBuilder.parse(new InputSource(new StringReader(xmlContent)));
        document.getDocumentElement().normalize();
        XmlUtils.transform(file.getAbsolutePath(), document);
    }

    private String documentToString(Document document) throws TransformerException {
        Transformer transformer = XmlUtils.getTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));
        return writer.toString();
    }

    private Map<String, String> toPropertiesMap(JsonElement content) {
        Map<String, String> map = new LinkedHashMap<>();
        JsonObject json;
        if (content == null || content.isJsonNull()) {
            throw new EfwServiceException("Invalid properties content. Expected a JSON object of key/value pairs.");
        }
        if (content.isJsonObject()) {
            json = content.getAsJsonObject();
        } else if (content.isJsonPrimitive()) {
            try {
                json = JsonParser.parseString(content.getAsString()).getAsJsonObject();
            } catch (Exception ex) {
                throw new EfwServiceException("Invalid properties content. Expected a JSON object of key/value pairs.");
            }
        } else {
            throw new EfwServiceException("Invalid properties content. Expected a JSON object of key/value pairs.");
        }

        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            JsonElement value = entry.getValue();
            if (value == null || value.isJsonNull()) {
                map.put(entry.getKey(), "");
            } else if (value.isJsonPrimitive()) {
                map.put(entry.getKey(), value.getAsString());
            } else {
                map.put(entry.getKey(), value.toString());
            }
        }
        return map;
    }

    private String asString(JsonElement content) {
        if (content == null || content.isJsonNull()) {
            return "";
        }
        if (content.isJsonPrimitive()) {
            return content.getAsString();
        }
        return content.toString();
    }
}
