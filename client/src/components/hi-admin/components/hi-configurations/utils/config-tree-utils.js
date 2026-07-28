import { cloneDeep } from "lodash";
import parser from "xml-js";

export const CONFIG_TYPES = {
  PROPERTIES: "properties",
  XML: "xml",
  JSON: "json",
  OTHER: "other",
};

export const getFileTypeLabel = (type) => {
  switch (type) {
    case CONFIG_TYPES.PROPERTIES:
      return "Properties";
    case CONFIG_TYPES.XML:
      return "XML";
    case CONFIG_TYPES.JSON:
      return "JSON";
    default:
      return "File";
  }
};

const makeNodeKey = (prefix, segment) => `${prefix}/${segment}`;

/**
 * Converts a JSON value into Ant Design Tree data nodes.
 */
export const jsonToTreeData = (value, path = "root") => {
  if (value === null || value === undefined) {
    return [
      {
        key: path,
        title: "null",
        isLeaf: true,
        data: { path, kind: "null", value: null },
      },
    ];
  }

  if (Array.isArray(value)) {
    return value.map((item, index) => {
      const childPath = makeNodeKey(path, index);
      const isComplex = item !== null && typeof item === "object";
      return {
        key: childPath,
        title: `[${index}]`,
        isLeaf: !isComplex,
        data: {
          path: childPath,
          kind: Array.isArray(item) ? "array" : isComplex ? "object" : typeof item,
          value: item,
          parentPath: path,
          index,
        },
        children: isComplex ? jsonToTreeData(item, childPath) : undefined,
      };
    });
  }

  if (typeof value === "object") {
    return Object.keys(value).map((key) => {
      const childPath = makeNodeKey(path, key);
      const childValue = value[key];
      const isComplex = childValue !== null && typeof childValue === "object";
      return {
        key: childPath,
        title: key,
        isLeaf: !isComplex,
        data: {
          path: childPath,
          kind: Array.isArray(childValue)
            ? "array"
            : isComplex
            ? "object"
            : typeof childValue,
          value: childValue,
          parentPath: path,
          propertyName: key,
        },
        children: isComplex ? jsonToTreeData(childValue, childPath) : undefined,
      };
    });
  }

  return [
    {
      key: path,
      title: String(value),
      isLeaf: true,
      data: { path, kind: typeof value, value },
    },
  ];
};

const parsePathSegments = (path) =>
  path
    .replace(/^root\/?/, "")
    .split("/")
    .filter((segment) => segment !== "");

export const getValueAtPath = (root, path) => {
  if (!path || path === "root") return root;
  const segments = parsePathSegments(path);
  let cursor = root;
  for (const segment of segments) {
    if (cursor == null) return undefined;
    const key = Array.isArray(cursor) && /^\d+$/.test(segment) ? Number(segment) : segment;
    cursor = cursor[key];
  }
  return cursor;
};

export const setValueAtPath = (root, path, nextValue) => {
  const draft = cloneDeep(root);
  if (!path || path === "root") {
    return nextValue;
  }
  const segments = parsePathSegments(path);
  let cursor = draft;
  for (let i = 0; i < segments.length - 1; i += 1) {
    const segment = segments[i];
    const key = Array.isArray(cursor) && /^\d+$/.test(segment) ? Number(segment) : segment;
    cursor = cursor[key];
  }
  const last = segments[segments.length - 1];
  const lastKey = Array.isArray(cursor) && /^\d+$/.test(last) ? Number(last) : last;
  cursor[lastKey] = nextValue;
  return draft;
};

export const deleteValueAtPath = (root, path) => {
  if (!path || path === "root") {
    return Array.isArray(root) ? [] : {};
  }
  const draft = cloneDeep(root);
  const segments = parsePathSegments(path);
  let cursor = draft;
  for (let i = 0; i < segments.length - 1; i += 1) {
    const segment = segments[i];
    const key = Array.isArray(cursor) && /^\d+$/.test(segment) ? Number(segment) : segment;
    cursor = cursor[key];
  }
  const last = segments[segments.length - 1];
  if (Array.isArray(cursor) && /^\d+$/.test(last)) {
    cursor.splice(Number(last), 1);
  } else {
    delete cursor[last];
  }
  return draft;
};

export const addChildAtPath = (root, path, { key, value }) => {
  const draft = cloneDeep(root);
  const parentRef =
    path && path !== "root"
      ? (() => {
          const segments = parsePathSegments(path);
          let cursor = draft;
          for (const segment of segments) {
            const segKey =
              Array.isArray(cursor) && /^\d+$/.test(segment) ? Number(segment) : segment;
            cursor = cursor[segKey];
          }
          return cursor;
        })()
      : draft;

  if (Array.isArray(parentRef)) {
    parentRef.push(value);
    return draft;
  }

  if (parentRef == null || typeof parentRef !== "object") {
    throw new Error("Parent is not an object");
  }
  if (!key) {
    throw new Error("Property name is required");
  }
  parentRef[key] = value;
  return draft;
};

