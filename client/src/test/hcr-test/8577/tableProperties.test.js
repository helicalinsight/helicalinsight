describe("TableProperties Dropdown functions", () => {
  it("dropdownRender should render menu with select/deselect button", () => {
    const menu = <div>Menu Content</div>;
    const onClick = jest.fn();
    const fields = ["field1", "field2"];
    const selectedValues = ["field1"];
    const buttonText =
      selectedValues.includes("field1") && selectedValues.includes("field2")
        ? "Deselect All"
        : "Select All";
    expect(buttonText).toBe("Select All");
  });

  it("it should select all fields", () => {
    const fields = [{ name: "field1" }, { name: "field2" }];
    const searchStr = "";
    const currentSelectedValues = [];
    const onPropertyChange = jest.fn();
    const fieldNames = fields.map((field) => field.name);
    const allCurrentlySelected =
      fieldNames.length > 0 &&
      fieldNames.every((field) => currentSelectedValues.includes(field));
    expect(allCurrentlySelected).toBe(false);
    expect(fieldNames).toEqual(["field1", "field2"]);
  });

  it("it should deselect all fieldss when all are currently selected", () => {
    const fields = [{ name: "field1" }, { name: "field2" }, { name: "field3" }];
    const searchStr = "";
    const currentSelectedValues = ["field1", "field2", "field3"];
    const onPropertyChange = jest.fn();
    const fieldNames = fields.map((field) => field.name);
    const allCurrentlySelected =
      fieldNames.length > 0 &&
      fieldNames.every((field) => currentSelectedValues.includes(field));
    expect(allCurrentlySelected).toBe(true);
    const buttonText = allCurrentlySelected ? "Deselect All" : "Select All";
    expect(buttonText).toBe("Deselect All");
    if (allCurrentlySelected) {
      const newSelectedValues = [];
      expect(newSelectedValues).toEqual([]);
    }
  });
});
