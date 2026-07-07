import actionTypes from "./actionTypes";

export const updateSearchValue = (data) => {
  return {
    type: actionTypes.UPDATE_SEARCH_VALUE,
    payload: data,
  };
};

export const loadInstantBIMetadata = (data) => {
  return {
    type: actionTypes.LOAD_INSTANTBI_METADATA,
    payload: data,
  };
};

export const updateInstantBIReportFile = (data) => {
  return {
    type: actionTypes.UPDATE_INSTANTBI_REPORT_FILE,
    payload: data,
  };
};
export const updateInstantBIMode = (data) => {
  return {
    type: actionTypes.UPDATE_INSTANTBI_MODE,
    payload: data,
  };
};
export const savedConfigInstantBIFile = (data) => {
  return {
    type: actionTypes.SAVED_INSTANT_BI_REPORT_FILE,
    payload: data,
  };
};
export const setInstantBIPageLoading = (data) => {
  return {
    type: actionTypes.INSTANT_BI_PAGE_LOADING,
    payload: data,
  };
};
export const loadDerivedFormData = (data) => {
  return {
    type: actionTypes.INSTANT_BI_LOAD_DERIVED_FORMDATA,
    payload: data,
  };
};

export const updateInstantBILayout = (data) => {
  return {
    type: actionTypes.UPDATE_INSTANT_BI_LAYOUT,
    payload: data
  }
}

export const updateIBChatMessageList = (data) => {
  return {
    type: actionTypes.UPDATE_IB_CHAT_MESSAGE_LIST,
    payload: data
  }
}

export const updateBIBotStatus = (data) => {
  return {
    type: actionTypes.UPDATE_IB_BOT_STATUS,
    payload: data
  }
}

export const loadInitialIBReport = (data) => {
  return {
    type: actionTypes.LOAD_INITIAL_IB_REPORT,
    payload: data
  }
}

export const removeIBReport = (data) => {
  return {
    type: actionTypes.REMOVE_IB_REPORT,
    payload: data
  }
}

export const changeIBReport = (data) => {
  return {
    type: actionTypes.CHANGE_IB_REPORT,
    payload: data
  }
}

export const addNewIBReport = (data) => {
  return {
    type: actionTypes.ADD_NEW_IB_REPORT,
    payload: data
  }
}

export const updateIBPreviewData = (data) => {
  return {
    type: actionTypes.UPDATE_IB_PREVIEW_DATA,
    payload: data
  }
}

export const updateIBActivePreview = (data) => {
  return {
    type: actionTypes.UPDATE_IB_ACTIVE_PREVIEW,
    payload: data
  }
}

export const removeIBPreview = (data) => {
  return {
    type: actionTypes.REMOVE_IB_PREVIEW,
    payload: data
  }
}

export const addIBRecommendations = (data) => {
  return {
    type: actionTypes.ADD_IB_RECOMMENDATIONS,
    payload: data
  }
}

export const loadingIBRecommendations = (data) => {
  return {
    type: actionTypes.LOADING_IB_RECOMENDATIONS,
    payload: data
  }
}

export const updateRecommendationsVisibility = (data) => {
  return {
    type: actionTypes.UPDATE_RECOMMENDATIONS_VISIBILITY,
    payload: data
  }
}

export const loadInstantBIReportData = (data) => {
  return {
    type: actionTypes.LOAD_IB_REPORT_DATA,
    payload: data
  }
}
export const loadIBOpenChatResponse = (data) => {
  return {
    type: actionTypes.LOAD_IB_OPEN_CHAT_RESPONSE,
    payload: data
  }
}

export const changeIBInputValue = (data) => {
  return {
    type: actionTypes.CHANGE_INPUT_VALUE,
    payload: data
  }
}

export const resetIBChatId = (data) => {
  return {
    type: actionTypes.RESET_IB_CHAT_ID,
    payload: data
  }
}