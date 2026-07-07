export const calculateSerialNumber = (page, pageSize, index) => {
    return pageSize * (page - 1) + (index + 1);
}