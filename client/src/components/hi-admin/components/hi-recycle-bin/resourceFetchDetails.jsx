import { Divider, Drawer, Table, Tooltip, Typography } from "antd";
import { useEffect, useMemo, useState } from "react";
import { useDispatch } from "react-redux";
import admin from "../../../../base/requests/admin.request";
import { CheckOutlined, CloseOutlined } from '@ant-design/icons';
import { VList } from "virtuallist-antd";
import { handleFetchDetailsDataSource } from "./helperMethods";
import CustomSkeletonFilebrowser from "../../../common/custom-icons/CustomSkeletons/filebrowser/CustomSkeletonFilebrowser";

const { Paragraph } = Typography;

const RecycleBinResourceDetails = ({ visible, onCloseDrawer, recyclebinItemRows = [] }) => {
    const [resourceDetails, setResourceDetails] = useState([]);
    // const [resourcesData, setResourcesData] = useState([]);
    // const [usersData, setUsersData] = useState([]);
    // const [dataSourcesData, setDataSourcesData] = useState([]);
    const dispatch = useDispatch();
    const { postRecycleBinResourceDetailsRequest } = admin(dispatch);
    const [expandedKeys, setExpandedKeys] = useState([]);
    const [loading, setLoading] = useState(false);


    useEffect(() => {
        if (visible) {
            setResourceDetails([]);
            setLoading(true);
            if (recyclebinItemRows.length) {
                const formdata = { action: "fetchDetails", recycleBinIds: recyclebinItemRows.map(obj => obj.recycleBinId) }
                postRecycleBinResourceDetailsRequest(formdata, (res) => {
                    setExpandedKeys([]);
                    setResourceDetails(handleFetchDetailsDataSource({ recyclebinItemRows, res }))
                    setLoading(false);
                }, (err) => { 
                    setLoading(false);
                })
            }
        }
    }, [visible])

    const Columns = [
        {
            title: 'Resource Name',
            dataIndex: 'resourceName',
            key: 'resourceName',
            render: (resourceName) => (
                <Tooltip
                    placement="topLeft"
                    overlayClassName="z-index-100000"
                    title={resourceName + ""}
                >
                    {resourceName}
                </Tooltip>
            ),
        }
    ];

    // const vComponents = useMemo(() => {
    //     return VList({
    //         height: 400,
    //         vid: "resource-details",
    //     });
    // }, []);

    const expandedRowRender = (record) => {
        const resourcesColumns = [
            {
                title: "Name",
                id: "name",
                dataIndex: "name",
                // width: 100,
                render: (name) => (
                    <Tooltip
                        placement="topLeft"
                        title={name}
                        overlayClassName="z-index-100000"
                        overlayInnerStyle={{ height: "100%", fontSize: 14 }}
                    >
                        <Paragraph style={{ maxWidth: 100, marginBottom: 0 }} ellipsis={true}>
                            {name}
                        </Paragraph>
                    </Tooltip>
                ),
            },
            {
                title: "Path",
                id: "path",
                className: "table-ellipsis",
                dataIndex: "path",
                // width: 110,
                render: (path) => (
                    <Tooltip
                        placement="topLeft"
                        title={path}
                        overlayClassName="z-index-100000"
                        overlayInnerStyle={{ height: "100%", fontSize: 14 }}
                    >
                        <Paragraph style={{ maxWidth: 120, marginBottom: 0 }} ellipsis={true}>
                            {path}
                        </Paragraph>
                    </Tooltip >
                ),
            },
            {
                title: "Deleted",
                id: "deleted",
                dataIndex: "deleted",
                // width: 70,
                render: (deleted) => (
                    <Tooltip
                        placement="topLeft"
                        overlayClassName="z-index-100000"
                        title={deleted + ""}
                    >
                        {deleted ? <CheckOutlined className="green-clr" /> : <CloseOutlined className="red-clr" />}
                    </Tooltip>
                ),
            },
            {
                title: "Type",
                id: "type",
                dataIndex: "type",
                // width: 90,
                render: (type) => (
                    <Tooltip
                        placement="topLeft"
                        overlayClassName="z-index-100000"
                        title={"resources"}
                    >
                        {"resources"}
                    </Tooltip>
                ),
            },
            {
                title: "Resource Id",
                id: "resourceId",
                dataIndex: "resourceId",
                // width: 90,
                render: (resourceId) => (
                    <Tooltip
                        placement="topLeft"
                        overlayClassName="z-index-100000"
                        title={resourceId}
                    >
                        {resourceId}
                    </Tooltip>
                ),
            },
        ];
        const datasourcesColumns = [
            {
                title: "Name",
                id: "name",
                dataIndex: "name",
                // width: 100,
                render: (name) => (
                    <Tooltip
                        placement="topLeft"
                        title={name}
                        overlayClassName="z-index-100000"
                        overlayInnerStyle={{ height: "100%", fontSize: 14 }}
                    >
                        <Paragraph style={{ maxWidth: 100, marginBottom: 0 }} ellipsis={true}>
                            {name}
                        </Paragraph>
                    </Tooltip>
                ),
            },
            {
                title: "Type",
                id: "type",
                dataIndex: "type",
                // width: 60,
                render: (type) => (
                    <Tooltip
                        placement="topLeft"
                        overlayClassName="z-index-100000"
                        title={type}
                    >
                        {type}
                    </Tooltip>
                ),
            },
            {
                title: "Deleted",
                id: "deleted",
                dataIndex: "deleted",
                // width: 70,
                render: (deleted) => (
                    <Tooltip
                        placement="topLeft"
                        overlayClassName="z-index-100000"
                        title={deleted + ""}
                    >
                        {deleted ? <CheckOutlined className="green-clr" /> : <CloseOutlined className="red-clr" />}
                    </Tooltip>
                ),
            },
            {
                title: "Connection Id",
                id: "connectionId",
                dataIndex: "connectionId",
                // width: 110,
                render: (connectionId) => (
                    <Tooltip
                        placement="topLeft"
                        overlayClassName="z-index-100000"
                        title={connectionId}
                    >
                        {connectionId}
                    </Tooltip>
                ),
            },
            {
                title: "Directory",
                id: "directory",
                dataIndex: "directory",
                // width: 90,
                render: (directory) => (
                    <Tooltip
                        placement="topLeft"
                        overlayClassName="z-index-100000"
                        title={directory}
                    >
                        {directory || "NA"}
                    </Tooltip>
                ),
            },
        ];
        const usersColumns = [
            {
                title: "Name",
                id: "name",
                dataIndex: "name",
                // width: 100,
                render: (name) => (
                    <Tooltip
                        placement="topLeft"
                        title={name}
                        overlayClassName="z-index-100000"
                        overlayInnerStyle={{ height: "100%", fontSize: 14 }}
                    >
                        <Paragraph style={{ maxWidth: 100, marginBottom: 0 }} ellipsis={true}>
                            {name}
                        </Paragraph>
                    </Tooltip>
                ),
            },
            {
                title: "Id",
                id: "id",
                dataIndex: "id",
                // width: 50,
                render: (id) => (
                    <Tooltip
                        placement="topLeft"
                        overlayClassName="z-index-100000"
                        title={id}
                    >
                        {id}
                    </Tooltip>
                ),
            },
            {
                title: "Deleted",
                id: "deleted",
                dataIndex: "deleted",
                // width: 70,
                render: (deleted) => (
                    <Tooltip
                        placement="topLeft"
                        overlayClassName="z-index-100000"
                        title={deleted + ""}
                    >
                        {deleted ? <CheckOutlined className="green-clr" /> : <CloseOutlined className="red-clr" />}
                    </Tooltip>
                ),
            },
            {
                title: "Type",
                id: "type",
                dataIndex: "type",
                // width: 90,
                render: (type) => (
                    <Tooltip
                        placement="topLeft"
                        overlayClassName="z-index-100000"
                        title={"users"}
                    >
                        {"users"}
                    </Tooltip>
                ),
            },
        ];

        const { usersData = [], resourcesData = [], dataSourcesData = [] } = record?.associatedDetails || {};

        return <>
            {(resourcesData.length > 0) && <>
                <Table columns={resourcesColumns} dataSource={resourcesData} pagination={false} />
                <Divider />
            </>}
            {(dataSourcesData.length > 0) && <>
                <Table columns={datasourcesColumns} dataSource={dataSourcesData} pagination={false} />
                <Divider />
            </>}
            {(usersData.length > 0) && <Table columns={usersColumns} dataSource={usersData} pagination={false} />}
        </>
    };

    return (
        <Drawer
            title={`Associated Details`}
            placement="right"
            size="large"
            onClose={onCloseDrawer}
            visible={visible}
            className="resource-details-drawer"
        >
             {loading ? (
            <CustomSkeletonFilebrowser size="mini" />
        ) : (
            <Table
                columns={Columns}
                dataSource={resourceDetails}
                bordered
                size="small"
                pagination={{ simple: false, size: 'small' }}
                expandable={{
                    expandedRowRender,
                    defaultExpandedRowKeys: ['0'],
                    expandedRowKeys: expandedKeys,
                    onExpand: ((isExpand, record) => {
                        if (isExpand) {
                            if (!expandedKeys.includes(record.key)) {
                                setExpandedKeys(prevKeys => [...prevKeys, record.key])
                            }
                        } else {
                            setExpandedKeys(prevKeys => prevKeys.filter(key => key !== record.key))
                        }
                    })
                }}
                // scroll={{ y: 400 }}
                rowKey="recycleBinId"
                // components={vComponents}
                rowClassName={({ index }) => {
                    let className = index % 2 && "table-row-color";
                    return className;
                }}
            />
        )}
        </Drawer>
    );
};

export default RecycleBinResourceDetails;
