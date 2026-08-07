import React, { useState } from "react";
import { Col, Dropdown, Empty, Menu, Row, Tooltip, Typography } from "antd";
import { InfoCircleOutlined } from "@ant-design/icons";
import { useSelector } from "react-redux";
import { Cube } from "../../../../hi-cube/cube";
import { useAgentName } from "../../../../common/agent-name-context";
import { CustomIcon } from "../../../../common/custom-icons/CustomIcon";
import TutorialInfo from "../../../../common/hi-tutorial";
import { getCubeEditorTooltipText } from "../../../../hi-cube/cubeEditorTooltips";
import { AgentRightSubmenuTextField } from "../../../../hi-cube/cubeConstants";
import { useCubeEditorBindings } from "../../../../hi-cube/cubeEditorContext";
import { updateFieldValues } from "../../../../../redux/actions/cube.actions";

const { Paragraph, Text } = Typography;

const DEFAULT_AGENT_NAME = "Model_1";
const MODEL_NAME_TOOLTIP = getCubeEditorTooltipText("Semantic Model", "agent");

function ModelDescriptionMenu({ value, onChange, variant }) {
  const [openKey, setOpenKey] = useState(null);
  const clearDescriptionTooltip = getCubeEditorTooltipText(
    "Clear field values",
    variant,
  );

  return (
    <Menu
      className="cube-bv-domain-description-menu"
      items={[
        {
          key: "model-description",
          label: (
            <AgentRightSubmenuTextField
              fieldKey="model-description"
              label="Description"
              tooltipLabel="Semantic Model Description"
              value={value ?? ""}
              placeholder="Add model description. Ex: Sales analytics semantic model."
              clearFieldTooltip={clearDescriptionTooltip}
              openKey={openKey}
              onOpenKeyChange={setOpenKey}
              onChange={onChange}
            />
          ),
        },
      ]}
    />
  );
}

/** Agent_1 shelf: name bar + Fields (cube) editor */
export function CubeShelf({ showBusinessFields = false }) {
  const { agentName, onAgentNameChange } = useAgentName();
  const { cubeState, dispatch, variant } = useCubeEditorBindings();
  const displayName = agentName || DEFAULT_AGENT_NAME;
  const modelDescription = cubeState?.cubeFieldsData?.cubeDescription || "";
  const metadataDetails = useSelector(
    (state) => state.agent.metadataDetails || {},
  );
  const isMetadataLoaded = Boolean(
    metadataDetails.path && metadataDetails.fileName,
  );

  const handleModelDescriptionChange = (next) => {
    dispatch(
      updateFieldValues({
        updateName: "cubeDescription",
        checkVal: next,
      }),
    );
  };

  return (
    <div className="cube-shelf">
      <TutorialInfo elementKey="hi-agent-fields-shelf">
        <div className="cube-shelf-name-bar hi-background-blue metadata-name-edit">
          <Row className="metadata-name-edit-row">
            <Col span={2} className="metadata-name-edit-icon">
              <CustomIcon name="Cube" />
            </Col>
            <Col span={22} className="metadata-name-edit-name">
              <span className="cube-shelf-name-row">
                <Dropdown
                  overlay={
                    <ModelDescriptionMenu
                      value={modelDescription}
                      onChange={handleModelDescriptionChange}
                      variant={variant}
                    />
                  }
                  trigger={["contextMenu"]}
                  destroyPopupOnHide
                >
                  <Paragraph
                    className="cube-shelf-name-text"
                    ellipsis={{ tooltip: displayName }}
                    editable={{
                      tooltip: "Edit",
                      text: displayName,
                      onChange: (value) => onAgentNameChange?.(value),
                    }}
                  >
                    {displayName}
                  </Paragraph>
                </Dropdown>
                {MODEL_NAME_TOOLTIP ? (
                  <Tooltip title={MODEL_NAME_TOOLTIP} placement="right">
                    <InfoCircleOutlined className="cube-info-icon cube-shelf-name-info" />
                  </Tooltip>
                ) : null}
              </span>
            </Col>
          </Row>
        </div>
      </TutorialInfo>
      <div className="cube-shelf-cube-area">
        {!isMetadataLoaded ? (
          <div className="cube-shelf-no-metadata">
            <Empty
              image={Empty.PRESENTED_IMAGE_SIMPLE}
              description={
                <>
                  <Text strong className="cube-shelf-no-metadata-title">
                    No Metadata Connected
                  </Text>
                  <br />
                  <Text type="secondary" className="cube-shelf-no-metadata-desc">
                    Connect a Metadata file to load Dimensions and Measures.
                  </Text>
                </>
              }
            />
          </div>
        ) : (
          <Cube showBusinessFields={showBusinessFields} />
        )}
      </div>
    </div>
  );
}

export default CubeShelf;
