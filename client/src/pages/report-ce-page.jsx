import React from "react";

import HIReportCE from "../components/hi-report-ce/hi-report-ce";
// import SaveItems from "../components/hi-fileBrowser/SaveItems";
// import notify from "../components/hi-notifications/notify";
import { useDispatch, useSelector } from "react-redux";
import { HINavbar } from "../components";
import { SaveOutlined, SaveFilled } from "@ant-design/icons";
import { fileBrowserActions } from "../redux/actions";
// import { HIFileBrowser } from "../components";
import { handleSaveReportCeFile } from "../components/hi-report-ce/utils/handleSave";
import { getFormData } from "../components/hi-report-ce/utils/getFormData";
import HILayout from "../layouts/hi-layout";


const HIReportCEPage = () => {
  const dispatch = useDispatch();
  // const Notify = notify();
  // const [showFb, setShowFb] = useState(false);
  const uuid = useSelector((store) => store.reportCe.uuid);
  const typesDetails = useSelector((store) => store.reportCe.typesDetails);
  const dataSourceData = useSelector((store) => store.reportCe.dataSourceData);
  const parametersData = useSelector((store) => store.reportCe.parametersData);
  const reportData = useSelector((store) => store.reportCe.reportData);
  const dashboardLayoutData = useSelector((store) => store.reportCe.dashboardLayoutData);

  const onSave = (e) => {
    // if (!reportData.dataId) {
    //   return Notify.warning({ message: "Please generate any report." });
    // }
    const formData = getFormData(
      typesDetails,
      dataSourceData,
      dashboardLayoutData,
      parametersData,
      reportData
    );

    if (uuid) {
      handleSaveReportCeFile(dispatch, formData);
    } else {
      // setShowFb(true);
      dispatch(fileBrowserActions.setShowFileBrowser(true));
    }
  };

  const onSaveAs = () => {
    // setShowFb(true);
    dispatch(fileBrowserActions.setShowFileBrowser(true));
  };

  // const saveCallback = (dir, fileName, saveToredux) => {
  //   const formData = getFormData(
  //     typesDetails,
  //     dataSourceData,
  //     dashboardLayoutData,
  //     parametersData,
  //     reportData,
  //     dir,
  //     fileName
  //   );

  //   if (dir && fileName) {
  //     handleSaveReportCeFile(dispatch, formData, Notify);
  //   }
  // };

  const taskBarItems = [
    {
      tooltip: "Save",
      icon: <SaveOutlined />,
      dropdown: [
        {
          tooltip: "Save",
          name: "Save",
          icon: <SaveOutlined />,
          callBack: onSave,
        },
        {
          tooltip: "Save As",
          name: "Save As",
          icon: <SaveFilled />,
          callBack: onSaveAs,
        },
      ],
    },
  ];

  return (
    <HILayout
      header={<HINavbar taskbar={taskBarItems} />}
      content={<HIReportCE />}
      defaultSidebar={false}
    />
  );

  // return (
  //   <Layout className="hi-report-ce-container">
  //     <Header className="hi-header hi-navbar">
  //       <HINavbar taskbar={taskBarItems} />
  //     </Header>
  //     <Layout className="hi-report-ce-body">
  //       <Row>
  //         <Col xs={24} md={4}>
  //           <SidebarLayout>
  //             <HIReportCeSidebar />
  //           </SidebarLayout>
  //         </Col>
  //         <Col xs={24} md={20}>
  //           <Content className="hi-body">
  //             <HIBodyLayout customClass={"height100"}>
  //               <HIReportCE />
  //             </HIBodyLayout>
  //           </Content>
  //         </Col>
  //       </Row>
  //     </Layout>

  //     {showFb && (
  //       <HIFileBrowser
  //         footerForm={{
  //           type: "Save",
  //           form: (
  //             <SaveItems
  //               formHeading="Report filename"
  //               onFormSumbit={saveCallback}
  //               saveButtonText="Save Report"
  //               cancelButtonText="Cancel"
  //               inputValue=""
  //             />
  //           ),
  //         }}
  //       />
  //     )}
  //   </Layout>
  // );
};
export { HIReportCEPage };
