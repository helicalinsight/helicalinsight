import React from "react";
import { Skeleton } from "antd";
import "../CustomSkeleton.css";

const ShareModalSkeleton = () => {
  return (
    <div className="skeleton-container">
      <div className="skeleton-bin-header left-padding">
        <Skeleton.Button active size="small" className="skeleton-bin-child "/>
        <Skeleton.Button active size="small" className="skeleton-bin-child"/>
        <Skeleton.Button active size="small" className="skeleton-bin-child"/>
          </div>
      <div className="skeleton-bin-rows">
      {Array.from({ length: 5 }, (_, index) => (
            <div className="skeleton-bin-row left-padding">
              <Skeleton.Button active size="small" className="skeleton-bin-child"/>
              <Skeleton.Button active size="small" className="skeleton-bin-child"/>
              <Skeleton.Button active size="small" className="skeleton-bin-child"/>
               </div>
          ))}
      </div>
    </div>
  )
}

export default ShareModalSkeleton;
