import { filterByFileType } from "../../../../components/hi-fileBrowser/helperMethods";

describe("filterByFileType", () => {
  test("filters files by type", () => {
    const files = [
      {
        type: "file",
        title: "file1.txt",
      },
      {
        type: "file",
        title: "file2.jpg",
      },
      {
        type: "folder",
        name: "folder1",
        children: [
          {
            type: "file",
            title: "file3.txt",
          },
        ],
      },
    ];

    const result = filterByFileType(files, "type", "file");

    expect(result).toEqual([
      { title: "file1.txt", type: "file" },
      { title: "file2.jpg", type: "file" },
      {
        children: [{ title: "file3.txt", type: "file" }],
        name: "folder1",
        type: "folder",
      },
    ]);
  });

  test("filters files by type with different value", () => {
    const files = [
      {
        type: "file",
        title: "file1.txt",
      },
      {
        type: "file",
        title: "file2.jpg",
      },
      {
        type: "folder",
        name: "folder1",
        children: [
          {
            type: "file",
            title: "file3.txt",
          },
        ],
      },
    ];

    const result = filterByFileType(files, "type", "folder");

    expect(result).toEqual([{ children: [], name: "folder1", type: "folder" }]);
  });
});
