import React, { useState, useEffect } from "react";
import moment from "moment";
import {
  Drawer,
  Table
} from "antd";
import { useSelector, useDispatch } from "react-redux";
import { fileBrowserActions } from "../../../redux/actions";
import { capitalize } from "lodash";
import { getPermissionLevelsText } from "../../../utils/utilities.js";


const PropertiesDrawer = () => {
    const dispatch = useDispatch();
    const [detailRecord, setDetailRecord] = useState(null);
    const [drawerVisible, setDrawerVisible] = useState(false);
    const [data, setData] = useState([]);
    const convertRecord = (record) => {
      return Object.entries(record).filter(e => typeof e[1] !== "object").map(e => {
        // if(e[0] === "lastModified") e[1] =  new Date(JSON.parse(e[1])).toDateString()
        if(e[0] === "lastModified") e[1] =    moment(new Date(JSON.parse(e[1]))).format(
          "dddd, MMMM Do, YYYY, h:mm:ss a"
        )
        if(e[0] === "permissionLevel") e[1] = getPermissionLevelsText().find(f => String(f.level) === e[1])?.permission + (record.inherit ? "*" : "")
        e[0] = capitalize(e[0])
        return e
      })
    } 
    useEffect(() => {
      if (!drawerVisible) {
        dispatch(
          fileBrowserActions.setContextMenuItemDetails({ contextItem: null })
        );
      }
    }, [drawerVisible]);
  
    const clickedContextMenuItem = useSelector(
      (state) => state.fileBrowser.clickedContextItemDetails
    );
    useEffect(() => {
      if(clickedContextMenuItem.contextItem && clickedContextMenuItem.contextItem === "Show Properties"){
        setDrawerVisible(true)
        setDetailRecord(clickedContextMenuItem.clickedRecord)
      }
    },[clickedContextMenuItem]);
    const getProperty = (text) => {
      if (text === "Permissionlevel") {
        return "Permission Level";
      }
      if (text === "Lastmodified") {
        return "Last Modified";
      }
      return capitalize(text);
    };
    useEffect(() => {
      if(detailRecord){
        const tableData = convertRecord(detailRecord).map(
          (property, i, arr) =>({
              "key": i,
              "Property": getProperty(property[0]),
              "PropertyValue": typeof property[1] === 'boolean' ? `${property[1]}` : property[1]
          })
        )
        setData(tableData)
      }
    }, [detailRecord])
    
    const columns = [
      {
        title: "Property",
        dataIndex: 'Property',
        key: 'Property',
      },
      {
        title: "Value",
        dataIndex: 'PropertyValue',
        key: 'PropertyValue',
      },
    ];

    return (
      detailRecord && (
        <Drawer
          width={500}
          title={
            detailRecord.type === "folder"
              ? detailRecord.name
              : detailRecord.title
          }
          placement="right"
          onClose={() => setDrawerVisible(false)}
          visible={drawerVisible}
          destroyOnClose={true}
        >
          <Table columns={columns} dataSource={data} pagination={false} size={"small"} bordered={false} />
        </Drawer>
      )
    );
  };

  export { PropertiesDrawer };