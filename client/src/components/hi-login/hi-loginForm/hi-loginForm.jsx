import { Form, Input, Button, Row, Col, Typography, Image, Radio, Space } from 'antd';
import { useSelector, useDispatch } from 'react-redux';
import { loginValidations } from '../../../utils/validations';
import { useHistory, useLocation } from 'react-router-dom';
import { loginHandlers } from '../helperMethods';
import actionTypes from '../../../redux/actions/actionTypes';

const { Paragraph } = Typography;
const { NORMAL_LOGIN } = actionTypes;

export function credentialsValidator(credentials) {
	const { username = undefined, password = undefined, organization = undefined } = credentials;
	if (username === undefined || password === undefined || organization === undefined) {
		return 0;
	} else {
		return 1;
	}
}

function HILoginForm({ defaultAdmin, defaultUser, loginLogo, setIsNormalLoginForm }) {
	const [form] = Form.useForm();
	const { nxtRoute, applicationSettingsData, activeRoute, isAuthenticated } = useSelector((state) => state.app);
	const dispatch = useDispatch();
	const history = useHistory();
	const { location: historyLocation } = history;
	const { pathname: historyPathname } = historyLocation;
	const location = useLocation();
	const { pathname } = location;

	function loginHandler(credentials) {
		credentialsValidator(credentials) &&
			loginHandlers({
				urlPath: historyPathname,
				userDetails: { ...credentials },
				dispatch,
				nxtRoute,
				pathname,
				logType: NORMAL_LOGIN,
				activeRoute,
				isAuthenticated,
				history,
				successNotification: applicationSettingsData?.settings?.successNotification,
			})
	}

	return (
		<Form data-testid="hi-login-form" className="main-login-form" layout="vertical" initialValues={{ username: '', password: '', organization: '' }}
			form={form} autoComplete="off" onFinish={loginHandler}>
			{loginLogo}
			<Form.Item className="login-form-item" label="ORGANIZATION NAME" name="organization">
				<Input
					addonBefore={
						<Image
							height="15px"
							width="15px"
							className="org-logo"
							preview={false}
							src="images/hi-loginPageImages/org.png"
							alt="organization"
						/>
					}
				/>
			</Form.Item>
			<Form.Item
				autoFocus
				className="login-form-item"
				label="USER NAME"
				name="username"
				rules={[{ validator: loginValidations }]}
			// validateTrigger="onFinish"
			>
				<Input
					addonBefore={
						<Image
							height="15px"
							width="15px"
							className="user-logo"
							preview={false}
							src="images/hi-loginPageImages/user.png"
							alt="user"
						/>
					}
				/>
			</Form.Item>
			<Form.Item
				className="login-form-item"
				label="PASSWORD"
				name="password"
				rules={[{ validator: loginValidations }]}
			// validateTrigger="onFinish"
			>
				<Input.Password
					addonBefore={
						<Image
							height="15px"
							width="15px"
							className="password-logo"
							preview={false}
							src="images/hi-loginPageImages/pass.png"
							alt="password"
						/>
					}
				/>
			</Form.Item>
			<Form.Item className="login-form-item login-btn">
				<Button type="primary" htmlType="submit">
					LOG IN
				</Button>
			</Form.Item>
			{!applicationSettingsData?.hideDefaultLoginButtons && <Row justify="space-around" className="login-form-actions" gutter={5}>
				<Col>
					<Button
						className="login-form-admin-action"
						onClick={() => {
							form.setFieldsValue({ username: 'hiadmin', password: 'hiadmin', organization: '' });
							setTimeout(() => {
								// process.env.NODE_ENV === 'development'
								// ? defaultAdmin()
								loginHandler({ username: 'hiadmin', password: 'hiadmin', organization: '' });
							}, 500);
						}}
					>
						<Image
							height="35px"
							width="35px"
							className="login-form-img-responsive"
							preview={false}
							src="images/hi-loginPageImages/admin_signin.png"
							alt="admin"
						/>
						<Paragraph className="action-text">
							Default <br /> Admin
						</Paragraph>
					</Button>
				</Col>
				<Col>
					<Button
						className="login-form-user-action"
						onClick={() => {
							form.setFieldsValue({ username: 'hiuser', password: 'hiuser', organization: '' });
							setTimeout(() => {
								// process.env.NODE_ENV === 'development'
								// 	? defaultUser()
								loginHandler({ username: 'hiuser', password: 'hiuser', organization: '' });
							}, 500);
						}}
					>
						<Image
							height="35px"
							width="35px"
							className="login-form-img-responsive"
							preview={false}
							src="images/hi-loginPageImages/user_signin.png"
							alt="user"
						/>
						<Paragraph className="action-text">
							Default <br /> User
						</Paragraph>
					</Button>
				</Col>
			</Row>}
			{applicationSettingsData.userData.saml?.enabled && (
				<div className="login-form-item">
					<a
						className="login-form-saml-link"
						onClick={() => {
							setIsNormalLoginForm(false);
						}}
					>
						Log In with SAML
					</a>
				</div>
			)}
		</Form>
	);
}

export default HILoginForm;
