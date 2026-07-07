import { Layout, Row, Col } from 'antd';
import { useSelector } from 'react-redux';
import React, { useEffect } from 'react';
import SidebarLayout from './side-bar-layout';
import { CubeSidebar } from '../components/hi-cube/cubeSidebar';

const { Content } = Layout;

const HIBodyLayout = ({ children, customClass = '', defaultSidebar, customSidebar, urlObj }) => {
	const { toggleSidebar } = useSelector((state) => state.app);
	useEffect(
		() => {
			window.dispatchEvent(new Event('resize'));
		},
		[toggleSidebar]
	);
	return (
		<Row className="hi-page-grid">
			{(defaultSidebar || customSidebar) && (
				<Col
					// xs={{ span: 24, order: 1 }} 
					xs={{ span: toggleSidebar ? 0 : 24 }} // on mobile screen this won't be visible, fix for bug id : 8156
					md={{ span: toggleSidebar ? 0 : 4, order: -1 }}
				>
					{customSidebar || <SidebarLayout urlObj={urlObj} />}
				</Col>
			)}
			<Col
				xs={{ span: 24, order: -1 }}
				md={{ span: toggleSidebar || !(defaultSidebar || customSidebar) ? 24 : 20, order: 1 }}
			>
				<Content className={`hi-body ${customClass}`}>
					<div className={`hi-body ${customClass}`}>{children}</div>
				</Content>
			</Col>
		</Row>
	);
};
//test
export default HIBodyLayout;
