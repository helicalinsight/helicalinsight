import { getGridItem } from "./recursive-functions";
export const getDashboardConfig = (array, id, type, gridWidthOption) => {
  let data = array;
  // for grid Item
  if (id) {
    data = getGridItem(data, id)?.gridItemConfig;
  }
  // getting the values

  const colsValues = data?.find((item) => item.key === "columns")?.values || [];
  const breakpointsValues = data?.find(
    (item) => item.key === "breakpoints"
  )?.values || [];
  const convertedBreakpoints = {};
  breakpointsValues?.forEach((item) => {
    const key = item.key;
    if (item.value) {
      convertedBreakpoints[key] = item.value;
    } else {
      convertedBreakpoints[key] = 2;
    }
  });
  let convertedCols = {};
  if (gridWidthOption) {
    convertedCols = {
      lg: gridWidthOption,
      md: gridWidthOption,
      sm: gridWidthOption,
      xs: gridWidthOption,
      xxs: gridWidthOption,
    };
  }
  !gridWidthOption &&
    colsValues?.forEach((item) => {
      const key = item.key;
      if (item.value) {
        convertedCols[key] = item.value;
      } else {
        convertedCols[key] = 2;
      }
    });

  const headerValues = data?.find((item) => item.key === "header")?.values || {};
  const shadowValues = data?.find((item) => item.key === "shadow")?.values || {};
  const backgroundValues = data?.find(
    (item) => item.key === "background"
  )?.values || {};

  const borderValues = data?.find((item) => item.key === "border")?.values || {};
  const editValues = data?.find((item) => item.key === "edit")?.values || {};

  //setting the values
  const borderValue = `${borderValues?.borderStyle} ${
    borderValues?.borderWidth
  }px ${`rgba(${borderValues?.color?.r},${borderValues?.color?.g},${borderValues?.color?.b},${borderValues?.color?.a})`}`;

  let borderStyles = {
    borderRadius: `${borderValues?.borderRadius}px`,
  };


  // Setting 'border' property and shorthand property like 'borderTop' together can cause issues
  // If 'border' is included in the positions, only set that. Else set the shorthand border properties
  if(borderValues?.borderPosition?.includes("border")){
    borderStyles["border"] = borderValue;
  }else{
    for(let borderPos of borderValues?.borderPosition || []){
    borderStyles[borderPos] = borderValue;
      if(borderPos === "border") break;
    }
  }


  borderStyles = borderValues?.enable ? borderStyles : null;
  const encodingURLString = `url(${
    backgroundValues?.image
      ? backgroundValues?.image.toString().replaceAll(/ /g, "%20")
      : ""
  })`;
  const backgroundStyles = backgroundValues?.enable
    ? {
        backgroundColor: `rgba(${backgroundValues?.backgroundColor.r},${backgroundValues?.backgroundColor?.g},${backgroundValues?.backgroundColor?.b},${backgroundValues?.backgroundColor?.a})`,
        backgroundImage: encodingURLString,
        backgroundSize: backgroundValues?.backgroundSize,
        backgroundRepeat: backgroundValues?.backgroundRepeat,
        opacity: parseFloat(backgroundValues?.opacity / 100),
      }
    : null;

  const shadowStyles = shadowValues?.enable
    ? {
        boxShadow: `${shadowValues?.xOffset}px ${shadowValues?.yOffset}px ${
          shadowValues.blur ? `${shadowValues?.blur}px` : null
        } ${
          shadowValues.spread ? `${shadowValues?.spread}px` : null
        } ${`rgba(${shadowValues?.color?.r},${shadowValues?.color?.g},${shadowValues?.color?.b},${shadowValues?.color?.a})`}`,
      }
    : null;
  const headerStyles = headerValues?.enable
    ? {
        backgroundColor: `rgba(${headerValues?.backgroundColor?.r},${headerValues?.backgroundColor?.g},${headerValues?.backgroundColor?.b},${headerValues?.backgroundColor?.a})`,
      }
    : null;
  const imageStyles = editValues?.enable
    ? {
        opacity: parseFloat(editValues?.opacity / 100),
        backgroundImage: `url(${
          editValues?.url
          ? editValues?.url.toString().replaceAll(/ /g, "%20")
          : ""
        })`,
        backgroundSize: editValues?.imageSize,
        backgroundRepeat: editValues?.imageRepeat,
      }
    : null;

  let gridItemStyles = {
    ...borderStyles,
    ...backgroundStyles,
    ...shadowStyles,
  };
  return type.map((item) => {
    switch (item) {
      case "breakpoints":
        return convertedBreakpoints;
      case "columns":
        return convertedCols;
      case "header":
        return headerStyles;
      case "background":
        return backgroundStyles;
      case "border":
        return borderStyles;
      case "shadow":
        return shadowStyles;
      case "image":
        return imageStyles;
      default:
        return gridItemStyles;
    }
  });
};
