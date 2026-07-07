import { userItem } from "../../../../../components/hi-admin/components/hi-userManagement/utils/userItem";


describe('userItem', () => {
  it('should call postUserManagementRequest with correct arguments', () => {
    const dispatch = jest.fn();
    const data = { name: 'Test User' };
    const successCB = jest.fn();
    const errorCB = jest.fn();
    const requests = {
      admin: jest.fn().mockReturnValue({
        postUserManagementRequest: jest.fn(),
      }),
    };

    userItem({ dispatch, data, successCB, errorCB, requests });

    expect(requests.admin().postUserManagementRequest).toHaveBeenCalledWith({
      url: undefined,
      data,
      callback: successCB,
      errback: errorCB,
    });
  });
});
