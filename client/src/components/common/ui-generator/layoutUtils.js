/**
 * Layout JSON helpers for ui-generator.
 * Expected shape:
 * {
 *   id, title, description, width,
 *   sections: [{ key, title, description, fields: [{ name, label, type, ... }] }]
 * }
 *
 * Catalog layouts may also include:
 * {
 *   categories: [{ key, title, description, files: [string | { name, title, description }] }]
 * }
 */

export const getNestedValue = (obj, path) => {
  if (!path) {
    return undefined;
  }
  return path.split(".").reduce((acc, key) => (acc == null ? undefined : acc[key]), obj);
};

export const setNestedValue = (obj, path, value) => {
  const keys = path.split(".");
  let cursor = obj;
  keys.forEach((key, index) => {
    if (index === keys.length - 1) {
      cursor[key] = value;
      return;
    }
    if (cursor[key] == null || typeof cursor[key] !== "object") {
      cursor[key] = {};
    }
    cursor = cursor[key];
  });
  return obj;
};

export const flattenLayoutFields = (layout) => {
  const fields = [];
  const walk = (sections = []) => {
    sections.forEach((section) => {
      (section.fields || []).forEach((field) => {
        if (field?.name) {
          fields.push(field);
        }
      });
      if (Array.isArray(section.sections) && section.sections.length) {
        walk(section.sections);
      }
    });
  };
  walk(layout?.sections || []);
  return fields;
};

/**
 * True when a layout defines at least one usable form section.
 * Empty JSON `{}` or `{ sections: [] }` returns false (caller should fall back).
 */
export const hasLayoutSections = (layout) => {
  if (!layout || typeof layout !== "object") {
    return false;
  }
  if (!Array.isArray(layout.sections)) {
    return false;
  }
  const walk = (sections = []) =>
    sections.some((section) => {
      if (!section) return false;
      if (
        section.title ||
        section.key ||
        (Array.isArray(section.fields) && section.fields.length > 0)
      ) {
        return true;
      }
      return Array.isArray(section.sections) && walk(section.sections);
    });
  return walk(layout.sections);
};

/**
 * Normalizes a layout catalog entry.
 * Accepts a plain string id/name or `{ name, title|displayName, description|help }`.
 */
export const normalizeLayoutEntry = (entry) => {
  if (!entry) {
    return null;
  }
  if (typeof entry === "string") {
    return { name: entry, title: entry, description: "" };
  }
  if (typeof entry === "object" && entry.name) {
    const normalized = {
      name: entry.name,
      title: entry.title || entry.displayName || entry.name,
      description: entry.description || entry.help || "",
    };
    if (entry.path) {
      normalized.path = entry.path;
    }
    if (entry.dir) {
      normalized.dir = entry.dir;
    }
    return normalized;
  }
  return null;
};

/** @deprecated Prefer normalizeLayoutEntry */
export const normalizeFileEntry = normalizeLayoutEntry;

const resolveFieldValue = (source, field) => {
  const name = field.name;
  let value;
  if (name.includes(".")) {
    value = getNestedValue(source, name);
  } else if (Object.prototype.hasOwnProperty.call(source, name)) {
    value = source[name];
  } else if (source?.config && Object.prototype.hasOwnProperty.call(source.config, name)) {
    value = source.config[name];
  }

  if (field.type === "boolean") {
    return value !== false;
  }
  if (field.type === "number") {
    return value == null || value === "" ? undefined : Number(value);
  }
  return value == null ? "" : value;
};

/**
 * Builds Ant Design Form initial values from a data record + layout fields.
 * Flat dotted keys (e.g. task.class) are kept flat for Form.Item names.
 */
export const buildInitialValuesFromLayout = (source = {}, layout) => {
  const initial = {};
  flattenLayoutFields(layout).forEach((field) => {
    initial[field.name] = resolveFieldValue(source, field);
  });
  return initial;
};

/**
 * Converts flat form values (including dotted names) into a nested object.
 * Known top-level keys listed in `flatKeys` stay as-is.
 */
export const unflattenFormValues = (values = {}, { nestDotted = true } = {}) => {
  if (!nestDotted) {
    return { ...values };
  }
  const result = {};
  Object.keys(values).forEach((key) => {
    if (key.includes(".")) {
      setNestedValue(result, key, values[key]);
    } else {
      result[key] = values[key];
    }
  });
  return result;
};

export const isFieldReadOnly = (field, { isAdd = false } = {}) => {
  if (field?.readOnly) {
    return true;
  }
  if (!isAdd && field?.readOnlyOnEdit) {
    return true;
  }
  return false;
};

export const getFieldRules = (field) => {
  if (field?.required) {
    return [{ required: true, message: `${field.label || field.name} is required` }];
  }
  return undefined;
};
