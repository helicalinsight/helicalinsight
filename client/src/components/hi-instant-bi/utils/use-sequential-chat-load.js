import { useCallback, useEffect, useRef, useState } from "react";
import notify from "../../hi-notifications/notify";
import { loadInstantBIOpenChat } from "./instant-bi-requests";

export const useSequentialChatLoad = ({
  messages = [],
  activeReport = {},
  reportId,
  dispatch,
  isOpenMode,
  isEditMode,
}) => {
  const [loadingSequenceId, setLoadingSequenceId] = useState(null);
  const [skippedSequenceIds, setSkippedSequenceIds] = useState([]);
  const [abortedSequenceIds, setAbortedSequenceIds] = useState([]);
  const queueRef = useRef([]);
  const isProcessingRef = useRef(false);
  const apiRef = useRef(null);
  const abortedRef = useRef(false);
  const initialLatestQueuedRef = useRef(false);
  const loadedChatResponsesRef = useRef({});
  const skippedSequenceIdsRef = useRef(new Set());
  const abortedSequenceIdsRef = useRef(new Set());
  const activeRequestIdRef = useRef(0);
  const inFlightSequenceIdRef = useRef(null);

  const { reportInfo = {}, loadedChatResponses = {} } = activeReport || {};
  const dynamicFileName =
    reportInfo?.uuid ||
    (reportInfo?.reportName && `${reportInfo.reportName}.instant`);
  const isScrollLoadMode = isOpenMode || isEditMode;

  loadedChatResponsesRef.current = loadedChatResponses;

  const isCached = useCallback((sequenceId) => {
    return Boolean(loadedChatResponsesRef.current?.[sequenceId]);
  }, []);

  const isSkipped = useCallback((sequenceId) => {
    return skippedSequenceIdsRef.current.has(sequenceId);
  }, []);

  const getBotMessages = useCallback(() => {
    return messages.filter(
      (message) =>
        !message.isUser && message.chatSequenceId && message.needsLoadChat
    );
  }, [messages]);

  const enqueue = useCallback(
    (sequenceId) => {
      if (!sequenceId || isCached(sequenceId) || isSkipped(sequenceId)) return;
      if (abortedSequenceIdsRef.current.has(sequenceId)) return;
      if (inFlightSequenceIdRef.current === sequenceId) return;
      if (queueRef.current.includes(sequenceId)) return;
      queueRef.current.push(sequenceId);
      queueRef.current.sort((a, b) => b - a);
    },
    [isCached, isSkipped]
  );

  const finishRequest = useCallback(
    (requestId) => {
      if (requestId !== activeRequestIdRef.current) return false;
      abortedRef.current = false;
      isProcessingRef.current = false;
      inFlightSequenceIdRef.current = null;
      setLoadingSequenceId(null);
      apiRef.current = null;
      return true;
    },
    []
  );

  const processQueue = useCallback(() => {
    if (!isScrollLoadMode) return;
    if (isProcessingRef.current || queueRef.current.length === 0) return;
    if (!reportInfo?.location || !dynamicFileName || !reportId) return;

    const sequenceId = queueRef.current[0];
    if (isCached(sequenceId)) {
      queueRef.current.shift();
      processQueue();
      return;
    }

    const message = getBotMessages().find(
      (item) => item.chatSequenceId === sequenceId
    );
    if (!message?.userInput) {
      queueRef.current.shift();
      processQueue();
      return;
    }

    const requestId = ++activeRequestIdRef.current;
    isProcessingRef.current = true;
    inFlightSequenceIdRef.current = sequenceId;
    setLoadingSequenceId(sequenceId);
    abortedRef.current = false;
    const Notify = notify(dispatch);

    apiRef.current = loadInstantBIOpenChat({
      dispatch,
      reportId,
      chatSequenceId: sequenceId,
      userInput: message.userInput,
      location: reportInfo.location,
      fileName: dynamicFileName,
      source: "scroll-load",
      showSuccessNotification: false,
      Notify,
      abortedRef,
      onComplete: ({ success, response, aborted } = {}) => {
        if (!finishRequest(requestId)) return;
        const wasAborted = aborted;
        if (success) {
          const chatResponse = response?.chat_response;
          if (chatResponse) {
            loadedChatResponsesRef.current = {
              ...loadedChatResponsesRef.current,
              [sequenceId]: chatResponse,
            };
          }
        } else if (!wasAborted) {
          skippedSequenceIdsRef.current.add(sequenceId);
          setSkippedSequenceIds(Array.from(skippedSequenceIdsRef.current));
        }

        if (queueRef.current[0] === sequenceId) {
          queueRef.current.shift();
        }
        processQueue();
      },
    });
  }, [
    isScrollLoadMode,
    reportInfo?.location,
    dynamicFileName,
    reportId,
    dispatch,
    getBotMessages,
    isCached,
    finishRequest,
  ]);

  const requestLoad = useCallback(
    (sequenceId) => {
      if (!isScrollLoadMode) return;
      enqueue(sequenceId);
      if (!isProcessingRef.current) {
        processQueue();
      }
    },
    [isScrollLoadMode, enqueue, processQueue]
  );

  const retryLoad = useCallback(
    (sequenceId) => {
      if (!isScrollLoadMode || !sequenceId) return;
      abortedSequenceIdsRef.current.delete(sequenceId);
      setAbortedSequenceIds(Array.from(abortedSequenceIdsRef.current));
      skippedSequenceIdsRef.current.delete(sequenceId);
      setSkippedSequenceIds(Array.from(skippedSequenceIdsRef.current));
      enqueue(sequenceId);
      if (!isProcessingRef.current) {
        processQueue();
      }
    },
    [isScrollLoadMode, enqueue, processQueue]
  );

  const abortLoadChat = useCallback(() => {
    if (!isProcessingRef.current && !apiRef.current) return;

    abortedRef.current = true;
    activeRequestIdRef.current += 1;

    const abortedSequenceId =
      inFlightSequenceIdRef.current ?? queueRef.current[0] ?? loadingSequenceId;
    apiRef.current?.abort();
    apiRef.current = null;
    isProcessingRef.current = false;
    inFlightSequenceIdRef.current = null;
    setLoadingSequenceId(null);
    if (abortedSequenceId) {
      if (queueRef.current[0] === abortedSequenceId) {
        queueRef.current.shift();
      }
      abortedSequenceIdsRef.current.add(abortedSequenceId);
      setAbortedSequenceIds(Array.from(abortedSequenceIdsRef.current));
    }
    processQueue();
  }, [loadingSequenceId, processQueue]);

  const cancelAllLoads = useCallback(() => {
    if (apiRef.current) {
      abortedRef.current = true;
      activeRequestIdRef.current += 1;
      apiRef.current.abort();
    }
    initialLatestQueuedRef.current = false;
    queueRef.current = [];
    isProcessingRef.current = false;
    inFlightSequenceIdRef.current = null;
    abortedRef.current = false;
    apiRef.current = null;
    skippedSequenceIdsRef.current = new Set();
    abortedSequenceIdsRef.current = new Set();
    setSkippedSequenceIds([]);
    setAbortedSequenceIds([]);
    setLoadingSequenceId(null);
  }, []);

  useEffect(() => {
    cancelAllLoads();
  }, [reportId, cancelAllLoads]);

  useEffect(() => {
    if (!isScrollLoadMode) {
      cancelAllLoads();
    }
  }, [isScrollLoadMode, cancelAllLoads]);

  useEffect(() => {
    return () => {
      cancelAllLoads();
    };
  }, [cancelAllLoads]);

  useEffect(() => {
    if (!isScrollLoadMode || initialLatestQueuedRef.current) return;

    const botMessages = getBotMessages();
    if (!botMessages.length) return;

    initialLatestQueuedRef.current = true;
    const latestSequenceId = Math.max(
      ...botMessages.map((message) => message.chatSequenceId)
    );

    if (!isCached(latestSequenceId)) {
      enqueue(latestSequenceId);
      processQueue();
    }
  }, [isScrollLoadMode, getBotMessages, isCached, enqueue, processQueue]);

  return {
    requestLoad,
    retryLoad,
    loadingSequenceId,
    abortLoadChat,
    isScrollLoadMode,
    isCached,
    skippedSequenceIds,
    abortedSequenceIds,
  };
};
