import { Drawer } from "antd";
import { HIOwnershipTransfer } from "./hi-ownership";

export default function RecycleBinOwnershipDrawer({ onCloseDrawer, visible, recyclebinItem, ownershipSearchedColumn, setOwnershipSearchedColumn, searchOwnershipText, setSearchOwnershipText }) {
    return <Drawer
        title={`Ownership Transfer`}
        placement="right"
        size="large"
        onClose={onCloseDrawer}
        visible={visible}
        className="ownership-drawer"
    >
        <HIOwnershipTransfer recyclebinItem={recyclebinItem} ownershipSearchedColumn={ownershipSearchedColumn} setOwnershipSearchedColumn={setOwnershipSearchedColumn} searchOwnershipText={searchOwnershipText} setSearchOwnershipText={setSearchOwnershipText} visible={visible} />
    </Drawer>;
}