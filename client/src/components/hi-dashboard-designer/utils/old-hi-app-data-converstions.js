import { initialConfig } from "./constants";

export function RGBAToHexA(r, g, b, a) {
  if (
    !r ||
    !g ||
    !b ||
    !a ||
    (typeof r !== "string" && typeof r !== "number") ||
    (typeof g !== "string" && typeof g !== "number") ||
    (typeof b !== "string" && typeof b !== "number") ||
    (typeof a !== "string" && typeof a !== "number")
  )
    return "#ffffff";
  r = (+r).toString(16);
  g = (+g).toString(16);
  b = (+b).toString(16);
  a = Math.round(+a * 255).toString(16);
  if (r.length === 1) r = "0" + r;
  if (g.length === 1) g = "0" + g;
  if (b.length === 1) b = "0" + b;
  if (a.length === 1) a = "0" + a;
  return "#" + r + g + b + a;
}

export const convertOldConfigToNewConfig = (data) => {
  if (data === undefined) {
    return initialConfig();
  }
  let headerValues = data?.header
    ? {
        enable: data.header.enable,
        title: data.header.text,
        placeholder: "Edit/Add your header content here",
        link: data.header.href,
        backgroundColor: data.header.color,
      }
    : {
        enable: false,
        title: "",
        placeholder: "Edit/Add your header content here",
        link: "",
        backgroundColor: "#fff",
      };
  let shadowValues = data.shadow
    ? {
        enable: data.shadow.enable,
        xOffset: data.shadow.xoffset,
        yOffset: data.shadow.yoffset,
        blur: data.shadow.blur,
        spread: data.shadow.spread,
        color: data.shadow.color,
      }
    : {
        enable: false,
        xOffset: 0,
        yOffset: 0,
        blur: 0,
        spread: 0,
        color: "#fff",
      };
  let backgroundValues = data?.background
    ? {
        enable: data.background.enable,
        backgroundColor: data.background.color,
        image: data.background.imgUrl,
      }
    : {
        enable: false,
        backgroundColor: "#fff",
        image: "",
      };
  let borderValues = data?.border
    ? {
        enable: data.border.enable,
        borderWidth: data.border.weight,
        borderStyle: data.border.borderStyle,
        color: data.border.color,
      }
    : {
        enable: false,
        borderWidth: 1,
        borderStyle: "none",
        color: "#fff",
      };
  let htmlValues = data?.htmlCode
    ? {
        enable: data.htmlCode?.enable,
        value: data.htmlCode?.content,
      }
    : {
        enable: false,
        value: "",
      };
  let cssValues = data?.cssCode
    ? {
        enable: data.cssCode?.enable,
        value: data.cssCode?.content,
      }
    : {
        enable: false,
        value: "",
      };
  let jsValues = data?.jsCode
    ? {
        enable: data.jsCode?.enable,
        value: data.jsCode?.content,
      }
    : {
        enable: false,
        value: "",
      };
  return [
    {
      key: "header",
      values: headerValues,
    },
    {
      key: "shadow",
      values: shadowValues,
    },
    {
      key: "background",
      values: backgroundValues,
    },
    {
      key: "border",
      values: borderValues,
    },
    {
      key: "html",
      values: htmlValues,
    },
    {
      key: "css",
      values: cssValues,
    },
    {
      key: "javascript",
      values: jsValues,
    },
  ];
};

export const convertChildComponents = (
  components,
  currentGridItem,
  allChilds
) => {
  let totalChildIds = [];
  currentGridItem.childComponents.forEach((id) => {
    totalChildIds.push(id.slice(5));
  });
  currentGridItem.layout = totalChildIds.map((childId) => {
    const currentGridItem = components[childId];
    return {
      i: childId,
      x: currentGridItem.gs_attr.x,
      y: currentGridItem.gs_attr.y,
      h: currentGridItem.gs_attr.height,
      w: currentGridItem.gs_attr.width,
    };
  });
  currentGridItem.children = totalChildIds.map((childId) => {
    const currentGridItem = {
      ...components[childId],
      id: childId,
      // initialPosition: {
      //   x: components[childId].gs_attr.x,
      //   y: components[childId].gs_attr.y,
      //   h: components[childId].gs_attr.height,
      //   w: components[childId].gs_attr.width,
      // },
      isGrouped: true,
      name: components[childId]?.label
        ? components[childId]?.label
        : components[childId]?.name,
      reportInfo: components[childId].options
        ? {
            file: {
              path: `${components[childId].options.dir}/${components[childId].options.file}`,
              name: components[childId].options.file,
            },
            mode: "dashboard",
            filters: [],
            extension: components[childId].options.ext,
          }
        : undefined,
      gridItemConfig: convertOldConfigToNewConfig(components[childId]),
    };

    if (currentGridItem?.childComponents?.length > 0) {
      convertChildComponents(components, currentGridItem, allChilds);
      allChilds.push(childId);
      return currentGridItem;
    }
    allChilds.push(childId);
    return currentGridItem;
  });
};

export const convertGridItemsData = (components) => {
  // let totalChildIds = [];
  let result = [],
    layout = [],
    allChilds = [];
  Object.keys(components).forEach((id) => {
    const currentGridItem = {
      ...components[id],
      id,
      // initialPosition: {
      //   x: components[id].gs_attr.x,
      //   y: components[id].gs_attr.y,
      //   h: components[id].gs_attr.height,
      //   w: components[id].gs_attr.width,
      // },
      isGrouped: false,
      reportInfo: components[id].options
        ? {
            file: {
              path: `${components[id].options.dir}/${components[id].options.file}`,
              name: components[id].options.file,
            },
            mode: "dashboard",
            filters: [],
            extension: components[id].options.ext,
          }
        : undefined,
      name: components[id]?.label
        ? components[id]?.label
        : components[id]?.name,
      gridItemConfig: convertOldConfigToNewConfig(components[id]),
    };
    if (currentGridItem?.childComponents?.length > 0) {
      convertChildComponents(components, currentGridItem, allChilds);
    }

    layout.push({
      i: id,
      x: currentGridItem.gs_attr.x,
      y: currentGridItem.gs_attr.y,
      h: currentGridItem.gs_attr.height,
      w: currentGridItem.gs_attr.width,
    });
    result.push(currentGridItem);
  });
  result = result.filter((item) => !allChilds.includes(item.uid));
  layout = layout.filter((item) => !allChilds.includes(item.i));
  return {
    gridItemsData: result,
    layout,
  };
};

export const convertOldVariablesConfigToNewVariablesConfig = (
  oldVariablesConfig
) => {
  const newVariablesConfig = {};
  oldVariablesConfig.forEach((item) => {
    newVariablesConfig[item[0]] = item[1];
  });
  return newVariablesConfig;
};
