import { routesUrl } from '../app/constants';
import { appActions } from '../redux/actions';
import actionTypes from '../redux/actions/actionTypes';
import qs from 'qs';

const { loginUrl, adminHomeUrl } = routesUrl;
const { updateAppDetails, updateRoute } = appActions;
const { URL_AUTHENTICATION, URL_AUTHENTICATION_FAILED } = actionTypes;

export function updateActiveRoute({ pathname, activeRoute, isAuthenticated, dispatch, history, nxtRoute, parameters }) {
	if (isAuthenticated) {
		if (pathname) {
			if (parameters) {
				let { j_organization, username, password, authToken, ...rest } = parameters;
				pathname += qs.stringify(rest, { arrayFormat: 'repeat', addQueryPrefix: true });
			}
			dispatch(updateRoute(pathname));
		} else {
			activeRoute && activeRoute !== loginUrl && history.push(activeRoute);
		}
		dispatch(
			updateAppDetails({
				isApplicationSettingsServiceCheck: true
			})
		);
	} else {
		if (pathname !== loginUrl) {
			nxtRoute = pathname;
			let { j_organization, username, password, authToken, ...rest } = parameters || {};
			nxtRoute += qs.stringify(rest, { arrayFormat: 'repeat', addQueryPrefix: true });
		}
		dispatch(
			updateAppDetails({
				activeRoute: loginUrl,
				nxtRoute,
				isApplicationSettingsServiceCheck: true
			})
		);
		activeRoute === loginUrl && history.push(loginUrl);
	}
}
