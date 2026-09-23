const QUESTION_LIMIT = 80;

export function quoteUserQuestion(question) {
  const cleaned = String(question || "").replace(/\s+/g, " ").trim();
  if (!cleaned) {
    return "your question";
  }
  if (cleaned.length <= QUESTION_LIMIT) {
    return cleaned;
  }
  return `${cleaned.slice(0, QUESTION_LIMIT - 1)}…`;
}

export function buildChatActivityLines(question) {
  const quoted = quoteUserQuestion(question);
  return [
    `Hi, you want to understand “${quoted}”. Let me find out what I can do.`,
    `I can query the database for this.`,
    `Looking for a query that fits… found one that looks suitable.`,
    `Executing your query now…`,
    `Got your data.`,
    `Finding which visualization suits this best… found it. Putting the chart together.`,
  ];
}

export function buildChatActivityScript(question) {
  return buildChatActivityLines(question).join("\n\n");
}

export function resolveActivityLines(lines = [], question = "", fallback = "") {
  if (Array.isArray(lines) && lines.length) {
    return lines;
  }
  if (fallback) {
    return [fallback];
  }
  if (question) {
    return [buildChatActivityLines(question)[0]];
  }
  return [];
}

export function appendActivityLine(lines = [], message) {
  const next = String(message || "").trim();
  if (!next) {
    return lines;
  }
  if (lines[lines.length - 1] === next) {
    return lines;
  }
  return [...lines, next];
}
