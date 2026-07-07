import React from 'react';
import { div, Skeleton, } from 'antd';
import "./CustomSkeleton.css"

const DatasourceSkeleton = () => {
  return (
    <div className="datasource-skeleton-container">
      {new Array(30).fill(0).map((_, idx) => <Skeleton.Button key={idx} active className="datasource-skeleton-card"/>)}
    </div>
  )
}

export default DatasourceSkeleton;


