import { handleAddDataSource } from "../../../../components/hi-sidebar/hi-reportCeSidebar";

import { handleAddParameters } from "../../../../components/hi-sidebar/hi-reportCeSidebar";

describe("handleAddDataSource", () => {
  test("should add a new data source", () => {
    const dispatchMock = jest.fn();
    const dataSourceData = [{ number: 1 }, { number: 2 }];

    handleAddDataSource(null, dispatchMock, dataSourceData);

    expect(dispatchMock).toHaveBeenCalled();

    const dispatchedAction = dispatchMock.mock.calls[0][0];
    expect(dispatchedAction.type).toEqual("storeReportCeDatasource");
    expect(dispatchedAction.payload).toHaveLength(dataSourceData.length + 1);
  });

  test("should add the first data source", () => {
    const dispatchMock = jest.fn();
    const dataSourceData = [];

    handleAddDataSource(null, dispatchMock, dataSourceData);

    expect(dispatchMock).toHaveBeenCalled();

    const dispatchedAction = dispatchMock.mock.calls[0][0];
    expect(dispatchedAction.type).toEqual("storeReportCeDatasource");
    expect(dispatchedAction.payload).toHaveLength(1);
  });
});

describe("handleAddParameters", () => {
  test("should add a new parameter", () => {
    const dispatchMock = jest.fn();
    const parametersData = [{ number: 1 }, { number: 2 }];

    handleAddParameters(null, dispatchMock, parametersData);

    expect(dispatchMock).toHaveBeenCalled();

    const dispatchedAction = dispatchMock.mock.calls[0][0];
    expect(dispatchedAction.type).toEqual("storeReportCeParameters");
    expect(dispatchedAction.payload).toHaveLength(parametersData.length + 1);
  });

  test("should add the first parameter", () => {
    const dispatchMock = jest.fn();
    const parametersData = [];

    handleAddParameters(null, dispatchMock, parametersData);

    expect(dispatchMock).toHaveBeenCalled();

    const dispatchedAction = dispatchMock.mock.calls[0][0];
    expect(dispatchedAction.type).toEqual("storeReportCeParameters");
    expect(dispatchedAction.payload).toHaveLength(1);
  });
});
