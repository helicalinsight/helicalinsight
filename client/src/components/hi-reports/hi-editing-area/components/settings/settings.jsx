
import { Button, Card, Checkbox, InputNumber, Radio, Row, Space, Table, Tooltip } from 'antd';
import { useEffect, useRef } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { fileBrowserActions } from '../../../../../redux/actions';
import { changeOptions } from "../../../../../redux/actions/hreport.actions";
import HITooltip from "../../../../common/components/hi-tooltip";
import ShortCutText from '../../../../common/hi-shortcuts/hi-shortcuts';
import { isMatchingShortcut, resetShortcuts } from '../../../utils/utilities';

const Settings = ({ openFileBrowser }) => {
    const dispatch = useDispatch()
    const changeMdRef = useRef()
    const keysPressed = useSelector(store => store.app.keysPressed)
    const currentSCLocation = useSelector(store => store.app.currentSCLocation)
    useEffect(() => {
        if (currentSCLocation === 'HR ST') {
            if (isMatchingShortcut(keysPressed, ['t', 'c'])) {
                if (changeMdRef.current) {
                    changeMdRef.current.click()
                    resetShortcuts(dispatch)
                }
            }
        }
    }, [keysPressed])
    const { options, metadata } = useSelector(state => {
        let activeReport = state.hreport.present.reports.find(report => report.active)
        return activeReport
    })
    const { limitBy, sample, prependTableNameToAlias } = options
    const toggleSample = e => {
        if (sample === e) return null
        // dispatch(changeOptions({ sample: e, limitBy, prependTableNameToAlias }))
        dispatch(changeOptions({ sample: e, limitBy: e === 'full' ? 1000 : limitBy, prependTableNameToAlias })) /// fix for 6693
    }
    const onChange = e => {
        dispatch(changeOptions({ sample, limitBy: e, prependTableNameToAlias }))
    }
    const toggleAlias = (e) => {
        dispatch(changeOptions({ sample, limitBy, prependTableNameToAlias: e.target.checked }))
    }
    let { metadataDir, metadataName } = metadata || { metadataDir: "Please Select Metadata", metadataName: "Please Select Metadata" }
    let columns = [
        {
            title: 'key',
            dataIndex: 'key',
            key: 'key',
            ellipsis: {
                showTitle: false,
            },
            render: (text) => (
                <HITooltip title={text}>
                    {text}
                </HITooltip>
            ),
        },
        {
            title: 'value',
            dataIndex: 'value',
            key: 'value',
            ellipsis: {
                showTitle: false,
            },
            render: (text) => (
                <HITooltip title={text}>
                    :{text}
                </HITooltip>
            ),
        }
    ]
    let dataSource = [
        { key: "Location", value: metadataDir },
        { key: "Name", value: metadataName },
    ]
    return (
        <div>
            <Card data-testid="hi-report-settings" size="small" title="Sample Size"  >
                <Row className="sample-row" >

                    <Radio checked={sample === "sample"} onChange={() => toggleSample("sample")} >Sample</Radio>
                </Row>
                {sample === "sample" && <Row className="sample-row" >
                    <InputNumber data-testid="hr-sample-iput" min={0} value={limitBy} onChange={onChange} />
                </Row>}
                <Row className="sample-row" >
                    <Space>
                        <Radio checked={sample === "full"} onChange={() => toggleSample("full")}><Tooltip title="Selecting the 'Full' option for generating reports with large data sets may decrease performance and result in longer loading times.">Full</Tooltip></Radio>
                    </Space>
                </Row>
                <Row className="sample-row" >
                    <Space>
                        <Checkbox onChange={toggleAlias} checked={prependTableNameToAlias} >
                            prepend table names while generating alias
                        </Checkbox>
                    </Space>
                </Row>
            </Card>
            <Card size="small" title="Current metadata file" className="change-metadata" >
                <Row>
                    <Table className={"hr-selected-md-info"} dataSource={dataSource} columns={columns} pagination={false} showHeader={false} />
                    <ShortCutText text="C" scLocation="HR ST">
                        <Button
                            type="primary"
                            ref={changeMdRef}
                            onClick={() => {
                                dispatch(fileBrowserActions.setShowFileBrowser(true))
                                openFileBrowser("metadata", true)
                            }}>
                            Change Metadata
                        </Button>
                    </ShortCutText>
                </Row>
            </Card>
        </div>
    )
}

export default Settings