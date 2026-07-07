import React, { useEffect, useMemo, useRef, useState } from 'react';
import { Table, Button, Space, Row, Col, Popconfirm, Tooltip, Card, Skeleton, Dropdown, Menu, Radio, Input, Anchor } from 'antd';
import { DeleteOutlined, ClearOutlined, InfoCircleOutlined, SyncOutlined, DownOutlined, SearchOutlined, CloseOutlined } from '@ant-design/icons';
import { getRecycleBinColumnSearchProps, handleSlnoOrder } from './helperMethods';
import admin from '../../../../base/requests/admin.request';
import { useDispatch, useSelector } from 'react-redux';
import moment from "moment";
import LoadingBar from '../../../common/components/hi-loading-bar';
import { VList } from 'virtuallist-antd';
import MoreRecycleBinInfo from './MoreRecycleBinInfo';
import { handleRecycleBinData } from '../../../../redux/actions/admin.actions';
import HIIcon from '../../../common/icons/hi-icons';
import HIPopConfirm from '../../../common/components/pop-confirm';
import "./hi-recycle-bin.scss";
import { fetchUsersData } from '../hi-userManagement/helperMethods';
import RecycleBinOwnershipDrawer from './recyclebin-ownership-drawer';
import RecycleBinResourceDetails from './resourceFetchDetails';
import OwnershipTransferIcon from '../../../common/icons/hi-ownership-icon';
import RecycleBinSkeleton from '../../../common/custom-icons/CustomSkeletons/recyclebin/RecycleBinSkeleton';
// let tableVirtualProps = {};
import {calculateSerialNumber} from '../../../../utils/table-utils.js';

const disableOwnershipCategories = ["Organizations", "Users"];

