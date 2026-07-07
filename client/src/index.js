import "./utils/polyfill/url";
import ReactDOM from 'react-dom';
import React from 'react';
import 'antd/dist/antd.css';
import 'leaflet/dist/leaflet.css';
import App from './app/app';

if (document.getElementById('root')) {
	ReactDOM.render(<App />, document.getElementById('root'));
}

window.unMountRootComponent = (elementId) => {
	ReactDOM.unmountComponentAtNode(document.getElementById(elementId));
};
export const embed = ({ auth, report, elementId, baseURL }) => {
	document.getElementById(elementId) &&
		ReactDOM.render(
			<App auth={auth} report={report} baseURL={baseURL} elementId={elementId} />,
			document.getElementById(elementId)
		);
};

window.embed = embed
