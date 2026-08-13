import { useState } from "react";
import { Form, Input } from "antd";
import { cloneDeep } from "lodash";
import {
  UiFormDrawer,
  UiFormGenerator,
  buildInitialValuesFromLayout,
  getNestedValue,
} from "../../../common/ui-generator";
import {
  DEFAULT_LAYOUT_CONTENT_ID,
  fetchSystemScheduleLayout,
  runSystemScheduleAction,
  toLayoutContentId,
} from "./system-schedule.utils";

const CRON_HELP =
  "Quartz cron with 6 fields: second minute hour day-of-month month day-of-week.\n\n" +
  "Examples:\n" +
  "• 0 0 12 * * ? — every day at noon\n" +
  "• 0 0/5 * * * ? — every 5 minutes\n" +
  "• 0 0 9 ? * MON-FRI — weekdays at 09:00\n" +
  "• 0 0 0 1 * ? — first day of each month at midnight\n\n" +
  "Special characters: * any, ? no specific value, - range, / increment, , list.";

const EXPIRE_DATE_HELP =
  "Allowed values:\n" +
  "• never — the schedule does not expire\n" +
  "• yyyy-MM-dd'T'HH:mm:ss — expire at a specific date and time, for example 2026-12-31T23:59:59";

const FIELD_HELP = {
  id: "Unique identifier for this system schedule.",
  enabled: "When ticked, this schedule is active and can run.",
  paused: "When ticked, the schedule is kept but will not run until resumed.",
  expireDate: EXPIRE_DATE_HELP,
  cron: CRON_HELP,
  scheduledTime: "Time of day in HH:mm:ss, for example 09:30:00.",
  timeZone: "Optional time zone ID, for example Asia/Kolkata. Leave blank to use the server default.",
  "task.class": "Fully qualified Java class that implements the scheduled task.",
  "task.script": "Groovy script file name, for example cleanup.groovy.",
  "task.function": "Method to invoke in the Groovy script. Defaults to execute.",
};

const TASK_TYPE_FIELD = {
  name: "taskType",
  label: "Task type",
  type: "radio",
  span: 24,
  options: [
    { label: "Java", value: "java" },
    { label: "Groovy", value: "groovy" },
  ],
  description: "Choose whether this schedule runs a Java class or a Groovy script.",
};

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
  width: 720,
  sections: [
    {
      key: "identity",
      title: "Identity",
      fields: [
        {
          name: "id",
          label: "Schedule ID",
          type: "text",
          required: true,
          readOnlyOnEdit: true,
          span: 24,
          description: FIELD_HELP.id,
        },
        {
          name: "enabled",
          label: "Enabled",
          type: "boolean",
          span: 12,
          description: FIELD_HELP.enabled,
        },
        {
          name: "paused",
          label: "Paused",
          type: "boolean",
          span: 12,
          description: FIELD_HELP.paused,
        },
      ],
    },
    {
      key: "schedule",
      title: "Schedule",
      fields: [
        {
          name: "expireDate",
          label: "Expire date",
          type: "text",
          span: 12,
          placeholder: "never or 2026-12-31T23:59:59",
          description: FIELD_HELP.expireDate,
        },
        {
          name: "scheduledTime",
          label: "Scheduled time",
          type: "text",
          span: 12,
          placeholder: "HH:mm:ss",
          description: FIELD_HELP.scheduledTime,
        },
        {
          name: "cron",
          label: "Cron expression",
          type: "text",
          span: 16,
          placeholder: "0 0 12 * * ?",
          description: FIELD_HELP.cron,
        },
        {
          name: "timeZone",
          label: "Time zone",
          type: "text",
          span: 8,
          placeholder: "Asia/Kolkata",
          description: FIELD_HELP.timeZone,
        },
      ],
    },
    {
      key: "task",
      title: "Task",
      fields: [
        TASK_TYPE_FIELD,
        {
          name: "task.class",
          label: "Java class",
          type: "text",
          span: 24,
          placeholder: "com.example.MyTask",
          description: FIELD_HELP["task.class"],
          visibleWhen: { field: "taskType", equals: "java" },
        },
        {
          name: "task.script",
          label: "Groovy script",
          type: "text",
          span: 16,
          placeholder: "cleanup.groovy",
          description: FIELD_HELP["task.script"],
          visibleWhen: { field: "taskType", equals: "groovy" },
        },
        {
          name: "task.function",
          label: "Function",
          type: "text",
          span: 8,
          placeholder: "execute",
          description: FIELD_HELP["task.function"],
          visibleWhen: { field: "taskType", equals: "groovy" },
        },
      ],
    },
    {
      key: "email",
      title: "Email notification",
      fields: [
        {
          name: "email.enabled",
          label: "Send email",
          type: "boolean",
          span: 8,
          description: "When ticked, an email is sent after the schedule runs.",
        },
        {
          name: "email.to",
          label: "To",
          type: "text",
          span: 16,
          placeholder: "Leave blank to use systemadmin",
        },
      ],
    },
  ],
};

