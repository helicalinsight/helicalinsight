import { Button, Result } from 'antd';
import { results } from './resultsHelperMethods';
import { useDispatch, useSelector } from 'react-redux';
import { appActions } from '../../redux/actions';
import { useHistory } from 'react-router-dom';
import { normalLogout } from '../hi-navbar/hi-userActions/userAction.helperMethods';

export const HIResults = ({ status, title, subTitle, icon, redirectRoute, btnContent = 'Go Home' }) => {
	const { title: setTitle, subTitle: setSubTitle } = results({ status, title, subTitle });
	const dispatch = useDispatch();
	const history = useHistory();

	return (
		<Result
			status={status}
			title={setTitle}
			icon={icon}
			subTitle={setSubTitle}
			extra={[
				status !== '403' && (
					<Button
						type="primary"
						key="console"
						onClick={() => {
							if (status !== 'error') {
								// if (status === '403') {
								// 	normalLogout(dispatch);
								// } else {
								dispatch(appActions.updateAppDetails({ activeRoute: redirectRoute, nxtRoute: '' }));
								// }
							} else {
								history.goBack();
							}
						}}
					>
						{btnContent}
					</Button>
				)
			]}
		/>
	);
};
