import { InfoCircleOutlined, SendOutlined } from "@ant-design/icons";
import { Button, Drawer, Form, Space, Tooltip } from "antd";
import moment from "moment";
import React, { useRef, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import requests from "../../../base/requests";
import { appActions } from "../../../redux/actions";
import { EmailOptions } from "./EmailOptions";
import EmailPreview from "./EmailPreview";
import notify from "../../hi-notifications/notify";

const initialPreviewState = {
  loading: true,
  data: {}
}
const ViewerDrawerEmail = (props) => {
  const dispatch = useDispatch();
  const [emailForm] = Form.useForm();
  const [submitClicked, setSubmitClicked] = useState(false);
  const [previewState, setPreviewState] = useState(initialPreviewState)
  const { adhocFormData, parameters, duplicateFiltersPresent = false } = props.parametersReview;
  const viewModeInfo = useSelector((state) => state.app.viewModeInfo);
  const { file, extension } = viewModeInfo;
  const { path, title } = file;
  const splitPath = path.split("/");
  splitPath.pop();
  const Notify = notify(dispatch);

  const apiRef = useRef(null);

  const applicationSettingsData = useSelector(
    (state) => state.app.applicationSettingsData
  );

  function handleAbort(prop = {}) {
    apiRef?.current?.abort(prop);
  }

  const emailModalVisible = useSelector(
    (state) => state.app.viewerEmailModalVisible
  );

  const onClose = () => {
    handleAbort();
    if (emailModalVisible)
      dispatch(appActions.setViewerEmailModalVisibility(false));
    emailForm.resetFields();
  };

  const buildObj = (formValues) => {
    const { path, name, title } = file;
    const sendObj = {};
    const splitPath = path.split("/");
    if (splitPath.length > 1) {
      splitPath.pop();
    }
    sendObj["dir"] = splitPath.join("/");
    sendObj["reportFile"] = name;
    sendObj["reportType"] = extension;
    const { formats, to, subject, editorData } = formValues;
    sendObj["formats"] = JSON.stringify(formats);
    sendObj["recipients"] = JSON.stringify(to.split(";"));
    sendObj["reportSourceType"] = "url";
    sendObj["reportParameters"] = JSON.stringify({
      mode: "dashboard",
      navigatorUserAgent: "print",
      ...parameters,
    });
    sendObj["subject"] = window.btoa(subject);
    sendObj["body"] = window.btoa(editorData);
    sendObj["reportName"] = title + " " + moment().format("YYMMDDHHmm");
    if ((formats.includes("csv") || formats.includes("xlsx")) && adhocFormData) {
      sendObj["adhocFormData"] = window.btoa(JSON.stringify(adhocFormData));
    }
    return sendObj;
  };

  const onPreview = () => {
    if (duplicateFiltersPresent) {
      Notify.warning({ message: 'Duplicate filters detected in parameters. Please note that while mailing only one filter name will be sent from duplicate filter names. If there is any change in filter values in open mode that time we will always get recently changed filter values as preview output.' });
    }
    const formValues = emailForm.getFieldsValue()
    return new Promise((resolve, reject) => {
      setPreviewState({ ...initialPreviewState, loading: true });
      let formData = buildObj(formValues)
      formData = {
        ...formData,
        formats: JSON.parse(formData.formats),
        recipients: JSON.parse(formData.recipients),
        reportParameters: JSON.parse(formData.reportParameters),
        subject: window.atob(formData.subject),
        body: window.atob(formData.body)
      }
      requests
        .usermodule(dispatch, applicationSettingsData.settings)
        .previewEmail(
          formData,
          (res) => {
            setPreviewState({ ...initialPreviewState, data: res, loading: false })
            resolve();
          },
          (e) => {
            reject(e);
            setPreviewState({ ...initialPreviewState, loading: false })
          }
        );
    });
  }

  return (
    <Drawer
      data-testid="hi-viewer-drawer-email"
      title={
        <span>
          Email
          <Tooltip
            placement="bottom"
            color="#000"
            title={"Default report parameters will be used while emailing"}
            overlayInnerStyle={{
              height: "100%",
              width: "100%",
              fontSize: 14,
            }}
          >
            <InfoCircleOutlined
              style={{
                fontSize: "0.6rem",
                marginLeft: 4,
                cursor: "pointer",
                width: "11px",
                height: "16px"
              }}
            />
          </Tooltip>
        </span>
      }
      placement="right"
      onClose={onClose}
      visible={emailModalVisible}
      width={500}
      zIndex={999}
      maskClosable={false}
      extra={
        <Space>
          <Button data-testid="hi-viewer-drawer-cancel" onClick={onClose}>Cancel</Button>
          <Button
            data-testid="hi-viewer-drawer-btn"
            type="primary"
            loading={submitClicked}
            icon={<SendOutlined style={{ transform: "rotate(-45deg)" }} />}
            onClick={() => {
              setSubmitClicked(true);
            }}
          >
            {submitClicked ? "Sending mail..." : "Email now"}
          </Button>
          <EmailPreview onPreviewClick={onPreview} loading={previewState.loading} data={previewState.data} />
        </Space>
      }
    >
      {emailModalVisible && (
        <EmailOptions
          apiRef={apiRef}
          submitClicked={submitClicked}
          setSubmitClicked={setSubmitClicked}
          form={emailForm}
          buildObj={buildObj}
          formFor={"email"}
          extension={extension}
          title={title}
          location={splitPath.join("/")}
          onClose={onClose}
        />
      )}
    </Drawer>
  );
};

export { ViewerDrawerEmail };

