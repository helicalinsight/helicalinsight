import { getTabActiveStatus } from "../../../components/hi-admin/hi-adminTabs/hi-adminTabs";

const eachData = {
        "tab": "User Management",
        "tabPath": "/usermanagement/organizations",
        "tutorialElementKey": "hi-user-management"
    };

const pathnames = ['/admin/usermanagement/roles', '/admin/usermanagement/users'];

describe("Testing 4675", () => {
  test("Usermanagement tab not showing as active when route to roles/users", (done) => {
    pathnames.forEach(pathname => {
        expect(getTabActiveStatus({pathname, eachData})).toBeTruthy();
    })
    done();
  });
});
