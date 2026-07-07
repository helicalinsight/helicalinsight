import { render, fireEvent, screen } from '@testing-library/react';
import { Provider } from 'react-redux';
import { DndProvider } from 'react-dnd';
import { HTML5Backend } from 'react-dnd-html5-backend';
import { configureStore } from '@reduxjs/toolkit';
import { hiMockAxios } from '../../../app/mock-axios';
import MeasureNamesAndValues from '../../../components/hi-reports/hi-editing-area/components/values/measure-names-and-values/measure-names-and-values';
import reducers from '../../../redux';
import { getIndexForNewValue, getValueObjectForMeasure, measureDataUpdateFn } from '../../../components/hi-reports/utils/base';
import { MEASURE_NAME, MEASURE_VALUE, initialMeasureFields } from '../../../redux/reducers/initialStates';

const App = (props) => {
    return (
        <DndProvider backend={HTML5Backend}>
            <Provider store={props.store}>
                {props.children}
            </Provider>
        </DndProvider>
    );
};

describe('To Test MeasureNamesAndValues Component', () => {
    beforeAll(() => {
        delete window.matchMedia
        window.matchMedia = (query) => ({
            matches: false,
            media: query,
            onchange: null,
            addListener: jest.fn(), // deprecated
            removeListener: jest.fn(), // deprecated
            addEventListener: jest.fn(),
            removeEventListener: jest.fn(),
            dispatchEvent: jest.fn(),
        })
        window.createObjectURL = jest.fn();
        window.HTMLElement.prototype.scrollBy = jest.fn();
        window.crypto = {};
        window.crypto.getRandomValues = arr => crypto.randomBytes(arr.length)
    });

    afterAll(() => {
        global.gc && global.gc()
    })

    const store = configureStore({
        reducer: reducers,
        middleware: (getDefaultMiddleware) =>
            getDefaultMiddleware({
                thunk: {
                    extraArgument: hiMockAxios
                },
                immutableCheck: false,
                serializableCheck: false,
            }),
    });

    it('should render without crashing', () => {
        render(
            <App store={store}>
                <MeasureNamesAndValues />
            </App>
        );
    });


    it('should render MeasureMarks and but not MeasureFields when enable is true', () => {
        const { getByText } = render(
            <App store={store}>
                <MeasureNamesAndValues measures={{ enable: true }} />
            </App>
        );
        expect(getByText('Measure Names and Values')).toBeTruthy();
        expect(screen.queryByTestId(/hr-measure_fields_card/i)).toBeFalsy();
        expect(screen.queryByTestId(/hr-measure_field_marks/i)).toBeTruthy();
    });

    it('should not render MeasureMarks and MeasureFields when enable is false', () => {
        const { queryByText } = render(
            <App store={store}>
                <MeasureNamesAndValues measures={{ enable: false }} />
            </App>
        );
        expect(queryByText('Measure Names and Values')).toBeNull();
        expect(screen.queryByTestId(/hr-measure_fields_card/i)).toBeFalsy();
        expect(screen.queryByTestId(/hr-measure_field_marks/i)).toBeFalsy();
    });
});


describe('to test getIndexForNewValue', () => {
    it('should return the first unused number in an empty array', () => {
        expect(getIndexForNewValue([])).toBe(0);
    });

    it('should return the first unused number in an array with one element', () => {
        expect(getIndexForNewValue([1])).toBe(2);
    });

    it('should return the first unused number in an array with multiple elements', () => {
        expect(getIndexForNewValue([1, 2, 3, 4])).toBe(5);
    });

    it('should return the first unused number in an array with repeated elements', () => {
        expect(getIndexForNewValue([1, 2, 2, 3, 4])).toBe(5);
    });
});


describe('to test getValueObjectForMeasure', () => {
    const customFrontendFields = [
        { column: MEASURE_NAME, alias: 'measureName' },
        { column: MEASURE_VALUE, alias: 'measureValue' },
        { column: 'other', alias: 'other' },
    ];

    it('should return an object with the measure name and value', () => {
        const name = 'testName';
        const value = 'testValue';
        const result = getValueObjectForMeasure(name, value, customFrontendFields);
        expect(result).toEqual({
            measureName: name,
            measureValue: value,
        });
    });

    it('should return an object with the measure name and value as empty,  if the value is empty', () => {
        const name = 'testName';
        const value = '';
        const result = getValueObjectForMeasure(name, value, customFrontendFields);
        expect(result).toEqual({
            measureName: name,
            measureValue: '',
        });
    });

    it('should return an object with the measure name as empty and value, if the name is empty', () => {
        const name = '';
        const value = 'testValue';
        const result = getValueObjectForMeasure(name, value, customFrontendFields);
        expect(result).toEqual({
            measureValue: value,
            measureName: ''
        });
    });

    it('should return an empty measure name and value,  if both name and value are empty', () => {
        const name = '';
        const value = '';
        const result = getValueObjectForMeasure(name, value, customFrontendFields);
        expect(result).toEqual({
            measureName: "",
            measureValue: "",
        });
    });

    it('should return an empty object , if customFrontendFields is empty', () => {
        const name = 'testName';
        const value = 'testValue';
        const result = getValueObjectForMeasure(name, value, []);
        expect(result).toEqual({});
    });
});

describe('measureDataUpdateFn', () => {
    it('should return the same data and metadata if measureFieldNames is empty', () => {
        const data = [
            { id: 1, name: 'John', age: 25 },
            { id: 2, name: 'Jane', age: 30 },
        ];
        const metadata = [
            { id: { name: 'ID', type: 'numeric' } },
            { name: { name: 'Name', type: 'text' } },
            { age: { name: 'Age', type: 'numeric' } },
        ];
        const measureFieldNames = [];
        const customFrontendFields = [];
        const result = measureDataUpdateFn(data, metadata, measureFieldNames, customFrontendFields);
        expect(result).toEqual({ data, metadata });
    });

    it('should return the same data and metadata if data is empty', () => {
        const data = [];
        const metadata = [
            { id: { name: 'ID', type: 'numeric' } },
            { name: { name: 'Name', type: 'text' } },
            { age: { name: 'Age', type: 'numeric' } },
        ];
        const measureFieldNames = ['age'];
        const customFrontendFields = [];
        const result = measureDataUpdateFn(data, metadata, measureFieldNames, customFrontendFields);
        expect(result).toEqual({ data, metadata });
    });

    it('should update the metadata and return the updated data and metadata', () => {
        const data = [
            { id: 1, name: 'John', age: 25 },
            { id: 2, name: 'Jane', age: 30 },
        ];
        const metadata = [
            {
                0: { name: 'ID', type: 'numeric' },
                1: { name: 'Name', type: 'text' },
                2: { name: 'age', type: 'numeric' }
            }
        ];
        const measureFieldNames = ['age'];
        const customFrontendFields = initialMeasureFields;
        const { data: mData, metadata: mMetadata } = measureDataUpdateFn(data, metadata, measureFieldNames, customFrontendFields);
        expect(mData).toEqual([
            { 'Measure Name': 'age', 'Measure Value': 25, id: 1, name: 'John' },
            { 'Measure Name': 'age', 'Measure Value': 30, id: 2, name: 'Jane' }
        ])
        expect(mMetadata).toEqual([
            {
                0: { name: 'ID', type: 'numeric' },
                1: { name: 'Name', type: 'text' },
                2: { name: 'Measure Name', type: 'numeric' },
                3: { name: 'Measure Value', type: 'numeric' },
            }
        ]);
    });
})