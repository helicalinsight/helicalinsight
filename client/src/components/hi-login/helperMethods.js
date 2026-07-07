import { axiosLogin, impersonateUserLogin, ssoLogin } from '../../base/service';
import notify from '../hi-notifications/notify';
import { applicationSettingsHandler } from '../../customHooks/applicationSettingsCheckLayer';
import { updateActiveRoute } from '../../customHooks/helperMethods';
import actionTypes from '../../redux/actions/actionTypes';
import { appActions } from '../../redux/actions';
import { routesUrl } from '../../app/constants';
import qs from 'qs';
import { resetNotificationData } from '../../redux/actions/useractions.actions';

const HTTP_OK = 200;
const { NORMAL_LOGIN, URL_AUTHENTICATION, IMPERSONATE_LOGIN, URL_AUTHENTICATION_FAILED, SSO_LOGIN } = actionTypes;
const { resetStore } = appActions;
const { loginUrl } = routesUrl;

const parsingData = (res) => {
	let data = JSON.parse(res.data);
	return res.status === HTTP_OK ? Promise.resolve(data.message) : Promise.reject(data);
};

export async function login({ logType, userDetails, dispatch, impersonateUserDetails, parameters, successNotification }) {
	let res = '';
	switch (logType) {
		case NORMAL_LOGIN:
		case URL_AUTHENTICATION:
			res = await axiosLogin(dispatch, { ...userDetails }, successNotification);
			return parsingData(res);
		case IMPERSONATE_LOGIN:
			res = await impersonateUserLogin({ dispatch, impersonateUserDetails, successNotification });
			return parsingData(res)
				.then((res) => {
					dispatch(resetStore({ logType: IMPERSONATE_LOGIN, successNotification}));
					return Promise.resolve(res);
				})
				.catch((err) => {
					return Promise.reject({ message: err, impersonateFailed: 'true' });
				});
		case SSO_LOGIN:
			res = await ssoLogin(dispatch, parameters, successNotification);
			return parsingData(res);
		default:
			return Promise.reject(new Error('Error in sending Login type'));
	}
}

export const loginHandlers = ({
	parameters,
	history,
	impersonateUserDetails,
	logType,
	userDetails,
	dispatch,	
	activeRoute,
	nxtRoute,
	urlPath,
	pathname,
	isAuthenticated,
	successNotification
}) => {
	login({ logType, userDetails, dispatch, impersonateUserDetails, parameters, successNotification })
		.then((msg) => {
			applicationSettingsHandler({
				dispatch,
				nxtRoute,
				pathname: urlPath,
				logType,
				history,
				activeRoute,
				parameters
			});
			// notify(dispatch).success({
			// 	message: msg,
			// 	status: '1',
			// 	type: 'Network Call'
			// });
		})
		.catch((error) => {
			let errorType, errorValue;
			if (typeof error === 'object') {
				if(error.status === '0' || error.status === 0) {
					errorValue = error.message;
					errorType = 'Network Call';
				} else{
					errorValue = error.message;
					errorType = 'Front End';
				}
			} 
			// else {
			// 	errorValue = error;
			// 	error = {};
			// 	errorType = 'Network Call';
			// }
			(errorType === 'Front End') && notify(dispatch).error({ message: errorValue, status: '0', type: errorType });
			if (typeof error === 'object' && !('impersonateFailed' in error)) {
				if (logType === URL_AUTHENTICATION && activeRoute !== loginUrl) {
					dispatch(resetStore({ logType: URL_AUTHENTICATION_FAILED }));
				} else {
					updateActiveRoute({ pathname, activeRoute, isAuthenticated, dispatch, history, nxtRoute });
					dispatch(resetNotificationData())
				}
			}
		});
};
