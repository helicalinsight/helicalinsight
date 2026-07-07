import { Popconfirm, Radio, Table, Tooltip } from "antd";
import { useEffect, useMemo, useRef, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { getRecycleBinColumnSearchProps, handleOwnershipFormdata } from "./helperMethods";
import HIPopConfirm from "../../../common/components/pop-confirm";
import admin from "../../../../base/requests/admin.request";
import { VList } from "virtuallist-antd";

const userKeys = ['name', 'id', 'orgName', 'email'];

export const HIOwnershipTransfer = ({ recyclebinItem, visible, ownershipSearchedColumn, setOwnershipSearchedColumn, searchOwnershipText, setSearchOwnershipText }) => {
    const userData = useSelector((state) => state.admin.userData || []);
    const dispatch = useDispatch();
    const { postOwnershipTransferRequest } = admin(dispatch);
    // const [searchUser, setSearchUser] = useState('');
    // const [ownershipSearchedColumn, setOwnershipSearchedColumn] = useState('');
    // const [searchOwnershipText, setSearchOwnershipText] = useState('');
    const searchInput = useRef(null);
    const [selectedUser, setSelectedUser] = useState([]);

    const userColumns = [
        {
            title: 'Name',
            dataIndex: 'name',
            className: "table-ellipsis",
            sorter: (a, b) => a.name.localeCompare(b.name),
            ...getRecycleBinColumnSearchProps({ dataIndex: 'name', searchText: searchOwnershipText, searchedColumn: ownershipSearchedColumn, searchInput, setSearchText: setSearchOwnershipText, setSearchedColumn: setOwnershipSearchedColumn }),
            render: (name) => (
                <Tooltip
                    placement="top"
                    title={name}
                >
                    {name}
                </Tooltip>
            ),
        },
        {
            title: 'Id',
            dataIndex: 'id',
            className: "table-ellipsis",
            // sorter: (a, b) => a.resource.localeCompare(b.resource),
            ...getRecycleBinColumnSearchProps({ dataIndex: 'id', searchText: searchOwnershipText, searchedColumn: ownershipSearchedColumn, searchInput, setSearchText: setSearchOwnershipText, setSearchedColumn: setOwnershipSearchedColumn }),
            render: (id) => (
                <Tooltip
                    placement="top"
                    title={id}
                >
                    {id}
                </Tooltip>
            ),
        },
        {
            title: 'Organization',
            dataIndex: 'orgName',
            className: "table-ellipsis",
            sorter: (a, b) => a.orgName.localeCompare(b.orgName),
            ...getRecycleBinColumnSearchProps({ dataIndex: 'orgName', searchText: searchOwnershipText, searchedColumn: ownershipSearchedColumn, searchInput, setSearchText: setSearchOwnershipText, setSearchedColumn: setOwnershipSearchedColumn }),
            render: (orgName) => (
                <Tooltip
                    placement="top"
                    title={orgName}
                >
                    {orgName}
                </Tooltip>
            ),
        },
        {
            title: 'Email',
            dataIndex: 'email',
            className: "table-ellipsis",
            sorter: (a, b) => a.email.localeCompare(b.email),
            ...getRecycleBinColumnSearchProps({ dataIndex: 'email', searchText: searchOwnershipText, searchedColumn: ownershipSearchedColumn, searchInput, setSearchText: setSearchOwnershipText, setSearchedColumn: setOwnershipSearchedColumn }),
            render: (email) => (
                <Tooltip
                    placement="top"
                    title={email}
                >
                    {email}
                </Tooltip>
            ),
        },
    ];

    useEffect(() => {
        if (selectedUser.length) {
            const formdata = handleOwnershipFormdata({ recyclebinItem, selectedUser })
            postOwnershipTransferRequest(formdata, (res) => {
                // console.log(res)
            }, (err) => {
                console.log(err)
            })
        }
    }, [selectedUser])

    const onSelectChange = (newSelectedUserKey) => {
        setSelectedUser([newSelectedUserKey]);
    };

    const onCancelPopover = () => {
        // setSelectedUser([]);
    }

    useEffect(() => {
        if (!visible) {
            setSelectedUser([]);
        }
    }, [visible])

    const rowSelection = {
        type: 'radio',
        selectedRowKeys: selectedUser,
        // onChange: onSelectChange,
        renderCell: (checked, record, index, originNode) => <Popconfirm
            title={<HIPopConfirm title={`${'Ownership Transfer'}`}
                description={<p>Are you sure you want to transfer ownership of <b>{recyclebinItem.data.name}</b> to <b>{record.name}</b> user? </p>} />}
            onConfirm={() => { onSelectChange(record.id) }}
            okText="Ok"
            onCancel={onCancelPopover}
            cancelText="Cancel"
            placement='left'
        >
            <Radio checked={selectedUser[0] === record.id} />
        </Popconfirm>

    }

    const vComponents = useMemo(() => {
        return VList({
            height: 450,
            vid: "ownership",
        });
    }, []);

    return <>
        <Table
            rowKey={'id'}
            // className='ownership-table'
            size='small'
            rowSelection={rowSelection}
            bordered
            pagination={false}
            columns={userColumns}
            dataSource={userData}
            scroll={{ y: 450 }}
            components={vComponents}
            rowClassName={(record, index) => {
                let className = index % 2 && "table-row-color";
                return className;
            }}
        />
    </>

}