/**
 * Shared ChatTabs shell — normal/fast and think steps use the same icon section.
 * Mode-specific fields (data, sql, viz, handlers) are merged by the caller.
 */
export const getSharedChatTabsShell = ({
  isOpenMode = false,
  hasMessage = false,
  showMaximizeButton = false,
  isMaximized = false,
  setIsMaximized,
  showDataInsightButton = false,
  workingLines = [],
  workingActive = false,
  workingQuestion = "",
  workingFallback = "No workings recorded for this response.",
  streamingEnabled = false,
} = {}) => ({
  hasMessage,
  showMaximizeButton,
  isMaximized,
  setIsMaximized,
  defaultTab: "preview",
  previewFirst: true,
  semanticLabel: "Semantic",
  // Working is the streamed activity trail. Hide it when streaming is off.
  showWorkingTab: !isOpenMode && Boolean(streamingEnabled),
  workingLines: !isOpenMode && streamingEnabled ? workingLines : [],
  workingActive: Boolean(!isOpenMode && streamingEnabled && workingActive),
  workingQuestion,
  workingFallback,
  showDataInsightButton,
  isOpenMode,
  showDashboardTab: false,
  showActivityDetailsTab: false,
});
