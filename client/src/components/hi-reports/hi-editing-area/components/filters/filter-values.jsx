import { Checkbox, Col, List, Radio, Skeleton, Typography } from "antd";
import { isEqual } from "lodash-es";
import { useEffect, useRef, useState } from "react";
import { useSelector } from "react-redux";
import { AutoSizer } from "react-virtualized";
import VList from "react-virtualized/dist/commonjs/List";
import { useWindowSize } from "../../../../../customHooks/useWindowSize";
import { radioButtonConditions } from "../../../../../utils/filter-utils";
import LoadingBar from "../../../../common/components/hi-loading-bar";
import { previousStateInstance } from "../../utils/filter-utils";

const { Text } = Typography;

const isTestMode = process.env.NODE_ENV === "test";

const FilterValues = (props) => {
  const dataIdsRef = useRef();
  const cascadeParentFilterValuesRef = useRef();
  const [height, setHeight] = useState(105);
  const [vListStyles, setVListStyles] = useState({
    background: props?.filterBGColor || "#fff",
  })
  const resizableDivRef = useRef(null);

  const [, offsetHeight] = useWindowSize();
  const { filter, values, reportId, abortFilterValues, mode, isFilterComponent, filterListItemColor, filterListItemFontSize } = props;
  const activeReport = useSelector((state) => {
    let activeReport = state.hreport.present.reports.find(
      (report) => report.id === reportId
    );
    return activeReport || {};
  });
  const { filters, reportData = {} } = activeReport;
  const { loading: hreportLoading } = reportData;
  const { valuesList, condition, loading, configId, cascade, dataType = '' } = filter;
  const isDateFilter = ['dateTime', 'date'].includes(dataType) || filter?.datePart;
  let cascadeFilterDataIds = [];
  let cascadeParentFilterValues = []
  let { isEnabled } = cascade;
  if (isEnabled) {
    let cascadeFilterIds = cascade.filters.map((filter) => filter.uid);
    let cascadeParentFilter = filters.find((filter) => cascadeFilterIds.includes(filter.uid));
    cascadeParentFilterValues = cascadeParentFilter?.values;
    cascadeFilterDataIds = filters
      .filter((filter) => cascadeFilterIds.includes(filter.uid))
      .map((filter) => {
        return filter.dataId;
      })
      .filter((dataId) => !!dataId);
  }

  useEffect(() => {
    if (
      isEnabled &&
      cascadeFilterDataIds.length &&
      (!isEqual(dataIdsRef.current, cascadeFilterDataIds) || !isEqual(cascadeParentFilterValuesRef.current, cascadeParentFilterValues)) &&
      !hreportLoading
    ) {
      if (!isTestMode) {
        let timeout = setTimeout(() => {
          clearTimeout(timeout)
          props.loadValues({ filter });
        }, 100)
      }
      // !isTestMode && props.loadValues();
      dataIdsRef.current = cascadeFilterDataIds;
      cascadeParentFilterValuesRef.current = cascadeParentFilterValues;
    }
  });


  useEffect(() => {
    const instance = previousStateInstance()
    if (!isEqual(instance.restore(configId), configId) && !isTestMode) {
      props.loadValues({ filter });
      instance.save(configId)
    }
  }, [configId]);

  useEffect(() => {
    if (isTestMode || isFilterComponent || !resizableDivRef.current) return;

    const observeNode = resizableDivRef.current;

    const resizeObserver = new ResizeObserver((entries) => {
      for (let entry of entries) {
        const newHeight = entry.contentRect.height;
        if (newHeight) setHeight(newHeight);
      }
    });
    resizeObserver.observe(observeNode);
    return () => {
      resizeObserver.unobserve(observeNode);
    };
  }, [valuesList?.length, offsetHeight, filter, loading]);

  useEffect(() => {
    if (['filter'].includes(mode) && isFilterComponent) { // 7771 changes
      let tempHeight = valuesList?.length * 28;
      if (tempHeight > offsetHeight - 150) {
        tempHeight = offsetHeight - 150
      }
      setHeight(tempHeight)
      setVListStyles({
        zIndex: 99999999,
        background: props?.filterBGColor || "#fff",
      })
    }
  }, [offsetHeight, valuesList?.length])


  if (loading) {
    return (
      <>
        <LoadingBar handleClick={() => abortFilterValues()} />
        <Skeleton />
      </>
    );
  }
  if (!valuesList.length) return null;
  let displayValuesList = valuesList;
  const onChange = (value) => {
    props.onChange({ value, filter });
  };
  const renderItem = ({ index, key, style }) => {
    const item = displayValuesList[index];
    let display = item.display;
    let value = item.value ? item.value : item.display;
    let Comp = Checkbox;
    if (radioButtonConditions.includes(condition)) {
      Comp = Radio;
    }
    return (
      <Col span={24} key={key}>
        <div style={style} data-testid={`filter-value-${display}`}>
          <Comp
            onChange={(e) => onChange(e.target.value)}
            value={value}
            checked={Array.isArray(values) ? values?.some((val) => val == value) : values == value} // added isArray condition 6065 - fix
            // checked={values.includes(value)}
            disabled={values.includes("_all_")}
          >
            <Text style={{ width: "100%", color: filterListItemColor, fontSize: filterListItemFontSize }} ellipsis={true}>
              {display}
            </Text>
          </Comp>
        </div>
      </Col>
    );
  };


  const containerStyles = {
    height,
    resize: !isFilterComponent ? "vertical" : 'none',
    overflow: "auto",
    background: props?.filterBGColor || "#fff",
    paddingRight: !isFilterComponent ? 4 : 0
  }

  return (
    <div style={containerStyles} ref={resizableDivRef}>
      <List>
        <AutoSizer disableHeight>
          {({ width }) => (
            <VList
              rowCount={displayValuesList.length}
              width={width - 5}
              height={height}
              rowHeight={28}
              rowRenderer={renderItem}
              overscanRowCount={0}
              style={vListStyles}
            />
          )}
        </AutoSizer>
      </List>
    </div>
  );
};

export default FilterValues;
