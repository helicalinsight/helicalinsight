import { getFloatingType } from "../../../utils/filter-utils";

describe("test getFloatingType", () => {
  const makeField = (overrides = {}) => ({
    type: null,
    databaseFunction: null,
    aggregate: null,
    applyBeforeAggregate: false,
    floatingType: null,
    ...overrides,
  });

  describe("default behavior", () => {
    test("returns discrete and isMeasure=false for empty field", () => {
      expect(getFloatingType(makeField())).toEqual({
        isMeasure: false,
        floatingType: "discrete",
      });
    });

    test("returns discrete for non-numeric type", () => {
      expect(getFloatingType(makeField({ type: { dataType: "string" } }))).toEqual({
        isMeasure: false,
        floatingType: "discrete",
      });
    });
  });

  describe("type.dataType = numeric", () => {
    test("returns continous and isMeasure=true", () => {
      expect(getFloatingType(makeField({ type: { dataType: "numeric" } }))).toEqual({
        isMeasure: true,
        floatingType: "continous",
      });
    });
  });

  describe("databaseFunction", () => {
    test("returns continous when databaseFunction.returns is numeric", () => {
      expect(
        getFloatingType(makeField({ databaseFunction: { returns: "numeric", fn: "SUM" } }))
      ).toEqual({ isMeasure: true, floatingType: "continous" });
    });

    test("returns discrete when databaseFunction.returns is not numeric", () => {
      expect(
        getFloatingType(makeField({ databaseFunction: { returns: "string", fn: "CONCAT" } }))
      ).toEqual({ isMeasure: false, floatingType: "discrete" });
    });

    test("returns discrete when databaseFunction is empty object", () => {
      expect(getFloatingType(makeField({ databaseFunction: {} }))).toEqual({
        isMeasure: false,
        floatingType: "discrete",
      });
    });

    test("returns discrete when databaseFunction is null", () => {
      expect(getFloatingType(makeField({ databaseFunction: null }))).toEqual({
        isMeasure: false,
        floatingType: "discrete",
      });
    });
  });

  describe("aggregate", () => {
    test("returns continous when aggregate is non-empty and applyBeforeAggregate=false", () => {
      expect(
        getFloatingType(makeField({ aggregate: ["SUM"], applyBeforeAggregate: false }))
      ).toEqual({ isMeasure: true, floatingType: "continous" });
    });

    test("returns discrete when aggregate is non-empty but applyBeforeAggregate=true", () => {
      expect(
        getFloatingType(makeField({ aggregate: ["SUM"], applyBeforeAggregate: true }))
      ).toEqual({ isMeasure: false, floatingType: "discrete" });
    });

    test("returns discrete when aggregate is empty array", () => {
      expect(getFloatingType(makeField({ aggregate: [] }))).toEqual({
        isMeasure: false,
        floatingType: "discrete",
      });
    });

    test("returns discrete when aggregate is null", () => {
      expect(getFloatingType(makeField({ aggregate: null }))).toEqual({
        isMeasure: false,
        floatingType: "discrete",
      });
    });
  });

  describe("field floatingType override", () => {
    test("overrides to discrete even when type is numeric", () => {
      expect(
        getFloatingType(makeField({ type: { dataType: "numeric" }, floatingType: "discrete" }))
      ).toEqual({ isMeasure: true, floatingType: "discrete" });
    });

    test("overrides to continous even when nothing else sets it", () => {
      expect(getFloatingType(makeField({ floatingType: "continous" }))).toEqual({
        isMeasure: true,
        floatingType: "continous",
      });
    });

    test("overrides to continous even when aggregate is blocked by applyBeforeAggregate", () => {
      expect(
        getFloatingType(
          makeField({ aggregate: ["SUM"], applyBeforeAggregate: true, floatingType: "continous" })
        )
      ).toEqual({ isMeasure: true, floatingType: "continous" });
    });
  });

  describe("isMeasure derived from final floatingType", () => {
    test("isMeasure=true whenever final floatingType is continous", () => {
      expect(getFloatingType(makeField({ floatingType: "continous" }))).toEqual({
        isMeasure: true,
        floatingType: "continous",
      });
    });

    test("isMeasure=false when floatingType override resets to discrete", () => {
      expect(
        getFloatingType(makeField({ aggregate: ["SUM"], floatingType: "discrete" }))
      ).toEqual({ isMeasure: true, floatingType: "discrete" });
    });
  });

  describe("combined conditions", () => {
    test("numeric type + non-numeric databaseFunction => continous", () => {
      expect(
        getFloatingType(
          makeField({ type: { dataType: "numeric" }, databaseFunction: { returns: "string" } })
        )
      ).toEqual({ isMeasure: true, floatingType: "continous" });
    });

    test("numeric databaseFunction + aggregate with applyBeforeAggregate=true => continous (from databaseFunction)", () => {
      expect(
        getFloatingType(
          makeField({
            databaseFunction: { returns: "numeric" },
            aggregate: ["SUM"],
            applyBeforeAggregate: true,
          })
        )
      ).toEqual({ isMeasure: true, floatingType: "continous" });
    });

    test("all conditions true, floatingType override to discrete => discrete", () => {
      expect(
        getFloatingType(
          makeField({
            type: { dataType: "numeric" },
            databaseFunction: { returns: "numeric" },
            aggregate: ["SUM"],
            applyBeforeAggregate: false,
            floatingType: "discrete",
          })
        )
      ).toEqual({ isMeasure: true, floatingType: "discrete" });
    });
  });
});