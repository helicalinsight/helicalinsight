/**
 * 
 * @param {name} metadata filename
 * @returns false if name is valid, else returns error message
 */
export const validateMetadataName = (name) => {
    if(name.length < 3){
        return 'Metadata name should be atleast 3 characters long'
    }
    if(name.length > 60){
        return 'Metadata name should not exceed 60 characters'
    }
    if(!(/^\w[\w.\-&#+~]*/i).test(name)){
        return 'Please provide valid Metadata name'
    }
    return false
}