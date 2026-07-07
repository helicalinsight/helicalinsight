import { isObject } from './is-object'

export const classnames = (...args) => {
    let r = ''
    args.map(e => {
        if (typeof e === 'string') {
            r += `${e} `
        }
        else if (isObject(e)) {
            Object.keys(e).map(key => {
                if (e[key] && typeof e[key] === 'boolean') {
                    r += `${key} `
                }
            })
        }
    })
    return r
}
