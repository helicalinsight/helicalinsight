import { CONFIG_TYPES } from "./config-tree-utils";
import {
  normalizeLayoutEntry,
  toSentenceCaseLabel,
} from "../../../../common/ui-generator";

export { toSentenceCaseLabel };

export const CONFIGURATION_LAYOUT_CONTENT_ID =
  "Static/layout/configuration/configuration.ui.layout";

export const CONFIGURATION_EDITORS_DIR = "Static/layout/configuration";

export const fileBaseName = (fileName) => {
  if (!fileName) return null;
  return String(fileName).replace(/\.[^.]+$/, "");
};

/**
 * Builds contentId for a per-file editor UI JSON.
 * getContents appends .json → Static/layout/configuration/<baseName>.ui.json
 */
export const toEditorUiContentId = (fileName) => {
  const baseName = fileBaseName(fileName);
  if (!baseName) return null;
  return `${CONFIGURATION_EDITORS_DIR}/${baseName}.ui`;
};

/**
 * Builds contentId for a per-file form layout.
 * getContents appends .json → Static/layout/configuration/<baseName>.ui.layout.json
 */
export const toFileLayoutContentId = (fileName) => {
  const baseName = fileBaseName(fileName);
  if (!baseName) return null;
  return `${CONFIGURATION_EDITORS_DIR}/${baseName}.ui.layout`;
};

export const resolveEditorLanguage = (editorUi, fileType) => {
  const language = editorUi?.language;
  // Only languages Monaco ships with out of the box; groovy/text → plaintext.
  if (language === "xml" || language === "json") {
    return language;
  }
  if (
    language === "groovy" ||
    language === "text" ||
    language === "plaintext" ||
    language === "properties"
  ) {
    return "plaintext";
  }
  const editor = editorUi?.editor || fileType;
  switch (editor) {
    case "xml":
    case CONFIG_TYPES.XML:
      return "xml";
    case "json":
    case CONFIG_TYPES.JSON:
      return "json";
    case "groovy":
    case "properties":
    case CONFIG_TYPES.PROPERTIES:
    case "text":
    case CONFIG_TYPES.OTHER:
    default:
      return "plaintext";
  }
};

/**
 * Monaco language for Raw mode, driven by file type / extension only.
 * xml → xml, json → json, properties/groovy/other → plaintext text editor
 */
export const resolveRawEditorLanguage = (fileType, fileName) => {
  const extension = String(fileName || "")
    .split(".")
    .pop()
    ?.toLowerCase();
  if (fileType === CONFIG_TYPES.XML || extension === "xml") {
    return "xml";
  }
  if (fileType === CONFIG_TYPES.JSON || extension === "json") {
    return "json";
  }
  // properties, groovy, and other files use a plain text editor in Raw mode
  return "plaintext";
};

/** File types that support structured ↔ raw editor toggle. */
export const TOGGLEABLE_EDITOR_TYPES = new Set([
  CONFIG_TYPES.XML,
  CONFIG_TYPES.JSON,
  CONFIG_TYPES.PROPERTIES,
  CONFIG_TYPES.OTHER,
  "xml",
  "json",
  "properties",
  "layout",
  "text",
  "groovy",
  "other",
]);

export const canToggleRawEditor = ({ fileType, editorUi, fileLayout, fileName } = {}) => {
  if (fileLayout?.sections?.length) return true;
  const extension = String(fileName || "")
    .split(".")
    .pop()
    ?.toLowerCase();
  const candidates = [
    fileType,
    extension,
    editorUi?.editor,
    editorUi?.fallbackEditor,
    fileLayout?.fallbackEditor,
    fileLayout?.format,
  ];
  return candidates.some((value) => TOGGLEABLE_EDITOR_TYPES.has(value));
};

export const propertiesObjectToText = (obj = {}) =>
  Object.keys(obj)
    .map((key) => `${key}=${obj[key] == null ? "" : String(obj[key])}`)
    .join("\n");

export const propertiesTextToObject = (text = "") => {
  const result = {};
  String(text)
    .split(/\r?\n/)
    .forEach((line) => {
      const trimmed = line.trim();
      if (!trimmed || trimmed.startsWith("#") || trimmed.startsWith("!")) {
        return;
      }
      let separator = trimmed.indexOf("=");
      if (separator < 0) {
        separator = trimmed.indexOf(":");
      }
      if (separator < 0) {
        return;
      }
      const key = trimmed.slice(0, separator).trim();
      if (!key) return;
      result[key] = trimmed.slice(separator + 1);
    });
  return result;
};

