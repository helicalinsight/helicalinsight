import { fbGroupBy } from "../../../../components/hi-fileBrowser/helperMethods";

describe("fbGroupBy", () => {
  test("groups data by key", () => {
    const data = [
      {
        type: "file",
        title: "file1.txt",
        extension: "txt",
      },
      {
        type: "file",
        title: "file2.js",
        extension: "js",
      },
      {
        type: "folder",
        name: "folder1",
        children: [
          {
            type: "file",
            title: "file3.jpg",
            extension: "jpg",
          },
          {
            type: "file",
            title: "file4.txt",
            extension: "txt",
          },
        ],
      },
      {
        type: "folder",
        name: "folder2",
        children: [
          {
            type: "file",
            title: "file5.js",
            extension: "js",
          },
          {
            type: "file",
            title: "file6.jpg",
            extension: "jpg",
          },
        ],
      },
    ];

    const key = "extension";

    const result = fbGroupBy(key, data);

    expect(result).toEqual([
      {
        name: "txt",
        children: [
          {
            type: "file",
            title: "file1.txt",
            extension: "txt",
          },
          {
            type: "file",
            title: "file4.txt",
            extension: "txt",
          },
        ],
        type: "folder",
        id: expect.any(String),
      },
      {
        name: "js",
        children: [
          {
            type: "file",
            title: "file2.js",
            extension: "js",
          },
          {
            type: "file",
            title: "file5.js",
            extension: "js",
          },
        ],
        type: "folder",
        id: expect.any(String),
      },
      {
        name: "jpg",
        children: [
          {
            type: "file",
            title: "file3.jpg",
            extension: "jpg",
          },
          {
            type: "file",
            title: "file6.jpg",
            extension: "jpg",
          },
        ],
        type: "folder",
        id: expect.any(String),
      },
    ]);
  });
});
