import DataSourceCreateAndEdit from "./datasource-create-and-edit";
import DataSourceView from "./datasource-view";
import DataSourceConnection from "./datasource-connection.jsx";
import { useEffect, useState } from "react";
import { setIsEditClicked } from "../../../redux/actions/datasource.actions";
import { YoutubeOutlined } from "@ant-design/icons";
import { useDispatch, useSelector } from "react-redux";
import {
  Space,
  Row,
  Col,
  Divider,
  Timeline,
  Tabs,
  Typography,
  Skeleton,
} from "antd";
import DatasourceViewSkeleton from "../../common/custom-icons/CustomSkeletons/DatasourceViewSkeleton";

const { TabPane } = Tabs;
const { Text } = Typography;

const CreateAndView = () => {
  const dispatch = useDispatch();
  const isEditClicked = useSelector((store) => store.datasource.isEditClicked);
  const viewData = useSelector((store) => store.datasource.viewData);
  const [activeKey, setActiveKey] = useState(
    isEditClicked ? "2" : viewData && viewData.length > 0 ? "3" : "1"
  );
  const clickedActiveDatabaseData = useSelector(
    (store) => store.datasource.clickedActiveDatabaseData
  );
  const isConnectionSuccess = useSelector(
    (store) => store.datasource.isDatasourceConnectionSuccess
  );
  const [reRenderKey, setReRenderKey] = useState(Date.now());

  function onChangeTab(key) {
    setActiveKey(key);
    if (
      clickedActiveDatabaseData.name !== "Groovy Managed Jdbc DataSource" &&
      clickedActiveDatabaseData.name !== "Groovy Plain Jdbc DataSource"
    ) {
      setReRenderKey(Date.now());
    }
    if (key !== "3") {
      dispatch(setIsEditClicked(false));
    }
  }

  return (
    <>
      {isConnectionSuccess ? (
        <DataSourceConnection />
      ) : viewData ? (
        <Tabs
          defaultActiveKey={
            isEditClicked ? "2" : viewData.length > 0 ? "3" : "1"
          }
          onChange={onChangeTab}
        >
          <TabPane tab="Create" key="1">
            <DataSourceCreateAndEdit editable={false} activeKey={activeKey} key={reRenderKey} />
          </TabPane>
          {isEditClicked ? (
            <TabPane tab="Edit" key="2">
              <DataSourceCreateAndEdit editable={true} activeKey={activeKey} key={reRenderKey} />
            </TabPane>
          ) : (
            <TabPane tab="View" key="3">
              <DataSourceView key={reRenderKey} />
            </TabPane>
          )}
        </Tabs>
      ) : (
        <DatasourceViewSkeleton />
      )}

      <Divider />
      <Row gutter={[16, 16]}>
        <Col span={24}>Driver Details</Col>
        <Col span={24}>
          <ul>
            <li>
              DataSource Name :
              <Text code>{clickedActiveDatabaseData.name}</Text>{" "}
            </li>
            <li>
              DataSource Category :
              <Text code>{clickedActiveDatabaseData.categoryName}</Text>
            </li>
          </ul>
        </Col>
        <Col span={24}>
          <Space>
            <a
              href="https://www.helicalinsight.com/adhoc-datasource/"
              target="_blanck"
            >
              <YoutubeOutlined />
              Tutorials
            </a>
          </Space>
        </Col>
        <Col span={24}>
          <p>Create Data Source by providing the required details</p>
          <Timeline>
            <Timeline.Item>
              Connect to your data by creating the datasource.
            </Timeline.Item>
            <Timeline.Item>
              Using the created datasource, create metadata where you can choose
              your tables,columns etc.
            </Timeline.Item>
            <Timeline.Item>
              With created MetaData ,create reports by dragging and dropping the
              fields.
            </Timeline.Item>
            <Timeline.Item>
              Visualize and get Insight of the data effectively with various
              options like tables,charts etc!!!
            </Timeline.Item>
          </Timeline>
          <p>
            **Share the datsource with various users, roles and organizations.
          </p>
        </Col>
      </Row>
    </>
  );
};

export default CreateAndView;
