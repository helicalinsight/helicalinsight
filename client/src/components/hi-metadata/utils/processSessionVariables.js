export const processSessionVariables = (res) => {
    let queryTypes = Object.keys(res)
    queryTypes.forEach((type) => {
        let expKeys = Object.keys(res[type].expressions)
        if(atob){
            res[type].helpText = atob(res[type].helpText)
        }
        expKeys.forEach(eachKey => {
            res[type].expressions[eachKey] = res[type].expressions[eachKey].map(value => {
                if (eachKey === 'globalOptions')
                    return value.split(' ').join('');
                return '${' + eachKey + '}.' + value
            })
        })
    })
    return res
}