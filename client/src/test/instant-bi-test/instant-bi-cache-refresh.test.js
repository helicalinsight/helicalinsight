import "regenerator-runtime/runtime";
import actionTypes from "../../redux/actions/actionTypes";
import { updateCacheRefreshStatus } from "../../redux/actions/instant-bi.actions";
import initialStates from "../../redux/reducers/initialStates";
import instantBIReducer from "../../redux/reducers/instant-bi.reducer";

describe("Instant BI cache refresh", () => {
  it("should create an update cache refresh status action", () => {
    expect(updateCacheRefreshStatus()).toEqual({
      type: actionTypes.UPDATE_CACHE_REFRESH_STATUS,
      payload: undefined,
    });
  });

  it("should start with a refresh count of zero", () => {
    expect(initialStates.instantBIInitialState.refreshReports).toBe(0);
  });

  it("should increment the refresh count for every cache refresh action", () => {
    const firstState = instantBIReducer(
      initialStates.instantBIInitialState,
      updateCacheRefreshStatus(),
    );
    expect(firstState.refreshReports).toBe(1);

    const secondState = instantBIReducer(firstState, updateCacheRefreshStatus());
    expect(secondState.refreshReports).toBe(2);
  });

  it("should initialize a missing refresh count before incrementing it", () => {
    const { refreshReports, ...stateWithoutCounter } =
      initialStates.instantBIInitialState;

    const nextState = instantBIReducer(
      stateWithoutCounter,
      updateCacheRefreshStatus(),
    );

    expect(refreshReports).toBe(0);
    expect(nextState.refreshReports).toBe(1);
  });
});
