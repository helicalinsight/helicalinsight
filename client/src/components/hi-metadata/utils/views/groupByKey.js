export function groupByKey(objectArr, fn){
    const grouped = {};

    for (let object of objectArr){
        const key = fn(object);

        if(!grouped[key])
            grouped[key] = [];
        
        grouped[key].push(object);
    }

    return grouped;
}