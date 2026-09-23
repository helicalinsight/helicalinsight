import InstantBIStreamHandler from "../../components/hi-instant-bi/utils/instant-bi-stream-handler";

describe("InstantBIStreamHandler", () => {
  test("collects progress trail messages", () => {
    const progress = [];
    const handler = new InstantBIStreamHandler({
      onProgress: (data) => progress.push(data.message),
    });
    handler.handleChunk({
      event: "progress",
      data: { stage: "sql", status: "started", message: "Generating SQL..." },
    });
    handler.handleChunk({
      event: "progress",
      data: { stage: "viz", status: "done", message: "Generated." },
    });
    expect(progress).toEqual(["Generating SQL...", "Generated."]);
  });

  test("unwraps complete envelope to today's JSON payload", () => {
    const completes = [];
    const handler = new InstantBIStreamHandler({
      onComplete: (payload) => completes.push(payload),
    });
    handler.handleChunk({
      event: "complete",
      data: { status: 1, response: { chat_response: { sql: { raw_sql: "select 1" } } } },
    });
    expect(completes[0].chat_response.sql.raw_sql).toBe("select 1");
  });

  test("parses abort error and leaves loaders to the caller", () => {
    const errors = [];
    const handler = new InstantBIStreamHandler({
      onError: (payload) => errors.push(payload),
    });
    handler.handleChunk({
      event: "error",
      data: { error: "Request has been cancelled.", aborted: true },
    });
    expect(errors[0].aborted).toBe(true);
    expect(errors[0].error).toBe("Request has been cancelled.");
  });

  test("parses string error payloads from HIStreamClient", () => {
    const errors = [];
    const handler = new InstantBIStreamHandler({
      onError: (payload) => errors.push(payload),
    });
    handler.handleChunk({
      event: "error",
      data: "{\"error\":\"Request has been cancelled.\",\"aborted\":true}",
    });
    expect(errors[0].aborted).toBe(true);
  });
});
