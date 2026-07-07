import "core-js/stable";
import "regenerator-runtime/runtime";
import { configureStore } from "@reduxjs/toolkit";
import axios from "axios";
import reducers from "../../../redux";
import HIAxios from "../../../app/HiAxios";
jest.mock("axios");

describe("HI Axios Testing", () => {
  test("HI Axios props test cases", async () => {
    const store = configureStore({
      reducer: reducers,
      middleware: (getDefaultMiddleware) =>
        getDefaultMiddleware({
          thunk: {
            extraArgument: axios,
          },
          immutableCheck: false,
          serializableCheck: false,
        }),
    });
    const dispatch = store.dispatch;
    const hiAxios = new HIAxios(undefined, undefined, dispatch);
    expect(typeof hiAxios.controller).toBe("object");
    expect(typeof hiAxios.requestId).toBe("string");
    // expect(typeof hiAxios.instance).toBe("object");
    // expect(typeof hiAxios.instance.get).toBe("function");
    expect(typeof hiAxios.dispatch).toBe("function");
    expect(typeof hiAxios.generateRequestId).toBe("function");
    expect(typeof hiAxios.getRequestId).toBe("function");
    expect(typeof hiAxios.abort).toBe("function");
  });
});
