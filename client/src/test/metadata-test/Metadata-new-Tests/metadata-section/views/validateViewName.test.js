import { validateViewName } from "../../../../../components/hi-metadata/utils/views/validateViewName";

describe("validateViewName", () => {
  const tables = {
    1: { name: "Table1", alias: "T1", category: "table", id: "1" },
    2: { name: "Table2", alias: "T2", category: "view", id: "2" },
    3: { name: "Table3", alias: "T3", category: "table", id: "3" },
  };
  const activeView = "2";
  const views = [{ id: "2", uuid: "123456" }];
  
  test("returns false and a message if viewName is empty", () => {
    const result = validateViewName("", tables, activeView, views);
    expect(result.isValid).toBe(false);
    expect(result.msg).toBe("Please provide valid name for view");
  });

  test("returns false and a message if viewName has less than 3 characters", () => {
    const result = validateViewName("v1", tables, activeView, views);
    expect(result.isValid).toBe(false);
    expect(result.msg).toBe("Please provide valid name for view");
  });

  test("returns false and a message if viewName has more than 255 characters", () => {
    const longViewName = "v".repeat(256);
    const result = validateViewName(longViewName, tables, activeView, views);
    expect(result.isValid).toBe(false);
    expect(result.msg).toBe("Name should contain maximum of 255 characters");
  });

  test("returns false and a message if viewName starts with a space", () => {
    const result = validateViewName(" view1", tables, activeView, views);
    expect(result.isValid).toBe(false);
    expect(result.msg).toBe("View name cannot start or end with space");
  });

  test("returns false and a message if viewName ends with a space", () => {
    const result = validateViewName("view1 ", tables, activeView, views);
    expect(result.isValid).toBe(false);
    expect(result.msg).toBe("View name cannot start or end with space");
  });

  test("returns false and a message if viewName contains invalid characters", () => {
    const result = validateViewName("view1@", tables, activeView, views);
    expect(result.isValid).toBe(false);
    expect(result.msg).toBe("Please provide valid name for view.");
  });

  test("returns false and a message if viewName already exists as a table name", () => {
    const result = validateViewName("Table2", tables, activeView, views);
    expect(result.isValid).toBe(false);
    expect(result.msg).toBe("View Name already exists");
  });

  it("returns false and a message if viewName already exists as an alias", () => {
    const tables = {
      1: { id: 1, name: "table1", alias: "T1", category: "table" },
      2: { id: 2, name: "table2", alias: "T2", category: "table" },
      3: { id: 3, name: "view1", alias: "V1", category: "view" },
    };
    const activeView = 3;
    const views = [{ id: 3, uuid: "abc123" }];
    const result = validateViewName("V1", tables, activeView, views);
    expect(result.isValid).toBe(false);
    expect(result.msg).toBe("Please provide valid name for view");
  });
  

  test("returns true and an empty message if viewName is valid", () => {
    const result = validateViewName("NewView", tables, activeView, views);
    expect(result.isValid).toBe(true);
    expect(result.msg).toBe("");
  });
});
