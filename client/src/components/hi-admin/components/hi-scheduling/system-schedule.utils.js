import requests from "../../../../base/requests";
import { uriConfig } from "../../../../base/requests/admin.request";
import { fetchUiLayout } from "../../../common/ui-generator";

export const SYSTEM_SCHEDULE_URI = uriConfig.monitorSystemSystemSchedule;

export const SYSTEM_SCHEDULE_TYPE = "system";

export const friendlyScheduleName = (record) =>
  String(record?.title || record?.jobId || record?.id || "").trim();

export const scheduleDescription = (record) =>
  String(record?.description || "").trim();

export const DEFAULT_LAYOUT_CONTENT_ID = "Static/layout/system-schedule.default.ui.layout";

export const isSystemScheduleRecord = (record) =>
  !!(record?.systemSchedule || record?.type === SYSTEM_SCHEDULE_TYPE);

/**
 * Normalizes a layout ref to a Static/layout/*.ui.layout contentId
 * (getContents appends .json → *.ui.layout.json).
 */
export const toLayoutContentId = (layout) => {
  if (!layout || typeof layout !== "string") {
    return DEFAULT_LAYOUT_CONTENT_ID;
  }
  let value = layout.trim().replace(/\\/g, "/");
  if (value.endsWith(".ui.layout.json")) {
    value = value.slice(0, -5);
  } else if (value.endsWith(".json") || value.endsWith(".groovy")) {
    value = value.replace(/\.(json|groovy)$/, "");
  }
  if (!value.startsWith("Static/")) {
    if (value.startsWith("layout/")) {
      value = `Static/${value}`;
    } else {
      value = `Static/layout/${value}`;
    }
  }
  if (!value.endsWith(".ui.layout")) {
    value = `${value}.ui.layout`;
  }
  return value;
};

/**
 * Fetches system schedules in the same scheduledList shape as user schedules.
 */
export const fetchSystemScheduledList = ({
  dispatch,
  apiRef,
  onSuccess = () => {},
  onError = () => {},
}) => {
  const request = requests.admin(dispatch).postAdminRequest(
    { action: "list" },
    SYSTEM_SCHEDULE_URI,
    (res) => {
      const list = Array.isArray(res?.scheduledList) ? res.scheduledList : [];
      onSuccess(list, res);
    },
    onError
  );
  if (apiRef) {
    apiRef.current = request;
  }
  return request;
};

/**
 * Loads a form layout via the shared ui-generator Static content loader.
 */
export const fetchSystemScheduleLayout = ({
  dispatch,
  layout = DEFAULT_LAYOUT_CONTENT_ID,
  onSuccess = () => {},
  onError = () => {},
}) =>
  fetchUiLayout({
    dispatch,
    contentId: toLayoutContentId(layout),
    onSuccess,
    onError,
  });

/**
 * Runs a system schedule action (trigger/pause/resume/enable/disable/delete/reload/save).
 */
export const runSystemScheduleAction = ({
  dispatch,
  formData,
  onSuccess = () => {},
  onError = () => {},
}) => {
  return requests.admin(dispatch).postAdminRequest(
    formData,
    SYSTEM_SCHEDULE_URI,
    onSuccess,
    onError
  );
};
