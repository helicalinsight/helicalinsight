import { isEmpty } from 'lodash';
import { Polyline, Tooltip } from 'react-leaflet';
import { v4 as uuidv4 } from 'uuid';
import { setMenuData } from '../../../../../redux/actions/hreport.actions';
import { getFieldAliasName } from '../../../../../utils/utilities';
import { getEachFieldColor, getFieldName, getFiledValueColor, getHTMLColorFormat } from '../../../hi-editing-area/utils/property-utils';
import { constructGeoJsonData } from '../../utils/utillities';
import { calculateScaleValues, generateColorRanges, getColorByValue, getOSMChartTooltip } from './utilities';
import randomColor from 'randomcolor';

const OSMLineMapLayer = (props = {}) => {
    let {
        data,
        geoGraphicRoleFields,
        labelField,
        colorField,
        sizeField,
        dispatch,
        isLongLatMapType,
        report,
        mapColors,
        tooltipFields,
        reportId,
        interactiveMode,
        formatColor
    } = props || {}

    let sizeMap = {}, isDiscrete = false;

    let { features: mapData = [] } = constructGeoJsonData(data, geoGraphicRoleFields, 'line', dispatch, isLongLatMapType, { labelField, report })

    const setMapColors = (data, formatColor) => {
        if (formatColor?.showAll) {
            const singleFiledcolor = getFiledValueColor(data, formatColor);
            if (singleFiledcolor) {
                return singleFiledcolor;
            } else {
                const { value } = data || {}
                if (value) {
                    return getEachFieldColor(formatColor, value);
                }
                return '#546ce6';
            }
        } else {
            return getHTMLColorFormat(formatColor.defaultColor);
        }
    }

    const getColor = (index, properties, isDiscrete, mapColors) => {

        if (formatColor?.formatColorStyle === "fieldValue") {
            const fieldName = getFieldName(formatColor?.formatColorField, report);
            if (colorField === fieldName) {
                if (fieldName) {
                    return setMapColors({ [fieldName]: properties?.[fieldName] }, formatColor);
                }
            } else {
                return getHTMLColorFormat(formatColor.defaultColor)
            }
        }
        if (formatColor?.formatColorStyle === "gradient") {
            let colorValue = index;
            if (colorField) {
                let value = properties?.[colorField];
                if (typeof value === "number") {
                    const sortedData = data?.sort((a, b) => a?.[colorField] - b?.[colorField]);
                    colorValue = sortedData?.findIndex((item) => item?.[colorField] === value);
                } else {
                    colorValue = value;
                }
            }
            return mapColors?.[colorValue] || '#a9d3f2'
        }


        if (!colorField) return '#a9d3f2'

        if (isDiscrete) {
            return mapColors?.[properties?.[colorField]] || "#a9d3f2";
        }

        const continuousColors = generateColorRanges(data, colorField);
        if (continuousColors?.length) {
            return getColorByValue(properties?.[colorField], continuousColors) || "#a9d3f2";
        }

        let color = mapColors?.[index] || '#a9d3f2'
        return color
    }

    if (sizeField) {
        const reportField = report?.fields.find((field) => {
            return getFieldAliasName(field) === sizeField;
        });
        if (reportField && reportField.floatingType !== "discrete") {
            sizeMap = calculateScaleValues(data, sizeField)
        }
    }

    if (colorField) {
        const reportField = report?.fields.find((field) => {
            return getFieldAliasName(field) === colorField;
        });
        if (reportField && reportField.floatingType === "discrete") {
            isDiscrete = true
            const fieldValues = [...new Set(data.map(d => d[colorField]))];
            mapColors = fieldValues.reduce((acc, val) => {
                acc[val] = randomColor({ count: 1, hue: 'random' })[0]
                return acc
            }, {})
        }
    }

    const getSize = (value) => {
        if (!sizeField || isEmpty(sizeMap)) return 4;
        return sizeMap[value]
    }


    const handleClick = (e, _data) => {
        if (interactiveMode) {
            let items = [...tooltipFields]?.map((field) => {
                return {
                    field,
                    name: field,
                    value: _data?.[field] || ""
                }
            })

            const { containerPoint: { x, y } = {} } = e || {};
            let menuData = {
                payload: items,
                position: { top: y, left: x },
                drillDownFilterValues: data,
            };
            dispatch(setMenuData({ reportId, menu: menuData }));
        }
    }

    return (
        mapData.map((mData, index) => {
            const { geometry: { coordinates = [] } = {}, properties = {} } = mData || {}
            let tooltipContent = getOSMChartTooltip([...tooltipFields], properties, report)

            return (
                <Polyline
                    pathOptions={{ color: getColor(index, properties, isDiscrete, mapColors) }}
                    positions={coordinates.map(d => d.reverse())} key={uuidv4()}
                    weight={getSize(properties?.[sizeField])}
                    eventHandlers={{
                        click: (e) => handleClick(e, properties)
                    }}
                >
                    <Tooltip content={tooltipContent} direction="center" />
                </Polyline>
            )
        })
    )
}

export default OSMLineMapLayer