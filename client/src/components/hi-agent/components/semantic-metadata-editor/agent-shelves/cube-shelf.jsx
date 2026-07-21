import React from "react";
import { Col, Row, Typography } from "antd";
import { Cube } from "../../../../hi-cube/cube";
import { useAgentName } from "../../../../common/agent-name-context";
import { CustomIcon } from "../../../../common/custom-icons/CustomIcon";

const { Paragraph } = Typography;

const DEFAULT_AGENT_NAME = "Model_1";

/** Agent_1 shelf: name bar + Fields (cube) editor */
export function CubeShelf({ showBusinessFields = false }) {
  const { agentName, onAgentNameChange } = useAgentName();
  const displayName = agentName || DEFAULT_AGENT_NAME;

  return (
    <div className="cube-shelf">
      <div className="cube-shelf-name-bar hi-background-blue metadata-name-edit">
        <Row className="metadata-name-edit-row">
          <Col span={2} className="metadata-name-edit-icon">
            <CustomIcon name="Cube" />
          </Col>
          <Col span={22} className="metadata-name-edit-name">
            <Paragraph
              ellipsis={{ tooltip: displayName }}
              editable={{
                tooltip: "Edit",
                text: displayName,
                onChange: (value) => onAgentNameChange?.(value),
              }}
            >
              {displayName}
            </Paragraph>
          </Col>
        </Row>
      </div>
      <div className="cube-shelf-cube-area">
        <Cube showBusinessFields={showBusinessFields} />
      </div>
    </div>
  );
}

export default CubeShelf;
