import { render, screen } from "@testing-library/react";
import { useFieldSelection } from "../../../hooks/useHCRFieldsSelection";

jest.mock('antd', () => ({
  Divider: () => <div data-testid="divider" />,
  Row: ({ children, justify }) => <div data-testid="row" data-justify={justify}>{children}</div>,
  Button: ({ children, onClick, type }) => (
    <button data-testid="button" data-type={type} onClick={onClick}>{children}</button>
  ),
}));

const TestComponent = ({ fields = [], columnFields = [], onPropChange = jest.fn() }) => {
  const field = useFieldSelection({ selectedFields: fields, selectedColumnFields: columnFields, onPropertyChange: onPropChange });
  return (
    <div>
      <div data-testid="available">{JSON.stringify(field.getAvailableColumnFields())}</div>
      <div data-testid="filtered">{JSON.stringify(field.getFields([{name:'FirstName'},{name:'LastName'},'Email'],'first'))}</div>
    </div>
  );
};

describe('useFieldSelection', () => {
  const mockOnPropertyChange = jest.fn();
  beforeEach(() => jest.clearAllMocks());
    it('should mapped fields with label and value', () => {
      render(<TestComponent fields={['field1','field2','field3']} onPropChange={mockOnPropertyChange} />);
      expect(JSON.parse(screen.getByTestId('available').textContent)).toEqual([
        { label: 'field1', value: 'field1' }, { label: 'field2', value: 'field2' }, { label: 'field3', value: 'field3' }
      ]);
    });

    it('its should return empty array when no fields', () => {
      render(<TestComponent fields={[]} onPropChange={mockOnPropertyChange} />);
      expect(JSON.parse(screen.getByTestId('available').textContent)).toEqual([]);
    });
      it('it should filter fields based on the search', () => {
    render(<TestComponent onPropChange={mockOnPropertyChange} />);
    expect(JSON.parse(screen.getByTestId('filtered').textContent)).toEqual([{ name: 'FirstName' }]);
  });
});