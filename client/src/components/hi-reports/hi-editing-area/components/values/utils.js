export const getMarkTitle = (selectedType, subVizType, markType) => {
    if (selectedType === 'GridChart' && subVizType === 'text' && markType ==='label') {
        return "Text"
    }
    return markType
}