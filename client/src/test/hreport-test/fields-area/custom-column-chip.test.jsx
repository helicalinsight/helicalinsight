import '../../utils/mockJsdom';
import { fireEvent, render, screen, waitFor, within } from '@testing-library/react';
import { Provider } from 'react-redux';

jest.setTimeout(30000);

const __originalFetch = global.fetch;
global.fetch = jest.fn((url, ...rest) => {
    const urlStr = String(url);
    if (urlStr.includes('.wasm') || urlStr.includes('muze')) {
        return new Promise(() => {});
    }
    return __originalFetch ? __originalFetch(url, ...rest) : Promise.reject(new Error('fetch not available'));
});
afterAll(() => {
    global.fetch = __originalFetch;
});
const CustomColumnChip = require('../../../components/hi-reports/hi-fields-area/custom-column-chip').default;

const textType = { backendDataType: 'java.lang.String', dataType: 'text' };
const numericType = { backendDataType: 'java.lang.Integer', dataType: 'numeric' };

const createMockStore = (functions = {}) => ({
    getState: () => ({
        hreport: { present: { reports: [{ active: true, functions }] } },
    }),
    dispatch: jest.fn(),
    subscribe: () => () => {},
});

const renderWithProvider = (ui, functions) =>
    render(<Provider store={createMockStore(functions)}>{ui}</Provider>);

describe('CustomColumnChip', () => {
    test('renders alias with text icon and discrete (blue) color', () => {
        const { container } = renderWithProvider(<CustomColumnChip alias="my_col" type={textType} />);
        expect(screen.getByTestId('hr-custom-column-chip-label').textContent).toBe('my_col');
        const iconWrap = screen.getByTestId('hr-custom-column-data-type-icon');
        expect(within(iconWrap).getByLabelText('file-text')).toBeTruthy();
        const tag = container.querySelector('.ant-tag');
        expect(tag.style.backgroundColor).toMatch(/51,\s*122,\s*183/);
    });

    test('renders numeric icon with continuous (green) color', () => {
        const { container } = renderWithProvider(<CustomColumnChip alias="num_col" type={numericType} />);
        const iconWrap = screen.getByTestId('hr-custom-column-data-type-icon');
        expect(within(iconWrap).getByLabelText('number')).toBeTruthy();
        const tag = container.querySelector('.ant-tag');
        expect(tag.style.backgroundColor).toMatch(/76,\s*175,\s*80/);
    });

    test('shows placeholder when alias is empty', () => {
        renderWithProvider(<CustomColumnChip alias="" type={textType} />);
        const label = screen.getByTestId('hr-custom-column-chip-label');
        expect(label.textContent).toBe('Custom column');
        expect(label.className).toMatch('custom-column-chip-placeholder');
    });

    test('double-click opens rename and Enter commits trimmed value', () => {
        const onAliasChange = jest.fn();
        renderWithProvider(<CustomColumnChip alias="old" type={textType} onAliasChange={onAliasChange} />);
        fireEvent.doubleClick(screen.getByTestId('hr-custom-column-chip-label'));
        const input = screen.getByTestId('hr-custom-column-rename-input');
        fireEvent.change(input, { target: { value: '  new_alias  ' } });
        fireEvent.keyDown(input, { keyCode: 13 });
        expect(onAliasChange).toHaveBeenCalledWith('new_alias');
    });

    test('Escape cancels rename without committing', () => {
        const onAliasChange = jest.fn();
        renderWithProvider(<CustomColumnChip alias="old" type={textType} onAliasChange={onAliasChange} />);
        fireEvent.doubleClick(screen.getByTestId('hr-custom-column-chip-label'));
        const input = screen.getByTestId('hr-custom-column-rename-input');
        fireEvent.change(input, { target: { value: 'changed' } });
        fireEvent.keyDown(input, { keyCode: 27 });
        expect(onAliasChange).not.toHaveBeenCalled();
        expect(screen.getByTestId('hr-custom-column-chip-label').textContent).toBe('old');
    });

    test('datatype icon opens capitalized menu and selection calls back', async () => {
        const onDataTypeChange = jest.fn();
        renderWithProvider(<CustomColumnChip alias="c" type={textType} onDataTypeChange={onDataTypeChange} />);
        fireEvent.click(screen.getByTestId('hr-custom-column-data-type-icon'));
        const numericItem = await screen.findByText('Numeric');
        expect(screen.getByText('Text')).toBeTruthy();
        expect(screen.getByText('Boolean')).toBeTruthy();
        expect(screen.queryByText('numeric')).toBeFalsy();
        fireEvent.click(numericItem);
        await waitFor(() => expect(onDataTypeChange).toHaveBeenCalledWith(
            { backendDataType: 'java.lang.Integer', dataType: 'numeric' }
        ));
    });

    test('allowRename=false blocks inline rename', () => {
        renderWithProvider(<CustomColumnChip alias="old" type={textType} allowRename={false} />);
        fireEvent.doubleClick(screen.getByTestId('hr-custom-column-chip-label'));
        expect(screen.queryByTestId('hr-custom-column-rename-input')).toBeFalsy();
    });

    test('allowDataTypeChange=false shows no menu', async () => {
        renderWithProvider(<CustomColumnChip alias="c" type={textType} allowDataTypeChange={false} />);
        fireEvent.click(screen.getByTestId('hr-custom-column-data-type-icon'));
        await waitFor(() => expect(screen.queryByText('Numeric')).toBeFalsy());
    });
});
