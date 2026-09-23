import React, { useMemo } from "react";
import Markdown from "react-markdown";
import remarkGfm from "remark-gfm";
import { CITATION_HREF_RE, linkifyThinkCitations } from "./think-plan-helpers";

const ThinkCitationMarkdown = ({ children, onCitationClick }) => {
  const components = useMemo(
    () => ({
      a: ({ href, children: linkChildren, ...props }) => {
        const match = String(href || "").match(CITATION_HREF_RE);
        if (match) {
          const stepIndex = Number(match[1]);
          return (
            <button
              type="button"
              className="ib-think-citation"
              data-testid={`ib-think-citation-${stepIndex}`}
              onClick={(event) => {
                event.preventDefault();
                onCitationClick?.(stepIndex);
              }}
            >
              {linkChildren}
            </button>
          );
        }
        return (
          <a href={href} {...props}>
            {linkChildren}
          </a>
        );
      },
    }),
    [onCitationClick],
  );

  return (
    <Markdown remarkPlugins={[remarkGfm]} components={components}>
      {linkifyThinkCitations(children)}
    </Markdown>
  );
};

export default ThinkCitationMarkdown;
