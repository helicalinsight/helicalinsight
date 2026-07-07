import { appActions } from "../../../../redux/actions";
import { contextMenuEditOptions } from "../../../hi-fileBrowser/constants";

export const handleEditModalOnOK = ({ dispatch, reportProps }) => {
  const extension = "hr";
  dispatch(
    appActions.setEditModeInfo({
      reportProps,
      extension: "hr",
    })
  );

  const routeToUrl = contextMenuEditOptions.find(
    (e) => e.extension === extension
  );
  // console.log("directory", directory, extension, "extension");
  if (routeToUrl) dispatch(appActions.updateRoute(routeToUrl.routeTo));
};
