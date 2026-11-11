/**
 * Shared ChatTabs shell — normal/fast and think steps use the same icon section.
 * Mode-specific fields (data, sql, viz, handlers) are merged by the caller.
 */
export const getSharedChatTabsShell = ({
  isOpenMode = false,
  isEditMode = false,
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
  // Working is the streamed activity trail. Hide it outside create mode.
  showWorkingTab: !isOpenMode && !isEditMode && Boolean(streamingEnabled),
  workingLines: !isOpenMode && !isEditMode && streamingEnabled ? workingLines : [],
  workingActive: Boolean(!isOpenMode && !isEditMode && streamingEnabled && workingActive),
  workingQuestion,
  workingFallback,
  showDataInsightButton,
  isOpenMode,
  showDashboardTab: false,
  showActivityDetailsTab: false,
});
