import React from "react";
import { Drawer, Button } from "antd";
import { useSelector, useDispatch } from "react-redux";
import {
  storeFilterItemsToGridItemsData,
  deleteGridItem,
  updateParameterDrawerStatus,
  applyDashboardFilters,
} from "../../../../redux/actions/dashboard-designer.actions";
import { SortableHandle } from "react-sortable-hoc";
import { MenuOutlined, CloseOutlined } from "@ant-design/icons";
import { HIDashboardWrapper } from "../../../hi-dashboard/components/hi-dashboard-wrapper";
import SortableTable from "./hi-sortable-table";
import "./index.scss";
import { updateDashboardVariables } from "../../../../redux/actions";

const DragHandle = SortableHandle(() => (
  <MenuOutlined style={{ cursor: "grab", color: "#999" }} />
));

const HIReportParametersDrawer = () => {
  // variables

  const variables = useSelector((state) => state.dashboard.present.dashboardVariables);
  const dispatch = useDispatch();
  const designerSettings = useSelector(
    (state) => state.designer.present.designerSettings
  );
  const parameterDrawerStatus = useSelector(
    (state) => state.designer.present.parameterDrawerStatus
  );
  const filterItemsData = useSelector((state) =>
    state.designer.present.gridItemsData.filter((item) => item.compType === "filter")
  );
  const parameterSettings = designerSettings?.find(
    (item) => item.key === "parameters"
  )?.values;
  const designerMode = useSelector((state) => state.designer.present.designerMode);
  const previewMode = useSelector((state) => state.designer.present.previewMode);
  const isOpenMode = ["open"].includes(designerMode);
  const columns = [
    ...(!isOpenMode && !previewMode
      ? [
          {
            title: "Sort",
            dataIndex: "sort",
            width: 10,
            className: "drag-visible",
            render: () => <DragHandle />,
          },
        ]
      : []),
    {
      title: "Filter",
      dataIndex: "index",
      width: 80,
      className: "drag-visible",
      render: (_, record) => (
        <div>
          <HIDashboardWrapper
            type={record.reportInfo?.extension}
            parameter={record.parameter}
            id={record.id}
            listeners={record.listeners}
            reportInfo={record.reportInfo}
            dashboardVariables={variables}
          />
          {/* <HIDashboard
            id={record.id}
            type={record.reportInfo?.extension}
            parameter={record.parameter}
            reportInfo={record.reportInfo}
            listeners={record.listeners}
          /> */}
        </div>
      ),
    },
    ...(!isOpenMode && !previewMode
      ? [
          {
            title: "Close",
            dataIndex: "close",
            width: 10,
            className: "drag-visible",
            render: (_, record) => (
              <CloseOutlined
                onClick={() => {
                  dispatch(deleteGridItem(record.id));
                }}
              />
            ),
          },
        ]
      : []),
  ];

  // functions

  const updateData = (data) => {
    dispatch(storeFilterItemsToGridItemsData(data));
  };

  // rendering

  return (
    <Drawer
      title={<span className="hi-drawer-title">Filters</span>}
      placement={parameterSettings?.orientation}
      width={"24%"}
      mask={false}
      zIndex={999}
      getContainer={false}
      // closable={false}
      className="hi-parameter-drawer"
      onClose={() => {
        dispatch(updateParameterDrawerStatus(false));
      }}
      style={{ position: "absolute" }}
      visible={
        parameterDrawerStatus && (isOpenMode ? true : parameterSettings?.enable)
      }
      extra={
        <>
        {parameterSettings?.enableApplyButton && (
          <Button
            size="small"
            type="primary"
            onClick={() => {
              dispatch(applyDashboardFilters(new Date().getTime().toString()));
              if(parameterSettings?.closeOnApply){
                dispatch(updateParameterDrawerStatus(false));
              }
            }}
          >
            Apply
          </Button>
        )}
        <Button
          size="small"
          style={{ marginLeft: 8 }} 
          onClick={() => {
            // Reset logic
            dispatch((dispatch, getState) => {
              const baseStateReports = getState().hreport.present.baseStateReports;

              for (let baseReport of baseStateReports){
                if (baseReport.mode !== "filter") continue;
                dispatch(updateDashboardVariables({ key: baseReport.dashboardFilter.columnName, value: baseReport.filters.find(filter => filter.uid === baseReport.dashboardFilter.uid)?.values}))
              }

            }); // Reset the filters
            dispatch(applyDashboardFilters(new Date().getTime().toString()));
          }}
          >
            Reset
          </Button>
        </>
      }
    >
      <SortableTable
        dataSource={filterItemsData}
        columns={columns}
        onSortEnd={(data) => {
          updateData(data);
        }}
      />
    </Drawer>
  );
};
export { HIReportParametersDrawer };

/* <div>
        {filterItemsData.map((item) => (
          <HIDashboardWrapper
            type={item.reportInfo?.extension}
            parameter={item.parameter}
            id={item.id}
            listeners={item.listeners}
            reportInfo={item.reportInfo}
            dashboardVariables={variables}
          />
        ))}
      </div> */
