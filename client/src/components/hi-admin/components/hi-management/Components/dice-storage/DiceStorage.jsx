import { useState } from "react";
import { EditOutlined, TableOutlined, SyncOutlined } from '@ant-design/icons';
import { Row, Col, Switch as Toggler, Skeleton, Button, Space, Tabs, Tooltip } from "antd";
import { HIFileBrowser } from "../../../../../hi-fileBrowser/hi-fileBrowser";
import { useDispatch, useSelector } from "react-redux";
import { fileBrowserActions } from "../../../../../../redux/actions";
import metadataRequests from "../../../../../../base/requests/metadata.requests";
// import notify from "../../../../../hi-notifications/notify";
import { diceStorageMetadataDumpList, saveDiceStorageMetadataFileDetails, setDiceStorageTableData, setDiceStorageTabName } from "../../../../../../redux/actions/admin.actions";
import TableContent from "./TableContent";
import { CustomIcon } from "../../../../../common/custom-icons/CustomIcon";
import { v4 as uuidv4 } from "uuid";
import { useEffect } from "react";
import admin from "../../../../../../base/requests/admin.request";
import moment from "moment";
import { handleDumpRefreshList } from "./helperMethods";

const tabList = ["Cube", "Metadata"];

export function DiceStorage({ handleAbort, apiRef }) {
    const dispatch = useDispatch();
    const tabName = useSelector((store) => store.admin.diceStorage.tabName);
    const diceStorageTableData = useSelector((store) => store.admin.diceStorage.diceStorageTableData);
    // const diceStorage = useSelector((store) => store.admin.diceStorage);
    // const cubeState = useSelector((store) => store.admin.cubeState);
    // const { metadataDetails, tabName, diceStorageTableData } = diceStorage;
    // const { fileName, path } = metadataDetails;
    // const { cube: cubeTableData, metadata: metadataTableData } = diceStorageTableData;
    const managementAdvancedData = useSelector((store) => store.admin.managementAdvancedData);
    const isGlobalFbEnabled = useSelector(state => state.fileBrowser.globalFbEnabled); // admin(dispatch)
    const { fetchMetadataDumpList } = admin(dispatch);
    const [loading, setLoading] = useState(true);


    useEffect(() => {
        apiRef.current = fetchMetadataDumpList({}, 'adhoc/metadata/listMetadataDump', (res) => {
            dispatch(diceStorageMetadataDumpList({ key: 'metadata', value: res.data }));
            setLoading(false);
        }, (err) => {
            setLoading(false);
            // console.log(err);
        })
    }, [])

    // console.log(diceStorageTableData, 'dice-sto')
    return (
        <Row >
            {
                managementAdvancedData?.computation ? (
                    <Col span={24} >
                        <div className="dice-storage-layout">
                            <Tabs data-testid="dice-storage-tabs" className="dice-storage-tabs" defaultActiveKey="1" size='small' onChange={(val) => { dispatch(setDiceStorageTabName(val)) }}>
                                <Tabs.TabPane tab="Metadata" key="metadata">
                                    <TableContent loading={loading} setLoading={setLoading} handleAbort={handleAbort} apiRef={apiRef} tabName={tabName} diceStorageTableData={diceStorageTableData} />
                                </Tabs.TabPane>
                                <Tabs.TabPane tab="Cube" key="cube">
                                    <TableContent loading={loading} setLoading={setLoading} handleAbort={handleAbort} apiRef={apiRef} tabName={tabName} diceStorageTableData={diceStorageTableData} />
                                </Tabs.TabPane>
                            </Tabs>
                            {/* <div className="cube-header"> */}
                            {/* <Space> */}
                            <div className="fb-btn">
                                <Space>
                                    <Button type="primary" onClick={() => {
                                        dispatch(fileBrowserActions.setShowFileBrowser(true));
                                        // dispatch(fileBrowserActions.setGlobalFbVisibility(false));
                                        // setFilebrowserFor(fbFor)
                                    }}>Select {tabName[0].toUpperCase() + tabName.slice(1)}</Button>
                                    <Tooltip title="Refresh">
                                        <Button data-testid="dice-storage-refresh" icon={<SyncOutlined />} onClick={() => { setLoading(true); handleDumpRefreshList({ dispatch, fetchMetadataDumpList, setLoading, apiRef }) }} />
                                    </Tooltip>
                                </Space>
                            </div>

                            {/* </Space> */}
                        </div>
                        {!isGlobalFbEnabled && <HIFileBrowser
                            extensionOptions={[tabName]}
                            contextMenuOptions={{
                                append: true,
                                options: [
                                    {
                                        // merge: true,
                                        icon: tabName === 'metadata' ? <TableOutlined /> : <CustomIcon name="Cube" />,
                                        id: 'use',
                                        name: `Use ${tabName}`,
                                        types: ["file"],
                                        extensions: [tabName],
                                        disabled: false,
                                        callback: (rec) => {
                                            // console.log(rec, 'use');
                                            let table = [...diceStorageTableData[tabName]], obj = {};
                                            if (tabName === 'metadata') {
                                                obj = { 'Metadata Title': rec.title };
                                            } else {
                                                obj = { 'Cube Title': rec.title };
                                            }
                                            Object.assign(obj, { fileDetails: rec, 'Dump Status': 'In-Complete', 'Dump Type': '--/--', 'Last Updated': moment(new Date(JSON.parse(rec.lastModified))).format('dddd, MMMM Do, YYYY, h:mm:ss a') })
                                            obj.key = uuidv4();
                                            table.push(obj);
                                            dispatch(setDiceStorageTableData({ key: tabName, value: table }));
                                            // let fileDtls = rec.path.split('/');
                                            // dispatch(saveDiceStorageMetadataFileDetails({
                                            //     path: fileDtls[0],
                                            //     fileName: fileDtls[1],
                                            // }))
                                        },
                                    }
                                ],
                            }}
                        />}

                    </Col>
                ) : (
                    <Col span={24} data-testid="dice-storage-text" className="alert-info">
                        DICE storage requires DICE Engine to be in enabled state.
                    </Col>
                )
            }
        </Row >
    );
}