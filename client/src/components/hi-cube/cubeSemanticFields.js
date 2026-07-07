export function parseCommaSeparatedToArray(value) {
  if (Array.isArray(value)) {
    return value.map((item) => String(item).trim()).filter(Boolean);
  }
  if (value == null || value === "") {
    return [];
  }
  return String(value)
    .split(",")
    .map((item) => item.trim())
    .filter(Boolean);
}

export function formatArrayAsCommaSeparated(value) {
  return parseCommaSeparatedToArray(value).join(", ");
}

export function parseSingleTopic(value) {
  if (Array.isArray(value)) {
    return String(value[0] ?? "").trim();
  }
  if (value == null || value === "") {
    return "";
  }
  return String(value).split(",")[0].trim();
}

export function formatSingleTopic(value) {
  return parseSingleTopic(value);
}

export function applySemanticListFieldsToCubePayload(formData) {
  if (!formData?.cubes?.length) {
    return formData;
  }
  return {
    ...formData,
    cubes: formData.cubes.map((cube) => ({
      ...cube,
      domainName: parseCommaSeparatedToArray(cube.domainName), 
      dimensions: (cube.dimensions || []).map((dimension) => ({
        ...dimension,
        hierarchies: (dimension.hierarchies || []).map((hierarchy) => ({
          ...hierarchy,
          levels: (hierarchy.levels || []).map((level) => ({
            ...level,
            synonyms: parseCommaSeparatedToArray(level.synonyms),
            topic: parseCommaSeparatedToArray(level.topic),
          })),
        })),
      })),
      measures: (cube.measures || []).map((measure) => ({
        ...measure,
        synonyms: parseCommaSeparatedToArray(measure.synonyms),
        topic: parseCommaSeparatedToArray(measure.topic),
      })),
    })),
  };
}
