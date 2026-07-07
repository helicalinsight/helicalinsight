import { Button, Form, Radio, Space } from 'antd';
import { useRef } from 'react';
import { useState } from 'react';
import { useDispatch } from 'react-redux';
import { useSelector } from 'react-redux';
import { samlLogin } from '../../../base/service';
import { loginValidations } from '../../../utils/validations';

export default function HISamlLogin({ loginLogo, setIsNormalLoginForm }) {
	const { saml } = useSelector(state => state.app.applicationSettingsData.userData);
	const dispatch = useDispatch();
	const [form] = Form.useForm();
	const [samlIdValue, setSamlIdValue] = useState('');
	const ref = useRef({
		id: ''
	});

	const callback = (res) => {
		// console.log(res);
	}

	const errback = (err) => {
		// console.log(err);
	}

	// const LayoutOptions = () => {
	// 	const plainOptions = ['Metadata Sheif', 'Tools Sheif', 'Visualize Sheif'];
	// 	return <Checkbox.Group options={plainOptions} defaultValue={plainOptions} />;
	// };

	return (
		<Form data-testid = "hi-saml-login" className="main-login-form" layout="vertical" form={form} autoComplete="off" onFinish={() => { samlLogin({ dispatch, samlId: saml.samlIds[ref.current.id], callback, errback }) }}>
			<div className="saml-container">
				<div className="saml-body">
					{loginLogo}
					<Form.Item
						name="radio-button"
						className='radio-list'
						rules={[{ validator: loginValidations }]}
						validateTrigger="onFinish"
					>
						<Radio.Group onChange={(e) => { setSamlIdValue(e.target.value); }} value={samlIdValue}>
							<Space direction="vertical">
								{saml.samlIdValues?.map((val, i) => <Radio value={val} onClick={() => { ref.current.id = i; }}>{val}</Radio>)}
							</Space>
						</Radio.Group>
					</Form.Item>
				</div>
				<div className="saml-footer">
					<Button type="primary" htmlType="submit" className="saml-sign-on" >
						START SINGLE SIGN-ON
					</Button>
					<a
						className="login-form-credentials-link"
						onClick={() => {
							setIsNormalLoginForm(true);
						}}
					>
						Log In with Credentials
					</a>
				</div>
			</div>
		</Form>
	);
}
