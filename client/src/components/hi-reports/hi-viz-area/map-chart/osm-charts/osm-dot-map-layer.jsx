import { isEmpty } from 'lodash'
import { CircleMarker, Marker, Tooltip } from 'react-leaflet'
import { v4 as uuidv4 } from 'uuid'
import { setMenuData } from '../../../../../redux/actions/hreport.actions'
import { getFieldAliasName } from '../../../../../utils/utilities'
import { getEachFieldColor, getFieldName, getFiledValueColor, getHTMLColorFormat } from '../../../hi-editing-area/utils/property-utils'
import { constructGeoJsonData } from '../../utils/utillities'
import OSMText from './osm-text'
import { calculateScaleValues, generateColorRanges, generateDiscreteShapeRanges, generateShapeRanges, getColorByValue, getOSMChartTooltip, getShapeByValue, shapesMapOSM, svgIconFromUrl } from './utilities'
import randomColor from 'randomcolor'


const OSMDotMapLayer = (props = {}) => {
    let {
        data,
        geoGraphicRoleFields,
        labelField,
        colorField,
        sizeField,
        shapeField,
        dispatch,
        isLongLatMapType,
        report,
        mapColors,
        tooltipFields,
        reportId,
        interactiveMode,
        formatColor
    } = props || {}

    let sizeMap = {},
        shapes = [];
    let displayPoint = true, isDiscrete = false, defaultShape = "circle";
    let { features: mapData = [] } = constructGeoJsonData(data, geoGraphicRoleFields, 'point', dispatch, isLongLatMapType, { labelField, report })

    if (!mapData?.length) return null;

    const { labels, shape: shapeProperty = {} } = report?.reportData?.properties || {};
    let labelsColor = isEmpty(labels?.labelsColor) ? null : getHTMLColorFormat(labels?.labelsColor);

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

        if (reportField) {
            if (reportField.floatingType !== "discrete") {
                sizeMap = calculateScaleValues(data, sizeField)
            }
            if (reportField.floatingType === "discrete") {
                isDiscrete = true
            }
        }
    }

    const getSize = (value) => {
        if (!sizeField || isEmpty(sizeMap)) return 8;
        return sizeMap[value]
    }

    if (!shapeField && shapeProperty?.mapDefaultShape !== "circle") {
        displayPoint = false;
        defaultShape = shapeProperty?.mapDefaultShape
    }

    if (shapeField) {
        const reportField = report?.fields.find((field) => {
            return getFieldAliasName(field) === shapeField;
        });
        if (reportField) {
            displayPoint = false
            if (reportField.floatingType !== "discrete") {
                shapes = generateShapeRanges(data, shapeField, 5)
            }
            if (reportField.floatingType === "discrete") {
                isDiscrete = true
                shapes = generateDiscreteShapeRanges(data, shapeField)
            }
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

    const getIcon = ({ shape, index, properties, isDiscrete, mapColors, sizeField }) => {
        if (shapeProperty?.mapShowAllShapes) {
            let url = shapeProperty?.[properties?.[shapeField]] || "";
            if (url) {
                return svgIconFromUrl(url, getSize(properties?.[sizeField]) * 2)
            }
        }
        return shapesMapOSM[shape](getColor(index, properties, isDiscrete, mapColors), (getSize(properties?.[sizeField]) * 2))
    }

    return (
        mapData.map((mData, index) => {
            const { geometry: { coordinates = [] } = {}, properties = {} } = mData || {}
            let tooltipContent = getOSMChartTooltip([...tooltipFields], properties, report)
            let label = properties["displayText"]
            if (labelField) label = properties?.[`${labelField}-formatted`] || properties?.[labelField]
            let shape = ""
            if (shapeField && !displayPoint) {
                if (!isDiscrete) {
                    shape = getShapeByValue(properties?.[shapeField], shapes);
                } else {
                    shape = shapes[properties?.[shapeField]]
                }
            } else {
                shape = defaultShape
            }

            return (
                <>
                    {displayPoint ?
                        <CircleMarker
                            center={coordinates.reverse()}
                            pathOptions={{
                                fillColor: getColor(index, properties, isDiscrete, mapColors),
                                stroke: false,
                                fillOpacity: 1
                            }}
                            radius={getSize(properties?.[sizeField])}
                            key={uuidv4()}
                            eventHandlers={{
                                click: (e) => handleClick(e, properties)
                            }}
                        >
                            <Tooltip content={tooltipContent} direction="center" />
                        </CircleMarker >
                        : null}
                    {(!displayPoint) ?
                        <Marker
                            key={uuidv4()}
                            position={coordinates.reverse()}
                            icon={getIcon({ shape, index, properties, isDiscrete, mapColors, sizeField })}
                            eventHandlers={{
                                click: (e) => handleClick(e, properties)
                            }}
                        >
                            <Tooltip
                                content={tooltipContent}
                                direction="center"
                            />
                        </Marker >
                        : null}
                    <OSMText {...{ coordinates, label, labelsColor }} />
                </>
            )
        })
    )
}

export default OSMDotMapLayer