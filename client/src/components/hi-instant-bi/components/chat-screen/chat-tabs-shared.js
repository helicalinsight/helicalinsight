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
} = {}) => ({
  hasMessage,
  showMaximizeButton,
  isMaximized,
  setIsMaximized,
  defaultTab: "preview",
  previewFirst: true,
  semanticLabel: "Semantic",
  // Same icon row in create/edit for every mode (Preview / Semantic / SQL / Working).
  showWorkingTab: !isOpenMode,
  workingLines: isOpenMode ? [] : workingLines,
  workingActive: Boolean(!isOpenMode && workingActive),
  workingQuestion,
  workingFallback,
  showDataInsightButton,
  isOpenMode,
  showDashboardTab: false,
  showActivityDetailsTab: false,
});
