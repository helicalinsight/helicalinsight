
import { Tooltip } from "antd"

const Watermark = (props) => {
    const {
        isOpenSource = true,
        text = "Helical Insight",
        link,
        placement = "bottom-right", // "bottom-right" | "bottom-left" | "top-right" | "top-left"
        tooltip = null
    } = props || {

    }
    const isBottom = placement.startsWith("bottom");
    const isRight = placement.endsWith("right");

    const wrapperStyle = {
        position: "absolute",
        [isBottom ? "bottom" : "top"]: isBottom ? 2 : 0,
        [isRight ? "right" : "left"]: isRight ? "5%" : 0,
        zIndex: 1000,
        pointerEvents: "none",
    };

    const badgeStyle = {
        background: "#fff",
        margin: 0,
        padding: "0 5px",
        color: "#333",
        fontSize: "11px",
        lineHeight: 1.4,
        pointerEvents: "auto",
        borderTopLeftRadius: isBottom && isRight ? 4 : 0,
        borderTopRightRadius: isBottom && !isRight ? 4 : 0,
        borderBottomLeftRadius: !isBottom && isRight ? 4 : 0,
        borderBottomRightRadius: !isBottom && !isRight ? 4 : 0,
    };

    const linkStyle = {
        color: "#0078A8",
        textDecoration: "none",
    };

    if (!isOpenSource) return null;

    return (
        <div style={wrapperStyle}>
            <div style={badgeStyle}>
                <Tooltip title={tooltip}>
                    {link ? (
                        <a
                            href={link}
                            target="_blank"
                            rel="noopener noreferrer"
                            style={linkStyle}
                            data-testid="hi-watermark-link"
                        >
                            {text}
                        </a>
                    ) : (
                        text
                    )}
                </Tooltip>
            </div>
        </div>
    );
}

export default Watermark