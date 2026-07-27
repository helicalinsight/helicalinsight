import { useState } from "react";
import { Form, Input } from "antd";
import {
  UiFormDrawer,
  buildInitialValuesFromLayout,
  getNestedValue,
} from "../../../common/ui-generator";
import {
  DEFAULT_LAYOUT_CONTENT_ID,
  fetchSystemScheduleLayout,
  runSystemScheduleAction,
  toLayoutContentId,
} from "./system-schedule.utils";

const NEW_SCHEDULE_TEMPLATE = {
  id: "",
  layout: DEFAULT_LAYOUT_CONTENT_ID,
  enabled: true,
  paused: false,
  expireDate: "never",
  cron: "",
  scheduledTime: "00:00:00",
  timeZone: "",
  task: {
    script: "",
    function: "execute",
  },
  email: {
    enabled: true,
    to: "",
  },
};

const FALLBACK_LAYOUT = {
  id: "system-schedule.default",
  title: "System Schedule",
  description: "Configure identity, timing, task, and email notification",
  width: 860,
  sections: [
    {
      key: "identity",
      title: "Identity",
      fields: [
        { name: "id", label: "Schedule Id", type: "text", required: true, readOnlyOnEdit: true, span: 24 },
        { name: "enabled", label: "Enabled", type: "boolean", span: 12 },
        { name: "paused", label: "Paused", type: "boolean", span: 12 },
      ],
    },
    {
      key: "schedule",
      title: "Schedule",
      fields: [
        {
          name: "expireDate",
          label: "Expire Date",
          type: "text",
          span: 12,
          placeholder: "never or yyyy-MM-dd'T'HH:mm:ss",
        },
        { name: "scheduledTime", label: "Scheduled Time", type: "text", span: 12, placeholder: "HH:mm:ss" },
        { name: "cron", label: "Cron Expression", type: "text", span: 16 },
        { name: "timeZone", label: "Time Zone", type: "text", span: 8 },
      ],
    },
    {
      key: "task",
      title: "Task",
      fields: [
        { name: "task.class", label: "Java Class", type: "text", span: 24 },
        { name: "task.script", label: "Groovy Script", type: "text", span: 16 },
        { name: "task.function", label: "Function", type: "text", span: 8 },
      ],
    },
    {
      key: "email",
      title: "Email Notification",
      fields: [
        { name: "email.enabled", label: "Send Email", type: "boolean", span: 8 },
        { name: "email.to", label: "To", type: "text", span: 16, placeholder: "blank = systemadmin" },
      ],
    },
  ],
};

const buildScheduleInitialValues = (record, isAdd, layout) => {
  const source = isAdd ? NEW_SCHEDULE_TEMPLATE : record || {};
  const initial = buildInitialValuesFromLayout(source, layout);

  if (!isAdd) {
    Object.keys(source.config || {}).forEach((key) => {
      if (initial[key] === undefined) {
        initial[key] = source.config[key];
      }
    });
  }

  if (initial.id === undefined || initial.id === "") {
    initial.id = source.id || source.jobId || "";
  }
  if (initial.enabled === undefined) {
    initial.enabled = source.enabled !== false;
  }
  if (initial.paused === undefined) {
    initial.paused = !!source.paused;
  }
  if (!initial.expireDate) {
    initial.expireDate = source.expireDate || "never";
  }
  if (!initial.scheduledTime) {
    initial.scheduledTime = source.scheduledTime || "00:00:00";
  }
  if (initial["email.enabled"] === undefined) {
    initial["email.enabled"] = getNestedValue(source, "email.enabled") !== false;
  }
  if (initial["email.to"] === undefined) {
    initial["email.to"] = getNestedValue(source, "email.to") || "";
  }
  if (initial["task.class"] === undefined) {
    initial["task.class"] = getNestedValue(source, "task.class") || "";
  }
  if (initial["task.script"] === undefined) {
    initial["task.script"] = getNestedValue(source, "task.script") || "";
  }
  if (initial["task.function"] === undefined) {
    initial["task.function"] = getNestedValue(source, "task.function") || "execute";
  }

  return initial;
};

