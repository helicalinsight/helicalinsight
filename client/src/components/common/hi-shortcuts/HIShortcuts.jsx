import { useCallback, useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { appActions, setShotCutCurrentLocation } from "../../../redux/actions";
import { eventIgnoreCondition, hiCommonShortcuts } from "./utils/utils";

const HIShortcuts = (props) => {
  const dispatch = useDispatch();
  const {moduleData} = props
  const handleKeyDown = useCallback((event) => {
    if (!eventIgnoreCondition(event)) {
      event.preventDefault();
      event.stopPropagation();
      dispatch(appActions.setKeysPressed(event.key));
      if (moduleData?.name ==="HelicalReport") {
        const {commonRefs} = moduleData;
        hiCommonShortcuts(dispatch, commonRefs, event)
      }

      if (moduleData?.name ==="DashboardDesigner" ) {
        const {commonRefs} = moduleData;
        hiCommonShortcuts(dispatch, commonRefs, event)
      }

      if (event.ctrlKey && event.key.toLowerCase() === 'c') {
        // Get the selected text or content
        const selectedText = window.getSelection().toString();
        if (selectedText && navigator.clipboard) {
          // Copy the selected text to the clipboard
          navigator.clipboard.writeText(selectedText);
        }
      }
    }
  }, []);

  const handleWindowClick = useCallback((event) => {
      if( !["hr-export","hr-save","dd-save","hr-layout"].includes(event.target.id)) {
          dispatch(appActions.setKeysPressed("reset"));
          dispatch(setShotCutCurrentLocation(""))
      }
  }, []);

  useEffect(() => {
    // This code will execute whenever the component is mounted
    window.addEventListener("click", handleWindowClick);
    // This code will execute whenever the component is unmounted
    return () => {
      window.removeEventListener("click", handleWindowClick);
    };
  }, [handleWindowClick]);

  useEffect(() => {
    // attach the event listener
    document.addEventListener("keydown", handleKeyDown);
    // remove the event listener
    return () => {
      document.removeEventListener("keydown", handleKeyDown);
    };
  }, [handleKeyDown]);
  return null;
};

export default HIShortcuts;
