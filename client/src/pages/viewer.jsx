import { HelicalReports, DashboardDesigner, HIInstantBI,HIAIAGENT, CannedReportsPage, ImageViewer } from ".";

// import { DashboardDesigner } from "./designer-page";

const ViewMode = (props) => {
  let mapComponent = null;
  const componentMap = {
    // report: HelicalReports,
    hr: HelicalReports,
    hcr: CannedReportsPage,
    efwdd: DashboardDesigner,
    instant: HIInstantBI,
    // aiAgent:HIAIAGENT,
    image: ImageViewer
  };
  let {
    viewModeInfo,
    parameters,
    renderTaskbar,
    setFileInfo,
    setParametersReview,
  } = props;
  mapComponent = viewModeInfo.extension ?? viewModeInfo.file.name.split(".")[1];
  if (['png', 'jpg', 'jpeg', 'gif', 'webp', 'bmp'].includes(mapComponent)) mapComponent = 'image';
  const ViewComponent = componentMap[mapComponent];
  const { file, filters, mode } = viewModeInfo;
  return ViewComponent ? (
    <ViewComponent
      file={file}
      filters={filters}
      mode={mode}
      parameters={parameters}
      renderTaskbar={renderTaskbar}
      setFileInfo={setFileInfo}
      setParametersReview={setParametersReview}
    />
  ) : (
    <></>
  );
};

export default ViewMode;
