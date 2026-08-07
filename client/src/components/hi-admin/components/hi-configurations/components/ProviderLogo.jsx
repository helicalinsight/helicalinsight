import { ApiOutlined } from "@ant-design/icons";
import {
  PROVIDER_SVG_ALIASES,
  PROVIDER_SVG_MAP,
} from "./ProviderSvg";

/** Populated from InstantBI ui layout (provider.ui.layout.json). */
let providerLabels = {};
let providerPackages = {};

const normalizeProviderKey = (name = "") =>
  String(name)
    .trim()
    .toLowerCase()
    .replace(/^langchain[-_]/i, "")
    .replace(/[\s_]+/g, "-");

/**
 * Build a label map from layout JSON:
 * - top-level `providerLabels` object, and/or
 * - `provider` field `options` ({ label, value }).
 */
export const labelsFromProviderLayout = (layout = {}) => {
  const labels = { ...(layout.providerLabels || {}) };

  (layout.sections || []).forEach((section) => {
    (section.fields || []).forEach((field) => {
      if (field.name !== "provider") return;
      (field.options || []).forEach((option) => {
        const value = option?.value ?? option?.key;
        if (value == null || value === "") return;
        const key = normalizeProviderKey(value);
        if (!key || labels[key]) return;
        labels[key] = option.label || String(value);
      });
    });
  });

  return labels;
};

/** Build provider-id → langchain package map from layout JSON. */
export const packagesFromProviderLayout = (layout = {}) => {
  const packages = { ...(layout.providerPackages || {}) };
  return Object.fromEntries(
    Object.entries(packages)
      .map(([key, pkg]) => [normalizeProviderKey(key), String(pkg || "").trim()])
      .filter(([key, pkg]) => key && pkg)
  );
};

/** Register labels loaded from InstantBI ui.json / layout files. */
export const setProviderLabels = (labels = {}) => {
  const next = {};
  Object.entries(labels || {}).forEach(([key, label]) => {
    const normalized = normalizeProviderKey(key);
    if (!normalized || label == null || label === "") return;
    next[normalized] = String(label);
  });
  providerLabels = next;
};

/** Register provider → package mappings from InstantBI layout. */
export const setProviderPackages = (packages = {}) => {
  const next = {};
  Object.entries(packages || {}).forEach(([key, pkg]) => {
    const normalized = normalizeProviderKey(key);
    if (!normalized || !pkg) return;
    next[normalized] = String(pkg).trim();
  });
  providerPackages = next;
};

export const getProviderLabels = () => ({ ...providerLabels });
export const getProviderPackages = () => ({ ...providerPackages });

/**
 * Resolve LangChain package for a provider id.
 * Uses layout providerPackages when present; otherwise langchain-<provider>.
 */
export const resolvePackageFromProvider = (provider) => {
  const name = String(provider || "").trim();
  if (!name) return "";
  if (name.startsWith("langchain-")) return name;
  const key = normalizeProviderKey(name);
  return providerPackages[key] || `langchain-${key}`;
};

const titleCaseProvider = (providerName) => {
  const raw = String(providerName || "").trim();
  if (!raw) return "Provider";
  return raw
    .replace(/^langchain[-_]/i, "")
    .split(/[-_]/)
    .map((part) => (part ? part.charAt(0).toUpperCase() + part.slice(1) : part))
    .join(" ");
};

/**
 * Merge a newly saved provider into provider.ui.layout.json shape
 * (labels, packages, select options). Does not invent SVG logos —
 * ProviderLogo falls back to the default icon until an SVG is added.
 */
export const registerProviderInLayout = (
  layout = {},
  { provider, package: pkg, label } = {}
) => {
  const key = normalizeProviderKey(provider);
  if (!key) return cloneLayout(layout);

  const next = cloneLayout(layout);
  const resolvedPackage =
    (pkg && String(pkg).trim()) ||
    next.providerPackages?.[key] ||
    `langchain-${key}`;
  const display =
    (label && String(label).trim()) ||
    next.providerLabels?.[key] ||
    titleCaseProvider(provider);

  next.providerLabels = { ...(next.providerLabels || {}), [key]: display };
  next.providerPackages = {
    ...(next.providerPackages || {}),
    [key]: resolvedPackage,
  };

  (next.sections || []).forEach((section) => {
    (section.fields || []).forEach((field) => {
      if (field.name !== "provider") return;
      const options = Array.isArray(field.options) ? [...field.options] : [];
      const exists = options.some(
        (option) => normalizeProviderKey(option?.value ?? option?.key) === key
      );
      if (!exists) {
        options.push({ label: display, value: key });
        options.sort((a, b) =>
          String(a.label || a.value).localeCompare(String(b.label || b.value))
        );
      }
      field.options = options;
    });
  });

  return next;
};

const cloneLayout = (layout) => {
  try {
    return JSON.parse(JSON.stringify(layout || { sections: [] }));
  } catch (_err) {
    return { sections: [] };
  }
};

const resolveProviderKey = (providerName) => {
  const key = normalizeProviderKey(providerName);
  if (!key) return null;
  return PROVIDER_SVG_ALIASES[key] || key;
};

export const displayProviderName = (providerName) => {
  const exact = normalizeProviderKey(providerName);
  if (exact && providerLabels[exact]) return providerLabels[exact];

  const key = resolveProviderKey(providerName);
  if (key && providerLabels[key]) return providerLabels[key];

  return titleCaseProvider(providerName);
};

/**
 * Brand logo for an LLM provider (datasource-style inline React SVG).
 * Match by provider name via PROVIDER_SVG_MAP / PROVIDER_SVG_ALIASES in ProviderSvg.jsx.
 * Display names come from InstantBI provider.ui.layout.json (via setProviderLabels).
 */
const ProviderLogo = ({ provider, size = 40, className = "" }) => {
  const key = resolveProviderKey(provider);
  const Svg = (key && PROVIDER_SVG_MAP[key]) || null;

  if (!Svg) {
    return (
      <span
        className={`instantbi-provider-logo instantbi-provider-logo--fallback ${className}`}
        style={{ width: size, height: size, fontSize: Math.round(size * 0.55) }}
        aria-hidden
      >
        <ApiOutlined />
      </span>
    );
  }

  return (
    <span
      className={`instantbi-provider-logo ${className}`}
      style={{ width: size, height: size, display: "inline-flex" }}
      aria-hidden
    >
      <Svg size={size} />
    </span>
  );
};

export default ProviderLogo;
