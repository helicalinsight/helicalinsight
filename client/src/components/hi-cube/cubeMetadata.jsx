import { useState, useRef, useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { setCubeMetadataTablesData } from '../../redux/actions/cube.actions';
// import notify from "../../../../../hi-notifications/notify";
// import { TableOutlined, CaretRightOutlined, CaretDownOutlined, NumberOutlined, CalendarOutlined, FileTextOutlined, CheckOutlined } from "@ant-design/icons";
import { Table, Typography, Tooltip, Col, Row, } from "antd";
import { cubeSecOneColumns } from "./helperMethods";
import { VList } from "virtuallist-antd";
import notify from "../hi-notifications/notify";
import cubeRequests from "../../base/requests/cube.requests";
import {
    TableOutlined,
    CaretRightOutlined,
    CaretDownOutlined,
} from "@ant-design/icons";

// const { Text, Paragraph } = Typography;
// const { Column } = Table;

export function CubeMetadata() {
    const dispatch = useDispatch();
    const { getCubeMetadataTablesData } = cubeRequests(dispatch);
    const Notify = notify(dispatch);
    const cubeState = useSelector((store) => store.cube);
    const { metadataTablesData, metadataDetails } = cubeState;
    const { fileName, path } = metadataDetails;
    const [expandedKeys, setExpandedKeys] = useState([]);
    const [datasourceData, setDatasourceData] = useState([]);
    // const metadataTableRef = useRef()
    // const [tableHeight, setTableHeight] = useState(250)
    // console.log({ datasourceData, metadataDetails })
    // [path, fileName]

    useEffect(() => {
        // const formData = { location: 'sai_ganesh', metadataFileName: 'Metadata_1.metadata' };
        // if (["development"].includes(process.env.NODE_ENV)) {
        //     getCubeMetadataTablesData({ path: 'narendra', fileName: 'Metadata_1.metadata' });
        // } else {
        (path && fileName) && getCubeMetadataTablesData({ path, fileName });
        // }
    }, [path, fileName])

    useEffect(() => {
        let data = [];
        let tables = metadataTablesData?.tables || {};
        for (let ele in tables) {
            let rowData = {
                key: tables[ele].name,
                row: tables[ele].name,
                // columnsData: Object.values(tables[ele].columns).map(data => ({ ...data, key: data.columnId })),
                children: Object.values(tables[ele].columns).map(col => ({ ...col, columnId: col.columnId, key: col.columnId, tableId: tables[ele].id, dataType: Object.values(col.type)[0] }))
            };
            data.push(rowData);
        }
        setDatasourceData(data);
    }, [metadataTablesData])

    const expandRow = (key) => {
        setExpandedKeys((prevState) => {
            if (prevState.includes(key)) {
                prevState = prevState.filter((item) => item !== key);
            } else {
                prevState.push(key);
            }
            return prevState;
        });
    };

    const tableVirtualProps = {
        scroll: { y: /*tableHeight || */460 },
        components: VList({
            height: /*tableHeight || */460,
            vid: "cube-metadata-table",
        }),
    };

    // console.log('in cube section one', datasourceData)

    return (
        <div className="cube-metadata-table-wraper">
            <Table
                columns={
                    cubeSecOneColumns({})
                }
                size="small"
                showHeader={false}
                rowKey={(record) => record.key}
                pagination={false}
                // bordered
                className="cube-metadata-table"
                expandedRowKeys={expandedKeys}
                {...tableVirtualProps}
                dataSource={datasourceData}
                expandable={{
                    indentSize: 8,
                    expandRowByClick: true,
                    onExpand: (expanded, record) => expandRow(record.key),
                    expandedRowKeys: expandedKeys || [],
                    expandIcon: (props) => {
                        let { record, expanded } = props;
                        console.log({ ...props });
                        if (!record.children) return null;
                        return (
                            <>
                                <TableOutlined />
                                {expanded ? (
                                    <CaretDownOutlined />
                                ) : (
                                    <CaretRightOutlined />
                                )}
                            </>
                        );
                    },

                    // expandedRowRender: (record) => {
                    //     return record.columnsData.map(column => {
                    //         // const [state, setState] = useState('');
                    //         // console.log(record);
                    //     });

                    // }
                }}
            />
            {/* </Table> */}
        </ div >
    );
}