import { Button, Form, Tooltip, Space } from "antd";
import { InfoCircleOutlined } from "@ant-design/icons";
import { useDispatch, useSelector } from "react-redux";
import { fileBrowserActions } from "../../../../../redux/actions";
import ExportModal from "../../../../hi-fileBrowser/components/ExportModal";

const ExportRepository = () => {
  const dispatch = useDispatch();
  // const exportModalData = useSelector(
  //   (store) => store.fileBrowser.exportModalData
  // );
  // const { recordData } = exportModalData;

  return (
    <div className="export-container">
      <Form
        data-testid="hi-admin-exportfiles-form"
        labelCol={{
          span: 12,
        }}
        wrapperCol={{
          span: 14,
        }}
        layout="horizontal"
      >
        <Form.Item
          label={[
            <Tooltip title=" Initiate a full repository export. Please note that this process may take some time depending on the repository size and network speed. Ensure you remain connected until the export is complete. You will receive a notification once your export is ready for download.">
              <InfoCircleOutlined
                style={{ marginRight: "5px", fontSize: "14px" }}
              />
            </Tooltip>,
            "Export Repository",
          ]}
        >
          <Space>
            <Button
              data-testid="hi-admin-exportfiles-btn"
              type="primary"
              onClick={() => {
                dispatch(
                  fileBrowserActions.setExportModalData({
                    visible: true,
                    // recordData: recordData,
                    recordData: {},
                  })
                );
              }}
            >
              Export All
            </Button>
          </Space>
        </Form.Item>
      </Form>
      <ExportModal allRepos={true}/>
    </div>
  );
};

export default ExportRepository;
