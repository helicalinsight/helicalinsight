import React from 'react'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import TooltipTemplateEditor from '../../../../../../../components/hi-reports/hi-editing-area/components/properties/tooltip-template-editor'

jest.mock('react-quill', () => {
    const React = require('react')
    const MockReactQuill = React.forwardRef(({ value, onChange, placeholder }, ref) => (
        <textarea
            data-testid="mock-quill-editor"
            value={value || ''}
            placeholder={placeholder}
            onChange={(e) => onChange && onChange(e.target.value)}
            ref={ref}
        />
    ))
    MockReactQuill.displayName = 'MockReactQuill'
    return MockReactQuill
})

jest.mock('react-quill', () => {
    const React = require('react')
    const MockQuill = React.forwardRef(({ value, onChange, placeholder }, ref) => (
        <textarea
            data-testid="mock-quill-editor"
            value={value || ''}
            placeholder={placeholder}
            onChange={(e) => onChange && onChange(e.target.value)}
            ref={ref}
        />
    ))
    MockQuill.displayName = 'MockQuill'
    MockQuill.Quill = { import: jest.fn(() => ({})) }
    return { __esModule: true, default: MockQuill, Quill: { import: jest.fn(() => ({})) } }
})

const defaultProps = {
    open: true,
    template: '<p>Hello World</p>',
    onClose: jest.fn(),
    onChange: jest.fn(),
    onReset: jest.fn()
}

describe('TooltipTemplateEditor', () => {
    beforeEach(() => {
        jest.clearAllMocks()
    })

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

    it('renders the drawer when open is true', () => {
        render(<TooltipTemplateEditor {...defaultProps} />)
        expect(screen.getByTestId('hi-tooltip-template-editor')).toBeTruthy()
    })

    it('does not render drawer content when open is false', () => {
        render(<TooltipTemplateEditor {...defaultProps} open={false} />)
        expect(screen.queryByTestId('hi-tooltip-template-editor')).not.toBeTruthy()
    })

    it('renders the drawer title "Tooltip Template"', () => {
        render(<TooltipTemplateEditor {...defaultProps} />)
        expect(screen.getByTestId('hi-tooltip-template-editor-drawer-title')).toBeTruthy()
        expect(screen.getByText('Tooltip Template')).toBeTruthy()
    })

    it('renders Save and Reset buttons', () => {
        render(<TooltipTemplateEditor {...defaultProps} />)
        expect(screen.getByTestId('hi-tooltip-template-editor-save-button')).toBeTruthy()
        expect(screen.getByTestId('hi-tooltip-template-editor-reset-button')).toBeTruthy()
    })

    it('calls onChange with the current template value when Save is clicked', async () => {
        render(<TooltipTemplateEditor {...defaultProps} />)

        const editor = screen.getByTestId('mock-quill-editor')
        fireEvent.change(editor, { target: { value: '<p>Updated content</p>' } })

        fireEvent.click(screen.getByTestId('hi-tooltip-template-editor-save-button'))

        await waitFor(() => {
            expect(defaultProps.onChange).toHaveBeenCalledWith('<p>Updated content</p>')
        })
    })

    it('calls onReset when Reset button is clicked', () => {
        render(<TooltipTemplateEditor {...defaultProps} />)
        fireEvent.click(screen.getByTestId('hi-tooltip-template-editor-reset-button'))
        expect(defaultProps.onReset).toHaveBeenCalledTimes(1)
    })

    it('calls onClose when the drawer is closed', () => {
        render(<TooltipTemplateEditor {...defaultProps} />)
        const closeButton = document.querySelector('.ant-drawer-close')
        if (closeButton) {
            fireEvent.click(closeButton)
            expect(defaultProps.onClose).toHaveBeenCalledTimes(1)
        }
    })

    it('initializes editor with the provided template', () => {
        render(<TooltipTemplateEditor {...defaultProps} />)
        const editor = screen.getByTestId('mock-quill-editor')
        expect(editor.value).toBe('<p>Hello World</p>')
    })

    it('updates editor content when template prop changes', async () => {
        const { rerender } = render(<TooltipTemplateEditor {...defaultProps} template="<p>Old</p>" />)

        rerender(<TooltipTemplateEditor {...defaultProps} template="<p>New</p>" />)

        await waitFor(() => {
            expect(screen.getByTestId('mock-quill-editor').value).toBe('<p>New</p>')
        })
    })

    it('renders with default empty template when no template prop is given', () => {
        render(<TooltipTemplateEditor open={true} />)
        const editor = screen.getByTestId('mock-quill-editor')
        expect(editor.value).toBe('')
    })
})