import React, { useEffect, useState } from "react";
import { Button, Input, Modal, Typography } from "antd";
import { namesByItemIdFromDrafts } from "../utils/save-instant-dashboard";

export const InstantChartSaveConfirmBody = ({ location, drafts, namesRef }) => {
  const [showNames, setShowNames] = useState(false);
  const [rows, setRows] = useState(drafts || []);

  useEffect(() => {
    if (namesRef) {
      namesRef.current = rows;
    }
  }, [namesRef, rows]);

  const updateName = (id, name) => {
    setRows((current) => {
      const next = current.map((row) => (row.id === id ? { ...row, name } : row));
      if (namesRef) {
        namesRef.current = next;
      }
      return next;
    });
  };

  return (
    <div style={{ padding: "8px 6px 0" }}>
      <Typography.Paragraph style={{ marginBottom: 8 }}>
        {`${(drafts || []).length} reports are being saved in the provided location.`}
      </Typography.Paragraph>
      {location ? (
        <Typography.Text type="secondary" style={{ display: "block", marginBottom: 8 }}>
          {location}
        </Typography.Text>
      ) : null}
      <Button type="link" style={{ paddingLeft: 0 }} onClick={() => setShowNames((value) => !value)}>
        {showNames ? "Hide names" : "Show names"}
      </Button>
      {showNames ? (
        <div style={{ maxHeight: 240, overflowY: "auto", marginTop: 4 }}>
          {rows.map((row, index) => (
            <Input
              key={row.id}
              value={row.name}
              style={{ marginBottom: 8 }}
              placeholder={`Report ${index + 1}`}
              onChange={(event) => updateName(row.id, event.target.value)}
            />
          ))}
        </div>
      ) : null}
    </div>
  );
};

export const confirmInstantInlineChartSaves = ({ location, drafts = [] }) =>
  new Promise((resolve) => {
    if (!drafts.length) {
      resolve({ ok: true, namesByItemId: {} });
      return;
    }
    const namesRef = { current: drafts };
    Modal.confirm({
      title: `${drafts.length} reports are being saved in the provided location`,
      icon: <></>,
      closable: true,
      width: 520,
      okText: "Save",
      cancelText: "Cancel",
      bodyStyle: { padding: "14px 10px" },
      content: (
        <InstantChartSaveConfirmBody
          location={location}
          drafts={drafts}
          namesRef={namesRef}
        />
      ),
      onOk: () => {
        resolve({
          ok: true,
          namesByItemId: namesByItemIdFromDrafts(namesRef.current),
        });
      },
      onCancel: () => {
        resolve({ ok: false, namesByItemId: {} });
      },
    });
  });
