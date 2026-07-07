import React, { useEffect } from "react";
import { CopyrightOutlined, FolderOutlined } from "@ant-design/icons";
import { Row, Col, Typography, Tooltip, Button, Input } from "antd";
import { useSelector, useDispatch } from "react-redux";
import "./index.scss";
import { getProductData } from "../hi-admin/components/hi-overview/utils/getProductInformationApi";
import notify from "../hi-notifications/notify";
import { HIFileBrowser } from "../../components";
import { fileBrowserActions } from "../../redux/actions";
import { agentInteractContextMenuOption } from "../hi-fileBrowser/constants";
import TutorialInfo from "../common/hi-tutorial";
import ShortCutText from "../common/hi-shortcuts/hi-shortcuts";

const { Text } = Typography;
const { Search } = Input;
// const { Link } = Anchor;

const SidebarFooter = (props) => {
  const productData = useSelector((state) => state.admin?.productData);
  const files = useSelector((state) => state.fileBrowser.files.data);
  const isFetched = useSelector((state) => state.admin?.isFetched?.productData);
  const isGlobalFbEnabled = useSelector(
    (state) => state.fileBrowser.globalFbEnabled
  );

  const dispatch = useDispatch();
  const Notify = notify(dispatch);

  useEffect(() => {
    if (props.urlObj?.mode !== 'dashboard') {
      getProductData({ isFetched, dispatch, Notify });
    }
  }, []);

  const onGlobalSearch = (term) => {
    dispatch(fileBrowserActions.setFilteredFiles(files));
    dispatch(fileBrowserActions.setShowFileBrowser(true));
    dispatch(fileBrowserActions.setGlobalSearch(true));
    dispatch(fileBrowserActions.setGlobalFbVisibility(true));
    if (props.onGlobalSearch) {
      props.onGlobalSearch();
    }
  };

  const sidebarFooterClassName =
    props.color === "white"
      ? `sidebar-footer sidebar-footer-bg-white`
      : `sidebar-footer sidebar-footer-bg-light-blue`;

  return (
    <div>
      {isFetched && (
        <Row data-testid="hi-sidebar-footer-row" className={sidebarFooterClassName}>
          <Col span={24}>
            {
              <Tooltip
                placement="top"
                title={"Filebrowser"}
              // overlayInnerStyle={{ height: "100%", fontSize: 14 }}
              >
                <TutorialInfo elementKey="hi-file-browser">
                  <ShortCutText text="B" scLocation={props.module} isButton={true}>
                    <Button
                      size="large"
                      className="hi-sidebar-file-browser-button"
                      block
                      type="primary"
                      onClick={props.fileBrowserOnClick}
                      icon={<FolderOutlined />}
                    >
                      File Browser
                    </Button>
                  </ShortCutText>
                </TutorialInfo>
              </Tooltip>
            }
            {/* <Button
              size="large"
              type="primary"
              icon={<FolderOutlined />}
              // fileBrowserOnClick={() => dispatch(fileBrowserActions.setShowFileBrowser(true))}
              className="hi-filebrowser-button"
            /> */}
            {isGlobalFbEnabled && !props.hideSideBar && (
              <HIFileBrowser
                onFbClose={props.onFbClose}
                contextMenuOptions={{
                  append: true,
                  options: [agentInteractContextMenuOption],
                }}
              />
            )}
          </Col>
          <Col span={24}>
            <TutorialInfo elementKey="hi-global-search">
              <Search
                data-testid="hi-sidebar-footer-search"
                placeholder="Search inside file browser"
                onSearch={onGlobalSearch}
                className="hi-sidebar-search"
                onFocus={onGlobalSearch}
              />
            </TutorialInfo>
          </Col>

          <Col span={12} className="sidebar-footer-text-left">
            <Tooltip title={productData["Version"]}>
              <Text className="sidebar-footer-text" ellipsis="true">
                {productData["Version"]}
              </Text>
            </Tooltip>
          </Col>
          <Col span={12} className={props?.module !== "HR" ? "sidebar-footer-text-right" : "sidebar-footer-text-right-hr"}>
            <a
              href="https://www.helicalinsight.com/"
              target="_blank"
              rel="noreferrer"
            >
              <Tooltip title={`@${productData["Product Name"]}`}>
                <Text className="sidebar-footer-text" ellipsis="true">
                  <CopyrightOutlined /> {productData["Product Name"]}
                </Text>
              </Tooltip>
            </a>
          </Col>
        </Row>
      )}
    </div>
  );
};
export { SidebarFooter };
