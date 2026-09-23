import {
  appendActivityLine,
  buildChatActivityLines,
  buildChatActivityScript,
  quoteUserQuestion,
  resolveActivityLines,
} from "../../components/hi-instant-bi/utils/chat-activity-script";

describe("chat activity script", () => {
  test("quotes the user question in the opening line", () => {
    const lines = buildChatActivityLines("total travel cost by travel type");
    expect(lines[0]).toContain("total travel cost by travel type");
    expect(lines[0]).toMatch(/^Hi, you want to understand/);
  });

  test("walks through query, data, and visualization", () => {
    const script = buildChatActivityScript("sales by region");
    expect(script).toContain("query the database");
    expect(script).toContain("Executing your query");
    expect(script).toContain("Got your data");
    expect(script).toContain("visualization");
  });

  test("shortens a long question", () => {
    const longQuestion = "a ".repeat(60);
    const quoted = quoteUserQuestion(longQuestion);
    expect(quoted.endsWith("…")).toBe(true);
    expect(quoted.length).toBeLessThanOrEqual(80);
  });

  test("uses live trail lines when they arrive", () => {
    expect(resolveActivityLines(["Got your data."], "sales")).toEqual(["Got your data."]);
  });

  test("falls back to the opening line until the first progress event", () => {
    const [opening] = resolveActivityLines([], "sales by region");
    expect(opening).toContain("sales by region");
    expect(opening).toMatch(/^Hi, you want to understand/);
  });

  test("does not repeat the same activity line twice", () => {
    const first = appendActivityLine([], "Got your data.");
    const second = appendActivityLine(first, "Got your data.");
    expect(second).toEqual(["Got your data."]);
  });
});
