import React, { useState } from "react";
import { Button, Col, Row, Skeleton, Table, Input, Tooltip, Typography } from "antd";
import { VList } from "virtuallist-antd";
import hotkeys from 'hotkeys-js';
import {
  TableOutlined,
  CaretRightOutlined,
  CaretDownOutlined,
} from "@ant-design/icons";
import { useDispatch, useSelector } from "react-redux";
import {
  searchInMetadata,
  onExpandTable,
  setHReportEditLoading,
  setHReportLoading,
} from "../../../redux/actions/hreport.actions";
import "./metadata.scss";
import { useEffect } from "react";
import MetadataField from "./metadata-field";
import { getTableTree } from "../../hi-reports/utils/utilities";
import MetadataTable from "./table";
import { SidebarFooter } from "../../hi-sidebar-footer";
import TutorialInfo from "../../common/hi-tutorial";
import ErrorFallback from "../../../errorBoundry/ErrorFallback";
import HrCubeTable from "./hr-cube-table";
import LoadingBar from "../../common/components/hi-loading-bar";
import { onExpandCubeTable } from "../../../redux/actions/cube.actions";
import { onExpandAgentTable } from "../../../redux/actions/agent.actions";
import ShortCutText from "../../common/hi-shortcuts/hi-shortcuts";
import { ObjectGroupIcon } from "../../common/icons/hi-icons-svg";
import MetadataSidebarSkeleton from "../../common/custom-icons/CustomSkeletons/MetadataSidebarSkeleton";
const { Column } = Table;

const { Text } = Typography

let env = process.env.NODE_ENV;

