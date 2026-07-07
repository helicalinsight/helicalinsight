import React, { useState } from "react";
import { Col, Drawer, Row, Space, Table, Tooltip } from "antd";
import { EyeOutlined } from "@ant-design/icons";
import { useSelector } from "react-redux";
import { isEqual } from "lodash";
const ViewEditorDrawer = () => {
  const [visible, setVisible] = useState(false);
  const activeView = useSelector((state) => state.metadata.present.activeView);
  const editViewsTempData = useSelector(
    (state) => state.metadata.present.editViewsTempData,
    isEqual
  );
  const labels = editViewsTempData[activeView].labels;
  const data = editViewsTempData[activeView].data;
  const viewName = useSelector((state) => state.metadata.present.viewName);
  // const isExecuted =
  //   editViewsTempData[activeView].isExecuted && data && data.length > 0;
  const isExecuted =
    editViewsTempData[activeView].isExecuted;

  const showLargeDrawer = () => {
    setVisible(true);
  };
  const onClose = () => {
    setVisible(false);
  };

  const columns = labels?.map((label) => ({
    title: <Tooltip title={label.name}> {label.name} </Tooltip>,
    dataIndex: label.name,
    key: label.name,
    className: "table-ellipsis",
    width: 100,
    onCell: () => {
      return {
        className:"preview-cell-content"
    };
  },
    render: (value) => {
      console.log(value)
      if (typeof value === 'object' && value !== null) {
        return (
          <Tooltip placement="top" title={JSON.stringify(value)}>
          <span className="preview-cell-content">
            {JSON.stringify(value)}
         </span>
          </Tooltip>
        );
      }
      return (
        <Tooltip placement="top" title={value}>
        <span className="preview-cell-content">
          {value}
        </span>
        </Tooltip>
      );
    },
  }));

  return (
    <>
      <Space style={{ marginLeft: "auto" }} data-testid="view-editor-drawer">
        <Tooltip
          title={
            isExecuted
              ? "Preview the data"
              : "Execute the query to preview the data"
          }
          style={{ marginBottom: "0px" }}
          placement="left"
        >
          <EyeOutlined
            style={{
              fontSize: "15px",
              marginLeft: "4px",
              marginRight: "4px",
              cursor: isExecuted ? "pointer" : "not-allowed",
            }}
            onClick={() => {
              if (isExecuted) 
                showLargeDrawer();
            }}
          />
        </Tooltip>
      </Space>
      <Drawer
        title={
          <p style={{ margin: "0px" }}>
            Previewing the data of <strong>{viewName}</strong>
          </p>
        }
        placement="right"
        width={"100%"}
        onClose={onClose}
        visible={visible}
      >
        <Row gutter={[16, 16]}>
          <Col span={24}>
            <Table
              size="small"
              className="fb-table"
              showSorterTooltip={false}
              scroll={{
                y: "65vh",
                x: "100%",
              }}
              rowKey="id"
              tableLayout="auto"
              columns={columns}
              dataSource={data}
            />
          </Col>
        </Row>
      </Drawer>
    </>
  );
};
export default ViewEditorDrawer;
