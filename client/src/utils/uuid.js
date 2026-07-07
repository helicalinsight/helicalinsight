export const uuid = (short = false) => {
    if (short === 'short') {
        return Math.random().toString(36).substring(2, 7)
    }
    return Math.random().toString(36).substring(2, 6)
        + '-' + Math.random().toString(36).substring(2, 6)
        + '-' + Math.random().toString(36).substring(2, 6)
        + '-' + Math.random().toString(36).substring(2, 6)
        + '-' + Math.random().toString(36).substring(2, 4)
}

export const shortId = () => Math.random().toString(36).substring(2, 7)