import React from "react";
import { createPortal } from "react-dom";
import { SLASH_ENVIRONMENT } from "./slash-mode";

let HIInstantBI;
// Lazy require avoids a cycle: pages barrel -> router -> Slash -> instant-bi-page.
const loadInstantBI = () => {
  if (!HIInstantBI) {
    HIInstantBI = require("../../../pages/instant-bi-page").HIInstantBI;
  }
  return HIInstantBI;
};

// anchor may be a DOM node, an element id, or a ref.
const resolveAnchor = (anchor) => {
  if (anchor == null || anchor === false) return null;
  if (typeof anchor === "string") {
    return typeof document !== "undefined" ? document.getElementById(anchor) : null;
  }
  if (typeof anchor === "object" && Object.prototype.hasOwnProperty.call(anchor, "current")) {
    return anchor.current || null;
  }
  if (anchor.nodeType === 1) return anchor;
  return null;
};

/**
 * Starts Instant BI in Slash mode.
 * Pass `anchor` (element, id, or ref) to mount that view on the given node.
 */
export class Slash extends React.Component {
  render() {
    const { anchor, environment = SLASH_ENVIRONMENT, exploreOnActiveTab = false, ...rest } = this.props;
    const InstantBI = loadInstantBI();
    const instantBi = (
      // Always start Instant BI as Slash, with hreport unless a prop overrides it.
      <InstantBI {...rest} slashMode environment={environment} exploreOnActiveTab={exploreOnActiveTab} />
    );
    // No anchor: render in place. With an anchor: mount into that node.
    if (anchor == null || anchor === false) return instantBi;
    const node = resolveAnchor(anchor);
    return node ? createPortal(instantBi, node) : null;
  }
}
