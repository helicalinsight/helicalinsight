import React from "react";
import { RingProgress, Pie, Column } from "@ant-design/plots";

const CHART_COMPONENTS = {
  ring: RingProgress,
  pie: Pie,
  column: Column,
};

const HIOverviewChart = ({ type = "pie", config, className = "" }) => {
  const ChartComponent = CHART_COMPONENTS[type];
  if (!ChartComponent || !config) return null;

  return (
    <div className={`hi-overview-chart ${className}`} data-testid={`hi-overview-chart-${type}`}>
      <ChartComponent {...config} />
    </div>
  );
};

export { HIOverviewChart };
