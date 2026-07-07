import React, { useMemo, useState } from "react";
import { Button, Col, Row, Skeleton, Table, Input, Tooltip } from "antd";
import { VList } from "virtuallist-antd";
import {
    TableOutlined,
    CaretRightOutlined,
    CaretDownOutlined,
} from "@ant-design/icons";
import { useDispatch, useSelector } from "react-redux";
import { useEffect } from "react";
import MetadataField from "./metadata-field";
import MetadataTable from "./table";
import { handleCubeToTableDataSource, handleHrCubeDataSource, hrCubeColumns } from "./hr-sidebar-helperMethods";
import { v4 as uuidv4 } from 'uuid';
import { CustomIcon } from "../../common/custom-icons/CustomIcon";
import { DataBricks } from "../../common/custom-icons/CustomSvg";

export default function HrCubeTable({ cube, searchText }) {
    const dispatch = useDispatch();
    const parent = document.querySelector(".hr-sidebar");
    let height = 450;
    if (parent) {
        height = parent.offsetHeight - 150;
    }
    const [expandedKeys, setExpandedKeys] = useState([]);
    const [datasourceData, setDatasourceData] = useState([]);

    let vc = useMemo(() => {
        return VList({
            height,
            vid: "hreport-cube-table",
            resetTopWhenDataChange: false,
        });
    }, [height]);

    useEffect(() => {
        let data = [];
        let cubes = cube?.cubes || null;
        if (cubes) {
            data = handleCubeToTableDataSource(cubes)
        }
        setDatasourceData(data);

    }, [cube])

    // useEffect(() => {
    //     if (searchText && datasourceData.length) {
    //         let data = datasourceData.filter(rec => {
    //             return rec.row.toLowerCase().includes(searchText.toLowerCase());
    //         })
    //         setDatasourceData(data);
    //     }
    // }, [searchText])

    // console.log(datasourceData, 'datasourceData')

    const expandRow = (key) => {
        // dispatch(onExpandTable({ key }));
        setExpandedKeys((prevState) => {
            if (prevState.includes(key)) {
                prevState = prevState.filter((item) => item !== key);
            } else {
                prevState.push(key);
            }
            return prevState;
        });
    };

    return (
        <Table
            columns={
                hrCubeColumns({})
            }
            className="hr-cube-sidebar"
            size="small"
            rowClassName="hr-cube-table-row"
            dataSource={handleHrCubeDataSource({ datasourceData, searchText })}
            scroll={{ y: height }}
            components={vc}
            pagination={false}
            rowKey={(record) => record.key}
            showHeader={false}
            expandable={{
                indentSize: 8,
                expandRowByClick: true,
                onExpand: (expanded, record) => expandRow(record.key),
                expandedRowKeys: expandedKeys || [],
                expandIcon: (props) => {
                    let { record, expanded } = props;
                    // console.log({ ...props });
                    // if (!record.children) return null;
                    return (
                        <div className="hr-sidebar-cube-expand-grp">
                            {record.dimensionId ? <CustomIcon name='Dimension' /> : (record.hierarchyId ? <CustomIcon name='Hierarchy' /> : (record.measureId ? <CustomIcon name='Measure' /> : <CustomIcon name='Level' />))}
                            {record.children && (expanded ? (
                                <CaretDownOutlined />
                            ) : (
                                <CaretRightOutlined />
                            ))}
                        </div>
                    );
                },
            }}
        />
    )
}