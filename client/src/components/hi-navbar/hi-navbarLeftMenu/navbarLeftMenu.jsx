import React, { useMemo, useState } from "react";
import { Menu, Typography, Button, Tooltip } from "antd";
import { ExperimentOutlined } from "@ant-design/icons";
import { NavLink } from "react-router-dom";
import { useSelector, useDispatch } from "react-redux";
import { MenuOutlined, RightOutlined, LeftOutlined } from "@ant-design/icons";
import { appActions } from "../../../redux/actions";
import { routeIcon } from "../helperMethods";
import { routesUrl } from "../../../app/constants";
import TutorialInfo from "../../../components/common/hi-tutorial";

const { Text } = Typography;
const { updateRoute, toggleMainNavbar } = appActions;
const { adminHomeUrl, userHomeUrl, hiUrl, reportViewUrl } = routesUrl;
const navbarTutorialKeys = [
  "hi-navbar-home",
  "hi-navbar-data-sources",
  "hi-navbar-metadata",
  "hi-navbar-reports",
  "hi-navbar-dashboard-designer",
  "hi-navbar-reports-ce",
  "hi-navbar-canned-reports",
  "hi-navbar-cube",
];
const NavbarLeftMenu = ({ screenSizeBreakPoint, width, activeRoute }) => {
  const { applicationSettingsData, toggleNavbarArrow } = useSelector((state) => state.app);
  let routes = useSelector((state) =>
    state.app.routes.filter(
      (item) =>
        item.url === userHomeUrl ||
        (applicationSettingsData.userData.user.roles?.some((role) => item.roles.includes(role)) &&
          item.addInNavbar)
    )
  );

  const dispatch = useDispatch();
  const [showMenu, setShowMenu] = useState(false);
  const [isModalVisible, setIsModalVisible] = useState(false);
  routes = routes.filter((item) => {
    if (applicationSettingsData.settings?.experimentalModules?.includes(item.expId)) {
      return applicationSettingsData.showExperimentalFeatures;
    }
    return true;
  });
  let duplicateRoutes = [...routes];
  let targetURL = "";

  // const handleRouteChange = ({ target }) => {
  //   targetURL = target;
  //   dispatch(appActions.aboutToChangeRoute(true))
  //   setIsModalVisible(target);
  // };

  const isExperimentalModule = (item) => {
    return applicationSettingsData.settings?.experimentalModules?.includes(item.expId);
  };

  const getNavText = (item) => {
    if (isExperimentalModule(item)) {
      return (
        <Tooltip title="Experimental Module" placement="right">
          <span>
            {item.title}
            <sup>
              <ExperimentOutlined
                style={{
                  fontSize: "10px",
                  backgroundColor: "#1890F5",
                  color: "#fff",
                  padding: "3px",
                  margin: "2px",
                  borderRadius: "50%",
                }}
              />
            </sup>
          </span>
        </Tooltip>
      );
    }
    return <span> {item.title} </span>;
  };

  const menuItems = useMemo(() => {
    return duplicateRoutes.map((item, index) => {
      return {
        routePath: item.url,
        element: (toggleNavbarArrow ||
          item.url.match(adminHomeUrl) ||
          item.url.match(userHomeUrl) ||
          activeRoute.match(item.url) ||
          showMenu) && (
            <Menu.Item
              icon={routeIcon(item.url)}
              className={`navbar-left-item ${!item.url.match(adminHomeUrl) && !item.url.match(userHomeUrl)
                ? !activeRoute.match(item.url)
                  ? "expand-link"
                  : ""
                : ""
                }`}
              key={item.url}
              onClick={(e) => {
                if (e.domEvent.ctrlKey === false) {
                  //     if (
                  //       activeRoute.includes("metadata") ||
                  //       activeRoute.includes("dashboard-designer")
                  //     ) {
                  //       handleRouteChange({ target: item.url });
                  //     } else {
                  //       dispatch(toggleMainNavbar(false));
                  //       dispatch(updateRoute(item.url));
                  //     }
                  dispatch(toggleMainNavbar(false));
                } //4926 Fix
              }}
            >
              <NavLink
                className="item-link"
                activeClassName={activeRoute.match(item.url) ? "link-selected" : ""}
                to={item.url}
              // to={'#'}
              >
                <Text className="link">
                  {!toggleNavbarArrow &&
                    !(activeRoute.match(adminHomeUrl) || activeRoute.match(userHomeUrl)) &&
                    (item.url.match(adminHomeUrl) || item.url.match(userHomeUrl)) ? (
                    ""
                  ) : (
                    <TutorialInfo elementKey={item.tutorialElementKey} placement="topLeft" className={'pad-top-10'}>
                      {getNavText(item)}
                    </TutorialInfo>
                  )}
                </Text>
              </NavLink>
            </Menu.Item>
          ),
      };
    });
  }, [toggleNavbarArrow, activeRoute, showMenu, duplicateRoutes]);

  const arrows = () => {
    return width <= screenSizeBreakPoint ? null : (
      <Menu.Item
        className="navbar-left-item arrows"
        onClick={() => {
          dispatch(toggleMainNavbar(!toggleNavbarArrow));
        }}
        key="arrows"
      >
        <Text className="arrow">{toggleNavbarArrow ? <LeftOutlined /> : <RightOutlined />}</Text>
      </Menu.Item>
    );
  };

  return (
    <div className="navbar-left-menu">
      {width <= screenSizeBreakPoint && (
        <Button
          className="menu-icon"
          onClick={() => {
            setShowMenu(!showMenu);
          }}
          icon={<MenuOutlined />}
        ></Button>
      )}
      {(showMenu || width > screenSizeBreakPoint) && (
        <Menu
          className="hi-navbar-left"
          mode={width <= screenSizeBreakPoint ? "vertical" : "horizontal"}
          selectedKeys={[routes.find((item) => activeRoute.match(item.url))?.url]}
        >
          {menuItems.map((item) => {
            if (!item) return null;
            if (
              activeRoute.match(item.routePath) &&
              !activeRoute.match(adminHomeUrl) &&
              !activeRoute.match(userHomeUrl)
            ) {
              return [arrows(), item.element];
            }
            if (
              (item.routePath.match(adminHomeUrl) && activeRoute.match(adminHomeUrl)) ||
              (item.routePath.match(userHomeUrl) && activeRoute.match(userHomeUrl)) ||
              ((activeRoute.match(hiUrl) || activeRoute.match(reportViewUrl)) &&
                (item.routePath.match(adminHomeUrl) || item.routePath.match(userHomeUrl)))
            ) {
              return [item.element, arrows()];
            }
            return item.element;
          })}
        </Menu>
      )}
      {/* <Modal
        visible={isModalVisible}
        onOk={() => {
          dispatch(toggleMainNavbar(false));
          dispatch(updateRoute(isModalVisible));
          dispatch(appActions.aboutToChangeRoute(false))
          setIsModalVisible(false);
        }}
        onCancel={() => {
          setIsModalVisible(false);
          dispatch(appActions.aboutToChangeRoute(null))
        }}
      >
        <p>Are you sure you want to leave?</p>
      </Modal> */}
    </div>
  );
};

export default NavbarLeftMenu;
