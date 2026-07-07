import { Modal } from "antd";
import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { HashRouter, useHistory } from "react-router-dom";
import { checkReport } from "../app/helperMethods";
import { HIRoutes } from "../components";
import { applicationSettingsHandler } from "../customHooks/applicationSettingsCheckLayer";
import useCookieManager from "../customHooks/cookieManager";
import { appActions } from "../redux/actions";
import { ReportViewer } from "./report-viewer";

const { updateRoute, setShouldBlockNavigation } = appActions;

const HIRouter = ({ report, auth }) => {
  useCookieManager(auth);
  const dispatch = useDispatch();
  const settings = useSelector((state) => state.app.applicationSettingsData.settings);
  const [isReportDetailsOk, setIsReportDetailsOk] = useState(false);

  useEffect(() => {
    if(isReportDetailsOk){
      applicationSettingsHandler({ dispatch, report });
    }
  }, [isReportDetailsOk]);

  useEffect(() => {
    if(auth && auth.embedMode === false){
      setIsReportDetailsOk(true)
    }else{
      if(report || auth){
        setIsReportDetailsOk(checkReport({ report, auth }));
      }
    }
  }, []);

  // const getConfirm = (content, callback) => {
  //   Modal.confirm({
  //     title: 'Confirm',
  //     content: content,
  //     onOk: () => {
  //       // dispatch(setShouldBlockNavigation(false));
  //       callback(true);
  //       dispatch(appActions.aboutToChangeRoute(false))
  //       // dispatch(updateRoute(content.path));
  //     },
  //     onCancel: () => {
  //       // dispatch(setShouldBlockNavigation(false));
  //       dispatch(appActions.aboutToChangeRoute(null))
  //       callback(false);
  //     }
  //   });
  // };
  // getUserConfirmation={getConfirm}
  let content = null
  if((report || auth)){
    if(settings){
      content = <ReportViewer report={auth.embedMode === false ? null : report} urlObj={auth?.rest} />
    }
  }else{
    content = <HIRoutes />
  }
  return (
    <HashRouter >
      {content}
    </HashRouter>
  );
};
// {report ? (settings ? <ReportViewer report={report} /> : <></>) : <HIRoutes />}

export { HIRouter };
