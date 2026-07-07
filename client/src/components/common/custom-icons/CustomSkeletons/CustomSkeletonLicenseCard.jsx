import React from "react";
import { Divider, Skeleton } from "antd";

const CustomSkeletonLicenseCard = () => {
  return (
    <div>
      <Divider style={{ margin: 10 }} />
      <div>
        <Skeleton.Input
          active
          style={{ minWidth: "50%", height: "10px", marginLeft: "10px" }}
        />
      </div>
      <Divider style={{ margin: 6 }} />

      <div style={{ display: "flex", alignItems: "center" }}>
        <div style={{ flex: 1, marginRight: "20px" }}>
          <Skeleton.Image style={{ marginLeft: "25px" }} active />
        </div>
        <div style={{ flex: 3 }}>
          <div>
            <Skeleton.Input
              active
              style={{ width: "80%", height: "8px", marginBottom: "10px" }}
            />
          </div>
          <div>
            <Skeleton.Input
              active
              style={{ width: "80%", height: "8px", marginBottom: "10px" }}
            />
          </div>
          <div>
            <Skeleton.Input
              active
              style={{ width: "80%", height: "8px", marginBottom: "10px" }}
            />
          </div>
          <div>
            <Skeleton.Input
              active
              style={{ width: "80%", height: "8px", marginBottom: "10px" }}
            />
          </div>
          <div>
            <Skeleton.Avatar
              shape="square"
              active
              style={{ width: "60px", height: "20px", marginLeft: "40px" }}
            />
          </div>
        </div>
      </div>
    </div>
  );
};

export default CustomSkeletonLicenseCard;
