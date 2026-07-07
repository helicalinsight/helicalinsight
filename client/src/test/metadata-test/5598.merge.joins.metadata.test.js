import { mergeJoins } from "../../components/hi-metadata/utils/mergeJoins";
import { configureStore } from "@reduxjs/toolkit";
import reducers from "../../redux";
import { payload5598, payload5598_2, store5598, mergeJoinsData } from "./5598.store.mock.data";
import axios from "axios";

describe("Testing merging joins", () => {
    const {existingJoins, fetchedJoins, tables, storeJoins} = mergeJoinsData;
    const store = configureStore({
        reducer: reducers,
        middleware: (getDefaultMiddleware) =>
            getDefaultMiddleware({
                thunk: {
                    extraArgument: axios
                },
                immutableCheck: false,
                serializableCheck: false,
            }),
        preloadedState: { metadata: store5598 }
    });

  test("To Test: Test merge joins", () => {
    let result = mergeJoins({ existingJoins, fetchedJoins, tables });
    expect(result).toStrictEqual(storeJoins);
  });

  test('testing arguments with empty existing joins', () => {
      let result = mergeJoins({ existingJoins: [], fetchedJoins })
      expect(result).toEqual(fetchedJoins)
  })

  test('serial number of joins', () => {
      let dispatch = store.dispatch
      dispatch(payload5598)
      let result = store.getState().metadata.present.joins.map(j => j.index)
      expect(result.length === [...(new Set(result))].length).toBeTruthy()
  })

  test('teting action property of exising joins', () => {  
      let dispatch = store.dispatch
      dispatch(payload5598_2)
      let result = store.getState().metadata.present.joins.filter(j => j.action === 'add')
      expect(result.length).toBeTruthy()
  })
});
