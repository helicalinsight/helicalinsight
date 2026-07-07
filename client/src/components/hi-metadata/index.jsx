import { Row } from 'antd';
import React, { useEffect, useRef, useState } from 'react';
import { Responsive, WidthProvider } from "react-grid-layout";
import { useDispatch, useSelector, useStore } from "react-redux";
import { HIMetadatSidebar } from '../hi-sidebar/hi-metadataSidebar';
import Editor from './components/editor/index';
import { MetadataSection } from './components/metadata-section';
import './index.scss';
import './metadata-images.scss';
import { fetchGetDataSource } from './utils';
import { isEqual } from 'lodash-es'
import { useWindowSize } from '../../customHooks/useWindowSize';
import { metadataOutsideClicked } from '../../redux/actions';

const ResponsiveGridLayout = WidthProvider(Responsive);
// const ResponsiveGridLayout = Responsive;

// let cancleTimeoutId = '';

function Metadata({ openFB, handleShare, filebrowserFor, setFilebrowserFor, saveType, onEditMetadata, isAlreadyFetched }) {
    const dispatch = useDispatch();
    const store = useStore()
    const getDataSourceServiceStatus = useSelector(state => state.metadata.present.dataFetchedFor.getDatasource, isEqual)
    const activeDataSource = useSelector(state => state.metadata.present.activeDataSource, isEqual) //activeDataSource is the datasource on which create metadata is clicked on in datasource-page
    const metadataToEdit = useSelector(state => state.metadata.present.metadataToEdit, isEqual) // metatada details when edited from Home page
    const mode = useSelector(state => state.metadata.present.mode, isEqual)
    const toggleSidebar = useSelector(state => state.app.toggleSidebar)
    const editFromMDFileBrowser = useSelector(state => state.metadata.present.editMdFromHomeInfo)
    const editorFullView = useSelector(state => state.metadata.present.editorFullView)
    const [, offsetHeight] = useWindowSize()
    const [savedLayout, setSavedLayout] = useState(null);
    const [resizeByArea, setResizeByArea] = useState({
        "sidebar-area": false,
        "editing-area": false,
        "metadata-area": false
    })
    const initialEditResponse = useSelector(state => state.metadata.present.initialEditResponse);

    const apiRef = useRef(null);

    function getApi(apiInstance) {
        apiRef.current = apiInstance;
    }

    function handleAbort(prop = {}) {
        // apiRef.current && (apiRef.current.Notify = {});
        // apiRef.current && (apiRef.current.data = {});
        apiRef.current?.abort(prop);
    }

    React.useEffect(() => {
        apiRef.current = undefined;
    }, [initialEditResponse])

    useEffect(() => { // component did mount - fetching all the information needed
        // cancleTimeoutId = setTimeout(() => {
        if (mode === 'create') { // #4421
            isAlreadyFetched.current = undefined
        }
        if (isAlreadyFetched.current) { // && mode === 'edit'
            return
        }
        if (mode === 'edit') {
            if (!metadataToEdit) {
                const onEdit = d => {
                    isAlreadyFetched.current = true
                    let { path, name } = d
                    path = path.split('/')
                    path.pop()
                    path = path.join('/')
                    if (getDataSourceServiceStatus !== true) {
                        fetchGetDataSource({
                            dispatch,
                            store,
                            mode,
                            location: path,
                            uuid: name,
                            activeDataSource,
                            apiRef,
                            getApi
                        })
                    }
                }
                // if (editMdFromHomeInfo && 'dir' in editMdFromHomeInfo) { //4533 //#4931
                //     onEdit({ path: editMdFromHomeInfo.dir, name: editMdFromHomeInfo.file })
                //     return
                // }
                if (editFromMDFileBrowser && 'dir' in editFromMDFileBrowser) {
                    onEdit({ path: editFromMDFileBrowser.dir, name: editFromMDFileBrowser.file })
                    return
                }
                openFB({
                    fbFor: 'edit',
                    onEdit: d => onEdit(d)
                })
                return
            } else if (metadataToEdit) {
                isAlreadyFetched.current = true
                let { path, name } = metadataToEdit
                path = path.split('/')
                path.pop()
                path = path.join('/')
                if (getDataSourceServiceStatus !== true) {
                    fetchGetDataSource({
                        dispatch,
                        store,
                        mode,
                        location: path,
                        uuid: name,
                        activeDataSource,
                        apiRef,
                        getApi
                    })
                }
            }
        }
        if (getDataSourceServiceStatus !== true && mode !== '') {
            fetchGetDataSource({
                dispatch,
                store,
                activeDataSource,
                getApi
                // mode: 'edit',
            })
        }
        // }, [500])

        // add shortcuts
        return () => {
            //remove shortcuts
            // clearTimeout(cancleTimeoutId);
        }
    }, [editFromMDFileBrowser, metadataToEdit, mode, getDataSourceServiceStatus, isAlreadyFetched])
    // useEffect(() => {

    // }, [getDataSourceServiceStatus])

    useEffect(() => {
        savedLayout && savedLayout.prev && savedLayout.current.forEach(ele => {
            const prevRow = savedLayout.prev.find(j => j.i === ele.i);
            if (JSON.stringify(ele) !== JSON.stringify(prevRow)) {
                const obj = { ...resizeByArea }
                if (!obj[ele["i"]]) {
                    obj[ele["i"]] = true
                    setResizeByArea(obj)
                }
            }
        })
    }, [savedLayout])

    let calculatedH = 0
    try {
        calculatedH = (offsetHeight / 12) || 52
    }
    catch (e) {
        calculatedH = 52
    }
    let storedLayouts = {
        xxs: [
            { i: "sidebar-area", w: 100, h: calculatedH, x: 0, y: 0 },
            { i: "metadata-area", w: 100, h: calculatedH, x: 0, y: calculatedH },
            { i: "editing-area", w: 100, h: calculatedH, x: 0, y: 2 * calculatedH }
        ],
        xs: [
            { i: "sidebar-area", w: 50, h: calculatedH, x: 0, y: 0 },
            { i: "metadata-area", w: 50, h: calculatedH, x: 50, y: 0 },
            { i: "editing-area", w: 100, h: calculatedH, x: 0, y: calculatedH }
        ],
        sm: [
            { i: "sidebar-area", w: 50, h: calculatedH, x: 0, y: 0 },
            { i: "metadata-area", w: 50, h: calculatedH, x: 50, y: 0 },
            { i: "editing-area", w: 100, h: calculatedH, x: 0, y: calculatedH }
        ],
        md: [
            { i: "sidebar-area", w: 17, h: calculatedH, x: 0, y: 0 },
            { i: "metadata-area", w: 20, h: calculatedH, x: 17, y: 0 },
            { i: "editing-area", w: 63, h: calculatedH, x: 37, y: 0 }
        ],
        lg: [
            { i: "sidebar-area", w: 17, h: calculatedH, x: 0, y: 0 },
            { i: "metadata-area", w: 20, h: calculatedH, x: 17, y: 0 },
            { i: "editing-area", w: 63, h: calculatedH, x: 37, y: 0 }
        ],
    };
    if (toggleSidebar) { //4529 :: dont show sidebar here
        storedLayouts = {
            xxs: [
                { i: "sidebar-area", w: 0, h: 0, x: 0, y: 0 },
                { i: "metadata-area", w: 100, h: calculatedH, x: 0, y: 0 },
                { i: "editing-area", w: 100, h: calculatedH, x: 0, y: calculatedH }
            ],
            xs: [
                { i: "sidebar-area", w: 0, h: 0, x: 0, y: 0 },
                { i: "metadata-area", w: 100, h: calculatedH, x: 0, y: 0 },
                { i: "editing-area", w: 100, h: calculatedH, x: 0, y: calculatedH }
            ],
            sm: [
                { i: "sidebar-area", w: 0, h: 0, x: 0, y: 0 },
                { i: "metadata-area", w: 100, h: calculatedH, x: 0, y: 0 },
                { i: "editing-area", w: 100, h: calculatedH, x: 0, y: calculatedH }
            ],
            md: [
                { i: "sidebar-area", w: 0, h: 0, x: 0, y: 0 },
                { i: "metadata-area", w: 30, h: calculatedH, x: 0, y: 0 },
                { i: "editing-area", w: 70, h: calculatedH, x: 30, y: 0 }
            ],
            lg: [
                { i: "sidebar-area", w: 0, h: 0, x: 0, y: 0 },
                { i: "metadata-area", w: 30, h: calculatedH, x: 0, y: 0 },
                { i: "editing-area", w: 70, h: calculatedH, x: 30, y: 0 }
            ],
        };
    }
    if (editorFullView) {
        storedLayouts = {
            xxs: [
                // { i: "sidebar-area", w: 0, h: 0, x: 0, y: 0 },
                // { i: "metadata-area", w: 0, h: 0, x: 0, y: 0 },
                { i: "editing-area", w: 100, h: calculatedH, x: 0, y: 0 }
            ],
            xs: [
                // { i: "sidebar-area", w: 0, h: 0, x: 0, y: 0 },
                // { i: "metadata-area", w: 0, h: 0, x: 0, y: 0 },
                { i: "editing-area", w: 100, h: calculatedH, x: 0, y: 0 }
            ],
            sm: [
                { i: "sidebar-area", w: 1, h: 0, x: 0, y: 0 },
                { i: "metadata-area", w: 1, h: 0, x: 0, y: 0 },
                { i: "editing-area", w: 100, h: calculatedH, x: 0, y: 0 }
            ],
            md: [
                { i: "sidebar-area", w: 1, h: 0, x: 0, y: 0 },
                { i: "metadata-area", w: 1, h: 0, x: 0, y: 0 },
                { i: "editing-area", w: 100, h: calculatedH, x: 0, y: 0 }
            ],
            lg: [
                // { i: "sidebar-area", w: 0, h: 0, x: 0, y: 0 },
                // { i: "metadata-area", w: 0, h: 0, x: 0, y: 0 },
                { i: "editing-area", w: 100, h: calculatedH, x: 0, y: 0 }
            ],
        };
    }
    let layoutProps = {
        cols: { lg: 100, md: 100, sm: 100, xs: 100, xxs: 100 },
        // width: 500,
        className: "layout",
        // rowHeight: 100,
        rowHeight: 1,
        // autoSize:true,
        colWidth: 1,
        isDraggable: false,
        isResizable: true,
        // compactType: null, 
        preventCollision: false,
        measureBeforeMount: true,
        breakpoints: { lg: 1200, md: 996, sm: 768, xs: 480, xxs: 0 },
        compactType: "vertical",
        margin: [0, 10],
        // useCSSTransforms:false
    }
    return (
        <Row className="height100percent" onClick={() => { dispatch(metadataOutsideClicked(true)) }}>
            <ResponsiveGridLayout
                {...layoutProps}
                layouts={storedLayouts}
                onLayoutChange={(layout) => {
                    if (!savedLayout)
                        setSavedLayout({ prev: undefined, current: layout });
                }}
                onResizeStop={(layout) => {
                    if (savedLayout && savedLayout.current) {
                        setSavedLayout({ prev: savedLayout.current, current: layout });
                    }
                }}
                onBreakpointChange={() => { }}
                className="metadata-layout"
            >
                <div
                    key={"sidebar-area"}
                    className={`${toggleSidebar
                        ? "display-none"
                        : "background-hi-light-blue b1pxddd metadata-sidebar"
                        } ${editorFullView ? "display-none" : ""} ${resizeByArea["sidebar-area"] ? '' : 'grid-height-99'}`}
                >
                    <HIMetadatSidebar handleAbort={handleAbort} filebrowserFor={filebrowserFor} setFilebrowserFor={setFilebrowserFor} saveType={saveType} openFB={openFB} onEditMetadata={onEditMetadata} />
                </div>
                <div
                    key={"metadata-area"}
                    className={`b1pxsr metadata-section ${editorFullView ? "display-none" : ""
                        } ${resizeByArea["metadata-area"] ? '' : 'grid-height-99'}`}
                >
                    <MetadataSection handleAbort={handleAbort} />
                </div>
                <div
                    key={"editing-area"}
                    className={`b1pxsr metadata-editor-section metadata-editor ${resizeByArea["editing-area"] ? '' : 'grid-height-99'}`}
                >
                    <Editor handleShare={handleShare} />
                </div>
            </ResponsiveGridLayout>
        </Row>
    );
}

//this function is not required
function arePropsEqual() {
    return false
}
const HIMetadata = React.memo(Metadata, arePropsEqual);

export { HIMetadata };