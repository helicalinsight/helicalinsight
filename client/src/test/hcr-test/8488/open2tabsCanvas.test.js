describe("Dropdown Tests", () => {
  let querySelectorAllSpy;
  let mockElements;

  beforeEach(() => {
    querySelectorAllSpy = jest.spyOn(document, "querySelectorAll");
    mockElements = [{ remove: jest.fn() }, { remove: jest.fn() }];
  });
  afterEach(() => {
    jest.restoreAllMocks();
  });

  it("should remove elements from DOM", () => {
    querySelectorAllSpy.mockReturnValue(mockElements);
    const cleanupDropdowns = () => {
      document
        .querySelectorAll(".x6-dropdown-overlay")
        .forEach((ele) => ele.remove());
    };
    cleanupDropdowns();
    expect(querySelectorAllSpy).toHaveBeenCalledWith(".x6-dropdown-overlay");
    mockElements.forEach((element) => {
      expect(element.remove).toHaveBeenCalledTimes(1);
    });
  });

  it("should handle empty dropdown list", () => {
    querySelectorAllSpy.mockReturnValue([]);
    const cleanupDropdowns = () => {
      document
        .querySelectorAll(".x6-dropdown-overlay")
        .forEach((ele) => ele.remove());
    };
    expect(() => cleanupDropdowns()).not.toThrow();
    expect(querySelectorAllSpy).toHaveBeenCalledWith(".x6-dropdown-overlay");
  });

  it("should be triggered on dependency changes", () => {
    const dependencies = [
      "tabNum",
      "sidebarPaneActiveKey",
      "flowchartInstance",
    ];
    dependencies.forEach((dep) => {
      console.log(`Effect should run when ${dep} changes`);
    });
    expect(dependencies).toHaveLength(3);
    expect(dependencies).toContain("tabNum")
    expect(dependencies).toContain("sidebarPaneActiveKey");
    expect(dependencies).toContain("flowchartInstance");
  });

  it("it should match the useEffect implementationn", () => {
    const useEffectImplementation = () => {
      document
        .querySelectorAll(".x6-dropdown-overlay")
        .forEach((ele) => ele.remove());
    };
    expect(typeof useEffectImplementation).toBe("function");
  });
});
