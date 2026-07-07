export const mockAxios = jest.genMockFromModule('axios');

mockAxios.create = jest.fn(() => mockAxios);
mockAxios.get = jest.fn(() => mockAxios);
mockAxios.post = jest.fn(() => {
	return Promise.resolve('ntw_suc_res');
	return mockAxios;
});

export const dispatch = (callback) => {
	typeof callback === 'function' &&
		callback(
			dispatch,
			() => {
				return {
					app: {
						applicationSettingsData: {
							settings: {
								DashboardGlobals: {
									services: '/services'
								}
							}
						}
					}
				};
			},
			(dispatch) => {
				return mockAxios;
			}
		);
};
