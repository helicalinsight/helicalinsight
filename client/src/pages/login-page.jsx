import React from "react";
import { useDispatch } from "react-redux";
import LoginLayout from "../layouts/login-layout";
import { getDevConstants } from "../lib/hi-app-constants";
import { HILoginPage } from "../components";
import { appActions } from "../redux/actions";
import { localApplicationSettings, roleTypes } from "../app/constants";

const { onLogin, updateRoute, storeApplicationSettings } = appActions;

const HILogin = () => {
  const dispatch = useDispatch();

  const defaultAdmin = () => {
    if (process.env.NODE_ENV === "development") {
      const {intial_page} = getDevConstants()
      dispatch(onLogin(localApplicationSettings(getDevConstants())));
      dispatch(storeApplicationSettings(localApplicationSettings(getDevConstants())));
      dispatch(updateRoute(intial_page));
    }
  };

  const defaultUser = () => {
    if (process.env.NODE_ENV === "development") {
      dispatch(onLogin(localApplicationSettings(getDevConstants())));
      dispatch(storeApplicationSettings(localApplicationSettings(getDevConstants())));
      dispatch(updateRoute('/welcome'));
    }
  };

  return (
    <LoginLayout>
      <HILoginPage defaultAdmin={defaultAdmin} defaultUser={defaultUser} data-testid = "hi-login-main-page" />
    </LoginLayout>
  );
};

export { HILogin };
