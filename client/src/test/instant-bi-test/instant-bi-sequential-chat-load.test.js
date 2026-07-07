import "core-js/stable";
import "regenerator-runtime/runtime";
import React from "react";
import { act, render, waitFor } from "@testing-library/react";
import { useSequentialChatLoad } from "../../components/hi-instant-bi/utils/use-sequential-chat-load";
import { loadInstantBIOpenChat } from "../../components/hi-instant-bi/utils/instant-bi-requests";

jest.mock("../../components/hi-instant-bi/utils/instant-bi-requests", () => ({
  loadInstantBIOpenChat: jest.fn(),
}));

jest.mock("../../components/hi-notifications/notify", () => () => ({
  error: jest.fn(),
  success: jest.fn(),
}));

const mockLoadInstantBIOpenChat = loadInstantBIOpenChat;

const buildBotMessage = (chatSequenceId, userInput = `input-${chatSequenceId}`) => ({
  isUser: false,
  needsLoadChat: true,
  chatSequenceId,
  userInput,
});

const defaultHookProps = {
  reportId: "report-1",
  dispatch: jest.fn(),
  isOpenMode: true,
  isEditMode: false,
  activeReport: {
    reportInfo: {
      location: "test-location",
      uuid: "report.instant",
    },
    loadedChatResponses: {},
  },
  messages: [
    { isUser: true, text: "question 1" },
    buildBotMessage(1),
    { isUser: true, text: "question 2" },
    buildBotMessage(2),
    { isUser: true, text: "question 3" },
    buildBotMessage(3),
  ],
};

let hookApi = null;

const HookProbe = (props) => {
  hookApi = useSequentialChatLoad(props);
  return null;
};

const renderHook = (overrideProps = {}) => {
  hookApi = null;
  const props = { ...defaultHookProps, ...overrideProps };
  render(<HookProbe {...props} />);
  return props;
};

const completeLoad =
  (overrides = {}) =>
  ({ onComplete, chatSequenceId }) => {
    onComplete({
      success: true,
      response: {
        chat_response: {
          summary: { insight: `insight-${chatSequenceId}` },
        },
      },
      ...overrides,
    });
  };

