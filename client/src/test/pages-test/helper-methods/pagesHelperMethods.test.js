import { validateReportName, validateDesignerName } from "../../../pages/utils/helperMethods";

describe('validateReportName', () => {
    test('should return error if name is less than 3 characters', () => {
        expect(validateReportName('ab')).toBe('Report name should be atleast 3 characters long');
    });

    test('should return error if name exceeds 60 characters', () => {
        expect(validateReportName('a'.repeat(61))).toBe('Report name should not exceed 60 characters');
    });

    test('should return false if name is valid with 3 characters', () => {
        expect(validateReportName('abc')).toBe(false);
    });

    test('should return false if name is valid with 60 characters', () => {
        expect(validateReportName('a'.repeat(60))).toBe(false);
    });

    test('should return false if name contains valid characters and length is within limit', () => {
        expect(validateReportName('Valid-Name_1.2')).toBe(false);
    });
});

describe('validateDesignerName', () => {
    test('should return error if name is less than 3 characters', () => {
        expect(validateDesignerName('ab')).toBe('Designer name should be atleast 3 characters long');
    });

    test('should return error if name exceeds 60 characters', () => {
        expect(validateDesignerName('a'.repeat(61))).toBe('Designer name should not exceed 60 characters');
    });

    test('should return false if name is valid with 3 characters', () => {
        expect(validateDesignerName('abc')).toBe(false);
    });

    test('should return false if name is valid with 60 characters', () => {
        expect(validateDesignerName('a'.repeat(60))).toBe(false);
    });

    test('should return false if name contains valid characters and length is within limit', () => {
        expect(validateDesignerName('Valid-Name_1.2')).toBe(false);
    });
});