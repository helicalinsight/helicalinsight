import qs from "qs";


export const viewsMockAxios = () => {
    // return null
    let data = {
        post: function (url, data, config) {
            let payload = qs.parse(data)
            let { type, serviceType, service, formData } = payload
            try {
                formData = window.atob(formData)
            }
            catch (e) {

            }
            formData = JSON.parse(formData)
            return new Promise((resolve, reject) => {
                let { contentId, location, metadataFileName, id } = formData
                console.log("POST REQ", type, serviceType, service, formData)

                return resolve({})
            })
        },
        get: (url, data, config) => {
            let { params } = data
            return new Promise((resolve, reject) => {
                console.log('GET REQ', url, data)
                resolve({
                    data: {}
                })
            })
        }
    }
    return { instance: data }
}