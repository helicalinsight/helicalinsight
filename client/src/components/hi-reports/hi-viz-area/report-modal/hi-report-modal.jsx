import { ExportOutlined, InfoCircleOutlined } from '@ant-design/icons'
import { Menu, Modal, Tooltip } from 'antd'
import useExportOptions from '../../../../hooks/useExportOptions'
import "./hi-report-modal.scss"

const HIReportModal = (props = {}) => {
    const { open,
        onClose = () => { },
        children = null,
        info = "This is preview window. Interactivity will not work on this.",
        handleExport = () => { },
        handlePrintExport = () => { },
        reportId,
        dispatch,
        report = {},
        downloadingInfoModal = null
    } = props || {}
    const { reportInfo = {} } = report;
    let activeParentReport = ''
    dispatch((_, getState) => {
        activeParentReport = getState().hreport.present.reports.find(item => item.activeDrillthroughId === reportId) || {};
    });
    const { getMenuOptions } = useExportOptions();

    let menuItems = [
        {
            key: "exportMenu",
            label: "",
            icon: <ExportOutlined size={36} />,
            children: getMenuOptions(
                "hreport",
                (format) => ["pdf", "png", "pptx"].includes(format) ?
                    handlePrintExport(format, activeParentReport.id)
                    : handleExport(reportId, format)).
                map(({ title, callback, icon, className }) => ({ label: title, icon: icon, onClick: callback, className: className }))
        },
    ]


    const title = (
        <div className='ant-modal-title-container display-flex align-items-center justify-content-space-between'>
            <div className='display-flex align-items-center'>
                <div className='display-flex align-items-center flex-direction-columns report-name-container'>
                    <span>Drillthrough Report</span>
                    <span>{reportInfo?.reportName}</span>
                </div>
                <Tooltip title={info}>
                    <InfoCircleOutlined style={{ marginLeft: 8, cursor: 'pointer' }} />
                </Tooltip>
            </div>
            <div>
                <Menu
                    defaultSelectedKeys={[]}
                    selectedKeys={[]}
                    selectable={false}
                    mode="vertical"
                    items={menuItems}
                />
            </div>
        </div>
    )

    return (
        <Modal
            title={open ? title : null}
            centered
            visible={open}
            onCancel={onClose}
            width={"95%"}
            footer={null}
            className='hi-report-modal'
        >
            {children}
            {downloadingInfoModal}
        </Modal >
    )
}

export default HIReportModal