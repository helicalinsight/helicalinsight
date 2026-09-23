import { useEffect, useMemo, useState } from "react";
import { resolveActivityLines } from "../../utils/chat-activity-script";

const CHAR_DELAY_MS = 18;
const BREAK_DELAY_MS = 260;

const ChatTypingText = ({
  lines = [],
  question = "",
  fallback = "",
  active = true,
}) => {
  const trail = useMemo(
    () => resolveActivityLines(lines, question, fallback),
    [lines, question, fallback]
  );
  const completed = trail.slice(0, -1);
  const current = trail[trail.length - 1] || "";
  const [typed, setTyped] = useState("");

  useEffect(() => {
    if (!active || !current) {
      setTyped(current);
      return undefined;
    }
    setTyped("");
    let index = 0;
    let timer;
    const typeNext = () => {
      index += 1;
      setTyped(current.slice(0, index));
      if (index >= current.length) {
        return;
      }
      const justTyped = current.charAt(index - 1);
      const delay = justTyped === "\n" ? BREAK_DELAY_MS : CHAR_DELAY_MS;
      timer = setTimeout(typeNext, delay);
    };
    timer = setTimeout(typeNext, CHAR_DELAY_MS);
    return () => clearTimeout(timer);
  }, [active, current]);

  const prefix = completed.length ? `${completed.join("\n")}\n` : "";

  return (
    <span className="ib-typing-narrative" data-testid="ib-typing-narrative">
      {prefix}
      {typed}
      {active ? <span className="ib-typing-cursor" aria-hidden="true" /> : null}
    </span>
  );
};

export default ChatTypingText;
