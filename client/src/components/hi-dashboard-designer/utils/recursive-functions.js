import _ from "lodash";
import { getFieldDisplayName } from "../../../utils/utilities";

export const unGroup = (data, id) => {
  let children = null;
  let resultArr = [];
  if (data && id) {
    data?.forEach((item) => {
      if (item.id === id) {
        children = item.children;
      } else {
        if (item.children) {
          item.children = unGroup(item.children, id);
        }
        resultArr.push(item);
      }
    });
    if (children) {
      resultArr = [...resultArr, ...children];
    }
    return resultArr;
  } else {
    return data;
  }
};

export const getFilteredGridItemsData = (data, id) => {
  return data?.filter((item) => {
    if (item.id !== id) {
      if (item.children && item.children.length) {
        item.children = getFilteredGridItemsData(item.children, id);
      }
      return true;
    }
    return false;
  });
};
export const getGridItem = (data, id, updatedGridItemConfig) => {
  let gridItem = null;
  if (!data || !id) {
    return gridItem;
  }
  data.forEach((item) => {
    if (item.id === id) {
      gridItem = item;
    } else if (item.children && item.children.length) {
      if (getGridItem(item.children, id)) {
        gridItem = getGridItem(item.children, id);
      }
    }
  });
  if (updatedGridItemConfig) {
    gridItem.gridItemConfig = updatedGridItemConfig;
  }
  return gridItem;
};

export const updateGridItem = (data, id, updatedGridItem) => {
  return data.map((item) => {
    if (item.id === id) {
      return updatedGridItem;
    } else if (item.children && item.children.length) {
      item.children = updateGridItem(item.children, id, updatedGridItem);
    }
    return item;
  })
};

export const replaceReportRecursive = ({ reportInfo, gridItemsData, replaceReportId, newId }) => {

  return gridItemsData.map((item) => {
    if (item.compType === "dashboard-designer-component" && item.id === replaceReportId) {
      const updatedGridItemConfig = item.gridItemConfig.map(config => {
        if (config.key !== "css" && config.key !== "javascript") return config;
        return { ...config, values: { ...config.values, value: config.values.value.replace(new RegExp(item.id, "g"), newId) } }
      })
      return { ...item, id: newId, reportInfo: reportInfo, gridItemConfig: updatedGridItemConfig };
    } else if (Array.isArray(item.children)) {
      const updatedLayout = item.layout?.map(l => {
        if (l.i !== replaceReportId) return l;
        return { ...l, i: newId };
      });

      if (item.compType === "tab") {
        const updatedtabsInfo = item.tabsInfo.map(tab => {
          if (!tab.item.includes(replaceReportId)) return tab;

          return {
            ...tab,
            item: tab.item.map(i => i === replaceReportId ? newId : i),
            layout: tab.layout.map(l => {
              if (l.i !== replaceReportId) return l;
              return { ...l, i: newId };
            }),
          }
        });

        return { ...item, layout: updatedLayout, tabsInfo: updatedtabsInfo, children: replaceReportRecursive({ reportInfo, gridItemsData: item.children, replaceReportId, newId }) };
      }

      return { ...item, layout: updatedLayout, children: replaceReportRecursive({ reportInfo, gridItemsData: item.children, replaceReportId, newId }) };
    }
    return item;
  });
}

export const injectLayoutToGroupedGridItem = (data, layout, id) => {
  let resultArr = [];
  data.forEach((item) => {
    if (item.id === id) {
      item.layout = layout;
    } else if (item.children && item.children.length) {
      item.children = injectLayoutToGroupedGridItem(item.children, layout, id);
    }
    resultArr.push(item);
  });
  return resultArr;
};

export const changeIsDraggableInLayout = (data, id, layout) => {
  let resultData = [],
    resultLayout = [];
  if (!layout?.some((item) => item.i === id)) {
    resultData = data.map((item) => {
      if (item.children && item.children.length) {
        let obj = changeIsDraggableInLayout(item.children, id, item.layout);
        item.children = obj.resultData;
        item.layout = obj.resultLayout;
      }
      return item;
    });
  } else {
    resultLayout = layout.map((item) => {
      if (item.i === id) {
        item.static = !item.static;
      }
      return item;
    });
  }

  return {
    resultData: resultData.length ? resultData : data,
    resultLayout: resultLayout.length ? resultLayout : layout,
  };
};

