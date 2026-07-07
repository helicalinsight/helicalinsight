import { Drawer, Table } from "antd";
import "codemirror/mode/xml/xml";
import { useEffect, useState } from "react";

const parametersColumn = [
  {
    title: "Key",
    id: "paramKey",
    dataIndex: "paramKey",
    width: "50%",
  },
  {
    title: "Value",
    id: "paramValue",
    dataIndex: "paramValue",
  },
];

const RenderEmailBody = ({ value }) => {
  useEffect(() => {
    document.getElementById(`html-body`).innerHTML = value ? value : "";
  }, [value]);

  return <div id={`html-body`} />;
};

const RenderReportParameters = ({ value }) => {
  let parsedData = null;
  const [paramData, setParamData] = useState([]);

  useEffect(() => {
    if (value) {
      parsedData = JSON.parse(value);
      if (parsedData.hasOwnProperty("adhocFormData")) {
        delete parsedData.adhocFormData;
      }
    }

    if (parsedData) {
      const dataLength = Object.entries(parsedData).length;
      let updatedData = [];
      if (dataLength) {
        Object.entries(parsedData).map(([key, value]) => {
          updatedData = [
            ...updatedData,
            {
              paramKey: key,
              paramValue: Array.isArray(value) ? value.join(", ") : value,
            },
          ];
        });
      }
      setParamData(updatedData);
    }
  }, [value]);

  if (!value) return null; // If there is no value

  return paramData.length ? (
    <Table
      columns={parametersColumn}
      dataSource={paramData}
      pagination={false}
    />
  ) : null;
};

const MoreSchedulingInfo = ({ visible, moreInfo, onCloseDrawer }) => {
  const moreInfoColumns = [
    {
      title: "Key",
      id: "schedulingKey",
      dataIndex: "schedulingKey",
    },
    {
      title: "Value",
      id: "schedulingValue",
      dataIndex: "schedulingValue",
      className: "scheduling-more-info-table-cell",
      render: (text, record) => {
        const { schedulingKey, schedulingValue } = record;
        if (schedulingKey === "Email Body") {
          return <RenderEmailBody value={schedulingValue} />;
        }
        if (schedulingKey === "Report Parameters") {
          return <RenderReportParameters value={schedulingValue} />;
        }
        return <span>{schedulingValue.toString()}</span>;
      },
    },
  ];
  return (
    <Drawer
      title="Schedule Details"
      placement="right"
      size="large"
      onClose={onCloseDrawer}
      visible={visible}
      className="more-info-drawer"
    >
      <Table
        columns={moreInfoColumns}
        dataSource={moreInfo}
        bordered
        size="small"
        pagination={false}
        rowClassName={({ index }) => {
          let className = index % 2 && "table-row-color";
          return className;
        }}
      />
    </Drawer>
  );
};

export default MoreSchedulingInfo;
