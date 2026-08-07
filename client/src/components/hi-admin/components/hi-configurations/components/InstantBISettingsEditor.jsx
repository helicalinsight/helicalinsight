import { useCallback, useEffect, useMemo, useState } from "react";
import {
  Button,
  Card,
  Col,
  Drawer,
  Empty,
  Form,
  Input,
  Row,
  Space,
  Spin,
  Tabs,
  Tooltip,
} from "antd";
import {
  CheckCircleFilled,
  MinusCircleOutlined,
  PlusCircleFilled,
  PlusOutlined,
  SearchOutlined,
  CloseOutlined,
  SyncOutlined,
} from "@ant-design/icons";
import { useDispatch } from "react-redux";
import { cloneDeep } from "lodash";
import {
  fetchUiLayout,
  UiFormGenerator,
} from "../../../../common/ui-generator";
import requests from "../../../../../base/requests";
import { uriConfig } from "../../../../../base/requests/instantbi.requests";
import { uriConfig as uriConfigAdmin } from "../../../../../base/requests/admin.request";
import notify from "../../../../hi-notifications/notify";
import ProviderLogo, {
  displayProviderName,
  labelsFromProviderLayout,
  packagesFromProviderLayout,
  registerProviderInLayout,
  resolvePackageFromProvider,
  setProviderLabels,
  setProviderPackages,
} from "./ProviderLogo";
import "./instantbi-settings-editor.scss";

const MANIFEST_CONTENT_ID = "Static/instantbi/instantbi-settings.ui";
const PROVIDER_LAYOUT_FILE = "provider.ui.layout.json";
const PROVIDER_LAYOUT_PATH = "Admin/Static/instantbi";

const utilityRequest = (dispatch, path, body = {}) =>
  new Promise((resolve, reject) => {
    requests.instantBI(dispatch).instantBIUtilityRequest({
      uri: path,
      formData: { body: JSON.stringify(body || {}) },
      callback: (res) => {
        const payload = res?.response != null ? res.response : res;
        if (payload?.status === 0) {
          const message = Array.isArray(payload.error)
            ? payload.error.join("; ")
            : payload.error || "InstantBI utility request failed";
          reject(new Error(message));
          return;
        }
        resolve(payload || {});
      },
      errback: (err) =>
        reject(new Error(err?.message || "InstantBI utility request failed")),
    });
  });

const adminWriteRequest = (dispatch, formData) =>
  new Promise((resolve, reject) => {
    requests.admin(dispatch).postAdminRequest(
      formData,
      uriConfigAdmin.monitorSystemReadWrite,
      (res) => resolve(res || {}),
      (err) =>
        reject(new Error(err?.message || "Failed to update provider UI layout"))
    );
  });

/** Persist layout without ephemeral model dropdown options. */
const layoutPayloadForWrite = (layout) => {
  const next = cloneDeep(layout || { sections: [] });
  (next.sections || []).forEach((section) => {
    (section.fields || []).forEach((field) => {
      if (field.name === "model") {
        field.options = [];
      }
    });
  });
  return next;
};

const coerceParameterValue = (raw) => {
  if (raw == null) return "";
  const text = String(raw).trim();
  if (text === "") return "";
  if (text === "true") return true;
  if (text === "false") return false;
  if (/^-?\d+(\.\d+)?$/.test(text)) {
    const num = Number(text);
    if (!Number.isNaN(num)) return num;
  }
  return text;
};

const parametersToEntries = (parameters = {}) =>
  Object.entries(parameters || {}).map(([key, value]) => ({
    key,
    value: value == null ? "" : String(value),
  }));

const entriesToParameters = (entries = []) => {
  const parameters = {};
  (entries || []).forEach((entry) => {
    const key = String(entry?.key || "").trim();
    if (!key) return;
    parameters[key] = coerceParameterValue(entry?.value);
  });
  return parameters;
};

