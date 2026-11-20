import React from "react";
import { configureStore } from "@reduxjs/toolkit";
import { act, render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { Provider } from "react-redux";
import { ContextDrawer } from "../../../components/hi-fileBrowser/components/ContextDrawer";
import fileBrowserReducer from "../../../redux/reducers/filebrowser.reducer";
import { fileBrowserActions } from "../../../redux/actions/filebrowser.actions";
import requests from "../../../base/requests";

jest.setTimeout(30000);

jest.mock("../../../redux/actions", () => ({
  fileBrowserActions: jest.requireActual("../../../redux/actions/filebrowser.actions").fileBrowserActions,
}));

jest.mock("../../../base/requests", () => ({
  __esModule: true,
  default: { filebrowser: jest.fn() },
}));

jest.mock("../../../components/hi-notifications/notify", () => ({
  __esModule: true,
  default: () => ({}),
}));

jest.mock("../../../components/hi-fileBrowser/components", () => ({
  get EditFolderForm() {
    return require("../../../components/hi-fileBrowser/components/EditFolderForm").EditFolderForm;
  },
  get SelectedItemBreadcrumb() {
    return require("../../../components/hi-fileBrowser/components/SelectedItemBreadcrumb").SelectedItemBreadcrumb;
  },
  SaveFileForm: () => null,
}));

const folder = { type: "folder", name: "Original folder", path: "Original folder", children: [] };
const file = { type: "file", name: "report.metadata", title: "Original report", path: "report.metadata" };
let postContextMenuOperations;

const renderDrawer = (files = [folder, file]) => {
  const store = configureStore({
    reducer: {
      fileBrowser: fileBrowserReducer,
      app: (state = { applicationSettingsData: { settings: {} } }) => state,
    },
    preloadedState: {
      fileBrowser: { ...fileBrowserReducer(undefined, {}), files: { loading: false, data: files, error: null } },
    },
  });
  render(<Provider store={store}><ContextDrawer /></Provider>);
  return store;
};

const openRename = async (store, record) => {
  act(() => {
    store.dispatch(fileBrowserActions.setContextMenuItemDetails({ contextItem: "Rename", clickedRecord: record }));
  });
  const input = await screen.findByLabelText("Name");
  await waitFor(() => expect(input).toHaveFocus());
  return input;
};

beforeEach(() => {
  postContextMenuOperations = jest.fn();
  requests.filebrowser.mockReturnValue({ postContextMenuOperations });
});

describe("Rename drawer", () => {
  test.each([
    ["folder", folder, "Original folder"],
    ["file", file, "Original report"],
  ])("focuses the %s name and accepts typing without a click", async (_type, record, name) => {
    const store = renderDrawer();
    const input = await openRename(store, record);
    expect(input).toHaveValue(name);
    userEvent.type(input, " edited", { skipClick: true });
    expect(input).toHaveValue(`${name} edited`);
  });

  test("focuses the correct name when reopened for another item after cancel", async () => {
    const store = renderDrawer();
    const input = await openRename(store, folder);
    userEvent.type(input, " edited", { skipClick: true });
    userEvent.click(screen.getByRole("button", { name: "Cancel" }));
    await waitFor(() => expect(screen.queryByLabelText("Name")).not.toBeInTheDocument());
    expect(postContextMenuOperations).not.toHaveBeenCalled();
    expect(store.getState().fileBrowser.files.data[0].name).toBe(folder.name);

    expect(await openRename(store, file)).toHaveValue(file.title);
    userEvent.click(screen.getByRole("button", { name: "Cancel" }));
    await waitFor(() => expect(screen.queryByLabelText("Name")).not.toBeInTheDocument());
    expect(await openRename(store, file)).toHaveValue(file.title);
  });

  test("submits a valid new name and closes the drawer", async () => {
    postContextMenuOperations.mockImplementation((_data, onSuccess) => onSuccess({}));
    const store = renderDrawer();
    const input = await openRename(store, folder);
    userEvent.clear(input);
    userEvent.type(input, "Renamed folder", { skipClick: true });
    userEvent.click(screen.getByRole("button", { name: "Submit" }));

    await waitFor(() => expect(postContextMenuOperations).toHaveBeenCalledWith(
      { action: "rename", sourceArray: JSON.stringify([[folder.path, "Renamed folder"]]) },
      expect.any(Function),
      expect.any(Function)
    ));
    await waitFor(() => expect(screen.queryByLabelText("Name")).not.toBeInTheDocument());
    expect(store.getState().fileBrowser.files.data[0].name).toBe("Renamed folder");
  });

  test("rejects an invalid name without submitting", async () => {
    const store = renderDrawer();
    const input = await openRename(store, file);
    userEvent.clear(input);
    userEvent.type(input, "ab", { skipClick: true });
    userEvent.click(screen.getByRole("button", { name: "Submit" }));

    expect(await screen.findByText("Name must be at least three characters long.")).toBeInTheDocument();
    expect(postContextMenuOperations).not.toHaveBeenCalled();
    expect(screen.getByLabelText("Name")).toHaveValue("ab");
  });
});
