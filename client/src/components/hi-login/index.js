import { Col, Row, Typography, Image } from 'antd';
import { useState } from 'react';
import HILoginForm from './hi-loginForm/hi-loginForm';
import HISamlLogin from './hi-samlLogin/hi-samlLogin';
import './hi-loginPage.scss';

const { Title, Paragraph } = Typography;
const loginLogo = (
	<div className="login-logo-container">
		<Image
			height="32px"
			width="60px"
			className="login-form-logo"
			preview={false}
			src="images/hi-loginPageImages/logo.svg"
			alt="logo"
		/>
		<Paragraph className="login-form-logo-text">
			ENTERPRISE <br /> EDITION
		</Paragraph>
	</div>
);

function HILoginPage({ defaultAdmin, defaultUser }) {
	const [ isNormalLoginForm, setIsNormalLoginForm ] = useState(true);

	return (
		<div data-testid = "hi-login-index-page" className="login-page-wrapper">
			<Row justify="center" className="login-page">
				<Col xs={24} lg={8} className="form-wrapper">
					{isNormalLoginForm ? (
						<HILoginForm
							loginLogo={loginLogo}
							setIsNormalLoginForm={setIsNormalLoginForm}
							defaultAdmin={defaultAdmin}
							defaultUser={defaultUser}
						/>
					) : (
						<HISamlLogin loginLogo={loginLogo} setIsNormalLoginForm={setIsNormalLoginForm} />
					)}
				</Col>
				<Col xs={24} lg={16} className="login-page-placeholder">
					<Image
						height="100%"
						width="100%"
						preview={false}
						src="images/hi-loginPageImages/log_placeholder.png"
					/>
					<Title className="login-page-text">Visualize, Analyze, Be Wise</Title>
				</Col>
			</Row>
		</div>
	);
}

export { HILoginPage };
