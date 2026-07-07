import { handleShareOptionsWithSearchTerm } from "../../../components/hi-fileBrowser/helperMethods";


    describe("Testing handleShareOptionsWithSearchTerm func", () => {
        test("testing with no search", () => {
            const tabData = [{a:1}, {b:2}]; 
            const debouncedSearchTerm = {col: null, value: ""};
            const tabKey = 'user';
            expect(handleShareOptionsWithSearchTerm({tabData, debouncedSearchTerm, tabKey})).toEqual([{a:1}, {b:2}]);
        });
        test("testing name search", () => {
            const tabData = [{name: "TEST123"}, {name: 'tesT_asc'}, {name: 'bat'}]; 
            const debouncedSearchTerm = {col: 'user', value: "teSt"};
            const tabKey = 'user';
            expect(handleShareOptionsWithSearchTerm({tabData, debouncedSearchTerm, tabKey})).toEqual([{name: "TEST123"}, {name: 'tesT_asc'}]);
        });
        test("testing with OrgName change", () => {
            const tabData = [{orgName: 'HelicaL'}, {orgName: '12365'},  {orgName: 'hELical'}, { orgName: 'heli'}]; 
            const debouncedSearchTerm = {col: 'organization', value: "helI"};
            const tabKey = 'user';
            expect(handleShareOptionsWithSearchTerm({tabData, debouncedSearchTerm, tabKey})).toEqual([{orgName: 'HelicaL'}, {orgName: 'hELical'}, {orgName: 'heli'}]);
        });
        test("testing with Description change", () => {
            const tabData = [{description: 'HelicaL'}, {description: '12365'}, {description: 'hELical'}, { description: 'heli'}]; 
            const debouncedSearchTerm = {col: 'Description', value: "helI"};
            const tabKey = 'organization';
            expect(handleShareOptionsWithSearchTerm({tabData, debouncedSearchTerm, tabKey})).toEqual([{description: 'HelicaL'}, {description: 'hELical'}, { description: 'heli'}]);
        });
    })