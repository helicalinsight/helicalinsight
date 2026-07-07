import { postRequest } from "../service";

function imageRequests(dispatch) {
    const fetchImageData = (formData, uri, callback = () => { }, errback = () => { }) => {
        uri = 'util/io/imageService';
        return postRequest(dispatch, uri, formData, callback, errback);
    };
    
    return {
        fetchImageData
    }
}

export default imageRequests;