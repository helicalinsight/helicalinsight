import { injectFiltersListAndListenersToGridItem } from "../../../../../../components/hi-dashboard-designer/utils/recursive-functions";

describe("injectFiltersListAndListenersToGridItem", () => {
  test("should inject filters and listeners into the specified item", () => {
    const data = [
      {
        id: 1,
        children: [
          {
            id: 2,
            children: [],
          },
          {
            id: 3,
            children: [],
          },
        ],
      },
      {
        id: 4,
        children: [
          {
            id: 5,
            children: [],
          },
        ],
      },
    ];

    const filtersList = [
      {
        uid: "filter1",
        label: "Filter 1",
        values: ["Value 1"],
        alias: "Alias 1",
        autogen_alias: false,
      },
      {
        uid: "filter2",
        label: "Filter 2",
        values: ["Value 2"],
        alias: "Alias 2",
        autogen_alias: true,
      },
    ];

    const id = 4;
    const reportId = "report123";

    const expectedResult = [
      {
        children: [
          { children: [], id: 2 },
          { children: [], id: 3 },
        ],
        id: 1,
      },
      {
        children: [{ children: [], id: 5 }],
        filters: [
          {
            alias: "Alias 1",
            autogen_alias: false,
            label: "Filter 1",
            uid: "filter1",
            values: ["Value 1"],
          },
          {
            alias: "Alias 2",
            autogen_alias: true,
            label: "Filter 2",
            uid: "filter2",
            values: ["Value 2"],
          },
        ],
        id: 4,
        listeners: ["Alias 1", "Alias 2"],
        reportId: "report123",
      },
    ];

    const result = injectFiltersListAndListenersToGridItem({
      data,
      filtersList,
      id,
      reportId,
    });

    expect(result).toEqual(expectedResult);
  });
});