const MetadataArea = (props) => {
  const [, setParentHeight] = useState(450);
  const handleResize = () => {
    const parent = document.querySelector(".hr-sidebar");
    if (parent) {
      setParentHeight(parent.offsetHeight);
    }
  };
  useEffect(() => {
    window.addEventListener("resize", handleResize, true);
    return () => {
      window.removeEventListener("resize", handleResize);
    };
  }, []);
  const dispatch = useDispatch();
  const hrSidebar = useSelector((state) => state.hreport.present.hrSidebar);
  const hreportEditLoading = useSelector((state) => state.hreport.present.hreportEditLoading)
  const [expandedRowKeys, setExpandedRowKeys] = useState([]);
  const [searchText, setSearchText] = useState("");
  const { metadata, isCube, metadataLoading, isInstantBI = false } = props;
  const parent = document.querySelector(".hr-sidebar");
  let height = 450;
  if (parent) {
    height = parent.offsetHeight - 150;
  }
  const { defaultMetadataSearchText, defaultexpandedRowKeys, uid } =
    metadata || {};
  useEffect(() => {
    matadataTablesList.forEach((table) => {
      if (table.expand && !expandedRowKeys.includes(table.key)) {
        let icon = document.querySelector(`#expand-${table.key}`);
        if (icon) {
          icon.click();
        }
      }
    });
  }, [searchText]);
  useEffect(() => {
    // const parent = docum
  }, []);
  useEffect(() => {
    setSearchText(defaultMetadataSearchText);
    if (Array.isArray(defaultexpandedRowKeys)) {
      setExpandedRowKeys([...defaultexpandedRowKeys]);
    }
  }, [uid]);
  const isCubeLikeModule = props.module === 'cube' || props.module === 'agent';
  const open = () => {
    if (isCubeLikeModule) {
      props.onConnectToMetadata();
    } else {
      props.openFileBrowser("metadata");
    }
  };

  const handleAbort = () => {
    props.abortFetchData()
    if (hreportEditLoading) {
      dispatch(setHReportEditLoading(false))
      dispatch(setHReportLoading(false))
    }
  }


  hotkeys('ctrl+m', () => open()); //key board short cut to connect to metadata

  const onSearch = (e) => {
    setSearchText(e.target.value);
    if (hrSidebar === 'metadata') {
      dispatch(searchInMetadata({ searchText: e.target.value }));
    } else {

    }
  };
  const expandRow = (key) => {
    if (props.module === 'cube') {
      dispatch(onExpandCubeTable({ key }));
    } else if (props.module === 'agent') {
      dispatch(onExpandAgentTable({ key }));
    } else {
      !isInstantBI && dispatch(onExpandTable({ key }));
    }
    setExpandedRowKeys((prevState) => {
      if (prevState.includes(key)) {
        prevState = prevState.filter((item) => item !== key);
      } else {
        prevState.push(key);
      }
      return prevState;
    });
  };
  let matadataTablesList = [];
  if (metadata && metadata.uid && !isCube && metadata.tables) {
    const tableTreeResult = getTableTree(metadata, searchText);
    matadataTablesList = tableTreeResult?.tables || [];
  }
  let vc = React.useMemo(() => {
    return VList({
      height,
      vid: "hreport-table",
      resetTopWhenDataChange: false,
    });
  }, [height, props.parentContainerId]);
  if (metadataLoading || (metadata && metadata.loading) || hreportEditLoading) {
    return <>
      {/* <LoadingBar handleClick={props.abortFetchData} /> */}
      <LoadingBar handleClick={handleAbort} />
      <MetadataSidebarSkeleton />
    </>;
  }
  vc = env === "test" ? {} : vc;
  // console.log(matadataTablesList);
  return (
    <ErrorFallback {...props} errTemplate="There is some error in Metadata" >
      <div className="hr-metadata-container">
        {!isInstantBI && <Input.Group>
          <Input
            disabled={isCubeLikeModule ? !Object.keys(metadata).length : !metadata}
            className="hr-md-search"
            value={searchText}
            placeholder="search tables or columns"
            onChange={onSearch}
          />
          <TutorialInfo elementKey="hr-metadata">
            <ShortCutText text="C" scLocation="HR" isButton={true}>
              <Tooltip title="Connect to Metadata">
                <Button
                  data-testid="connect-to-metadata"
                  icon={<TableOutlined />}
                  onClick={open}
                />
              </Tooltip>
            </ShortCutText>
          </TutorialInfo>
        </Input.Group>}
        {hrSidebar === 'metadata' ? <div className="hr-metadata-table">
          <Table
            size="small"
            rowClassName="hr-metadata-table-row"
            dataSource={matadataTablesList}
            scroll={{ y: height }}
            components={vc}
            pagination={false}
            rowKey={(record) => record.key}
            showHeader={false}
            expandable={{
              indentSize: 8,
              expandRowByClick: true,
              expandedRowKeys: expandedRowKeys || [],
              onExpand: (expanded, record) => expandRow(record.key),
              expandIcon: (props) => {
                let { record, expanded } = props;
                if (record.columnName) return null;
                return (
                  <div
                    className="hr-md-expand-icons"
                    data-testid={`expand-table`}
                    id={`expand-${record.key}`}
                  >
                    <span>
                      {record.type === 'view' ? <ObjectGroupIcon /> : <TableOutlined />}
                    </span>
                    <span>
                      {!expanded && <CaretRightOutlined />}
                      {expanded && <CaretDownOutlined />}
                    </span>
                  </div>
                );
              },
            }}
          >
            <Column
              ellipsis={{
                showTitle: true,
              }}
              key="alias"
              onCell={(item) => {
                if (!item.columnName) return {};
                return { className: "hr-metadata-field" };
              }}
              render={(text, record) => {
                return (
                  <Row>
                    <Col span={24}>
                      {!record.columnName && (
                        <MetadataTable item={record} metadata={metadata} />
                      )}
                      {record.columnName ?
                        isInstantBI ? <Text className="hr-metadata-ellipsis" ellipsis={true}>{record.alias}</Text> :
                          <MetadataField module={props.module} item={record} metadata={metadata} />
                        : null
                      }
                      {/* {record.columnName && (
                        <MetadataField module={props.module} item={record} metadata={metadata} />
                      )} */}
                    </Col>
                  </Row>
                );
              }}
            />
          </Table>
        </div> : <div className="hr-cube-table"><HrCubeTable cube={metadata} searchText={searchText} /></div>}
        {!isInstantBI && env !== "test" && (
          <SidebarFooter
            color="white"
            hideSideBar={props?.hideSideBar ?? true}
            onGlobalSearch={props.onGlobalSearch}
            fileBrowserOnClick={() => props.openFileBrowser("hr")}
            module="HR"
          />
        )}
      </div>
    </ErrorFallback>
  );
};

// export default MetadataArea

const areEqual = (prevProps, nextProps) => {
  if (
    ((prevProps.metadata !== nextProps.metadata) &&
      (!prevProps.metadata ||
        !nextProps.metadata ||
        prevProps.metadata.uid !== nextProps.metadata.uid)) ||
    prevProps.size.height !== nextProps.size.height ||
    prevProps.metadataLoading !== nextProps.metadataLoading
  ) {
    return false;
  } else {
    return true;
  }
};

export default React.memo(MetadataArea, areEqual);
