//
import { authTypes } from '../../app/constants';
import { responseHandler } from '../../app/helperMethods';

const { JWT, DIRECT, SSO_TOKEN } = authTypes;

export const reportDetails = {
	crt: {
		dir: 'naresh',
		file: 'hreport.hr'
	},
	withOutDir: {
		file: 'hreport.hr'
	},
	withOutFile: {
		dir: 'naresh'
	}
};

export const normalAxiosObject = {
	transformRequest: (data) => {
		return data;
	},
	headers: {
		'X-Requested-With': 'XMLHttpRequest'
	},
	transformResponse: [
		function(data) {
			responseHandler({ dispatch });
			return data;
		}
	],
	baseURL: undefined
};
 // ------------------------------------------ jwt ------------------------
const jwtAuthToken =
	'Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJIZWxpY2FsSW5zaWdodCIsInN1YiI6ImhpYWRtaW4iLCJzY29wZXMiOiJST0xFX0FETUlOLFJPTEVfVVNFUixST0xFX1ZJRVdFUiIsImlhdCI6MTY1Mjg3NDU1OCwiZXhwIjoxNjcwODc0NTU4fQ.p-5QrCy5g5sAuqrI6mJmEK2OwNPh1ZSrpR8OwVK7Bfg';

export const jwtAuth = {
	crtJwtAuth: {
		type: JWT,
		authToken: jwtAuthToken
	},
	withOutType: {
		authToken: jwtAuthToken
	},
	withOutAuthToken: {
		type: JWT
	}
};

export const jwtDetails = {
	crtDetails: {
		baseURL: 'jwtBaseUrl',
		auth: jwtAuth.crtJwtAuth
	},
	withOutBaseURL: {
		auth: jwtAuth.crtJwtAuth
	}
};

//AUTH( JWT ), BASE_URL passed
export const jwtAxiosObject = {
	crt: {
		transformRequest: (data) => {
			return data;
		},
		headers: {
			'X-Requested-With': 'XMLHttpRequest',
			// Authorization: jwtDetails.crtDetails.auth.authToken,
			type:"jwt",
			authToken: jwtDetails.crtDetails.auth.authToken
		},
		transformResponse: [
			function(data) {
				responseHandler({ dispatch });
				return data;
			}
		],
		baseURL: jwtDetails.crtDetails.baseURL
	},
	withNoBaseURL: {
		transformRequest: (data) => {
			return data;
		},
		headers: {
			'X-Requested-With': 'XMLHttpRequest',
			// Authorization: jwtDetails.withOutBaseURL.auth.authToken,
			type:"jwt",
			authToken: jwtDetails.crtDetails.auth.authToken
		},
		transformResponse: [
			function(data) {
				responseHandler({ dispatch });
				return data;
			}
		]
		// baseURL: ''
	}
};

// -------------------------------------------  sso --------------------------------

const ssoAuthToken = 'jKbOeRDbUVRqOA4mNgjtTR6K6eBRra23cvc_cITZVWw';

export const ssoAuth = {
	crtSsoAuth: {
		type: SSO_TOKEN,
		authToken: ssoAuthToken
	},
	withOutType: {
		authToken: ssoAuthToken
	},
	withOutAuthToken: {
		type: SSO_TOKEN
	}
};

export const ssoDetails = {
	crtDetails: {
		baseURL: 'ssoBaseUrl',
		auth: ssoAuth.crtSsoAuth
	},
	withOutBaseURL: {
		auth: ssoAuth.crtSsoAuth
	}
};

//AUTH( SSO ), BASE_URL passed
export const ssoAxiosObject = {
	crt: {
		transformRequest: (data) => {
			return data;
		},
		headers: {
			'X-Requested-With': 'XMLHttpRequest',
			// Authorization: jwtDetails.crtDetails.auth.authToken,
			type:"token",
			authToken: ssoDetails.crtDetails.auth.authToken
		},
		transformResponse: [
			function(data) {
				responseHandler({ dispatch });
				return data;
			}
		],
		baseURL: ssoDetails.crtDetails.baseURL
	},
	withNoBaseURL: {
		transformRequest: (data) => {
			return data;
		},
		headers: {
			'X-Requested-With': 'XMLHttpRequest',
			// Authorization: jwtDetails.withOutBaseURL.auth.authToken,
			type:"token",
			authToken: ssoDetails.crtDetails.auth.authToken
		},
		transformResponse: [
			function(data) {
				responseHandler({ dispatch });
				return data;
			}
		]
		// baseURL: ''
	}
};

//-----------------------------------------------------   direct    --------------------------------------------

