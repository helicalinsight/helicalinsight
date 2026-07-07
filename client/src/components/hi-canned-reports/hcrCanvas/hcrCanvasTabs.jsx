import { CloseOutlined } from "@ant-design/icons";

export default function HCRCanvasTabs({ tabs = [], active, onClick = () => { }, onClose = () => { } }) {
    const containerStyles = {
        display: "flex",
        flexDirection: "column",
        width: "10%",
        position: "absolute",
    }

    const wrapperStyles = {
        display: "flex",
        flexDirection: "column",
        width: "100%",
    }

    const closeStyles = {
        position: "absolute",
        right: "5px",
        top: "5px",
        cursor: "pointer",
        zIndex: 99
    }

    return (
        <div style={containerStyles}>
            <div style={wrapperStyles}>
                {tabs.map((tab) => {
                    return (
                        <div
                            key={tab.key}
                            onClick={(e) => {
                                e.preventDefault()
                                onClick(tab)
                            }}
                            style={{
                                padding: "4px 16px",
                                cursor: "pointer",
                                background: active === tab.key ? "#fff" : "#fafafa",
                                border: "1px solid #d9d9d9",
                                borderRight: active === tab.key ? "none" : "1px solid #d9d9d9",
                                marginBottom: -1,
                                color: active === tab.key ? "rgba(5, 145, 246, 0.85)" : "rgba(0,0,0,.65)",
                                fontWeight: active === tab.key ? 500 : 400,
                                borderRadius: "2px 0 0 2px",
                                transition: "background .2s, color .2s",
                                whiteSpace: "nowrap",
                                userSelect: "none",
                                position: "relative",
                                textAlign: "center"
                            }}
                        >
                            {tab.label}
                            {tab.closable ?
                                <div
                                    style={closeStyles}
                                    onClick={(e) => {
                                        e.stopPropagation()
                                        onClose(tab)
                                    }}
                                >
                                    <CloseOutlined color="#000" />
                                </div>
                                :
                                null}
                        </div>
                    );
                })}
            </div>
        </div>
    );
}