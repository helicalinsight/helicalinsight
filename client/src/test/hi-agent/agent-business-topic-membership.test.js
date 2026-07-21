import {
  addBusinessTopicAssignment,
  childMatchesBusinessTopic,
  collectAssignableBusinessTopicKeys,
  getBusinessTopicAssignments,
  groupAssignedFieldsForTopicDisplay,
  removeBusinessTopicAssignment,
  resolveBusinessTopicDropKeys,
} from "../../components/hi-cube/agentBusinessTopicMembership";
import {
  convertAgentDataToCubeFieldsData,
  convertCubeFieldsDataToAgentData,
} from "../../components/hi-agent/utils/agent-cube-bridge";
import { ensureShape } from "../../components/hi-agent/components/semantic-metadata-editor/semantic-metadata-utils";

describe("agentBusinessTopicMembership", () => {
  it("adds a field to multiple topics without removing prior membership", () => {
    let field = {
      key: "address-1",
      fields: "address",
      domain: "business domain",
      topic: "Topic 1",
    };

    field = addBusinessTopicAssignment(field, "business domain", "Topic 2");

    expect(getBusinessTopicAssignments(field)).toEqual([
      { domain: "business domain", topic: "Topic 1" },
      { domain: "business domain", topic: "Topic 2" },
    ]);
    expect(childMatchesBusinessTopic(field, "business domain", "Topic 1")).toBe(
      true,
    );
    expect(childMatchesBusinessTopic(field, "business domain", "Topic 2")).toBe(
      true,
    );
  });

  it("expands a hierarchy drop to all column keys inside it", () => {
    const hierarchy = {
      key: "hierarchy-1",
      fields: "employee_details",
      isHierarchy: true,
      children: [
        { key: "name-1", fields: "employee_name" },
        { key: "address-1", fields: "address" },
        { key: "deleted-1", fields: "age", isDelete: true },
      ],
    };

    expect(collectAssignableBusinessTopicKeys(hierarchy)).toEqual([
      "name-1",
      "address-1",
    ]);
    expect(
      resolveBusinessTopicDropKeys(
        { key: "hierarchy-1", isHierarchy: true },
        [hierarchy],
      ),
    ).toEqual(["name-1", "address-1"]);
    expect(
      resolveBusinessTopicDropKeys({ key: "address-1", isHierarchy: false }),
    ).toEqual(["address-1"]);
  });

  it("groups hierarchy children under the parent for topic display", () => {
    const hierarchy = {
      key: "hierarchy-1",
      fields: "employeedetails",
      isHierarchy: true,
      children: [
        {
          key: "name-1",
          fields: "employee_name",
          isHierarchyChild: true,
          parentKey: "hierarchy-1",
        },
        {
          key: "address-1",
          fields: "address",
          isHierarchyChild: true,
          parentKey: "hierarchy-1",
        },
      ],
    };
    const standalone = { key: "address-2", fields: "address_1" };
    const groups = groupAssignedFieldsForTopicDisplay(
      [...hierarchy.children, standalone],
      [hierarchy, standalone],
    );

    expect(groups).toEqual([
      {
        type: "hierarchy",
        key: "hierarchy-1",
        name: "employeedetails",
        children: hierarchy.children,
      },
      { type: "field", key: "address-2", field: standalone },
    ]);
  });

  it("removes membership from one topic and leaves others intact", () => {
    const field = addBusinessTopicAssignment(
      addBusinessTopicAssignment(
        { key: "address-1", fields: "address" },
        "business domain",
        "Topic 1",
      ),
      "business domain",
      "Topic 2",
    );

    const next = removeBusinessTopicAssignment(
      field,
      "business domain",
      "Topic 1",
    );

    expect(childMatchesBusinessTopic(next, "business domain", "Topic 1")).toBe(
      false,
    );
    expect(childMatchesBusinessTopic(next, "business domain", "Topic 2")).toBe(
      true,
    );
    expect(next.domain).toBe("business domain");
    expect(next.topic).toBe("Topic 2");
  });
});

describe("agent-cube-bridge multi-topic field membership", () => {
  it("round-trips the same column in multiple topics", () => {
    const agentData = ensureShape({
      domain: [
        {
          domain_name: "business domain",
          description: "",
          topics: [
            {
              topic: "Topic 1",
              description: "",
              components: [{ id: "1001", name: "address" }],
            },
            {
              topic: "Topic 2",
              description: "",
              components: [{ id: "1001", name: "address" }],
            },
          ],
        },
      ],
      cube_info: [
        {
          cubeName: "",
          dimensions: [
            {
              dimensionName: "address",
              columnId: "1001",
              tableId: "109",
              columnName: "employee_details.address",
              sortOrder: 0,
            },
          ],
          measures: [],
        },
      ],
    });

    const cubeFields = convertAgentDataToCubeFieldsData(agentData);
    const address = cubeFields.children.find(
      (child) => child.fields === "address",
    );

    expect(getBusinessTopicAssignments(address)).toEqual([
      { domain: "business domain", topic: "Topic 1" },
      { domain: "business domain", topic: "Topic 2" },
    ]);

    const exported = convertCubeFieldsDataToAgentData(cubeFields, agentData);
    expect(exported.domain[0].topics).toEqual([
      {
        topic: "Topic 1",
        description: "",
        components: [{ id: "1001", name: "address" }],
      },
      {
        topic: "Topic 2",
        description: "",
        components: [{ id: "1001", name: "address" }],
      },
    ]);
  });
});
