import { getGlobalVariables } from "../../../utils/utilities";

describe("Global variables function test",()=>{
    const dispatchMock = jest.fn(()=>{
        return {getState : jest.fn().mockReturnValue({
        app: {
          applicationSettingsData: {
            userData: { user: { name: "John Doe", age: 25 } }
          }
        },
        designer: {
          present: {
            gridItemsData: [
              {
                id: "1",
                reportInfo: {
                  file: {
                    title: "My Report"
                  }
                }
              }
            ]
          }
        },
        dashboard: {
          present: {
            dashboardVariables: { var1: "value1", var2: "value2" }
          }
        }
      })
    }
    });
    const result = getGlobalVariables({ dispatch: dispatchMock, level: "reportLevel", id: "1" });
    test("default test",()=>{
        expect(result).toEqual({
               "report": undefined,
               "user": undefined,
        });
        
    })
    

})