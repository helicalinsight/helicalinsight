from helicalbi.sql.SqlSanitizer import as_sql_markdown, strip_sql_markdown


def test_strip_sql_markdown_removes_nested_fences():
    assert strip_sql_markdown("```sql\n```sql\nSELECT 1\n```") == "SELECT 1"
    assert strip_sql_markdown("```sql\nSELECT 1") == "SELECT 1"
    assert strip_sql_markdown("SELECT 1") == "SELECT 1"


def test_as_sql_markdown_does_not_double_wrap():
    once = as_sql_markdown("SELECT 1")
    assert once == "```sql\nSELECT 1"
    assert as_sql_markdown(once) == "```sql\nSELECT 1"
    assert as_sql_markdown("```sql\n```sql\nSELECT region FROM t\n```") == (
        "```sql\nSELECT region FROM t"
    )
