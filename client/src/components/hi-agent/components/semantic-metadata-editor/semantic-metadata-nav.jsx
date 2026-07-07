import React from "react";
import InstantBITooltip from "../../../hi-instant-bi/instant-bi-tooltip-title";
import { SECTIONS } from "./semantic-metadata-utils";
import "./semantic-metadata-editor.scss";
import { Button } from "antd";

export function SemanticMetadataNav({
  activeSection,
  onSectionChange,
  counts = {},
}) {
  return (
    <div className="semantic-metadata-editor semantic-metadata-nav-panel">
      <nav className="me-sidebar" >
       {SECTIONS.map((section) => (
          <InstantBITooltip
            key={section.key}
            title={section.description}
            placement="right"
          >
            <div
              // type="button"
              className={`me-nav-item${
                activeSection === section.key ? " active" : ""
              }`}
              onClick={() => onSectionChange(section.key)}
            >
              {/* {section.icon && <span className="me-nav-icon">{section.icon}</span>} */}
              <span>{section.label}</span>
              {/* {section.countKey && (
                <span className="me-nav-count">{counts[section.countKey]}</span>
              )} */}
            </div>
          </InstantBITooltip>
        ))}
      </nav>
    </div>
  );
}
