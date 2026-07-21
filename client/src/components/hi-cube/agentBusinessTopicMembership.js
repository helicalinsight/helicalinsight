export function normalizeBusinessTopicAssignment(domain, topic) {
  return {
    domain: (domain || "").trim(),
    topic: (topic || "").trim(),
  };
}
export function getBusinessTopicAssignments(child = {}) {
  if (Array.isArray(child.businessTopics) && child.businessTopics.length) {
    return child.businessTopics
      .map((assignment) =>
        normalizeBusinessTopicAssignment(assignment.domain, assignment.topic),
      )
      .filter((assignment) => assignment.domain || assignment.topic);
  }
  const domain = (child.domain || "").trim();
  const topic = (child.topic || "").trim();
  if (domain || topic) {
    return [{ domain, topic }];
  }
  return [];
}

export function childMatchesBusinessTopic(child, domainName, topicName) {
  const domain = (domainName || "").trim();
  const topic = (topicName || "").trim();
  if (!domain || !topic) {
    return false;
  }
  return getBusinessTopicAssignments(child).some(
    (assignment) =>
      assignment.domain === domain && assignment.topic === topic,
  );
}

function withSyncedScalarAssignment(child, businessTopics) {
  return {
    ...child,
    businessTopics,
    domain: businessTopics[0]?.domain || "",
    topic: businessTopics[0]?.topic || "",
  };
}

export function addBusinessTopicAssignment(child, domainName, topicName) {
  const next = normalizeBusinessTopicAssignment(domainName, topicName);
  if (!next.domain || !next.topic) {
    return child;
  }
  const existing = getBusinessTopicAssignments(child);
  if (
    existing.some(
      (assignment) =>
        assignment.domain === next.domain && assignment.topic === next.topic,
    )
  ) {
    return Array.isArray(child.businessTopics)
      ? child
      : withSyncedScalarAssignment(child, existing);
  }
  return withSyncedScalarAssignment(child, [...existing, next]);
}

export function removeBusinessTopicAssignment(child, domainName, topicName) {
  const target = normalizeBusinessTopicAssignment(domainName, topicName);
  const existing = getBusinessTopicAssignments(child);
  if (!existing.length) {
    return child;
  }
  const businessTopics = existing.filter(
    (assignment) =>
      !(
        assignment.domain === target.domain &&
        assignment.topic === target.topic
      ),
  );
  if (businessTopics.length === existing.length) {
    return child;
  }
  return withSyncedScalarAssignment(child, businessTopics);
}

export function remapBusinessTopicDomain(child, oldDomain, nextDomain) {
  const from = (oldDomain || "").trim();
  const to = (nextDomain || "").trim();
  const existing = getBusinessTopicAssignments(child);
  if (!existing.length || !from) {
    return child;
  }
  const businessTopics = existing.map((assignment) =>
    assignment.domain === from ? { ...assignment, domain: to } : assignment,
  );
  return withSyncedScalarAssignment(child, businessTopics);
}

export function remapBusinessTopicName(child, domainName, oldTopic, nextTopic) {
  const domain = (domainName || "").trim();
  const from = (oldTopic || "").trim();
  const to = (nextTopic || "").trim();
  const existing = getBusinessTopicAssignments(child);
  if (!existing.length || !domain || !from) {
    return child;
  }
  const businessTopics = existing.map((assignment) =>
    assignment.domain === domain && assignment.topic === from
      ? { ...assignment, topic: to }
      : assignment,
  );
  return withSyncedScalarAssignment(child, businessTopics);
}

export function clearBusinessTopicsMatching(child, predicate) {
  const existing = getBusinessTopicAssignments(child);
  if (!existing.length) {
    return child;
  }
  const businessTopics = existing.filter(
    (assignment) => !predicate(assignment),
  );
  if (businessTopics.length === existing.length) {
    return child;
  }
  return withSyncedScalarAssignment(child, businessTopics);
}

/** Leaf field keys under a node (hierarchy parent expands to its columns). */
export function collectAssignableBusinessTopicKeys(node) {
  if (!node || node.isDelete) {
    return [];
  }
  if (node.isHierarchy) {
    return (node.children || []).flatMap(collectAssignableBusinessTopicKeys);
  }
  return node.key ? [node.key] : [];
}

const findCubeChildByKey = (nodes = [], key) => {
  for (const node of nodes) {
    if (node.key === key) {
      return node;
    }
    if (node.isHierarchy && Array.isArray(node.children)) {
      const nested = findCubeChildByKey(node.children, key);
      if (nested) {
        return nested;
      }
    }
  }
  return null;
};
export function resolveBusinessTopicDropKeys(fieldItem, children = []) {
  if (!fieldItem?.key) {
    return [];
  }
  if (!fieldItem.isHierarchy) {
    return [fieldItem.key];
  }
  const hierarchy =
    findCubeChildByKey(children, fieldItem.key) ||
    (Array.isArray(fieldItem.children)
      ? { isHierarchy: true, children: fieldItem.children }
      : null);
  if (hierarchy) {
    return collectAssignableBusinessTopicKeys(hierarchy);
  }
  return Array.isArray(fieldItem.childKeys) ? fieldItem.childKeys.filter(Boolean) : [];
}
export function groupAssignedFieldsForTopicDisplay(
  assignedFields = [],
  allChildren = [],
) {
  const hierarchyByKey = new Map();
  const walk = (nodes = []) => {
    nodes.forEach((node) => {
      if (!node || node.isDelete) {
        return;
      }
      if (node.isHierarchy) {
        hierarchyByKey.set(node.key, node);
        walk(node.children || []);
      }
    });
  };
  walk(allChildren);

  const childrenByParent = new Map();
  assignedFields.forEach((field) => {
    const parentKey =
      field.isHierarchyChild && field.parentKey && hierarchyByKey.has(field.parentKey)
        ? field.parentKey
        : null;
    if (!parentKey) {
      return;
    }
    if (!childrenByParent.has(parentKey)) {
      childrenByParent.set(parentKey, []);
    }
    childrenByParent.get(parentKey).push(field);
  });

  const groups = [];
  const emittedParents = new Set();
  assignedFields.forEach((field) => {
    const parentKey =
      field.isHierarchyChild && field.parentKey && hierarchyByKey.has(field.parentKey)
        ? field.parentKey
        : null;
    if (parentKey) {
      if (emittedParents.has(parentKey)) {
        return;
      }
      emittedParents.add(parentKey);
      const parent = hierarchyByKey.get(parentKey);
      groups.push({
        type: "hierarchy",
        key: parent.key,
        name: parent.fields || parent.columnName || "hierarchy",
        children: childrenByParent.get(parentKey) || [],
      });
      return;
    }
    groups.push({ type: "field", key: field.key, field });
  });
  return groups;
}
