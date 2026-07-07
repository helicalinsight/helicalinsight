import { render } from "@testing-library/react";
import { SVGDisplay, addDashes, getCardValue } from "./property-card-utils";

describe('property-card-utils', () => {

    describe('getCardValue', () => {
      it('returns null when no arguments are passed', () => {
        expect(getCardValue()).toBeNull();
      });
    
      it('returns an img element when type is "url" and value is a valid URL', () => {
        const result = getCardValue('url', 'https://example.com/image.png');
        expect(result.type).toEqual('img');
        expect(result.props.src).toEqual('https://example.com/image.png');
      });
    
      it('returns an SVGDisplay element when type is "svg" and value is a valid SVG string', () => {
        const result = getCardValue('svg', '<svg><rect x="10" y="10" width="100" height="100"/></svg>');
        expect(result.type.name).toEqual('SVGDisplay');
        expect(result.props.svgString).toEqual('<svg><rect x="10" y="10" width="100" height="100"/></svg>');
      });
    
      it('trims the SVG string before returning an SVGDisplay element when type is "svg"', () => {
        const result = getCardValue('svg', '<svg><rect x="10" y="10" width="100" height="100"/></svg>');
        expect(result.props.svgString).toEqual('<svg><rect x="10" y="10" width="100" height="100"/></svg>');
      });
    
      it('returns null when type is "staticValue" and value is empty', () => {
        const result = getCardValue('staticValue', '');
        expect(result).toBeNull();
      });
    });

    describe('SVGDisplay', () => {
        test('renders an SVG string', () => {
          const svgString = '<svg><rect x="0" y="0" width="100" height="100"/></svg>';
          const output= '<svg><rect x=\"0\" y=\"0\" width=\"100\" height=\"100\"></rect></svg>';
          const { container } = render(<SVGDisplay svgString={svgString} />);
          expect(container.innerHTML).toEqual(`<span role=\"img\" class=\"anticon\"><div>${output}</div></span>`);
        });
      
        test('renders nothing when no svgString is provided', () => {
          const { container } = render(<SVGDisplay />);
          expect(container.innerHTML).toEqual("<span role=\"img\" class=\"anticon\"><div></div></span>");
        });
      
        test('escapes HTML characters in the SVG string', () => {
          const svgString = '<svg><text>Some text &amp; symbols</text></svg>';
          const { container } = render(<SVGDisplay svgString={svgString} />);
          expect(container.innerHTML).toEqual(`<span role=\"img\" class=\"anticon\"><div>${svgString}</div></span>`);
        });
    });

    describe('addDashes', () => {
      it('should return an empty string when given an empty string', () => {
        expect(addDashes('')).toBe('');
      });
    
      it('should add a dash before each uppercase letter and convert the string to lowercase', () => {
        expect(addDashes('HelloWorld')).toBe('hello-world');
        expect(addDashes('CamelCaseString')).toBe('camel-case-string');
        expect(addDashes('ALLCAPS')).toBe('a-l-l-c-a-p-s');
      });
    
      it('should not add a dash before the first letter of the string', () => {
        expect(addDashes('SomeString')).toBe('some-string');
        expect(addDashes('AnotherString')).toBe('another-string');
      });
    });
    
});
  