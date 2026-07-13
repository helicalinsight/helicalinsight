import React from 'react'
import SidebarPanel from '../components/sidebarPanel';
import HCRCrossTab from '../../hcrCrossTab/hcrCrossTab';

const HCRCrosstabEditMode = (props = {}) => {
    const { data = {}, lastSelectedNodeRef } = props || {};

    const {
        width,
        height,
        id,
    } = data || {};
    const widthConstant = 40, heightConstant = 50;

    const handleOutsideClick = (e) => {
        e.preventDefault();
    }


    const sidebarPanelProps = {
        open: false
    }

    return (
        <div className='hcr-table-edit-container' style={{ width: width + widthConstant, height: height + heightConstant }}>
            <div className='table-wrapper' style={{ width, height }} onClick={handleOutsideClick}>
                <div style={{ width: 'max-content' }}>
                    {/* crosstab in edit mode */}
                </div>
            </div>
            <div className='flowchart-editor-panel-body hcr-side-bar-wrapper'>
                <SidebarPanel {...sidebarPanelProps} />
            </div>
        </div >
    )
}

export default HCRCrosstabEditMode