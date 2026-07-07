import requests from "../../base/requests";

const fetchImageState = async (formData, dispatch, getApi) => {
    return new Promise((resolve, reject) => {
        let apiInstance = requests.imagemodule(dispatch).fetchImageData(formData, "", (res) => {
            resolve(res);
        }, (err) => {
            resolve(err);
        });
        typeof getApi === "function" && getApi(apiInstance)
    })
}

const fetchImage = async ({ file }, dispatch, getApi) => {
    const formData = { dir: file.path?.replace(file.name, "")?.replace(/[\\|\/]+$/, ""), file: file.name }
    let res = await fetchImageState(formData, dispatch, getApi);
    return new Promise((resolve, reject) => {
        if (res) {
            resolve(res)
        } else {
            resolve(false)
        }
    })
}


export {
    fetchImage
}