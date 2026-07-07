import React, { useState } from "react";
import { Button } from "antd";
import { useDispatch } from "react-redux";
import { LoadData } from "../../lib/pagenation";

const HIUsers = (props) => {
  const [count, setCount] = useState(1);
  const { data, loaded, onNext } = LoadData({ key: "users" });
  if (!loaded) {
    return <div>Loading....aaa..</div>;
  }
  return (
    // <div>
    //   <div>it is2 {count} </div>
    //   <div onClick={(e) => setCount(count + 1)}> change </div>
    //   <h2>Users2</h2>
    //   {data.map((item, i) => {
    //     return (
    //       <div key={i}>
    //         {" "}
    //         {item.slno} . {item.name}{" "}
    //       </div>
    //     );
    //   })}
    //   <div>
    //     <Button>Prev</Button>
    //     <Button type="primary" onClick={onNext}>
    //       Next
    //     </Button>
    //   </div>
    // </div>
    <h1>users</h1>
  );
};

export { HIUsers };
