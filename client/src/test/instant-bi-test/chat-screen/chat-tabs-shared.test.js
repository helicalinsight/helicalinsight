import { getSharedChatTabsShell } from "../../../components/hi-instant-bi/components/chat-screen/chat-tabs-shared";

describe("getSharedChatTabsShell - workings visibility", () => {
  test("shows workings in create mode when streaming is enabled", () => {
    const shell = getSharedChatTabsShell({
      isOpenMode: false,
      isEditMode: false,
      streamingEnabled: true,
      workingLines: ["Planning", "Executing"],
      workingActive: true,
    });

    expect(shell.showWorkingTab).toBe(true);
    expect(shell.workingLines).toEqual(["Planning", "Executing"]);
    expect(shell.workingActive).toBe(true);
  });

  test("hides workings in edit mode even when streaming is enabled", () => {
    const shell = getSharedChatTabsShell({
      isOpenMode: false,
      isEditMode: true,
      streamingEnabled: true,
      workingLines: ["Planning", "Executing"],
      workingActive: true,
    });

    expect(shell.showWorkingTab).toBe(false);
    expect(shell.workingLines).toEqual([]);
    expect(shell.workingActive).toBe(false);
  });

  test("hides workings in open mode", () => {
    const shell = getSharedChatTabsShell({
      isOpenMode: true,
      isEditMode: false,
      streamingEnabled: true,
      workingLines: ["Planning"],
      workingActive: true,
    });

    expect(shell.showWorkingTab).toBe(false);
    expect(shell.workingLines).toEqual([]);
    expect(shell.workingActive).toBe(false);
  });

  test("hides workings when streaming is disabled", () => {
    const shell = getSharedChatTabsShell({
      isOpenMode: false,
      isEditMode: false,
      streamingEnabled: false,
      workingLines: ["Planning"],
      workingActive: true,
    });

    expect(shell.showWorkingTab).toBe(false);
    expect(shell.workingLines).toEqual([]);
    expect(shell.workingActive).toBe(false);
  });
});
