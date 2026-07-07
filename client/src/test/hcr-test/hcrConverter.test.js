import { onConfiguration } from '../../components/hi-canned-reports/hcrHelperMethods';
import {
	diagramAreaOldConfig,
	HCR_test,
	propertyPaneData_test,
	expectedDiagramAreaNewConfig,
	textFieldOldConfig,
	expectedTextFieldNewConfig
} from './constants';

describe('Testing HCR Property Panes', () => {
	test('Success: Diagram Area new configuration', (done) => {
		let groups = [];
		const newConfig = onConfiguration({
			hcrStaticData: HCR_test,
			oldConfigContent: diagramAreaOldConfig,
			groups,
			propertyPaneData: propertyPaneData_test
		});
		expect(newConfig).toEqual(expectedDiagramAreaNewConfig);
		done();
	});

	test('Success: Text-Field new configuration', (done) => {
		let groups = [];
		const newConfig = onConfiguration({
			hcrStaticData: HCR_test,
			oldConfigContent: textFieldOldConfig,
			groups,
			propertyPaneData: propertyPaneData_test
		});
		expect(newConfig).toEqual(expectedTextFieldNewConfig);
		done();
	});
});
