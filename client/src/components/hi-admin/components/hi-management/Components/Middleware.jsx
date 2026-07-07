import { useState } from "react";
import { Route, Switch, useRouteMatch } from "react-router-dom";

import { Row, Col, Switch as Toggler, Skeleton } from "antd";
import { useDispatch, useSelector } from "react-redux";
import { updateManagementData } from "../../../../../redux/actions/admin.actions";
import requests from "../../../../../base/requests";
import Settings from "./Settings";
import { appActions } from "../../../../../redux/actions";

const { updateRoute } = appActions;

const Middleware = () => {
  const middleWareData = useSelector((store) => store.admin.managementData);
  const dispatch = useDispatch();
  const [isMiddlewareClicked, setIsMiddlewareClicked] = useState(false);

  const { path } = useRouteMatch();

  const middlewareUri = "core/dataSource/drillConfig";

  const handleMiddlewareClick = () => {
    const formData = middleWareData?.enabled === "true" ? { enabled: false } : { enabled: true };
    setIsMiddlewareClicked(true);

    requests.admin(dispatch).postManagementData(
      formData,
      middlewareUri,
      (res) => {
        const data = res.drill || res;
        dispatch(updateManagementData(data));
        setIsMiddlewareClicked(false);
        dispatch(updateRoute(`${path}`));
      },
      () => {
        setIsMiddlewareClicked(false);
      }
    );
  };

  return (
    <>
      <Row justify="space-between" align="middle" className="middleware-menu-container">
        <Col span={8} className="switch-container">
          {middleWareData ? (
            <Row>
              <Col xs={24} md={7} lg={4}>
                Disable
              </Col>
              <Col xs={10} md={7} lg={4}>
                <Toggler
                  disabled={isMiddlewareClicked}
                  loading={isMiddlewareClicked}
                  checked={middleWareData?.enabled === "true" ? true : false}
                  onClick={handleMiddlewareClick}
                />
              </Col>
              <Col xs={24} md={7} lg={4}>
                Enable
              </Col>
            </Row>
          ) : (
            <Skeleton />
          )}
        </Col>
      </Row>
      <Row className="middleware-routes-data-container">
        {middleWareData?.enabled === "true" ? (
          <Col span={24}>
            <Switch>
              <Route path={`${path}/`}>
                <Settings />
              </Route>
            </Switch>
          </Col>
        ) : (
          <Col span={24} className="alert-info">
            Helical Insight supports middleware such as Apache Drill to work with flat files (csv,
            tsv, psv, json, parquet etc). You need to have separately installed Apache drill
            software in same/different machine. Please make sure that the Apache Drill software is
            up and running. Please Enable this button to integrate the middleware with Helical
            Insight w.r.t Apache Drill installation.
          </Col>
        )}
      </Row>
    </>
  );
};

export default Middleware;