describe("useSequentialChatLoad", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockLoadInstantBIOpenChat.mockImplementation(completeLoad());
  });

  it("should auto-load the latest sequence on mount in open mode", async () => {
    renderHook();

    await waitFor(() => {
      expect(mockLoadInstantBIOpenChat).toHaveBeenCalledWith(
        expect.objectContaining({
          chatSequenceId: 3,
          userInput: "input-3",
          source: "scroll-load",
          showSuccessNotification: false,
        })
      );
    });
  });

  it("should not trigger loads in create mode", async () => {
    renderHook({ isOpenMode: false, isEditMode: false });

    await act(async () => {
      await Promise.resolve();
    });

    expect(mockLoadInstantBIOpenChat).not.toHaveBeenCalled();
    expect(hookApi.isScrollLoadMode).toBe(false);
  });

  it("should process queue one sequence at a time", async () => {
    const pendingCompletes = [];
    mockLoadInstantBIOpenChat.mockImplementation(({ onComplete }) => {
      pendingCompletes.push(onComplete);
    });

    renderHook();

    await waitFor(() => {
      expect(mockLoadInstantBIOpenChat).toHaveBeenCalledTimes(1);
    });

    act(() => {
      hookApi.requestLoad(2);
      hookApi.requestLoad(1);
    });

    expect(mockLoadInstantBIOpenChat).toHaveBeenCalledTimes(1);

    await act(async () => {
      pendingCompletes[0]({
        success: true,
        response: { chat_response: { summary: { insight: "insight-3" } } },
      });
    });

    await waitFor(() => {
      expect(mockLoadInstantBIOpenChat).toHaveBeenCalledTimes(2);
      expect(mockLoadInstantBIOpenChat).toHaveBeenLastCalledWith(
        expect.objectContaining({ chatSequenceId: 2 })
      );
    });
  });

  it("should skip failed sequences and continue loading earlier ones", async () => {
    mockLoadInstantBIOpenChat.mockImplementation(
      ({ chatSequenceId, onComplete }) => {
        if (chatSequenceId === 2) {
          onComplete({ success: false });
          return;
        }
        onComplete({
          success: true,
          response: {
            chat_response: {
              summary: { insight: `insight-${chatSequenceId}` },
            },
          },
        });
      }
    );

    renderHook();

    await waitFor(() => {
      expect(mockLoadInstantBIOpenChat).toHaveBeenCalledWith(
        expect.objectContaining({ chatSequenceId: 3 })
      );
    });

    act(() => {
      hookApi.requestLoad(2);
      hookApi.requestLoad(1);
    });

    await waitFor(() => {
      expect(mockLoadInstantBIOpenChat).toHaveBeenCalledWith(
        expect.objectContaining({ chatSequenceId: 2 })
      );
    });

    await waitFor(() => {
      expect(mockLoadInstantBIOpenChat).toHaveBeenCalledWith(
        expect.objectContaining({ chatSequenceId: 1 })
      );
      expect(hookApi.skippedSequenceIds).toContain(2);
    });
  });

  it("should not re-enqueue cached sequences", async () => {
    renderHook({
      activeReport: {
        reportInfo: {
          location: "test-location",
          uuid: "report.instant",
        },
        loadedChatResponses: {
          3: { summary: { insight: "cached" } },
        },
      },
    });

    await act(async () => {
      await Promise.resolve();
    });

    expect(mockLoadInstantBIOpenChat).not.toHaveBeenCalled();

    act(() => {
      hookApi.requestLoad(3);
    });

    expect(mockLoadInstantBIOpenChat).not.toHaveBeenCalled();
  });

  it("should not re-enqueue skipped sequences after a failed load", async () => {
    mockLoadInstantBIOpenChat.mockImplementation(
      ({ chatSequenceId, onComplete }) => {
        if (chatSequenceId === 2) {
          onComplete({ success: false });
          return;
        }
        onComplete({
          success: true,
          response: {
            chat_response: {
              summary: { insight: `insight-${chatSequenceId}` },
            },
          },
        });
      }
    );

    renderHook();

    act(() => {
      hookApi.requestLoad(2);
    });

    await waitFor(() => {
      expect(hookApi.skippedSequenceIds).toContain(2);
    });

    mockLoadInstantBIOpenChat.mockClear();

    act(() => {
      hookApi.requestLoad(2);
    });

    expect(mockLoadInstantBIOpenChat).not.toHaveBeenCalled();
  });

  it("should abort the current load-chat request without marking it as failed", async () => {
    const mockAbort = jest.fn();
    mockLoadInstantBIOpenChat.mockImplementation(({ onComplete }) => ({
      abort: () => {
        mockAbort();
        onComplete({ success: false, aborted: true });
      },
    }));
    renderHook();
    await waitFor(() => {
      expect(hookApi.loadingSequenceId).toBe(3);
    });
    act(() => {
      hookApi.abortLoadChat();
    });
    expect(mockAbort).toHaveBeenCalled();
    expect(hookApi.loadingSequenceId).toBeNull();
    expect(hookApi.skippedSequenceIds).not.toContain(3);
    expect(hookApi.abortedSequenceIds).toContain(3);
  });

  it("should continue the queue after an aborted load-chat request", async () => {
    mockLoadInstantBIOpenChat.mockImplementation(
      ({ chatSequenceId, onComplete }) => ({
        abort: () => onComplete({ success: false, aborted: true }),
      })
    );
    renderHook();
    await waitFor(() => {
      expect(mockLoadInstantBIOpenChat).toHaveBeenCalledTimes(1);
    });
    act(() => {
      hookApi.requestLoad(2);
      hookApi.requestLoad(1);
    });
    await act(async () => {
      hookApi.abortLoadChat();
    });
    await waitFor(() => {
      expect(mockLoadInstantBIOpenChat).toHaveBeenCalledTimes(2);
      expect(mockLoadInstantBIOpenChat).toHaveBeenLastCalledWith(
        expect.objectContaining({ chatSequenceId: 2 })
      );
      expect(hookApi.skippedSequenceIds).not.toContain(3);
    });
  });

  it("should continue with remaining sequences after abort without restarting higher ones", async () => {
    const pendingCompletes = [];
    mockLoadInstantBIOpenChat.mockImplementation(({ onComplete, chatSequenceId }) => {
      pendingCompletes.push({ onComplete, chatSequenceId });
    });

    const messages = [];
    for (let sequenceId = 1; sequenceId <= 9; sequenceId += 1) {
      messages.push({ isUser: true, text: `question ${sequenceId}` });
      messages.push(buildBotMessage(sequenceId));
    }

    renderHook({ messages });

    await waitFor(() => {
      expect(mockLoadInstantBIOpenChat).toHaveBeenCalledWith(
        expect.objectContaining({ chatSequenceId: 9 })
      );
    });

    for (let sequenceId = 9; sequenceId >= 7; sequenceId -= 1) {
      await act(async () => {
        pendingCompletes
          .find((item) => item.chatSequenceId === sequenceId)
          ?.onComplete({
            success: true,
            response: {
              chat_response: {
                summary: { insight: `insight-${sequenceId}` },
              },
            },
          });
      });
    }

    act(() => {
      for (let sequenceId = 6; sequenceId >= 1; sequenceId -= 1) {
        hookApi.requestLoad(sequenceId);
      }
    });

    await waitFor(() => {
      expect(mockLoadInstantBIOpenChat).toHaveBeenLastCalledWith(
        expect.objectContaining({ chatSequenceId: 6 })
      );
    });

    mockLoadInstantBIOpenChat.mockClear();

    await act(async () => {
      hookApi.abortLoadChat();
    });

    await waitFor(() => {
      expect(mockLoadInstantBIOpenChat).toHaveBeenCalledTimes(1);
      expect(mockLoadInstantBIOpenChat).toHaveBeenCalledWith(
        expect.objectContaining({ chatSequenceId: 5 })
      );
    });

    act(() => {
      hookApi.requestLoad(9);
      hookApi.requestLoad(8);
      hookApi.requestLoad(7);
      hookApi.requestLoad(6);
    });

    expect(mockLoadInstantBIOpenChat).toHaveBeenCalledTimes(1);
    expect(hookApi.skippedSequenceIds).not.toContain(6);
  });

  it("should process only one remaining sequence at a time after abort", async () => {
    const pendingCompletes = [];
    mockLoadInstantBIOpenChat.mockImplementation(({ onComplete, chatSequenceId }) => {
      pendingCompletes.push({ onComplete, chatSequenceId });
    });

    const messages = [];
    for (let sequenceId = 1; sequenceId <= 4; sequenceId += 1) {
      messages.push({ isUser: true, text: `question ${sequenceId}` });
      messages.push(buildBotMessage(sequenceId));
    }

    renderHook({ messages });

    await waitFor(() => {
      expect(mockLoadInstantBIOpenChat).toHaveBeenCalledWith(
        expect.objectContaining({ chatSequenceId: 4 })
      );
    });

    act(() => {
      hookApi.requestLoad(3);
      hookApi.requestLoad(2);
      hookApi.requestLoad(1);
    });

    await act(async () => {
      pendingCompletes
        .find((item) => item.chatSequenceId === 4)
        ?.onComplete({
          success: true,
          response: {
            chat_response: {
              summary: { insight: "insight-4" },
            },
          },
        });
    });

    await waitFor(() => {
      expect(mockLoadInstantBIOpenChat).toHaveBeenLastCalledWith(
        expect.objectContaining({ chatSequenceId: 3 })
      );
    });

    mockLoadInstantBIOpenChat.mockClear();
    pendingCompletes.length = 0;

    await act(async () => {
      hookApi.abortLoadChat();
    });

    await waitFor(() => {
      expect(mockLoadInstantBIOpenChat).toHaveBeenCalledTimes(1);
      expect(mockLoadInstantBIOpenChat).toHaveBeenCalledWith(
        expect.objectContaining({ chatSequenceId: 2 })
      );
    });

    act(() => {
      hookApi.requestLoad(1);
      hookApi.requestLoad(2);
      hookApi.requestLoad(3);
    });

    expect(mockLoadInstantBIOpenChat).toHaveBeenCalledTimes(1);
  });

  it("should stop after remaining sequences finish when an earlier sequence was aborted", async () => {
    const pendingCompletes = [];
    mockLoadInstantBIOpenChat.mockImplementation(({ onComplete, chatSequenceId }) => {
      pendingCompletes.push({ onComplete, chatSequenceId });
    });

    const messages = [];
    for (let sequenceId = 1; sequenceId <= 9; sequenceId += 1) {
      messages.push({ isUser: true, text: `question ${sequenceId}` });
      messages.push(buildBotMessage(sequenceId));
    }

    renderHook({ messages });

    await waitFor(() => {
      expect(mockLoadInstantBIOpenChat).toHaveBeenCalledWith(
        expect.objectContaining({ chatSequenceId: 9 })
      );
    });

    act(() => {
      for (let sequenceId = 8; sequenceId >= 1; sequenceId -= 1) {
        hookApi.requestLoad(sequenceId);
      }
    });

    await act(async () => {
      hookApi.abortLoadChat();
    });

    await waitFor(() => {
      expect(mockLoadInstantBIOpenChat).toHaveBeenLastCalledWith(
        expect.objectContaining({ chatSequenceId: 8 })
      );
    });

    for (let sequenceId = 8; sequenceId >= 1; sequenceId -= 1) {
      await act(async () => {
        pendingCompletes
          .find((item) => item.chatSequenceId === sequenceId)
          ?.onComplete({
            success: true,
            response: {
              chat_response: {
                summary: { insight: `insight-${sequenceId}` },
              },
            },
          });
      });
    }

    mockLoadInstantBIOpenChat.mockClear();

    await act(async () => {
      await Promise.resolve();
      hookApi.requestLoad(9);
    });

    expect(mockLoadInstantBIOpenChat).not.toHaveBeenCalled();
  });

  it("should retry an aborted sequence and re trigger the load", async () => {
    const mockAbort = jest.fn();
    mockLoadInstantBIOpenChat.mockImplementation(({ onComplete }) => ({
      abort: () => {
        mockAbort();
        onComplete({ success: false, aborted: true });
      },
    }));
    renderHook();
    await waitFor(() => {
      expect(hookApi.loadingSequenceId).toBe(3);
    });
    act(() => {
      hookApi.abortLoadChat();
    });

    expect(hookApi.abortedSequenceIds).toContain(3);
    mockLoadInstantBIOpenChat.mockImplementation(completeLoad());
    mockLoadInstantBIOpenChat.mockClear();
    act(() => {
      hookApi.retryLoad(3);
    });
    await waitFor(() => {
      expect(mockLoadInstantBIOpenChat).toHaveBeenCalledWith(
        expect.objectContaining({ chatSequenceId: 3 })
      );
      expect(hookApi.abortedSequenceIds).not.toContain(3);
    });
  });

  it("should retry a failed sequence and ree trigger the load", async () => {
    mockLoadInstantBIOpenChat.mockImplementation(({ chatSequenceId, onComplete }) => {
      if (chatSequenceId === 3) {
        onComplete({ success: false });
        return;
      }
      onComplete({
        success: true,
        response: {
          chat_response: {
            summary: { insight: `insight-${chatSequenceId}` },
          },
        },
      });
    });
    renderHook();
    await waitFor(() => {
      expect(hookApi.skippedSequenceIds).toContain(3);
    });
    mockLoadInstantBIOpenChat.mockImplementation(completeLoad());
    mockLoadInstantBIOpenChat.mockClear();
    act(() => {
      hookApi.retryLoad(3);
    });
    await waitFor(() => {
      expect(mockLoadInstantBIOpenChat).toHaveBeenCalledWith(
        expect.objectContaining({ chatSequenceId: 3 })
      );
      expect(hookApi.skippedSequenceIds).not.toContain(3);
    });
  });

  it("should abort in flight loads when exiting scroll load mode", async () => {
    const mockAbort = jest.fn();
    mockLoadInstantBIOpenChat.mockImplementation(({ onComplete }) => ({
      abort: mockAbort,
    }));
    const props = { ...defaultHookProps, isOpenMode: false, isEditMode: true };
    const { rerender, unmount } = render(<HookProbe {...props} />);
    await waitFor(() => {
      expect(hookApi.loadingSequenceId).toBe(3);
    });
    rerender(<HookProbe {...props} isOpenMode={false} isEditMode={false} />);
    expect(mockAbort).toHaveBeenCalled();
    expect(hookApi.loadingSequenceId).toBeNull();
    mockLoadInstantBIOpenChat.mockClear();
    act(() => {
      hookApi.requestLoad(2);
    });
    expect(mockLoadInstantBIOpenChat).not.toHaveBeenCalled();
    unmount();
  });
});
