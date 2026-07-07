import L from "leaflet";
import { divStyles, setStyles } from "../../ant-charts/ant-utils";
import { getPropertyFieldInfo, getPropertyText } from "../../utils/utillities";

const getOSMChartTooltip = (fields = [], data = {}, report) => {
  const container = document.createElement("div");
  if (fields?.length > 0) {
    fields.forEach((name) => {
      const tooltipValue = data[name];
      const { isApplyClicked, fieldType, formatField } = getPropertyFieldInfo(report, name);
      const value = getPropertyText({
        text: tooltipValue,
        applyOn: "tooltip",
        isApplyClicked,
        fieldType,
        formatField,
      });
      const child = document.createElement("div");
      child.innerHTML = `${name} : ${value}`;
      container.appendChild(child);
    });
    setStyles(container, divStyles);
    return container;
  }
  container.innerHTML = ''
  return container;
}

const calculateScaleValues = (data, sizeField) => {
  if (!data.length) return {};
  const values = data.map(d => d[sizeField]);
  const minValue = Math.min(...values);
  const maxValue = Math.max(...values);


  const result = {};

  data.forEach(d => {
    const scaledValue = 4 + ((d[sizeField] - minValue) / (maxValue - minValue)) * 8;
    result[d[sizeField]] = Math.round(scaledValue * 100) / 100;
  });

  return result;
}

const svgIcon = (svg, size) =>
  L.divIcon({
    className: "leaflet-shape-icon",
    html: svg,
    iconSize: [size, size],
    iconAnchor: [size / 2, size / 2],
  });

const svgIconFromUrl = (url, size) =>
  L.divIcon({
    className: "leaflet-shape-icon",
    html: `
    <svg width="${size}" height="${size}" xmlns="http://www.w3.org/2000/svg">
        <image href="${url}" x="0" y="0" width="${size}" height="${size}" />
    </svg>
    `,
    iconSize: [size, size],
    iconAnchor: [size / 2, size / 2],
  });

const circleIcon = (color, size) =>
  svgIcon(`
    <svg width="${size}" height="${size}" viewBox="0 0 100 100">
      <circle cx="50" cy="50" r="50" fill="${color}" />
    </svg>
  `, size);

const triangleIcon = (color, size) =>
  svgIcon(`
    <svg width="${size}" height="${size}" viewBox="0 0 100 100">
      <polygon points="50,0 100,100 0,100" fill="${color}" />
    </svg>
  `, size);

const squareIcon = (color, size) =>
  svgIcon(`
    <svg width="${size}" height="${size}" viewBox="0 0 100 100">
      <rect x="0" y="0" width="100" height="100" fill="${color}" />
    </svg>
  `, size);

const pentagonIcon = (color, size) =>
  svgIcon(`
    <svg width="${size}" height="${size}" viewBox="0 0 100 100">
      <polygon
        points="50,0 100,38 82,100 18,100 0,38"
        fill="${color}"
      />
    </svg>
  `, size);

const hexagonIcon = (color, size) =>
  svgIcon(`
    <svg width="${size}" height="${size}" viewBox="0 0 100 100">
      <polygon
        points="25,0 75,0 100,50 75,100 25,100 0,50"
        fill="${color}"
      />
    </svg>
  `, size);

const octagonIcon = (color, size) =>
  svgIcon(`
    <svg width="${size}" height="${size}" viewBox="0 0 100 100">
      <polygon
        points="30,0 70,0 100,30 100,70 70,100 30,100 0,70 0,30"
        fill="${color}"
      />
    </svg>
  `, size);

const rhombusIcon = (color, size) =>
  svgIcon(`
    <svg width="${size}" height="${size}" viewBox="0 0 100 100">
      <polygon
        points="50,0 100,50 50,100 0,50"
        fill="${color}"
      />
    </svg>
  `, size);