/**
 * Merges server file list with layout categories.
 * Files in hideCategories / visible:false categories are excluded from the UI list.
 */
export const buildCategorizedFiles = (layout, files = []) => {
  const fileKey = (name, path) => `${path || "Admin"}::${name}`;
  const fileByKey = new Map(
    (files || []).map((file) => [fileKey(file.name, file.path), file])
  );
  const fileByName = new Map();
  (files || []).forEach((file) => {
    if (!fileByName.has(file.name)) {
      fileByName.set(file.name, file);
    }
  });
  const hideKeys = new Set(
    (layout?.hideCategories || []).map((key) => String(key).toLowerCase())
  );

  const categories = [];
  const categorizedKeys = new Set();

  (layout?.categories || []).forEach((category) => {
    const key = category.key || category.title || "";
    const categoryPath = category.path || null;
    const visible =
      category.visible !== false && !hideKeys.has(String(key).toLowerCase());
    if (!visible) {
      (category.files || []).forEach((entry) => {
        const normalized = normalizeLayoutEntry(entry);
        if (normalized?.name) {
          categorizedKeys.add(
            fileKey(normalized.name, normalized.path || categoryPath)
          );
        }
      });
      return;
    }

    const items = (category.files || [])
      .map((entry) => {
        const normalized = normalizeLayoutEntry(entry);
        if (!normalized?.name) return null;
        const path = normalized.path || categoryPath || "Admin";
        const mapKey = fileKey(normalized.name, path);
        categorizedKeys.add(mapKey);
        const meta =
          fileByKey.get(mapKey) ||
          fileByName.get(normalized.name) ||
          null;
        if (!meta) return null;
        return {
          ...meta,
          ...normalized,
          path: path || meta.path || "Admin",
          category: key,
          categoryTitle: category.title || key,
        };
      })
      .filter(Boolean);

    if (!items.length) return;

    categories.push({
      key,
      title: category.title || key,
      icon: category.icon || key,
      description: category.description || "",
      path: categoryPath || undefined,
      files: items,
    });
  });

  // Opt-in bucket for files not listed in any category. Default off — InstantBI
  // and other dedicated UIs should not leak into a catch-all "Other" group.
  const showUncategorized = layout?.showUncategorized === true;
  if (showUncategorized) {
    const uncategorized = (files || []).filter(
      (file) => !categorizedKeys.has(fileKey(file.name, file.path))
    );
    if (uncategorized.length) {
      categories.push({
        key: "other",
        title: "Other",
        icon: "other",
        description: "Configuration files not assigned to a layout category.",
        files: uncategorized.map((file) => ({
          ...file,
          title: file.title || file.name,
          description: file.description || "",
          path: file.path || "Admin",
        })),
      });
    }
  }

  return categories;
};

export const filterCategorizedFiles = (categories, filterText) => {
  if (!filterText) return categories;
  const lower = filterText.toLowerCase();
  return categories
    .map((category) => ({
      ...category,
      files: (category.files || []).filter((file) => {
        const haystack = [file.name, file.title, file.description]
          .filter(Boolean)
          .join(" ")
          .toLowerCase();
        return haystack.includes(lower);
      }),
    }))
    .filter((category) => category.files.length > 0);
};

export const contentToEditorText = (type, content) => {
  if (content == null) return "";
  if (typeof content === "string") return content;
  if (type === CONFIG_TYPES.PROPERTIES) {
    return propertiesObjectToText(content);
  }
  if (type === CONFIG_TYPES.JSON) {
    try {
      return JSON.stringify(content, null, 2);
    } catch (error) {
      return String(content);
    }
  }
  return String(content);
};

/**
 * Converts Raw editor text back to the payload expected by the write API.
 */
export const editorTextToSaveContent = (type, text) => {
  if (type === CONFIG_TYPES.PROPERTIES) {
    return propertiesTextToObject(text);
  }
  if (type === CONFIG_TYPES.JSON) {
    const trimmed = String(text ?? "").trim();
    if (!trimmed) return {};
    return JSON.parse(trimmed);
  }
  return text;
};
