import { Row, Col, Input, Form, Table, Menu, Checkbox, Dropdown, Button, Popconfirm } from "antd";
import { useEffect, useState } from "react";
import { columns, deleteAllConfirm, validatedTableData } from "./constants";
import { PlusOutlined, SearchOutlined, CloseOutlined, SyncOutlined } from '@ant-design/icons';
import { VList } from "virtuallist-antd";
import { useDispatch, useSelector } from "react-redux";
import { handleAddOneMoreSecurity, setDoResetFormData, setFlterbyData, setIsApplyDisabled, setSecurityKeysChecked, setSecurityTableData, setSelectedTableOrColumnKey, setShowValidatedTable } from "../../../../../../redux/actions";
import { handleGetSecurityData } from "../securityHelperMethods";
import { v4 as uuidv4 } from "uuid";
import { cloneDeep } from 'lodash-es';
import PopconfirmBody from "../../../../../common/components/Hi-Popconfirm";

const { Search } = Input;
const defaultValues = {
    show: 5,
}
export const filterValues = [{ value: 'All', isChecked: true }, { value: 'Table', isChecked: true }, { value: 'Column', isChecked: true }, { value: 'Global', isChecked: true }];

export default function ValidatedTable({ setFormData, setIsInfoShow, setShowContent, securityKeysChecked, addOneMoreSecurity, selectedTableOrColumnKey }) {
    const [form] = Form.useForm();
    const [selectOption, setSelectOption] = useState(5);
    const [expandedKeys, setExpandedKeys] = useState([]);
    const [selectedKeys, setSelectedKeys] = useState([]);
    const [searchVal, setSearchVal] = useState('');
    const [presentPage, setPresentPage] = useState(1);
    const [visible, setVisible] = useState(false);
    const { securityTableData, saveDetails, initialEditResponse, filterbyData, tables, getSecurityTableData, mode, edit, isValidatedTableShow, isApplyDisabled } = useSelector(state => state.metadata.present);
    const dispatch = useDispatch();
    let tableVirtualProps = {};
    // filterbyData    setFlterbyData

    useEffect(() => {
        if (!["test"].includes(process.env.NODE_ENV)) {
            dispatch(setFormData({}));
        }
    }, [])

    const handleFilterByChange = (isChecked, value) => {
        let prevState = cloneDeep(filterbyData), returnVal = [];
        if (value === 'All') {
            if (isChecked) {
                returnVal = cloneDeep(filterValues);
            } else {
                returnVal = prevState.map(ele => {
                    ele.isChecked = false;
                    return ele;
                })
            }
        } else {
            const arr = [{ value: 'All', isChecked: true }];
            prevState.forEach(ele => {
                if (ele.value !== 'All') {
                    if (ele.value === value) {
                        ele.isChecked = isChecked;
                    }
                    (!ele.isChecked) && (arr[0].isChecked) && (arr[0].isChecked = false);
                    arr.push(ele);
                }
            })
            returnVal = arr;
        }
        dispatch(setFlterbyData(returnVal));
    }
    const filterByMenu = <Menu className="filter-by-menu">
        {filterbyData.map((ele) => <Menu.Item className="filter-by-menu-item" key={uuidv4()}>
            <Checkbox checked={ele.isChecked} onChange={(e) => { handleFilterByChange(e.target.checked, ele.value) }}>{ele.value}</Checkbox>
        </Menu.Item>)}
    </Menu>

    const onSearch = (value) => {
        form.setFieldsValue({ search: value });
        setSearchVal(value);
    }

    (securityTableData.length > 12) && (tableVirtualProps = {
        scroll: { y: 425 },
        components: VList({
            height: 425,
            vid: "security-table",
        }),
    })

    const pagination = {
        pageSize: selectOption,
        total: securityTableData.length,
        // position: ['bottomLeft'],
        current: presentPage,
        pageSizeOptions: [5, 10, 25, 50, 75, 100],
        showSizeChanger: true,
        showTotal: (total, range) => {
            return `${range[0]} to ${range[1]} of ${total}`;
        },
        onChange: (page) => {
            setPresentPage(page)
        },
        onShowSizeChange: (current, size) => {
            setSelectOption(size)
        }
    }

    return (
        <Row>
            <Col span={24}>
                <Form data-testid="hi-metadata-validated-table-form" className="validated-table-form" form={form} autoComplete="off" initialValues={defaultValues}>
                    <Row justify="space-between" className="validated-table-row">
                        <Col span={9} className="validated-table-col-1">
                            <Form.Item className="validated-table-form-item mr-3" label="Search" name="search">
                                <Search className='validated-table-search' placeholder='Search Phrase...' onSearch={(value) => { onSearch(value) }} enterButton={(searchVal) ?
                                    <CloseOutlined onClick={(e) => { onSearch(''); e.stopPropagation() }} /> : <SearchOutlined />} />
                            </Form.Item>
                        </Col>
                        <Col span={(saveDetails) ? 14 : 11} className="validated-table-col-2"> {/*11*/}
                            <Form.Item className="validated-table-form-item" name="action-btns">
                                <Row justify='space-around'>
                                    <Col>
                                        <Dropdown visible={visible} onVisibleChange={(flag) => { setVisible(flag) }} className="filter-by" overlay={filterByMenu}>
                                            <Button type="primary">Filter By</Button>
                                        </Dropdown>
                                    </Col>
                                    <Col>
                                        <Popconfirm
                                            title={<PopconfirmBody intent="delete" description={selectedKeys.length ? "Are you sure you want to delete the selected expression(s)? You cannot undo the action." : "Please select expressions to delete."} />}
                                            onConfirm={() =>
                                                deleteAllConfirm({
                                                    setSelectedKeys,
                                                    selectedKeys,
                                                    dispatch,
                                                    securityTableData,
                                                    setSecurityTableData,
                                                    setExpandedKeys
                                                })}
                                            // onCancel={deleteAllCancel}
                                            okText="Ok"
                                            cancelText="Cancel"
                                        >
                                            <Button type="primary">Delete Selected</Button>
                                        </Popconfirm>
                                    </Col>
                                    <Col >
                                        <Button
                                            data-testid="hi-metadata-validated-table-btn"
                                            type="primary"
                                            icon={<PlusOutlined />}
                                            onClick={() => {
                                                dispatch(setDoResetFormData());
                                                Object.keys(selectedTableOrColumnKey || {}).length && dispatch(setSelectedTableOrColumnKey({}));
                                                !isApplyDisabled && dispatch(setIsApplyDisabled(true));
                                                isValidatedTableShow && dispatch(setShowValidatedTable(false));
                                                !addOneMoreSecurity && dispatch(handleAddOneMoreSecurity(true));
                                            }}
                                        >
                                            Add
                                        </Button>
                                    </Col>
                                    {(saveDetails) && <Col >
                                        <Popconfirm
                                            title="Are you sure you want to refresh security? All your changes will be lost."
                                            onConfirm={() =>
                                                handleGetSecurityData({
                                                    saveDetails,
                                                    dispatch,
                                                    initialEditResponse,
                                                    setSecurityTableData,
                                                    setIsInfoShow,
                                                    setShowValidatedTable,
                                                    setShowContent,
                                                    tables,
                                                    mode
                                                })}
                                            // onCancel={deleteCancel}
                                            okText="Ok"
                                            cancelText="Cancel"
                                        >
                                            <Button
                                                type="primary"
                                                icon={<SyncOutlined />}
                                            >
                                                Refresh
                                            </Button>
                                        </Popconfirm>
                                    </Col>}
                                </Row>
                            </Form.Item>
                        </Col>
                    </Row>
                </Form>
            </Col>
            <Col span={24}>
                <Table
                    columns={
                        columns({ tables, getSecurityTableData, mode, })
                    }
                    bordered
                    size="small"
                    rowKey={(record) => record.key}
                    // rowClassName={(record, index) => {
                    //     let className = Math.random() + "table-row-color";
                    //     return className;
                    // }}
                    pagination={pagination}
                    className="validated-table"
                    expandedRowKeys={expandedKeys}
                    tableLayout="fixed"
                    // onSelectAll={(selected, selectedRows, changeRows) => {
                    //     console.log({ selected, selectedRows, changeRows })
                    // }}
                    rowSelection={{
                        // type: 'checkbox',
                        selectedRowKeys: [...selectedKeys],
                        onSelect: (record, selected, selectedRows) => {
                            // console.log({ record, selected, selectedRows });
                            let newRows = selectedRows.map((ele) => {
                                if (ele) {
                                    return ele.key;
                                }
                            });
                            setSelectedKeys(newRows);
                        },
                        onChange: (selectedRowKeys) => {
                            // console.log(selectedRowKeys, selectedRows);
                            setSelectedKeys([...selectedRowKeys]);
                        },
                        // getCheckboxProps: (record) => {
                        //     console.log(record);
                        // },
                    }}
                    {...tableVirtualProps}
                    // onExpandedRowsChange={(keys) => console.log(keys)}
                    dataSource={[...validatedTableData({ securityTableData, setExpandedKeys, setIsApplyDisabled, setFormData, searchVal, filterbyData, selectedKeys, setSelectedKeys, dispatch, expandedKeys, edit, isValidatedTableShow, isApplyDisabled, tables, mode })]}
                    expandable={{
                        expandedRowRender: (record) => {
                            let rowChild = [];
                            for (let ele in record) {
                                (!['Actions', 'key', 'securityKeysToBeCheck', 'tooltipInfo'].includes(ele)) && rowChild.push(<Row className="row-child">
                                    <Col className='column-header' span={10}><span>{ele[0].toUpperCase() + ele?.slice(1)} : </span></Col>
                                    <Col className='column-value' span={14}><span>{record[ele]}</span></Col>
                                </Row>)
                            }
                            return rowChild;
                        },
                        showExpandColumn: false
                    }}
                />
            </Col>
        </Row >
    );

}