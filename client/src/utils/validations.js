export const emptyValidation = 'Please fill out this field.';
export const radioButton = 'radio-button';
export const radioEmptyValidtion = 'Please pick an item!';

export const loginValidations = (rule, value = '') => {
	// console.log(value);
	if (value.length === 0) {
		if (rule.field === radioButton) {
			return Promise.reject(new Error(radioEmptyValidtion));
		}
		return Promise.reject(new Error(emptyValidation));
	}
	// if (rule.field === 'username') {
	//     if (/[0-9]|\s/.test(value.charAt(0))) {
	//         return Promise.reject(new Error(`${rule.field} must not contain Number|Space.`));
	//     }
	// }
	// if (value.length < 6 || value.length > 20) {
	//     return Promise.reject(new Error(`${rule.field} must contain 6 to 20 characters.`));
	// }
	return Promise.resolve(null);
};
