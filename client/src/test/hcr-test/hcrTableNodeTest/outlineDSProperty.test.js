import '@testing-library/jest-dom';
import { fireEvent, render, screen } from '@testing-library/react';
import "core-js/stable";
import OutlineDSFieldProperties from '../../../components/hi-canned-reports/hcrCanvas/advanceComponents/components/outlineDSFieldProperties';
import { EditorPanels } from '@ant-design/flowchart';

const baseTableData = {
    outlineDSFields: [
        { id: 'field-1', name: 'Field One', clazz: 'classA' },
        { id: 'field-2', name: 'Field Two', clazz: 'classB' },
    ],
};

const baseClassNames = {
    'Class A': 'classA',
    'Class B': 'classB',
};

const baseProps = {
    EditorPanels,
    outlineDsSelectedField: 'field-1',
    onChange: jest.fn(),
    tableData: baseTableData,
    classNames: baseClassNames,
    dispatch: jest.fn(),
};

describe('test OutlineDSFieldProperties component', () => {

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


    describe('Returns null when field is not found', () => {
        it('returns null when outlineDsSelectedField does not match any field', () => {
            const { container } = render(<OutlineDSFieldProperties {...baseProps} outlineDsSelectedField='non-existent-id' />);
            expect(container.firstChild).toBeNull();
        });

        it('returns null when outlineDSFields is empty', () => {
            const { container } = render(<OutlineDSFieldProperties {...baseProps} tableData={{ outlineDSFields: [] }} />);
            expect(container.firstChild).toBeNull();
        });

        it('returns null when tableData is undefined', () => {
            const { container } = render(
                <OutlineDSFieldProperties {...baseProps} tableData={undefined} />
            )
            expect(container.firstChild).toBeNull();
        });

        it('returns null when outlineDsSelectedField is undefined', () => {
            const { container } = render(
                <OutlineDSFieldProperties {...baseProps} outlineDsSelectedField={undefined} />
            )
            expect(container.firstChild).toBeNull();
        });
    });
});