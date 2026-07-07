import { useSelector } from "react-redux";
import { Card, Row } from "antd";

const Overview = (props) => {
  const staticData = useSelector((store) => store.admin.managementStaticData);

  return (
    <Card hoverable>
      <Row className="overview-container">
        {staticData && (
          <ul>
            {staticData.overviewItems?.map((eachItem) => (
              <li key={eachItem}>{eachItem}</li>
            ))}
          </ul>
        )}
      </Row>
    </Card>
  );
};

export default Overview;