const enhanceSystemScheduleLayout = (layout) => {
  const next = cloneDeep(layout && layout.sections ? layout : FALLBACK_LAYOUT);
  const walk = (sections = []) => {
    sections.forEach((section) => {
      const key = String(section.key || section.title || "").toLowerCase();
      if (key === "task") {
        const extras = (section.fields || []).filter((field) => {
          const name = field?.name;
          return (
            name &&
            name !== "taskType" &&
            name !== "task.class" &&
            name !== "task.script" &&
            name !== "task.function"
          );
        });
        const classField = (section.fields || []).find((field) => field.name === "task.class");
        const scriptField = (section.fields || []).find((field) => field.name === "task.script");
        const functionField = (section.fields || []).find(
          (field) => field.name === "task.function"
        );
        section.fields = [
          TASK_TYPE_FIELD,
          {
            ...(classField || {}),
            name: "task.class",
            label: classField?.label || "Java class",
            type: classField?.type || "text",
            span: 24,
            placeholder: classField?.placeholder || "com.example.MyTask",
            description: classField?.description || FIELD_HELP["task.class"],
            visibleWhen: { field: "taskType", equals: "java" },
          },
          {
            ...(scriptField || {}),
            name: "task.script",
            label: scriptField?.label || "Groovy script",
            type: scriptField?.type || "text",
            span: 16,
            placeholder: scriptField?.placeholder || "cleanup.groovy",
            description: scriptField?.description || FIELD_HELP["task.script"],
            visibleWhen: { field: "taskType", equals: "groovy" },
          },
          {
            ...(functionField || {}),
            name: "task.function",
            label: functionField?.label || "Function",
            type: functionField?.type || "text",
            span: 8,
            placeholder: functionField?.placeholder || "execute",
            description: functionField?.description || FIELD_HELP["task.function"],
            visibleWhen: { field: "taskType", equals: "groovy" },
          },
          ...extras,
        ];
      }
      (section.fields || []).forEach((field) => {
        if (FIELD_HELP[field.name] && !field.description && !field.help) {
          field.description = FIELD_HELP[field.name];
        }
        if (field.name === "cron" && !field.placeholder) {
          field.placeholder = "0 0 12 * * ?";
        }
        if (field.name === "expireDate" && !field.placeholder) {
          field.placeholder = "never or 2026-12-31T23:59:59";
        }
      });
      if (Array.isArray(section.sections) && section.sections.length) {
        walk(section.sections);
      }
    });
  };
  walk(next.sections || []);
  return next;
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
  if (!initial.taskType) {
    initial.taskType = initial["task.class"] ? "java" : "groovy";
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
  if (values.taskType === "java") {
    if (values["task.class"]) {
      schedule.task.class = values["task.class"];
    }
  } else if (values["task.script"]) {
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
    "taskType",
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
    const resolved = enhanceSystemScheduleLayout(
      layout && layout.sections ? layout : FALLBACK_LAYOUT
    );
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
      >
        <UiFormGenerator
          form={form}
          layout={formLayout}
          isAdd={isAdd}
          dense
          className="system-schedule-form"
          formProps={{
            layout: "horizontal",
            labelAlign: "left",
            colon: false,
            labelCol: { flex: "140px" },
            wrapperCol: { flex: 1 },
          }}
        />
      </UiFormDrawer>

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
