
import { addFileToFolder } from "../../components/hi-fileBrowser/helperMethods";
import { getFilterOptionsForExtensions } from "../../components/hi-fileBrowser/constants";
import data from "../../components/hi-fileBrowser/dummy";

describe("Filebrowser Utilities", () => {
    describe("getFilterOptionsForExtensions", () => {
        const apiExtensions = ["efwsr", "hr", "report", "hwf"];

        test("returns known options for matching API extensions", () => {
            const options = getFilterOptionsForExtensions(apiExtensions);
            const hrOption = options.find((op) => op.value === "hr");
            expect(hrOption?.label).toBe("Helical Report");
            expect(hrOption?.isUnknown).toBeUndefined();
        });

        test("maps unknown API extensions with default icon", () => {
            const options = getFilterOptionsForExtensions(apiExtensions);
            const reportOption = options.find((op) => op.value === "report");
            expect(reportOption?.value).toBe("report");
            expect(reportOption?.label).toBe("Unknown");
            expect(reportOption?.isUnknown).toBe(true);
            expect(reportOption?.icon).toBeTruthy();
        });

        test("excludes frontend options not present in API response", () => {
            const options = getFilterOptionsForExtensions(apiExtensions);
            expect(options.find((op) => op.value === "efw")).toBeUndefined();
            expect(options.find((op) => op.value === "agent")).toBeUndefined();
        });

        test("intersects API extensions with page extensionOptions", () => {
            const options = getFilterOptionsForExtensions(apiExtensions, ["hr", "report"]);
            expect(options.map((op) => op.value)).toEqual(["hr", "report"]);
        });

        test("maps known extensionOptions when API extensions are not loaded", () => {
            const options = getFilterOptionsForExtensions(null, ["agent"]);
            expect(options).toEqual([
                expect.objectContaining({
                    value: "agent",
                    label: "Agent",
                }),
            ]);
        });

        test("should maps extensionOptions as unknown when API extensions are not loaded", () => {
            const options = getFilterOptionsForExtensions(null, ["customext"]);
            expect(options).toEqual([
                expect.objectContaining({
                    value: "customext",
                    label: "Unknown",
                    isUnknown: true,
                }),
            ]);
        });
    });

    describe("save new file to redux", () => {
        test("save new file to array", () => {
            const file = [{
                "extension": "hr",
                "lastModified": 1654627298428,
                "name": "newhrtest",
                "options": {},
                "path": "naresh/newhrtest.hr",
                "permissionLevel": "5",
                "title": "newhrtest",
                "type": "file"
            }]
            const parentPath = "naresh"
            const output = addFileToFolder(data, file, parentPath)
            expect(output[0].children.find(e => e.path === file[0].path)).toBeTruthy();
        });
        test("empty array for file", () => {
            const file = []
            const parentPath = "naresh"
            const output = addFileToFolder(data, file, parentPath)
            expect(output.length).toBeTruthy();
        });
        test("empty array for data", () => {
            const file = [{
                "extension": "hr",
                "lastModified": 1654627298428,
                "name": "newhrtest",
                "options": {},
                "path": "naresh/newhrtest.hr",
                "permissionLevel": "5",
                "title": "newhrtest",
                "type": "file"
            }]
            const parentPath = "naresh"
            const output = addFileToFolder([], file, parentPath)
            expect(output).toEqual([]);
        });
    })
}); 