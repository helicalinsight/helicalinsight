import { loginHandlers } from "../../../../components/hi-login/helperMethods";
import { mock_data } from "../mocks/helper.mock";
describe("loginHandlers function", () => {
  test("to check the loginHandlers function", () => {
    const urlPath = mock_data.urlPath ;
    const userDetails = mock_data.userDetails;
    const dispatch = jest.fn();
    const nxtRoute = mock_data.nxtRoute;
    const pathname = mock_data.pathname;
    const logType = mock_data.logType;
    const activeRoute = mock_data.activeRoute;
    const isAuthenticated = mock_data.isAuthenticated;
    const history = mock_data.history;

    

    const result = loginHandlers({   
        urlPath ,
        userDetails,
        dispatch,
        nxtRoute,
        pathname,
        logType ,
        activeRoute,
        isAuthenticated,
        history
     } )
    expect(result).toEqual(undefined);
  });
});
