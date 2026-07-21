import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import "@testing-library/jest-dom";
import {
  CUBE_FIELD_SEMANTIC_TEXT_FIELDS,
  CUBE_FIELD_SEMANTIC_MENU_KEY,
  CUBE_FIELD_MENU_TEXT_KEYS,
  commitCubeFieldSemanticDraft,
  applyFieldMeasureRoleChange,
  cubeEditorMeasureData,
  CubeFieldsMenu,
  cubeSortMenu,
  cubeMeasureMenu,
  cubeAggregationMenu,
} from "../../components/hi-cube/cubeConstants";
import { updateFieldValues, modifyHierarchy } from "../../redux/actions/cube.actions";
import { getDefaultSemanticTypeForRole } from "../../components/hi-cube/cubeSemanticTypeField";

jest.mock("../../components/hi-cube/cubeEditorContext", () => ({
  useCubeEditorBindings: () => ({
    cubeState: {},
    dispatch: jest.fn(),
    variant: "cube",
  }),
}));

const baseRecord = {
  key: "record-1",
  fields: "Revenue",
  fieldsDropdownOpen: false,
  isHierarchy: false,
  isHierarchyChild: false,
  isDimensionCheck: false,
  parentKey: undefined,
  formula: "",
  filter: "",
  example: "",
  description: "",
  measure: { isMeasureCheck: true, DataType: "Number", Format: "0.00" },
  sort: { isSortCheck: true, value: "Ascending" },
  aggregation: { isAggregationCheck: false, value: "Sum" },
};

const emptyHierarchyData = {
  isHierarchyPresent: false,
  hierarchyList: [],
};

