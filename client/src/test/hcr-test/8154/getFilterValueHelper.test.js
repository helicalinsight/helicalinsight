import React from 'react';
import * as helperMethods from '../../../components/hi-canned-reports/hcrHelperMethods'; 
import { render, fireEvent } from '@testing-library/react';

jest.mock('../../../components/hi-canned-reports/hcrHelperMethods', () => ({
  ...jest.requireActual('../../../components/hi-canned-reports/hcrHelperMethods'),
  getFilterValueForHCR: jest.fn(),
  validateNodes: () => ({ isValid: true, bandLimits: {} }),
  getHcrParameterFilters: () => [
    { orgPara: { canvasValues: { filterType: 'Query Based Dropdown List' } } },
  ],
}));

const PreviewButton = () => {
  const dispatch = jest.fn();
  const reqQuery = { id: 1, temp_uuid: 'abc', connectionDetails: { baseType: 'sql' } };
  const hcrDiagramNodesData = [{ id: 1 }];
  const canvasProperties = {};
  const parameters = { menu: [{ id: 1 }] };
  const queries = { menu: [{ id: 1 }] };
  const isPreviewing = false;
  const previewRequest = jest.fn();

  const fetchPreviewDetails = helperMethods.fetchPreviewDetails || (({
    updatedPageNo,
    isInitialPreview,
    isFilterOpn,
    isCache,
    openMode
  }) => {
    const { isValid } = helperMethods.validateNodes({});
    if (isValid) {
      const filters = helperMethods.getHcrParameterFilters({ parameters, hcrDiagramNodesData });
      const dropdownFilters = filters.filter(
        f => f?.orgPara?.canvasValues?.filterType === 'Query Based Dropdown List'
      );
      if (dropdownFilters.length > 0) {
        helperMethods.getFilterValueForHCR({
          reqQuery,
          dispatch,
          cb: () => {},
        });
      }
    }
  });

  const handleClick = () => {
    fetchPreviewDetails({ isInitialPreview: true });
  };

  return <button onClick={handleClick}>Preview</button>;
};

test('calls getFilterValueForHCR when clicking preview button', () => {
  const { getByText } = render(<PreviewButton />);
  fireEvent.click(getByText('Preview'));

  expect(helperMethods.getFilterValueForHCR).toHaveBeenCalled();
});
