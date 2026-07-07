export const getQuickTestFD = (record = {}) => {
    const formData = {
        id: record.data.id,
        type: record.data.type,
        classifier: record.classifier,
        ...(record.classifier === "efwd" && {
            dir: record.data.dir,
        }),
    };

    return formData
}