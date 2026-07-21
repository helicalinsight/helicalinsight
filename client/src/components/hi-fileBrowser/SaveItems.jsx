import React, { useState, useEffect, useRef } from "react";
import { Typography, Input, Button, Form } from "antd";
import { useSelector, useDispatch } from "react-redux";
import { SelectedItemBreadcrumb } from "./components";
import { fileBrowserActions } from "../../redux/actions";
import { addFileToFolder } from "./helperMethods.js";
import notify from "../hi-notifications/notify";
const { Title } = Typography;

const SaveItems = ({
  formHeading,
  onFormSumbit,
  saveButtonText,
  cancelButtonText,
  inputValue,
  onNameChange,
  validateName = null
}) => {
  const dispatch = useDispatch();
  const Notify = notify(dispatch);
  const [defaultInput, setDefaultInput] = useState(inputValue || "");
  const clickedContextRecord = useSelector(
    (state) => state.fileBrowser.clickedContextItemDetails.clickedRecord
  );
  const files = useSelector((state) => state.fileBrowser.files.data);
  const saveForm = useRef(null);
  const inputFocus = useRef(null);
  const layout = {
    labelCol: { span: 0 },
    wrapperCol: { span: 8 },
  };
  useEffect(() => {
    if (inputFocus && inputFocus.current) {
      inputFocus.current.focus();
    }
  }, []);

  useEffect(() => {
    const nextValue = inputValue || "";
    setDefaultInput(nextValue);
    saveForm.current?.setFieldsValue?.({ name: nextValue });
  }, [inputValue]);

  const handleNameChange = (value) => {
    setDefaultInput(value);
    onNameChange?.(value);
  };

  const onCancelClick = () => dispatch(fileBrowserActions.setShowFileBrowser(false));
  const saveToRedux = (savedFile) => {
    const updatedResult = addFileToFolder(JSON.parse(JSON.stringify(files)), savedFile, clickedContextRecord.path)
    dispatch(
      fileBrowserActions.setFbContent({
        loading: false,
        data: updatedResult,
        error: null,
      })
    );
  }
  const onSubmit = () => {
    if(!clickedContextRecord){
      Notify.error({message: "Please select a directory to save", type: "Frontend" });
    }
    if(clickedContextRecord){
      onCancelClick()
      onFormSumbit(clickedContextRecord, defaultInput)
    }
  };
  return (
    <React.Fragment>
      <SelectedItemBreadcrumb pathItem={clickedContextRecord} />
      <Title level={5}>{formHeading || "Save file"}</Title>
      <Form
        data-testid="hi-file-browser-saveItems"
        {...layout}
        onFinish={onSubmit}
        ref={saveForm}
        initialValues={{ name: defaultInput }} // added this because the input box is empty when saving metadta - modified by gagan 16th March 2022
      >
        <Form.Item
          name="name"
          label="Name"
          rules={[
            { required: true },
            ({ getFieldValue }) => ({
              validator(_, value) {
                if (value) {
                  if(getFieldValue("name").length < 3) {
                    return Promise.reject(
                      new Error("Name must be at least three characters long.")
                    );
                  }
                  if(validateName && validateName(value)){
                    const error = validateName(value);
                    return Promise.reject(
                      new Error(error)
                    );
                  }
                  if (
                    value &&
                    getFieldValue("name").length >= 3 &&
                    getFieldValue("name").length <= 60 &&
                    !value.match(/^\w[\w \.\-\&\#\+\~]*/i)
                  ) {
                    return Promise.reject(
                    new Error("Name should not starts with a special characters.")
                    );
                  }
                }
                return Promise.resolve();
              },
            }),
          ]}
        >
          <Input
            value={defaultInput}
            ref={inputFocus}
            onChange={(e) => handleNameChange(e.target.value)}
          />
        </Form.Item>
        <Form.Item>
          <Button
            htmlType="button"
            onClick={onCancelClick}
            style={{ marginRight: 8 }}
          >
            {cancelButtonText || "Cancel"}
          </Button>
          <Button type="primary" htmlType="submit">
            {saveButtonText || "Save"}
          </Button>
        </Form.Item>
      </Form>
    </React.Fragment>
  );
};

export default SaveItems;
