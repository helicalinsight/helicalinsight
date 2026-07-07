import { useState, useEffect, useRef } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  Link,
  Route,
  Switch,
  useRouteMatch,
  useHistory,
} from "react-router-dom";
import {
  setViewData,
  setReportsData,
  setDsSericeCall,
  setDataSources,
  setDataSourceTypes,
  setSelectedDriverInfo,
  setDataSourceDriversList,
  setClickedActiveDatabaseData,
  resetStateFields,
  setClickedDataSource,
} from "../../redux/actions/datasource.actions";
import TutorialInfo from "../common/hi-tutorial";
import { v4 as uuidv4 } from "uuid";
import {
  Row,
  Col,
  Menu,
  Card,
  Drawer,
  Button,
  Input,
  Skeleton,
  Space,
} from "antd";
import { CheckCircleFilled, PlusCircleFilled } from "@ant-design/icons";
import { routesUrl } from "../../app/constants";
import { useDebounce } from "../../hooks";
import { appActions } from "../../redux/actions";
import { handleEdit } from "../hi-datasources/Components/datasource-connection";
import UploadFile from "./Components/datasource-upload";
import CreateAndView from "./Components/datasource-create-and-view";
import { CustomIcon } from "../common/custom-icons/CustomIcon";
import requests from "../../base/requests";
import notify from "../hi-notifications/notify";

import "./hi-dataSource.scss";
import DatasourceSkeleton from "../common/custom-icons/CustomSkeletons/DatasourceSkeleton";
import { cloneDeep, isArray } from "lodash";

const { Search } = Input;
const { dataSourceUrl } = routesUrl;

