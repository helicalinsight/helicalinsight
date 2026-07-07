
export const toTitleCase = str =>{
    str = str.split("")
    str[0] =str[0].toUpperCase()
    str= str.join("")
    return str
}

export const toCapitalize = str =>{
    str = str.split("")
    str = str.map((char,i) => i === 0 ? char.toUpperCase() : char.toLowerCase())
    str= str.join("")
    return str
}