import { jwtAuth, jwtAxiosObject, jwtDetails, normalAxiosObject, reportDetails, ssoAxiosObject, ssoDetails } from './embed-constants';
import { dispatch } from '../../__mocks__/axios';
import { checkReport, handleAuthDetails, handleAxiosObject } from '../../app/helperMethods';

describe('Testing handleAxiosObject function without JWT', () => {
	test('handle when NO JWT details passed', (done) => {
		expect(JSON.stringify(handleAxiosObject({ dispatch }))).toBe(JSON.stringify(normalAxiosObject));
		done();
	});
});
// jwt
describe('Testing handleAxiosObject function with jWT', () => {
	const { crtDetails, withOutBaseURL } = jwtDetails;
	const { crt, withNoBaseURL } = jwtAxiosObject;
	test('handle when JWT correct details passed', (done) => {
		expect(JSON.stringify(handleAxiosObject({ ...crtDetails, dispatch }))).toBe(JSON.stringify(crt));
		done();
	});

	test('handle when JWT withOutBaseURL details passed', (done) => {
		expect(JSON.stringify(handleAxiosObject({ ...withOutBaseURL, dispatch }))).toBe(JSON.stringify(withNoBaseURL));
		done();
	});
});

// sso-token
describe('Testing handleAxiosObject function with sso', () => {
	const { crtDetails, withOutBaseURL } = ssoDetails;
	const { crt, withNoBaseURL } = ssoAxiosObject;
	test('handle when SSO correct details passed', (done) => {
		expect(JSON.stringify(handleAxiosObject({ ...crtDetails, dispatch }))).toBe(JSON.stringify(crt));
		done();
	});

	test('handle when SSO withOutBaseURL details passed', (done) => {
		expect(JSON.stringify(handleAxiosObject({ ...withOutBaseURL, dispatch }))).toBe(JSON.stringify(withNoBaseURL));
		done();
	});
});


describe('Testing checkReport function', () => {
	const { crt, withOutDir, withOutFile } = reportDetails;
	test('handle when correct report sending', (done) => {
		expect(checkReport({ report: crt, auth: {} })).toBe(true);
		done();
	});
	test('handle when report with no DIR sending', (done) => {
		expect(checkReport({ report: withOutDir, auth: {} })).toBe(false);
		done();
	});
	test('handle when report with no FILE sending', (done) => {
		expect(checkReport({ report: withOutFile, auth: {} })).toBe(false);
		done();
	});
	test('handle when no report is sending', (done) => {
		expect(checkReport({ report: undefined, auth: {} })).toBe(false);
		done();
	});
});

describe('Testing handleAuthDetails function', () => {
	describe('Testing JWT details', () => {
		const { crtJwtAuth, withOutType, withOutAuthToken } = jwtAuth;
		test('handle when correct JWT auth details sending', (done) => {
			expect(handleAuthDetails({ auth: crtJwtAuth, report: {} })).toBe(true);
			done();
		});
		test('handle when JWT with no TYPE is sending', (done) => {
			expect(handleAuthDetails({ auth: withOutType, report: {} })).toBe(false);
			done();
		});
		test('handle when JWT with no AUTHTOKEN is sending', (done) => {
			expect(handleAuthDetails({ auth: withOutAuthToken, report: {} })).toBe(false);
			done();
		});
		test('handle when JWT with no auth obj but with report obj is sending', (done) => {
			expect(handleAuthDetails({ auth: '', report: {} })).toBe(false);
			done();
		});
	});
});
