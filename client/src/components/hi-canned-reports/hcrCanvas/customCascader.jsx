import { RightOutlined } from "@ant-design/icons";
import { useEffect, useRef, useState } from "react";
import { v4 as uuidv4 } from "uuid";

const CustomCascader = (props) => {
    const {
        options = [],
        onChange,
        open
    } = props || {};
    const [selectedPath, setSelectedPath] = useState([]);
    const [activeMenus, setActiveMenus] = useState([options]);
    const containerRef = useRef(null);

    const handleItemClick = (item, index, level) => {
        const newPath = selectedPath.slice(0, level);
        newPath.push({ index, level });

        if (item.children && item.children.length > 0) {
            const newMenus = activeMenus.slice(0, level + 1);
            newMenus.push(item.children);
            setActiveMenus(newMenus);
            setSelectedPath(newPath);
        } else {
            setSelectedPath(newPath);
            setActiveMenus([options]);

            if (onChange) {
                const values = getValuesFromPath(newPath);
                onChange(values);
            }
        }
    };

    // const handleItemHover = (item, index, level) => {
    //     if (item.children && item.children.length > 0) {
    //         const newMenus = activeMenus.slice(0, level + 1);
    //         newMenus.push(item.children);
    //         setActiveMenus(newMenus);
    //     } else {
    //         setActiveMenus(activeMenus.slice(0, level + 1));
    //     }
    // };

    const getValuesFromPath = (path) => {
        let current = options;
        const values = [];
        for (let pathItem of path) {
            const item = current[pathItem.index];
            values.push(item);
            current = item.children || [];
        }
        return values[values.length - 1];
    };

    useEffect(() => {
        if (!open) {
            setActiveMenus([options]);
            setSelectedPath([]);
        }
    }, [open, options])

    return (
        <div className="cascader-container" ref={containerRef}>
            <div className="cascader-dropdown">
                {activeMenus.map((menu, level) => (
                    <div key={uuidv4()} className="cascader-menu">
                        {menu.map((item, index) => (
                            <div
                                key={`${item.value}-${uuidv4()}`}
                                className={"cascader-menu-item"}
                                onClick={() => handleItemClick(item, index, level)}
                            // onMouseEnter={() => handleItemHover(item, index, level)}
                            >
                                <span className="cascader-menu-item-label">{item.label}</span>
                                {item.children && item.children.length > 0 && (
                                    <RightOutlined size={14} className="cascader-menu-item-arrow" />
                                )}
                            </div>
                        ))}
                    </div>
                ))}
            </div>
        </div>
    );
};


export default CustomCascader;