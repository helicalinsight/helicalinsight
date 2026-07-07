import { getDatasourceFD } from "../../../../components/hi-metadata/utils/getDatasourceFD";

const record = {
  data: {
    id: "1",
    type: "dynamicDataSource",
  },
  dataSourceProvider: "tomcat",
  type: "dynamicDataSource",
  permissionLevel: 2,
  driver: "org.apache.derby.jdbc.AutoloadedDriver",
  name: "SampleTravelDataDerby",
  classifier: "global",
  dataSourceType: "Managed DataSource",
  category: "dataSource",
  children: [],
  driverType: "Derby",
  keyPath: "oao2-9yhh-jeb4-h49k-d9/2tns-rpgz-lc44-2pq9-b0",
  key: "2tns-rpgz-lc44-2pq9-b0",
  uuid: "2tns-rpgz-lc44-2pq9-b0",
};

const refreshDataSource = true;

describe("Metadata refresh data source test", () => {
  test("Referesh parameter is added to form data when refreshed", () => {
    const formdata = getDatasourceFD(record, refreshDataSource);
    expect(formdata.parameters.refresh).toBe(true)
  });

  test("Referesh parameter is not added to form data during normal loading", () => {
    const formdata = getDatasourceFD(record);
    expect(formdata.parameters.refresh).toBe(undefined)
  });
});
