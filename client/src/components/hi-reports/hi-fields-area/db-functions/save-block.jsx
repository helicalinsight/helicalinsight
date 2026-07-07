

import React from 'react';
import { Button } from 'antd';
import { useDispatch } from "react-redux";


import { saveDataBaseFunction } from '../utils/utilities';


const SaveBlock = (props) => { 
    const dispatch = useDispatch()
    let { databaseFunctions, fields,editingField } = props
    const handleSave = () => {
        saveDataBaseFunction({databaseFunctions,fields,editingField}, dispatch)
    }
    return (
        <div className="db-editor-actions" >
            <Button onClick={handleSave} type="primary" data-testid="save-db-function">Save</Button>
        </div>
    )
}

export default SaveBlock