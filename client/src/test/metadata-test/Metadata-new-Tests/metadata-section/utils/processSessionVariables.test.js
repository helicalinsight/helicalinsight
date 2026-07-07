import { processSessionVariables } from "../../../../../components/hi-metadata/utils/processSessionVariables";

// describe("processSessionVariables function", () => {
//   test("checking the functionality of processSessionVariables function", () => {
//     const res = {
        
//             hasStoredProcedure: {
//                 expressions: {
//                     user: [
//                         "${user}.id",
//                         "${user}.email",
//                         "${user}.enabled",
//                         "${user}.name",
//                         "${user}.isExternalUser"
//                     ],
//                     org: [
//                         "${org}.id",
//                         "${org}.name"
//                     ],
//                     profile: [
//                         "${profile}.name",
//                         "${profile}.id",
//                         "${profile}.value"
//                     ],
//                     role: [
//                         "${role}.id",
//                         "${role}.name"
//                     ]
//                 },
//                 tooltip: "Stored procedure",
//                 placeHolder: "call  yourStoredProcedure('param1','param2',...)",
//                 helpText: "You can execute a stored procedure"
//     }
// };
    
//     const expectedResult = {
      
//     };

//     let result = processSessionVariables((JSON.stringify(res)));
//     console.log(expectedResult)
   
//     expect(true).toEqual(true);
//   });
// });


describe('processSessionVariables', () => {
    it('should correctly process session variables', () => {
      const input = {
        queryType1: {
          expressions: {
            expression1: ['value1', 'value2'],
            expression2: ['value3']
          },
          helpText: 'aGVsbG8gd29ybGQ=' // 'hello world' in base64
        },
        queryType2: {
          expressions: {
            expression3: ['value4']
          },
          helpText: 'c29tZXRoaW5nIGhlbHAgdGV4dA==' // 'something help text' in base64
        }
      };
      
      const expectedOutput = {
        queryType1: {
          expressions: {
            expression1: ['${expression1}.value1', '${expression1}.value2'],
            expression2: ['${expression2}.value3']
          },
          helpText: 'hello world'
        },
        queryType2: {
          expressions: {
            expression3: ['${expression3}.value4']
          },
          helpText: 'something help text'
        }
      };
      
      const output = processSessionVariables(input);
      
      expect(output).toEqual(expectedOutput);
    });
  });
  