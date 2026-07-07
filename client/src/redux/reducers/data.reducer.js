import { ON_NEXT_VIEW, SET_INIT_DATA } from "../actions/data.actions";


const intialState = {

}

const dataReducer = (state = intialState ,action) => {
    switch (action.type) {
        case SET_INIT_DATA:{
                let { data,viewPage,currentPage,viewEntries,pageSize,fetchMore,showMore,key } = action
                return {...state,[key]:{ data,viewPage,currentPage,viewEntries,pageSize,fetchMore,showMore }}
            }
        case ON_NEXT_VIEW:{
                let { key } = action
                let dataSource = state[key]
                if(!dataSource)return {state}
                let viewPage = dataSource.viewPage+1
                return {...state,[key]:{...dataSource,viewPage}}
            }
        default:
            return {...state}
    }
}

export default dataReducer;
