/** Read and write developer-settings fields from layout `path` values. */

function walkFields(layout, visit) {
  const walk = (section) => {
    (section?.fields || []).forEach((field) => {
      if (field?.name && field?.path) visit(field);
    });
    (section?.sections || []).forEach(walk);
  };
  (layout?.sections || []).forEach(walk);
}

function readPath(source, path) {
  return String(path)
    .split(".")
    .filter(Boolean)
    .reduce((current, key) => (current == null ? undefined : current[key]), source);
}

function writePath(target, path, value) {
  const keys = String(path).split(".").filter(Boolean);
  if (!keys.length) return;
  let current = target;
  keys.slice(0, -1).forEach((key) => {
    if (!current[key] || typeof current[key] !== "object") current[key] = {};
    current = current[key];
  });
  current[keys[keys.length - 1]] = value;
}

function fieldValue(field, raw) {
  if (field.type === "boolean" || field.type === "switch") return !!raw;
  if (field.type === "number") {
    const number = Number(raw);
    return Number.isFinite(number) ? number : field.min ?? undefined;
  }
  return raw ?? "";
}

export function formValuesFromConfig(layout, config) {
  const values = {};
  walkFields(layout, (field) => {
    values[field.name] = fieldValue(field, readPath(config || {}, field.path));
  });
  return values;
}

export function configUpdatesFromForm(layout, formValues) {
  const updates = {};
  walkFields(layout, (field) => {
    writePath(updates, field.path, fieldValue(field, formValues?.[field.name]));
  });
  return updates;
}

export function mergeConfigUpdates(...parts) {
  const merged = {};
  parts.forEach((part) => {
    Object.entries(part || {}).forEach(([section, value]) => {
      if (
        value &&
        typeof value === "object" &&
        !Array.isArray(value) &&
        merged[section] &&
        typeof merged[section] === "object"
      ) {
        merged[section] = { ...merged[section], ...value };
      } else {
        merged[section] = value;
      }
    });
  });
  return merged;
}
