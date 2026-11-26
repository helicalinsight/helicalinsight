import "./ai-disclaimer.scss";

const AI_DISCLAIMER_TEXT =
  "Instant BI uses AI and may generate inaccurate insights. Verify key findings.";
const SLASH_DISCLAIMER_TEXT =
  "Slash uses AI and may generate inaccurate insights. Verify key findings.";

// Refactored the single disclaimer string into a Slash wording and the original Instant BI wording.
const AiDisclaimer = ({ className = "", slashMode = false }) => (
  <p
    data-testid="ib-ai-disclaimer"
    className={`ib-ai-disclaimer ${className}`.trim()}
  >
    {slashMode ? SLASH_DISCLAIMER_TEXT : AI_DISCLAIMER_TEXT}
  </p>
);

export default AiDisclaimer;
