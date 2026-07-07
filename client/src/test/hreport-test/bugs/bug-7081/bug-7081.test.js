import { modifyFilters } from "../../../../utils/utilities";

describe("test modifyFilters", () => {
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

    it("should modify the filters with numeric data type correctly when mode is not present", () => {
        const parameters = { Price: [20, 30] };
        const result = modifyFilters(state, parameters);

        expect(result.filters[1].values).toEqual([20, 30]);
    });

    it("should modify the filters with string data type correctly when mode is present", () => {
        const parameters = { Category: ["Shoes", "Clothing"] };
        const result = modifyFilters(state, parameters, "dashboard");

        expect(result.filters[0].values).toEqual(["Shoes", "Clothing"]);
    });

    it("should modify the filters with numeric data type correctly when mode is empty", () => {
        const parameters = { Price: [20, 30] };
        const result = modifyFilters(state, parameters, "");

        expect(result.filters[1].values).toEqual([20, 30]);
    });
});