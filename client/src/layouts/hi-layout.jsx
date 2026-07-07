import { useSelector } from "react-redux";
import classnames from "classnames";
import { Layout } from "antd";
import HIBodyLayout from "../layouts/hi-body-layout";
import "../app/app-print.scss"
const { Header, Content } = Layout;
const HILayout = (props) => {
  let {
    defaultSidebar,
    customClass = "",
    isCustomSidebar,
    customSidebar,
    urlObj,
  } = props;
  const showNavbar = useSelector((state) => state.app.showNavbar);

  return (
    <>
      {showNavbar && (
        <Header className="hi-header hi-navbar">{props.header}</Header>
      )}
      <Content
        data-testid="hi-content-area"
        className={classnames({
          "hi-content-area": true,
          "hi-content-area-new-window": !showNavbar,
        })}
      >
        <HIBodyLayout
          defaultSidebar={defaultSidebar}
          customSidebar={customSidebar}
          customClass={customClass}
          urlObj={urlObj}
        >
          {props.content}
        </HIBodyLayout>
      </Content>
    </>
  );
};

export default HILayout;
