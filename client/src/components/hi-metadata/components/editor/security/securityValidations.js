export const emptyValidation = 'Please fill out this field.';
export const radioButton = 'radio-button';
export const radioEmptyValidtion = 'Please pick an item!';

export const securityValidations = (rule, value = '', edit) => {
	if (rule.field === 'Expression Name') {
		if (!(value.length && /^\w(\*|\[|\]|\w|\.|'|\$|\(|\)|_|-| )*$/.test(value) && value.length <= 100)) {
			return Promise.reject(
				new Error(
					"Please enter expression name (starts with alphanumerics/underscore(_) and contains these .$()[]'_-* characters) and should not be more than 100 characters"
				)
			);
		}
	} else if ([ 'Condition', 'Filter' ].includes(rule.field)) {
		return Promise.reject(new Error(`Please enter ${rule.field} expression`));
	} else if (rule.field === 'Entity Names') {
		if (!value) {
			return Promise.reject(
				new Error(
					edit
						? 'Please select entity name by clicking Add more...'
						: 'Please select Table/Column/View to apply security'
				)
			);
		}
	}
	return Promise.resolve(null);
};
