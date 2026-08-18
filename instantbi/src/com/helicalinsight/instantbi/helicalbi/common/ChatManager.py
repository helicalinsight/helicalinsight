import logging

chat_store = {}
viz_response_store = {}
sql_store = {}
prev_insight={}

logger = logging.getLogger(__name__)

def add_message(thread_id, content):
    if thread_id not in chat_store:
        chat_store[thread_id] = []
    chat_store[thread_id].append({
        "previous_query": content
    })

def add_viz_response(thread_id, content):
    if thread_id not in viz_response_store:
        viz_response_store[thread_id] = []
    viz_response_store[thread_id].append({
        "previous_visualization": content
    })

def add_sql(thread_id, content):
    if thread_id not in sql_store:
        sql_store[thread_id] = []
    sql_store[thread_id].append({
        "previous_sql": content
    })

def add_insight(thread_id, content):
    if thread_id not in prev_insight:
        prev_insight[thread_id] = []
    prev_insight[thread_id].append({
        "previous_insight": content
    })


def get_last_n(thread_id, n=3):
    if thread_id not in chat_store:
        return []
    return chat_store[thread_id][-n:]



def get_last_n_viz(thread_id, n=1):
    if thread_id not in viz_response_store:
        return []
    return viz_response_store[thread_id][-n:]


def get_last_sql(thread_id, n=1):
    if thread_id not in sql_store:
        return []
    return sql_store[thread_id][-n:]


def get_last_insight(thread_id, n=1):
    if thread_id not in prev_insight:
        return []
    return prev_insight[thread_id][-n:]


def get_last_sql_only(thread_id, n=1):
    if thread_id not in sql_store:
        return []

    results = []
    for item in sql_store[thread_id][-n:]:
        previous_sql = item.get("previous_sql") if isinstance(item, dict) else None
        if previous_sql is None:
            continue
        sql = (
            previous_sql.get("sql")
            if isinstance(previous_sql, dict)
            else getattr(previous_sql, "sql", None)
        )
        if sql is not None:
            results.append(sql)
    return results