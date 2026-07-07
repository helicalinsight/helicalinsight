import { addFolder } from "../../../../components/hi-fileBrowser/helperMethods";

describe("addFolder", () => {
  test('adds a folder to the root level', () => {
    const files = [
      {
        type: 'file',
        title: 'file1.txt'
      },
      {
        type: 'folder',
        name: 'folder1',
        children: [
          {
            type: 'file',
            title: 'file2.txt'
          }
        ]
      }
    ];

    const folderObj = {
      type: 'folder',
      name: 'folder2',
      path: '/folder2'
    };

    const result = addFolder(files, folderObj);

    expect(result).toEqual([
      {
        type: 'file',
        title: 'file1.txt'
      },
      {
        type: 'folder',
        name: 'folder1',
        children: [
          {
            type: 'file',
            title: 'file2.txt'
          }
        ]
      },
      {
        type: 'folder',
        name: 'folder2',
        path: 'folder2'
      }
    ]);
  });

  test("adds a folder to a nested level", () => {
    const files = [
      {
        type: "folder",
        name: "folder1",
        children: [
          {
            type: "folder",
            name: "subfolder1",
            children: [],
          },
        ],
      },
    ];

    const folderObj = {
      type: "folder",
      name: "subfolder2",
      path: "/folder1/subfolder2",
    };

    const result = addFolder(files, folderObj);

    expect(result).toEqual([
      {
        children: [{ children: [], name: "subfolder1", type: "folder" }],
        name: "folder1",
        type: "folder",
      },
    ]);
  });
});
