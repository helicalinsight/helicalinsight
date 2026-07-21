"""
Shared output constraints only. Chart-specific config/polish lives in each viz module.
Assumes LLM knows @ant-design/charts@1.4.2 (G2Plot API).
"""

BASE_RULES = """
@ant-design/charts@1.4.2. Output JSX only: no imports/markdown/export statements.
Keep `data` as-is. Replace placeholders with real metadata keys (dims=text/cat/date, measures=numeric).
Keep function name + component. Valid braces/tags.
"""

OTHER_BASE_RULES = """
@ant-design/charts@1.4.2 custom viz. Output JSX only: no imports/markdown/export statements.
Keep `data` as-is. Function must be DrawOther.
Pick ONE component from `components` matching viz_hint. Map dims->xField/colorField; measures->yField/angleField/sizeField.
Wrap: return <div><Comp {...config} /></div>. Optional helperFunctions: getTooltip, enableInteractivity, getPropertiesConfig.
"""
