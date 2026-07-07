import HIAxios from "../../app/HiAxios";


describe('HIAxios', () => {
    test("HIAxios initializes with auth, baseURL, and dispatch", () => {
        const auth = { user: "testuser", password: "testpassword" };
        const baseURL = "https://test-api.com";
        const dispatch = jest.fn();
        const hiAxios = new HIAxios(auth, baseURL, dispatch);
        expect(hiAxios.auth).toBe(auth);
        expect(hiAxios.baseURL).toBe(baseURL);
        expect(hiAxios.dispatch).toBe(dispatch);
      });
      test("generateRequestId generates a unique ID", () => {
        const hiAxios1 = new HIAxios();
        const hiAxios2 = new HIAxios();
        expect(hiAxios1.generateRequestId()).not.toBe(hiAxios2.generateRequestId());
      });
      test("getRequestId returns the current request ID", () => {
        const hiAxios = new HIAxios();
        const requestId = hiAxios.getRequestId();
        expect(requestId).toBeDefined();
        expect(typeof requestId).toBe("string");
      });      
});
