import { groupByKey } from "../../../../../components/hi-metadata/utils/views/groupByKey";

describe('groupByKey function', () => {
    it('should group objects by key returned from the provided function', () => {
        const objectArr = [
            { name: 'John', age: 25 },
            { name: 'Alice', age: 30 },
            { name: 'Bob', age: 25 }
        ];

        const fn = (obj) => obj.age;

        const expectedResult = {
            25: [
                { name: 'John', age: 25 },
                { name: 'Bob', age: 25 }
            ],
            30: [
                { name: 'Alice', age: 30 }
            ]
        };

        expect(groupByKey(objectArr, fn)).toEqual(expectedResult);
    });

    it('should handle empty array input', () => {
        const objectArr = [];
        const fn = (obj) => obj.age;

        expect(groupByKey(objectArr, fn)).toEqual({});
    });

    it('should handle custom key function', () => {
        const objectArr = [
            { name: 'John', age: 25 },
            { name: 'Alice', age: 30 },
            { name: 'Bob', age: 25 }
        ];

        // Grouping by the first character of the name
        const fn = (obj) => obj.name.charAt(0);

        const expectedResult = {
            J: [
                { name: 'John', age: 25 }
            ],
            A: [
                { name: 'Alice', age: 30 }
            ],
            B: [
                { name: 'Bob', age: 25 }
            ]
        };

        expect(groupByKey(objectArr, fn)).toEqual(expectedResult);
    });
});
