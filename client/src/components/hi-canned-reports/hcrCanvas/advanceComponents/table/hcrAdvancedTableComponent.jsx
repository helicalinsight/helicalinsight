import { TableOutlined } from "@ant-design/icons";
import "./hcrAdvancedTable.scss";
import HCRAdvancedTable from "./hcrAdvancedTableView";

const HCRAdvancedTableComponent = (props = {}) => {
    const { isElementRender, data: { label = "Table" } = {} } = props;
    return (
        <div>
            {isElementRender ? (
                <div className="element-wrapper" data-testid="hcr-table-item">
                    <TableOutlined className="ele-icn" />
                    <span>{label}</span>
                </div>
            ) : (
                <HCRAdvancedTable {...props} />
            )}
        </div>
    );
};
export default HCRAdvancedTableComponent;