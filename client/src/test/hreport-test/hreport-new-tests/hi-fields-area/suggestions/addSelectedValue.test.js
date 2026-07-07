import { addSelectedValue } from "../../../../../components/hi-reports/hi-fields-area/utils/suggetions";

describe('addSelectedValue', () => {
    test('should replace the cursor position with the selected value', () => {
      document.body.innerHTML = '<input id="ip" value="SUM(A1, B2)">' 
      const currentValue = { value: 'C3', type: 'column' }
      const lastCursorPos = 7
      const result = addSelectedValue(currentValue, lastCursorPos)
      expect(result.value).toBe("SUM(A1,C3)")
      expect(result.cursorPos).toBe(7 + currentValue.value.length)
    })
  })
  
  