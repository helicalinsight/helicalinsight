import React, { useEffect, useRef } from "react";
import { Typography, Input, Button, Form } from "antd";
import { useSelector, useDispatch } from "react-redux";
import { fileBrowserActions } from "../../../redux/actions";
import { SelectedItemBreadcrumb } from ".";
const { Title } = Typography;

const SaveFileForm = (props) => {
  const dispatch = useDispatch();
  const saveForm = useRef(null);
  const selectedFolderRow = useSelector(
    (state) => state.fileBrowser.expandedRow
  );
  const layout = {
    labelCol: { span: 0 },
    wrapperCol: { span: 8 },
  };
  const onFinish = ({ name }) => {
    dispatch(
      fileBrowserActions.setSaveFileDetails({
        name: name,
        path: selectedFolderRow.path,
      })
    );
    dispatch(fileBrowserActions.setShowFileBrowser(false));
    // const encodedString = btoa(JSON.stringify(formData))
    //   requests.filebrowser.filebrowser.filebrowser.postSaveFileToFolder(uri,encodedString, (res) => {
    //   console.log(res,'mmmmm')
    // }, (error) => {
    //   //
    // })
  };
  useEffect(() => {
    if (saveForm && saveForm.current) {
      saveForm.current.focus();
    }
  }, []);

  return (
    <React.Fragment>
      <SelectedItemBreadcrumb pathItem={selectedFolderRow} />
      <Title level={5}>Save file</Title>
      <Form data-testid = "hi-file-browser-saveFileForm" {...layout} onFinish={onFinish}>
        <Form.Item name="name" label="Name" rules={[{ required: true }]}>
          <Input ref={saveForm} />
        </Form.Item>
        <Form.Item>
          <Button type="primary" htmlType="submit" style={{ marginRight: 8 }}>
            Save
          </Button>
          <Button type="danger" htmlType="button" onClick={props.onCancelClick}>
            Cancel
          </Button>
        </Form.Item>
      </Form>
    </React.Fragment>
  );
};

export { SaveFileForm };
