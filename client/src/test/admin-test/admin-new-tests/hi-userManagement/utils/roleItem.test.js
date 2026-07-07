import { roleItem } from "../../../../../components/hi-admin/components/hi-userManagement/utils/roleItem";


describe('roleItem', () => {
  it('should call postUserManagementRequest with correct arguments', () => {
    const dispatch = jest.fn();
    const data = { name: 'Test Role' };
    const successCB = jest.fn();
    const errorCB = jest.fn();
    const requests = {
      admin: jest.fn().mockReturnValue({
        postUserManagementRequest: jest.fn(),
      }),
    };

    roleItem({ dispatch, data, successCB, errorCB, requests });

    expect(requests.admin().postUserManagementRequest).toHaveBeenCalledWith({
      url: undefined,
      data,
      callback: successCB,
      errback: errorCB,
    });
  });
});
