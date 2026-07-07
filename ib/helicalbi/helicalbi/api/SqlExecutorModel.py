from dataclasses import dataclass
from typing import Optional


@dataclass(frozen=True)
class SqlExecutorModel:
    md_location:str
    md_file_name:str
    table:str
    column:str
    data_type:str
    sql: Optional[str] = None
