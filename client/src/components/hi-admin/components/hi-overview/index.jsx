import { Row, Col, Card } from "antd";
import "./index.scss";
import "../../../../styles/variables.scss";
import {
  HIJVMMemoryCard,
  HIDiskSpaceCard,
  HITempDirCard,
  HICacheSizeCard,
  HICachedReportsCard,
  HILicenseCard,
  HILoggerCard,
  HIReloadConfigurationCard,
  HIDataSourcesCard,
} from "./components";
import TutorialInfo from "../../../common/hi-tutorial";
import { useRef } from "react";

const HIOverview = ({ apiRef, handleAbort }) => {

  return (
    <div data-testid="hi-overview-container" className="hi-overview">
      <Row gutter={[8, 8]} className="hi-overview-box">
        <Col className="hi-disk-space" xs={{ span: 24 }} md={8}>
          <TutorialInfo elementKey="hi-disk-space">
            <HIDiskSpaceCard handleAbort={handleAbort} apiRef={apiRef} />
          </TutorialInfo>
        </Col>
        <Col className="hi-jvm-memory" xs={{ span: 24 }} md={8}>
          <TutorialInfo elementKey="hi-jvm-memory">
            <HIJVMMemoryCard handleAbort={handleAbort} apiRef={apiRef} />
          </TutorialInfo>
        </Col>
        <Col className="hi-temp-directory" xs={{ span: 24 }} md={8}>
          <TutorialInfo elementKey="hi-temp-directory">
            <HITempDirCard handleAbort={handleAbort} apiRef={apiRef} />
          </TutorialInfo>
        </Col>
        <Col xs={{ span: 24, offset: 0 }}>
          <Card>
            <Row className="hi-overview-border hi-overview-cache-box">
              <Col
                className="hi-cache-card"
                xs={{ span: 24, offset: 0 }}
                lg={{ span: 8, offset: 0 }}
              >
                <TutorialInfo elementKey="hi-cache-card">
                  <HICacheSizeCard handleAbort={handleAbort} apiRef={apiRef} />
                </TutorialInfo>
              </Col>
              <Col
                className="hi-cached-reports"
                xs={{ span: 24, offset: 0 }}
                lg={{ span: 8, offset: 0 }}
              >
                <TutorialInfo elementKey="hi-cached-reports">
                  <HICachedReportsCard handleAbort={handleAbort} apiRef={apiRef} />
                </TutorialInfo>
              </Col>
              <Col
                className="hi-data-sources"
                xs={{ span: 24, offset: 0 }}
                lg={{ span: 8, offset: 0 }}
              >
                <TutorialInfo elementKey="hi-data-sources-cached">
                  <HIDataSourcesCard handleAbort={handleAbort} apiRef={apiRef} />
                </TutorialInfo>
              </Col>
            </Row>
          </Card>
        </Col>
        <Col
          className="hi-license"
          xs={{ span: 24, offset: 0 }}
          lg={{ span: 16, offset: 0 }}
        >
          <TutorialInfo elementKey="hi-license-details">
            <HILicenseCard />
          </TutorialInfo>
        </Col>
        <Col xs={{ span: 24, offset: 0 }} lg={{ span: 8, offset: 0 }}>
          <Row gutter={[8, 8]}>
            <Col
              className="hi-logger"
              xs={{ span: 24, offset: 0 }}
              lg={{ span: 24, offset: 0 }}
            >
              <TutorialInfo elementKey="hi-logger-settings">
                <HILoggerCard handleAbort={handleAbort} apiRef={apiRef} />
              </TutorialInfo>
            </Col>
            <Col
              className="hi-reload-configs"
              xs={{ span: 24, offset: 0 }}
              lg={{ span: 24, offset: 0 }}
            >
              <TutorialInfo elementKey="hi-reload-configurations">
                <HIReloadConfigurationCard />
              </TutorialInfo>
            </Col>
          </Row>
        </Col>
      </Row>
    </div>
  );
};

export { HIOverview };
