import { handleSave } from ".";
//This function is only used in development mode to generate the formData.
export const handleShortcuts = ({ e, store, dispatch}) => {
    console.log('in handle short cunt called')
    if (e.ctrlKey && e.shiftKey && e.keyCode === 83) {
        //ctrl  + shift + s
        e.preventDefault();
        handleSave({ store, dispatch, type: 'saveAs' })
    } else if (e.ctrlKey && e.keyCode === 83) {
        //ctrl + s
        e.preventDefault();
        handleSave({store, dispatch, type : 'save'})
    }
};
