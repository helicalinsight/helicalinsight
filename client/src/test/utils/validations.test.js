import { emptyValidation, loginValidations, radioButton, radioEmptyValidtion } from '../../utils/validations';

describe('Checking loginValidations function : ', () => {
	describe('Checking Login Page : ', () => {
		// describe('Checking username validations : ', () => {
		test("handle when username length is 'ZERO'", () => {
			return expect(loginValidations({ field: 'username' }, '')).rejects.toThrow(emptyValidation);
		});
		// });

		// describe('Checking PASSWORD validations : ', () => {
		test("handle when password length is 'ZERO'", () => {
			return expect(loginValidations({ field: 'password' }, '')).rejects.toThrow(emptyValidation);
		});
		// });

		test('handle when username and password satisfies validation ', (done) => {
			expect(loginValidations({ field: 'username' }, 'abcdef')).resolves.toBeNull();
			expect(loginValidations({ field: 'password' }, 'abcdef')).resolves.toBeNull();
			done();
		});
	});

	describe('Checking SAML Login Page : ', () => {
		test('handle when no item is Picked // selected', () => {
			return expect(loginValidations({ field: radioButton }, '')).rejects.toThrow(radioEmptyValidtion);
		});

		test('handle when (saml login page || radio-button) satisfies validation ', (done) => {
			expect(loginValidations({ field: radioButton }, 'abcdef')).resolves.toBeNull();
			done();
		});
	});
});

// describe('Checking username validations : ', () => {
//   test("handle when username length is 'ZERO'", () => {
//     return expect(loginValidations({ field: 'username' }, '')).rejects.toThrow(emptyValidation);
//   });
//   // test("handle when username starts with 'SPACE'", () => {
//   // 	return expect(loginValidations({ field: 'username' }, ' ')).rejects.toThrow(
//   // 		/must not contain Number|Space/i
//   // 	);
//   // });
//   // test("handle when username starts with 'NUMBER'", () => {
//   // 	return expect(loginValidations({ field: 'username' }, '1')).rejects.toThrow(
//   // 		/must not contain Number|Space/i
//   // 	);
//   // });
//   // test("handle when username length is less than 'SIX'", () => {
//   // 	return expect(loginValidations({ field: 'username' }, 'abc')).rejects.toThrow(
//   // 		/must contain 6 to 20 characters/i
//   // 	);
//   // });
//   // test("handle when username length is greater than 'TWENTY'", () => {
//   // 	return expect(loginValidations({ field: 'username' }, 'abcdefghijklmnopqrstu')).rejects.toThrow(
//   // 		/must contain 6 to 20 characters/i
//   // 	);
//   // });
// });

// describe('Checking PASSWORD validations : ', () => {
//   test("handle when username length is 'ZERO'", () => {
//     return expect(loginValidations({ field: 'username' }, '')).rejects.toThrow(emptyValidation);
//   });
//   test('handle when password starts with SPACE ', () => {
//     return expect(loginValidations({ field: 'password' }, ' bcdef')).resolves.toBeNull();
//   });
//   test('handle when password starts with NUMBER ', () => {
//     return expect(loginValidations({ field: 'password' }, '1bcdef')).resolves.toBeNull();
//   });
// });
