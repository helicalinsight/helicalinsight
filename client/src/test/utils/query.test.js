import Query from '../../utils/query';

const data = [{
    "name": "Naresh",
    "type": "folder",
    "children": [
      {
        "path": "1633601282357/abc",
        "name": "abc",
        "type": "folder",
        "children": [
          {
            "extension": "hr",
            "name": "2a9f1701-4a30-4980-bd22-bd552663b8ba.report",
            "type": "file",
            "title": "3773 date asc"
          },
          {
            "extension": "efw",
            "name": "9439ab9a-bf7e-4907-9a91-765530076251.efw",
            "type": "file",
            "title": "hiselect 2 hidataman"
          }
        ]
      },
      {
        "extension": "efwdd",
        "name": "ec8c38f3-1049-433a-b7eb-d6d94299925b.efwdd",
        "type": "file",
        "title": "efw"
      },
      {
        "extension": "hr",
        "name": "1beb3cc4-78b1-4815-bd3f-a4b768b9a795.hr",
        "type": "file",
        "title": "all"
      }
    ]
  }
]

test("Preserve structure with filtering", () => {
    const outputObj = [{
        "name": "Naresh",
        "type": "folder",
        "children": [
          {
            "path": "1633601282357/abc",
            "name": "abc",
            "type": "folder",
            "children": [
              {
                "extension": "hr",
                "name": "2a9f1701-4a30-4980-bd22-bd552663b8ba.report",
                "type": "file",
                "title": "3773 date asc"
              },
            ]
          },
          {
            "extension": "hr",
            "name": "1beb3cc4-78b1-4815-bd3f-a4b768b9a795.hr",
            "type": "file",
            "title": "all"
          }
        ]
      }
    ]
  expect(new Query().from(data).select("*").preserveKey("children").where("extension", "equals", "hr").execute()).toStrictEqual(outputObj);
});

test("Get results with key value, structure doesnt matter", () => {
    const outputObj = [
      {
        extension: "hr",
        name: "2a9f1701-4a30-4980-bd22-bd552663b8ba.report",
        type: "file",
        title: "3773 date asc",
      },
      {
        extension: "hr",
        name: "1beb3cc4-78b1-4815-bd3f-a4b768b9a795.hr",
        type: "file",
        title: "all",
      },
    ];
  expect(new Query().from(data).where("extension", "equals", "hr").execute()).toStrictEqual(outputObj);
});

test("Get results with key value, filter specific keys from result ", () => {
    const outputObj = [
        {
            "name": "2a9f1701-4a30-4980-bd22-bd552663b8ba.report",
            "type": "file"
        },
        {
            "name": "1beb3cc4-78b1-4815-bd3f-a4b768b9a795.hr",
            "type": "file"
        }
    ];
  expect(new Query().from(data).select("name","type").where("extension", "equals", "hr").execute()).toStrictEqual(outputObj);
});

test("Get all values for a key in an array", () => {
    const outputObj = [
        "Naresh",
        "abc",
        "2a9f1701-4a30-4980-bd22-bd552663b8ba.report",
        "9439ab9a-bf7e-4907-9a91-765530076251.efw",
        "ec8c38f3-1049-433a-b7eb-d6d94299925b.efwdd",
        "1beb3cc4-78b1-4815-bd3f-a4b768b9a795.hr"
    ];
  expect(new Query().from(data).select("name").execute()).toStrictEqual(outputObj);
});

test("Get all values for a key in an accumulated arrays", () => {
    const outputObj = {
        "name": [
            "Naresh",
            "abc",
            "2a9f1701-4a30-4980-bd22-bd552663b8ba.report",
            "9439ab9a-bf7e-4907-9a91-765530076251.efw",
            "ec8c38f3-1049-433a-b7eb-d6d94299925b.efwdd",
            "1beb3cc4-78b1-4815-bd3f-a4b768b9a795.hr"
        ],
        "type": [
            "folder",
            "folder",
            "file",
            "file",
            "file",
            "file"
        ]
    };
  expect(new Query().from(data).select("name","type").execute()).toEqual(outputObj);
});

test("Checking or condition", () => {
    const outputObj = [
        {
            "extension": "hr",
            "name": "2a9f1701-4a30-4980-bd22-bd552663b8ba.report",
            "type": "file",
            "title": "3773 date asc"
        },
        {
            "extension": "efw",
            "name": "9439ab9a-bf7e-4907-9a91-765530076251.efw",
            "type": "file",
            "title": "hiselect 2 hidataman"
        },
        {
            "extension": "hr",
            "name": "1beb3cc4-78b1-4815-bd3f-a4b768b9a795.hr",
            "type": "file",
            "title": "all"
        }
    ];
  expect( new Query().from(data).where("extension", "equals", "hr").or("extension", "equals", "efw").execute()).toStrictEqual(outputObj);
});