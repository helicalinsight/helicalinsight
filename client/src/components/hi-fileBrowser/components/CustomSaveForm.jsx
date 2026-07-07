import React, { useState, useEffect, useRef } from "react";
import { Typography, Input, Button, Form } from "antd";
import { useSelector } from "react-redux";
import { SelectedItemBreadcrumb } from ".";
import { capitalize } from "lodash";
const { Title } = Typography;

const CustomSaveForm = ({
  onFormConfirm,
  title,
  onCancelClick,
  buttonText,
  addToRoot
}) => {
  const clickedContextRecord = useSelector(
    (state) => state.fileBrowser.clickedContextItemDetails.clickedRecord
  );
  const [defaultInput, setDefaultInput] = useState("");
  const saveForm = useRef(null);
  const layout = {
    labelCol: { span: 0 },
    wrapperCol: { span: 8 },
  };

  const onSubmit = () => {
  };

  useEffect(() => {
    if (saveForm && saveForm.current) {
        saveForm.current.focus();
    }
  }, []);

  return (
    <React.Fragment>
      {!addToRoot ?
      <>
        <SelectedItemBreadcrumb pathItem={clickedContextRecord} />
        <Title level={5}>{title}</Title>
      </> : 
        <Title level={5}>Add folder to root</Title>
      }
      <Form {...layout} name="control-hooks">
        <Form.Item label="Name" rules={[{ required: true }]}>
          <Input
            value={defaultInput}
            onChange={(e) => setDefaultInput(e.target.value)}
            ref={saveForm}
          />
        </Form.Item>
        <Form.Item>
          <Button
            type="primary"
            htmlType="submit"
            style={{ marginRight: 8 }}
            onClick={onSubmit}
          >
            {capitalize(buttonText)}
          </Button>
          <Button type="danger" htmlType="button" onClick={onCancelClick}>
            Cancel
          </Button>
        </Form.Item>
      </Form>
    </React.Fragment>
  );
};

export { CustomSaveForm };
