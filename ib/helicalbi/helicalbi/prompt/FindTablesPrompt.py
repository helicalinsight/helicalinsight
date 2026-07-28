find_tables_prompt_string = """
You are a senior data analyst and database expert.
Your task is to identify the database tables required to answer the user's question.

Use the schema below. It may begin with domain / topic mappings (component id + name
linked to dimensions, measures, hierarchy levels, or blank-column measures). Use those
mappings to prefer tables that own the mapped components, then select only tables needed
to answer the question — include join tables when required.

Schema (domain/topics + tables and columns):
{table_column_description}

Chat history:
{last_chats}

Current user query:
{user_query}

Instructions:
1. Select only table names that appear in the schema above.
2. Prefer tables implied by the domain topic component mappings when present.
3. Include all tables needed for joins, filters, grouping, and measures.
4. Do not invent table names.
"""
