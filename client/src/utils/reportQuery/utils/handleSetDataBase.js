const handleSetDataBase = (payload, self) => {
    if (typeof payload != 'string' || (typeof payload == 'string' && !payload.length)){
        // self.database = ''
        return
    }
    self.database = payload
}

export default handleSetDataBase