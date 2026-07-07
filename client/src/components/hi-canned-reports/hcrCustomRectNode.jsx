const CustomRectangleNode = (props) => {
    const { size = { width: 160, height: 40 }, data, position, key, isNodeTreePanel } = props;
    const { width, height } = size;
    const { label = 'Custom node', stroke = '#ccc', fill = '#fff', fontFill, fontSize } = data;

    return (
        <div
            className="rectangle-node-container"
            style={{
                width,
                height,
                borderColor: stroke,
                backgroundColor: fill,
                color: fontFill,
                fontSize,
            }}
        >
            <div style={{ color: fontFill }}>{label}</div>
        </div>
    );
};

export default CustomRectangleNode;