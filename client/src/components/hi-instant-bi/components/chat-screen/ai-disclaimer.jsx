import "./ai-disclaimer.scss";

const AI_DISCLAIMER_TEXT =
  "Instant BI uses AI and may generate inaccurate insights. Verify key findings.";

const AiDisclaimer = ({ className = "" }) => (
  <p
    data-testid="ib-ai-disclaimer"
    className={`ib-ai-disclaimer ${className}`.trim()}
  >
    {AI_DISCLAIMER_TEXT}
  </p>
);

export default AiDisclaimer;
