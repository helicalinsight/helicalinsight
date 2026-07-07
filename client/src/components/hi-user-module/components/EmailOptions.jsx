import { CopyOutlined, FolderOpenOutlined, InfoCircleFilled, InfoCircleOutlined } from "@ant-design/icons";
import { Button, Checkbox, Col, Collapse, Divider, Form, Input, Popover, Row, Select, Tooltip, Typography } from "antd";
import moment from "moment";
import React, { useEffect, useState } from "react";
import ReactQuill from "react-quill";
import { useDispatch, useSelector } from "react-redux";
import requests from "../../../base/requests";
import { fileBrowserActions } from "../../../redux/actions";
import HITooltip from "../../common/components/hi-tooltip";
import { HIFileBrowser } from "../../hi-fileBrowser/hi-fileBrowser";
import SaveItems from "../../hi-fileBrowser/SaveItems";
import notify from "../../hi-notifications/notify";
// import { extensionBasedFileFormats } from "../constants";
import "../hi-user-module.scss";
import useExportOptions from "../../../hooks/useExportOptions";
var toolbarOptions = [
  ["bold", "italic", "underline", "strike", "link"], // toggled buttons
  // ["blockquote", "code-block"],

  // [{ header: 1 }, { header: 2 }], // custom button values
  // [{ list: "ordered" }, { list: "bullet" }],
  [{ script: "sub" }, { script: "super" }], // superscript/subscript
  [{ indent: "-1" }, { indent: "+1" }], // outdent/indent
  [{ direction: "rtl" }], // text direction

  // [{ size: ["small", false, "large", "huge"] }], // custom dropdown
  [{ header: [1, 2, 3, 4, 5, 6, false] }],

  [{ color: [] }, { background: [] }], // dropdown with defaults from theme
  // [{ font: [] }],
  [
    { align: "" },
    { align: "center" },
    { align: "right" },
    { align: "justify" },
  ],

  ["clean"], // remove formatting button
];

const formats = [
  "clean",
  "header",
  "bold",
  "italic",
  "underline",
  "strike",
  "blockquote",
  "list",
  "bullet",
  "indent",
  "link",
  "image",
  "align",
  "color",
  "script",
  "background",
  "indent",
  "direction",
];