const valuesToSchedule = (values, selectedRecord, isAdd, layoutId) => {
  const schedule = {
    id: values.id,
    layout: toLayoutContentId(layoutId || selectedRecord?.layout || DEFAULT_LAYOUT_CONTENT_ID),
    enabled: !!values.enabled,
    paused: !!values.paused,
    expireDate: values.expireDate || "never",
    cron: values.cron || "",
    scheduledTime: values.scheduledTime || "00:00:00",
    timeZone: values.timeZone || "",
    task: {},
    email: {
      enabled: values["email.enabled"] !== false,
      to: values["email.to"] || "",
    },
  };
  if (values["task.class"]) {
    schedule.task.class = values["task.class"];
  }
  if (values["task.script"]) {
    schedule.task.script = values["task.script"];
    schedule.task.function = values["task.function"] || "execute";
  }
  const skipKeys = new Set([
    "id",
    "layout",
    "enabled",
    "paused",
    "expireDate",
    "cron",
    "scheduledTime",
    "timeZone",
    "task.class",
    "task.script",
    "task.function",
    "email.enabled",
    "email.to",
  ]);
  Object.keys(values).forEach((key) => {
    if (!skipKeys.has(key) && values[key] !== undefined) {
      schedule[key] = values[key];
    }
  });
  if (!isAdd && selectedRecord) {
    Object.keys(selectedRecord.config || {}).forEach((key) => {
      if (schedule[key] === undefined) {
        schedule[key] = selectedRecord.config[key];
      }
    });
  }
  return schedule;
};

/**
 * Hook + drawer UI for system schedule add / edit / script / JSON.
 * Add/Edit form UI is generated via common/ui-generator from layout JSON.
 */
