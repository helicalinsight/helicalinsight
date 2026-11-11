import { SettingOutlined } from "@ant-design/icons";
import { Checkbox, Dropdown, Tooltip } from "antd";

const QuerySettings = ({
  enableLimit = true,
  onEnableLimitChange = () => {},
  disabled = false,
}) => {
  return (
    <Dropdown
      trigger={["click"]}
      placement="bottomRight"
      disabled={disabled}
      overlay={
        <div
          style={{
            background: "#fff",
            padding: "8px 12px",
            boxShadow: "0 2px 8px rgba(0,0,0,0.15)",
            borderRadius: 4,
          }}
          onClick={(e) => e.stopPropagation()}
        >
          <Tooltip
            title={
              <div>
                <div>
                  Enabled: Limits the number of records fetched for preview.
                </div>
                <div style={{ marginTop: 4 }}>
                  Disabled: Runs the query as written. Use this for stored
                  procedures, multiple statements, or CTEs. Warning: may cause
                  performance issues on large result sets.
                </div>
              </div>
            }
            placement="left"
          >
            <Checkbox
              checked={enableLimit}
              onChange={(e) => onEnableLimitChange(e.target.checked)}
            >
              Enable Limit
            </Checkbox>
          </Tooltip>
        </div>
      }
    >
      <SettingOutlined
        style={{
          fontSize: 16,
          cursor: disabled ? "not-allowed" : "pointer",
          marginLeft: 8,
          opacity: disabled ? 0.5 : 1,
        }}
        title="Query settings"
      />
    </Dropdown>
  );
};

export default QuerySettings;
