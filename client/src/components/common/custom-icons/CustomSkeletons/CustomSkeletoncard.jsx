import React from 'react';
import { Divider, Skeleton } from 'antd';

const CustomSkeletonCard = () => {
  
  return (
    <div>

        <Skeleton.Input active style={{ minWidth: '60%', height: '10px' ,marginTop:"10px"}} />
      
      <Divider />
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Skeleton.Input active style={{ width: '10%', height: '15px' }} />
      </div>
      <br />
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Skeleton.Input active style={{ width: '60%', height: '10px' }} />
        <Skeleton.Avatar 
          shape="square"
          style={{ minWidth: '25px', height: '10px', marginLeft: '10px' , marginRight:"4px" }}
          active
        />
      </div>
    
    </div>
  );
};

export default CustomSkeletonCard;
