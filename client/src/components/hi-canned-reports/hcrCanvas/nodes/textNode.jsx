import { FileTextOutlined } from "@ant-design/icons";
const canvasDefaultNodeHeight = 30;

const TextNode = (props) => {
    const { size, data, position, key, isElementRender } = props;
    const {
        id,
        label,
        fill = "#ffffff",
        fontFill = "#000000",
        fontSize = 14,
        verticalAlign = "middle",
        horizontalAlign = "center",
        mode = "Transparent",
        strikeThrough,
        underLine,
        italic,
        bold,
        fontFamily = "Serif",
        rotation = "None",
        width = 100,
        height = canvasDefaultNodeHeight,
        borders = {},
        padding = {},
    } = data;
    const defaultBorder = `0px solid #000000`;
    const defaultPadding = 0;

    let alignItems,
        justifyContent,
        textAlign = horizontalAlign;

    if ((textAlign = "justified")) {
        textAlign = "justify";
    }

    if (verticalAlign === "top") {
        alignItems = "flex-start";
    } else if (verticalAlign === "middle") {
        alignItems = "center";
    } else if (verticalAlign === "bottom") {
        alignItems = "flex-end";
    } else if (verticalAlign === "justified") {
        alignItems = "stretch";
    }

    if (horizontalAlign === "left") {
        justifyContent = "flex-start";
    } else if (horizontalAlign === "center") {
        justifyContent = "center";
    } else if (horizontalAlign === "right") {
        justifyContent = "flex-end";
    }

    const styleObj = {
        width,
        height,
        display: "flex",
        backgroundColor:
            mode.toLowerCase() === "transparent" ? "transparent" : fill,
        alignItems,
        justifyContent: isElementRender ? "none" : justifyContent,
        color: fontFill,
        borderTop: borders.Top
            ? `${borders.Top.stroke}px ${borders.Top.style?.toLowerCase()} ${borders.Top.color
            }`
            : defaultBorder,
        fontSize,
        borderBottom: borders.Bottom
            ? `${borders.Bottom.stroke}px ${borders.Bottom.style?.toLowerCase()} ${borders.Bottom.color
            }`
            : defaultBorder,
        borderLeft: borders.Left
            ? `${borders.Left.stroke}px ${borders.Left.style?.toLowerCase()} ${borders.Left.color
            }`
            : defaultBorder,
        borderRight: borders.Right
            ? `${borders.Right.stroke}px ${borders.Right.style?.toLowerCase()} ${borders.Right.color
            }`
            : defaultBorder,
        // overflow: 'hidden'
    };

    if (italic) {
        styleObj.fontStyle = "italic";
    }
    if (bold) {
        styleObj.fontWeight = "bold";
    }
    if (underLine) {
        styleObj.textDecoration = "underline";
    } else if (strikeThrough) {
        styleObj.textDecoration = "line-through";
    }

    let textRotate = 0;
    if (rotation === "Left") {
        textRotate = -90;
    } else if (rotation === "Right") {
        textRotate = 90;
    } else if (rotation === "UpsideDown") {
        textRotate = 180;
    }

    return (
        <div className="rectangle-node-container" style={styleObj}>
            <span
                id={id}
                style={{
                    fontFamily,
                    marginTop: padding.Top || defaultPadding,
                    marginBottom: padding.Bottom || defaultPadding,
                    marginLeft: padding.Left || defaultPadding,
                    marginRight: padding.Right || defaultPadding,
                    transform: `rotate(${textRotate}deg)`,
                    textAlign,
                }}
            >
                {isElementRender ? (
                    <div className="element-wrapper">
                        <FileTextOutlined className="ele-icn" />
                        <span style={{ fontFamily, marginTop: 2 }}>{label}</span>
                    </div>
                ) : (
                    label
                )}
            </span>
        </div>
    );
};

export default TextNode;