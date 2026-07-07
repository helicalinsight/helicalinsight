import { searchData } from "../../../../components/hi-fileBrowser/helperMethods";

describe("searchData", () => {
  test("searches data and returns matching results", () => {
    const data = [
      {
        type: "file",
        title: "file1.txt",
        path: "/file1.txt",
      },
      {
        type: "folder",
        name: "folder1",
        path: "/folder1",
        children: [
          {
            type: "file",
            title: "file2.txt",
            path: "/folder1/file2.txt",
          },
          {
            type: "folder",
            name: "subfolder",
            path: "/folder1/subfolder",
            children: [
              {
                type: "file",
                title: "file3.txt",
                path: "/folder1/subfolder/file3.txt",
              },
            ],
          },
        ],
      },
      {
        type: "folder",
        name: "folder2",
        path: "/folder2",
        children: [
          {
            type: "file",
            title: "file4.txt",
            path: "/folder2/file4.txt",
          },
        ],
      },
    ];

    const searchTerm = "file";

    const result = searchData(data, searchTerm);

    expect(result).toEqual([
      { path: "/folder1/file2.txt", title: "file2.txt", type: "file" },
      {
        path: "/folder1/subfolder/file3.txt",
        title: "file3.txt",
        type: "file",
      },
      { path: "/folder2/file4.txt", title: "file4.txt", type: "file" },
      { children: [], path: "/file1.txt", title: "file1.txt", type: "file" },
    ]);
  });
});
