import { modifyFilters } from "../../../../utils/utilities";

describe("modifyFilters", () => {
    const state = {
      filters: [
        {
          id: "1",
          name: "Category",
          alias: "Category",
          dataType: "string",
          values: ["Shoes"],
        },
        {
          id: "2",
          name: "Price",
          alias: "Price",
          dataType: "numeric",
          values: [10],
        },
      ],
    };
  
    it("should return the state if parameters is empty", () => {
      const result = modifyFilters(state, {});
      expect(result).toEqual(state);
    });
  
    it("should modify the filters with numeric data type correctly", () => {
      const parameters = { Price: [20, 30] };
      const result = modifyFilters(state, parameters);
  
      expect(result.filters[1].values).toEqual([20, 30]);
    });
  
    it("should modify the filters with string data type correctly", () => {
      const parameters = { Category: ["Shoes", "Clothing"] };
      const result = modifyFilters(state, parameters);
  
      expect(result.filters[0].values).toEqual(["Shoes", "Clothing"]);
    });
  });

  describe("modifyFilters2", () => {
    const state = {
      filters: [
        {
          id: "1",
          name: "Category",
          alias: "Category",
          dataType: "string",
          values: ["Shoes"],
        },
        {
          id: "2",
          name: "Price",
          alias: "Price",
          dataType: "numeric",
          values: [10],
        },
        {
          id: "3",
          name: "Rating",
          alias: "Rating",
          dataType: "numeric",
          values: [],
        },
      ],
    };
  
    it("should return the state if parameters is undefined", () => {
      const result = modifyFilters(state, undefined);
      expect(result).toEqual(state);
    });
  
    it("should return the state if parameters is null", () => {
      const result = modifyFilters(state, null);
      expect(result).toEqual(state);
    });
  
    it("should return the state if parameters is an empty object", () => {
      const result = modifyFilters(state, {});
      expect(result).toEqual(state);
    });
  
    it("should modify the filters with numeric data type correctly for single value", () => {
      const parameters = { Price: 20 };
      const result = modifyFilters(state, parameters);
  
      expect(result.filters[1].values).toEqual([20]);
    });
  
    it("should modify the filters with numeric data type correctly for '_all_' value", () => {
      const parameters = { Price: "_all_" };
      const result = modifyFilters(state, parameters);
  
      expect(result.filters[1].values).toEqual("_all_");
    });
  
    it("should modify the filters with numeric data type correctly for array of values", () => {
      const parameters = { Price: [20, 30] };
      const result = modifyFilters(state, parameters);
  
      expect(result.filters[1].values).toEqual([20, 30]);
    });
  
    it("should modify the filters with string data type correctly for single value", () => {
      const parameters = { Category: "Clothing" };
      const result = modifyFilters(state, parameters);
  
      expect(result.filters[0].values).toEqual(["Clothing"]);
    });
  
    it("should modify the filters with string data type correctly for '_all_' value", () => {
      const parameters = { Category: "_all_" };
      const result = modifyFilters(state, parameters);
  
      expect(result.filters[0].values).toEqual(["_all_"]);
    });
  
    it("should modify the filters with string data type correctly for array of values", () => {
      const parameters = { Category: ["Shoes", "Clothing"] };
      const result = modifyFilters(state, parameters);
  
      expect(result.filters[0].values).toEqual(["Shoes", "Clothing"]);
    });
  
    it("should not modify filters if parameter value is undefined", () => {
      const parameters = { Category: undefined };
      const result = modifyFilters(state, parameters);
  
      expect(result.filters[0].values).toEqual(["Shoes"]);
    });
  
    it("should not modify filters if parameter value is null", () => {
      const parameters = { Category: null };
      const result = modifyFilters(state, parameters);
  
      expect(result.filters[0].values).toEqual(["Shoes"]);
    });
  
    it("should not modify filters if parameter value is an empty array", () => {
      const parameters = { Category: [] };
      const result = modifyFilters(state, parameters);
  
      expect(result.filters[0].values).toEqual([]);
    });
  });
  
  