const EmailOptions = (props) => {
  const dispatch = useDispatch();
  const Notify = notify(dispatch);
  const {
    submitClicked,
    setSubmitClicked,
    form,
    buildObj,
    formFor,
    onClose,
    current,
    apiRef
  } = props;
  const applicationSettingsData = useSelector(
    (state) => state.app.applicationSettingsData
  );
  const emailDrawerVisible = useSelector(
    (state) => state.app.viewerEmailModalVisible
  );
  const viewModeInfo = useSelector((state) => state.app.viewModeInfo);
  const { file, extension } = viewModeInfo;
  const { path, name, title } = file;

  const { getEmailExportOptions } = useExportOptions();

  useEffect(() => {
    form?.setFieldValue("reportName", `/${getDirectory(path, name)}/${title + " " + moment().format("YYMMDDHHmm")
      }`);
    if (submitClicked) {
      form
        .validateFields()
        .then(() => {
          if (emailDrawerVisible) form.submit();
        })
        .catch((e) => setSubmitClicked(false));
    }
  }, [submitClicked]);

  // const fileFormats = extensionBasedFileFormats[extension];
  const formats = getEmailExportOptions();
  const fileFormats = formats[extension];
  const [searchVal, setSearchVal] = useState('');
  // let emailValidatorRegex = new RegExp("[a-z0-9]+@[a-z]+.[a-z]{2,3}");

  const saveFile = (rec, input) => {
    form.setFieldsValue({
      reportName: `/${rec.path}/${input + " " + moment().format("YYMMDDHHmm")}`,
    });
  };

  const onSendMail = (formValues) => {
    apiRef.current = requests
      .usermodule(dispatch, applicationSettingsData.settings)
      .postSendMail(
        buildObj(formValues),
        (res) => {
          apiRef.current = null;
          // Notify.success({
          //   type: "Network call",
          //   ...res,
          // });
          onClose();
          setSubmitClicked(false);
        },
        (e) => {
          apiRef.current = null;
          // Notify.error({
          //   type: "Network call",
          //   ...e,
          // });
          onClose();
          setSubmitClicked(false);
        }
      );
  };


  const onFinish = (values) => onSendMail(values);

  const layout = {
    labelCol: {
      span: 4,
    },
  };

  let getDirectory = (path, name) => {
    return path?.replace(name, "").replace(/[\\|\/]+$/, "");
  };

  const filterCheck = (option, inputValue, type) => option?.[type]?.toLowerCase()?.includes(inputValue?.toLowerCase());

  const renderDropdown = (menu, searchValue = "") => {
    const formatsValue = form.getFieldValue("formats");
    let filteredFormats = fileFormats.filter((item) => filterCheck(item, searchValue, "title"));
    let allSelected = formatsValue.length === filteredFormats.length;
    const handleSelectAll = () => {
      form.setFieldsValue({ formats: allSelected ? [] : filteredFormats.map(({ value }) => value) });
    };

    return (
      <>
        {menu}
        <Divider style={{ margin: '4px 0' }} />
        {filteredFormats?.length ? <Row justify="center">
          <Button type="link" onClick={handleSelectAll}>
            {allSelected ? "Deselect All" : "Select All"}
          </Button>
        </Row> : null}
      </>
    )
  }

  return (
    <Form
      data-testid="hi-user-module-email-form"
      {...layout}
      form={form}
      onFinish={onFinish}
      name="control-hooks"
      style={{ display: "flex", flexDirection: "column", height: "100%" }}
      labelWrap={true}
      labelAlign="left"
      initialValues={{
        formats: ["pdf"],
        to: "",
        subject: "",
        editorData: "",
        reportName: `/${getDirectory(path, name)}/${title + " " + moment().format("YYMMDDHHmm")
          }`,
      }}
    >
      <Form.Item
        label="Formats"
        name="formats"
        rules={[
          { required: true, message: "Please select at least one format" },
        ]}
      >
        <Select
          mode="multiple"
          size={'middle'}
          className="canvas-parameter-select"
          style={{ width: '100%' }}
          options={fileFormats.map(({ title, value }) => ({ label: title, value }))}
          showArrow
          allowClear={true}
          maxTagCount={"responsive"}
          dropdownRender={(menu) => renderDropdown(menu, searchVal)}
          searchValue={searchVal}
          onSearch={(val) => setSearchVal(val)}
          filterOption={(inputValue, option) => {
            return filterCheck(option, inputValue, "label") || filterCheck(option, inputValue, "value");
          }}
          onDropdownVisibleChange={() => setSearchVal("")}
        />
      </Form.Item>
      <Form.Item
        label={[
          "To",
          <Tooltip title="To send an email to multiple recipients, separate their email addresses with a semicolon. You can also use predefined variables like org, user, and date in this context. e.g : ${user}.email">
            <InfoCircleOutlined
              style={{ marginLeft: 5, fontSize: 10, cursor: "pointer" }}
            />
          </Tooltip>,
        ]}
        labelWrap={true}
        name="to"
        rules={[
          { required: true },
          // {
          //   validator: (_, value) => {
          //     return emailValidatorRegex.test(value)
          //       ? Promise.resolve()
          //       : Promise.reject("Please enter valid email ID!");
          //   },
          // },
        ]}
      >
        <Input />
      </Form.Item>
      <Form.Item
        label={[
          "Subject",
          <Tooltip title="You can create a dynamic subject line by incorporating predefined variables such as report, date, parameters, user, and org details. Ensure that the subject line does not exceed 255 characters. E.g: ${reportName} on ${date:yyyy-MM-dd}.">
            <InfoCircleOutlined
              style={{ marginLeft: 1, fontSize: 10, cursor: "pointer" }}
            />
          </Tooltip>
        ]}
        name="subject"
        rules={[
          {
            required: true,
            message: "Please enter valid subject.",
          },
          {
            max: 255,
            message: "Please ensure that the subject must not exceed 255 characters.",
          },
        ]}
      >
        <Input />
      </Form.Item>
      <Form.Item
        label={
          <span style={{ paddingLeft: "12px" }}>
            Body
            <EmailTemplatingOptions {...{ form }} />
          </span>
        }
        name="editorData"
        className="email-text-editor"
      >
        <ReactQuill
          modules={{ toolbar: toolbarOptions }}
          formats={formats}
          placeholder={"Enter text..."}
        />
      </Form.Item>
      {
        formFor && formFor === "scheduling" && (
          <>
            <Form.Item
              label="Report"
              rules={[{ required: true }]}
              name="reportName"
              style={{ marginTop: 20 }}
            >
              <Input
                className={"file-email-select"}
                disabled
                addonAfter={
                  <Button
                    data-testid="hi-user-module-btn"
                    type="primary"
                    onClick={() => {
                      dispatch(fileBrowserActions.setShowFileBrowser(true));
                    }}
                    icon={<FolderOpenOutlined />}
                  >
                    Browse
                  </Button>
                }
              />
            </Form.Item>
            <HIFileBrowser
              footerForm={{
                type: "Save",
                form: (
                  <SaveItems
                    formHeading="Schedule report name"
                    onFormSumbit={saveFile}
                    saveButtonText="Save"
                    cancelButtonText="Cancel"
                    inputValue={title}
                  />
                ),
              }}
            />
          </>
        )
      }
    </Form >
  );
};

