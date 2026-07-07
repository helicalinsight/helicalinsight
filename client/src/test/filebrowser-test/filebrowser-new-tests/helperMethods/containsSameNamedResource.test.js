import {containsSameNamedResource } from "../../../../components/hi-fileBrowser/helperMethods";


describe('containsSameNamedResource', () => {

  test('returns false when resourceList is empty', () => {
    const resource = { name: 'Resource1', type: 'Type1' };
    const resourceList = [];
    expect(containsSameNamedResource(resource, resourceList)).toBe(false);
  });

  test('when resourceList contains a resource with the same name and type', () => {
    const resource = { name: 'Resource1', type: 'Type1' };
    const resourceList = [{ name: 'Resource1', type: 'Type1' }, { name: 'Resource2', type: 'Type2' }];
    expect(containsSameNamedResource(resource, resourceList)).toStrictEqual({ name: 'Resource1', type: 'Type1' });
  });

  test('returns false when resourceList contains a resource with the same name but different type', () => {
    const resource = { name: 'Resource1', type: 'Type1' };
    const resourceList = [{ name: 'Resource1', type: 'Type2' }, { name: 'Resource2', type: 'Type2' }];
    expect(containsSameNamedResource(resource, resourceList)).toBe(false);
  });

  test('returns false when resourceList does not contain a resource with the same name', () => {
    const resource = { name: 'Resource1', type: 'Type1' };
    const resourceList = [{ name: 'Resource2', type: 'Type1' }, { name: 'Resource3', type: 'Type2' }];
    expect(containsSameNamedResource(resource, resourceList)).toBe(false);
  });

  test('returns false when resourceList contains a resource with the same name but different case', () => {
    const resource = { name: 'Resource1', type: 'Type1' };
    const resourceList = [{ name: 'resource1', type: 'Type1' }, { name: 'Resource2', type: 'Type2' }];
    expect(containsSameNamedResource(resource, resourceList)).toBe(false);
  });
});
