import React from 'react'
import { useSelector } from 'react-redux';
import { HIDashboardWrapper } from '../../hi-dashboard/components/hi-dashboard-wrapper';

const DashboardInstantFilter = (props = {}) => {
    const { gridItem = {} } = props || {}
    const variables = useSelector(
        (state) => state.dashboard.present.dashboardVariables
    );

    if (!gridItem?.reportInfo?.hreportId) return null;

    const reportInfo = {
        ...(gridItem.reportInfo || {}),
        file: {
            ...(gridItem.reportInfo?.file || {}),
            reportId: gridItem?.reportInfo?.hreportId
        },
        mode: "dashboard-instant-filter"
    }

    return (
        <HIDashboardWrapper
            type={gridItem.reportInfo?.extension}
            parameter={gridItem.parameter}
            id={gridItem.id}
            listeners={gridItem.listeners}
            reportInfo={reportInfo}
            dashboardVariables={variables}
        />
    )
}

export default DashboardInstantFilter