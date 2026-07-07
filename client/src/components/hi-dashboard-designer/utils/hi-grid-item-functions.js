import { DeleteOutlined, FilterOutlined, GroupOutlined, MoreOutlined, PushpinOutlined, UngroupOutlined, RetweetOutlined } from "@ant-design/icons";
import { Tooltip } from "antd";
import { changeIsDraggableInGridItem, changeIsSelectedInGridItem, deleteGridItem, openCompactFbBrower, replaceReportId, unGroupGridItem, updateTabGridItemsLayout } from "../../../redux/actions/dashboard-designer.actions";

export const getMinimizedGridItemOptions = (
  { id, isOpenMode, dispatch, gridItemlayoutObj, childElement, isGrouped, children, openModeOptions, filtersGridItemsConfig, reportInfo, compType, isTabChild }
) => {
  const childrenItems = [
    ...(!isOpenMode
      ? [
        {
          key: "pintodashboard",
          label: "Pin",
          tooltip: "Pin to dashboard",
          icon: (
            <Tooltip title="Pin to dashboard">
              <PushpinOutlined
                className={
                  gridItemlayoutObj?.static
                    ? "hi-icon hi-selected"
                    : "hi-icon"
                }
              />
            </Tooltip>
          ),

          onClick: () => {
            dispatch(changeIsDraggableInGridItem(id));
            if (isTabChild) {
              dispatch(updateTabGridItemsLayout({ isPinAction: true, layoutID: id }))
            }
          },
        },
        ...(childElement || compType === "tab" || Boolean(isTabChild)
          ? []
          : [
            {
              key: "itemgrouping",
              // iconTooltip: "Tag this item for grouping",
              label: "Group",
              icon: (
                <Tooltip title="Tag this item for grouping">
                  <GroupOutlined
                    className={
                      isGrouped ? "hi-icon hi-selected" : "hi-icon"
                    }
                  />
                </Tooltip>
              ),
              onClick: () => {
                dispatch(changeIsSelectedInGridItem(id));
              },
            },
          ]),
        ...(children?.length > 0
          ? [
            {
              key: "itemungrouping",
              label: "Ungroup",
              iconTooltip:
                "Ungroup report items and place them in dashboard",
              icon: (
                <Tooltip title="Ungroup report items and place them in dashboard">
                  <UngroupOutlined />
                </Tooltip>
              ),
              onClick: () => {
                dispatch(unGroupGridItem(id));
              },
            },
          ]
          : []),
        ...(reportInfo &&
          reportInfo?.mode !== "filter" &&
          reportInfo?.extension === "hr"
          ? [
            {
              key: "filters",
              // tooltip: "Filters",
              label: "Filters",

              icon: (
                <Tooltip title="Add filters">
                  <FilterOutlined />
                </Tooltip>
              ),
              children: filtersGridItemsConfig,
            },
          ]
          : []),
        {
          key: "remove",
          // iconTooltip: "Delete grid item",
          label: "Delete",
          icon: (
            <Tooltip title="Delete grid item">
              <DeleteOutlined />
            </Tooltip>
          ),

          onClick: () => {
            dispatch(deleteGridItem(id));
          },
        },
        ...(compType === "dashboard-designer-component" ? [
          {
            key: "changethereport",
            label: "Change the report",
            icon: (
              <Tooltip title="Change the report">
                <RetweetOutlined />
              </Tooltip>
            ),

            onClick: () => {
              dispatch(openCompactFbBrower(true));
              dispatch(replaceReportId(id))
            },
          },
        ] : [])
      ]
      : []),
    ...openModeOptions,
  ]
  return [
    {
      icon: childrenItems?.length ? (
        <Tooltip title="Settings">
          <MoreOutlined />
        </Tooltip>
      ) : null,
      children: childrenItems,
      key: "minimizedGridItemSettings",
    },
  ]
}
