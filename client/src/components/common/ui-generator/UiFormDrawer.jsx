import { Button, Drawer } from "antd";
import { labelWithInfo } from "./labelWithInfo";
import { UiFormGenerator } from "./UiFormGenerator";

/**
 * Right-side drawer that hosts a layout-driven form (backend JSON → UI).
 */
export const UiFormDrawer = ({
  visible,
  onClose,
  onSave,
  saving = false,
  title,
  description,
  width = 720,
  form,
  layout,
  isAdd = false,
  className = "my-drawer ui-form-drawer",
  children,
  destroyOnClose = true,
  okText = "Save",
  cancelText = "Cancel",
  showFooter = true,
}) => {
  const drawerTitle =
    title || layout?.title ? (
      <span className="hi-drawer-title">
        {labelWithInfo(title || layout?.title, description || layout?.description)}
      </span>
    ) : null;

  return (
    <Drawer
      title={drawerTitle}
      placement="right"
      width={layout?.width || width}
      className={className}
      visible={visible}
      onClose={onClose}
      destroyOnClose={destroyOnClose}
      footer={
        showFooter ? (
          <div style={{ textAlign: "right" }}>
            <Button onClick={onClose} style={{ marginRight: 8 }}>
              {cancelText}
            </Button>
            <Button type="primary" loading={saving} onClick={onSave}>
              {okText}
            </Button>
          </div>
        ) : null
      }
    >
      {children || (
        <UiFormGenerator form={form} layout={layout} isAdd={isAdd} />
      )}
    </Drawer>
  );
};

export default UiFormDrawer;
