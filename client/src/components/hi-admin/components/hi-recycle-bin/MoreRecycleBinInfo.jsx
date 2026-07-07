import { Drawer, Table } from "antd";

const MoreRecycleBinInfo = ({ visible, moreInfo = [], onCloseDrawer }) => {
    const moreInfoColumns = [
        {
            title: "Key",
            id: "recycleBinKey",
            dataIndex: "recycleBinKey",
        },
        {
            title: "Value",
            id: "recycleBinValue",
            dataIndex: "recycleBinValue",
            render: (text, record) => {
                const { recycleBinKey, recycleBinValue } = record;
                return <span>{recycleBinValue.toString()}</span>;
            },
        },
    ];

    const dataInfo = moreInfo?.find(ele => ele.recycleBinKey === 'Data');
    const nameInfo = dataInfo?.children.find(ele => ele.recycleBinKey === 'Name');
    const title =`${nameInfo?.recycleBinValue} Details`;

    return (
        <Drawer
            destroyOnClose={true}
            title={title}
            placement="right"
            size="large"
            onClose={onCloseDrawer}
            visible={visible}
            className="more-info-drawer"
        >
            <Table
                columns={moreInfoColumns}
                dataSource={moreInfo}
                bordered
                size="small"
                pagination={false}
                expandable={{
                    defaultExpandAllRows: true,
                }}
                rowClassName={({ index }) => {
                    let className = index % 2 && "table-row-color";
                    return className;
                }}
            />
        </Drawer>
    );
};

export default MoreRecycleBinInfo;
