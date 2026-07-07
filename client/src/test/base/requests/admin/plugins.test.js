
import { filterPluginsData, filterDeletePlugins } from "../../../../utils/utilities";
import { addSpacesInCamelCase } from "../../../../components/hi-admin/components/hi-plugins/helperMethods";


const pluginsFilter = [
  { id: 1, temporaryName: "frontend", details: { jarName: "helical-1" } },
  { id: 2, temporaryName: "backend", details: { jarName: "helical-2" } },
  { id: 3, temporaryName: "fullstack", details: { jarName: "helical-3" } },
];



describe("Plugins Data Testing", () => {


  describe("Filter function", () => {
    test("it should filter by a search term - success", () => {
      const output = [
        {
          id: 3,
          temporaryName: "fullstack",
          details: { jarName: "helical-3" },
        },
      ];
      expect(filterPluginsData(pluginsFilter, "full")).toEqual(output);
    });

    test("it should not filter by a search term - Failure", () => {
      expect(filterPluginsData(pluginsFilter, "five")).toEqual([]);
    });
  });

  describe("Delete Filter function", () => {
    test("it should filter remaing plugins - success", () => {
      const output = [
        { id: 1, temporaryName: "frontend", details: { jarName: "helical-1" } },
        { id: 2, temporaryName: "backend", details: { jarName: "helical-2" } },
      ];
      expect(filterDeletePlugins(pluginsFilter, "helical-3")).toEqual(output);
    });

    test("it should not filter by a search term - Failure", () => {
      expect(filterDeletePlugins(pluginsFilter, "one")).toEqual(pluginsFilter);
    });
  });

 
});



describe("addSpacesInCamelCase", () => {
  test("to test the addSpacesInCamelCase function", () => {
    const pluginKey = "ClassLoaderInstance";
    expect(addSpacesInCamelCase(pluginKey)).toEqual("Class Loader Instance");
  });
});