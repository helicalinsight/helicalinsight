


import { fireEvent, render } from "@testing-library/react";
import { getFieldInfo } from "../../../components/hi-reports/hi-viz-area/utils/grid-chart-utils";
import { ShowMore } from "../../../components/hi-reports/hi-viz-area/viz-tooltip";
import { grid_chart_config_with_dateTime, grid_chart_config_with_date, grid_chart_config_with_time } from "./mock.data"
describe("Chart utilities", () => {
    test("Grid chart configuration width date time", async () => {
        let formdata = getFieldInfo(grid_chart_config_with_dateTime.args)
        expect(formdata).toEqual(grid_chart_config_with_dateTime.output);
    });
    test("Grid chart configuration with date", async () => {
        let formdata = getFieldInfo(grid_chart_config_with_date.args)
        expect(formdata).toEqual(grid_chart_config_with_date.output);
    });
    test("Grid chart configuration with time", async () => {
        let formdata = getFieldInfo(grid_chart_config_with_time.args)
        expect(formdata).toEqual(grid_chart_config_with_time.output);
    });
});

describe('Test ShowMore Component', () => {
    test('should render the "More" text when show prop is false', () => {
        const { getByText } = render(<ShowMore show={false} />);
        expect(getByText('...More')).toBeTruthy();
    });

    test('should render the "Less" text when show prop is true', () => {
        const { getByText } = render(<ShowMore show={true} />);
        expect(getByText('Less')).toBeTruthy();
    });

    test('should call onClick handler when clicked', () => {
        const onClickMock = jest.fn();
        const { getByText } = render(<ShowMore show={false} onClick={onClickMock} />);

        fireEvent.click(getByText('...More'));
        expect(onClickMock).toHaveBeenCalledTimes(1);
    });
});