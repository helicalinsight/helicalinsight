import { searchOptions } from "../../../../components/hi-fileBrowser/helperMethods";

describe("searchOptions", () => {
  test("returns an array of paths without adding logicalPath", () => {
    const files = [
      {
        type: "file",
        title: "file1.txt",
      },
      {
        type: "folder",
        name: "folder1",
        children: [
          {
            type: "file",
            title: "file2.txt",
          },
        ],
      },
    ];

    const result = searchOptions(files);

    expect(result).toEqual([
      {
        breadcrumbs: [{ id: undefined, name: "file1.txt", type: "file" }],
        path: "file1.txt",
      },
      {
        breadcrumbs: [{ id: undefined, name: "folder1", type: "folder" }],
        path: "folder1",
      },
      {
        breadcrumbs: [
          { id: undefined, name: "folder1", type: "folder" },
          { id: undefined, name: "file2.txt", type: "file" },
        ],
        path: "folder1 / file2.txt",
      },
    ]);
  });

  test("returns an array of paths with logicalPath added", () => {
    const files = [
      {
        type: "file",
        title: "file1.txt",
      },
      {
        type: "folder",
        name: "folder1",
        children: [
          {
            type: "file",
            title: "file2.txt",
          },
        ],
      },
    ];

    const result = searchOptions(files, true);

    expect(result).toEqual([
      [
        { logicalPath: "file1.txt", title: "file1.txt", type: "file" },
        {
          children: [
            {
              logicalPath: "folder1 / file2.txt",
              title: "file2.txt",
              type: "file",
            },
          ],
          logicalPath: "folder1",
          name: "folder1",
          type: "folder",
        },
      ],
      [
        {
          breadcrumbs: [{ id: undefined, name: "file1.txt", type: "file" }],
          path: "file1.txt",
        },
        {
          breadcrumbs: [{ id: undefined, name: "folder1", type: "folder" }],
          path: "folder1",
        },
        {
          breadcrumbs: [
            { id: undefined, name: "folder1", type: "folder" },
            { id: undefined, name: "file2.txt", type: "file" },
          ],
          path: "folder1 / file2.txt",
        },
      ],
    ]);
  });
});
