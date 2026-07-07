import { profileItem } from "../../../../../components/hi-admin/components/hi-userManagement/utils/profileItem";


describe('profileItem', () => {
  it('should call postUserManagementRequest with correct arguments', () => {
    const dispatch = jest.fn();
    const data = { name: 'Test Profile' };
    const successCB = jest.fn();
    const errorCB = jest.fn();
    const requests = {
      admin: jest.fn().mockReturnValue({
        postUserManagementRequest: jest.fn(),
      }),
    };

    profileItem({ dispatch, data, successCB, errorCB, requests });

    expect(requests.admin().postUserManagementRequest).toHaveBeenCalledWith({
      url: undefined,
      data,
      callback: successCB,
      errback: errorCB,
    });
  });
});
