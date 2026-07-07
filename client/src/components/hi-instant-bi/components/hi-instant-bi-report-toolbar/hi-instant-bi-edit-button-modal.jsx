import { Modal } from "antd";
import { useDispatch, useSelector } from "react-redux";
import { derivedFormDataConvertorToReportProps } from "../../utils/instant-bi-utilities";
import { handleEditModalOnOK } from "./hi-handle-edit-mode-on-ok";
import "./hi-instant-bi-report-toolbar.scss";

const HIInstantBIEditButtonModal = ({
  editModalVisible,
  setEditModalVisible,
}) => {
  const derivedFormdata = useSelector(
    (state) => state.instantBI.derivedFormdata
  );
  const metadata = useSelector((state) => state.instantBI.metadata);
  const dispatch = useDispatch();
  let reportProps = derivedFormDataConvertorToReportProps({
    derivedFormData: derivedFormdata,
    metadata: metadata,
  });
  return (
    <Modal
      visible={editModalVisible}
      okText={"Leave"}
      onOk={() => {
        handleEditModalOnOK({ dispatch, reportProps });
      }}
      onCancel={() => {
        setEditModalVisible(false);
      }}
    >
      Are you sure you want to leave?
    </Modal>
  );
};

export default HIInstantBIEditButtonModal;
