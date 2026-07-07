
export function results({ status, title, subTitle }) {
    status = status?.toLowerCase();
    switch (status) {
        case 'success':
            !title && (title = "Process successfully done")
            !subTitle && (subTitle = "The Operation is carried out successfully")
            break;
        case 'info':
            !title && (title = "Your operation has been executed")
            break;
        case 'warning':
            !title && (title = "There are some problems with your operation.")
            break;
        case '403':
            !title && (title = "403")
            !subTitle && (subTitle = "Sorry, You are not meet the expected authorization to access this application.")
            break;
        case '404':
            !title && (title = "404")
            !subTitle && (subTitle = "Sorry, the page you visited does not exist.")
            break;
        case '500':
            !title && (title = "500")
            !subTitle && (subTitle = "Sorry, something went wrong.")
            break;
        case 'error':
            !title && (title = "Technically something Went Wrong !")
            !subTitle && (subTitle = "Please visit after sometime.")
            break;
        case "default":
            title = 'Please re-check the status that you send.'
    }
    return {title, subTitle};
}