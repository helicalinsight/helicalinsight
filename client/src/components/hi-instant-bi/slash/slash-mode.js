import { createContext, useContext } from "react";

// Request mode string. Slash is not an Auto/Fast/Think dropdown option.
export const SLASH_MODE = "Slash";
// Default environment when the check URL does not pass env.
export const SLASH_ENVIRONMENT = "hreport";
// Environment when Slash is opened from the dashboard designer.
export const DASHBOARD_ENVIRONMENT = "dashboard";

// One flag for the chat tree so children do not take a slashMode prop.
export const SlashModeContext = createContext(false);
export const useSlashMode = () => useContext(SlashModeContext);

// True only when Slash is hosted in a Helical Report. The chart waits for Explore.
export const SlashExploreContext = createContext(false);
export const useSlashExploreOnActiveTab = () => useContext(SlashExploreContext);

// Accept mode=slash from the URL as well as the canonical "Slash" request value.
export const isSlashMode = (mode) => String(mode || "").toLowerCase() === SLASH_MODE.toLowerCase();

// #/instant-bi/ must match the Instant BI route.
const routePath = (path = "") => String(path || "").replace(/\/+$/, "") || "/";

/** Check URL: #/instant-bi?mode=slash&env=hreport */
export const readInstantBiSlashCheck = (pathname, search, instantBiPath = "/instant-bi") => {
  // Only the Instant BI hash route starts Slash. Other pages ignore the query.
  if (routePath(pathname) !== routePath(instantBiPath)) return null;
  return readSlashCheckQuery(search);
};

export const readSlashCheckQuery = (search = "") => {
  const query = String(search || "").replace(/^\?/, "");
  if (!query) return null;
  const params = new URLSearchParams(query);
  if (!isSlashMode(params.get("mode"))) return null;
  return {
    mode: SLASH_MODE,
    // Keep an explicit env from the URL; otherwise use hreport.
    environment: params.get("env") || SLASH_ENVIRONMENT,
  };
};

/** Request fields the Slash module adds when it calls Instant BI. Not a chat-mode option. */
export const applySlashMode = (fields = {}) => {
  if (!isSlashMode(fields.mode)) return fields;
  return {
    ...fields,
    mode: SLASH_MODE,
    environment: fields.environment || SLASH_ENVIRONMENT,
  };
};