export { EmailOptions };

const EmailTemplatingOptions = ({ form }) => {
  const [open, setOpen] = useState();
  const [copied, setCopied] = useState(false);
  const handleOpenChange = (newOpen) => {
    setOpen(newOpen);
  };

  const handleChangeBody = (value) => {
    const editor = form.getFieldInstance('editorData').getEditor();
    // Ensure the editor is focused
    editor.focus();
    let range = editor.getSelection();
    if (!range) {
      const contentLength = editor.getLength();
      range = { index: contentLength, length: 0 };
    }
    editor.insertText(range.index, " " + value);
  };

  const examplesTooltip = (examples = [], heading = "Available Date Formats") => {
    if (!examples.length) return null
    return (
      <>
        <h5 style={{ marginLeft: '4px', color: '#fff' }}>{heading}:</h5>
        <ul className="hi-email-templating-examples">
          {examples.map((example, index) => (
            <li key={index}>
              {example}
            </li>
          ))}
        </ul>
      </>
    )
  }


  const renderParameters = () => {
    let data = {
      user: { variables: [{ name: '${user}.name', tooltip: "Represents the user's name." }, { name: '${user}.email', tooltip: "Contains the user's email address." }, { name: '${user}.id', tooltip: "Holds the user's unique identifier." }, { name: '${user}.enabled', tooltip: "Indicates whether the user is enabled." }, { name: '${user}.isExternalUser', tooltip: "Specifies if the user is an external user." }] },
      org: { variables: [{ name: '${org}.name', tooltip: "Represents the organization's name." }, { name: '${org}.id', tooltip: "Contains the organization's unique identifier." }] },
      profile: { variables: [{ name: '${profile}.name', tooltip: "Represents all the profile's name in comma seperated values. e.g: 'department','country','area'" }, { name: '${profile}.id', tooltip: "Contains the profile's unique identifier. e.g: 1,2,3" }, { name: '${profile}.value', tooltip: "Holds the profile's value. e.g: 'Sales','India','APAC'. Access specific profile value of a single profile. e.g: ${profile['country']}.value = 'India','USA'" }] },
      role: { variables: [{ name: '${role}.name', tooltip: "Represents the role's name. e.g: 'ROLE_ADMIN','ROLE_USER'" }, { name: '${role}.id', tooltip: "Contains the role's unique identifier. e.g: 1,2" }] },
      'file metadata': { variables: [{ name: '${reportDir}', tooltip: "Represents the directory of the current resource where the report is stored. e.g: Sample/Reports/" }, { name: '${reportName}', tooltip: "Contains the name of the report. e.g: Client wise Tours and expense" }, { name: '${reportTitle}', tooltip: "Contains the title of the report. e.g: Client wise Tours and expense" }, { name: '${reportNameWithExtension}', tooltip: "Holds the report name along with its file extension. e.g: sales_report.hr" }, { name: '${reportPath}', tooltip: "Provides the resource path to access the report. e.g: Client_wise_Tours_and_expense" }, { name: '${reportUrl}', tooltip: "Provides the resource URL to access the report. e.g: Sample/Reports/Client_wise_Tours_and_expense.hr" }] },
      'parameter details': { variables: [{ name: '${filter name}', tooltip: "Specifies the filter values in a comma separated value. e.g: ${travel_type} = Agent" }], examples: ["${mode}", "${booking_platform}", "${travel_cost}"], heading: "Report parameter examples" },
      'system variables': {
        variables: [{ name: '${date:yyyy-MM-dd}', tooltip: "Displays the current date in the format yyyy-MM-dd." }, { name: '${baseURL}', tooltip: "Represents the base URL of the application. e.g: https://app.helicalinsight.com/hiee/" }, { name: '${deepURL}', tooltip: "Contains the deep link URL for specific content or functionality within the website or application. e.g: https://app.helicalinsight.com/hi-ee/#/report-viewer?dir=Sample/Reports&file=Client_wise_Tours_and_expense.hr&mode=open" }], examples: [
          "yyyy - MM - dd: 2024 - 11 - 20(ISO - style date)",
          "dd / MM / yyyy: 20 / 11 / 2024(European style)",
          "MM - dd - yyyy: 11 - 20 - 2024(US style)",
          "EEE, MMM d, yyyy: Wed, Nov 20, 2024(Short day and month names)",
          "MMMM d, yyyy: November 20, 2024(Full month name)",
          "yyyy.MM.dd G 'at' HH: mm: ss z: 2024.11.20 AD at 14: 30: 45 PST",
          "h: mm a: 2: 30 PM(12 - hour clock)",
          "HH: mm: ss: 14: 30: 45(24 - hour clock)",
          "yyyy - MM - dd'T'HH: mm: ss.SSSXXX: 2024 - 11 - 20T14: 30: 45.987 -08:00",
        ]
      }
    }
    return (
      <Col span={24}>
        {Object.keys(data).map((key) => {
          return <Row key={key} style={{ marginBottom: "10px" }}>
            <Col span={24} style={{ display: "flex" }}>
              <span style={{ minWidth: "145px" }}><strong>
                {key + ":"} {data[key]?.examples ? <HITooltip placement="topLeft" title={examplesTooltip(data[key]?.examples, data[key]?.heading)}> <InfoCircleFilled style={{ cursor: 'pointer' }} /></HITooltip> : null}&nbsp;
                &nbsp;&nbsp;</strong></span>
              <div style={{ display: "flex", flexWrap: "wrap" }}>{data[key].variables.map((value, index) => {
                return <div className='md-view-parameter' key={index}>
                  <Tooltip title={value.tooltip} placement="top">
                    <Typography.Text code>
                      <span
                        className='cursor-pointer onhover-bold'
                        onClick={(e) => e.detail === 2 && handleChangeBody(value.name)}
                      >{value.name}&nbsp;&nbsp;</span></Typography.Text>
                  </Tooltip>
                </div>

              })}
              </div>
            </Col>
          </Row>
        })}
        <div>
          <span>
            <strong>Note: </strong>
            If a profile/role attributes is not visible on the preview screen, please logout and login, as newly added profiles/roles are not available to the current user until re-login.
          </span>
        </div>
      </Col>
    )
  }

  const copyToClipboard = (text) => {
    const textarea = document.createElement("textarea");
    textarea.value = text;
    textarea.style.position = "fixed"; // Prevent scrolling to bottom
    textarea.style.left = "-9999px";
    document.body.appendChild(textarea);
    textarea.select();
    document.execCommand("copy");
    document.body.removeChild(textarea);
  };

  const emailTemplate = `Dear \${user}.name,

The report you have scheduled is ready with your data.

Report Name: \${reportName}
Executed Date: \${date:yyyy-MM-dd}

Open this report in the browser:  \${deepURL}

This email is intended for \${user}.email.

Thanks,

Helical Insight`;

  const PanelHeader = (
    <div style={{ display: "flex", justifyContent: "space-between" }}>
      <span>For Example</span>
      <div>
        {copied && <span style={{ marginRight: "10px", color: "#40a9ff" }}>Copied!</span>}
        <Tooltip title="Click to copy the default example">
          <CopyOutlined
            onClick={(e) => {
              copyToClipboard(emailTemplate);
              setCopied(true);
              e.stopPropagation();
              setTimeout(() => setCopied(false), 3000);
            }}
            style={{ cursor: "pointer", fontSize: 18, color: copied ? "#40a9ff" : "inherit" }}
          /></Tooltip>
      </div>
    </div>
  )
  const popOverContent = (
    <Col span={24} className='hi-email-templating font-size-12'>
      <p style={{ wordBreak: "keep-all" }}>You can construct your email body using the below listed predefined
        variables.<br></br> Double click on the variables to add it to the email body</p>
      <hr></hr>
      <Row>
        {renderParameters()}
      </Row>
      <div>
        <Row style={{ marginTop: 10, justifyContent: "space-between", alignItems: "center" }}>

        </Row>
        <Collapse size="small" className="example-collapse">
          <Collapse.Panel header={PanelHeader} key="example">
            <pre>{emailTemplate}</pre>
          </Collapse.Panel>
        </Collapse>
      </div>
    </Col>
  )

  return (
    <Popover
      content={popOverContent}
      trigger="click"
      open={open}
      onOpenChange={handleOpenChange}
    >
      <InfoCircleOutlined
        style={{
          marginLeft: 5, fontSize: 10, cursor: "pointer"
        }}
      />
    </Popover>
  );
}