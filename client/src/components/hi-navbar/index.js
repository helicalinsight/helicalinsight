import React, { useLayoutEffect, useState, useEffect } from "react";
import { Layout, notification } from "antd";
import NavbarLeftMenu from "./hi-navbarLeftMenu/navbarLeftMenu";
import { useDispatch, useSelector } from "react-redux";
import Taskbar from "./hi-taskbar/Taskbar";
import UserActions from "./hi-userActions/UserActions";
import { v4 as uuidv4 } from "uuid";
import { appActions } from "../../redux/actions";
import "./navbar.scss";

const { Header } = Layout;
const screenSizeBreakPoint = 1180;

const HINavbar = (props) => {
  let { taskbar, urlObj={}, hideToggleSidebar } = props;
  const [width, setWidth] = useState(window.innerWidth);
  const dispatch = useDispatch();
  const {
    activeRoute,
    toggleNavbarArrow,
    isApplicationSettingsServiceCheck,
    applicationSettingsData,
    showLicenseNotification,
    isLicenseRendered,
  } = useSelector((state) => state.app);

  function updateWidth() {
    setWidth(window.innerWidth);
  }

  useLayoutEffect(() => {
    window.addEventListener("resize", updateWidth);
    return () => window.removeEventListener("resize", updateWidth);
  }, []);

  const Message = () => {
    return (
      <>
        <span>{applicationSettingsData.license.remainingDayMessage}</span>
        <span> Please </span>
        <a
          href="https://www.helicalinsight.com/contact-us/"
          target="_blank"
          className="renew-license"
        >
          renew now
        </a>
      </>
    );
  };

  useEffect(() => {
    const key = uuidv4();
    if (isLicenseRendered === false && showLicenseNotification && urlObj.mode !== 'dashboard') {
      if (
        applicationSettingsData?.license?.remainingDayMessage &&
        isApplicationSettingsServiceCheck
      ) {
        const args = {
          description: <Message />,
          duration: 0,
          placement: "topRight",
          key,
          onClose: () => dispatch(appActions.storeShowLicense(false)),
        };
        notification.warn(args);
        dispatch(appActions.storeIsLicenseRendered(true));
      }
    }
  }, []);

  return (
    <>
      <NavbarLeftMenu
        screenSizeBreakPoint={screenSizeBreakPoint}
        width={width}
        activeRoute={activeRoute}
      />
      <div className="hi-navbar-body">
        {!toggleNavbarArrow && (
          <div className="hi-navbar-middle">
            {props.children} {/*     ANTD  TABS      */}
          </div>
        )}
        <UserActions screenSizeBreakPoint={screenSizeBreakPoint} width={width} />
        {!toggleNavbarArrow && <Taskbar activeRoute={activeRoute} taskbar={taskbar} hideToggleSidebar={hideToggleSidebar} />}
      </div>
    </>
  );
};

export { HINavbar };
