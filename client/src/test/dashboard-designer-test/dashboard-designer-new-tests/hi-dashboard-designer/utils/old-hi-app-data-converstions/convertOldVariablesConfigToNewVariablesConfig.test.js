import { convertOldVariablesConfigToNewVariablesConfig } from "../../../../../../components/hi-dashboard-designer/utils/old-hi-app-data-converstions";



describe('convertOldVariablesConfigToNewVariablesConfig', () => {
    test('converts old variables config to new variables config', () => {
      const oldVariablesConfig = [      ['var1', 'value1'],
        ['var2', 'value2'],
        ['var3', 'value3'],
      ];
      const expectedNewVariablesConfig = {
        var1: 'value1',
        var2: 'value2',
        var3: 'value3',
      };
      const result = convertOldVariablesConfigToNewVariablesConfig(oldVariablesConfig);
      expect(result).toEqual(expectedNewVariablesConfig);
    });
  
    test('returns an empty object when given an empty array', () => {
      const oldVariablesConfig = [];
      const expectedNewVariablesConfig = {};
      const result = convertOldVariablesConfigToNewVariablesConfig(oldVariablesConfig);
      expect(result).toEqual(expectedNewVariablesConfig);
    });
  });
  