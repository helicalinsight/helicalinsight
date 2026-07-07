import "core-js/stable";
import "regenerator-runtime/runtime";
import "../../../../utils/polyfill/url";
import { configureStore } from "@reduxjs/toolkit";
import { fireEvent, render, screen } from "@testing-library/react";
import { Provider } from "react-redux";
import reducers from "../../../../redux";
import { FbContextMenu } from "../../../../components/hi-fileBrowser/components";
import {
  AGENT_INTERACT_ACTION,
  agentInteractContextMenuOption,
  allContextMenuOptions,
} from "../../../../components/hi-fileBrowser/constants";
import { routesUrl } from "../../../../app/constants";
import { hiMockAxios } from "../../../../app/mock-axios";

const crypto = require("crypto");

const agentRecord = {
  path: "agents/MyAgent.agent",
  extension: "agent",
  permissionLevel: "5",
  name: "MyAgent.agent",
  description: "MyAgent.agent",
  type: "file",
  title: "MyAgent",
};

const renderContextMenu = (store) => {
  const setMenuVisible = jest.fn();
  render(
    <Provider store={store}>
      <FbContextMenu
        setMenuVisible={setMenuVisible}
        record={agentRecord}
        menuOptions={[agentInteractContextMenuOption]}
        files={[]}
        hiddenFileInput={{ current: null }}
        selectedRecordRef={{ current: null }}
      />
    </Provider>
  );
  return { setMenuVisible };
};

describe("Agent Interact context menu", () => {
  beforeAll(() => {
    delete window.matchMedia;
    window.matchMedia = () => ({
      matches: false,
      media: "",
      onchange: null,
      addListener: jest.fn(),
      removeListener: jest.fn(),
      addEventListener: jest.fn(),
      removeEventListener: jest.fn(),
      dispatchEvent: jest.fn(),
    });
    window.crypto = {};
    window.crypto.getRandomValues = (arr) => crypto.randomBytes(arr.length);
  });

  afterAll(() => {
    global.gc && global.gc();
  });

  it("should defines interact option for global file browser append only", () => {
    expect(agentInteractContextMenuOption).toMatchObject({
      id: "intr",
      name: "Interact",
      extensions: ["agent"],
    });
    expect(AGENT_INTERACT_ACTION).toBe("interact");
    expect(allContextMenuOptions.some((option) => option.id === "intr")).toBe(
      false
    );
  });

  it("should routes to Instant BI with sinteract editModeInfo when Interact is clicked", () => {
    const store = configureStore({
      reducer: reducers,
      middleware: (getDefaultMiddleware) =>
        getDefaultMiddleware({
          thunk: { extraArgument: hiMockAxios },
          immutableCheck: false,
          serializableCheck: false,
        }),
    });
    const { setMenuVisible } = renderContextMenu(store);
    fireEvent.click(screen.getByText("Interact"));
    const state = store.getState();
    expect(state.app.editModeInfo).toMatchObject({
      dir: agentRecord.path,
      file: agentRecord.name,
      title: agentRecord.title,
      extension: "agent",
      action: AGENT_INTERACT_ACTION,
    });
    expect(state.fileBrowser.showFileBrowser).toBe(false);
    expect(state.app.activeRoute).toBe(routesUrl.instantBIUrl);
    expect(setMenuVisible).toHaveBeenCalledWith({ visible: false });
  });
});
