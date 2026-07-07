import filebrowser from "./filebrowser.request";
import admin from "./admin.request";
import datasource from "./datasource.request";
import metadata from "./metadata.requests";
import hreport from "./hreport.requests";
import dashboard from "./dashboard.requests";
import cannedReport from "./cannedReports.request";
import reportce from "./reportce.request";
import usermodule from "./usermodule.request";
import instantBI from "./instantbi.requests";
import imagemodule from "./imagemodule.requests";

const requests = {
  filebrowser,
  admin,
  datasource,
  metadata: metadata,
  hreport,
  dashboard,
  cannedReport,
  reportce,
  usermodule,
  instantBI,
  imagemodule
};

export default requests;
