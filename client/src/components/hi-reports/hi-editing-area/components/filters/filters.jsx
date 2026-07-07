import { useEffect, useRef } from "react";
import { useSelector } from "react-redux";
import { v4 as uuidv4 } from "uuid";
import { useDrop } from 'react-dnd';
import { PageHeader, Button } from 'antd';
import { useDispatch } from "react-redux";
import Filter from "./filter";
import { changeFilterSearch, changeFilterValue, createFilter, deleteFilter, toggleFilter, updateCustomCondition, updateFilter } from "../../../../../redux/actions/hreport.actions"
import { generateReport } from "../../../utils/base";
import ShortCutText from "../../../../common/hi-shortcuts/hi-shortcuts";
import { isMatchingShortcut, resetShortcuts } from "../../../utils/utilities";
import { addFilterValue, fetchFilterValues } from "../../../../../utils/filter-utils";
import { getFieldDisplayName } from "../../../../../utils/utilities";
import { filter } from "lodash";


const Filters = props => {
    let { reportId, dashboardFilter } = props
    const dispatch = useDispatch()
    const applyRef = useRef()
    const apiRef = useRef(null);
    const keysPressed = useSelector(store => store.app.keysPressed)
    const currentSCLocation = useSelector(store => store.app.currentSCLocation)
    const activeReport = useSelector(state => {
        let activeReport = state.hreport.present.reports.find(report => report.id === reportId)
        return activeReport || {}
    })
    const { user = {} } = useSelector((state) => state.app.applicationSettingsData.userData);
    const { filters, mode, metadata, dateFunctions, databaseFunctions, fields, reportData = {}, properties: initialProperties } = activeReport || {}
    let filtersIds = filters.map(filter => filter.uid)
    let isDashboardFilterDeleted = dashboardFilter && !filtersIds.includes(dashboardFilter.uid)

    const [collectedProps, drop] = useDrop(() => ({
        accept: ["canvas_field", "metadata_field"],
        drop: item => handleDrop(item, collectedProps),
    }))
    let reportProperties = initialProperties
    if (reportData?.properties) {
        reportProperties = reportData.properties;
    }
    const filterProperties = reportProperties?.filter || {}

    useEffect(() => {
        if (currentSCLocation === 'HR FIL') {
            if (isMatchingShortcut(keysPressed, ['f', 'a'])) {
                if (applyRef.current) {
                    applyRef.current.click()
                    resetShortcuts(dispatch)
                }
            }
        }
    }, [keysPressed])

    const handleDrop = item => {
        if (item?.field?.custom_frontend_field) return;
        if (item.type === "metadata_field") {
            dispatch(createFilter({ ...item.field, from: "metadata" }))
        } else {
            dispatch(createFilter(item.field))
        }
    }

    const applyChanges = () => {
        // dispatch(updateReportLayout({ pane: "toolsAreaShelf" }));
        generateReport({ ...activeReport, user }, dispatch, props.getApi);
        // generateReport({ ...activeReport }, dispatch,props.getApi);
    }

    const onChange = ({ value, filter }) => {
        dispatch(changeFilterValue({ value, uid: filter.uid, reportId, mode }));
        if ((dashboardFilter || mode === "open") && typeof props.changeDashboardFilter === "function") {
            let values = addFilterValue(filter, value).values;
            props.changeDashboardFilter(getFieldDisplayName(filter), values);
        }
    };

    const handleToggle = ({ filter }) => {
        dispatch(toggleFilter({ uid: filter.uid, reportId }));
    };

    const removeFilter = ({ filter }) => {
        dispatch(deleteFilter({ uid: filter.uid }));
    };

    const handleSearch = ({ data, filter }) => {
        dispatch(changeFilterSearch({ ...data, uid: filter.uid, reportId }));
    };

    const loadValues = ({ debouncedSearchTerm, filter }) => {
        fetchFilterValues({
            filter, filters, debouncedSearchTerm, dateFunctions, metadata, reportId, fields, databaseFunctions
        }, dispatch, apiInstance => {
            apiRef.current = apiInstance;
        });
    };

    const handleCustomCondition = ({ value, filter }) => {
        dispatch(updateCustomCondition({ uid: filter.uid, customCondition: value }));
    };

    const handleQuotesToggle = ({ filter }) => {
        dispatch(updateFilter({ ...filter, encloseInQuotes: !filter.encloseInQuotes, reportId }));
    };

    return (
        <div ref={drop} className="filter-area" data-testid={`${reportId}-filters-pane`} >
            {!dashboardFilter && <PageHeader
                ghost={false}
                title="Filters"
                className="filters-heading"
                extra={[
                    <ShortCutText scLocation="HR FIL" text="A" menuItem={true}>
                        <Button ref={applyRef} key="1" type="primary" onClick={applyChanges} data-testid="filters-apply-btn" >
                            Apply
                        </Button>
                    </ShortCutText>,
                ]}
            >
            </PageHeader>}
            {isDashboardFilterDeleted && <div>{dashboardFilter.columnName} Filter is not available </div>}
            {filters && filters.filter(filter => {
                if (mode === "open" && filter.hideInViewMode) return false;
                if (mode === "open" && filter.drillDownId) return false;
                if (!dashboardFilter) {
                    return true
                } else if (dashboardFilter?.uid === filter.uid) {
                    return true
                }
            }).map((filter) => {
                return <Filter
                    key={filter.uid}
                    filter={filter}
                    reportId={reportId}
                    dashboardFilter={dashboardFilter}
                    allFilters={props.allFilters}
                    changeDashboardFilter={props.changeDashboardFilter}
                    handleQuotesToggle={handleQuotesToggle}
                    handleCustomCondition={handleCustomCondition}
                    loadValues={loadValues}
                    handleSearch={handleSearch}
                    removeFilter={removeFilter}
                    handleToggle={handleToggle}
                    onChange={onChange}
                    apiRef={apiRef}
                    filters={filters}
                    mode={mode}
                    metadata={metadata}
                    dateFunctions={dateFunctions}
                    databaseFunctions={databaseFunctions}
                    isFilterComponent={props.isFilterComponent}
                    filterProperties={filterProperties}
                />
            })}
        </div>
    )
}

export default Filters