import { diceStorageColumns, diceStorageDataSource } from "./helperMethods";
import { Row, Col, Input, Form, Table, Skeleton, } from "antd";
import { useDispatch, useSelector } from "react-redux";
import { VList } from "virtuallist-antd";
import { useRef, useState } from "react";
import LoadingBar from "../../../../../common/components/hi-loading-bar";

export default function TableContent({ tabName, diceStorageTableData, handleAbort, apiRef, loading, setLoading }) {
    // const diceStorage = useSelector((store) => store.admin.diceStorage);
    const diceStorageFieldSearchText = useSelector((store) => store.admin.diceStorage.diceStorageFieldSearchText);
    const diceStorageSearchedColumn = useSelector((store) => store.admin.diceStorage.diceStorageSearchedColumn);
    const dispatch = useDispatch();
    const skeletonRowKeys = useSelector((store) => store.admin.diceStorage.skeletonRowKeys);
    const [presentPage, setPresentPage] = useState(1);
    const [selectOption, setSelectOption] = useState(5);
    let tableVirtualProps = {
        scroll: { y: 380 },
        components: VList({
            height: 380,
            vid: "security-table",
        }),
    };
    const searchInput = useRef(null);
    const pagination = {
        pageSize: selectOption,
        total: [].length,
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
    return loading ? <><LoadingBar handleClick={() => handleAbort({ setLoading })} /><Skeleton
        loading={true}
        title={false}
        paragraph={{
            rows: 4,
        }}
    /></> : <Table
        columns={
            diceStorageColumns({ skeletonRowKeys, tabName, diceStorageFieldSearchText, diceStorageSearchedColumn, dispatch, searchInput })
        }
        bordered
        size="small"
        rowKey={(record) => record.key}
        pagination={false}
        className="dice-storage-table"
        {...tableVirtualProps}
        dataSource={diceStorageDataSource({ tableData: diceStorageTableData[tabName], dispatch, tabName, apiRef, skeletonRowKeys, handleAbort })}
    />
}