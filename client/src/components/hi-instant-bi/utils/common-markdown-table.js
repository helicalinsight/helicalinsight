import { Pagination } from "antd";
import { useEffect, useMemo, useState } from "react";
import Markdown from "react-markdown";
import remarkGfm from "remark-gfm";

const DEFAULT_PAGE_SIZE = 10;
const PAGE_SIZE_OPTIONS = ["10", "20", "50", "100"];

const generateMarkdownTable = (tableData = []) => {
  if (!tableData.length) return "No data available";
  const headers = Object.keys(tableData[0]);
  const rows = [
    `| ${headers.join(" | ")} |`,
    `| ${headers.map(() => "---").join(" | ")} |`,
    ...tableData.map(
      (row) => `| ${headers.map((key) => row[key] ?? "").join(" | ")} |`,
    ),
  ];
  return rows.join("\n");
};

const CommonMarkdownTable = ({ data = [], pageSize: initialPageSize = DEFAULT_PAGE_SIZE }) => {
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(initialPageSize);
  const tableData = Array.isArray(data) ? data : [];
  const total = tableData.length;

  useEffect(() => {
    setCurrentPage(1);
  }, [total, pageSize]);

  const pageData = useMemo(() => {
    const start = (currentPage - 1) * pageSize;
    return tableData.slice(start, start + pageSize);
  }, [tableData, currentPage, pageSize]);

  if (!total) {
    return <div className="ib-data-table__empty">No data available</div>;
  }

  return (
    <div className="ib-data-table">
      <div className="ib-data-table__content">
        <Markdown remarkPlugins={[remarkGfm]}>
          {generateMarkdownTable(pageData)}
        </Markdown>
      </div>
      {total > 0 && (
        <Pagination
          className="ib-data-table__pagination"
          size="small"
          current={currentPage}
          pageSize={pageSize}
          total={total}
          showSizeChanger
          pageSizeOptions={PAGE_SIZE_OPTIONS}
          onChange={(page, size) => {
            setCurrentPage(page);
            if (size !== pageSize) {
              setPageSize(size);
            }
          }}
          onShowSizeChange={(_current, size) => {
            setPageSize(size);
            setCurrentPage(1);
          }}
        />
      )}
    </div>
  );
};

export default CommonMarkdownTable;