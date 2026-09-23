import React, { useEffect, useRef } from "react";
import { LoadingOutlined } from "@ant-design/icons";
import ChatTypingText from "./chat-typing-text";

/**
 * Full-width Working panel. Keeps streamed activity paragraphs after the turn ends.
 */
const WorkBehindIcon = ({
  active = false,
  lines = [],
  question = "",
  fallback = "Working on this…",
  className = "",
  testId = "ib-work-behind-icon",
  title = "Working",
}) => {
  const bodyRef = useRef(null);
  const hasLines = Array.isArray(lines) && lines.length > 0;

  useEffect(() => {
    const el = bodyRef.current;
    if (!el) return;
    el.scrollTop = el.scrollHeight;
  }, [lines, active]);

  if (!active && !hasLines) {
    return null;
  }

  return (
    <div
      className={`ib-workings ${active ? "is-active" : ""} ${className}`.trim()}
      data-testid={testId}
    >
      <div className="ib-workings__header">
        <span className="ib-workings__title">{title}</span>
        {active ? (
          <LoadingOutlined spin className="ib-workings__spinner" aria-label="Working" />
        ) : null}
      </div>
      <div className="ib-workings__body" ref={bodyRef}>
        {hasLines ? (
          <div className="ib-workings__paragraphs" data-testid="ib-workings-paragraphs">
            {lines.map((line, index) => {
              const text = String(line || "").trim();
              if (!text) return null;
              const isLast = index === lines.length - 1;
              if (active && isLast) {
                return (
                  <p key={`${index}-${text.slice(0, 24)}`} className="ib-workings__p">
                    <ChatTypingText
                      lines={[text]}
                      question={question}
                      fallback={fallback}
                      active
                    />
                  </p>
                );
              }
              return (
                <p key={`${index}-${text.slice(0, 24)}`} className="ib-workings__p">
                  {text}
                </p>
              );
            })}
          </div>
        ) : (
          <p className="ib-workings__p ib-workings__p--muted">{fallback}</p>
        )}
      </div>
    </div>
  );
};

export default WorkBehindIcon;
