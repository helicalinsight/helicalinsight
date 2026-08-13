import { Button, Card, Col, Popconfirm, Row, Switch, Tooltip } from "antd";
import {
  CaretRightOutlined,
  CodeOutlined,
  DeleteOutlined,
  EditOutlined,
  PauseOutlined,
  PlayCircleOutlined,
  PlusOutlined,
  ReloadOutlined,
  StopOutlined,
  SyncOutlined,
} from "@ant-design/icons";
import PopconfirmBody from "../../../common/components/Hi-Popconfirm";

/**
 * Action buttons for a system schedule row (rendered in the last table cell).
 */
export const SystemScheduleRowActions = ({
  record,
  loading = false,
  clickedRowId,
  onAction,
  onEdit,
  onEditScript,
}) => {
  const available = record.availableActions || [];

  return (
    <div className="system-schedule-actions" style={{ display: "flex", flexWrap: "wrap" }}>
      {available.includes("edit") && (
        <Tooltip title="Edit">
          <Button type="text" icon={<EditOutlined />} onClick={() => onEdit?.(record)} />
        </Tooltip>
      )}
      {available.includes("editScript") && (
        <Tooltip title="Edit Script">
          <Button type="text" icon={<CodeOutlined />} onClick={() => onEditScript?.(record)} />
        </Tooltip>
      )}
      {available.includes("trigger") && (
        <Tooltip title="Trigger Now">
          <Button
            type="text"
            icon={<ReloadOutlined />}
            loading={clickedRowId === record.jobId && loading}
            onClick={() => {
              onAction({ action: "trigger", id: record.jobId }, record);
            }}
          />
        </Tooltip>
      )}
      {available.includes("pause") && (
        <Tooltip title="Pause">
          <Button
            type="text"
            icon={<PauseOutlined />}
            onClick={() => onAction({ action: "pause", id: record.jobId }, record)}
          />
        </Tooltip>
      )}
      {available.includes("resume") && (
        <Tooltip title="Resume">
          <Button
            type="text"
            icon={<CaretRightOutlined />}
            onClick={() => onAction({ action: "resume", id: record.jobId }, record)}
          />
        </Tooltip>
      )}
      {available.includes("disable") && (
        <Tooltip title="Disable">
          <Button
            type="text"
            icon={<StopOutlined />}
            onClick={() => onAction({ action: "disable", id: record.jobId }, record)}
          />
        </Tooltip>
      )}
      {available.includes("enable") && (
        <Tooltip title="Enable">
          <Button
            type="text"
            icon={<PlayCircleOutlined />}
            onClick={() => onAction({ action: "enable", id: record.jobId }, record)}
          />
        </Tooltip>
      )}
      {available.includes("delete") && (
        <Popconfirm
          title={
            <PopconfirmBody
              intent="delete"
              description="Are you sure you want to delete this system schedule?"
            />
          }
          placement="left"
          onConfirm={() => onAction({ action: "delete", id: record.jobId }, record)}
        >
          <Tooltip title="Delete">
            <Button
              type="text"
              icon={<DeleteOutlined />}
              loading={clickedRowId === record.jobId && loading}
            />
          </Tooltip>
        </Popconfirm>
      )}
    </div>
  );
};

/**
 * Header toolbar shown when System Schedules toggle is on.
 */
export const SystemScheduleActionsCard = ({ onRefresh, onAdd, onEditJson }) => (
  <Card hoverable className="actions-card">
    <Row justify="end" align="middle">
      <Col>
        <Tooltip title="Add system schedule">
          <Button data-testid="system-add" type="text" icon={<PlusOutlined />} onClick={onAdd}>
            Add
          </Button>
        </Tooltip>
        <Tooltip title="Edit configuration in JSON editor">
          <Button
            data-testid="system-edit-json"
            type="text"
            icon={<EditOutlined />}
            onClick={onEditJson}
          >
            Edit
          </Button>
        </Tooltip>
        <Tooltip title="Refresh system schedule list">
          <Button data-testid="refresh" type="text" icon={<SyncOutlined />} onClick={onRefresh}>
            Refresh
          </Button>
        </Tooltip>
      </Col>
    </Row>
  </Card>
);

/**
 * Super-admin toggle to switch the schedule table to system schedules.
 */
export const SystemScheduleToggle = ({ checked, onChange }) => (
  <span className="system-schedule-toggle" data-testid="system-schedule-toggle">
    <Switch checked={checked} onChange={onChange} size="small" />
    <span style={{ marginLeft: 8 }}>System Schedules</span>
  </span>
);
