import React from 'react';
import { render, screen } from '@testing-library/react';
import HCRFiltersDrawer from '../../components/hi-canned-reports/hcrFilters/hcrFiltersDrawer';
import { checkRelativeDateFilter } from '../../utils/filter-utils'; 
import moment from 'moment';

jest.mock('moment', () => {
  const originalMoment = jest.requireActual('moment');
  return (timestamp) => originalMoment(timestamp || '2025-06-13T12:00:00.000Z');
});

describe('Checking HCR RelativeDateFilter', () => {

  describe('In HCR when called with an array of filter objects', () => {
    
    test('should return an empty array if the input is an empty array', () => {
        const result = checkRelativeDateFilter([]);
        expect(result).toEqual([]);
      });
  });

  describe('when handling invalid or edge-case input', () => {
    test('should return the filter unchanged if `values` property is not an array', () => {
      const invalidFilter = { label: 'Date', values: 'TODAY' };
      const result = checkRelativeDateFilter(invalidFilter);
      expect(result).toEqual(invalidFilter);
    });

    test('should return the filter unchanged if `values` property is missing', () => {
        const invalidFilter = { label: 'Date' };
        const result = checkRelativeDateFilter(invalidFilter);
        expect(result).toEqual(invalidFilter);
      });
  });

  describe('when Passing string parsing', () => {

    test('should NOT modify a string that just starts with TODAY', () => {
      const inputFilter = { values: ['TODAY_IS_A_GOOD_DAY'] };
      const result = checkRelativeDateFilter(inputFilter);
      expect(result.values).toEqual(['TODAY_IS_A_GOOD_DAY']);
    });
});
});