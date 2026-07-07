
import  { useState, useEffect } from 'react';
import { useSelector,useDispatch } from 'react-redux';
import { setInitData,onNextView } from '../redux/actions/data.actions';

export const LoadData = (props) => {
    const dispatch = useDispatch()
    const storeData = useSelector(state=>state.data[props.key])
    const [items, setItems] = useState(null);
    // const [entries, setEntries] = useState(10);
    // const [currentPage, setCurrentPage] = useState(1);
    // const [fetchMore, setFetchMore] = useState(true);
    let loaded = true;
    const onNext = () =>{
        dispatch(onNextView({key:"users"}))
    }
    useEffect(() => {
        if(!storeData){
            dispatch(setInitData({key:props.key}))
        }else{
            let { viewPage,data,viewEntries } = storeData
            let tempData = data.slice(((viewPage-1)*viewEntries),(viewPage*viewEntries))
            setItems(tempData)
        }
    },[storeData]);

    if (!items) {
        loaded = false;
    }
    return {loaded,data:items,onNext};
}