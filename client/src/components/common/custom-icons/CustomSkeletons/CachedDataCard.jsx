import React from 'react';
import {  Skeleton } from 'antd';

const CacheDataCard = () => {
  
  return (
    <div>

      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Skeleton.Input active style={{ width: '10%', height: '15px',marginTop:"45px" }} />
      </div>
      <br />
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Skeleton.Input active style={{ width: '60%', height: '10px' }} />
      </div>
    
    </div>
  );
};

export default CacheDataCard;
