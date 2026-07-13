from helicalbi.core.flows.CubeInfoSqlGenerator import build_cube_info_sql_generator_graph
from helicalbi.core.flows.SqlGenerator import build_sql_generator_graph
from helicalbi.core.insightflow.DataInsightGraph import build_data_insight_graph
from helicalbi.core.sqlflow.CubeInfoSqlFlowGraph import build_cube_info_sql_graph
from helicalbi.core.sqlflow.SqlFlowGraph import build_sql_graph
from helicalbi.core.vizflow.VizFlowGraph import build_viz_graph

sql_graph = build_sql_graph()
sql_generator_graph = build_sql_generator_graph(sql_graph)

cube_info_sql_graph = build_cube_info_sql_graph()
cube_info_sql_generator_graph = build_cube_info_sql_generator_graph(cube_info_sql_graph)

viz_graph = build_viz_graph()
data_insight_graph = build_data_insight_graph()
