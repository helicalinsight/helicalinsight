function sum(a, b) {
  return a + b;
}

test("adds 1 + 2 to equal 3", () => {
  expect(sum(1, 2)).toBe(3);
});

// import {
//   handleEdit,
//   handleNameChange,
//   handleNameChangeKeyDown,
//   handleDelete,
//   handleSqlTypeChange,
// } from "../utils/reportActions";

// const data = [
//   { id: 1, name: "datasource", isEditClicked: true },
//   { id: 2, name: "parameter", isEditClicked: true },
// ];

// const sqlTypes = [
//   { id: 1, name: "datasource", sql: { name: "sql1", type: "sql" } },
//   { id: 2, name: "parameter", sql: { name: "sql2", type: "sql.adhoc" } },
// ];

// const e = {
//   key: "",
//   target: { value: "report" },
// };

// const e2 = {
//   key: "Enter",
//   target: { value: "report" },
// };

// describe("Handle Edit", () => {
//   test("edit with correct id", () => {
//     expect(handleEdit(1, data)).toEqual([
//       { id: 1, name: "datasource", isEditClicked: false },
//       { id: 2, name: "parameter", isEditClicked: true },
//     ]);
//   });

//   test("edit with wrong id", () => {
//     expect(handleEdit(3, data)).toEqual(data);
//   });
// });

// describe("handleNameChange", () => {
//   test("name change with correct id", () => {
//     expect(handleNameChange(e, 2, data)).toEqual([
//       { id: 1, name: "datasource", isEditClicked: true },
//       { id: 2, name: "report", isEditClicked: false },
//     ]);
//   });

//   test("name change with wrong id", () => {
//     expect(handleNameChange(e, 3, data)).toEqual(data);
//   });
// });

// describe("handleNameChangeKeyDown", () => {
//   test("name change with correct id", () => {
//     expect(handleNameChangeKeyDown(e2, 2, data)).toEqual([
//       { id: 1, name: "datasource", isEditClicked: true },
//       { id: 2, name: "report", isEditClicked: false },
//     ]);
//   });

//   test("name change with wrong id", () => {
//     expect(handleNameChangeKeyDown(e2, 3, data)).toEqual(data);
//   });
// });

// describe("handleDelete", () => {
//   test("delete with correct id", () => {
//     expect(handleDelete(2, data)).toEqual([
//       { id: 1, name: "datasource", isEditClicked: true },
//     ]);
//   });

//   test("delete with wrong id", () => {
//     expect(handleDelete(3, data)).toEqual(data);
//   });
// });

// describe("handleSqlTypeChange", () => {
//   test("type change with correct id", () => {
//     expect(handleSqlTypeChange(1, "type", sqlTypes)).toEqual([
//       { id: 1, name: "datasource", sql: { name: "sql1", type: "type" } },
//       { id: 2, name: "parameter", sql: { name: "sql2", type: "sql.adhoc" } },
//     ]);
//   });

//   test("type change with wrong id", () => {
//     expect(handleSqlTypeChange(3, "sql.groovy", sqlTypes)).toEqual(sqlTypes);
//   });
// });
