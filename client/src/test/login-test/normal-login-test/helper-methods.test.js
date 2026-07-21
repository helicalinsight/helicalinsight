import { defaultRoutes, routesUrl } from "../../../app/constants";
import { credentialsValidator } from "../../../components/hi-login/hi-loginForm/hi-loginForm";
import { updateAppTitle } from "../../../customHooks/applicationSettingsCheckLayer";

const expectedTitles = [
  "HI: Login",
  "HI: Admin",
  "HI: User",
  "HI: Data Sources",
  "HI: Meta Data",
  // 'HI: CE-Report-Create',
  "HI: Reports",
  'HI: Canned Report',
  "HI: Dashboard Designer",
  "HI: Report View",
  "HI: Cube",
  "HI: User",
  "HI: Instant",
  "HI: Semantic Model",
  "HI: Community Report",
  "HI: Workflow",
  "HI: Admin",
  "HI: Data Sources",
  // "HI: Canned Report",
  
];

describe("Testing credentialsValidator function:", () => {
  test('Must contain "organization", "password", "usrname": success handler', (done) => {
    expect(
      credentialsValidator({ organization: "", username: "", password: "" })
    ).toEqual(1);
    done();
  });
  test('If not contain "organization" : failure handler', (done) => {
    expect(credentialsValidator({ username: "", password: "" })).toEqual(0);
    done();
  });
  test('If not contain "password" : failure handler', (done) => {
    expect(credentialsValidator({ username: "", organization: "" })).toEqual(0);
    done();
  });
  test('If not contain "usrname": failure handler', (done) => {
    expect(credentialsValidator({ organization: "", password: "" })).toEqual(0);
    done();
  });
  test('If not contain any of "organization", "password", "usrname": failure handler', (done) => {
    expect(credentialsValidator({})).toEqual(0);
    done();
  });
});

describe("Testing updateAppTitle function:", () => {
  test("Success: Title must match with url", (done) => {
    let urls = Object.values(routesUrl);
    urls = [...urls, "/admin/overview", "/datasources/all"];
    const titles = urls.map((url) => updateAppTitle(url, defaultRoutes));
    expect(titles).toEqual(expectedTitles);
    done();
  });

  test("Failure: Title must be empty string", (done) => {
    const url = "abcdef";
    const title = updateAppTitle(url, defaultRoutes);
    expect(title).toEqual("");
    done();
  });
});
