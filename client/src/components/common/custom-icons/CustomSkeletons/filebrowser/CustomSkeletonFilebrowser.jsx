import React from "react";
import { Skeleton } from "antd";
import "../CustomSkeleton.css";

const skeletons = {

  // Mini Skeleton
  mini: <div className="skeleton-container">
  <div className="skeleton-bin-header">
    <Skeleton.Button active size="small" className="skeleton-bin-child"/>
  </div>
  <div className="skeleton-bin-rows">
  {Array.from({ length: 10 }, (_, index) => (
        <div className="skeleton-bin-row">
          <Skeleton.Button active size="small" className="skeleton-bin-child"/>
        </div>
      ))}
  </div>
</div>,

  // Full Skeleton
  full: <div className="skeleton-container">
  <div className="skeleton-bin-header">
    <Skeleton.Button active size="small" className="skeleton-bin-child thicker-height"/>
    <Skeleton.Button active size="small" className="skeleton-bin-child thicker-height"/>
    <Skeleton.Button active size="small" className="skeleton-bin-child thicker-height"/>
    <Skeleton.Button active size="small" className="skeleton-bin-child thicker-height"/>
    <Skeleton.Button active size="small" className="skeleton-bin-child thicker-height"/>
  </div>
  <div className="skeleton-bin-rows">
  {Array.from({ length: 13 }, (_, index) => (
        <div className="skeleton-bin-row">
          <Skeleton.Button active size="small" className="skeleton-bin-child thick-height"/>
          <Skeleton.Button active size="small" className="skeleton-bin-child thick-height"/>
          <Skeleton.Button active size="small" className="skeleton-bin-child thick-height"/>
          <Skeleton.Button active size="small" className="skeleton-bin-child thick-height"/>
          <Skeleton.Button active size="small" className="skeleton-bin-child thick-height"/>
        </div>
      ))}
  </div>
</div>
}


const CustomSkeletonFilebrowser = ({ size="full" }) => {
  return (
    skeletons[size]
  )
}

export default CustomSkeletonFilebrowser;
