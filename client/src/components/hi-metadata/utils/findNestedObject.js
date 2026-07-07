/*
* to find a nested object in metadata section
* @data In this function we need to pass the entire object , a key and the value of object and it is called whenever we select a table in metadata section through nested objects.
* return: nestedValue (return the object that match the key value from the entire object)
*/
export const findNestedObj = (entireObj, keyToFind, valToFind) => {
   
    let foundObj;
    JSON.stringify(entireObj, (_, nestedValue) => {
        if (nestedValue && nestedValue[keyToFind] === valToFind) {
            foundObj = nestedValue;
        }
        return nestedValue;
    });
    return foundObj;
};