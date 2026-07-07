import { isEqual } from 'lodash-es';
import { useRef } from "react";
import { useDispatch, useSelector } from "react-redux";
import { v4 as uuidv4 } from 'uuid';
import Filter from "../../hi-reports/hi-editing-area/components/filters/filter";
import { hcrParaQueryBasedDropdownList } from "../hcr-constants";
import { getFilterValueForHCR, handleHcrFilterChange, updateHCRFilterValuesList } from "../hcrHelperMethods";

const HcrFilters = ({ setFilters, filters, reportMode }) => {
    const activeReport = useSelector(state => state.cannedReports.present.hcrTabData.panes.find(pane => pane.key === state.cannedReports.present.hcrTabData.activeKey), isEqual) || {};
    const reportId = activeReport.uuid;
    const dispatch = useDispatch()
    const apiRef = useRef(null);

    const handleChangeInFilters = (filter, keyWithValues = {}) => {
        setFilters((prevFilters) => {
            return prevFilters.map((f) => {
                if (f.uid === filter.uid) {
                    for (const key in keyWithValues) {
                        f[key] = keyWithValues[key]
                    }
                }
                return f
            })
        })
    }

    const handleFilterLoadingChange = (filter, value) => {
        handleChangeInFilters(filter, { loading: value })
    }

    const handleToggle = ({ filter }) => {
        handleChangeInFilters(filter, { active: !filter.active, configId: uuidv4() })
    };

    const removeFilter = ({ filter }) => {
        setFilters(prevFilters => prevFilters.filter(f => f.uid !== filter.uid));
    };

    const handleSearch = ({ data, filter }) => { };

    const loadValues = ({ debouncedSearchTerm, filter }) => {
        let reqQuery = filter?.orgPara ?? {}
        const { canvasValues = {} } = reqQuery;
        if (canvasValues?.filterType === hcrParaQueryBasedDropdownList) {
            handleFilterLoadingChange(filter, true)
            apiRef.current = getFilterValueForHCR({
                reqQuery: reqQuery,
                dispatch,
                successCB: (res) => {
                    if (res.data) {
                        updateHCRFilterValuesList(res.data, filter, setFilters);
                    } else {
                        handleFilterLoadingChange(filter, false)
                    }
                },
                errorCB: () => {
                    handleChangeInFilters(filter, { valuesList: [], loading: false })
                }
            });
        }
    };


    const handleCustomCondition = ({ value, filter }) => { };

    const handleQuotesToggle = ({ filter }) => { };

    const handleAbortRequest = (filter) => {
        handleFilterLoadingChange(filter, false)
    }

    return (
        <div className="filter-area hcr-filter-wrapper" >
            {filters.map((filter) => {
                return (filter.show) && <Filter
                    key={filter.uid}
                    filter={filter}
                    reportId={reportId}
                    handleQuotesToggle={handleQuotesToggle}
                    handleCustomCondition={handleCustomCondition}
                    loadValues={loadValues}
                    handleSearch={handleSearch}
                    removeFilter={removeFilter}
                    handleToggle={handleToggle}
                    onChange={({ value, filter }) => { handleHcrFilterChange({ value, filter, setFilters }) }}
                    apiRef={apiRef}
                    filters={filters}
                    mode={reportMode}
                    dateFunctions={{}}
                    setFilters={setFilters}
                    parentComp={'hcr'}
                    onFilterRequestAbort={handleAbortRequest}
                />
            }).filter(Boolean)}
        </div>
    )
}

export default HcrFilters;