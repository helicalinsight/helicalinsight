import { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { hcrActions } from '../../../../../../redux/actions';
import { HCR_CROSSTAB_CELL_HEIGHT, HCR_CROSSTAB_CELL_WIDTH } from '../../../../hcr-constants';
import { getCrosstabApplyStyles } from '../../../hcrCanvasPaneHelperMethods';
import { getCrosstabLayout, getCrosstabTrackIndexes } from '../../utils';
import CrosstabEditableCell from './crosstabEditableCell';

const MIN_HEIGHT = HCR_CROSSTAB_CELL_HEIGHT,
    MIN_WIDTH = HCR_CROSSTAB_CELL_WIDTH;


const CrosstabLayout = (props = {}) => {
    const {
        data,
        onNodeClick = () => { },
        onCloseSidePanel = () => { },
        selectedCells = [],
        selectedNodes = [],
        copiedNodes = [],
        mode = "view"
    } = props || {}
    const editable = mode === "edit";
    const {
        config = {},
        id: CTId,
    } = data || {};
    const {
        columnGroups = [],
        rowGroups = [],
        measureCells = [],
        crosstabHeader = {},
        nodes = {},
        colWidths: ctColWidths,
        rowHeights: ctRowHeights,
    } = config || {};
    const { width: emptyCellWidth, height: emptyCellHeight } = crosstabHeader || {}
    const dispatch = useDispatch()
    const activeTab = useSelector((state) => state.cannedReports.present.hcrTabData.panes.find(
        (pane) => pane.key === state.cannedReports.present.hcrTabData.activeKey)
    ) || {};

    const [colWidths, setColWidths] = useState(ctColWidths || []);
    const [rowHeights, setRowHeights] = useState(ctRowHeights || []);

    const gridRef = useRef(null);
    const dragRef = useRef(null);
    const colHandleRefs = useRef([]);
    const rowHandleRefs = useRef([]);

    const { tableStyles = [] } = activeTab || {}
    const editableCellProps = {
        onNodeClick,
        selectedCells,
        selectedNodes,
        onCloseSidePanel,
        copiedNodes
    }

    const borderRight = {
        borderRight: '1px solid #b9c7d8',
        borderBottom: '1px solid #b9c7d8',
    }

    const cells = getCrosstabLayout(config);
    const { colIndexes, rowIndexes } = useMemo(() => getCrosstabTrackIndexes(config), [config]);
    const visibleColWidths = useMemo(() => colIndexes.map((index) => colWidths[index] ?? 0), [colIndexes, colWidths]);
    const visibleRowHeights = useMemo(() => rowIndexes.map((index) => rowHeights[index] ?? 0), [rowIndexes, rowHeights]);
    const totalWidth = visibleColWidths.reduce((a, b) => a + b, 0);
    const totalHeight = visibleRowHeights.reduce((a, b) => a + b, 0);

    function updateCrosstab(actionType, payload = {}) {
        dispatch(hcrActions.hcrUpdateCrosstabComponent({
            id: CTId,
            actionType,
            ...payload
        }))
    }


    const updateCTLayout = ({ colWidths, rowHeights, index, type, resizeFactor }) => {
        function getCellsWithIndex(type, index) {
            return cells.filter((cell) => {
                if (type === "col") {
                    return cell.widthUpdaters.includes(index)
                }
                if (type === "row") {
                    return cell.heightUpdaters.includes(index)
                }
            })
        }

        function getCellIds(cells) {
            return cells.map((cell) => cell.id)
        }

        let payload = { colWidths, rowHeights, type, cellsToUpdate: getCellIds(getCellsWithIndex(type, index)), position: index, resizeFactor }

        updateCrosstab("updateCrosstabLayout", payload)
    }


    const applyGridTemplate = useCallback((widths, heights) => {
        const el = gridRef.current;
        if (!el) return;
        el.style.gridTemplateColumns = widths.map((w) => `${w}px`).join(" ");
        el.style.gridTemplateRows = heights.map((h) => `${h}px`).join(" ");
    }, []);

    const positionHandle = useCallback((handleEl, type, index, widths, heights) => {
        if (!handleEl) return;
        if (type === "col") {
            const x = widths.slice(0, index + 1).reduce((a, b) => a + b, 0);
            handleEl.style.left = `${x - 3}px`;
        } else {
            const y = heights.slice(0, index + 1).reduce((a, b) => a + b, 0);
            handleEl.style.top = `${y - 3}px`;
        }
    }, []);

    const onMouseDown = useCallback(
        (type, index, e) => {
            e.preventDefault();
            const startCoord = type === "col" ? e.clientX : e.clientY;
            const startSize = type === "col" ? colWidths[index] : rowHeights[index];
            const workingWidths = [...colWidths];
            const workingHeights = [...rowHeights];
            const getVisibleWidths = (widths) => colIndexes.map((i) => widths[i] ?? 0);
            const getVisibleHeights = (heights) => rowIndexes.map((i) => heights[i] ?? 0);

            dragRef.current = { type, index, startCoord, startSize, workingWidths, workingHeights, resizeFactor: 0 };

            const onMouseMove = (moveEvt) => {
                const d = dragRef.current;
                if (!d) return;
                const coord = d.type === "col" ? moveEvt.clientX : moveEvt.clientY;
                const delta = coord - d.startCoord;
                dragRef.current.resizeFactor = delta;
                if (d.type === "col") {
                    d.workingWidths[d.index] = Math.max(MIN_WIDTH, Math.round(d.startSize + delta));
                } else {
                    d.workingHeights[d.index] = Math.max(MIN_HEIGHT, Math.round(d.startSize + delta));
                }
                const nextWidths = getVisibleWidths(d.workingWidths);
                const nextHeights = getVisibleHeights(d.workingHeights);
                applyGridTemplate(nextWidths, nextHeights);
                colHandleRefs.current.forEach((el, i) =>
                    positionHandle(el, "col", i, nextWidths, nextHeights)
                );
                rowHandleRefs.current.forEach((el, i) =>
                    positionHandle(el, "row", i, nextWidths, nextHeights)
                );
            };

            const onMouseUp = () => {
                const d = dragRef.current;
                if (d) {
                    setColWidths(d.workingWidths);
                    setRowHeights(d.workingHeights);
                    if (d.resizeFactor) {
                        updateCTLayout({
                            colWidths: d.workingWidths,
                            rowHeights: d.workingHeights,
                            index: d.index,
                            type: d.type,
                            resizeFactor: d.resizeFactor
                        });
                    }
                }
                dragRef.current = null;
                window.removeEventListener("mousemove", onMouseMove);
                window.removeEventListener("mouseup", onMouseUp);
            };

            window.addEventListener("mousemove", onMouseMove);
            window.addEventListener("mouseup", onMouseUp);
        },
        [colWidths, rowHeights, colIndexes, rowIndexes, applyGridTemplate, positionHandle]
    );

    useEffect(() => {
        applyGridTemplate(visibleColWidths, visibleRowHeights);
        colHandleRefs.current.forEach((el, i) => positionHandle(el, "col", i, visibleColWidths, visibleRowHeights));
        rowHandleRefs.current.forEach((el, i) => positionHandle(el, "row", i, visibleColWidths, visibleRowHeights));
    }, [visibleColWidths, visibleRowHeights, applyGridTemplate, positionHandle]);

    useEffect(() => {
        setColWidths(ctColWidths || []);
        setRowHeights(ctRowHeights || []);
    }, [ctColWidths, ctRowHeights])

    return (
        <div className="hcr-crosstab-view-mode-container">
            <div style={{ position: "relative", paddingBottom: 8 }}>
                <div
                    ref={gridRef}
                    style={{
                        display: "grid",
                        gridTemplateColumns: visibleColWidths.map((w) => `${w}px`).join(" "),
                        gridTemplateRows: visibleRowHeights.map((h) => `${h}px`).join(" "),
                        width: totalWidth,
                        height: totalHeight,
                        boxSizing: "content-box",
                        border: "1px solid #b9c7d8"
                    }}
                >
                    {cells.map((cell) => {
                        const cellStyles = getCrosstabApplyStyles({ styles: tableStyles, crosstabId: CTId, cell })
                        return (
                            <CrosstabEditableCell
                                cell={cell}
                                gridColumn={`${cell.col[0]} / span ${cell.col[1] - cell.col[0]}`}
                                gridRow={`${cell.row[0]} / span ${cell.row[1] - cell.row[0]}`}
                                componentId={CTId}
                                cellStyles={{
                                    ...borderRight,
                                    ...cellStyles,
                                }}
                                nodes={nodes}
                                {...editableCellProps}
                                crosstabData={data}
                                mode={mode}
                            />
                        )
                    })}
                </div>

                {editable && colIndexes.map((originalIndex, i) => (
                    <div
                        key={`col-handle-${originalIndex}`}
                        ref={(el) => (colHandleRefs.current[i] = el)}
                        onMouseDown={(e) => onMouseDown("col", originalIndex, e)}
                        style={{
                            position: "absolute",
                            top: 0,
                            width: 6,
                            height: totalHeight,
                            cursor: "col-resize",
                            zIndex: 5,
                        }}
                        onMouseEnter={(e) => (e.currentTarget.style.background = "rgba(11,99,206,0.35)")}
                        onMouseLeave={(e) => (e.currentTarget.style.background = "transparent")}
                    />
                ))}

                {editable && rowIndexes.map((originalIndex, i) => (
                    <div
                        key={`row-handle-${originalIndex}`}
                        ref={(el) => (rowHandleRefs.current[i] = el)}
                        onMouseDown={(e) => onMouseDown("row", originalIndex, e)}
                        style={{
                            position: "absolute",
                            left: 0,
                            height: 6,
                            width: totalWidth,
                            cursor: "row-resize",
                            zIndex: 5,
                        }}
                        onMouseEnter={(e) => (e.currentTarget.style.background = "rgba(11,99,206,0.35)")}
                        onMouseLeave={(e) => (e.currentTarget.style.background = "transparent")}
                    />
                ))}
            </div>


        </div>
    )
}

export default CrosstabLayout