const HIDataSource = () => {
  const dispatch = useDispatch();
  const Notify = notify(dispatch);
  const dsMode = useSelector((store) => store.datasource.dsMode);
  const menuData = useSelector((store) => store.datasource.menuData);
  const dataSources = useSelector((store) => store.datasource.dataSources).sort(
    (a, b) => a.name.localeCompare(b.name)
  );
  const dataSourceTypes = useSelector(
    (store) => store.datasource.dataSourceTypes
  );
  const clickedActiveDatabaseData = useSelector(
    (store) => store.datasource.clickedActiveDatabaseData
  );
  const { applicationSettingsData, activeRoute } = useSelector(
    (state) => state.app
  );
  const dataSourceRequest = requests.datasource(
    dispatch,
    applicationSettingsData.settings
  );
  const selectedDriverInfo = useSelector(
    (store) => store.datasource.selectedDriverInfo
  );
  const history = useHistory();
  let { location } = history;
  let { pathname } = location;

  const [activeTab, setActiveTab] = useState("");
  const [userSearch, setUserSearch] = useState("");
  const [drawerVisible, setDrawerVisible] = useState(false);

  const debounceValue = useDebounce(userSearch, 500);
  const { path, url } = useRouteMatch();
  let apiRef = useRef(null);

  function handleAbort(prop = {}) {
    apiRef.current?.abort(prop);
  }

  useEffect(() => {
    if (activeRoute === dataSourceUrl || activeRoute === `${dataSourceUrl}/`) {
      dispatch(appActions.updateRoute(path + "" + menuData[0].path));
      history.push(path + "" + menuData[0].path)
    }
  }, [activeRoute]);

  useEffect(() => {
    setActiveTab(pathname.split("/")[2]);
  }, [pathname]);

  useEffect(() => {
    // if (process.env.NODE_ENV === "test") {
    //   return null;
    // } else {
    getDatasourceContent();
    // getReportsData();
    // }
  }, []);

  useEffect(() => {
    if (dsMode.mode === "metadataEdit" && dataSources.length > 0) {
      let active = dataSources.filter(
        (eachData) => {
          if (eachData.driver === dsMode.driver) {
            if (eachData.sendVendorName) {
              if (!dsMode.data.vendorName) {
                return false
              }
            }
            return true
          }
          return false
        }
      );
      if (active[0] && active[0].driver === "dynamicSwitch") {
        //todo - this is a temporary fix, should fix this properly
        active = [
          {
            ...active[0],
            type: "sql.jdbc.groovy.managed",
            name: "Groovy Managed Jdbc DataSource",
            classifier: "efwd",
            categoryName: "advanced",
            categoryType: "advanced",
          },
        ];
      } else if (dsMode.data.type === "sql.jdbc.groovy") {
        active = [
          {
            type: "sql.jdbc.groovy",
            name: "Groovy Plain Jdbc DataSource",
            classifier: "efwd",
            categoryName: "advanced",
            categoryType: "advanced",
          },
        ];
      } else if (dsMode.data._dataSourceType === "Plain Jdbc DataSource") {
        active = [
          {
            // ...active[0],
            ...{
              type: "sql.jdbc",
              name: "Plain Jdbc DataSource",
              classifier: "efwd",
              categoryName: "advanced",
              categoryType: "advanced",
            },
          },
        ];
      }
      if (dsMode.data?.vendorName) {
        const activeDS = active.find(ac => ac.name === dsMode.data.vendorName);
        if (activeDS) {
          active[0] = activeDS;
        }
      }

      let activeDS = active[0]
      if (activeDS.url && isArray(activeDS.url)) {
        activeDS.url = activeDS.url[0];
      }
      if (activeDS.labels && isArray(activeDS.labels)) {
        activeDS.labels = activeDS.labels[0];
      }

      dispatch(setClickedActiveDatabaseData(activeDS));
      showDrawer(true);
      
      if (activeDS.sendVendorName) {
        activeDS.vendorName = activeDS.name
      }

      getDataSourceList(activeDS);
      handleEdit({
        dispatch,
        setDsSericeCall,
        Notify,
        data: dsMode.data,
      });
    }
  }, [dsMode, dataSources]);

  const getDatasourceContent = () => {
    const uri = "content/static/getContents";
    dataSourceRequest.postDataSourceData(
      { contentId: "Static/DataSourcesList" },
      uri,
      (res) => {
        dispatch(setDataSources(res.dataSources));
        dispatch(setDataSourceTypes(res.dataSourceTypes));
        dispatch(setDataSourceDriversList(res.driversList));
      },
      (e) => {
        // console.log(e);
      }
    );
  };

  // const getReportsData = () => {
  //   const uri = "monitor/system/reportStats";
  //   const formData = {
  //     extension: ["report", "efw", "metadata", "efwdd", "hcr"],
  //     depth: 1000,
  //   };

  //   dataSourceRequest.postDataSourceData(
  //     formData,
  //     uri,
  //     (res) => {
  //       setReportsData(res);
  //     },
  //     (e) => {
  //       // console.log(e);
  //     }
  //   );
  // };

  const onCloseDrawer = () => {
    setDrawerVisible(false);
    if (selectedDriverInfo?.driver) {
      handleAbort();
    }
    dispatch(resetStateFields(dataSources));
  };

  const showDrawer = () => {
    setDrawerVisible(true);
  };

  const handleDataSourceClick = (data) => {
    const copiedData = cloneDeep(data);
    dispatch(setClickedDataSource(data));

    // if (copiedData.url && Array.isArray(copiedData.url)) {
    //   copiedData.url = copiedData.url[0];
    // }

    if (copiedData.driver || copiedData.categoryName === "advanced") {
      // const updatedData = {
      //   ...data,
      //   vendorName: data.name
      // };

      let updatedData = cloneDeep(copiedData);
      if (updatedData.url) {
        delete updatedData.url
      }
      if (updatedData.labels) {
        delete updatedData.labels
      }

      if (copiedData.sendVendorName) {
        updatedData.vendorName = copiedData.name
      }
      // apiRef.current = data.sendVendorName ? getDataSourceList(updatedData) : getDataSourceList(data);
      apiRef.current = getDataSourceList(updatedData)
    }
    showDrawer();
    dispatch(setClickedActiveDatabaseData(copiedData));
    dispatch(setSelectedDriverInfo(copiedData));
  };

  const getDataSourceList = (data) => {
    return dataSourceRequest.getDataSourceData(data, (res) => {
      apiRef.current = undefined;
      if (data.categoryType === "advanced") {
        dispatch(setViewData(res.dataSources));
      } else {
        const filteredData = res.dataSources.filter(
          (eachData) => eachData.driver && eachData.driver === data.driver
        );
        dispatch(setViewData(filteredData));
      }
    });
  };

  const handleMenuClick = (e) => {
    setActiveTab(e.key);
  };

  const getSearchResults = (type = "") => {
    if (debounceValue === "" && type === "all") {
      return dataSources;
    }

    if (debounceValue === "" && type === "advanced") {
      return dataSourceTypes;
    }

    const filteredDataSources = [...dataSources, ...dataSourceTypes].filter(
      (eachData) =>
        eachData.name.toLowerCase().includes(userSearch.toLowerCase())
    );

    return filteredDataSources;
  };

  const renderDataSource = (eachData) => {
    return (
      <Card.Grid
        key={uuidv4()}
        className="gridstyle"
        onClick={() => handleDataSourceClick(eachData)}
      >
        {eachData.driver !== undefined ? (
          <CheckCircleFilled className="checked-icon" />
        ) : (
          eachData.categoryName !== "advanced" && (
            <PlusCircleFilled className="add-icon" />
          )
        )}
        <CustomIcon name={eachData.name} />
        <span data-testid={eachData.name} className="title ellipsis">
          {eachData.name}
        </span>
      </Card.Grid>
    );
  };

  const searchedData = getSearchResults("all");
  const advancedSearchedData = getSearchResults("advanced");

  return (
    <Card className="hi-datasource-card">
      <Row className="datasource-container">
        <Col span={24} className="datasource-header">
          <Row justify="space-between" align="middle">
            <Col
              xs={24}
              md={3}
              lg={3}
              style={{ textAlign: "center" }}
              data-testid="choose-database"
            >
              <TutorialInfo elementKey="hi-datasource">
                Choose Database
              </TutorialInfo>
            </Col>
            <Col xs={24} md={10} lg={16}>
              <Card bordered={false} hoverable className="menu-card">
                <Menu
                  mode="horizontal"
                  onClick={handleMenuClick}
                  selectedKeys={[activeTab]}
                  className="datasource-menu"
                >
                  {menuData.map((eachMenu) => (
                    <Menu.Item key={eachMenu.id} data-testid={eachMenu.id}>
                      <Link to={`${url}${eachMenu.path}`}>{eachMenu.name}</Link>
                    </Menu.Item>
                  ))}
                </Menu>
              </Card>
            </Col>
            <Col xs={24} md={10} lg={4}>
              <Search
                placeholder="search database"
                onChange={(e) => setUserSearch(e.target.value)}
                enterButton
                allowClear
              />
            </Col>
          </Row>
        </Col>
        {dataSources.length ? (
          <Col span={24} className="datasource-content">
            {debounceValue !== "" &&
              (!searchedData.length || !advancedSearchedData.length) ? (
              <p className="search-warning">No Datasource Found</p>
            ) : (
              <Card bordered={false} className="datasource-card">
                <Switch>
                  <Route exact path={`${path}/all`}>
                    {searchedData.map((eachData) => {
                      return renderDataSource(eachData);
                    })}
                  </Route>
                  <Route exact path={`${path}/supported`}>
                    {searchedData
                      .filter(
                        (eachData) => eachData.categoryName === "Supported"
                      )
                      .map((eachData) => {
                        return renderDataSource(eachData);
                      })}
                  </Route>
                  <Route exact path={`${path}/bigdata`}>
                    {searchedData
                      .filter(
                        (eachData) => eachData.categoryName === "Big Data"
                      )
                      .map((eachData) => {
                        return renderDataSource(eachData);
                      })}
                  </Route>
                  <Route exact path={`${path}/flatfiles`}>
                    {searchedData
                      .filter(
                        (eachData) => eachData.categoryName === "Flat Files"
                      )
                      .map((eachData) => {
                        return renderDataSource(eachData);
                      })}
                  </Route>
                  <Route exact path={`${path}/rdbms`}>
                    {searchedData
                      .filter((eachData) => eachData.categoryName === "RDBMS")
                      .map((eachData) => {
                        return renderDataSource(eachData);
                      })}
                  </Route>
                  <Route exact path={`${path}/nosql`}>
                    {searchedData
                      .filter(
                        (eachData) =>
                          eachData.categoryName === "No SQL & Big Data"
                      )
                      .map((eachData) => {
                        return renderDataSource(eachData);
                      })}
                  </Route>
                  <Route exact path={`${path}/advanced`}>
                    {advancedSearchedData
                      .filter(
                        (eachData) => eachData.categoryName === "advanced"
                      )
                      .map((eachData) => {
                        return renderDataSource(eachData);
                      })}
                  </Route>
                </Switch>
              </Card>
            )}
          </Col>
        ) : (
          <Col span={24} style={{ height: "100%" }}>
            <Card>
              <DatasourceSkeleton />
            </Card>
          </Col>
        )}
      </Row>
      {drawerVisible && <Drawer
        title={`${clickedActiveDatabaseData?.name}`}
        width={720}
        onClose={onCloseDrawer}
        visible={drawerVisible}
        zIndex={999}
        // bodyStyle={{ paddingBottom: 80 }}
        extra={<Button onClick={onCloseDrawer}>Cancel</Button>}
      >
        {clickedActiveDatabaseData.driver !== undefined ||
          clickedActiveDatabaseData.categoryType === "advanced" ? (
          <CreateAndView />
        ) : (
          <UploadFile
            getData={getDatasourceContent}
            onCloseDrawer={onCloseDrawer}
          />
        )}
      </Drawer>}
    </Card>
  );
};

export default HIDataSource;
