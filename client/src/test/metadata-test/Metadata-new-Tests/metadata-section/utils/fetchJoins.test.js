import { fetchJoins } from "../../../../../components/hi-metadata/utils";
import {
  metadataActions,
  updateLoadingStatusForJoins,
} from "../../../../../redux/actions";
import { uuid } from "../../../../../utils/uuid";
import requests from "../../../../../base/requests";

jest.mock("../../../../../base/requests");

describe("fetchJoins", () => {
  const dataSource = [
    {
      id: "1",
      type: "postgres",
      dir: "left",
      catalog: "test_catalog",
      schema: "test_schema",
      connId: "123",
    },
    {
      id: "2",
      type: "mysql",
      dir: "right",
      catalog: "test_catalog",
      schema: "test_schema",
      connId: "456",
    },
  ];

  const tables = {
    1: {
      name: "test_table_1",
      connId: "123",
    },
    2: {
      name: "test_table_2",
      connId: "123",
    },
    3: {
      name: "test_table_3",
      connId: "456",
    },
  };

  const dispatch = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("should return if dataSource is empty", async () => {
    await fetchJoins({
      dataSource: [],
      tables,
      dispatch,
    });

    expect(dispatch).not.toHaveBeenCalled();
  });

  // test("should fetch joins for each dataSource", async () => {
  //   const joins = [
  //     {
  //       uuid: uuid(),
  //       left: { table: "test_table_1", column: "test_column_1", dbId: "123" },
  //       right: { table: "test_table_2", column: "test_column_2", dbId: "123" },
  //     },
  //   ];

  //   requests.metadata.mockImplementation(() => ({
  //     fetchJoins: (formData, success) => {
  //       success({ joins });
  //     },
  //   }));

  //   await fetchJoins({
  //     dataSource,
  //     tables,
  //     dispatch,
  //   });

  //   expect(requests.metadata).toHaveBeenCalledTimes(2);
  //   expect(dispatch).toHaveBeenCalledTimes(6);
  //   expect(dispatch).toHaveBeenCalledWith(
  //     updateLoadingStatusForJoins({
  //       connId: "123",
  //       status: true,
  //     })
  //   );
  //   expect(dispatch).toHaveBeenCalledWith(
  //     metadataActions.updateJoinsData({
  //       data: joins,
  //       override: true,
  //     })
  //   );
  //   expect(dispatch).toHaveBeenCalledWith(
  //     updateLoadingStatusForJoins({
  //       connId: "456",
  //       status: true,
  //     })
  //   );
  // });

  test("should skip if joins are already fetched", async () => {
    const joins = [
      {
        uuid: uuid(),
        left: { table: "test_table_1", column: "test_column_1", dbId: "123" },
        right: { table: "test_table_2", column: "test_column_2", dbId: "123" },
      },
    ];

    requests.metadata.mockImplementation(() => ({
      fetchJoins: (formData, success) => {
        success({ joins });
      },
    }));

    await fetchJoins({
      dataSource: [
        ...dataSource,
        {
          id: "3",
          type: "postgres",
          dir: "left",
          catalog: "test_catalog",
          schema: "test_schema",
          connId: "789",
          joinsFetched: true,
        },
      ],
      tables,
      dispatch,
    });

    expect(requests.metadata).toHaveBeenCalledTimes(3);
  });
});
