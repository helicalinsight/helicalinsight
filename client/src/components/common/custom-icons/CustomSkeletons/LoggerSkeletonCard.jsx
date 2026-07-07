import React from 'react';
import { Divider, Skeleton } from 'antd';

const LoggerSkeletonCard = () => {
  
  return (
    <div>

        <Skeleton.Input active style={{ minWidth: '60%', height: '10px' ,marginTop:"10px"}} />
      
      <Divider style={{ margin: 10 }}/>
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
        <Skeleton.Input active style={{ width: '10%', height: '15px',justifyItems:"center" }} />
      </div>
    
    </div>
  );
};

export default LoggerSkeletonCard;