describe("cubeConstants", () => {
  describe("exports", () => {
    it("it should defines semantic field config and menu keys", () => {
      expect(CUBE_FIELD_SEMANTIC_TEXT_FIELDS).toHaveLength(4);
      expect(CUBE_FIELD_SEMANTIC_TEXT_FIELDS.map((f) => f.fieldName)).toEqual([
        "formula",
        "filter",
        "example",
        "description",
      ]);
      expect(CUBE_FIELD_SEMANTIC_MENU_KEY).toBe("semantic-fields");
      expect(CUBE_FIELD_MENU_TEXT_KEYS).toContain(CUBE_FIELD_SEMANTIC_MENU_KEY);
      expect(CUBE_FIELD_MENU_TEXT_KEYS).toContain("agent-role-options");
    });

    it("it should defines measure editor data types and formats", () => {
      expect(cubeEditorMeasureData).toEqual([
        { dataType: "Number", format: ["0.00", "00.00"] },
        { dataType: "Date", format: ["DD-MM-YY", "YYYY-MM-DD"] },
      ]);
    });
  });

  describe("applyFieldMeasureRoleChange", () => {
    it("updates semantic type to Number when converting to measure in agent variant", () => {
      const dispatch = jest.fn();
      const record = {
        ...baseRecord,
        semanticType: "Text",
        measure: { isMeasureCheck: false, DataType: "", Format: "" },
        isDimensionCheck: true,
      };
      const semanticTypeOptions = [
        {
          label: "Numeric",
          value: "NUMERIC",
          options: [{ label: "Number", value: "NUMBER" }],
        },
      ];

      applyFieldMeasureRoleChange(
        dispatch,
        record,
        true,
        "agent",
        semanticTypeOptions,
      );

      expect(dispatch).toHaveBeenCalledWith(
        updateFieldValues({
          updateName: "semanticType",
          checkVal: "Number",
          recordKey: record.key,
          isHierarchyChild: record.isHierarchyChild,
          hierarchyKey: record.parentKey,
        }),
      );
    });

    it("updates semantic type to Text when converting to dimension in agent variant", () => {
      const dispatch = jest.fn();
      const record = {
        ...baseRecord,
        semanticType: "Number",
        measure: { isMeasureCheck: true, DataType: "Number", Format: "0.00" },
      };
      const semanticTypeOptions = [
        {
          label: "Categorical",
          value: "CATEGORICAL",
          options: [{ label: "Text", value: "TEXT" }],
        },
      ];

      applyFieldMeasureRoleChange(
        dispatch,
        record,
        false,
        "agent",
        semanticTypeOptions,
      );

      expect(dispatch).toHaveBeenCalledWith(
        updateFieldValues({
          updateName: "semanticType",
          checkVal: "Text",
          recordKey: record.key,
          isHierarchyChild: record.isHierarchyChild,
          hierarchyKey: record.parentKey,
        }),
      );
    });

    it("uses cube semantic type defaults for cube variant", () => {
      expect(getDefaultSemanticTypeForRole(true, "cube")).toBe("numeric");
      expect(getDefaultSemanticTypeForRole(false, "cube")).toBe("text");
      expect(
        getDefaultSemanticTypeForRole(true, "agent", [
          {
            label: "Numeric",
            value: "NUMERIC",
            options: [{ label: "Number", value: "NUMBER" }],
          },
        ]),
      ).toBe("Number");
      expect(
        getDefaultSemanticTypeForRole(false, "agent", [
          {
            label: "Categorical",
            value: "CATEGORICAL",
            options: [{ label: "Text", value: "TEXT" }],
          },
        ]),
      ).toBe("Text");
    });
  });

  describe("commitCubeFieldSemanticDraft", () => {
    it("it should dispatches updateFieldValues only for changed semantic fields", () => {
      const dispatch = jest.fn();
      const record = { ...baseRecord, formula: "old", filter: "", example: "", description: "" };
      const draft = { formula: "new formula", filter: "", example: "", description: "desc" };
      commitCubeFieldSemanticDraft({ dispatch, record, draft });
      expect(dispatch).toHaveBeenCalledTimes(2);
      expect(dispatch).toHaveBeenCalledWith(
        updateFieldValues({
          updateName: "formula",
          checkVal: "new formula",
          recordKey: record.key,
          isHierarchyChild: record.isHierarchyChild,
          hierarchyKey: record.parentKey,
        }),
      );
      expect(dispatch).toHaveBeenCalledWith(
        updateFieldValues({
          updateName: "description",
          checkVal: "desc",
          recordKey: record.key,
          isHierarchyChild: record.isHierarchyChild,
          hierarchyKey: record.parentKey,
        }),
      );
    });

    it("should does not dispatch when draft matches record", () => {
      const dispatch = jest.fn();
      const record = {
        ...baseRecord,
        formula: "same",
        filter: "",
        example: "ex",
        description: "desc",
      };
      const draft = { formula: "same", filter: "", example: "ex", description: "desc" };
      commitCubeFieldSemanticDraft({ dispatch, record, draft });
      expect(dispatch).not.toHaveBeenCalled();
    });
  });

  describe("cubeSortMenu", () => {
    it("it shoould dispatches sort value when a sort option is chosen", () => {
      const dispatch = jest.fn();
      const record = { ...baseRecord, sort: { isSortCheck: false, value: "Natural" } };
      render(cubeSortMenu({ dispatch, record }));
      fireEvent.click(screen.getByText("Descending"));
      expect(dispatch).toHaveBeenCalledWith(
        updateFieldValues({
          updateName: "sort",
          key: "value",
          value: "Descending",
          recordKey: record.key,
          isHierarchyChild: record.isHierarchyChild,
          hierarchyKey: record.parentKey,
        }),
      );
    });

    it("shows Natural instead of None and disables explicit sorting", () => {
      const dispatch = jest.fn();
      const record = { ...baseRecord, sort: { isSortCheck: true, value: "Ascending" } };
      render(cubeSortMenu({ dispatch, record }));

      expect(screen.queryByText("None")).not.toBeInTheDocument();
      fireEvent.click(screen.getByText("Natural"));
      expect(dispatch).toHaveBeenCalledWith(
        updateFieldValues({
          updateName: "sort",
          key: "isSortCheck",
          value: false,
          recordKey: record.key,
          isHierarchyChild: record.isHierarchyChild,
          hierarchyKey: record.parentKey,
        }),
      );
    });
  });

  describe("cubeAggregationMenu", () => {
    it("it should dispatches aggregation value when an option is chosen", () => {
      const dispatch = jest.fn();
      const record = {
        ...baseRecord,
        aggregation: { isAggregationCheck: true, value: "Sum" },
      };
      render(
        cubeAggregationMenu({
          dispatch,
          record,
          hierarchyData: emptyHierarchyData,
        }),
      );
      fireEvent.click(screen.getByText("Avg"));
      expect(dispatch).toHaveBeenCalledWith(
        updateFieldValues({
          updateName: "aggregation",
          key: "value",
          value: "Avg",
          recordKey: record.key,
          isHierarchyChild: record.isHierarchyChild,
          hierarchyKey: record.parentKey,
        }),
      );
    });

    it("includes None and clears aggregation behavior", () => {
      const dispatch = jest.fn();
      const record = {
        ...baseRecord,
        aggregation: { isAggregationCheck: true, value: "Sum" },
      };
      render(
        cubeAggregationMenu({
          dispatch,
          record,
          hierarchyData: emptyHierarchyData,
        }),
      );

      fireEvent.click(screen.getByText("None"));
      expect(dispatch).toHaveBeenCalledWith(
        updateFieldValues({
          updateName: "aggregation",
          key: "isAggregationCheck",
          value: false,
          recordKey: record.key,
          isHierarchyChild: record.isHierarchyChild,
          hierarchyKey: record.parentKey,
        }),
      );
      expect(dispatch).toHaveBeenCalledWith(
        updateFieldValues({
          updateName: "defaultFunction",
          checkVal: "db.generic.aggregate.none",
          recordKey: record.key,
          isHierarchyChild: record.isHierarchyChild,
          hierarchyKey: record.parentKey,
        }),
      );
    });
  });

  describe("CubeFieldsMenu", () => {
    it("it should renders delete for a regular field row", () => {
      const dispatch = jest.fn();
      render(
        <CubeFieldsMenu
          dispatch={dispatch}
          record={baseRecord}
          hierarchyData={emptyHierarchyData}
        />,
      );
      expect(screen.getByText("Delete")).toBeInTheDocument();
      expect(screen.getByText("Formula")).toBeInTheDocument();
      expect(screen.getByText("Example")).toBeInTheDocument();
      expect(screen.getByText("Description")).toBeInTheDocument();
    });

    it("it should dispatches deleteRow when delete is clicked", () => {
      const dispatch = jest.fn();
      render(
        <CubeFieldsMenu
          dispatch={dispatch}
          record={baseRecord}
          hierarchyData={emptyHierarchyData}
        />,
      );
      fireEvent.click(screen.getByText("Delete"));
      expect(dispatch).toHaveBeenCalledWith(
        modifyHierarchy({ record: baseRecord, step: "deleteRow" }),
      );
    });

    it("should not show semantic fields on hierarchy parent rows", () => {
      const dispatch = jest.fn();
      const hierarchyRecord = {
        ...baseRecord,
        key: "hierarchy-1",
        isHierarchy: true,
        isDimensionCheck: true,
        fields: "Date Hierarchy",
      };
      render(
        <CubeFieldsMenu
          dispatch={dispatch}
          record={hierarchyRecord}
          hierarchyData={{
            isHierarchyPresent: true,
            hierarchyList: [
              { hierarchyName: "Geo Hierarchy", hierarchyKey: "hierarchy-2" },
            ],
          }}
        />,
      );
      expect(screen.getByText("Delete without columns")).toBeInTheDocument();
      expect(screen.getByText("Add to an existing Hierarchy")).toBeInTheDocument();
      expect(screen.queryByText("Formula")).not.toBeInTheDocument();
    });

    it("should show disabled semantic fields for hierarchy child rows", () => {
      const dispatch = jest.fn();
      const hierarchyChildRecord = {
        ...baseRecord,
        isHierarchyChild: true,
        parentKey: "hierarchy-1",
        formula: "[Year]",
      };
      const { container } = render(
        <CubeFieldsMenu
          dispatch={dispatch}
          record={hierarchyChildRecord}
          hierarchyData={emptyHierarchyData}
        />,
      );
      commitCubeFieldSemanticDraft({
        dispatch,
        record: hierarchyChildRecord,
        draft: { formula: "new", filter: "", example: "", description: "" },
      });
      expect(dispatch).not.toHaveBeenCalled();
    });
  });
});