const HIRecycleBin = ({ apiRef, handleAbort }) => {
    const recycleBinData = useSelector((store) => store.admin.recycleBinData);
    const userData = useSelector((state) => state.admin.userData);
    const [visible, setVisible] = useState(false);
    const [isOwnershipDrawerOpen, setIsOwnershipDrawerOpen] = useState(false);
    const [isResourceDetailsDrawerOpen, setIsResourceDetailsDrawerOpen] = useState(false);
    const [loading, setLoading] = useState(false);
    const [moreInfo, setMoreInfo] = useState([]);
    const [selectedRowKeys, setSelectedRowKeys] = useState([]);
    const [selectedRowObjs, setSelectedRowObjs] = useState([]);
    const [searchText, setSearchText] = useState('');
    const [searchedColumn, setSearchedColumn] = useState('');
    const dispatch = useDispatch();
    const { postRecycleBinListRequest, postRecycleBinDeleteRequest, postRecycleBinRestoreRequest, postRecycleBinClearRequest } = admin(dispatch);
    const searchInput = useRef(null);
    const [ownershipSearchedColumn, setOwnershipSearchedColumn] = useState('');
    const [searchOwnershipText, setSearchOwnershipText] = useState('');
    const [resourceItem, setResourceItem] = useState({});
    const [resourceItemKeysToDelete, setResourceItemKeysToDelete] = useState([]);
    const [page, setPage] = useState(1);
    const [pageSize, setPageSize] = useState(10);

    const onCloseDrawer = () => {
        setVisible(false);
    };

    const handleRecycleListService = () => {
        setLoading(true);
        apiRef.current = postRecycleBinListRequest((res) => {
            setLoading(false);
            let data = res?.data.map((ele, i) => {
                const data = ele.data;
                const type = ele.recycleBinType;
                const deletedOn = ele.deletedOn;
                const deletedBy = ele.deletedBy;
                const recycleBinId = ele.recycleBinId;
                const slno = i + 1;
                let reqObj = { slno, data, deletedOn, deletedBy, recycleBinId, type };
                return reqObj;
            }) || [];
            dispatch(handleRecycleBinData(data))
            setSelectedRowKeys([]);
            setSelectedRowObjs([]);
            setPage(1);
            setPageSize(10);
        }, (err) => {
            setLoading(false);
        })
    }

    useEffect(() => {
        if (!recycleBinData?.length) {
            handleRecycleListService();
        }
        if (!userData?.length) {
            fetchUsersData(dispatch, false)
        }
    }, [])

    const onSelectChange = (newSelectedRowKeys, selectedRows = []) => {
        setSelectedRowObjs(selectedRows.map(ele => ({ recycleBinId: ele.recycleBinId, name: ele.data.name })))
        setSelectedRowKeys(newSelectedRowKeys);
    };

    const rowSelection = {
        selectedRowKeys,
        onChange: onSelectChange,
    }

    const onClickMoreInfo = (record) => {
        let convertedMoreDetails = [];
        let recycleBinValue;
        setVisible(true);

        const moreInfoTransformer = (record) => Object.entries(record).map(([key, value]) => {
            let entry = {};
            entry.recycleBinKey = key === "slno" ? "S No" :
                key.charAt(0).toUpperCase() +
                key
                    .slice(1)
                    .replace(/([A-Z])/g, " $1")
                    .trim();

            if (key === "deletedOn") {
                entry.recycleBinValue = moment(new Date(JSON.parse(record[key]))).format("dddd, MMMM Do, YYYY, h:mm:ss a");
            } else if (key === "data") {
                entry.recycleBinValue = "";
                entry.children = moreInfoTransformer(value);
            } else {
                entry.recycleBinValue = value.toString();
            }

            return entry;
        });

        setMoreInfo(moreInfoTransformer(record))
    };

    const handleFetchDetails = (rowObjs) => {
        setResourceItemKeysToDelete(rowObjs);
        setIsResourceDetailsDrawerOpen(true);
    }

    const handleOwnership = (record) => {
        setResourceItem(record);
        setIsOwnershipDrawerOpen(true);
    }

    const columns = [
        {
            title: "S.No",
            dataIndex: "slno",
            width: 70,
            className: "table-ellipsis",
            render: (value, item, index) => calculateSerialNumber(page, pageSize, index),
            fixed: 'left',
        },
        {
            title: 'Resource',
            dataIndex: 'data',
            className: "table-ellipsis",
            sorter: (a, b) => a.data.name.localeCompare(b.data.name),
            ...getRecycleBinColumnSearchProps({ dataIndex: 'data', searchText, searchedColumn, searchInput, setSearchText, setSearchedColumn }),
            render: (data) => (
                <Tooltip
                    placement="topLeft"
                    title={data.name}
                >
                    {data.name}
                </Tooltip>
            ),
        },
        {
            title: 'Deleted On',
            className: "table-ellipsis",
            dataIndex: 'deletedOn',
            render: (date) => (
                <Tooltip
                    placement="topLeft"
                    title={moment(new Date(JSON.parse(date))).format("dddd, MMMM Do, YYYY, h:mm:ss a")}
                    overlayInnerStyle={{ height: "100%", fontSize: 14 }}
                >
                    {moment(new Date(JSON.parse(date))).format("dddd, MMMM Do, YYYY, h:mm:ss a")}
                </Tooltip>
            ),
            sorter: (a, b) =>
                new Date(a.deletedOn) - new Date(b.deletedOn),
        },
        {
            title: 'Deleted By',
            dataIndex: 'deletedBy',
            className: "table-ellipsis",
            sorter: (a, b) => a.deletedBy.localeCompare(b.deletedBy),
            ...getRecycleBinColumnSearchProps({ dataIndex: 'deletedBy', searchText, searchedColumn, searchInput, setSearchText, setSearchedColumn }),
            render: (deletedBy) => (
                <Tooltip
                    placement="topLeft"
                    title={deletedBy}
                >
                    {deletedBy}
                </Tooltip>
            ),
        },
        {
            title: 'Type',
            dataIndex: 'type',
            className: "table-ellipsis",
            sorter: (a, b) => a.type.localeCompare(b.type),
            render: (type) => (
                <Tooltip
                    placement="topLeft"
                    title={type}
                >
                    {type}
                </Tooltip>
            ),
            filters: [
                {
                    text: 'Datasources[Efwd]',
                    value: 'Datasources[Efwd]',
                },
                {
                    text: 'Datasources[Managed]',
                    value: 'Datasources[Managed]',
                },
                {
                    text: 'Files',
                    value: 'Files',
                },
                {
                    text: 'Folders',
                    value: 'Folders',
                },
                {
                    text: 'Organizations',
                    value: 'Organizations',
                },
                {
                    text: 'Users',
                    value: 'Users',
                },
            ],
            onFilter: (value, record) => record.type.indexOf(value) === 0,
        },
        {
            title: 'Actions',
            key: 'action',
            render: (record) => (
                <Space size="middle">
                    <Tooltip
                        placement="top"
                        title={'Restore'}
                    >
                        <span onClick={() => handleRestore(record.recycleBinId)}>
                            <HIIcon color={'black'} name="hi-restore" />
                        </span>
                    </Tooltip>
                    <Popconfirm
                        title={<HIPopConfirm title="Delete" description={<><p>This will erase this resource and you will not be able to recover it. Please make sure you want to do this before proceeding.
                        </p>
                            <Button style={{ padding: 0 }} onClick={() => { handleFetchDetails([{ recycleBinId: record.recycleBinId, name: record.data.name }]) }} type="link" size={'small'}>
                                Display associated file(s)
                            </Button>
                        </>}
                        />}
                        onConfirm={() => {
                            handleDelete(record.recycleBinId)
                        }}
                        onCancel={(e) => { }}
                        okText="Ok"
                        cancelText="Cancel"
                        placement='left'
                    >
                        <Tooltip
                            placement="top"
                            title={'Delete Permanently'}
                        >
                            <DeleteOutlined />
                        </Tooltip>
                    </Popconfirm>
                    <Tooltip
                        title={'Ownership Transfer'}
                    >
                        <Button className={`ownership-btn ${disableOwnershipCategories.includes(record.type) ? "opacity-4" : ""}`} disabled={disableOwnershipCategories.includes(record.type) ? true : false}>
                            <span onClick={() => { handleOwnership(record) }}>
                                <OwnershipTransferIcon />
                            </span>
                        </Button>
                    </Tooltip>
                </Space>
            ),
        },
        {
            title: "Details",
            className: "table-ellipsis",
            render: (_, record, index) => (
                <Tooltip title="More Info">
                    <Button type="link" onClick={() => {
                        const dupRecord = { ...record };
                        dupRecord.slno = calculateSerialNumber(page, pageSize, index);
                        onClickMoreInfo(dupRecord);
                    }}>
                        More Info
                    </Button>
                </Tooltip>
            ),
        },
    ];

    const handleDelete = (id) => {
        let formdata = { action: "delete", recycleBinIds: [id] }
        postRecycleBinDeleteRequest(formdata, (res) => {
            const deleted = res.recycleBin.completed;
            const data = recycleBinData.filter(ele => !deleted.includes(ele.recycleBinId)).map((ele, i) => {
                return handleSlnoOrder(ele, i);
            });
            dispatch(handleRecycleBinData(data));
            setSelectedRowKeys(prevKeys => prevKeys.filter(key => key !== id));
            setSelectedRowObjs(prevEles => prevEles.filter(obj => obj.recycleBinId !== id));
        }, (err) => { })
    };

    const handleRestore = (id) => {
        let formdata = { action: "restore", recycleBinIds: [id] }
        postRecycleBinRestoreRequest(formdata, (res) => {
            const restored = res.recycleBin.completed;
            const data = recycleBinData.filter(ele => !restored.includes(ele.recycleBinId)).map((ele, i) => {
                return handleSlnoOrder(ele, i);
            });
            dispatch(handleRecycleBinData(data));
            setSelectedRowKeys(prevKeys => prevKeys.filter(key => key !== id));
            setSelectedRowObjs(prevEles => prevEles.filter(obj => obj.recycleBinId !== id));
        }, (err) => { })
    };

    const deleteSelected = () => {
        let formdata = { action: "delete", recycleBinIds: [...selectedRowKeys] }
        postRecycleBinDeleteRequest(formdata, (res) => {
            const deleted = res.recycleBin.completed;
            const data = recycleBinData.filter(ele => !deleted.includes(ele.recycleBinId)).map((ele, i) => {
                return handleSlnoOrder(ele, i);
            });
            dispatch(handleRecycleBinData(data));
            setSelectedRowKeys([]);
            setSelectedRowObjs([]);

        }, (err) => { })
    }

    const restoreSelected = () => {
        let formdata = { action: "restore", recycleBinIds: [...selectedRowKeys] }
        postRecycleBinRestoreRequest(formdata, (res) => {
            const restored = res.recycleBin.completed;
            const data = recycleBinData.filter(ele => !restored.includes(ele.recycleBinId)).map((ele, i) => {
                return handleSlnoOrder(ele, i);
            });
            dispatch(handleRecycleBinData(data));
            setSelectedRowKeys([]);
            setSelectedRowObjs([]);
        }, (err) => { })
    }

    const vComponents = useMemo(() => {
        return VList({
            height: 420,
            vid: "recycleBin",
            resetTopWhenDataChange: false
        });
    }, []);

    // if (recycleBinData !== null) {
    //     if (recycleBinData.length >= 8) {
    //         tableVirtualProps = {
    //             scroll: { y: 370 },
    //             components: VList({
    //                 height: 420,
    //                 vid: "recycleBin",
    //             }),
    //         };
    //     }
    // } else {
    //     tableVirtualProps = {};
    // }

    const handleClear = () => {
        postRecycleBinClearRequest((res) => {
            const notDeleted = res.recycleBin.incomplete;
            const filtered = recycleBinData.filter(r => {
                return notDeleted.includes(r.recycleBinId)
            })
            dispatch(handleRecycleBinData(filtered));
            setSelectedRowKeys([]);
            setSelectedRowObjs([]);
        }, (err) => { })
    }

    const ActionsCard = () => {
        return (
            <Card hoverable className="actions-card">
                <Row justify="space-evenly" align="middle">
                    {/* <Col >
                        <Tooltip title={'Ownership Transfer'} placement='top'>
                            <Button
                                type="text"
                                icon={<HIIcon color={'black'} name="hi-ownership-transfer" />}
                                onClick={() => {
                                    setOwnershipResourceItem(record); setIsOwnershipDrawerOpen(true);
                                }}
                            >
                                Transfer Ownership
                            </Button>
                        </Tooltip>
                    </Col> */}
                    <Col >
                        <Tooltip title={'Click on refresh button to see the updated data'}
                            placement='top'>
                            <Button
                                type="text"
                                icon={<SyncOutlined />}
                                onClick={() => {
                                    handleRecycleListService();
                                }}
                            >
                                Refresh
                            </Button>
                        </Tooltip>
                    </Col>
                    <Col >
                        <Tooltip title={'This will bring back the selected resources to its original location'}
                            placement='top'>
                            <Button
                                type="text"
                                disabled={!selectedRowKeys.length}
                                icon={<HIIcon color={selectedRowKeys.length ? 'black' : ''} name="hi-restore" />}
                                onClick={() => {
                                    restoreSelected();
                                }}
                            >
                                Restore Selected{selectedRowKeys.length ? ` (${selectedRowKeys.length})` : ''}
                            </Button>
                        </Tooltip>
                    </Col>
                    <Col >
                        <Popconfirm
                            disabled={!selectedRowKeys.length}
                            title={<HIPopConfirm title="Delete" description={<><p>{selectedRowKeys.length ? "This will erase the selected resources and you will not be able to recover it. Please make sure you want to do this before proceeding." : "Please select resources to delete permanently."}</p>
                                <Button style={{ padding: 0 }} onClick={() => { handleFetchDetails([...selectedRowObjs]) }} type="link" size={'small'}>
                                    Display associated file(s)
                                </Button>
                            </>} />}
                            onConfirm={() => {
                                deleteSelected();
                            }}
                            okText="Ok"
                            cancelText="Cancel"
                            placement='bottomRight'
                        >
                            <Tooltip title={'Delete Selected Resources Permanently'} placement='top'>
                                <Button disabled={!selectedRowKeys.length} icon={<DeleteOutlined />} type="text">Delete Selected{selectedRowKeys.length ? ` (${selectedRowKeys.length})` : ''}</Button>
                            </Tooltip>
                        </Popconfirm>
                    </Col>
                    <Col >
                        <Popconfirm
                            title={<HIPopConfirm title={`${'Empty Recycle Bin'}`} description={<p>Are you sure you want to clear everything? All Recycle Bin resources will be deleted permanently.</p>} />}
                            onConfirm={() => { handleClear(); }}
                            okText="Ok"
                            cancelText="Cancel"
                            placement='bottomRight'
                        >
                            <Tooltip title={'Empty Recycle Bin'} placement='top'>
                                <Button
                                    type="text"
                                    icon={<ClearOutlined />}
                                >
                                    Empty Recycle Bin
                                </Button>
                            </Tooltip>

                        </Popconfirm>
                    </Col>
                </Row>
            </Card>
        );
    };

    const onCloseOwnershipDrawer = () => {
        setIsOwnershipDrawerOpen(false);
    }

    const onCloseResourceDetailsDrawer = () => {
        setIsResourceDetailsDrawerOpen(false);
    }

    const renderSkeleton = () => <Skeleton title={true} paragraph={false} />;

    return (
        <>
            <Row className="hi-admin-recycle-bin-container">
                <Col span={24} className="recycle-bin-header-container">
                    <Row className="recycle-bin-header-data-container">
                        <Col span={4}>
                            <Row align="middle">
                                <Col className="recycle-bin-title">
                                    <span>Recycle Bin</span>
                                </Col>
                                <Col>
                                    <Tooltip overlayClassName='recycle-info' title={"A recycle bin features allows users to restore deleted files, folder, users or any other resources."} placement='right'>
                                        <InfoCircleOutlined className="recycle-bin-alert-icon" />
                                    </Tooltip>
                                </Col>
                            </Row>
                        </Col>
                        <Col lg={16} sm={20}>
                            <ActionsCard />
                        </Col>
                    </Row>
                </Col>
                <Col span={24} className="recycle-bin-table-container">
                    <Card bordered={false} hoverable>
                        {(loading && !["test"].includes(process.env.NODE_ENV)) ? <><LoadingBar handleClick={handleAbort} /><RecycleBinSkeleton />
                        </> :
                            <Table
                                dataSource={recycleBinData}
                                size="small"
                                rowSelection={rowSelection}
                                rowKey="recycleBinId"
                                columns={
                                    loading
                                        ? columns.map((column) => {
                                            return { ...column, render: renderSkeleton };
                                        })
                                        : columns
                                }
                                pagination={{
                                    simple: false,
                                    size: 'small',
                                    current: page,
                                    pageSize: pageSize,

                                    onChange: (current) => setPage(current),
                                    onShowSizeChange: (current, size) => setPageSize(size),
                                    pageSizeOptions: [10, 20, 50, 100],
                                }}
                                bordered
                                scroll={{ y: 370 }}
                                components={vComponents}
                                // {...tableVirtualProps}
                                rowClassName={(record, index) => {
                                    let className = index % 2 && "table-row-color";
                                    return className;
                                }}
                            />}
                    </Card>
                </Col>
            </Row>
            <MoreRecycleBinInfo visible={visible} moreInfo={moreInfo} onCloseDrawer={onCloseDrawer} />
            {isOwnershipDrawerOpen && <RecycleBinOwnershipDrawer visible={isOwnershipDrawerOpen} onCloseDrawer={onCloseOwnershipDrawer} recyclebinItem={resourceItem} ownershipSearchedColumn={ownershipSearchedColumn} setOwnershipSearchedColumn={setOwnershipSearchedColumn} searchOwnershipText={searchOwnershipText} setSearchOwnershipText={setSearchOwnershipText} />}
            <RecycleBinResourceDetails visible={isResourceDetailsDrawerOpen} onCloseDrawer={onCloseResourceDetailsDrawer} recyclebinItemRows={resourceItemKeysToDelete} />
        </>

    );
};

export { HIRecycleBin };
