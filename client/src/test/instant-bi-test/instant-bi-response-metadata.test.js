import "core-js/stable";
import "regenerator-runtime/runtime";
import { render, screen } from "@testing-library/react";
import InstantBIResponseMetadata, {
  getInstantBIResponseMetadata,
} from "../../components/hi-instant-bi/components/instant-bi-response-metadata";

const sampleSqlDetails = {
  raw_sql: "```sql\nSELECT 1",
  dialect: "postgresql",
  required_domain: ["Sales Operation"],
  required_topic: ["Travel"],
  required_table: ["employee_details", "travel_details"],
  required_column: [
    "travel_details.destination",
    "travel_details.travel_medium",
  ],
  required_join: "",
  reason: "Group by destination to find popular travel patterns.",
};

const sampleVizDetails = {
  chart_name: "bar",
  vf_title: "Popular Travel Destinations",
};

const sampleTokenUsage = {
  input_tokens: 3172,
  output_tokens: 376,
  total_tokens: 3548,
};

describe("getInstantBIResponseMetadata", () => {
  it("it should excludes raw_sql & empty values from sql details", () => {
    const metadata = getInstantBIResponseMetadata({ sqlDetails: sampleSqlDetails });
    expect(metadata).not.toHaveProperty("raw_sql");
    expect(metadata).not.toHaveProperty("required_join");
    expect(metadata.dialect).toBe("postgresql");
    expect(metadata.required_domain).toEqual(["Sales Operation"]);
    expect(metadata.required_table).toHaveLength(2);
  });

  it("should merges chart_name from vizDetails", () => {
    const metadata = getInstantBIResponseMetadata({
      sqlDetails: sampleSqlDetails,
      vizDetails: sampleVizDetails,
    });
    expect(metadata.chart_name).toBe("bar");
  });

  it("should returns chart_name only when sql details are emmpty", () => {
    const metadata = getInstantBIResponseMetadata({
      sqlDetails: {},
      vizDetails: sampleVizDetails,
    });
    expect(metadata).toEqual({ chart_name: "bar" });
  });

  it("should merges token_usage from tokenUsage", () => {
    const metadata = getInstantBIResponseMetadata({
      sqlDetails: sampleSqlDetails,
      tokenUsage: sampleTokenUsage,
    });
    expect(metadata.token_usage).toEqual(sampleTokenUsage);
  });

  it("should omits chart_name when vizDetails does not provide it", () => {
    const metadata = getInstantBIResponseMetadata({
      sqlDetails: sampleSqlDetails,
      vizDetails: {},
    });
    expect(metadata).not.toHaveProperty("chart_name");
  });
});

describe("InstantBIResponseMetadata", () => {
  it("should renders chart name in the footer only", () => {
    render(
      <InstantBIResponseMetadata
        sqlDetails={sampleSqlDetails}
        vizDetails={sampleVizDetails}
      />
    );
    expect(screen.getByText(/chart:/i)).toBeTruthy();
    expect(screen.getByText("bar")).toBeTruthy();
    expect(screen.queryByText("Chart Name")).toBeNull();
  });

  it("it should render sql metadata section & footer ", () => {
    render(
      <InstantBIResponseMetadata
        sqlDetails={sampleSqlDetails}
        vizDetails={sampleVizDetails}
      />
    );
    expect(screen.getByText("Domain")).toBeTruthy();
    expect(screen.getByText("Topic")).toBeTruthy();
    expect(screen.getByText("Tables")).toBeTruthy();
    expect(screen.getByText("Columns")).toBeTruthy();
  });

  it("it should return null when there is no metadata to show", () => {
    const { container } = render(
      <InstantBIResponseMetadata sqlDetails={{}} vizDetails={{}} />
    );
    expect(container.firstChild).toBeNull();
  });

  it("should render token usage in footer", () => {
    const fullTokenUsage = {
      ...sampleTokenUsage,
      input_cost: null,
      output_cost: null,
      total_cost: null,
    };
    render(
      <InstantBIResponseMetadata
        sqlDetails={sampleSqlDetails}
        vizDetails={sampleVizDetails}
        tokenUsage={fullTokenUsage}
      />
    );
    expect(screen.getByText("Token Usage")).toBeTruthy();
    expect(screen.getByText("input tokens")).toBeTruthy();
    expect(screen.getByText("3548")).toBeTruthy();
    expect(screen.getByText("input cost")).toBeTruthy();
    expect(screen.getAllByText("—")).toHaveLength(3);
    expect(screen.queryByText(/input tokens:/i)).toBeNull();
  });

  it("should sshows chart in footer when only viz chart_name is present", () => {
    render(
      <InstantBIResponseMetadata
        sqlDetails={{}}
        vizDetails={sampleVizDetails}
      />
    );
    expect(screen.getByText(/chart:/i)).toBeTruthy();
    expect(screen.getByText("bar")).toBeTruthy();
    expect(screen.queryByText("Domain")).toBeNull();
  });
});
