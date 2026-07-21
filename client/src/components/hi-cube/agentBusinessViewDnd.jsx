import { useDrag, useDrop } from "react-dnd";
import {
  createColumnChildFromDropRecord,
} from "../hi-agent/utils/agent-cube-bridge";
import { collectAssignableBusinessTopicKeys } from "./agentBusinessTopicMembership";

export const AGENT_CUBE_FIELD_DND_TYPE = "agentCubeField";
export const METADATA_ROW_CHILD_DND_TYPE = "metadataRowChild";

export function AgentCubeFieldDragSource({ record, disabled = false, children }) {
  const canDragRecord = !disabled && !record?.isDelete;
  const [, drag] = useDrag(
    () => ({
      type: AGENT_CUBE_FIELD_DND_TYPE,
      item: () => ({
        key: record.key,
        fields: record.fields,
        columnId: record.columnId,
        tableId: record.tableId,
        isHierarchy: Boolean(record.isHierarchy),
        isHierarchyChild: Boolean(record.isHierarchyChild),
        parentKey: record.parentKey,
        childKeys: record.isHierarchy
          ? collectAssignableBusinessTopicKeys(record)
          : undefined,
        children: record.isHierarchy
          ? (record.children || []).map((child) => ({
              key: child.key,
              fields: child.fields,
              isDelete: child.isDelete,
              isHierarchy: child.isHierarchy,
              children: child.children,
            }))
          : undefined,
      }),
      canDrag: () => canDragRecord,
    }),
    [record, canDragRecord],
  );

  if (!canDragRecord) {
    return children;
  }

  return (
    <div
      ref={drag}
      className={`agent-cube-field-drag-source${
        record?.isHierarchy ? " agent-cube-field-drag-source--hierarchy" : ""
      }`}
    >
      {children}
    </div>
  );
}

export function AgentTopicDropZone({
  domainName,
  topicName,
  onDropCubeField,
  onDropMetadataField,
  children,
  className = "",
}) {
  const [{ isOver, canDrop }, drop] = useDrop(
    () => ({
      accept: [AGENT_CUBE_FIELD_DND_TYPE, METADATA_ROW_CHILD_DND_TYPE],
      canDrop: (item, monitor) => {
        const type = monitor.getItemType();
        if (type === METADATA_ROW_CHILD_DND_TYPE) {
          return Boolean(item?.columnId || item?.alias || item?.columnName);
        }
        return Boolean(item?.key);
      },
      drop: (item, monitor) => {
        if (monitor.didDrop()) {
          return;
        }
        const type = monitor.getItemType();
        if (type === METADATA_ROW_CHILD_DND_TYPE) {
          onDropMetadataField?.(item);
          return { droppedOnTopic: true };
        }
        if (!item?.key) {
          return;
        }
        onDropCubeField?.(item);
        return { droppedOnTopic: true };
      },
      collect: (monitor) => ({
        isOver: monitor.isOver({ shallow: true }),
        canDrop: monitor.canDrop(),
      }),
    }),
    [domainName, topicName, onDropCubeField, onDropMetadataField],
  );

  return (
    <div
      ref={drop}
      className={[
        className,
        "business-view-agent-topic-dropzone",
        isOver && canDrop ? "is-drop-over" : "",
        canDrop ? "is-drop-ready" : "",
      ]
        .filter(Boolean)
        .join(" ")}
    >
      {children}
    </div>
  );
}

export function buildTopicChildFromMetadata(record, domainName, topicName) {
  const child = createColumnChildFromDropRecord(record);
  const domain = domainName || "";
  const topic = topicName || "";
  return {
    ...child,
    domain,
    topic,
    businessTopics:
      domain.trim() && topic.trim()
        ? [{ domain: domain.trim(), topic: topic.trim() }]
        : [],
  };
}
