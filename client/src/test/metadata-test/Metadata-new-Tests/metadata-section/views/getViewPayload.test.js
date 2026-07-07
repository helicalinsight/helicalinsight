import { getViewPayload } from "../../../../../components/hi-metadata/utils/views/getViewPayload";

describe('getViewPayload', () => {
  test('should return false if saveDetails is missing uuid or location', () => {
    const dataSource = [{ classifier: 'classifier' }];
    const activeView = { id: 'viewId' };
    const saveDetails = {};
    expect(getViewPayload({ activeView, dataSource, saveDetails })).toBe(false);
  });

  test('should return false if dataSource is missing classifier', () => {
    const dataSource = [{}];
    const activeView = { id: 'viewId' };
    const saveDetails = { uuid: 'uuid', location: 'location' };
    expect(getViewPayload({ activeView, dataSource, saveDetails })).toBe(false);
  });

  test('should return a payload object if all required data is present', () => {
    const dataSource = [{ classifier: 'classifier' }];
    const activeView = { id: 'viewId' };
    const saveDetails = { uuid: 'uuid', location: 'location' };
    expect(getViewPayload({ activeView, dataSource, saveDetails })).toEqual({
      hasStoredProcedure: false,
      location: 'location',
      metadataFileName: 'uuid',
      classifier: 'classifier',
      viewId: 'viewId',
    });
  });
});
