import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Button, Modal, notification, Space, Typography } from 'antd';
import { logoutHandler } from '../components/hi-navbar/hi-userActions/userAction.helperMethods';
import { appActions } from '../redux/actions';
import { routesUrl } from '../app/constants';
import actionTypes from '../redux/actions/actionTypes';
export let expTime;
export let contTime;


const { setSessionOver } = appActions;
const { NORMAL_LOGOUT, IMPERSONATE_LOGOUT } = actionTypes;
let key = "";

export default function useCookieManager(auth) {
	const activeRoute = useSelector((state) => state.app.activeRoute);
	const sessionExpiry = useSelector((state) => state.app.sessionExpiry);
	const applicationSettingsData = useSelector((state) => state.app.applicationSettingsData);
	const logType = useSelector((state) => state.app.logType);
	const isAuthenticated = useSelector((state) => state.app.isAuthenticated);
	// const [sessionExpiry, setSessionExpiry] = useState('');
	const dispatch = useDispatch();
	const { user } = applicationSettingsData.userData;
	// const warningTime = 10000;

	useEffect(
		() => {
			let end = parseInt(sessionExpiry, 10);
			let start = Date.now();
			let AskToContTime = end - start;
			// let AbtToExpTime = AskToContTime - warningTime;
			if (!auth && logType !== NORMAL_LOGOUT && isAuthenticated && sessionExpiry !== '' && activeRoute !== routesUrl.loginUrl && process.env.NODE_ENV !== 'development') {
				<>{// if (AbtToExpTime > 0) {
				//     expTime = setTimeout(() => {
				//         notification["warning"]({
				//             message: 'Warning',
				//             duration: 3,
				//             description:
				//                 'Your session time is about to Expire.',
				//         });
				//     }, AbtToExpTime)
				// }
				// console.log(user);
			}</>
				key = `${Date.now()}`;
				contTime = setTimeout(() => {
						dispatch(setSessionOver(true));
						notification['info']({
							className: 'session-notify',
							duration: 0,
							key,
							maxCount: 1,
							description: 'Your Session is Expired. Please Login',
							btn: (
								<div onClick={() => notification.close(key)}>
									<Space align="center">
										{/* <Button type="primary">YES</Button> */}
										<Button
											type="primary"
											onClick={() => {
												logoutHandler({ dispatch, user, isSessionOver: true, activeRoute });
											}}
										>
											OK
										</Button>
									</Space>
								</div>
							)
						});
				}, AskToContTime); // AskToContTime
			}

			return () => {
				notification.close(key);
				// clearTimeout(expTime);
				clearTimeout(contTime);
			};
		},
		[ sessionExpiry, logType, isAuthenticated ]
	);
}
