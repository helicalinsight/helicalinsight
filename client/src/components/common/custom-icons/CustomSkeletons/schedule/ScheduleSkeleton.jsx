import React from "react";
import { Skeleton } from "antd";
import "../CustomSkeleton.css";


const ScheduleSkeleton = () => {
  return (
    <div className="skeleton-container">
      <div className="skeleton-bin-header">
        <Skeleton.Button active size="small" className="skeleton-bin-child small-width"/>
        <Skeleton.Button active size="small" className="skeleton-bin-child large-width"/>
        <Skeleton.Button active size="small" className="skeleton-bin-child medium-width"/>
        <Skeleton.Button active size="small" className="skeleton-bin-child large-width"/>
        <Skeleton.Button active size="small" className="skeleton-bin-child"/>
        <Skeleton.Button active size="small" className="skeleton-bin-child"/>
        <Skeleton.Button active size="small" className="skeleton-bin-child"/>
      </div>
      <div className="skeleton-bin-rows">
      {Array.from({ length: 11 }, (_, index) => (
        <div className="skeleton-bin-row">
        <Skeleton.Button active size="small" className="skeleton-bin-child small-width"/>
        <Skeleton.Button active size="small" className="skeleton-bin-child large-width"/>
        <Skeleton.Button active size="small" className="skeleton-bin-child medium-width"/>
        <Skeleton.Button active size="small" className="skeleton-bin-child large-width"/>
        <Skeleton.Button active size="small" className="skeleton-bin-child"/>
        <Skeleton.Button active size="small" className="skeleton-bin-child"/>
        <Skeleton.Button active size="small" className="skeleton-bin-child"/>
          </div>
          ))}
      </div>
    </div>
  )
}

export default ScheduleSkeleton;
