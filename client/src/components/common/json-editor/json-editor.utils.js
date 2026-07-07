import { useCallback, useState } from "react";

export function copyToClipboard(text) {
  if (navigator.clipboard) {
    return navigator.clipboard.writeText(text);
  }
  const textarea = Object.assign(document.createElement("textarea"), {
    value: text,
    style: { position: "fixed", left: "-9999px" },
  });
  document.body.appendChild(textarea);
  textarea.select();
  document.execCommand("copy");
  textarea.remove();
  return Promise.resolve();
}

export const extractMetadataReference = (data) => {
  if (!data || typeof data !== "object") {
    return null;
  }
  const meta = data.metadata;
  if (!meta || typeof meta !== "object" || Array.isArray(meta)) {
    return null;
  }
  const path = meta.location || meta.path;
  const fileName = meta.metadataFileName || meta.fileName || meta.file;
  if (path && fileName) {
    return { path, fileName };
  }
  return null;
};

export const unwrapStatePayload = (data) => {
  if (!data || typeof data !== "object") {
    return data;
  }

  if (data.state != null) {
    return typeof data.state === "string" ? JSON.parse(data.state) : data.state;
  }

  return data;
};

export const buildMetadataClipboardBlock = (metadataDetails) => {
  const path = metadataDetails?.path;
  const fileName = metadataDetails?.fileName;
  if (!path || !fileName) {
    return null;
  }

  return { location: path, metadataFileName: fileName };
};

export const resolveMetadataRefForPaste = (metadataRef, metadataDetails) => {
  if (metadataRef?.path && metadataRef?.fileName) {
    return metadataRef;
  }

  const path = metadataDetails?.path;
  const fileName = metadataDetails?.fileName;
  if (path && fileName) {
    return { path, fileName };
  }

  return null;
};

export const parseClipboardPayload = (rawText, { normalizeState }) => {
  const parsed = JSON.parse(rawText);
  const metadataRef = extractMetadataReference(parsed);
  const state = normalizeState(unwrapStatePayload(parsed));

  return { state, metadataRef };
};

export const buildClipboardPayload = (
  state,
  metadataDetails,
  { normalizeState, prettyPrint = true },
) => {
  const normalizedState = normalizeState(state);
  const metadata = buildMetadataClipboardBlock(metadataDetails);

  if (!metadata) {
    return prettyPrint
      ? JSON.stringify(normalizedState, null, 2)
      : JSON.stringify(normalizedState);
  }

  const payload = { metadata, state: normalizedState };
  return prettyPrint ? JSON.stringify(payload, null, 2) : JSON.stringify(payload);
};

export const parsePayloadForSave = (rawText, { normalizeState }) =>
  normalizeState(unwrapStatePayload(JSON.parse(rawText || "{}")));

export function useJsonClipboard({
  getPayload,
  applyPayload,
  onCopySuccess,
  onPasteError,
}) {
  const [pasteOpen, setPasteOpen] = useState(false);
  const [pasteText, setPasteText] = useState("");

  const handleCopy = useCallback(async () => {
    await copyToClipboard(getPayload());
    onCopySuccess?.();
  }, [getPayload, onCopySuccess]);

  const handleOpenPaste = useCallback(() => {
    setPasteText(getPayload());
    setPasteOpen(true);
  }, [getPayload]);

  const handlePasteLoad = useCallback(() => {
    try {
      applyPayload(pasteText);
      setPasteOpen(false);
    } catch (err) {
      onPasteError?.(err);
    }
  }, [applyPayload, pasteText, onPasteError]);

  const closePasteModal = useCallback(() => {
    setPasteOpen(false);
  }, []);

  const resetPasteModal = useCallback(() => {
    setPasteOpen(false);
    setPasteText("");
  }, []);

  return {
    pasteOpen,
    pasteText,
    setPasteText,
    setPasteOpen,
    handleCopy,
    handleOpenPaste,
    handlePasteLoad,
    closePasteModal,
    resetPasteModal,
  };
}
