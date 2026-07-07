import { getCurrentValue } from "../../../../../components/hi-reports/hi-fields-area/utils/suggetions";

describe("getCurrentValue", () => {
  let input;

  beforeEach(() => {
    input = document.createElement("input");
    input.setAttribute("id", "ip");
    document.body.appendChild(input);
  });

  afterEach(() => {
    document.body.removeChild(input);
  });

  test("should return the current value of the input field", () => {
    input.value = "booking_platform";

    const result = getCurrentValue();

    expect(result).toEqual("booking_platform");
  });
});
