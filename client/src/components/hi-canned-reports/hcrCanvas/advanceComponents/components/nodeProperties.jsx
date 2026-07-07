import React from 'react'
import TextProperties from '../../textProperties'
import LineProperties from '../../lineProperties'
import ImageProperties from '../../imageProperties'
import PageBreakProperties from '../../pageBreakProperties'
import CrosstabProperties from '../../crosstabProperties'
import ChartProperties from '../../chartsProperties'
import AdvancedTableProperties from '../../advancedTableProperties'

const NodeProperties = (props = {}) => {
    const {
        EditorPanels,
        onNodeConfigChange = () => { },
        nodeConfig = {}
    } = props || {}
    const category = nodeConfig?.category || ""
    if (!category) return null;
    return {
        text: (
            <TextProperties
                EditorPanels={EditorPanels}
                onNodeConfigChange={onNodeConfigChange}
                nodeConfig={nodeConfig}
            />
        ),
        line: (
            <LineProperties
                EditorPanels={EditorPanels}
                onNodeConfigChange={onNodeConfigChange}
                nodeConfig={nodeConfig}
            />
        ),
        image: (
            <ImageProperties
                EditorPanels={EditorPanels}
                onNodeConfigChange={onNodeConfigChange}
                nodeConfig={nodeConfig}
            />
        ),
        pageBreak: (
            <PageBreakProperties
                EditorPanels={EditorPanels}
                onNodeConfigChange={onNodeConfigChange}
                nodeConfig={nodeConfig}
            />
        ),
        crosstab: (
            <CrosstabProperties
                EditorPanels={EditorPanels}
                onNodeConfigChange={onNodeConfigChange}
                nodeConfig={nodeConfig}
            />
        ),
        chart: (
            <ChartProperties
                EditorPanels={EditorPanels}
                onNodeConfigChange={onNodeConfigChange}
                nodeConfig={nodeConfig}
            />
        ),
        advancedTable: (
            <AdvancedTableProperties
                EditorPanels={EditorPanels}
                onNodeConfigChange={onNodeConfigChange}
                nodeConfig={nodeConfig}
            />
        )
    }[category]
}

export default NodeProperties