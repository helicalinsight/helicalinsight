import { useDispatch } from "react-redux";
import { useWindowSize } from "../customHooks/useWindowSize";
import { updateGridItemsLayout } from "../redux/actions/hreport.actions";

const useHreportGridLayout = (props) => {
    const dispatch = useDispatch();
    const [, offsetHeight] = useWindowSize();

    const createGridItems = ({ preview, fullscreen, metadataShelf, toolsAreaShelf }) => {
        let sidebarWidth = !(preview || fullscreen || !metadataShelf) ? 16.7 : 0;
        const toolsAreaWidth = !(preview || fullscreen || !toolsAreaShelf)
            ? 18.5
            : 0;
        let calculatedH = 52;
        try {
            calculatedH = offsetHeight / 12 || 52;
        } catch (e) {
            calculatedH = 52;
        }
        let layoutItems = [
            {
                i: "sidebar-area",
                y: 0,
                h: calculatedH,
                isDraggable: false,
                isResizable: false,
            },
            {
                i: "chart-area",
                y: 0,
                h: calculatedH,
                isDraggable: false,
                isResizable: false,
            },
            {
                i: "editing-area",
                y: 0,
                h: calculatedH,
                isDraggable: false,
                isResizable: false,
            },
        ];
        if (props.mode === "open") {
            sidebarWidth = 0;
            layoutItems = layoutItems.map((item) => {
                item.h = 45;
                return item;
            });
        }
        let lg = [
            { ...layoutItems[0], x: 0, w: sidebarWidth },
            {
                ...layoutItems[1],
                x: sidebarWidth,
                w: 100 - (sidebarWidth + toolsAreaWidth),
            },
            { ...layoutItems[2], x: 100 - toolsAreaWidth, w: toolsAreaWidth },
        ];
        let obj = {
            lg,
            md: lg,
            sm: [
                { ...layoutItems[0], x: 0, w: 50 },
                { ...layoutItems[1], x: 50, w: 50 },
                { ...layoutItems[2], x: 0, w: 100 },
            ],
            xs: [
                { ...layoutItems[0], x: 0, w: 50 },
                { ...layoutItems[1], x: 50, w: 50 },
                { ...layoutItems[2], x: 0, w: 100 },
            ],
            xxs: [
                { ...layoutItems[0], x: 0, w: 100 },
                { ...layoutItems[1], x: 0, w: 100 },
                { ...layoutItems[2], x: 0, w: 100 },
            ],
        };
        return obj;
    };

    const update = (layout) => {
        dispatch(updateGridItemsLayout({ layout }));
    }

    const handleToggle = (key, value) => {
        let layout = createGridItems({ ...props, [key]: value })
        update(layout);
    }

    return {
        handleToggle
    }
}

export default useHreportGridLayout;