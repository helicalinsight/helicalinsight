import React from "react";
import "./customCannedSkeleton.scss";

const pageDimensions = {
  A4: {
    portrait: { width: 595, height: 842 },
    landscape: { width: 842, height: 595 },
  },
  A3: {
    portrait: { width: 842, height: 1191 },
    landscape: { width: 1191, height: 842 },
  },
  LETTER: {
    portrait: { width: 612, height: 792 },
    landscape: { width: 792, height: 612 },
  },
};

export const getCanvasDimensions = (canvasProperties = {}) => {
  const layout = canvasProperties.layout || {};
  const pageName = layout.name || "A4";
  const orientation = layout.orientation?.toLowerCase() || "portrait";

  return (
    pageDimensions?.[pageName]?.[orientation] || {
      width: 595,
      height: 842,
    }
  );
};

export const CustomCannedSkeleton = ({ canvasHeight }) => {
  const rowHeight = 32;
  const rowCount = Math.ceil(canvasHeight / rowHeight);

  return (
    <div className="canned-skeleton-wrapper">
      {Array.from({ length: rowCount }).map((_, i) => (
        <div className="skeleton-row" key={i}>
          {Array.from({ length: 5 }).map((_, j) => (
            <div className="skeleton-block" key={j} />
          ))}
        </div>
      ))}
    </div>
  );
};