export const injectFiltersListAndListenersToGridItem = ({
  data,
  filtersList,
  id,
  reportId,
}) => {
  let resultArr = [];
  data?.forEach((item) => {
    if (item.id === id) {
      item.filters = filtersList.map((item) => ({
        uid: item.uid,
        label: item.label,
        values: item.values,
        alias: item.alias,
        autogen_alias: item.autogen_alias,
      }));
      item.listeners = filtersList.map((item) => getFieldDisplayName(item));
      item.reportId = reportId;
    } else if (item.children && item.children.length) {
      item.children = injectFiltersListAndListenersToGridItem({
        data: item.children,
        filtersList,
        id,
        reportId,
      });
    }
    resultArr.push(item);
  });
  return resultArr;
};

export const changeFiltersDataInGridItems = ({ data }) => {
  let resultArr = [];
  data?.forEach((item) => {
    if (item.filters) {
      item.filters = item.filters.map((item) => ({
        uid: item.uid,
        value: item.value,
      }));
    } else if (item.children && item.children.length) {
      item.children = changeFiltersDataInGridItems({
        data: item.children,
      });
    }
    resultArr.push(item);
  });
  return resultArr;
};

export const injectLastModifiedToGridItem = ({ data, lastModified, id }) => {
  let resultArr = [];
  data?.forEach((item) => {
    if (item.id === id) {
      item.lastModified = lastModified.lastModified;
    } else if (item.children && item.children.length) {
      item.children = injectLastModifiedToGridItem({
        data: item.children,
        lastModified,
        id,
      });
    }
    resultArr.push(item);
  });
  return resultArr;
};

export const getGridItemLayoutObject = ({ data, id, layout }) => {
  let resultObj = {};
  const findingLayout = layout?.find((item) => item.i === id);
  if (!findingLayout) {
    data?.forEach((item) => {
      if (item.children && item.children.length) {
        if (
          getGridItemLayoutObject({
            data: item.children,
            id,
            layout: item.layout,
          })
        ) {
          resultObj = getGridItemLayoutObject({
            data: item.children,
            id,
            layout: item.layout,
          });
        }
      }
      return item;
    });
  } else {
    resultObj = findingLayout;
  }

  return resultObj;
};

export const getUpdatedLayout = ({ data, id, layout, updatedItem }) => {
  let resultData = [],
    resultLayout = [];
  if (!layout?.some((item) => item.i === id)) {
    resultData = data.map((item) => {
      if (item.children && item.children.length) {
        let obj = getUpdatedLayout({
          data: item.children,
          id,
          layout: item.layout,
          updatedItem,
        });
        item.children = obj.resultData;
        item.layout = obj.resultLayout;
      }
      return item;
    });
  } else {
    resultLayout = layout.map((item) => {
      if (item.i === id) {
        item = { ...item, ...updatedItem };
      }
      return item;
    });
  }

  return {
    resultData: resultData.length ? resultData : data,
    resultLayout: resultLayout.length ? resultLayout : layout,
  };
};

export const getTabsInfo = (data, id) => {
  data.forEach(r => {
    if (r.id !== id) return;

    const editValues = r?.gridItemConfig?.find(
      (item) => item.key === "edit"
    )?.values;

    const tabNames = editValues.tabNames.split(',').map(name => name.trim());

    const copiedTabsInfo = r.tabsInfo;
    r.tabsInfo = []

    for (let i = 0; i < parseInt(editValues.numberOfTabs); i++) {
      r.tabsInfo.push({
        tabId: `${r.id}-${i}`,
        item: copiedTabsInfo.find(t => t.tabId === `${r.id}-${i}`)?.item || [],
        name: tabNames[i] || 'Tab ' + (i + 1),
        layout: copiedTabsInfo.find(t => t.tabId === `${r.id}-${i}`)?.layout || []
      })
    }
  })

  return data;
}

export const checkIfTabChild = (gridItem, gridItemsData) => {
  const tabComps = gridItemsData.filter(item => item.compType === "tab");

  for (let tabComp of tabComps) {
    for (let tabInfo of tabComp.tabsInfo) {
      if (tabInfo.item.includes(gridItem?.id)) {
        const layout = tabInfo.layout.find(l => l.i === gridItem?.id)
        return [true, layout];
      }
    }
  }
  return [false, null];
}

export function replacePathsInJson(json, pathList) {
  let pathIndex = 0;

  const traverse = (obj) => {
    if (_.isArray(obj)) {
      return obj.map(traverse);
    } else if (_.isObject(obj)) {
      return _.mapValues(obj, (value, key) => {
        if (key === "path") {
          if (pathIndex < pathList.length) {
            const newPath = pathList[pathIndex++].path;
            return newPath ? newPath : value;
          }
        }
        return traverse(value);
      });
    }
    return obj;
  };

  return traverse(json);
}
