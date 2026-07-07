import { resetShortcuts } from "../../../hi-reports/utils/utilities"
import searchAndReplace from "./search-and-replace-function"

export const eventIgnoreCondition = (event) => {
    return  ( event.target instanceof HTMLInputElement ||
    event.target instanceof HTMLTextAreaElement ||
    event.target?.className?.includes("ql-editor") ||
    (event.ctrlKey && event.key.toLowerCase() === "f") || 
    (event.ctrlKey && event.key.toLowerCase() === "c") )
}

export const hiCommonShortcuts = (dispatch, commonRefs, event) => {
    if (event.ctrlKey && event.key.toLowerCase() === "s") {
        event.preventDefault()
        commonRefs.saveRef.current()
        resetShortcuts(dispatch)
      }

      if (event.ctrlKey && event.key.toLowerCase() === "z") {
        event.preventDefault()
        commonRefs.undoRef.current()
        resetShortcuts(dispatch)
      }

      if (event.ctrlKey && event.key.toLowerCase() === "y") {
        event.preventDefault()
        commonRefs.redoRef.current()
        resetShortcuts(dispatch)
      }

      if (event.ctrlKey && event.key.toLowerCase() === "h") {
        event.preventDefault();
        searchAndReplace()
        resetShortcuts(dispatch)
      }
}