import { ShareFinalModal } from "../../hi-fileBrowser/components";
import requests from "../../../base/requests";
import notify from "../../hi-notifications/notify";
import { useDispatch, useSelector } from "react-redux";
import { Button, Card, Col, Row, Skeleton } from "antd";
import { fileBrowserActions, updateMetadataState, appActions } from "../../../redux/actions";
import {
  CheckCircleOutlined,
  ShareAltOutlined,
  EditOutlined,
  InsertRowBelowOutlined,
  CloseCircleOutlined,
} from "@ant-design/icons";
import {
  setDataSourceConnection,
  setClickedRecordData,
  setIsEditClicked,
  setDsSericeCall,
  setEditData,
  resetStateFields,
  setFileBrowserFolder,
} from "../../../redux/actions/datasource.actions";
import { checkIfGroovyManaged } from "../utils/checkIfGroovyManaged";
import { checkIfGroovyPlain } from "../utils/checkIfGroovyPlain";

export const handleEdit = ({
  dispatch,
  setDsSericeCall,
  connectionData,
  clickedActiveDatabaseData,
  Notify,
  data,
}) => {
  dispatch(setDsSericeCall(true));
  let formData;
  if (connectionData) {
    dispatch(setClickedRecordData(connectionData));
  }
  const uri = "core/dataSource/read";
  if (data) {
    formData = data;
  } else {
    formData = {
      id: connectionData.data.id,
      type: connectionData.data.type,
      classifier: clickedActiveDatabaseData.classifier,
      dir: connectionData.data.dir,
    };
  }
  dispatch(setEditData({}));
  requests.datasource(dispatch).postDataSourceData(
    formData,
    uri,
    (res) => {
      dispatch(setDsSericeCall(false));
      dispatch(setIsEditClicked(true));
      dispatch(setEditData(res));
      dispatch(setDataSourceConnection(false));
      if (checkIfGroovyManaged(res) || checkIfGroovyPlain(res) || res?.data?.type === 'sql.jdbc') {
        dispatch(
          setFileBrowserFolder({
            path: res.data.dir,
          })
        )
      }
    },

    (e) => {
      dispatch(setDsSericeCall(false));
      // Notify.error({
      //   type: "Network Call",
      //   ...e,
      // });
    }
  );
};

const DataSourceConnection = () => {
  const dispatch = useDispatch();
  const isShareVisible = useSelector((store) => store.fileBrowser.isShareModalVisible);
  const clickedActiveDatabaseData = useSelector(
    (store) => store.datasource.clickedActiveDatabaseData
  );
  const isTestConnectionSuccess = useSelector((store) => store.datasource.isTestConnectionSuccess);
  const connectionData = useSelector((store) => store.datasource.connectedDatasourceData);
  const viewData = useSelector((store) => store.datasource.viewData);

  const Notify = notify(dispatch);

  const activeData = viewData.filter((eachData) => eachData.data.id.toString() === connectionData.data.id);

  const closeConnection = () => {
    dispatch(setIsEditClicked(false));
    dispatch(setDataSourceConnection(false));
  };

  const handleCreateMetaData = (event) => {
    if(!isTestConnectionSuccess){
      return;
    }
    // #4128 fix by gagan - start
    dispatch(
      updateMetadataState({
        key: "activeDataSource",
        value: connectionData,
      })
    );
    dispatch(appActions.updateRoute("/metadata"));
    // #4128 fix by gagan - end
    dispatch(resetStateFields([]));
    if (isTestConnectionSuccess === false) {
      event.preventDefault();
    }
  };

  const handleShare = () => {
    dispatch(fileBrowserActions.setShareModalVisibility());
  };

  const showCard = {
    height: "130px",
    textAlign: "center",
    opacity: "1",
  };

  const hideCard = {
    height: "130px",
    textAlign: "center",
    opacity: "0.5",
    cursor: "none",
  };

  const renderSuccess = () => {
    return (
      <Row data-testid = "hi-datasource-connection" gutter={[16, 16]}>
        <Col span={24} style={{ textAlign: "end" }} className="close-button">
          <Button type="text" icon={<CloseCircleOutlined />} onClick={closeConnection} />
        </Col>
        <Col span={24} style={{ textAlign: "center" }}>
          <CheckCircleOutlined style={{ color: "green" }} />
          {connectionData.message.includes("updated") ? (
            <p>Datasource Updated Successfully</p>
          ) : (
            <p>Datasource Created Successfully</p>
          )}

          <p>You may choose one of the following options for further actions</p>
        </Col>
        <Col span={8} onClick={handleCreateMetaData}>
          <Card hoverable style={isTestConnectionSuccess ? showCard : hideCard}>
            <InsertRowBelowOutlined />
            <p>Create Metadata</p>
          </Card>
        </Col>
        <Col
          span={8}
          onClick={() =>
            handleEdit({
              dispatch,
              setDsSericeCall,
              connectionData,
              clickedActiveDatabaseData,
              Notify,
            })
          }
        >
          <Card hoverable style={showCard}>
            <EditOutlined />
            <p>Edit</p>
          </Card>
        </Col>
        <Col span={8} onClick={handleShare}>
          <Card
            hoverable
            style={activeData && activeData[0].permissionLevel < 5 ? hideCard : showCard}
          >
            <ShareAltOutlined />
            <p>Share</p>
          </Card>
        </Col>
        {isShareVisible && (
          <ShareFinalModal
            from="datasource"
            shareOptions={{
              classifier: clickedActiveDatabaseData.classifier,
              dataSourceProvider: connectionData.data.dataSourceProvider,
              id: connectionData.data.id,
              ...(clickedActiveDatabaseData.classifier === "efwd" && {
                dir: connectionData.data.dir,
              }),
              type: "dataSource",
            }}
          />
        )}
      </Row>
    );
  };

  return activeData.length > 0 ? renderSuccess() : <Skeleton />;
};

export default DataSourceConnection;
