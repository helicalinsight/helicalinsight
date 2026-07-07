import {
    EyeOutlined, InfoCircleOutlined,
    LinkOutlined, LoadingOutlined, SecurityScanOutlined
} from '@ant-design/icons';
import { Empty, Layout, Menu, Spin } from 'antd';
import React, { useEffect } from 'react';
// import Info from './info'
import { isEqual } from 'lodash-es';
import { useDispatch, useSelector } from "react-redux";
import { metadataActions } from "../../../../redux/actions";
import TutorialInfo from '../../../common/hi-tutorial';
import Security from './security';
import { updateJoinsWithChangedDS, updateTablesJoinsWithChangedDS } from '../../../../redux/actions/metadata.actions';
// const Security = React.lazy(() => import('./security/index'))
const Info = React.lazy(() => import('./info/index'));
const Joins = React.lazy(() => import('./joins/index'));
const Views = React.lazy(() => import('./views/index'))
const SaveActions = React.lazy(() => import('./saveActions/index'))

const { Sider } = Layout;
const antIcon = <LoadingOutlined style={{ fontSize: 24 }} spin />;
function Editor({ handleShare }) {
    const activeEditorTab = useSelector(state => state.metadata.present.activeEditorTab)
    const dataSource = useSelector(state => state.metadata.present.dataSource)
    // const activeEditorTab = 'saveActions'
    const dataSourcesAddedToMetadata = useSelector(state => state.metadata.present.dataSourcesAddedToMetadata, isEqual)
    const mode = useSelector(state => state.metadata.present.mode)
    const dispatch = useDispatch()
    const isDisabled = mode === 'edit' ? false : !!!(dataSourcesAddedToMetadata.length)

    useEffect(() => {
        if (dataSource.length === 1) {
            // dispatch(updateJoinsWithChangedDS());
            // dispatch(updateTablesJoinsWithChangedDS());
        }
    }, [dataSource])

    // React.useEffect(() => {
    // dispatch(setSelectedTableOrColumnKey({}));
    // }, [activeEditorTab])
    return (
        <div className='height100percent'>
            <Layout className='height100percent'>
                <Sider
                    collapsible
                    collapsed={true}
                    trigger={null}
                    theme={'light'}
                    className='metadata-editor-sider b1pxddd'
                >
                    <Menu
                        mode="inline"
                        onClick={e => {
                            dispatch(metadataActions.updateActiveEditorTab(e.key))
                        }}
                        // defaultSelectedKeys={isDisabled ? [] : [activeEditorTab]}
                        // defaultSelectedKeys={[activeEditorTab]}
                        selectedKeys={[activeEditorTab]}

                    >
                        <Menu.Item key="info"
                            // label={<div>
                            //     <div><InfoCircleOutlined /></div>
                            //     <div>test</div>
                            // </div>}
                            disabled={isDisabled}
                            icon={
                                <TutorialInfo elementKey='hi-metadata-editor-info'>
                                    <div>
                                        <div><InfoCircleOutlined /></div>
                                        <div className='hi-metadata-sider-menu-title sider-menu-info-icon'>Info</div>
                                    </div>
                                </TutorialInfo>
                            }
                        >
                        </Menu.Item>
                        <Menu.Item key="joins" disabled={isDisabled} icon={
                            <TutorialInfo elementKey='hi-metadata-editor-joins'>
                                <div>
                                    <div><LinkOutlined /></div>
                                    <div className='hi-metadata-sider-menu-title sider-menu-joins-icon'>Joins</div>
                                </div>
                            </TutorialInfo>
                        }>
                        </Menu.Item>
                        <Menu.Item key="saveActions" disabled={isDisabled} className='hi-metadata-actions' icon={
                            <TutorialInfo elementKey='hi-metadata-editor-joins'>
                                <div>
                                    <div><LinkOutlined /></div>
                                    <div className='hi-metadata-sider-menu-title sider-menu-joins-icon'>Joins</div>
                                </div>
                            </TutorialInfo>
                        }>
                        </Menu.Item>
                        <Menu.Item key="views" disabled={isDisabled} icon={
                            <TutorialInfo elementKey='hi-metadata-editor-views'>
                                <div>
                                    <div><EyeOutlined /></div>
                                    <div className='hi-metadata-sider-menu-title sider-menu-joins-icon'>Views</div>
                                </div>
                            </TutorialInfo>
                        }>
                        </Menu.Item>
                        <Menu.Item key="security" disabled={isDisabled} icon={
                            <TutorialInfo elementKey='hi-metadata-editor-security'>
                                <div>
                                    <div><SecurityScanOutlined /></div>
                                    <div className='hi-metadata-sider-menu-title sider-menu-security-icon'>Security</div>
                                </div>
                            </TutorialInfo>
                        }>
                        </Menu.Item>
                    </Menu>
                </Sider>
                <Layout className='hi-p10 b1pxddd-l0 overflow-y-auto edit-section'>
                    <React.Suspense fallback={<Empty className='edit-section-lazy-loading-spinner' image={null} description={null}>
                        <Spin indicator={antIcon} />;
                    </Empty>}>
                        {
                            {
                                'info': <Info />,
                                'joins': <Joins />,
                                'views': <Views />,
                                'security': <Security />,
                                'saveActions': <SaveActions handleShare={handleShare} />
                            }[activeEditorTab] || <div>in other tabs</div>
                        }
                    </React.Suspense>

                </Layout>
            </Layout>
        </div>
    )
}

export default Editor