export const useSystemScheduleEditors = ({ dispatch, Notify, onSaved }) => {
  const [form] = Form.useForm();
  const [saving, setSaving] = useState(false);
  const [editVisible, setEditVisible] = useState(false);
  const [scriptVisible, setScriptVisible] = useState(false);
  const [jsonVisible, setJsonVisible] = useState(false);
  const [isAdd, setIsAdd] = useState(false);
  const [selectedRecord, setSelectedRecord] = useState(null);
  const [formLayout, setFormLayout] = useState(FALLBACK_LAYOUT);
  const [layoutId, setLayoutId] = useState(DEFAULT_LAYOUT_CONTENT_ID);
  const [scriptContent, setScriptContent] = useState("");
  const [scriptName, setScriptName] = useState("");
  const [jsonContent, setJsonContent] = useState("[]");

  const applyLayoutAndOpen = (layout, record, addMode) => {
    const resolved = layout && layout.sections ? layout : FALLBACK_LAYOUT;
    setFormLayout(resolved);
    setLayoutId(
      toLayoutContentId(
        resolved.contentId || resolved.id || record?.layout || DEFAULT_LAYOUT_CONTENT_ID
      )
    );
    setIsAdd(addMode);
    setSelectedRecord(record);
    form.setFieldsValue(buildScheduleInitialValues(record || NEW_SCHEDULE_TEMPLATE, addMode, resolved));
    setEditVisible(true);
  };

  const openAdd = () => {
    setSaving(true);
    fetchSystemScheduleLayout({
      dispatch,
      layout: DEFAULT_LAYOUT_CONTENT_ID,
      onSuccess: (res) => {
        setSaving(false);
        applyLayoutAndOpen(res || FALLBACK_LAYOUT, NEW_SCHEDULE_TEMPLATE, true);
      },
      onError: () => {
        setSaving(false);
        applyLayoutAndOpen(FALLBACK_LAYOUT, NEW_SCHEDULE_TEMPLATE, true);
      },
    });
  };

  const openEdit = (record) => {
    setSaving(true);
    fetchSystemScheduleLayout({
      dispatch,
      layout: record?.layout || DEFAULT_LAYOUT_CONTENT_ID,
      onSuccess: (res) => {
        setSaving(false);
        applyLayoutAndOpen(res || FALLBACK_LAYOUT, record, false);
      },
      onError: () => {
        setSaving(false);
        applyLayoutAndOpen(FALLBACK_LAYOUT, record, false);
      },
    });
  };

  const openScriptEditor = (record) => {
    setSelectedRecord(record);
    setSaving(true);
    runSystemScheduleAction({
      dispatch,
      formData: { action: "getScript", id: record.jobId || record.id },
      onSuccess: (res) => {
        setSaving(false);
        setScriptName(res?.script || getNestedValue(record, "task.script") || "");
        setScriptContent(res?.content || "");
        setScriptVisible(true);
      },
      onError: () => setSaving(false),
    });
  };

  const openJsonEditor = () => {
    setSaving(true);
    runSystemScheduleAction({
      dispatch,
      formData: { action: "getJson" },
      onSuccess: (res) => {
        setSaving(false);
        setJsonContent(res?.content || "[]");
        setJsonVisible(true);
      },
      onError: () => setSaving(false),
    });
  };

  const saveSchedule = () => {
    form.validateFields().then((values) => {
      if (!values.id) {
        Notify.error?.({ type: "System Schedule", message: "Id is required" });
        return;
      }
      const schedule = valuesToSchedule(values, selectedRecord, isAdd, layoutId);
      setSaving(true);
      runSystemScheduleAction({
        dispatch,
        formData: { action: "save", schedule },
        onSuccess: (res) => {
          setSaving(false);
          setEditVisible(false);
          if (res?.message) {
            Notify.success({ type: "System Schedule", message: res.message });
          }
          onSaved?.();
        },
        onError: () => setSaving(false),
      });
    });
  };

  const saveScript = () => {
    setSaving(true);
    runSystemScheduleAction({
      dispatch,
      formData: {
        action: "saveScript",
        id: selectedRecord?.jobId || selectedRecord?.id,
        script: scriptName,
        content: scriptContent,
      },
      onSuccess: (res) => {
        setSaving(false);
        setScriptVisible(false);
        if (res?.message) {
          Notify.success({ type: "System Schedule", message: res.message });
        }
        onSaved?.();
      },
      onError: () => setSaving(false),
    });
  };

  const saveJson = () => {
    setSaving(true);
    runSystemScheduleAction({
      dispatch,
      formData: { action: "saveJson", content: jsonContent },
      onSuccess: (res) => {
        setSaving(false);
        setJsonVisible(false);
        if (res?.message) {
          Notify.success({ type: "System Schedule", message: res.message });
        }
        onSaved?.();
      },
      onError: () => setSaving(false),
    });
  };

  const drawerTitle = isAdd
    ? formLayout?.title
      ? `Add ${formLayout.title}`
      : "Add System Schedule"
    : `Edit ${formLayout?.title || "Schedule"}${
        selectedRecord ? ` — ${selectedRecord.jobId || selectedRecord.id}` : ""
      }`;

  const editorsUi = (
    <>
      <UiFormDrawer
        visible={editVisible}
        onClose={() => setEditVisible(false)}
        onSave={saveSchedule}
        saving={saving}
        title={drawerTitle}
        description={formLayout?.description}
        layout={formLayout}
        form={form}
        isAdd={isAdd}
        className="my-drawer system-schedule-drawer"
      />

      <UiFormDrawer
        visible={scriptVisible}
        onClose={() => setScriptVisible(false)}
        onSave={saveScript}
        saving={saving}
        title={`Edit Script${scriptName ? ` - ${scriptName}` : ""}`}
        width={800}
        className="my-drawer system-schedule-drawer"
      >
        <Input.TextArea
          rows={18}
          value={scriptContent}
          onChange={(e) => setScriptContent(e.target.value)}
        />
      </UiFormDrawer>

      <UiFormDrawer
        visible={jsonVisible}
        onClose={() => setJsonVisible(false)}
        onSave={saveJson}
        saving={saving}
        title="Edit systemschedule.json"
        width={900}
        className="my-drawer system-schedule-drawer"
      >
        <Input.TextArea
          rows={20}
          value={jsonContent}
          onChange={(e) => setJsonContent(e.target.value)}
        />
      </UiFormDrawer>
    </>
  );

  return {
    editorsUi,
    openAdd,
    openEdit,
    openScriptEditor,
    openJsonEditor,
    saving,
  };
};
