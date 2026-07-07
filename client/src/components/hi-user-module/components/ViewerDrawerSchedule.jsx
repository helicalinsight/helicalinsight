import React, { useRef, useState } from "react";
import { Button, Form, Drawer, Steps, Space, Tooltip } from "antd";
import { useSelector, useDispatch } from "react-redux";
import moment from "moment";
import { capitalize } from "lodash";
import { appActions } from "../../../redux/actions";
import { ClockCircleOutlined, InfoCircleOutlined } from "@ant-design/icons";
import { SchedulingOptions } from "./SchedulingOptions";
import { EmailOptions } from "./EmailOptions";
import requests from "../../../base/requests";
import EmailPreview from "./EmailPreview";
import notify from "../../hi-notifications/notify";

const { Step } = Steps;

const initialPreviewState = {
  loading: true,
  data: {}
}

const ViewerDrawerSchedule = (props) => {
  const dispatch = useDispatch();
  const viewModeInfo = useSelector((state) => state.app.viewModeInfo);
  const { file, extension } = viewModeInfo;
  const { path, name } = file;

  const [submitClicked, setSubmitClicked] = useState(false);
  const [previewState, setPreviewState] = useState(initialPreviewState)
  const [emailForm] = Form.useForm();
  const [scheduleForm] = Form.useForm();
  const [current, setCurrent] = useState(0);
  const [formObjects, setFormObjects] = useState([]);
  const { adhocFormData, parameters, duplicateFiltersPresent = false } = props.parametersReview;
  const Notify = notify(dispatch);

  const apiRef = useRef(null);

  const applicationSettingsData = useSelector(
    (state) => state.app.applicationSettingsData
  );

  function handleAbort(prop = {}) {
    apiRef?.current?.abort(prop);
  }


  let getDirectory = (path, name) => {
    return path?.replace(name, "").replace(/[\\|\/]+$/, "");
  };

  let getFile = (s) => {
    // const sA = s.replace(/[0-9]/g, "").split("/");
    const sA = s.split("/");
    return sA[sA.length - 1].trim();
  };

  const next = (gotoNextStep = true) => {
    const currentForm = steps[current].formForStep;
    let formResolve = null;
    formResolve = currentForm.validateFields();
    if (formResolve, gotoNextStep) {
      formResolve.then(() => {
        setCurrent(current + 1);
      });
    }
    let newFormObjects = [];
    if (formObjects.length > 0) {
      newFormObjects = formObjects.filter(
        (e) => e.key !== steps[current].title.toLowerCase()
      );
    }
    newFormObjects.push({
      key: steps[current].title.toLowerCase(),
      formObj: currentForm.getFieldsValue(),
    });
    setFormObjects(newFormObjects);
    return newFormObjects;
  };

  const prev = () => {
    setCurrent(current - 1);
  };

  const buildEmailObj = (formValues) => {
    const sendObj = {};
    const { formats, to, subject, editorData } = formValues;
    sendObj["Formats"] = formats;
    sendObj["Recipients"] = to.split(";");
    sendObj["Zip"] = false;
    sendObj["Subject"] = subject;
    sendObj["Body"] = editorData;
    return sendObj;
  };

  const buildScheduleObj = () => {
    const {
      ends,
      endsAfter,
      endsOn,
      repeatBy,
      repeatOn,
      repeats,
      repeatsEvery,
      startsOn,
      timezone,
    } = scheduleForm.getFieldsValue();
    const returnObj = {};
    returnObj["DaysofWeek"] = repeatOn || [];
    returnObj["Frequency"] = capitalize(repeats);
    returnObj["RepeatBy"] = repeatBy;
    returnObj["RepeatsEvery"] = repeatsEvery;
    returnObj["StartDate"] = moment(startsOn).format("YYYY-MM-DD");
    returnObj["EndDate"] = moment(endsOn).format("YYYY-MM-DD");
    returnObj["endsRadio"] = capitalize(ends);
    returnObj["timeZone"] = timezone.split(" ")[0];
    returnObj["EndAfterExecutions"] = endsAfter;
    returnObj["dateFormat"] = "DD/MM/YYYY hh:mm A";
    returnObj["ScheduledTime"] = moment(startsOn).format("HH:mm:ss");
    returnObj["ScheduledEndTime"] = moment(endsOn).format("HH:mm:ss");
    return returnObj;
  };
  const buildScheduleFormData = (sendScheduleptions = true, customFormObj = null) => {
    if (formObjects) {
      let tempFormObj = customFormObj ? customFormObj : formObjects;
      const emailFormObj = tempFormObj.find((e) => e.key === "email")?.formObj;
      const emailObj = buildEmailObj(emailFormObj);
      let scheduleForm = ''
      if (sendScheduleptions) scheduleForm = buildScheduleObj();
      const sendObj = {};
      const reportName = getFile(emailFormObj.reportName);
      const locArr = emailFormObj.reportName.split(" ")[0].split("/");
      locArr.pop();
      const location = locArr.filter((e) => e).join("/");
      sendObj["command"] = "add";
      sendObj["reportDirectory"] = getDirectory(path, name);
      sendObj["reportFile"] = name;
      let reqLoc = path.split("/");
      reqLoc.pop();
      sendObj["reportName"] = reportName;
      // sendObj["location"] = reqLoc.join("/");
      sendObj['location'] = location;
      sendObj["EmailSettings"] = window.btoa(JSON.stringify(emailObj));
      sendObj["isActive"] = true;
      sendObj["reportParameters"] = window.btoa(
        JSON.stringify({ mode: "dashboard", navigatorUserAgent: "print", ...parameters })
      );
      const { formats } = emailFormObj;
      if (
        (formats.includes("csv") || formats.includes("xlsx")) &&
        adhocFormData
      ) {
        sendObj["adhocFormData"] = window.btoa(JSON.stringify(adhocFormData));
      }
      if (sendScheduleptions) sendObj["ScheduleOptions"] = window.btoa(JSON.stringify(scheduleForm));
      return sendObj;
    }
  };

  const onClose = () => {
    handleAbort();
    if (viewerScheduleModalVisible) setCurrent(0);
    dispatch(appActions.setViewerScheduleVisibility(false));
    emailForm.resetFields();
    scheduleForm.resetFields();
  };

  const onPreview = () => {
    if (duplicateFiltersPresent) {
      Notify.warning({ message: 'Duplicate filters detected in parameters. Please note that while mailing only one filter name will be sent from duplicate filter names. If there is any change in filter values in open mode that time we will always get recently changed filter values as preview output.' });
    }
    return new Promise((resolve, reject) => {
      setPreviewState({ ...initialPreviewState, loading: true });
      const tempForm = next(false);
      let formData = buildScheduleFormData(false, tempForm);
      const { formats, to, subject, editorData } = emailForm.getFieldsValue()
      formData = {
        ...formData,
        EmailSettings: JSON.parse(window.atob(formData.EmailSettings)),
        reportParameters: JSON.parse(window.atob(formData.reportParameters)),
        formats: formats,
        recipients: to.split(";"),
        subject: subject,
        body: editorData,
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

  const steps = [
    {
      title: "Email",
      content: (
        <EmailOptions
          form={emailForm}
          buildObj={buildEmailObj}
          formFor={"scheduling"}
          onClose={onClose}
          current={current}
        />
      ),
      formForStep: emailForm,
    },
    {
      title: "Schedule",
      content: (
        <SchedulingOptions
          apiRef={apiRef}
          form={scheduleForm}
          submitClicked={submitClicked}
          setSubmitClicked={setSubmitClicked}
          buildScheduleFormData={buildScheduleFormData}
          onClose={onClose}
        />
      ),
      formForStep: scheduleForm,
    },
  ];

  const viewerScheduleModalVisible = useSelector(
    (state) => state.app.viewerScheduleModalVisible
  );

  const style =
    steps.find((e, i) => i === current)?.title === "Schedule"
      ? {}
      : { height: "100%" };

  return (
    <Drawer
      data-testid="hi-viewer-drawer-schedule"
      size={"large"}
      title={
        <span>
          Schedule
          <Tooltip
            placement="bottom"
            color="#000"
            title={"Default report parameters will be used while Schedule"}
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
                width: "10px",
                height: "16px"
              }}
            />
          </Tooltip>
        </span>
      }
      placement="right"
      onClose={onClose}
      visible={viewerScheduleModalVisible}
      zIndex={999}
      destroyOnClose={true}
      maskClosable={false}
      extra={
        <Space>
          <Button data-testid="hi-viewer-drawer-cancelbtn" onClick={onClose}>Cancel</Button>
          {current === steps.length - 1 && (
            <Button
              type="primary"
              loading={submitClicked}
              icon={<ClockCircleOutlined />}
              onClick={() => {
                setSubmitClicked(true);
              }}
            >
              {submitClicked ? "Scheduling..." : "Schedule"}
            </Button>
          )}
          {current !== steps.length - 1 ?
            <EmailPreview onPreviewClick={onPreview} loading={previewState.loading} data={previewState.data} />
            : null}

        </Space>
      }
    >
      {viewerScheduleModalVisible && (
        <>
          <Steps current={current}>
            {steps.map((item) => (
              <Step key={item.title} title={item.title} />
            ))}
          </Steps>
          <div style={{ margin: "20px 0", ...style }}>
            {steps[current].content}
          </div>
          <div>
            {current < steps.length - 1 && (
              <Button data-testid="hi-viewer-drawer-nextbtn" type="primary" onClick={() => next()}>
                Next
              </Button>
            )}
            {current > 0 && <Button data-testid="hi-viewer-drawer-previousbtn" onClick={() => prev()}>Previous</Button>}
          </div>
        </>
      )}
    </Drawer>
  );
};

export { ViewerDrawerSchedule };
