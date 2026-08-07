import AdvancedTableProperties from '../../advancedTableProperties'
import ChartProperties from '../../chartsProperties'
import CrosstabPropertiesV2 from '../../crosstabPropertiesv2'
import ImageProperties from '../../imageProperties'
import LineProperties from '../../lineProperties'
import PageBreakProperties from '../../pageBreakProperties'
import TextProperties from '../../textProperties'

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
        crosstabv2: (
            <CrosstabPropertiesV2
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