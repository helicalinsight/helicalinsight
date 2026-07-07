import HIIcon from "../../../common/icons/hi-icons";

const canvasDefaultNodeHeight = 30;
const PageBreakNode = (props) => {
    const { size, data, position, key, isElementRender } = props;
    const {
        label,
        width = 100,
        height = canvasDefaultNodeHeight,
        lineStyles = {},
    } = data;
    const angleRad = Math.atan2(height, width);

    const {
        stroke = 1,
        style = "solid",
        direction = "TopDown",
        styleName,
        color = "#000000",
    } = lineStyles;

    return isElementRender ? (
        <div
            style={{
                alignItems: "center",
                display: "flex",
                justifyContent: isElementRender ? "none" : "center",
            }}
        >
            <div className="element-wrapper" style={{ width: 103 }}>
                <HIIcon name="hcr-page-break" className={"ele-icn"} />
                <span style={{ height: 20 }}>Page Break</span>
            </div>
        </div>
    ) : (
        <div
            style={{
                display: "flex",
                alignItems: "center",
                height,
            }}
        >
            <div
                style={{
                    width,
                    borderTop: `${stroke}px ${style} ${color}`,
                }}
            />
        </div>
    );
};


export default PageBreakNode;