const setFieldOptions = (layout, fieldName, options) => {
  const next = cloneDeep(layout || { sections: [] });
  (next.sections || []).forEach((section) => {
    (section.fields || []).forEach((field) => {
      if (field.name === fieldName) {
        field.options = options;
      }
    });
  });
  return next;
};

/**
 * InstantBI Settings editor.
 * Layout JSON from Admin/Static/instantbi is rendered with UiFormGenerator.
 * Save/load uses InstantBI utility APIs (not Admin file write).
 */
const InstantBISettingsEditor = () => {
  const dispatch = useDispatch();
  const Notify = notify(dispatch);

  const [providerForm] = Form.useForm();
  const [loggingForm] = Form.useForm();
  const [applicationForm] = Form.useForm();

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [modelsLoading, setModelsLoading] = useState(false);
  const [error, setError] = useState(null);
  const [manifest, setManifest] = useState(null);
  const [layouts, setLayouts] = useState({});
  const [providers, setProviders] = useState([]);
  const [activeTab, setActiveTab] = useState("llm");
  const [reloadKey, setReloadKey] = useState(0);
  const [providerDrawerOpen, setProviderDrawerOpen] = useState(false);
  const [editingProvider, setEditingProvider] = useState(null);
  const [providerSearchOpen, setProviderSearchOpen] = useState(false);
  const [providerFilter, setProviderFilter] = useState("");

  const endpoints = useMemo(
    () => ({
      settings: uriConfig.instantBIUtilitySettings,
      models: `${uriConfig.instantBIUtilityPrefix}/llm/models`,
      defaultProvider: `${uriConfig.instantBIUtilityPrefix}/llm/default-provider`,
      upsertProvider: `${uriConfig.instantBIUtilityPrefix}/llm/provider`,
      appConfig: `${uriConfig.instantBIUtilityPrefix}/app-config`,
      ...(manifest?.endpoints || {}),
    }),
    [manifest]
  );

  const loadLayout = useCallback(
    (contentId) =>
      new Promise((resolve, reject) => {
        fetchUiLayout({
          dispatch,
          contentId,
          onSuccess: (res) => resolve(res || null),
          onError: (err) => reject(err || new Error(`Failed to load ${contentId}`)),
        });
      }),
    [dispatch]
  );

  const applyBootstrap = useCallback(
    async (boot, layoutMap) => {
      const providerRows = boot.providers || boot.llm?.providers || [];
      setProviders(providerRows);
      setLayouts((prev) => ({ ...prev, ...layoutMap }));
      setProviderLabels(labelsFromProviderLayout(layoutMap.provider));
      setProviderPackages(packagesFromProviderLayout(layoutMap.provider));

      const config = boot.config || {};
      const logging = config.logging || {};
      const app = config.app || {};
      const kpi = config.kpi || {};
      const flags = config.feature_flags || {};
      const sql = config.sql || {};
      const apiCache = config.api_cache || {};

      loggingForm.setFieldsValue({
        level: logging.level || "INFO",
        backup_days: logging.backup_days ?? 14,
        file: logging.file || "logs/app.log",
        error_file: logging.error_file || "logs/error.log",
        show_llm_activity: !!logging.show_llm_activity,
        show_endpoint_log: !!logging.show_endpoint_log,
        show_api_call_log: !!logging.show_api_call_log,
        app_debug: !!app.debug,
      });

      applicationForm.setFieldsValue({
        suggestion_query: kpi.suggestion_query || "",
        enable_llm_usage_audit: !!flags.enable_llm_usage_audit,
        hide_prompt_reason: !!flags.hide_prompt_reason,
        enable_cache: !!flags.enable_cache,
        enable_streaming: !!flags.enable_streaming,
        enable_memory: !!flags.enable_memory,
        default_limit: sql.default_limit ?? 100,
        api_cache_enabled: !!apiCache.enabled,
        api_cache_max_entries: apiCache.max_entries ?? 100,
      });
    },
    [applicationForm, loggingForm]
  );

  useEffect(() => {
    let cancelled = false;

    const boot = async () => {
      setLoading(true);
      setError(null);
      try {
        const nextManifest = await loadLayout(MANIFEST_CONTENT_ID);
        if (cancelled) return;
        setManifest(nextManifest);

        const panels = nextManifest?.panels || [];
        const layoutEntries = await Promise.all(
          panels.map(async (panel) => {
            const layout = await loadLayout(panel.layout);
            return [panel.key, layout];
          })
        );
        if (cancelled) return;
        const layoutMap = Object.fromEntries(layoutEntries);

        const settings = await utilityRequest(
          dispatch,
          nextManifest?.endpoints?.settings || uriConfig.instantBIUtilitySettings,
          {}
        );
        if (cancelled) return;
        await applyBootstrap(settings, layoutMap);
        setLoading(false);
      } catch (err) {
        if (cancelled) return;
        setLoading(false);
        const message = err?.message || "Failed to load InstantBI settings";
        setError(message);
        Notify.error({ type: "InstantBI Settings", message });
      }
    };

    boot();
    return () => {
      cancelled = true;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [dispatch, loadLayout, applyBootstrap, reloadKey]);

  const loadModelsForPackage = useCallback(
    async (pkg, currentModel) => {
      if (!pkg) {
        setLayouts((prev) => ({
          ...prev,
          provider: setFieldOptions(prev.provider, "model", []),
        }));
        return { catalog_known: false, models: [] };
      }
      setModelsLoading(true);
      try {
        const modelsRes = await utilityRequest(dispatch, endpoints.models, {
          package: pkg,
        });
        const models = modelsRes.models || [];
        const options = models.map((model) => ({
          label: model,
          value: model,
        }));
        if (
          currentModel &&
          !options.some((option) => option.value === currentModel)
        ) {
          options.unshift({ label: currentModel, value: currentModel });
        }
        setLayouts((prev) => ({
          ...prev,
          provider: setFieldOptions(prev.provider, "model", options),
        }));
        // Unknown packages return an empty catalog — not an error.
        return {
          catalog_known: !!modelsRes.catalog_known,
          models,
        };
      } catch (err) {
        // Soft-fail: allow typing a custom model id for new providers.
        const fallback = currentModel
          ? [{ label: currentModel, value: currentModel }]
          : [];
        setLayouts((prev) => ({
          ...prev,
          provider: setFieldOptions(prev.provider, "model", fallback),
        }));
        return { catalog_known: false, models: [] };
      } finally {
        setModelsLoading(false);
      }
    },
    [dispatch, endpoints.models]
  );

  const persistProviderLayout = useCallback(
    async (provider, pkg) => {
      const nextLayout = registerProviderInLayout(layouts.provider, {
        provider,
        package: pkg,
        label: displayProviderName(provider),
      });
      setLayouts((prev) => ({ ...prev, provider: nextLayout }));
      setProviderLabels(labelsFromProviderLayout(nextLayout));
      setProviderPackages(packagesFromProviderLayout(nextLayout));
      try {
        await adminWriteRequest(dispatch, {
          action: "write",
          file: PROVIDER_LAYOUT_FILE,
          path: PROVIDER_LAYOUT_PATH,
          content: layoutPayloadForWrite(nextLayout),
        });
      } catch (_err) {
        // Provider save already succeeded; layout persistence is best-effort.
      }
    },
    [dispatch, layouts.provider]
  );

  const closeProviderDrawer = () => {
    setProviderDrawerOpen(false);
    setEditingProvider(null);
    providerForm.resetFields();
  };

  const openAddProvider = () => {
    setEditingProvider(null);
    providerForm.resetFields();
    providerForm.setFieldsValue({
      usage_path: "usage_metadata",
      set_as_default: false,
      extra_parameters: [{ key: "", value: "" }],
    });
    setLayouts((prev) => ({
      ...prev,
      provider: setFieldOptions(prev.provider, "model", []),
    }));
    setProviderDrawerOpen(true);
  };

  const openEditProvider = async (row) => {
    setEditingProvider(row?.provider || null);
    const params = row.parameters || {};
    providerForm.setFieldsValue({
      provider: row.provider,
      model: row.model,
      usage_path: row.usage_path || "usage_metadata",
      set_as_default: !!row.is_default,
      extra_parameters: parametersToEntries(params).length
        ? parametersToEntries(params)
        : [{ key: "", value: "" }],
    });
    setProviderDrawerOpen(true);
    await loadModelsForPackage(
      resolvePackageFromProvider(row.provider),
      row.model
    );
  };

  const saveProvider = async () => {
    const values = await providerForm.validateFields();
    const parameters = entriesToParameters(values.extra_parameters);
    const pkg = resolvePackageFromProvider(values.provider);

    setSaving(true);
    try {
      await utilityRequest(dispatch, endpoints.upsertProvider, {
        provider: values.provider,
        package: pkg,
        model: values.model,
        usage_path: values.usage_path || "usage_metadata",
        parameters,
        replace_parameters: true,
        set_as_default: !!values.set_as_default,
      });
      // Once installed (saved), register provider in InstantBI ui layout.
      await persistProviderLayout(values.provider, pkg);
      Notify.success({
        type: "InstantBI Settings",
        message: `Provider '${values.provider}' saved`,
      });
      closeProviderDrawer();
      setReloadKey((key) => key + 1);
    } catch (err) {
      Notify.error({
        type: "InstantBI Settings",
        message: err?.message || "Failed to save provider",
      });
    } finally {
      setSaving(false);
    }
  };

  const saveDefaultProvider = async (provider) => {
    setSaving(true);
    try {
      await utilityRequest(dispatch, endpoints.defaultProvider, { provider });
      Notify.success({
        type: "InstantBI Settings",
        message: `Default provider set to ${provider}`,
      });
      setReloadKey((key) => key + 1);
    } catch (err) {
      Notify.error({
        type: "InstantBI Settings",
        message: err?.message || "Failed to set default provider",
      });
    } finally {
      setSaving(false);
    }
  };

  const saveDeveloperSettings = async () => {
    const [loggingValues, applicationValues] = await Promise.all([
      loggingForm.validateFields(),
      applicationForm.validateFields(),
    ]);
    setSaving(true);
    try {
      await utilityRequest(dispatch, endpoints.appConfig, {
        logging: {
          level: loggingValues.level,
          backup_days: loggingValues.backup_days,
          file: loggingValues.file,
          error_file: loggingValues.error_file,
          show_llm_activity: !!loggingValues.show_llm_activity,
          show_endpoint_log: !!loggingValues.show_endpoint_log,
          show_api_call_log: !!loggingValues.show_api_call_log,
        },
        app: { debug: !!loggingValues.app_debug },
        kpi: { suggestion_query: applicationValues.suggestion_query },
        feature_flags: {
          enable_llm_usage_audit: !!applicationValues.enable_llm_usage_audit,
          hide_prompt_reason: !!applicationValues.hide_prompt_reason,
          enable_cache: !!applicationValues.enable_cache,
          enable_streaming: !!applicationValues.enable_streaming,
          enable_memory: !!applicationValues.enable_memory,
        },
        sql: { default_limit: applicationValues.default_limit },
        api_cache: {
          enabled: !!applicationValues.api_cache_enabled,
          max_entries: applicationValues.api_cache_max_entries,
        },
      });
      Notify.success({
        type: "InstantBI Settings",
        message: "Developer settings saved",
      });
      setReloadKey((key) => key + 1);
    } catch (err) {
      Notify.error({
        type: "InstantBI Settings",
        message: err?.message || "Failed to save developer settings",
      });
    } finally {
      setSaving(false);
    }
  };

  const filteredProviders = useMemo(() => {
    const query = String(providerFilter || "").trim().toLowerCase();
    if (!query) return providers;
    return providers.filter((row) => {
      const name = displayProviderName(row.provider).toLowerCase();
      const provider = String(row.provider || "").toLowerCase();
      const model = String(row.model || "").toLowerCase();
      return (
        name.includes(query) ||
        provider.includes(query) ||
        model.includes(query)
      );
    });
  }, [providers, providerFilter]);

  const renderProviderCard = (row) => {
    const title = displayProviderName(row.provider);
    const isDefault = !!row.is_default;
    return (
      <Card.Grid
        key={row.provider}
        className={`instantbi-provider-gridstyle${
          isDefault ? " instantbi-provider-gridstyle--default" : ""
        }`}
        onClick={() => openEditProvider(row)}
      >
        {isDefault ? (
          <Tooltip title="Default provider">
            <CheckCircleFilled className="instantbi-provider-status-icon instantbi-provider-status-icon--default" />
          </Tooltip>
        ) : (
          <Tooltip title="Set as default">
            <PlusCircleFilled
              className="instantbi-provider-status-icon instantbi-provider-status-icon--set-default"
              onClick={(event) => {
                event.stopPropagation();
                saveDefaultProvider(row.provider);
              }}
            />
          </Tooltip>
        )}
        <ProviderLogo provider={row.provider} size={42} />
        <span className="instantbi-provider-title ellipsis" title={title}>
          {title}
        </span>
        {row.model ? (
          <span className="instantbi-provider-model ellipsis" title={row.model}>
            {row.model}
          </span>
        ) : null}
      </Card.Grid>
    );
  };

  if (error) {
    return (
      <div className="instantbi-settings-editor">
        <Empty description={error} />
        <div style={{ textAlign: "center", marginTop: 12 }}>
          <Button
            icon={<SyncOutlined />}
            onClick={() => setReloadKey((key) => key + 1)}
          >
            Retry
          </Button>
        </div>
      </div>
    );
  }

  return (
    <div className="instantbi-settings-editor">
      <Spin spinning={loading}>
        <Tabs
          activeKey={activeTab}
          onChange={setActiveTab}
          tabBarExtraContent={
            <Tooltip title="Refresh">
              <Button
                size="small"
                icon={<SyncOutlined spin={loading} />}
                onClick={() => setReloadKey((key) => key + 1)}
              />
            </Tooltip>
          }
        >
          <Tabs.TabPane tab="LLM Providers" key="llm">
            <div className="instantbi-settings-editor__panel">
              <div className="instantbi-provider-toolbar">
                {providerSearchOpen ? (
                  <Input
                    allowClear
                    autoFocus
                    size="small"
                    placeholder="Search providers"
                    value={providerFilter}
                    onChange={(event) => setProviderFilter(event.target.value)}
                    className="instantbi-provider-search"
                    prefix={<SearchOutlined />}
                  />
                ) : (
                  <span className="instantbi-provider-toolbar-spacer" />
                )}
                <Tooltip title={providerSearchOpen ? "Hide search" : "Search providers"}>
                  <Button
                    size="small"
                    icon={providerSearchOpen ? <CloseOutlined /> : <SearchOutlined />}
                    type={providerSearchOpen || providerFilter ? "primary" : "default"}
                    ghost={!!(providerSearchOpen || providerFilter)}
                    onClick={() => {
                      setProviderSearchOpen((open) => {
                        if (open) setProviderFilter("");
                        return !open;
                      });
                    }}
                    aria-label="Search providers"
                  />
                </Tooltip>
              </div>
              <Card bordered={false} className="instantbi-provider-card">
                {providers.length ? (
                  filteredProviders.length || !providerFilter ? (
                    <>
                      {filteredProviders.map((row) => renderProviderCard(row))}
                      <Card.Grid
                        className="instantbi-provider-gridstyle instantbi-provider-gridstyle--add"
                        onClick={openAddProvider}
                      >
                        <span className="instantbi-provider-status-icon instantbi-provider-status-icon--add" />
                        <PlusCircleFilled className="instantbi-provider-add-icon" />
                        <span className="instantbi-provider-title">Add Provider</span>
                      </Card.Grid>
                    </>
                  ) : (
                    <Empty
                      image={Empty.PRESENTED_IMAGE_SIMPLE}
                      description="No matching providers"
                      style={{ width: "100%", padding: "24px 0" }}
                    />
                  )
                ) : (
                  <Empty
                    image={Empty.PRESENTED_IMAGE_SIMPLE}
                    description="No providers configured"
                    style={{ width: "100%", padding: "24px 0" }}
                  >
                    <Button type="primary" icon={<PlusOutlined />} onClick={openAddProvider}>
                      Add provider
                    </Button>
                  </Empty>
                )}
              </Card>
            </div>
          </Tabs.TabPane>
          <Tabs.TabPane tab="Developer Settings" key="developer">
            <div className="instantbi-settings-editor__panel">
              <UiFormGenerator
                form={loggingForm}
                layout={layouts.logging}
                dense
                columns={2}
              />
              <div className="instantbi-settings-editor__section-divider" />
              <UiFormGenerator
                form={applicationForm}
                layout={layouts.application}
                dense
                columns={2}
              />
              <Space className="instantbi-settings-editor__actions">
                <Button
                  type="primary"
                  loading={saving}
                  onClick={saveDeveloperSettings}
                >
                  Save settings
                </Button>
              </Space>
            </div>
          </Tabs.TabPane>
        </Tabs>
      </Spin>

      <Drawer
        title={editingProvider ? `Edit Provider (${editingProvider})` : "Add Provider"}
        width={520}
        open={providerDrawerOpen}
        visible={providerDrawerOpen}
        onClose={closeProviderDrawer}
        destroyOnClose
        footer={
          <Space style={{ float: "right" }}>
            <Button onClick={closeProviderDrawer}>Cancel</Button>
            <Button type="primary" loading={saving} onClick={saveProvider}>
              Save provider
            </Button>
          </Space>
        }
      >
        <Form
          form={providerForm}
          layout="vertical"
          className="instantbi-settings-editor__provider-form"
          requiredMark={false}
          onValuesChange={(changed) => {
            if (Object.prototype.hasOwnProperty.call(changed, "provider")) {
              const provider = changed.provider;
              const currentModel = providerForm.getFieldValue("model");
              loadModelsForPackage(
                resolvePackageFromProvider(provider),
                currentModel
              );
            }
          }}
        >
          <Spin spinning={modelsLoading}>
            <UiFormGenerator
              form={providerForm}
              layout={layouts.provider}
              dense
              embedded
            />
          </Spin>
          <div className="instantbi-settings-editor__parameters">
            <div className="instantbi-settings-editor__parameters-title">
              Parameters
            </div>
            <Form.List name="extra_parameters">
              {(fields, { add, remove }) => (
                <>
                  {fields.map((field) => (
                    <Row
                      key={field.key}
                      gutter={8}
                      align="middle"
                      className="instantbi-settings-editor__param-row"
                    >
                      <Col span={10}>
                        <Form.Item
                          {...field}
                          name={[field.name, "key"]}
                          fieldKey={[field.fieldKey, "key"]}
                          style={{ marginBottom: 8 }}
                        >
                          <Input placeholder="Key (e.g. api_key)" />
                        </Form.Item>
                      </Col>
                      <Col span={12}>
                        <Form.Item
                          {...field}
                          name={[field.name, "value"]}
                          fieldKey={[field.fieldKey, "value"]}
                          style={{ marginBottom: 8 }}
                        >
                          <Input placeholder="Value" />
                        </Form.Item>
                      </Col>
                      <Col span={2}>
                        <MinusCircleOutlined
                          className="instantbi-settings-editor__param-remove"
                          onClick={() => remove(field.name)}
                        />
                      </Col>
                    </Row>
                  ))}
                  <Button
                    type="dashed"
                    block
                    icon={<PlusOutlined />}
                    onClick={() => add({ key: "", value: "" })}
                  >
                    Add more parameters
                  </Button>
                </>
              )}
            </Form.List>
          </div>
        </Form>
      </Drawer>
    </div>
  );
};

export default InstantBISettingsEditor;