export const coercePrimitive = (raw, valueType) => {
  switch (valueType) {
    case "number": {
      const num = Number(raw);
      return Number.isNaN(num) ? 0 : num;
    }
    case "boolean":
      return String(raw).toLowerCase() === "true";
    case "null":
      return null;
    case "object":
      return {};
    case "array":
      return [];
    default:
      return raw == null ? "" : String(raw);
  }
};

const getElementByIndexPath = (document, indexPath = []) => {
  let cursor = document;
  for (const index of indexPath) {
    if (!cursor?.elements?.[index]) return null;
    cursor = cursor.elements[index];
  }
  return cursor;
};

/**
 * Build Ant Design tree nodes from xml-js (non-compact) structure using index paths.
 */
export const xmlJsToTreeData = (document) => {
  const walk = (element, indexPath, keyPrefix) => {
    if (!element || (element.type && element.type !== "element")) {
      return [];
    }
    const name = element.name || "element";
    const nodeKey = `${keyPrefix}/${name}[${indexPath.join(".") || "root"}]`;

    const attributeNodes = Object.entries(element.attributes || {}).map(([attr, value]) => ({
      key: `${nodeKey}/@${attr}`,
      title: `@${attr}`,
      isLeaf: true,
      data: {
        kind: "attribute",
        name: attr,
        value: String(value ?? ""),
        indexPath,
      },
    }));

    const childNodes = [];
    (element.elements || []).forEach((child, index) => {
      const childPath = [...indexPath, index];
      if (child.type === "text" || child.type === "cdata") {
        childNodes.push({
          key: `${nodeKey}/text-${index}`,
          title: String(child.text ?? child.cdata ?? ""),
          isLeaf: true,
          data: {
            kind: "text",
            value: String(child.text ?? child.cdata ?? ""),
            indexPath: childPath,
            parentIndexPath: indexPath,
            textIndex: index,
          },
        });
      } else if (child.type === "element" || child.name) {
        childNodes.push(...walk(child, childPath, nodeKey));
      }
    });

    return [
      {
        key: nodeKey,
        title: name,
        isLeaf: attributeNodes.length === 0 && childNodes.length === 0,
        data: {
          kind: "element",
          name,
          indexPath,
        },
        children: [...attributeNodes, ...childNodes],
      },
    ];
  };

  const roots = [];
  (document?.elements || []).forEach((element, index) => {
    roots.push(...walk(element, [index], "root"));
  });
  return roots;
};

export const parseXmlToTree = (xmlContent) => {
  const document = parser.xml2js(xmlContent || "", { compact: false });
  return {
    document,
    treeData: xmlJsToTreeData(document),
  };
};

export const updateXmlNodeValue = (document, nodeData, nextValue) => {
  const draft = cloneDeep(document);
  if (nodeData.kind === "attribute") {
    const element = getElementByIndexPath(draft, nodeData.indexPath);
    if (element) {
      if (!element.attributes) element.attributes = {};
      element.attributes[nodeData.name] = nextValue;
    }
    return draft;
  }
  if (nodeData.kind === "text") {
    const parent = getElementByIndexPath(draft, nodeData.parentIndexPath);
    const textNode = parent?.elements?.[nodeData.textIndex];
    if (textNode) {
      if (textNode.type === "cdata") {
        textNode.cdata = nextValue;
      } else {
        textNode.type = "text";
        textNode.text = nextValue;
      }
    }
    return draft;
  }
  return draft;
};

export const xmlDocumentToString = (document) =>
  parser.js2xml(document, { compact: false, spaces: 2 });

export const filterTreeBySearch = (nodes, searchValue) => {
  if (!searchValue) {
    return { treeData: nodes, expandedKeys: [] };
  }
  const lower = searchValue.toLowerCase();
  const expandedKeys = [];

  const filterNodes = (list = []) =>
    list
      .map((node) => {
        const titleText = String(node.title ?? "").toLowerCase();
        const valueText = String(node.data?.value ?? "").toLowerCase();
        const children = filterNodes(node.children || []);
        const matched = titleText.includes(lower) || valueText.includes(lower);
        if (matched || children.length) {
          expandedKeys.push(node.key);
          return { ...node, children };
        }
        return null;
      })
      .filter(Boolean);

  return { treeData: filterNodes(nodes), expandedKeys };
};

export const collectExpandableKeys = (nodes = [], acc = []) => {
  nodes.forEach((node) => {
    if (node.children?.length) {
      acc.push(node.key);
      collectExpandableKeys(node.children, acc);
    }
  });
  return acc;
};
