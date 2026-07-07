

const intialState = {
    
}


const adhocReducer = (state = intialState ,action) => {
    switch (action.type) {
        case 'addUser':
            return {...state}
        case 'NEXT':
            return {...state}
        default:
            return {...state}
    }
}

export default adhocReducer;
