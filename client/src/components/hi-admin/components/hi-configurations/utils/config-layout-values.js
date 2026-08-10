/**
 * XML / properties / JSON path helpers for configuration layout forms.
 */

import { flattenLayoutFields } from "../../../../common/ui-generator";

const isTrue = (value) => {
  if (typeof value === "boolean") return value;
  const text = String(value ?? "").trim().toLowerCase();
  return text === "true" || text === "1" || text === "yes";
};

export const coerceLayoutValue = (raw, type) => {
  switch (type) {
    case "boolean":
    case "switch":
      return isTrue(raw);
    case "number": {
      if (raw === "" || raw == null) return undefined;
      const num = Number(raw);
      return Number.isNaN(num) ? undefined : num;
    }
    default:
      return raw == null ? "" : String(raw);
  }
};

export const serializeLayoutValue = (value, type) => {
  switch (type) {
    case "boolean":
    case "switch":
      return value ? "true" : "false";
    case "number":
      return value == null || value === "" ? "" : String(value);
    default:
      return value == null ? "" : String(value);
  }
};

const splitPath = (path) =>
  String(path || "")
    .split("/")
    .map((part) => part.trim())
    .filter(Boolean);

/**
 * Resolves an XML node from a slash path.
 * Supports trailing @attr for attributes, e.g. contexts/context/@name
 */
export const resolveXmlNode = (xmlString, path) => {
  if (!xmlString || !path) {
    return { document: null, node: null, attribute: null };
  }
  const parser = new DOMParser();
  const document = parser.parseFromString(xmlString, "application/xml");
  if (document.getElementsByTagName("parsererror").length) {
    throw new Error("Invalid XML content");
  }

  const parts = splitPath(path);
  let attribute = null;
  if (parts.length && parts[parts.length - 1].startsWith("@")) {
    attribute = parts.pop().slice(1);
  }

  let node = document.documentElement;
  let index = 0;
  if (node && parts[0] && node.nodeName === parts[0]) {
    index = 1;
  }

  for (; index < parts.length; index += 1) {
    if (!node) break;
    const name = parts[index];
    const match = Array.from(node.children || []).find(
      (child) => child.nodeName === name
    );
    node = match || null;
  }

  return { document, node, attribute };
};

export const getXmlPathValue = (xmlString, path) => {
  const { node, attribute } = resolveXmlNode(xmlString, path);
  if (!node) return "";
  if (attribute) {
    return node.getAttribute(attribute) ?? "";
  }
  return node.textContent ?? "";
};

export const setXmlPathValue = (xmlString, path, value) => {
  const { document, node, attribute } = resolveXmlNode(xmlString, path);
  if (!document || !node) {
    throw new Error(`XML path not found: ${path}`);
  }
  if (attribute) {
    node.setAttribute(attribute, value == null ? "" : String(value));
  } else {
    node.textContent = value == null ? "" : String(value);
  }
  return new XMLSerializer().serializeToString(document);
};

export const getJsonPathValue = (data, path) => {
  const parts = splitPath(path);
  let cursor = data;
  for (const part of parts) {
    if (cursor == null) return undefined;
    cursor = cursor[part];
  }
  return cursor;
};

export const setJsonPathValue = (data, path, value) => {
  const parts = splitPath(path);
  if (!parts.length) return value;
  const draft = Array.isArray(data)
    ? [...data]
    : { ...(data && typeof data === "object" ? data : {}) };
  let cursor = draft;
  for (let i = 0; i < parts.length - 1; i += 1) {
    const part = parts[i];
    const next = cursor[part];
    const clone =
      next == null
        ? {}
        : Array.isArray(next)
        ? [...next]
        : typeof next === "object"
        ? { ...next }
        : {};
    cursor[part] = clone;
    cursor = clone;
  }
  cursor[parts[parts.length - 1]] = value;
  return draft;
};

/**
 * Reads layout field values from XML string / JSON object / properties map.
 */
export const readLayoutValues = (layout, content, format) => {
  const values = {};
  const fields = flattenLayoutFields(layout);
  const resolvedFormat = format || layout?.format || "xml";

  fields.forEach((field) => {
    const path = field.path || field.name;
    let raw;
    if (resolvedFormat === "xml") {
      raw = getXmlPathValue(content || "", path);
    } else if (resolvedFormat === "json" || resolvedFormat === "properties") {
      raw = getJsonPathValue(content || {}, path);
    } else {
      raw = "";
    }
    values[field.name] = coerceLayoutValue(raw, field.type);
  });

  return values;
};

/**
 * Writes layout form values back into the original content.
 * Returns XML string for xml format, object for json/properties.
 */
export const writeLayoutValues = (layout, content, formValues, format) => {
  const fields = flattenLayoutFields(layout);
  const resolvedFormat = format || layout?.format || "xml";

  if (resolvedFormat === "xml") {
    let xml = content || "";
    fields.forEach((field) => {
      const path = field.path || field.name;
      const serialized = serializeLayoutValue(formValues?.[field.name], field.type);
      xml = setXmlPathValue(xml, path, serialized);
    });
    return xml;
  }

  let data =
    resolvedFormat === "properties"
      ? { ...(content || {}) }
      : content && typeof content === "object"
      ? JSON.parse(JSON.stringify(content))
      : {};

  fields.forEach((field) => {
    const path = field.path || field.name;
    const nextValue =
      field.type === "boolean" || field.type === "switch"
        ? !!formValues?.[field.name]
        : field.type === "number"
        ? formValues?.[field.name]
        : formValues?.[field.name] ?? "";
    data = setJsonPathValue(data, path, nextValue);
  });
  return data;
};
