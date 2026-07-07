import { LineOutlined } from "@ant-design/icons";

const canvasDefaultNodeHeight = 30;

const LineNode = (props) => {
    const { size, data, position, key, isElementRender } = props;
    const {
        label,
        width = 100,
        height = canvasDefaultNodeHeight,
        lineStyles = {},
    } = data;
    const defaultBorder = `0px solid #000000`;
    const angleRad = Math.atan2(height, width);

    const {
        stroke = 1,
        style = "solid",
        direction = "TopDown",
        styleName,
        color = "#000000",
    } = lineStyles;

    const diagonalLength = Math.sqrt(width ** 2 + height ** 2);

    return isElementRender ? (
        <div
            style={{
                alignItems: "center",
                display: "flex",
                justifyContent: isElementRender ? "none" : "center",
            }}
        >
            <div className="element-wrapper">
                <LineOutlined className="ele-icn" />
                <span style={{ height: 20 }}>Line</span>
            </div>
        </div>
    ) : (
        <div
            style={
                {
                    // display: 'flex',
                    // alignItems: 'center'
                }
            }
        >
            {direction === "TopDown" ? (
                <div
                    style={{
                        width: diagonalLength,
                        height: height,
                        transform: `rotate(${angleRad}rad)`,
                        transformOrigin: "0% 0%",
                        boxSizing: "border-box",
                        borderTop: `${stroke}px ${style} ${color}`,
                    }}
                />
            ) : (
                <div
                    style={{
                        width: diagonalLength,
                        height: height,
                        transform: `rotate(-${angleRad}rad)`,
                        transformOrigin: "0% 0%",
                        boxSizing: "border-box",
                        marginTop: height,
                        borderTop: `${stroke}px ${style} ${color}`,
                    }}
                />
            )}
        </div>
    );
};

export default LineNode;