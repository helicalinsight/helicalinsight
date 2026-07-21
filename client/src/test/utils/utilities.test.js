
import { createTabTitle, isOpenSource } from "../../utils/utilities";

describe("HI Utilities", () => {
    test("jest example", async () => {
        expect(1 + 1).toBeTruthy();
    });
    test("new tab name1", async () => {
        expect(createTabTitle(["Untitled 1"])).toEqual("Untitled 2")
    });
    test("new tab name2", async () => {
        expect(createTabTitle(["Untitled 2"])).toEqual("Untitled 3")
    });
    test("new tab name 3", async () => {
        expect(createTabTitle(["saved", "Untitled 2"])).toEqual("Untitled 3")
    });
    test("new tab name 4", async () => {
        expect(createTabTitle(["saved", "Untitled 1"])).toEqual("Untitled 3")
    });
    test("new tab name 5", async () => {
        expect(createTabTitle(["saved"])).toEqual("Untitled 2")
    });
    test("new tab name 6", async () => {
        expect(createTabTitle([])).toEqual("Untitled 1")
    });
    test("new tab name 7", async () => {
        expect(createTabTitle(["Untitled 3"])).toEqual("Untitled 2")
    });
});


describe('test isOpenSource', () => {
    test('should return true if licenseType is present in metaInfo', () => {
        const metaInfo = { licenseType: 'Community' };
        expect(isOpenSource(metaInfo)).toBe(true);
    });

    test('should return false if licenseType is not present in metaInfo', () => {
        const metaInfo = {};
        expect(isOpenSource(metaInfo)).toBe(false);
    });

    test('should return false if metaInfo is null', () => {
        expect(isOpenSource(null)).toBe(false);
    });

    test('should return false if metaInfo is undefined', () => {
        expect(isOpenSource()).toBe(false);
    });
});