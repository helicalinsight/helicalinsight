

export default function(path) {
    let paramPath = path.slice(0),
        l = paramPath.length,
        i = 0;

    while(paramPath.length < 3 * l) {
        paramPath.splice(i, 0, "parameters");
        paramPath.splice(i + 2, 0, "value");
        i += 3;
    }

    return ["databaseFunction", ...paramPath];
}
