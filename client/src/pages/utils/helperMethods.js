const HREPORT_TITLE_EXCLUDED_MODES = [
    "open",
    "dashboard",
    "filter",
    "instant-bi",
    "instant-bi-create",
    "instant-bi-open",
    "dashboard-create",
    "dashboard-instant-filter",
]

export const shouldUpdateHreportDocumentTitle = (mode) =>
    !HREPORT_TITLE_EXCLUDED_MODES.includes(mode)

export const validateReportName = (name) => {
    if(name.length < 3){
        return 'Report name should be atleast 3 characters long'
    }
    if(name.length > 60){
        return 'Report name should not exceed 60 characters'
    }
    if(!(/^\w[\w.\-&#+~]*/i).test(name)){
        return 'Please provide valid Report name'
    }
    return false
}


export const validateDesignerName = (name) => {
    if(name.length < 3){
        return 'Designer name should be atleast 3 characters long'
    }
    if(name.length > 60){
        return 'Designer name should not exceed 60 characters'
    }
    if(!(/^\w[\w.\-&#+~]*/i).test(name)){
        return 'Please provide valid Designer name'
    }
    return false
}