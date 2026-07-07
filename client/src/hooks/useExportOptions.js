import { Tooltip } from 'antd';
import { useSelector } from 'react-redux';

const useExportOptions = () => {
    const applicationSettings = useSelector((state) => state.app.applicationSettingsData) || {};
    const { settings = {} } = applicationSettings || {};
    const { exportType = {} } = settings || {};
    const { HReport = {}, HCR = {}, Efwdd = {} } = exportType || {};

    const getOptions = (obj, cb = () => { }) => {
        const options = []
        for (let op in obj) {
            let ob = obj[op]
            options.push({
                dropdown: true,
                title: ob.name,
                icon: null,
                tooltip: ob.name,
                className: "report-viewer-sub-menu-export-item",
                callback: () => cb(ob.format),
                value: ob.format
            })
        }
        return options
    }

    const getModuleLevelOptions = ({ obj, key, exportTypes = [], shortcutsMap = {} }, cb = () => { }) => {
        const options = []
        for (let op in obj) {
            let ob = obj[op]
            let format = ob.format
            let option = {
                name: ob.name,
                key: ob.name,
                icon: null,
                tooltip: ob.name,
                className: "report-viewer-sub-menu-export-item",
                callBack: () => cb(format)
            }
            if (exportTypes.includes("all")) {
                options.push(option)
            } else {
                if (exportTypes.includes(format)) {
                    if (["hreport"].includes(key)) {
                        option.scText = shortcutsMap[format]
                        option.scLocation = "HR EX"
                    }
                    if (["designer"].includes(key)) {
                        delete option.callBack
                        delete option.tooltip
                        option.label = <Tooltip title={ob.name}>{ob.name}</Tooltip>
                        option.onClick = () => cb(format)
                    }
                    options.push(option)
                }
            }
        }
        return options
    }

    function prepareOptions(key, callback = () => { }) {
        switch (key) {
            case "hreport":
                return getOptions(HReport, callback);
            case "hcr":
                return getOptions(HCR, callback);
            case "designer":
                return getOptions(Efwdd, callback);
            default:
                return {};
        }
    }

    function prepareModuleLevelOptions(key, callback = () => { }) {
        switch (key) {
            case "hreport":
                return getModuleLevelOptions({
                    obj: HReport,
                    key: "hreport",
                    exportTypes: ["csv", "xlsx"],
                    shortcutsMap: { csv: "1", xlsx: "2" }
                }, callback);
            case "hcr":
                return getModuleLevelOptions({
                    obj: HCR,
                    key: "hcr",
                    exportTypes: ["all"]
                }, callback);
            case "designer":
                return getModuleLevelOptions({
                    obj: HCR,
                    key: "designer",
                    exportTypes: ["csv", "xls", "pdf"]
                }, callback);
            default:
                return {};
        }
    }


    function getEmailExportOptions() {
        return {
            hr: getOptions(HReport).map(({ title, value }) => ({ title, value })) || [],
            efwdd: getOptions(Efwdd).map(({ title, value }) => ({ title, value })) || [],
            hcr: getOptions(HCR).map(({ title, value }) => ({ title, value })) || []
        }
    }

    return {
        hreport: HReport,
        hcr: HCR,
        designer: Efwdd,
        getMenuOptions: prepareOptions,
        getDropdownOptions: prepareModuleLevelOptions,
        getEmailExportOptions
    }
}

export default useExportOptions