import React from "react";
import { Skeleton } from "antd";
import "./CustomSkeleton.css";

const MetadataSidebarSkeleton = () => {
  return (
    <div className="skeleton-container">
      <div className="skeleton-bin-header">
        <Skeleton.Button active size="small" className="skeleton-bin-child"/>
       
      </div>
      <div className="skeleton-bin-rows">
      {Array.from({ length: 6 }, (_, index) => (
            <div className="skeleton-bin-row">
              <Skeleton.Button active size="small" className="skeleton-bin-child"/>
   
            </div>
          ))}
      </div>
    </div>
  )
}

export default MetadataSidebarSkeleton;