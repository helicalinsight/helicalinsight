import { Row, Col, Image, Typography, Button, Card, Skeleton } from "antd";
import { useSelector, useDispatch } from "react-redux";
import { useEffect } from "react";
import "../index.scss";
import notify from "../../../../hi-notifications/notify";
import { getProductData } from "../utils/getProductInformationApi";
import { getDaysLeftBeforeExpiry } from "../utils/getDaysLeftBeforeExpiry";
import CustomSkeletonLicenseCard from "../../../../common/custom-icons/CustomSkeletons/CustomSkeletonLicenseCard";

const { Text } = Typography;

const HILicenseCard = () => {
  const productData = useSelector((state) => state.admin?.productData);

  const isFetched = useSelector((state) => state.admin?.isFetched?.productData);
  const metaInfo = useSelector((state) => (state.app.applicationSettingsData.meta || {}));
  const licence = metaInfo?.licenseType;

  const dispatch = useDispatch();
  const Notify = notify(dispatch);
  useEffect(() => {
    getProductData({ isFetched, dispatch, Notify });
  }, []);
  const { applicationSettingsData } = useSelector((state) => state.app);

  const daysLeft = applicationSettingsData?.license?.remainingDays;

  return (
    <>
      {!isFetched ? (
        <>
          <CustomSkeletonLicenseCard />
        </>
      ) : (
        <Card
          size="small"
          title="License Details"
          hoverable={isFetched}
          className="hi-overview hi-overview-border-box hi-height-30 hi-license-details"
        >
          <Row className="hi-overview-padding-3">
            <Col xs={24} lg={8}>
              <Image
                preview={false}
                className="hi-overview-image"
                height={115}
                width={115}
                src="images/hi-adminPageImages/license.svg"
              />
            </Col>
            <Col xs={24} lg={16}>
              <Typography>
                <Row className="hi-ellipses">
                  <Col span={24}>
                    <Text ellipsis="true">
                      {productData["Product Name"]} : {productData["Version"]}
                      <Text
                        ellipsis="true"
                        copyable={{
                          text: productData["Version"],
                          tooltips: ["Copy Version Details", "Copied"],
                        }}
                      />
                    </Text>
                  </Col>
                  <Col span={24}>
                    <Text ellipsis="true">
                      Build : {productData["Build"]}
                      <Text
                        copyable={{
                          text: productData["Build"],
                          tooltips: ["Copy Build Details", "Copied"],
                        }}
                      />
                    </Text>
                  </Col>
                  {licence && <Col span={24}>
                    <Text ellipsis="true">
                      License Type : {productData["License Type"]}
                    </Text>
                  </Col>}
                  {licence && <Col span={24}>
                    <Text ellipsis="true">
                      Expiration Date :{" "}
                      {`${productData["Expiration"]} ${daysLeft
                        ? `( ${daysLeft} ${daysLeft > 1 ? "days left" : "day left"
                        } )`
                        : ""
                        }`}
                    </Text>
                  </Col>}
                  <Col span={24}>
                    <Button
                      href="http://www.helicalinsight.com/upgrade/"
                      type="primary"
                      target="_blank"
                      className="hi-upgrade-btn"
                    >
                      Upgrade
                    </Button>
                  </Col>
                </Row>
              </Typography>
            </Col>
          </Row>
        </Card>
      )
      }
    </>
  )
}


export { HILicenseCard };
