import { decompile } from "../../../../components/hi-datasources/utils/decompile";


describe('decompile', () => {
  test('should decompile template and return correct result', () => {
    const tpl = 'Hello, {{ name }}! You have {{ count }} new messages.';
    const str = 'Hello, user! You have 5 new messages.';
    const expectedResult = {
      name: 'user',
      count: '5',
    };

    const result = decompile(tpl, str);

    expect(result).toEqual(expectedResult);
  });

  test('should return an empty object if no interpolation is found', () => {
    const tpl = 'Hello, world!';
    const str = 'Hello, world!';
    const expectedResult = {};

    const result = decompile(tpl, str);

    expect(result).toEqual(expectedResult);
  });

  test('should handle multiple interpolations in the template', () => {
    const tpl = 'Today is {{ day }}. The month is {{ month }}.';
    const str = 'Today is Monday. The month is June.';
    const expectedResult = {
      day: 'Monday',
      month: 'June',
    };

    const result = decompile(tpl, str);

    expect(result).toEqual(expectedResult);
  });
});