const vesicaIcon = (color, size) =>
  svgIcon(`
    <svg width="${size * 2}" height="${size * 2}" viewBox="0 0 100 100">
      <defs>
          <clipPath id="clip1">
            <circle cx="35" cy="50" r="30"/>
          </clipPath>
      </defs>
      <circle cx="65" cy="50" r="30" fill="${color}" clip-path="url(#clip1)"/>
    </svg>
  `, size);

const dotIcon = (color, size) =>
  svgIcon(`
    <svg width="${size}" height="${size}" viewBox="0 0 100 100">
      <circle cx="50" cy="50" r="50" fill="${color}" />
    </svg>
  `, size);

const hexagramIcon = (color, size) =>
  svgIcon(`
    <svg width="${size}" height="${size}" viewBox="0 0 100 100">
      <polygon
        points="50,5 95,80 5,80"
        fill="${color}"
        stroke={"none"}
        strokeWidth={1}
      />
      <polygon
        points="50,95 95,20 5,20"
        fill="${color}"
        stroke={"none"}
        strokeWidth={1}
      />
    </svg>
  `, size);

const SHAPES = ["triangle", "square", "pentagon", "hexagon", "octogon"];
const CONTINUOUS_COLORS = ["#faa307", "#e85d04", "#d00000"]

const shapesMapOSM = {
  triangle: triangleIcon,
  square: squareIcon,
  pentagon: pentagonIcon,
  hexagon: hexagonIcon,
  octogon: octagonIcon,
  circle: circleIcon,
  rhombus: rhombusIcon,
  vesica: vesicaIcon,
  dot: dotIcon,
  hexagram: hexagramIcon
};

const generateShapeRanges = (data, field, buckets = 5) => {

  const values = data
    .map(d => d[field])
    .filter(v => typeof v === "number");

  const min = Math.min(...values);
  const max = Math.max(...values);
  const step = (max - min) / buckets;

  const ranges = [];

  for (let i = 0; i < buckets; i++) {
    const from = i === 0 ? min : Math.ceil(min + step * i);
    const to =
      i === buckets - 1
        ? max
        : Math.floor(min + step * (i + 1));

    ranges.push({
      min: from,
      max: to,
      shape: SHAPES[i],
    });
  }

  return ranges;
}

const generateDiscreteShapeRanges = (data, shapeField) => {
  const fieldValues = [...new Set(data.map(d => d[shapeField]))];
  let returnObj = {}, shapeIndex = 0;

  for (let shape of fieldValues) {
    let shapeVal = SHAPES[shapeIndex];
    shapeIndex += 1;
    if (shapeIndex > SHAPES.length - 1) {
      shapeIndex = 0
    }
    returnObj[shape] = shapeVal
  }
  return returnObj
}

const getShapeByValue = (value, ranges) => {
  return (
    ranges.find(r => value >= r.min && value <= r.max)?.shape || SHAPES[0]
  );
}


const generateColorRanges = (data, field, buckets = 3) => {
  const values = data
    .map(d => d[field])
    .filter(v => typeof v === "number");

  const min = Math.min(...values);
  const max = Math.max(...values);
  const step = (max - min) / buckets;

  const ranges = [];

  for (let i = 0; i < buckets; i++) {
    const from = i === 0 ? min : Math.ceil(min + step * i);
    const to =
      i === buckets - 1
        ? max
        : Math.floor(min + step * (i + 1));

    ranges.push({
      min: from,
      max: to,
      shape: CONTINUOUS_COLORS[i],
    });
  }

  return ranges;
}

const getColorByValue = (value, ranges) => {
  return (
    ranges.find(r => value >= r.min && value <= r.max)?.shape || CONTINUOUS_COLORS[0]
  );
}


export {
  calculateScaleValues,
  generateShapeRanges,
  getOSMChartTooltip,
  getShapeByValue,
  shapesMapOSM,
  generateDiscreteShapeRanges,
  generateColorRanges,
  getColorByValue,
  svgIconFromUrl
};
