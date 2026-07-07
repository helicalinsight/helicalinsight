export const getDeleteFD = ({ clickedRecordData, deleteType }) => {
    const formData = {
        classifier: clickedRecordData.classifier,
        id: clickedRecordData.data.id,
        type: deleteType,
        ...(clickedRecordData.classifier === "global" && {
            dataSourceProvider: clickedRecordData.dataSourceProvider,
        }),
        ...(clickedRecordData.classifier === "efwd" && {
            directory: clickedRecordData.data.dir,
            driver: clickedRecordData.driver,
        }),
    };

    return formData
}