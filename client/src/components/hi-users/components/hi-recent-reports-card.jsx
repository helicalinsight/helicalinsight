import { Col, Tooltip, Typography } from "antd";
import { useDispatch } from "react-redux";
import { routesUrl } from "../../../app/constants";
import { appActions } from "../../../redux/actions";
import "../hi-users.scss";

const { updateRoute } = appActions;
const { Text, Paragraph } = Typography;


const HIRecentReportsCard = (props) => {
  console.log(props)
  const dispatch = useDispatch();
  const { report } = props;
  const handleRecentReport = () => {
    dispatch(
      appActions.setViewModeInfo({
        file: { path: report.reportPath, name: report.file, title: report.title },
        mode: "open",
        filters: [],
        extension: report.file.split(".")[1],
      })
    );
    dispatch(updateRoute(routesUrl.reportViewUrl));
  };
  return (

    <Col className="recent-reports-card" span={6} onClick={handleRecentReport}>
      <div
        style={{
          backgroundImage: `url(images/hi-adminPageImages/sample-report-1.png)`,
          backgroundSize: "cover",
        }}
      >
        <div className="recent-reports-bg-img">
          <Tooltip title={report?.title}>
            <Paragraph data-testid='recent-report-paragraph' className="text" ellipsis={true}>
              {report?.title}
            </Paragraph>
          </Tooltip>
          {/* <p className="text">{report?.title}</p> */}
        </div>
      </div>
    </Col>
  );
};
export { HIRecentReportsCard };
