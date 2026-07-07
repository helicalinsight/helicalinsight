export function addSpacesInCamelCase(str){
    let newStr = "";
    let temp = "";
    for (let i=0; i<str.length; i++){
        let ch = str[i];
        if(i === 0){
            temp += ch; continue;
        }
        if(/[A-Z]/.test(ch)){
            newStr += temp + " "  ;
            temp = ch;
            continue;
        }
        temp += ch;
    }
    return newStr + temp;
}