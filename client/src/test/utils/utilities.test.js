
import { createTabTitle } from "../../utils/utilities";

describe("HI Utilities", () => {
    test("jest example",async () => { 
        expect(1+1).toBeTruthy();
    });
    test("new tab name1",async () => {
        expect(createTabTitle(["Untitled 1"])).toEqual("Untitled 2")
    });
    test("new tab name2",async () => {
        expect(createTabTitle(["Untitled 2"])).toEqual("Untitled 3")
    });
    test("new tab name 3",async () => {
        expect(createTabTitle(["saved","Untitled 2"])).toEqual("Untitled 3")
    });
    test("new tab name 4",async () => {
        expect(createTabTitle(["saved","Untitled 1"])).toEqual("Untitled 3")
    });
    test("new tab name 5",async () => {
        expect(createTabTitle(["saved"])).toEqual("Untitled 2")
    });
    test("new tab name 6",async () => {
        expect(createTabTitle([])).toEqual("Untitled 1")
    });
    test("new tab name 7",async () => {
        expect(createTabTitle(["Untitled 3"])).toEqual("Untitled 2")
    });
}); 